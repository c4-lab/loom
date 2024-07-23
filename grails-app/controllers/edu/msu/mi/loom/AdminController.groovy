package edu.msu.mi.loom


import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import org.springframework.web.multipart.MultipartFile

@Slf4j
@Secured("ROLE_ADMIN")
class AdminController {
    def fileService
    def jsonParserService
    def experimentService

    def userService
    def springSecurityService
    def exportService
    def trainingSetService
    def sessionService
    def simulationService
    def adminService

    def mturkService
    def constraintService
    def csvParserService

    static allowedMethods = [board           : 'GET',
                             createExperiment: 'POST',
                             view            : 'GET',
                             cloneSession    : 'GET']

    def index() {}

    def board() {
        if (!adminService.APPLICATION_BASE_URL) {
            adminService.APPLICATION_BASE_URL = getFullUrl()
        }


        def sessionStates = Session.list().collectEntries { Session it ->


            boolean active = false
            Date startWaiting = null
            Date startActive = null

            def round = 0

            if (it.state == Session.State.WAITING) {
                active = experimentService.waitingTimer[it.id]

            } else if (it.state == Session.State.ACTIVE) {
                round = experimentService.getExperimentStatus(it)?.round
                active = experimentService.experimentsRunning.containsKey(it.id)

            }

            def paid = it.paid
            def total = it.total
            def connected_users = UserSession.countBySessionAndStateInList(it as Session, [UserSession.State.WAITING, UserSession.State.ACTIVE])
            //def mturkEnabled = it.credentials != null
            [(it.id): ["active"   : active,
                       "round"    : round,
                       "paid"     : paid.toString() + "/" + total.toString(),
                       "connected": connected_users,]]
        }
        def trainingSets = TrainingSet.list()

        def users = User.findAllByWorkerIdIsNullAndUsernameNotEqual("admin", [sort: 'dateCreated', order: 'desc'])
        //print("Render view")
        List<Session> availableSession = Session.findAllByDeleted(false)
        Map<String, List<Session>> groupedByState = availableSession.groupBy { it.state }


        render(view: 'board', model: [sessions   : groupedByState, sessionState: sessionStates,
                                      experiments: Experiment.list(), trainings: trainingSets,
                                      stories    : Story.list(), users: users, credentials: CrowdServiceCredentials.list()])
    }


    def getFullUrl() {
        return "${request.getScheme()}://${request.getServerName()}:${request.getServerPort()}${request.contextPath}"
    }


    private SessionParameters createSessionParameters(data) {

        def initParams = data.collectEntries { key, value ->
            switch (key) {
                case "story":
                    Story s = Story.findByName(value.title as String)
                    if (!s) {
                        s = adminService.createStory(value.title as String, value.data, value?.seed)

                    }
                    return [key, s]
                case "constraints":
                   Set<ConstraintTest> constraints = [] as Set<ConstraintTest>
                    value.each {
                        constraints << constraintService.getConstraintTest(it.constraint as String, it.operator as String, it.params as String)
                    }
                    return [key, constraints]
                  case "networkTemplate":
                    def networkTemplate = NetworkFactory.createNetwork(value.type as String, value.params as Map)
                    return [key, networkTemplate]
                case "sessionType":
                    List<ConstraintTest> constraintTests = []
                    if ("groups" in value) {
                        value.groups.each {
                            constraintTests << constraintService.parseConstraintTest(it)
                        }
                    }

                    def sessionType = SessionType.create(value.type as String, constraintTests)
                    return [key, sessionType]
                default:
                    [key, value]
            }
        }

        return adminService.createSessionParameters(initParams)
    }

    def createSession() {

        def name = params.name
        def experimentId = params.experimentId
        if (!name) {
            return fail(["Missing session name"])
        }

        if (!experimentId) {
            return fail(["Missing experiment id"])
        }

        def exp = Experiment.get(experimentId as Long)
        def sessionData = jsonParserService.parseToJSON(params.sessiondata)

        if (!sessionData) {
            return fail(["Missing session data"])
        }

        SessionParameters params = null

        try {
            params = createSessionParameters(sessionData.sessionParameters)
        } catch (Exception e) {
            return fail(["Errors creating session parameters: ${e.message}"])
        }


        params.setParentParameters(exp.defaultSessionParams)

        List<String> messages = params.checkMissingValues()
        if (messages) {
            return fail(messages)
        }


        if (!params.save(flush: true)) {
            return fail(["Error saving parameters for session"])
        }

        Session session = new Session(exp: Experiment.get(experimentId), name: name, sessionParameters: params)

        //TODO: This is a bug I just can't figure out; for whatever reason, it doesn't seem that the
        //lifecycle events are being called on the session at this point.  Punting on the problem in the interest
        //of time
        session.generateCodes()

        if (session.save(flush: true)) {
            redirect(action: 'board', fragment: "sessions")
        } else {
            fail(["Error saving session"])
        }


    }

