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
    def networkGenerateService
    def mturkService

    static allowedMethods = [
            board           : 'GET',
            uploadExperiment: 'POST',
            view            : 'GET',
            cloneSession    : 'POST'
    ]

    def index() {}

    def board() {

        def sessionStates = Session.list().collectEntries {
            int count = UserSession.countBySession(it)
//            List listening_time = UserSession.findAllBySession(it).sort {UserSession.started}
//            print(listening_time)

            boolean active = false
            Long startPending = null
            Long startActive = null
            String completed = null
            String payment_status = null
            def round = 0
            if (it.state == Session.State.PENDING) {
                active = experimentService.waitingTimer[it.id]
                startPending = it.startPending


            } else if (it.state == Session.State.ACTIVE) {
                round = experimentService.getExperimentStatus(it)?.round
                active = experimentService.experimentsRunning.containsKey(it.id)
//                activeTime = new Date().getTime()
                startActive = it.startActive


            }else if (it.state == Session.State.FINISHED){
                payment_status = mturkService.check_payable(it)
            }
            [(it.id):[active,count,startPending,startActive, round, payment_status]]
        }

        render(view: 'board', model: [sessions: Session.list(), sessionState: sessionStates, experiments: Experiment.list(), trainings: TrainingSet.list(), stories: Story.list()])
    }

    def refresh() {
        def session = Session.get(params.sessionId)

//        if(session.state == Session.State.ACTIVE){
//            int flag = 1
//            List userSessions = UserSession.findAllBySession(session)
//            for(userSession in userSessions){
//                if(userSession.state != "COMPLETE"){
//                    flag = 0
//                }
//            }
//            if(flag){
//                session.state = Session.State.FINISHED
//            }
//        }
        if(session){
            int count = UserSession.countBySession(session)
            def round = experimentService.getExperimentStatus(session)?.round
            if(!round){
                round = 0
            }

            def (payable_hit, payment_status) = mturkService.check_payable(session)
            def result = [ 'payment_status':payment_status,'startPending': session.startPending, 'startActive': session.startActive, 'count':count,'sessionState':session.state.toString(),'round':round]

            render result as JSON
        }
        else{
            redirect(action:"board")
        }

    }

    def launchExperiment() {

        Experiment exp = Experiment.get(params.experimentId)

        TrainingSet ts = exp.training_set
        def session = adminService.createSession(exp,ts)

        sessionService.launchSession(session.id)
        mturkService.createExperimentHIT(exp, session.id as String)
        redirect(action: 'board')
    }

    def launchTraining() {

        TrainingSet ts = TrainingSet.get(params.trainingId)
        mturkService.createTrainingHIT(ts)
        redirect(action: 'board')
    }

    def uploadExperiment() {

        def name = params.name
        def exp = Experiment.findByName(name)
        if (exp){
            def result = [ 'message': "duplicate"]
            render result as JSON

        }else{
            def story = Story.get(params.storySet)
            def min_nodes = params.min_nodes
            def max_nodes = params.max_nodes
            def min_degree = params.min_degree
            def max_degree = params.max_degree
            def initialNbrOfTiles = params.initialNbrOfTiles
            def network_type = params.network_type
            def rounds = params.rounds
            def duration = params.duration
            def qualifier = ''
            def training_set = TrainingSet.get(params.trainingSet)
            def m = params.m
            def prob = params.prob
            def isQualifier = params.isQualifier
            def performance = params.performance
            def reading = params.reading
            def vaccine_min = params.vaccine_min
            def vaccine_max = params.vaccine_max
            def accepting = params.accepting
            def completion = params.completion
            def waiting = params.waiting
            def score = params.score
            if (network_type == 'Lattice'){
                network_type = Experiment.Network_type.Lattice
//            min_degree = min_degree[0]
//            max_degree = 0
//            m = 0
//            prob = 0
            }else if (network_type == 'Newman_Watts'){
                network_type = Experiment.Network_type.Newman_Watts
//            min_degree = min_degree[1]
//            max_degree = max_degree[0]
//            m = 0
//            prob = prob[0]
            }else if(network_type == 'Barabassi_Albert'){
                network_type = Experiment.Network_type.Barabassi_Albert
//            min_degree = min_degree[2]
//            max_degree = max_degree[1]
//            prob = prob[1]
            }
            if (isQualifier == 'yes'){
                qualifier = "Hitnum>500;ApprovalRate>98;Local=US;"+"!Story"+story.id+";Performance>="+performance+";"+"Reading>="+reading+";"+ vaccine_min+"<=vaccine<="+vaccine_max+";"
            }

            def experiment = adminService.createExperiment(name,story,min_nodes as int,max_nodes as int, min_degree as int, max_degree as int,initialNbrOfTiles as int,
                    network_type,rounds as int,duration as int,qualifier,training_set, m as int, prob, accepting,completion,waiting,score)
            if (experiment) {
                def result = [ 'message': "success"]
//
                render result as JSON

//                HashMap<String, List<String>> nodeStoryMap = networkGenerateService.generateGraph(experiment)
//
//                if (nodeStoryMap){
//                    println(nodeStoryMap)
//                    adminService.setExperimentNetwork(nodeStoryMap, experiment.id as int)
////                def url = createLink(controller: 'admin', action: 'board')
////                render(contentType: 'text/html', text: "<script>window.location.href='$url'</script>")
//
//                } else{
//                    def result = [ 'message': "error"]
////
//                    render result as JSON
//                }
//            def file = request.getFile('graphmlFile').inputStream
//            HashMap<String, List<String>> nodeStoryMap = graphParserService.parseGraph(file)
                // [n0:[Story1, n2], n1:[Story1, n2], n2:[Story1, n0, n1, n3], n3:[Story1, n2, n5, n4], n4:[Story1, n3, n6], n5:[Story1, n7], n6:[Story1, n4, n5, n8], n7:[Story1, n5, n8], n8:[Story1, n6, n7, n9, n10], n9:[Story1, n8], n10:[Story1, n8]]


//                return redirect(action: 'board')
//                return redirect(action: 'completeExperimentCreation', params: [experiment: experiment.id])
//                render(view: 'board')

//            return redirect(action: 'completeExperimentCreation', params: [experiment: experiment.id, initNbrOfTiles: experiment.initialNbrOfTiles])
            }else{
                def result = [ 'message': "exp_error"]

                render result as JSON
            }




        }

    }

    def uploadTrainingSet() {
        def file = request.getFile('inputFile')
        def qualifiers_list = []
        def hit_num = params.hit_num
        def name = TrainingSet.findByName(params.name)
        def training_payment = params.training_payment
        if (name){

//            def result = [ 'message': "duplicate"]
//            render result as JSON
            flash.error = "name already exists, please use another name."

        }else{
            if ('perform' in params){
                def performance_score = params.performance
                qualifiers_list.add("peform>="+performance_score as String)
            }else{
                qualifiers_list.add('')
            }
            if ('read' in params){
                qualifiers_list.add("read")
            }else{
                qualifiers_list.add('')
            }
            if ('vaccine' in params){
                qualifiers_list.add("vaccine")
            }else{
                qualifiers_list.add('')
            }
            def text = fileService.readFile(file as MultipartFile)
            def json = jsonParserService.parseToJSON(text)

            if (json) {

                trainingSetService.createTrainingSet(json,params.name, qualifiers_list.join(';'), hit_num as int, training_payment)

            }
//            def result = [ 'message': "success"]
////
//            render result as JSON

        }
//        if (file) {
//            def text = fileService.readFile(file as MultipartFile)
//            def json = jsonParserService.parseToJSON(text)
//            println("trainisnidfsd")
//            println(params)
//            println(json)
//            if (json) {
//
//                trainingSetService.createTrainingSet(json,params.name)
//
//            }
//        }

        redirect(action: 'board')
    }


    def uploadStorySet(){
        def title = params.title
        def tails = params.tails
        def story = Story.findByTitle(title)
        if (story){
            def result = [ 'message': "duplicate"]
            render result as JSON

        }else{
            story = adminService.createStory(title, tails)
            if (story){
                mturkService.createQualification(story, "loom story")
                def result = [ 'message': "success"]
//
                render result as JSON
            }else{
                def result = [ 'message': "error"]

                render result as JSON
            }
        }

    }

