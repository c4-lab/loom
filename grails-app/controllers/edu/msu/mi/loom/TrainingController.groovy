package edu.msu.mi.loom


import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import grails.converters.JSON
import org.springframework.http.HttpStatus

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Slf4j
@Secured(["ROLE_USER", "ROLE_MTURKER", "ROLE_PROLIFIC"])
class TrainingController {

    static allowedMethods = [
            submitTraining  : 'POST',
            getTrainingScore: 'POST'
    ]

    def experimentService
    def springSecurityService
    def simulationService
    def mturkService


    def trainingSetService


    def index() {
        User u = springSecurityService.currentUser as User
        [model: TrainingSet.list().collectEntries { TrainingSet it ->
            UserTrainingSet ts = UserTrainingSet.findByUserAndTrainingSet(u, it)
            [it, ts?.trainingEndTime]
        }]

    }


    /**
     * Dispatch method - will direct the user to the next training item
     * @return
     */
    def advanceTraining() {
        println("Now advancing training")
        def trainingSetId = params.trainingSetId as Long
        def assignmentId = params?.assignmentId
        def hitId = params?.hitId
        def user = springSecurityService.currentUser as User

        def trainingSet = TrainingSet.get(trainingSetId)
        if (!trainingSet) {
            return render(status: HttpStatus.NOT_FOUND, view: '/not-found')
        }

        if (assignmentId == "null") {
            assignmentId = null
        }

        if (hitId == "null") {
            hitId = null
        }

        MturkAssignment  mturkAssignment = null
        if (assignmentId) {
            println("Assignment id is "+assignmentId)
            mturkAssignment = MturkAssignment.findByAssignmentId(assignmentId)
            if (!mturkAssignment) {
                mturkAssignment = mturkService.attachAssignment(assignmentId, hitId)
            }
            if (!mturkAssignment) {
                return fail("You appear to have come from MTurk with a HIT that we don't know about (HitID = ${hitId}, AssignmentID = ${assignmentId})")
            }
        }

        if (!Demographics.findByUser(user)) {
            return render(view: 'demographics', model: [trainingSetId: trainingSetId, assignmentId: assignmentId])
        }


        UserTrainingSet uts = UserTrainingSet.findByTrainingSetAndUser(trainingSet, user)

        if (!uts) {
            uts = UserTrainingSet.create(user, trainingSet, new Date(), mturkAssignment, false, true)
        }


        if (uts?.complete) {
            return redirect(action: "index")
        }

        if (mturkAssignment && uts.mturkAssignment && uts.mturkAssignment != mturkAssignment) {
            return render(view: 'duplicate_uts')
        }

        if (!uts.intro) {

        }


        Trainable t = trainingSetService.getNextTraining(uts)

        if (t) {
            if (t instanceof Training && !uts.intro) {
                return render(view: "intro", model: [trainingSetId: trainingSetId, assignmentId: assignmentId])
            } else if (t instanceof Simulation && !uts.simIntro) {
                return render(view: "sim_intro", model: [trainingSetId: trainingSetId, assignmentId: assignmentId])
            } else {
                return redirect(action: t.getViewName(), params: [trainingSetId: trainingSet.id, trainingItem: t.id, assignmentId: assignmentId])
            }
        } else {
            trainingSetService.completeTrainingSet(uts)

            return redirect(action: 'trainingSetComplete', params: [trainingSetId: trainingSetId])
        }

    }

    def submitIntro() {
        def user = springSecurityService.currentUser
        def trainingSet = TrainingSet.get(params.trainingSetId)
        UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(user, trainingSet)
        uts.intro = true
        uts.save(flush: true)
        redirect(action: 'advanceTraining', params: [trainingSetId: trainingSet.id, assignmentId: params.assignmentId])
    }

    def submitSimIntro() {
        def user = springSecurityService.currentUser
        def trainingSet = TrainingSet.get(params.trainingSetId)
        UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(user, trainingSet)
        uts.simIntro = true
        uts.save(flush: true)
        redirect(action: 'advanceTraining', params: [trainingSetId: trainingSet.id, assignmentId: params.assignmentId])
    }

