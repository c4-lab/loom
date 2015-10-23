package edu.msu.mi.loom

import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import org.springframework.web.multipart.MultipartFile

@Slf4j
@Secured("ROLE_ADMIN")
class AdminController {
    def fileService
    def parserService
    def experimentService

    static allowedMethods = [
            board : 'GET',
            upload: 'POST'
    ]

    def index() {}

    def board() {
        def experimentsCount = Experiment.count();
        def trainingsCount = Training.count();
        def simulationsCount = Simulation.count();

        def experiments = Experiment.list();
        def trainings = Training.list();
        def simulations = Simulation.list();

        render(view: 'board', model: [experimentsCount: experimentsCount, trainingsCount: trainingsCount, simulationsCount: simulationsCount, experiments: experiments, trainings: trainings, simulations: simulations])
    }

    def upload() {
        def file = request.getFile('inputFile')
        def text = fileService.readFile(file as MultipartFile)

        parserService.createExperiment(text)

        redirect(action: 'board')
    }

    def deleteExperiment() {
        def id = params.experimentId
        def type = params.type

        if (experimentService.deleteExperiment(id, type)) {
            log.info("Experiment with id ${id} has been deleted.")
        }
        redirect(action: 'board')
    }
}