    def launchSession() {

        def session = Session.get(params.sessionId)
        def launchErrors

        if (session.state == Session.State.CANCEL) {
            return fail("Cannot restart a cancelled session", "sessions")
        }

        MturkTask task = null
        if ("enableMturk" in params) {
            task = new MturkTask(session: session,
                    title: "Story Loom Session: ${session.exp.name}:${session.name}[${session.id}]",
                    description: "Play an online, multiplayer puzzle game for a research study",
                    keywords: "game, research",
                    basePayment:  params.payment as String,
                    credentials: CrowdServiceCredentials.get(params.mturkSelectCredentials),
                    mturkAdditionalQualifications: params.other_quals,
                    mturkAssignmentLifetimeInSeconds: Integer.parseInt(params.assignment_time),
                    mturkHitLifetimeInSeconds: Integer.parseInt(params.hit_time),
                    singleHit: params.mturk_method as boolean,
                    mturkNumberHits: Integer.parseInt(params.num_hits))

            session.addToMturkTasks(task)
            session.save(flush: true)

        }
        if (params.launchTime == 'schedule') {
            // Parse the scheduled date and time
            def scheduledDateTime = Date.parse("yyyy-MM-dd'T'HH:mm", params.scheduleDateTime)

            launchErrors = sessionService.scheduleSession(session, task, scheduledDateTime)
        } else {
            launchErrors = sessionService.launchSession(session, task)
        }
        if (launchErrors) {
            log.warn("Failed to launch with errors ${errors}")
            return fail(errors.toString(), "sessions")
        } else {
            return redirect(action: "board", fragment: "sessions")
        }
    }


    def cancelSession() {

        def session = Session.get(params.sessionId)
        if (![Session.State.WAITING, Session.State.ACTIVE].contains(session.state)) {
            return fail("Cannot cancel an inactive session; launch first", "sessions")
        }
        sessionService.cancelSession(session)

        redirect(action: 'board', fragment: "sessions")

    }

    def deleteSession() {

        def session = Session.get(params.sessionId)
        if ([Session.State.WAITING, Session.State.ACTIVE].contains(session.state)) {
            return fail("Cannot delete an enabled session; cancel first", "sessions")
        }
        session.deleted = true
        session.save(flush:true)

        redirect(action: 'board', fragment: "sessions")

    }


    def paySession() {
        def session = Session.get(params.sessionId)
        def (payableHIT, paid, total, count) = mturkService.check_session_payable(session)
        if (payableHIT) {
            mturkService.pay_session_HIT(session)
            def result = ['status': "success", "payment_status": total.toString() + "/" + total.toString()]
            render result as JSON
        } else {
            def result = ['status': "no_payable", "payment_status": total.toString() + "/" + total.toString()]
            render result as JSON
        }


    }

    def checkSessionPayble() {
        def check_greyed = false
        def pay_greyed = false
        def session = Session.get(params.sessionId)
        def (payableHIT, paid, total, session_count) = mturkService.check_session_payable(session)
        if (total == session_count || total == paid) {
            check_greyed = true
        }
        if (paid == total && total != 0) {
            pay_greyed = true
        }
        def result = ['payment_status': paid.toString() + "/" + total.toString(),
                      'check_greyed'  : check_greyed, 'pay_greyed': pay_greyed]

        render result as JSON
    }

    def createExperiment() {

        def messages = []
        def name = params.name
        if (!name) {
            return fail(["Missing experiment name"])
        }

        def exp = Experiment.findByName(name)

        if (exp) {
            return fail(["Experiment name already exists"])
        }

        def experimentData = jsonParserService.parseToJSON(params.experimentdata)

        if (!experimentData) {
            return fail(["Missing experiment data"])
        }

        SessionParameters sessionParameters = null
        try {
            sessionParameters = createSessionParameters(experimentData.sessionParameters)
        } catch (Exception e) {
            return fail(["Errors creating session parameters: ${e.message}"])
        }


        if (messages) {
            return fail(messages)
        }

        //sessionParameters.save()
        def experiment = adminService.createExperiment(name, sessionParameters)

        if (experiment) {
            redirect(action: 'board', fragment: "experiments")
        } else {
            fail(["Error saving experiment"])
        }
    }


