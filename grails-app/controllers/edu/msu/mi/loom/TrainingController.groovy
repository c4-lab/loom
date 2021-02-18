package edu.msu.mi.loom

import grails.converters.JSON
//import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Slf4j
//@Secured("ROLE_USER")
class TrainingController {

    static allowedMethods = [
            submitTraining: 'POST',
            getTrainingScore: 'POST'
    ]

    def experimentService
    def springSecurityService
    def simulationService
    def statService

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
        def userTails = params.tails
        log.debug("User Tails: ${userTails}")
        List<String> tailsList
        if (userTails) {
            tailsList = userTails?.split(";") as List
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
                trainingSetService.changeTrainingState(user,training,null)
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
                render(view: 'training', model: [tts: tts, training: training, tailsList: userTiles, rawTails: userTails])
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
        println "Round $roundNumber - $tempStory"
        if (trainingSetId) {
            def trainingSet = TrainingSet.get(trainingSetId)
            if (UserTrainingSet.findByTrainingSetAndUser(trainingSet,user)?.complete) {
                return redirect(action:"trainingComplete")
            }
            if (trainingSet) {
                def model = simulationService.simulation(trainingSet, roundNumber, tempStory)
                log.debug("Received model $model")
                //silly, this means that we are done

                if (model instanceof JSON) {
                    //TODO handle multiple simulations
                    trainingSetService.changeTrainingState(user,null,trainingSet.simulations.first())

                    return render(status: OK, text: model)
                } else if (model.tempStory) {
                    log.debug("Should render sim content with $model")
                    return render(template: 'simulation_content', model: model)
                } else {
                    return render(view: 'simulation', model: model)
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
            redirect(action: 'simulation', params: [trainingSet: simulation?.trainingSet?.id, roundNumber: roundNumber+1, tempStory: tempSimulation?.currentTails])
            return
        }

        render(status: BAD_REQUEST)
    }

    def score() {
        def training = Simulation.get(params.simulationId).trainingSet
        def user = springSecurityService.currentUser
        def scores = UserTrainingSet.findByUserAndTrainingSet(user,training).simulationsCompleted.first().scores
        render(view:"trainingScore",model:[scores:scores,trainingId:training.id])
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
            if (UserTrainingSet.findByTrainingSetAndUser(trainingSet,user)?.complete) {
                return redirect(action:"index")
            } else if (!UserTrainingSet.countByTrainingSetAndUser(trainingSet,user) && !params.begin) {
               return render(view:"intro",model: [trainingId:trainingSetId])
            }

            def seqNumber = params.seqNumber?Integer.parseInt(params.seqNumber):null
            def training

            if (seqNumber!=null)
                training = trainingSet.trainings[seqNumber]
            else {
                training = trainingSetService.getNextTraining(user, trainingSet)
                log.debug("Got training $training")
            }
            if (training) {
                def tts = TrainingTask.findAllByTraining(training).tail
                log.debug("Render training")
                render(view: 'training', model: [tts: tts, training: training])
                return
            } else {
                redirect(action: 'simulation', params: [trainingSet: trainingSet.id, roundNumber: 0])
            }

            return

        }

        redirect(uri: '/not-found')
    }






}
