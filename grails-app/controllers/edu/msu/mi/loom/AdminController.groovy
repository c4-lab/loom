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
            int count = UserSession.countBySession(it as Session)
//            List listening_time = UserSession.findAllBySession(it).sort {UserSession.started}
//            print(listening_time)

            boolean active = false
            Long startPending = null
            Long startActive = null

            def round = 0
            def check_greyed = true
            def pay_greyed = true
            if (it.state == Session.State.PENDING) {
                active = experimentService.waitingTimer[it.id]
                startPending = it.startPending

            } else if (it.state == Session.State.ACTIVE) {
                round = experimentService.getExperimentStatus(it)?.round
                active = experimentService.experimentsRunning.containsKey(it.id)
                startActive = it.startActive


            }

            def paid = it.paid
            def total = it.total
            def connected_users = UserSession.countBySessionAndStateInList(it as Session, ['WAITING', 'ACTIVE'])
            [(it.id):[active,count,startPending,startActive, round, paid.toString()+"/"+total.toString(), connected_users]]
        }
        def users = User.findAllByTurkerIdIsNullAndUsernameNotEqual("admin", [sort: 'dateCreated', order: 'desc'])

        render(view: 'board', model: [sessions: Session.list(), sessionState: sessionStates, experiments: Experiment.list(), trainings: TrainingSet.list(), stories: Story.list(), users: users])
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
            def connected_users = UserSession.countBySessionAndStateInList(session, ['WAITING','ACTIVE'])


            def result = ['startPending': session.startPending, 'startActive': session.startActive, 'connected':connected_users,'count':count,'sessionState':session.state.toString(),'round':round]

            render result as JSON
        }
        else{
            redirect(action:"board")
        }

    }

    def getFullUrl() {
       return "${request.getScheme()}://${request.getServerName()}:${request.getServerPort()}${request.contextPath}"
    }

    def launchExperiment() {

        List<Session> sessionList = Session.findAll()
        boolean  flag = true
//        for(Session s: sessionList){
//            if(s.state != Session.State.FINISHED){
//                flash.error = "Cannot create two sessions without finishing them first!"
//                flag = false
//
//            }
//        }
        if(flag){
            Experiment exp = Experiment.get(params.experimentID)

            TrainingSet ts = exp.training_set
            def session = adminService.createSession(exp,ts)

            sessionService.launchSession(session.id)
            mturkService.createExperimentHIT(exp, session.id as String,params.num_hits as int,params.assignment_lifetime as int, params.hit_lifetime as int, getFullUrl())
        }

        redirect(action: 'board')
    }

    def launchTraining() {
        String fullUrl = getFullUrl()

        TrainingSet ts = TrainingSet.get(params.trainingId)
        int num_hits = params.num_hits as int
        mturkService.createTrainingHIT(ts, num_hits, params.assignment_lifetime as int, params.hit_lifetime as int, fullUrl)
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
            def uiflag = params.uiflag
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

            }else if (network_type == 'Newman_Watts'){
                network_type = Experiment.Network_type.Newman_Watts

            }else if(network_type == 'Barabassi_Albert'){
                network_type = Experiment.Network_type.Barabassi_Albert

            }
            if (isQualifier == 'yes'){

                qualifier = "Hitnum>500;ApprovalRate>98;Local=US;"+"!Story"+story.id+";Performance>="+performance+";"+"Reading>="+reading+";"+ vaccine_min+"<=vaccine<="+vaccine_max+";"
            }

            def experiment = adminService.createExperiment(name,story,min_nodes as int,max_nodes as int, min_degree as int, max_degree as int,initialNbrOfTiles as int,
                    network_type,rounds as int,duration as int,qualifier,training_set, m as int, prob, accepting,completion,waiting,score, uiflag as int)
            if (experiment) {
                def result = [ 'message': "success"]
                render result as JSON


            }else{
                def result = [ 'message': "exp_error"]

                render result as JSON
            }

        }

    }

    def uploadTrainingSet() {
        def file = params.inputFile
        def qualifiers_list = []
//        def hit_num = params.hit_num
        def name = TrainingSet.findByName(params.name)
        def training_payment = params.training_payment
        def uiflag = params.UIflag

        if (name){

            flash.error = "name already exists, please use another name."

        }else{
            if ('simulation' in params){
                qualifiers_list.add("simulation")
            }else{
                qualifiers_list.add('-')
            }
            if ('read' in params){
                qualifiers_list.add("read")
            }else{
                qualifiers_list.add('-')
            }
            if ('survey' in params){
                qualifiers_list.add("survey")
            }else{
                qualifiers_list.add('-')
            }
            def text = fileService.readFile(file as MultipartFile)
            def json = jsonParserService.parseToJSON(text)

            if (json) {

                trainingSetService.createTrainingSet(json,params.name, qualifiers_list.join(';'), training_payment, uiflag as int)

            }

        }


        redirect(action: 'board')
    }


    def uploadStorySet(){
        def title = params.title

        def story = Story.findByTitle(title)
        if (story){
            def result = [ 'message': "duplicate"]
            render result as JSON

        }else{
            String story_text = params.tails

            story = adminService.createStory(title, story_text)
            if (story){
                print(story)
                try {
                    mturkService.createQualification(story, "loom story [" + title + "]")
                }  catch (Exception e) {
                   log.warn("Couldn't create qualification for story",e)
                }
                def result = [ 'message': "success"]

                render result as JSON
            }else{
                def result = [ 'message': "error"]

                render result as JSON
            }
        }

    }


    def startSession() {

        def session = Session.get(params.sessionId)
        boolean auto = params.auto
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
                        mturkService.updateExpirationForHit(session)
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
    }

    def paySession(){
        def session = Session.get(params.sessionId)
        def (payableHIT, paid, total, count) = mturkService.check_session_payable(session)
        if (payableHIT){
            mturkService.pay_session_HIT(session)
            def result = ['status': "success","payment_status":total.toString()+"/"+total.toString()]
            render result as JSON
        }else{
            def result = ['status': "no_payable","payment_status":total.toString()+"/"+total.toString()]
            render result as JSON
        }


    }

    def checkSessionPayble(){
        def check_greyed = false
        def pay_greyed = false
        def session = Session.get(params.sessionId)
        def (payableHIT, paid, total, session_count) = mturkService.check_session_payable(session)
        if(total == session_count || total == paid){
            check_greyed = true
        }
        if(paid == total && total!=0){
            pay_greyed = true
        }
        def result = ['payment_status': paid.toString()+"/"+total.toString(),
                      'check_greyed':check_greyed, 'pay_greyed':pay_greyed]

        render result as JSON
    }

    def checkTrainingsetPayble(){
        def check_greyed = false
        def pay_greyed = false
        def trainingset = TrainingSet.get(params.trainingsetId)
        def (payableHIT, paid, total) = mturkService.check_trainingset_payable(trainingset)
        if(total == paid){
            check_greyed = true
        }

        def result = ['payment_status': paid.toString()+"/"+total.toString(),
                      'check_greyed':check_greyed]

        render result as JSON
    }


    def payTrainingHIT(){
        def trainingsetHIT = TrainingSet.get(params.trainingsetId)
        def (payableHIT, paid, total) = mturkService.check_trainingset_payable(trainingsetHIT)
        if (payableHIT){
            mturkService.pay_trainingset_HIT(trainingsetHIT)
            def result = ['status': "success","payment_status":total.toString()+"/"+total.toString()]
            render result as JSON
        }else{
            def result = ['status': "no_payable","payment_status":total.toString()+"/"+total.toString()]
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
        log.info("Got params ${params}")
        def sessionId = Integer.parseInt(params.session)
        if (sessionId) {
            def session = Session.get(sessionId)
            if (session) {

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

    def createUser() {

        def usernames = params["usernames[]"] as List
        def final_usernames = []
        def duplicate_usernames = []
        def status = "success"

        for(String username: usernames){
            if(username){
                if (username == "default-user"){
                    List<User> users = User.findAll()
                    username = "user-" + (users.last().id + 1).toString()
                    duplicate_usernames.add(0)
                }else{

                    if (User.findByUsername(username) || usernames.count(username)>1){
                        duplicate_usernames.add(1)


//                    def result = ['status': "duplicate_username"]
//                    render result as JSON
//                    return
                    }else{
                        duplicate_usernames.add(0)
                    }
                }
            }



//            final_usernames.add(username)
        }
        if (duplicate_usernames.contains(1)){

            final_usernames = duplicate_usernames
            status = "duplicate"

        }else{
            for(String username: usernames){
                if(username){
                    def user
                    if(username != "default-user"){
                        user = new User(username: username, password: "pass").save(failOnError: true)
                    }else{
                        user = new User(username: "user-"+User.findAll().last().id.toString(), password: "pass").save(failOnError: true)
                    }

                    def role = Role.findByAuthority(Roles.ROLE_USER.name)
                    UserRole.create(user, role, true)
                    final_usernames.add(user.username)

                }


            }

        }


        def result = ['status': status, "username":final_usernames]
        render result as JSON

    }

//    def createUser() {
//        def usernames = params["usernames[]"] as List
//        def final_usernames = []
//        def duplicate_usernames = []
//        for(String username: usernames){
//            if (username == "default-user"){
//                List<User> users = User.findAll()
//                username = "user-" + (users.last().id + 1).toString()
//                duplicate_usernames.add(0)
//            }else{
//
//                if (User.findByUsername(username)){
//                    duplicate_usernames.add(1)
//
////                    def result = ['status': "duplicate_username"]
////                    render result as JSON
////                    return
//                }else{
//                    duplicate_usernames.add(0)
//                }
//            }
//            final_usernames.add(username)
//        }
//        if (username == "default-user"){
//            List<User> users = User.findAll()
//            username = "user-" + (users.last().id + 1).toString()
//        }else{
//
//            if (User.findByUsername(username)){
//                def result = ['status': "duplicate_username"]
//                render result as JSON
//                return
//            }
//        }
//        def user = new User(username: username, password: "pass").save(failOnError: true)
//        def role = Role.findByAuthority(Roles.ROLE_USER.name)
//        UserRole.create(user, role, true)
//        def result = ['status': "success", "username":username]
//        render result as JSON
//
//    }


}
