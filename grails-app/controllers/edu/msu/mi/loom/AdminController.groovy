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
            upload: 'POST',
            view  : 'GET'
    ]

    def index() {}

    def board() {
        def sessionCount = Session.count();
        def sessions = Session.list()

        render(view: 'board', model: [sessionCount: sessionCount, sessions: sessions])
    }

    def upload() {
        def file = request.getFile('inputFile')
        def text = fileService.readFile(file as MultipartFile)

        def json = parserService.parseToJSON(text)

        if (json) {
            experimentService.createSession(json)
        }


        redirect(action: 'board')
    }

    def deleteExperiment() {
        def id = params.experimentId
        def type = params.type

        if (params.experimentId && params.type) {
            experimentService.deleteExperiment(id, type)
        }

        redirect(action: 'board')
    }

    def view() {
        def sessionId = Integer.parseInt(params.session)
        if (sessionId) {
            def session = Session.get(sessionId)
            if (session) {
                def trainingsCount = Training.countBySession(session)
                def experimentsCount = Experiment.countBySession(session)
                def simulationsCount = Simulation.countBySession(session)

                def experiments = Experiment.findAllBySession(session)
                def trainings = Training.findAllBySession(session)
                def simulations = Simulation.findAllBySession(session)

                render(view: 'session', model: [trainingsCount  : trainingsCount, experimentsCount: experimentsCount,
                                                simulationsCount: simulationsCount, experiments: experiments,
                                                trainings       : trainings, simulations: simulations,
                                                session         : session])
            } else {
                redirect(uri: '/not-found')
            }
        } else {
            redirect(uri: '/not-found')
        }
    }
}
