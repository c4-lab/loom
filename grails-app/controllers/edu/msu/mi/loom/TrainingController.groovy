package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONElement
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Slf4j
@Secured("ROLE_USER")
class TrainingController {

    static allowedMethods = [
            submitTraining: 'POST',
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
        [model:TrainingSet.list().collectEntries {
            UserTrainingSet ts = UserTrainingSet.findByUserAndTrainingSet(u,it)
            [it,ts?.trainingEndTime]
        }]

    }

    def trainingComplete() {
        User user = springSecurityService.currentUser as User
        TrainingSet ts = TrainingSet.get(params.trainingSetId)
        render view:"trainingComplete",model:[confirmationCode:UserTrainingSet.findByUserAndTrainingSet(user,ts).confirmationCode]
    }

    def submitTraining() {

        def userTails = params.userTails
        log.debug("User Tails: ${userTails}")
        List<String> tailsList
        if (userTails) {
            tailsList = userTails as List
        }
        def trainingId = params.training


        if (trainingId) {
            def training = Training.findById(trainingId)
            def story = Story.findByTraining(training)
            def tails = Tile.executeQuery(("from Tile t where t.story=? order by t.text_order asc"), [story])
            println "Submitted: $tailsList"
            println "Correct: ${tails.text}"
            if (tails.text.equals(tailsList)) {
                def user = springSecurityService.currentUser as User
                trainingSetService.changeTrainingState(user,training,null,null,null)
                log.debug("Redirect to training")
                redirect(action: 'training', params: [trainingId: training.trainingSet.id])
                return
            } else {
                //UGH - Emil, this sucks
                def tts = TrainingTask.findAllByTraining(training).tail
                def userTiles = tailsList.collect { tileText->
                    tts.find {
                        it.text == tileText
                    }
                }
                flash.error = true
                render(view: 'training', model: [tts: tts, training: training, tailsList: userTiles, rawTails: userTails, uiflag: params.uiflag as int])
                return
            }
        }

        render(status: BAD_REQUEST)
    }



    def simulation() {

        def trainingSetId = params.trainingSet
        def roundNumber = params.roundNumber
        def tempStory = params.tempStory
        def user = springSecurityService.currentUser as User
        def uiflag = params.uiflag
        println "usdfsdfdfdf"
        println(uiflag)
        println "Round $roundNumber - $tempStory"
        if (trainingSetId) {
            def trainingSet = TrainingSet.get(trainingSetId)
            if (UserTrainingSet.findByTrainingSetAndUser(trainingSet,user)?.complete) {
                return redirect(action:"trainingComplete", params: [trainingId: trainingSetId])
            }
            if (trainingSet) {
                def simModel = simulationService.simulation(trainingSet, roundNumber, tempStory)
                log.debug("Received model $simModel")
                //silly, this means that we are done

                if (simModel instanceof JSON) {
                    //TODO handle multiple simulations
                    trainingSetService.changeTrainingState(user,null,trainingSet.simulations.first(),null,null)

                    return render(status: OK, text: simModel)
                } else if (simModel.tempStory) {
                    log.debug("Should render sim content with $simModel")
                    return render(template: 'simulation_content', model: simModel+[uiflag:uiflag as int])
                } else {
                    return render(view: 'simulation', model: simModel+[uiflag:uiflag as int])
                }
            }
        }
        redirect(uri: '/not-found')
    }

    def submitSimulation() {
        log.debug("Submit simulation: $params")
        def userTails = params.tails
        List<Integer> tailsList

        if (userTails) {
            tailsList = Arrays.asList(userTails.split(";"))
        }
        def simulationId = params.simulation
        log.debug("Got sim id ")
        if (simulationId && params.roundNumber!=null) {
            def simulation = Simulation.findById(simulationId)
            def roundNumber = params.roundNumber.split("[^0-9]+")[1] as Integer
            simulationService.addRoundScore(tailsList, simulation)

            def tempSimulation = new TempSimulation(simulation: simulation, currentTails: tailsList, user: springSecurityService.currentUser as User).save(flush: true)
            redirect(action: 'simulation', params: [trainingSet: simulation?.trainingSet?.id, roundNumber: roundNumber+1, tempStory: tempSimulation?.currentTails,uiflag:simulation?.trainingSet?.uiflag])
            return
        }

        render(status: BAD_REQUEST)
    }

    def score() {
        def simulation = Simulation.get(params.simulationId)
        def training = simulation.trainingSet
        def user = springSecurityService.currentUser
        def scores = UserTrainingSet.findByUserAndTrainingSet(user,training).simulationsCompleted.first().scores
        def qualifier = training.qualifier
        def action = "trainingComplete"

//        mturkService.assignQualification(user.turkerId,Simulation.constructQualificationString(simulation), scores.last())
        render(view:"trainingScore",model:[scores:scores,trainingId:training.id, action:action])
    }

    def reading(){
        def trainingSetId = params.trainingSetId
        ArrayList<Reading> readingTasks = TrainingSet.get(params.trainingSetId).readings.sort{it.id} as ArrayList<Reading>
        render(view:"reading", model:[trainingSetId:trainingSetId, readingTasks:readingTasks])
    }