    def uploadReading() {

        def file = params.inputFile

        def text = fileService.readFile(file as MultipartFile)
        def json = jsonParserService.parseToJSON(text)

        if (json) {

            trainingSetService.createReading(json)

        }

        redirect(action: 'board')


    }

    def uploadSimulation() {
        def file = params.inputFile

        def text = fileService.readFile(file as MultipartFile)
        def json = jsonParserService.parseToJSON(text)

        if (json) {

            simulationService.createSimulations(json)

        }
        redirect(action: 'board', fragment: "trainings")


    }

    def uploadSurvey() {
        def file = params.inputFile

        def text = fileService.readFile(file as MultipartFile)
        def json = jsonParserService.parseToJSON(text)

        if (json) {

            trainingSetService.createSurvey(json)

        }
        redirect(action: 'board', fragment: "trainings")


    }


    private def fail(messages, String fragment = null) {
        //def status = [status: "error", message: messages.join(",")]
        flash.error = messages.join(",")

        if (fragment) {
            redirect(action: 'board', fragment: "trainings")

        } else {
            redirect(action: 'board')
        }
    }

    def validateParametersFile() {
        def file = params.inputFile
        print("Attempting to parse file ${file}")
        def json = adminService.uploadJsonFile(file)
        print("Received json ${json}")
        def messages = []


        if (!json.containsKey("sessionParameters")) {
            messages.add("Missing sessionParameters section")
            return fail(messages)
        }


        if (!messages.isEmpty()) {
            return fail(messages)
        } else {
            render json
        }


    }

    def addNewUser(String workerId, String type, String constraints) {
        User u = User.findByWorkerId(workerId)
        Roles role = Roles.ROLE_USER

        try {
            role = Roles.valueOf(type)
        } catch (Exception e) {
            log.warn("Error identifying role ${type} - defaulting to regular user")
        }
        if (!u) {
            u = userService.createUserByWorkerId(workerId,role)
        }
        if (!u) {
            throw new RuntimeException("Error creating user")
        }


        if (constraints.trim()) {
            List constraintList = constraints.split(";").collect {

                UserConstraintValue value = constraintService.parseConstraintValue(u, it)
                value.save(flush: true)
                return value

            }
            log.debug("Created constraint values ${constraintList}")
        }



    }


    def uploadUsers() {
        def file = params.inputFile
        def text = fileService.readFile(file as MultipartFile, false)
        def users = csvParserService.parseCsvFile(text)
        users.each {
            String workerId = it.workerId
            String type = it.type
            String constraints = it.constraints
            addNewUser(workerId,type,constraints)
        }
        redirect(action: 'board')
    }


    def uploadTrainingSet() {
        def file = params.inputFile
        def qualifiers_list = []
//        def hit_num = params.hit_num
        def name = TrainingSet.findByName(params.name)
        def training_payment = params.training_payment
        def uiflag = params.UIflag

        if (name) {

            flash.error = "name already exists, please use another name."

        } else {

            def text = fileService.readFile(file as MultipartFile)
            def json = jsonParserService.parseToJSON(text)

            if (json) {

                trainingSetService.createTrainingSet(json, params.name, uiflag as int)

            }

        }
        redirect(action: 'board', fragment: "trainings")
    }

    def launchTraining() {

        TrainingSet ts = TrainingSet.get(params.trainingId)

        if (ts.state == TrainingSet.State.AVAILABLE) {
            return fail("Cannot restart a training; cancel first", "trainings")
        }

        MturkTask task = null
        if ("enableMturk" in params) {
            task = new MturkTask(training: ts,
                    title: "Story Loom Training: ${ts.name}",
                    description: "Obtain qualification to participate in a research study",
                    keywords: "qualification, training, game, research",
                    basePayment: params.payment as String,
                    credentials: CrowdServiceCredentials.get(params.mturkSelectCredentials),
                    mturkAdditionalQualifications: params.other_quals,
                    mturkAssignmentLifetimeInSeconds: Integer.parseInt(params.assignment_time),
                    mturkHitLifetimeInSeconds: Integer.parseInt(params.hit_time),
                    mturkNumberHits: Integer.parseInt(params.num_hits))

            ts.addToMturkTasks(task)
            ts.save(flush: true)

        }
        def errors = trainingSetService.launchTrainingSet(ts, task)
        if (errors) {
            return fail(errors.toString(), "trainings")
        } else {
            return redirect(action: "board", fragment: "trainings")
        }

    }

