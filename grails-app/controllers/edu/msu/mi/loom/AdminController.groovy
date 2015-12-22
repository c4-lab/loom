package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import org.springframework.web.multipart.MultipartFile

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Slf4j
@Secured("ROLE_ADMIN")
class AdminController {
    def fileService
    def jsonParserService
    def experimentService
    def graphParserService
    def roomService
    def emailService
    def userService
    def springSecurityService

    static allowedMethods = [
            board       : 'GET',
            upload      : 'POST',
            view        : 'GET',
            cloneSession: 'POST'
    ]

    def index() {}

    def board() {
        def sessionCount = Session.count();
        def sessions = Session.list()
        def roomsCount = Room.count()

        render(view: 'board', model: [roomsCount: roomsCount, sessionCount: sessionCount, sessions: sessions])
    }

    def upload() {
        def file = request.getFile('inputFile')
        if (file) {
            def text = fileService.readFile(file as MultipartFile)
            def json = jsonParserService.parseToJSON(text)
            if (json) {
                def session = experimentService.createSession(json)
                if (session.id) {
                    return redirect(action: 'completeExperimentCreation', params: [experiment: session.experiments[0].id, initNbrOfTiles: session.experiments[0].initialNbrOfTiles])
                }
            }
        }

        redirect(action: 'board')
    }

    def completeExperimentCreation() {
        if (request.method == 'GET') {
            def sessionCount = Session.count()
            render(view: 'complete', model: [sessionCount: sessionCount, experiment: params.experiment, initNbrOfTiles: params.initNbrOfTiles])
        } else {
            def file = request.getFile('graphmlFile').inputStream
            HashMap<String, List<String>> nodeStoryMap = graphParserService.parseGraph(file)

            def experiment = experimentService.completeExperiment(nodeStoryMap, params.experimentId)
            if (experiment.enabled) {
                log.debug("Experiment with id ${experiment.id} is enabled.")
            } else {
                log.warn("Something went wrong, experiment with id ${experiment.id} cannot be enabled.")
            }

            redirect(action: 'board')
        }
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

    def cloneSession() {
        def sessionId = params.session

        if (sessionId) {
            def session = Session.get(sessionId)

            if (session) {
                def clone = experimentService.cloneExperiment(session)

                if (clone.id) {
                    render(status: OK, text: [session: clone] as JSON)
                    return
                }
            }
        }

        render(status: BAD_REQUEST)
    }

    def publishAnonym() {
        def sessionId = params.session

        if (sessionId) {
            def session = Session.get(sessionId)
            def room = roomService.createRoom(session)
            if (room.id) {
                render(status: OK)
            } else {
                render(status: BAD_REQUEST)
            }
        } else {
            redirect(uri: '/not-found')
        }
    }

    def publishEmail() {
        def emailsString = params.emailAddress
        def sessionId = params.session
        if (sessionId) {
            def session = Session.get(sessionId)
            if (session) {
                def room = roomService.createRoom(session)

                if (room.id) {
                    emailService.sendInvitationEmail(emailsString, room.id)
                    log.info("Invitations have been sent for room with id ${room.id}.")
                    redirect(action: 'board')
                    return
                }
            }
        }
        redirect(uri: '/not-found')
    }

    @Secured('permitAll')
    def deleteUser() {
        def user = springSecurityService.currentUser as User
        userService.deleteUser(user)
    }
}