    def reading() {
        def trainingSetId = params.trainingSetId
        Reading reading = Reading.get(params.trainingItem)
        render(view: "reading", model: [trainingSetId: trainingSetId, reading: reading, assignmentId: params.assignmentId])
    }

    def fail(String message) {
        render(view: "error",model:[message:message])
    }


    def readingComplete() {

        def trainingSetId = params.trainingSetId
        def reading = Reading.get(params.readingId)
        float correct = 0
        float total = 0

        reading.questions.eachWithIndex { ReadingQuestion ques, int j ->
            List corrects = ques.corrects
            if (corrects.contains(params["question" + ques.id] as Integer)) {
                correct = correct + 1
            }
            total = total + 1
        }
        User u = springSecurityService.currentUser
        UserReadingResponse usrr = new UserReadingResponse(user:u, constraintProvider: reading, value: Math.round(correct*100 / total))
        UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(u, TrainingSet.get(params.trainingSetId))
        uts.addToReadingResponses(usrr)
        uts.save(flush: true)
        if (uts.mturkAssignment) {
            MturkAssignment mta = uts.mturkAssignment
            mta.getHit().getTask().credentials
            mturkService.assignQualification(u.workerId,usrr.constraintProvider,usrr.value,mta.retrieveCredentials())
        }

        redirect(action: 'advanceTraining', params: [trainingSetId: trainingSetId, assignmentId: params.assignmentId])
    }

    def survey() {
        User user = springSecurityService.currentUser as User
        def trainingSetId = params.trainingSetId
        def trainingSet = TrainingSet.get(trainingSetId)
        def userTrainingSet = UserTrainingSet.findByUserAndTrainingSet(user, trainingSet)

        Set<Survey> completed = userTrainingSet.surveyReponses.collect {
            it.castConstraintProvider()
        } as Set


        Survey s = Survey.get(params.trainingItem)
        List<SurveyItem> surveyItems = s.getSurveyItems() as List
        render(view: "survey", model: [trainingSetId: trainingSetId,
                                       survey: s,
                                       assignmentId: params.assignmentId,
                                        total: trainingSet.surveys.size(),
                                        completed: completed.size(),
                                        ])
    }

    def surveyComplete() {

        User user = springSecurityService.currentUser as User
        def trainingSetId = params.trainingSetId
        def trainingSet = TrainingSet.get(trainingSetId)
        def survey = Survey.get(params.surveyId)
        def userTrainingSet = UserTrainingSet.findByUserAndTrainingSet(user, trainingSet)
        List<SurveyOption> options = []
        survey.surveyItems.each { SurveyItem sur ->
            Integer option_id = params["question" + sur.id.toString()] as Integer
            options.add(SurveyOption.get(option_id))
        }

        UserSurveyResponse usr = UserSurveyResponse.completeSurvey(user, options)
        userTrainingSet.addToSurveyReponses(usr)
        userTrainingSet.save(flush: true)

        if (userTrainingSet.mturkAssignment) {
            MturkAssignment mta = userTrainingSet.mturkAssignment
            mta.getHit().getTask().credentials
            mturkService.assignQualification(user.workerId,usr.constraintProvider,usr.value,mta.retrieveCredentials())
        }

        redirect(action: 'advanceTraining', params: [trainingSetId: trainingSetId, assignmentId: params.assignmentId])

    }


    /**
     * Return a training instance; note that the specific training item to be rendered is passed via the "trainingItem" param
     *
     * @return
     */
    def practice() {
        def trainingSet = TrainingSet.get(params.trainingSetId)
        def training = Training.get(params.trainingItem)
        def tts = TrainingTask.findAllByTraining(training).tile
        render(view: 'training', model: [storyTiles: [], allTiles: tts, trainingSet: trainingSet, training: training, uiflag: trainingSet.uiflag as int, assignmentId: params.assignmentId])
    }

