package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Slf4j
@Secured("ROLE_USER")
class ExperimentController {
    static allowedMethods = [
            submitTraining: 'POST'
    ]

    def experimentService
    def springSecurityService

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
                def roundNumber
                if (params.roundNumber) {
                    roundNumber = Integer.parseInt(params.roundNumber)
                    if (roundNumber < simulation.roundCount) {
                        for (int i = 1; i <= userCount; i++) {
                            def tts = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, i, roundNumber).tail
                            userList.put(i, [roundNbr: roundNumber, tts: tts])
                        }

                        def tailList = []
                        if (params.tempStory) {
                            params.tempStory.each {
                                tailList.add(Tail.findById(it))
                            }
                        }


                        render(template: '/home/simulation_content', model: [roundNbr: roundNumber, simulation: simulation, userList: userList, tempStory: tailList])
                        return
                    } else {
                        def user = springSecurityService.currentUser as User
                        user.isReady = true
                        user.save(flush: true)

                        render(status: OK, text: [experiment: 'experiment', sesId: sessionId] as JSON)
                        return
                    }
                } else {
                    roundNumber = 0
                    for (int i = 1; i <= userCount; i++) {
                        def tts = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, i, roundNumber).tail
                        userList.put(i, [roundNbr: roundNumber, tts: tts])
                    }

                    render(view: '/home/simulation', model: [roundNbr: roundNumber, simulation: simulation, userList: userList])
                    return
                }
            }
        }
        redirect(uri: '/not-found')
    }

    def submitSimulation() {
        def userTails = params.tails
        List<Integer> tailsList = Arrays.asList(userTails.split(";"));
        def simulationId = params.simulation

        if (simulationId && params.roundNumber) {
            def simulation = Simulation.findById(simulationId)
            def roundNumber = params.roundNumber.split("[^0-9]+")[1]

            def tempSimulation = new TempSimulation(simulation: simulation, currentTails: tailsList, user: springSecurityService.currentUser as User).save(flush: true)

            redirect(action: 'simulation', params: [id: simulation?.session?.id, roundNumber: roundNumber, tempStory: tempSimulation?.currentTails])
            return
        }

        render(status: BAD_REQUEST)
    }

    def experiment() {
        def sessionId = params.id

        if (sessionId) {
            def session = Session.get(Long.parseLong(sessionId))
//            if (User.countByRoomAndIsReady(session?.room, true) == session.experiments.getAt(0).userCount) {
                def experiment = session.experiments.getAt(0)
                def userCount = experiment.userCount
                def userList = [:]
                def roundNumber
                if (params.roundNumber) {
                    roundNumber = Integer.parseInt(params.roundNumber)
                    def tailList = []
                    if (params.tempStory) {
                        params.tempStory.each {
                            tailList.add(Tail.findById(it))
                        }
                    }

                    if (roundNumber < experiment.roundCount) {
                        for (int i = 1; i <= userCount; i++) {
                            def tts = ExperimentTask.findAllByExperimentAndUser_nbrAndRound_nbr(experiment, i, roundNumber).tail
                            userList.put(i, [roundNbr: roundNumber, tts: tts])
                        }

                        render(template: '/home/experiment_content', model: [roundNbr: roundNumber, experiment: experiment, userList: userList, tempStory: tailList])
                        return
                    } else {
                        def user = springSecurityService.currentUser as User
                        flash."${user.alias}-${experiment.id}" = tailList.text_order
                        render(status: OK, text: [experiment: 'finishExperiment', sesId: sessionId] as JSON)
                        return
                    }
                } else {
                    roundNumber = 0
                    for (int i = 1; i <= userCount; i++) {
                        def tts = ExperimentTask.findAllByExperimentAndUser_nbrAndRound_nbr(experiment, i, roundNumber).tail
                        userList.put(i, [roundNbr: roundNumber, tts: tts])
                    }

                    render(view: '/home/experiment', model: [roundNbr: roundNumber, experiment: experiment, userList: userList])
                    return
                }

//            } else {
//
//            }
        }

        render(status: BAD_REQUEST)
    }

    def submitExperiment() {
        def userTails = params.tails
        List<String> tailsList = Arrays.asList(userTails.split(";"));
        def experimentId = params.experiment

        if (experimentId && params.roundNumber) {
            def experiment = Experiment.findById(experimentId)
            def roundNumber = params.roundNumber.split("[^0-9]+")[1]

            def tempExperiment = new TempExperiment(experiment: experiment, currentTails: tailsList, user: springSecurityService.currentUser as User).save(flush: true)

            redirect(action: 'experiment', params: [id: experiment?.session?.id, roundNumber: roundNumber, tempStory: tempExperiment?.currentTails])
            return
        }

        render(status: BAD_REQUEST)
    }

    def finishExperiment() {
        def sessionId = params.session

        if (sessionId) {
            def session = Session.get(sessionId)

            if (session) {
                def experiment = session.experiments.getAt(0)

                if (experiment) {
                    def user = springSecurityService.currentUser as User
                    def story = UserStory.findByExperimentAndAlias(experiment, user.alias)?.story
                    def rightStory = Tail.findAllByStory(story).text_order
                    def userStory = flash."${user.alias}-${experiment.id}"

                    println "-----right story--------"
                    println rightStory
                    println "::::::::::::::::::::::::::::::::::"
                    println "-----user story--------"
                    println userStory
                    println "::::::::::::::::::::::::::::::::::"
                    def score = score(rightStory, userStory)

                    if (score != -1) {
                        render(view: 'finish', model: [experiment: experiment, score: score])
                        return
                    }
                }
            }
        }

        render(status: BAD_REQUEST)

    }

    public static Float score(List<Integer> truth, List<Integer> sample) {
        log.debug("Checking truth:" + truth + " against sample:" + sample);
        Map<Integer, Integer> tmap = new HashMap<Integer, Integer>();
        int i = 0;
        for (Integer t : truth) {
            tmap.put(t, i++);
        }

        if (sample) {
            tmap.keySet().retainAll(sample);
            int last = -1;
            int accountedFor = 0;
            for (Integer s : sample) {
                if (last > -1) {
                    if (tmap.get(last) < tmap.get(s)) {
                        accountedFor++;
                    }
                }
                last = s;

            }
            return Math.round(accountedFor / (float) (truth.size() - 1));
        } else {
            return -1;
        }
    }
}
