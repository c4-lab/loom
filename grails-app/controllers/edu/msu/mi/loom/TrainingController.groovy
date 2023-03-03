package edu.msu.mi.loom


import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import grails.converters.JSON

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Slf4j
@Secured(["ROLE_USER", "ROLE_MTURKER"])
class TrainingController {

    static allowedMethods = [
            submitTraining  : 'POST',
            getTrainingScore: 'POST'
    ]

    def experimentService
    def springSecurityService
    def simulationService
    def statService
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
        def trainingSetId = params.trainingSetId as Long
        def assignmentId = params.assignmentId
        def user = springSecurityService.currentUser as User

        if (!Demographics.findByUser(user)) {
            return render(view: 'demographics', model: [trainingSetId: trainingSetId, assignmentId: assignmentId])
        }

        def trainingSet = TrainingSet.get(trainingSetId)
        UserTrainingSet uts = UserTrainingSet.findByTrainingSetAndUser(trainingSet, user)
        if (!uts) {
            uts = new UserTrainingSet(user: user, trainingSet: trainingSet, trainingStartTime: new Date(), assignmentId: assignmentId)
            uts.save(flush: true)

        }
        if (uts?.complete) {
            return redirect(action: "index")
        }

        if (assignmentId && uts.assignmentId && uts.assignmentId != assignmentId as String) {
            return render(view: 'duplicate_uts')
        }
        if (!uts.intro) {
            return render(view: "intro", model: [trainingSetId: trainingSetId, assignmentId: assignmentId])
        }


        Trainable t = trainingSetService.getNextTraining(uts)