    /**
     * Handles ajaxed calls to update the training score
     *
     * @return
     */
    def getTrainingScore() {
        String userTiles = params.userTiles
        def training = Training.findById(params.trainingId)
        def story = training.story


        log.debug("User Tiles: ${userTiles}")
        List<Long> tilesList = []


        if (userTiles) {
            tilesList = userTiles?.split(",")?.collect { it as Long }
        }
        render(String.valueOf(trainingSetService.calculateTrainingScore(tilesList,story.tiles)))

    }


    /**
     * Complete training if correct, otherwise return to view
     *
     * @return
     */
    def submitTraining() {

        def trainingSet = TrainingSet.get(params.trainingSetId)
        def training = Training.findById(params.trainingId)
        def assignmentId = params.assignmentId
        def alltiles = training.story.tiles as List<Tile>
        List storyTiles = ((String) params.storyTiles).split(",").collect {
            try {
                Long.parseLong(it)
            } catch (NumberFormatException nf) {
                return -1
            }
        }

        storyTiles.removeAll {
            it < 0
        }


        if (training) {
            if (trainingSetService.calculateTrainingScore(storyTiles,alltiles)==1.0f) {

                return completeTraining(training, trainingSet, assignmentId)

            } else {
                def userTiles = storyTiles.collect { Tile.get(it) }
                flash.error = true
                Collections.shuffle(alltiles)
                print(alltiles)
                render(view: 'training', model: [allTiles: alltiles, storyTiles: userTiles, trainingSet: trainingSet, training: training, uiflag: params.uiflag as int, assignmentId: assignmentId])
                return
            }
        }
        render(status: BAD_REQUEST)
    }

    def completeTraining(Training t, TrainingSet ts, def assignmentId) {
        def user = springSecurityService.currentUser as User
        UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(user, ts)
        UserTrainingResponse utr = new UserTrainingResponse(user: user, constraintProvider: t)
        utr.value = 1
        uts.addToTrainingResponses(utr)
        uts.save(flush: true)
        if (uts.mturkAssignment) {
            MturkAssignment mta = uts.mturkAssignment
            mta.getHit().getTask().credentials
            mturkService.assignQualification(user.workerId,utr.constraintProvider,utr.value,mta.retrieveCredentials())
        }
        redirect(action: 'advanceTraining', params: [trainingSetId: ts.id, assignmentId: assignmentId])
    }

    /**
     * Respond with the simulation interface; expects the following params
     *
     * trainingSetId
     * trainintItem - in this case, the id of the simulation
     *
     *
     * Optional params:
     * tempStory - user's current story
     * roundNumber
     * assignmentId
     *
     * @return
     */
    def simulation() {
        print("In simulation with params ${params}")
        TrainingSet trainingSet = TrainingSet.get(params.trainingSetId)
        Simulation simulation = Simulation.get(params.trainingItem)
        User user = springSecurityService.currentUser as User
        def uiflag = trainingSet.uiflag

        //The following are optional parameters and may be null
        Integer roundNumber

        if (params.roundNumber) {
            roundNumber = Integer.parseInt(params.roundNumber)+1
        } else {
            roundNumber = 0
        }

        Integer[] tempStory = params.tempStory.collect { Integer.parseInt(it)}
        def assignmentId = params.assignmentId

        //Advance the simulation
        if (roundNumber < simulation.roundCount) {
            //TODO OK JOSH HERE"S HOW THIS WORKS
            //At the end of each round, javascript (see {see @loom.js#submitSimulationAjax}) submits to submitSimulation
            //which in turn assigns a round score to a survey response


            def simModel = simulationService.simulation(simulation, roundNumber, tempStory)
            def model = simModel + [uiflag: uiflag as int, assignmentId: assignmentId, trainingSetId: params.trainingSetId]
            if (roundNumber == 0) {
                println("Return new simulation with model: ${model}")
                return render(view: 'simulation', model: model)
                //THIS IS THE VERY FIRST ROUND
            } else {
                println("Return next simulation round with model: ${model}")
                //NOTE that this is being picked up inline by an ajaxed call for all but the first round
                return render(template: 'simulation_content', model: model)
            }
        } else {

            //TODO OK JOSH HERE"S HOW THIS WORKS
            //Once we send this json back, javascript picks it up (see {see @loom.js#submitSimulationAjax})
            //and forwards to the 'score' method below
            UserSimulationResponse usr = UserSimulationResponse.findByUserAndConstraintProvider(user, simulation)
            usr.value = Math.round(100.0 * usr.scores.last().value)
            UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(user, trainingSet)
            uts.addToSimulationResponses(usr)
            uts.save(flush: true)
            if (uts.mturkAssignment) {
                MturkAssignment mta = uts.mturkAssignment
                mta.getHit().getTask().credentials
                mturkService.assignQualification(user.workerId,usr.constraintProvider,usr.value,mta.retrieveCredentials())
            }
            return render(status: OK, text: [status: 'simulation_complete'] as JSON)
        }
    }

