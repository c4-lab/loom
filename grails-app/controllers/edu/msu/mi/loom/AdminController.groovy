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

    def emailService
    def userService
    def springSecurityService
    def exportService
    def trainingSetService
    def sessionService
    def adminService

    static allowedMethods = [
            board           : 'GET',
            uploadExperiment: 'POST',
            view            : 'GET',
            cloneSession    : 'POST'
    ]

    def index() {}

    def board() {
        def sessionStates = Session.list().collectEntries {
            boolean active = false
            if (it.state == Session.State.PENDING) {
               active = experimentService.waitingTimer[it.id]
            } else if (it.state == Session.State.ACTIVE) {
               active = experimentService.experimentsRunning.containsKey(it.id)
            }
            [it.id,active]
        }
        render(view: 'board', model: [sessions: Session.list(), sessionState: sessionStates, experiments: Experiment.list(), trainings: TrainingSet.list()])
    }

    def launchExperiment() {
        Experiment exp = Experiment.get(params.experimentId)
        TrainingSet ts = TrainingSet.get(params.trainingSet)
        def session = adminService.createSession(exp,ts)
        sessionService.launchSession(session)
        redirect(action: 'board')
    }

    def uploadExperiment() {
        def file = request.getFile('inputFile')
        if (file) {
            def text = fileService.readFile(file as MultipartFile)
            def json = jsonParserService.parseToJSON(text)
            if (json) {
                def experiment = adminService.createExperiment(json.experiment)
                if (experiment.id) {
                    return redirect(action: 'completeExperimentCreation', params: [experiment: experiment.id, initNbrOfTiles: experiment.initialNbrOfTiles])
                }
            }
        }

        redirect(action: 'board')
    }

    def uploadTrainingSet() {
        def file = request.getFile('inputFile')
        if (file) {
            def text = fileService.readFile(file as MultipartFile)
            def json = jsonParserService.parseToJSON(text)
            if (json) {
                trainingSetService.createTrainingSet(json,params.name)

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

            def experiment = adminService.setExperimentNetwork(nodeStoryMap, params.experimentId)
            if (experiment.enabled) {
                log.debug("Experiment with id ${experiment.id} is enabled.")
            } else {
                log.warn("Something went wrong, experiment with id ${experiment.id} cannot be enabled.")
            }

            redirect(action: 'board')
        }
    }

    def restartSession() {
        def session = Session.get(params.sessionId)
        if (session.state == Session.State.PENDING) {
            experimentService.kickoffSession(session)
        }
        redirect(action: 'board')
    }

    def deleteExperiment() {
        def id = params.experimentId
        def type = params.type

        if (params.experimentId && params.type) {
            adminService.deleteExperiment(id, type)
        }

        redirect(action: 'board')
    }

    def view() {
        def sessionId = Integer.parseInt(params.session)
        if (sessionId) {
            def session = Session.get(sessionId)
            if (session) {




 //               def simulationsCount = Simulation.countBySession(session)


//                def trainings = Training.findAllBySession(session)
//                def simulations = Simulation.findAllBySession(session)

                render(view: 'session', model: [ session         : session])
            } else {
                redirect(uri: '/not-found')
            }
        } else {
            redirect(uri: '/not-found')
        }
    }

    def exportCSV() {
        def userStats = UserStatistic.list()
        def file = exportService.writeCSV(userStats)
        response.setHeader "Content-disposition", "attachment; filename=test.csv"
        response.contentType = 'text/csv'

//        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),
//                CsvPreference.STANDARD_PREFERENCE);
//
//        String[] header = ["user", "session", "training time", "simulation score", "experiment score by rounds", "tiles order"];
//
//        csvWriter.writeHeader(header);
//
//        for (UserStatistic stat : userStats) {
//            csvWriter.write(stat, header);
//        }
//
//        csvWriter.close();
        response.outputStream << file
        response.outputStream.flush()
    }

    def cloneSession() {
        def sessionId = params.session

        if (sessionId) {
            def session = Session.get(sessionId)

            if (session) {
                def clone = adminService.cloneExperiment(session)

                if (clone.id) {
                    render(status: OK, text: [session: clone] as JSON)
                    return
                }
            }
        }

        render(status: BAD_REQUEST)
    }

//    def publishAnonym() {
//        def sessionId = params.session
//
//        if (sessionId) {
//            def session = Session.get(sessionId)
//            session.isActive = true
//            session.save(flush:true)
//            def room = roomService.createRoom(session)
//            if (room.id) {
//                render(status: OK)
//            } else {
//                render(status: BAD_REQUEST)
//            }
//        } else {
//            redirect(uri: '/not-found')
//        }
//    }
//
//    def publishEmail() {
//        def emailsString = params.emailAddress
//        def sessionId = params.session
//        if (sessionId) {
//            def session = Session.get(sessionId)
//            if (session) {
//                def room = roomService.createRoom(session)
//
//                if (room.id) {
//                    emailService.sendInvitationEmail(emailsString, room.id)
//                    log.info("Invitations have been sent for room with id ${room.id}.")
//                    redirect(action: 'board')
//                    return
//                }
//            }
//        }
//        redirect(uri: '/not-found')
//    }

    @Secured('permitAll')
    def deleteUser() {
        def user = springSecurityService.currentUser as User
        userService.deleteUser(user)
    }
}
