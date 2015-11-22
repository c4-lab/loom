package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Secured("ROLE_USER")
class ExperimentController {
    static allowedMethods = [
            submitTraining: 'POST'
    ]

    def experimentService

    def submitTraining() {
        def userTails = params.tails
        List<String> tailsList = Arrays.asList(userTails.split(";"));

        def trainingId = params.training

        if (trainingId) {
            def training = Training.findById(trainingId)
            String trainingName = training.name
            def seqNumber = trainingName.split("[^0-9]+")[1]

            def story = Story.findByTraining(training)
            def tails = Tail.findAllByStory(story)
            if (tails.text.equals(tailsList)) {
                redirect(action: 'nextTraining', params: [seqNumber: seqNumber, session: training?.session?.id])
                return
            }
        }

        render(status: BAD_REQUEST)
    }

    def nextTraining() {
        def sessionId = params.session
        if (sessionId) {
            def expSession = Session.get(Long.parseLong(sessionId))
            if (expSession && params.seqNumber) {
                def training = experimentService.getNextTraining(expSession, Integer.parseInt(params.seqNumber))
                if (training) {
                    session["seqNumber"] = params.seqNumber
                    def tts = TrainingTask.findAllByTraining(training).tail
                    render(template: '/home/content', model: [tts: tts, training: training])
                    return
                } else {
                    session["seqNumber"] = null
                    render(status: OK, text: [simulation: 'simulation', sesId: sessionId] as JSON)
                    return
                }
            }
        }

        redirect(uri: '/not-found')
    }

    def simulation() {
        def sessionId = params.id

        if (sessionId) {
            def session = Session.get(Long.parseLong(sessionId))
            if (session) {
                def simulation = session.simulations.getAt(0)
                def userCount = simulation.userCount
                def userList = [:]

                if (params.roundNumber) {
                    def roundNumber = Integer.parseInt(params.roundNumber)
                    for (int i = 1; i <= userCount; i++) {
                        def tts = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, i, roundNumber).tail
                        userList.put(i, [roundNbr: roundNumber, tts: tts])
                    }

                    render(view: '/home/simulation', model: [roundNbr: roundNumber, simulation: simulation, userList: userList])
                    return
                } else {
                    for (int i = 1; i <= userCount; i++) {
                        def tts = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, i, 0).tail
                        userList.put(i, [roundNbr: 0, tts: tts])
                    }

                    render(view: '/home/simulation', model: [roundNbr: 0, simulation: simulation, userList: userList])
                    return
                }
            }
        }
        redirect(uri: '/not-found')
    }

    def submitSimulation() {
        def userTails = params.tails
        List<String> tailsList = Arrays.asList(userTails.split(";"));
        def simulationId = params.simulation

        if (simulationId && params.roundNumber) {
            def simulation = Simulation.findById(simulationId)
            def roundNumber = params.roundNumber.split("[^0-9]+")[1]

            def story = Story.findBySimulation(simulation)
            def tails = Tail.findAllByStory(story)
            if (tails.text.equals(tailsList)) {
                redirect(action: 'simulation', params: [roundNumber: roundNumber, id: simulation?.session?.id])
                return
            }
        }

        render(status: BAD_REQUEST)
    }
}