    def readingComplete(){

        User user = springSecurityService.currentUser as User
        def trainingSetId = params.trainingSetId
        def trainingSet = TrainingSet.get(trainingSetId as Integer)
        def correct = 0
        def total = 0
        trainingSet.readings.eachWithIndex{ read, int i ->
            read.questions.eachWithIndex{ ReadingQuestion ques, int j ->
                List corrects = ques.corrects
                if(corrects.contains(params["question"+ques.id] as Integer)){
                    correct = correct + 1
                }
                total = total + 1
            }
        }
        trainingSetService.changeTrainingState(user,null,null,trainingSet.readings.first(),null,null,correct/total as Float)
        redirect(action: 'training', params: [trainingId: trainingSetId, begin:true])
    }

    def survey(){
        def trainingSetId = params.trainingSetId
        List<Survey> surveyTask = TrainingSet.get(params.trainingSetId).surveys.sort{it.id} as ArrayList<Survey>
        render(view:"survey", model: [trainingSetId:trainingSetId, surveyTask:surveyTask])
    }

    def surveyComplete(){

        User user = springSecurityService.currentUser as User
        def trainingSetId = params.trainingSetId
        def trainingSet = TrainingSet.get(trainingSetId)
        def scores = 0
        trainingSet.surveys.eachWithIndex{ Survey sur, int i ->
            int score = params["question"+sur.id.toString()] as Integer
            scores = scores + score

        }

        trainingSetService.changeTrainingState(user,null,null,null,trainingSet.surveys.first(), null,scores as Float)

        redirect(action: 'training', params: [trainingId: trainingSetId, begin:true])

    }

    def demographicsComplete(){
        def trainingSet = TrainingSet.get(params.trainingSetId)
        def user = springSecurityService.currentUser as User
        List countryList = params.country
        String country = countryList[0]
        if("Other" in countryList[0]){
            country =  countryList[1]
        }
        List languageList = params.language
        String language = languageList[0]
        if("Other" in languageList[0]){
            language =  languageList[1]
        }
        List genderList = params.gender
        String gender = genderList[0]
        if("Other" in genderList[0]){
            gender =  genderList[1]
        }
        Demographics demographics = new Demographics(gender: gender, age: params.age, country: country, language: language, education: params.education, income: params.income,
                political: params.political, user: user)
        if (!demographics.save(flush: true)) {
            log.error("Demographics creation attempt failed")
            return null;
        }

        trainingSetService.changeTrainingState(user,null,null,null,null,trainingSet.id)
        redirect(action: 'training', params: [trainingId: trainingSet.id, begin:true])
    }

    def getTrainingScore() {
        def userTails = params.userTiles
        println userTails
        log.debug("User Tails: ${userTails}")
        List<Long> tailsList
        List<Long> correct

        if (userTails) {
            tailsList = userTails?.split(";").collect {it as Long}
        }

        def trainingId = params.training


        if (trainingId) {
            def training = Training.findById(trainingId)
            def story = Story.findByTraining(training)
            correct = Tile.executeQuery(("from Tile t where t.story=? order by t.text_order asc"), [story]).collect {it.id as Long}
        }
        def result = experimentService.score(correct,tailsList)

        println "Score:${result}"
        render(String.valueOf(result))
    }

    def training() {
        def trainingSetId = params.trainingId as Long
        def user = springSecurityService.currentUser as User
        if (trainingSetId) {

            def trainingSet = TrainingSet.get(trainingSetId)
            def qualifier = trainingSet.qualifier
            UserTrainingSet uts = UserTrainingSet.findByTrainingSetAndUser(trainingSet,user)
            def seqNumber = params.seqNumber?Integer.parseInt(params.seqNumber):null
            def training
            if (uts?.complete) {
                return redirect(action:"index")
            }

            if(!Demographics.findByUser(user)){

                return render(view: 'demographics', model: [trainingSetId: trainingSet.id])
            }
//            if(!uts){
            if(qualifier.contains("read") && !uts?.readingScore){
                redirect(action: 'reading', params: [trainingSetId: trainingSet.id])
            } else if(qualifier.contains("survey") && !uts?.surveyScore){
                redirect(action: 'survey', params: [trainingSetId: trainingSet.id])
            } else if (params.begin) {
                return render(view:"intro",model: [trainingId:trainingSetId])
            }
//            }

            if (seqNumber!=null)
                training = trainingSet.trainings[seqNumber]
            else {
                training = trainingSetService.getNextTraining(user, trainingSet)
                log.debug("Got training $training")
            }

            if (training) {
                println("traanifnsidnfsd")
                def tts = TrainingTask.findAllByTraining(training).tail
                log.debug("Render training")
                render(view: 'training', model: [tts: tts, training: training, uiflag: trainingSet.uiflag as int])
                return
            }
//            else if(!mturkService.hasQualification(user.turkerId, Simulation.constructQualificationString(trainingSet.simulations.first()))){
            else{
                redirect(action: 'simulation', params: [trainingSet: trainingSet.id, roundNumber: 0, uiflag: trainingSet.uiflag as int])
            }

            return

        }

        redirect(uri: '/not-found')
    }

}