    def cancelTraining() {
        TrainingSet ts = TrainingSet.get(params.trainingId)

        if (ts.state != TrainingSet.State.AVAILABLE) {
            return fail("Cannot cancel an inactive training; launch first", "trainings")
        }

        trainingSetService.cancelTrainingSet(ts)
        render([status: "success"] as JSON)

    }

    def checkTrainingsetPayble() {
        def check_greyed = false
        def pay_greyed = false
        def trainingset = TrainingSet.get(params.trainingsetId)
        def (payableHIT, paid, total) = mturkService.check_trainingset_payable(trainingset)
        if (total == paid) {
            check_greyed = true
        }

        def result = ['payment_status': paid.toString() + "/" + total.toString(),
                      'check_greyed'  : check_greyed]

        render result as JSON
    }


    def payTrainingHIT() {
        def trainingsetHIT = TrainingSet.get(params.trainingsetId)
        def (payableHIT, paid, total) = mturkService.check_trainingset_payable(trainingsetHIT)
        if (payableHIT) {
            mturkService.pay_trainingset_HIT(trainingsetHIT)
            def result = ['status': "success", "payment_status": total.toString() + "/" + total.toString()]
            render result as JSON
        } else {
            def result = ['status': "no_payable", "payment_status": total.toString() + "/" + total.toString()]
            render result as JSON
        }


    }


    def uploadStorySet() {
        def title = params.title
        def result = ['message': "success"]

        def story = Story.findByName(title)
        if (story) {
            result = ['message': "duplicate"]


        } else {
            String story_text = params.tiles
            String storySeed = params.seed

            story = adminService.createStory(title, story_text, storySeed)
            if (!story) {

                result = ['message': "error"]

            }

        }

        render result as JSON


    }


    def deleteExperiment() {
        def id = params.sessionId
        def type = params.type

        if (params.sessionId && params.type) {
            adminService.deleteExperiment(id, type)
        }

        redirect(action: 'board')
    }

    def deleteCredential() {
        def id = params.credentialId
        if (id) {
            adminService.deleteCredential(id)
        }

        redirect(action: 'board')
    }