//    def completeExperimentCreation() {
////        if (request.method == 'GET') {
////            def sessionCount = Session.count()
////            render(view: 'complete', model: [sessionCount: sessionCount, experiment: params.experiment, initNbrOfTiles: params.initNbrOfTiles])
////        } else {
//        Experiment experiment = Experiment.get(params.experiment)
//
//            HashMap<String, List<String>> nodeStoryMap = networkGenerateService.generateGraph(experiment)
//
//            // [n0:[Story1, n2], n1:[Story1, n2], n2:[Story1, n0, n1, n3], n3:[Story1, n2, n5, n4], n4:[Story1, n3, n6], n5:[Story1, n7], n6:[Story1, n4, n5, n8], n7:[Story1, n5, n8], n8:[Story1, n6, n7, n9, n10], n9:[Story1, n8], n10:[Story1, n8]]
//            println(nodeStoryMap)
//            println(params.experiment as int)
//            adminService.setExperimentNetwork(nodeStoryMap, params.experiment as int)
////            if (experiment.enabled) {
////                log.debug("Experiment with id ${experiment.id} is enabled.")
////            } else {
////                log.warn("Something went wrong, experiment with id ${experiment.id} cannot be enabled.")
////            }
//
//            redirect(action: 'board')
//
////        }
//    }

    def startSession() {

        def session = Session.get(params.sessionId)
        List userSession = UserSession.findAllBySessionAndStateInList(session, ["ACTIVE", "WAITING"])
        def result = ['status': null]
        if(session.state == Session.State.CANCEL){
            result = ['status': "cancel"]
//            render(text:"cancel")
        }else{
            if (userSession.size() >= session.exp.min_node) {
                if (session.state == Session.State.PENDING) {
                    experimentService.kickoffSession(session)
                    session = Session.get(params.sessionId)
                    if(session.state == Session.State.ACTIVE){
                        result = ['status': "start"]
                    }else{
                        result = ['status': "fail"]
                    }
                }else{
                    result = ['status': "fail"]
                }
            }else{
                result = ['status': "less"]

            }
        }
        render result as JSON
    }

    def cancelSession() {

        def session = Session.get(params.sessionId)
        mturkService.updateExpirationForHit(session)
//        mturkService.updateExpirationForHit(session)
        if (session.state == Session.State.PENDING || session.state == Session.State.ACTIVE) {
            session.startPending = null
            session.startActive = null
            session.state = Session.State.CANCEL
            session.save(flush: true)
        }
        redirect(action: 'board')

    }

    def validateSession() {
        def session = Session.get(params.sessionId)
        if (session.state == Session.State.CANCEL) {
            session.state = Session.State.PENDING
            session.startPending = new Date().getTime()
            session.save(flush: true)

        }
        mturkService.createExperimentHIT(session.exp, session.id as String)
        int count = UserSession.countBySession(session)
        def round = experimentService.getExperimentStatus(session)?.round
        if(!round){
            round = 0
        }
        def result = [ 'startPending': session.startPending, 'startActive': session.startActive, 'count':count,'sessionState':session.state.toString(),'round':round]

        render result as JSON
//        redirect(action: 'board')
    }

    def paySession(){
        def session = Session.get(params.sessionId)
        def (payableHIT, b) = mturkService.check_payable(session)
        if (payableHIT.size()){
            mturkService.pay_HIT(session)
            def result = ['status': "success"]
            render result as JSON
        }else{
            def result = ['status': "no_payable"]
            render result as JSON
        }


    }


    def deleteExperiment() {
        def id = params.sessionId
        def type = params.type

        if (params.sessionId && params.type) {
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

                render(view: 'session', model: [ session : session])
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