        if (t) {
            return redirect(action: t.getViewName(), params: [trainingSetId: trainingSet.id, trainingItem: t.id, assignmentId: assignmentId])
        } else {
            trainingSetService.completeTrainingSet(uts)
            return redirect(action: 'trainingSetComplete', params: [trainingSetId: trainingSetId, assignmentId: assignmentId])
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

    def reading() {
        def trainingSetId = params.trainingSetId
        Reading reading = Reading.get(params.trainingItem)
        render(view: "reading", model: [trainingSetId: trainingSetId, reading: reading, assignmentId: params.assignmentId])
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

        UserReadingResponse usrr = new UserReadingResponse(reading: reading, score: correct / total)
        UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(springSecurityService.currentUser, TrainingSet.get(params.trainingSetId))
        uts.addToReadingResponse(usrr)
        uts.save(flush: true)

        redirect(action: 'advanceTraining', params: [trainingSetId: trainingSetId, assignmentId: params.assignmentId])
    }

    def survey() {
        def trainingSetId = params.trainingSetId
        Survey s = Survey.get(params.trainingItem)
        List<SurveyItem> surveyItems = s.getSurveyItems() as List
        render(view: "survey", model: [trainingSetId: trainingSetId, survey: s, assignmentId: params.assignmentId])
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
        userTrainingSet.addToSurveyReponse(usr)
        userTrainingSet.save(flush: true)
        redirect(action: 'advanceTraining', params: [trainingSetId: trainingSetId, assignmentId: params.assignmentId])

    }


    /**
     * Return a training instance; note that the specific training item to be rendered is passed via the "trainingItem" param
     *
     * @return
     */
    def training() {
        def trainingSet = TrainingSet.get(params.trainingSetId)
        def training = Training.get(params.trainingItem)
        def tts = TrainingTask.findAllByTraining(training).tile
        render(view: 'training', model: [storyTiles: [], allTiles: tts, trainingSet: trainingSet, training: training, uiflag: trainingSet.uiflag as int, assignmentId: params.assignmentId])
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


        List storyTiles = ((String) params.storyTiles).split(",").collect { Integer.parseInt(it) }


        if (training) {

            def allTiles = TrainingTask.findAllByTraining(training).tile
            def tile_order = allTiles.collect({ it.text_order }).sort()

            if (tile_order.equals(storyTiles)) {
                return completeTraining(training, trainingSet, assignmentId)

            } else {
                def userTiles = storyTiles.collect { submitted_order ->
                    allTiles.find {
                        it.text_order == submitted_order
                    }
                }
                flash.error = true
                render(view: 'training', model: [allTiles: allTiles, storyTiles: userTiles, trainingSet: trainingSet, training: training, uiflag: params.uiflag as int, assignmentId: assignmentId])
                return
            }
        }
        render(status: BAD_REQUEST)
    }

    def completeTraining(Training t, TrainingSet ts, def assignmentId) {
        def user = springSecurityService.currentUser as User
        UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(user, ts)
        UserTrainingResponse utr = new UserTrainingResponse(user: user, date: new Date(), training: t)
        uts.addToTrainingResponse(utr)
        uts.save(flush: true)
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

        TrainingSet trainingSet = TrainingSet.get(params.trainingSetId)
        Simulation simulation = Simulation.get(params.trainingItem)
        User user = springSecurityService.currentUser as User
        def uiflag = trainingSet.uiflag

        //The following are optional parameters and may be null
        Integer roundNumber = params.roundNumber
        if (roundNumber == null) {
            roundNumber = 0
        } else {
            roundNumber++
        }
        Integer[] tempStory = params.tempStory
        def assignmentId = params.assignmentId

        //Advance the simulation
        if (roundNumber <= simulation.roundCount) {
            //TODO OK JOSH HERE"S HOW THIS WORKS
            //At the end of each round, javascript (see {see @loom.js#submitSimulationAjax}) submits to submitSimulation
            //which in turn assigns a round score to a survey response


            def simModel = simulationService.simulation(simulation, roundNumber, tempStory)
            def model = simModel + [uiflag: uiflag as int, assignmentId: assignmentId, trainingSetId: params.trainingSetId]
            if (roundNumber == 0) {
                return render(view: 'simulation', model: model)
                //THIS IS THE VERY FIRST ROUND
            } else {
                //NOTE that this is being picked up inline by an ajaxed call for all but the first round
                return render(template: 'simulation_content', model: model)
            }
        } else {

            //TODO OK JOSH HERE"S HOW THIS WORKS
            //Once we send this json back, javascript picks it up (see {see @loom.js#submitSimulationAjax})
            //and forwards to the 'score' method below
            UserSimulationResponse usr = UserSimulationResponse.findByUserAndSimulation(user, simulation)
            usr.averageScore = usr.scores.sum() / (Float) usr.scores.size()
            UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(user, trainingSet)
            uts.addToSimulationResponse(usr)
            uts.save(flush: true)
            return render(status: OK, text: [status: 'simulation_complete'] as JSON)
        }
    }

    def submitSimulation() {
        String userTiles = params.tiles
        List<Integer> tilesList

        if (userTiles) {
            tilesList = userTiles.split(";").collect{Integer.parseInt(it)}
        } else {
            tilesList = []
        }
        def simulationId = params.simulation

        if (simulationId && params.roundNumber != null) {
            def simulation = Simulation.get(simulationId)
            def roundNumber = params.roundNumber.split("[^0-9]+")[1] as Integer
            simulationService.addRoundScore(tilesList, simulation)

            def tempSimulation = new TempSimulation(simulation: simulation, currentTiles: tilesList, user: springSecurityService.currentUser as User).save(flush: true)
            redirect(action: 'simulation', params: [trainingSetId: params.trainingSetId, trainingItem: params.simulatipon, roundNumber: roundNumber, tempStory: tempSimulation?.currentTiles, assignmentId: params.assignmentId])
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
        def usr = UserSimulationResponse.findByUserAndSimulation(user,Simulation.get(params.simulationId))
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
        def assignmentId = params.assignmentId
        render view: "trainingSetComplete", model: [confirmationCode: UserTrainingSet.findByUserAndTrainingSet(user, ts).confirmationCode, assignmentId: assignmentId]
    }





    def demographicsComplete() {
        def trainingSet = TrainingSet.get(params.trainingSetId)
        def user = springSecurityService.currentUser as User
        List countryList = params.country
        String country = countryList[0]
        if ("Other" in countryList[0]) {
            country = countryList[1]
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
        Demographics demographics = new Demographics(gender: gender, age: params.age, country: country, language: language, education: params.education, income: params.income,
                political: params.political, user: user)
        if (!demographics.save(flush: true)) {
            log.error("Demographics creation attempt failed")
            return null;
        }

        redirect(action: 'advanceTraining', params: [trainingSetId: trainingSet.id, begin: true, assignmentId: params.assignmentId])
    }

    /**
     * Handles ajaxed calls to update the training score
     *
     * @return
     */
    def getTrainingScore() {
        String userTiles = params.userTiles

        log.debug("User Tails: ${userTiles}")
        List<Long> tilesList = []


        if (userTiles) {
            tilesList = userTiles?.split(",")?.collect { it as Long }
        }

        def trainingId = params.trainingSetId


        def training = Training.findById(trainingId)
        def story = Story.findByTraining(training)
        List<Long> correct = Tile.executeQuery(("from Tile t where t.story=? order by t.text_order asc"), [story]).collect { it.id as Long }

        def result = experimentService.score(correct, tilesList)

        println "Score:${result}"
        render(String.valueOf(result))
    }


}