    def view() {
        log.info("Got params ${params}")
        def sessionId = Integer.parseInt(params.session)
        if (sessionId) {
            def session = Session.get(sessionId)
            if (session) {

                render(view: 'session', model: [session: session])
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
        def sessionId = params.sessionId
        String message = "Successfully cloned session"

        if (sessionId) {
            def session = Session.get(sessionId)

            if (session) {
                def clone = adminService.cloneExperiment(session)

                if (!clone.id) {
                    message = "Failed to create clone: ${clone.errors}"
                }
            }
        }

        redirect(action: 'board', fragment: "sessions", flash: message)
    }


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

        for (String username : usernames) {
            if (username) {
                if (username == "default-user") {
                    List<User> users = User.findAll()
                    username = "user-" + (users.last().id + 1).toString()
                    duplicate_usernames.add(0)
                } else {

                    if (User.findByUsername(username) || usernames.count(username) > 1) {
                        duplicate_usernames.add(1)


//                    def result = ['status': "duplicate_username"]
//                    render result as JSON
//                    return
                    } else {
                        duplicate_usernames.add(0)
                    }
                }
            }


//            final_usernames.add(username)
        }
        if (duplicate_usernames.contains(1)) {

            final_usernames = duplicate_usernames
            status = "duplicate"

        } else {
            for (String username : usernames) {
                if (username) {
                    def user
                    if (username != "default-user") {
                        user = new User(username: username, password: "pass").save(failOnError: true)
                    } else {
                        user = new User(username: "user-" + User.findAll().last().id.toString(), password: "pass").save(failOnError: true)
                    }

                    def role = Role.findByAuthority(Roles.ROLE_USER.name)
                    UserRole.create(user, role, true)
                    final_usernames.add(user.username)

                }


            }

        }


        def result = ['status': status, "username": final_usernames]
        render result as JSON

    }

    def createUserCredentials() {
        def user = springSecurityService.currentUser as User
        def credentialsName = params.credentialsName
        def accessKey = params.accessKey
        def secretKey = params.secretKey
        CrowdService serviceType = CrowdService.valueOf(params.serviceType)
        def sandbox = params.sandboxSetting

        def status = 'success'

        def credentials = CrowdServiceCredentials.findByUserAndName(user, credentialsName)
        if (credentials) {
            status = "duplicate"

        } else {
            if (serviceType == CrowdService.MTURK) {
                if (sandbox == "sandbox" || sandbox == "both") {
                    credentials = CrowdServiceCredentials.create(user, credentialsName, accessKey, secretKey, serviceType)
                    credentials.setSandbox(true)
                    credentials.save(flush: true)
                }
                if (sandbox == "production" || sandbox == "both") {
                    credentials = CrowdServiceCredentials.create(user, credentialsName, accessKey, secretKey, serviceType)
                    credentials.setSandbox(false)
                    credentials.save(flush: true)
                }

            } else {

                credentials = CrowdServiceCredentials.create(user, credentialsName, accessKey, secretKey, serviceType)

            }
            if (credentials) {
                status = "failure"
            }
        }

        def result = ['status': status]
        render result as JSON

    }

    def getExperiment() {
        def result = Experiment.get(params.id)
        render result ? result : "{'status': 'No experiment with id = ${params.id} found'}" as JSON
    }

    def getExperimentData() {
        //TODO create a custom marshaller - @see https://blog.mrhaki.com/2013/11/grails-goodness-register-custom.html,
        //TODO https://manbuildswebsite.com/2010/02/15/rendering-json-in-grails-part-3-customise-your-json-with-object-marshallers/
        def exp = Experiment.get(params.experimentId)

        def data = exp.defaultSessionParams.properties.findResults {
            def value = null
            switch (it.key) {
                case "constraintTests":
                    value = exp.defaultSessionParams.constraintTests.collect {
                        it.buildMturkString()
                    }.join(";\n")
                    break
                case "story":
                    value = exp.defaultSessionParams?.story?.name
                    break
                case "networkTemplate":
                    value = exp.defaultSessionParams?.networkTemplate?.toString()
                    break
                case ["parentParameters", "parentParametersId", "constraintService", "storyId", "networkTemplateId"]:
                    return null
                default:
                    value = it.value
            }
            [(it.key): value]
        }.collectEntries()
        data['name'] = exp.name
        def result = ['status': 'OK', 'data': data]
        render result as JSON

    }

    def getDynamicSessionInfo() {
        def result = ["waiting": null, "active": null]
        result['waiting'] = Session.findAllByState(Session.State.WAITING).collectEntries { Session loomSession ->
            List<UserSession> us = UserSession.findAllBySession(loomSession);
            def missingcount = sessionService.countMissing(us)

            Map counts = experimentService.countWaitingUsers(loomSession).sort {
                a, b  -> a.key.id <=> b.key.id
            }

            String countString = counts.collect { k, v -> "$k=$v" }.join(', ')

            [loomSession.id, ['started'  : loomSession.startWaiting,
                              'elapsed'  : (int) (System.currentTimeMillis() - loomSession.startWaiting.time) / 1000,
                              'connected': countString,
                              'stopped'  : UserSession.countBySessionAndState(loomSession, UserSession.State.STOP),
                              'missing'  : missingcount]]

        }

        result['active'] = Session.findAllByState(Session.State.ACTIVE).collectEntries { Session loomSession ->
            List<UserSession> us = UserSession.findAllBySession(loomSession);
            def missingcount = sessionService.countMissing(us)


            [loomSession.id, ['state'       : "ACTIVE",
                              'started'     : loomSession.startActive,
                              'round'       : experimentService.experimentsRunning[loomSession.id]?.round,
                              'connected'   : UserSession.countBySessionAndState(loomSession, UserSession.State.ACTIVE),
                              'missing'     : missingcount,
                              'round-status': experimentService.experimentsRunning[loomSession.id]?.currentStatus?.toString()]]

        }
        result['cancelled'] = Session.findAllByState(Session.State.CANCEL).collect {Session loomSession -> loomSession.id}
        result['finished'] = Session.findAllByState(Session.State.FINISHED).collect {Session loomSession -> loomSession.id}

        render result as JSON


    }

    def fixConstraintValues() {
        adminService.fixDuplicateConstraints()
    }

    def getStorySeedUpdate() {
        render(view: 'story_seed_form', model: [stories   : Story.list()])
    }

    def fixStorySeeds() {
        log.debug("Fixing story seeds")
        Map<String,List<StorySeed>> seedmap = new HashMap()
        StorySeed.list().each { StorySeed seed ->
            if (seed.name.endsWith("-PRO") || seed.name.endsWith("-ANTI")) {
                String result = (seed.name =~ /(.+)(-ANTI|-PRO)/)[0][1]
                if (!seedmap.containsKey(result)) {
                    seedmap[result] = []
                }
                seedmap[result]<<seed
            }
        }
        println("Collected ${seedmap}")

        seedmap.each {String name, List<StorySeed> seeds ->
            StorySeed targetseed = seeds[0]
            targetseed.name = name
            targetseed.constraintTitle = targetseed.getConstraintTitle()
            targetseed.save(flush:true)
            seeds.tail().each { StorySeed seedToPrune ->
                UserConstraintValue.findAllByConstraintProvider(seedToPrune).each { UserConstraintValue ucv ->
                    ucv.constraintProvider = targetseed
                    ucv.save(flush:true)
                }
                Story.findAllBySeed(seedToPrune).each { Story s ->
                    s.seed = targetseed
                    s.save(flush:true)
                }
                ConstraintTest.findAllByConstraintProvider(seedToPrune).each { ConstraintTest ct ->
                    ct.constraintProvider = targetseed
                    ct.save(flush:true)
                }
                seedToPrune.name = "x_"+seedToPrune.name
                seedToPrune.constraintTitle = seedToPrune.getConstraintTitle()
                seedToPrune.save(flush:true)
                print("Marked ${seedToPrune.constraintTitle} for deletion")
            }
        }
    }

    def updateStorySeeds() {
        List<StorySeed> storySeedUpdates = []
        List<Story> storyUpdates = []
        List<UserConstraintValue> ucvUpdates = []
        Map<String,StorySeed> seedMap = new HashMap<>()

        request.JSON.each {
            Long id = it.value.id as Long
            String seed = it.value.seed
            Story story = Story.get(id)
            StorySeed storySeed = null
            if (seed) {
                storySeed = StorySeed.findByName(seed)?:seedMap.get(seed)
                if (!storySeed) {
                    log.debug("Adding new story seed: ${seed}")
                    storySeed = new StorySeed(name: seed)
                    seedMap.put(seed,storySeed)
                    storySeedUpdates << storySeed
                }
            }
            if (story?.seed?.name != seed) {
                story.seed = storySeed
                storyUpdates<<story
                if (seed) {
                    List<UserConstraintValue> ucvs = UserConstraintValue.findAllByConstraintProvider(story)
                    ucvs.each {
                        it.constraintProvider = storySeed
                        ucvUpdates<<it
                    }
                }

            }

        }

        List<ConstraintTest> ctUpdates = []
        Story.list().each { Story it ->
            if (it.seed) {
                ConstraintTest.findAllByConstraintProvider(it).each { ConstraintTest ct ->
                    ct.constraintProvider = it.seed
                    ctUpdates<<ct
                }
            }
        }


        // Save all instances using withTransaction for atomicity
        Story.withTransaction { status ->
            storySeedUpdates.each { storySeed ->
                if (!storySeed.save(flush: true)) {
                    flash.message = "Failed to update story seeds"
                    status.setRollbackOnly()
                    return
                }
            }

            storyUpdates.each { story ->
                if (!story.save(flush: true)) {
                    flash.message = "Failed to update stories"
                    status.setRollbackOnly()
                    return
                }
            }
            ucvUpdates.each { ucv ->
                if (!ucv.save(flush: true)) {
                    flash.message = "Failed to update user constraint values"
                    status.setRollbackOnly()
                    return
                }
            }

            ctUpdates.each { test ->
                if (!test.save(flush: true)) {
                    flash.message = "Failed to update constraint tests"
                    status.setRollbackOnly()
                    return
                }
            }
        }


        flash.message = "Stories updated successfully"
        render(status: 200, contentType: 'application/json') {
            message = 'Stories updated successfully'
            seed_updates = storySeedUpdates.size()
            story_updates = storyUpdates.size()
            constraint_test_updates = ctUpdates.size()
            ucv_updates = ucvUpdates.size()
        }

    }

}