    def submitSimulation() {
        String userTiles = params.tiles
        List<Integer> tilesList

        if (userTiles) {
            tilesList = userTiles.split(";").collect{Long.parseLong(it)}
        } else {
            tilesList = []
        }
        def simulationId = params.simulation

        if (simulationId && params.roundNumber != null) {
            def simulation = Simulation.get(simulationId)
            def roundNumber = Integer.parseInt(params.roundNumber)
            simulationService.addRoundScore(tilesList, simulation,roundNumber)

            def tempSimulation = new TempSimulation(simulation: simulation, currentTiles: tilesList, user: springSecurityService.currentUser as User).save(flush: true)
            redirect(action: 'simulation', params: [trainingSetId: params.trainingSetId, trainingItem: params.simulation, roundNumber: roundNumber, tempStory: tempSimulation?.currentTiles, assignmentId: params.assignmentId])
        } else {
            render(status: BAD_REQUEST)
        }
    }

    /**
     * This is an action method, that will render once we have finished a simulation (called from ajaxed method "submitSimulation"
     *
     * @return
     */
    def viewSimulationScores() {

        def user = springSecurityService.currentUser
        def usr = UserSimulationResponse.findByUserAndConstraintProvider(user,Simulation.get(params.simulationId))
        def assignmentId = params.assignmentId

        render(view: "simulationScore", model: [usersimresult: usr, assignmentId: assignmentId, trainingSetId: params.trainingSetId])
    }

    /**
     * This is an action method called by the simulation view
     * @return
     */
    def trainingSetComplete() {
        User user = springSecurityService.currentUser as User
        TrainingSet ts = TrainingSet.get(params.trainingSetId)
        UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(user,ts)

        if (uts.mturkAssignment) {
            MturkAssignment mta = uts.mturkAssignment
            mta.getHit().getTask().credentials
            mturkService.assignQualification(user.workerId,ts,1,mta.retrieveCredentials())
        }
        render view: "trainingSetComplete", model: [confirmationCode: UserTrainingSet.findByUserAndTrainingSet(user, ts).confirmationCode, user: user]
    }





    def demographicsComplete() {
        def trainingSet = TrainingSet.get(params.trainingSetId)
        def user = springSecurityService.currentUser as User


        List raceList = params.race
        String race = raceList[0]
        if ("Other" in raceList[0]) {
            race = raceList[1]
        }

        List languageList = params.language
        String language = languageList[0]
        if ("Other" in languageList[0]) {
            language = languageList[1]
        }
        List genderList = params.gender
        String gender = genderList[0]
        if ("Other" in genderList[0]) {
            gender = genderList[1]
        }
        List politicsList = params.political
        String politics = politicsList[0]
        if ("Other" in politicsList[0]) {
            politics = politicsList[1]
        }
        Demographics demographics = new Demographics(gender: gender, age: params.age,  race: race, language: language, education: params.education, income: params.income,
                political: politics, user: user)
        if (!demographics.save(flush: true)) {
            log.error("Demographics creation attempt failed")
            return null;
        }

        redirect(action: 'advanceTraining', params: [trainingSetId: trainingSet.id, begin: true, assignmentId: params.assignmentId])
    }




}
