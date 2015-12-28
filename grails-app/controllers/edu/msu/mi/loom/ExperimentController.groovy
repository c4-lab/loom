package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

import java.text.DecimalFormat

import static org.springframework.http.HttpStatus.*

@Slf4j
@Secured("ROLE_USER")
class ExperimentController {
    static allowedMethods = [
            submitTraining: 'POST'
    ]

    def experimentService
    def springSecurityService
    def simulationService

    def submitTraining() {
        def userTails = params.tails
        List<String> tailsList = Arrays.asList(userTails.split(";"));

        def trainingId = params.training

        if (trainingId) {
            def training = Training.findById(trainingId)
            String trainingName = training.name
            def seqNumber = trainingName.split("[^0-9]+")[1]

            def story = Story.findByTraining(training)
            def tails = Tail.executeQuery(("from Tail t where t.story=? order by t.text_order asc"), [story])
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
        def sessionId = params.session
        def roundNumber = params.roundNumber
        def tempStory = params.tempStory
        if (sessionId) {
            def session = Session.get(Long.parseLong(sessionId))
            if (session) {
                def model = simulationService.simulation(session, roundNumber, tempStory)
                if (model instanceof JSON) {
                    return render(status: OK, text: model)
                } else if (model.tempStory) {
                    return render(template: '/home/simulation_content', model: model)
                } else {
                    return render(view: '/home/simulation', model: model)
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

            redirect(action: 'simulation', params: [session: simulation?.session?.id, roundNumber: roundNumber, tempStory: tempSimulation?.currentTails])
            return
        }

        render(status: BAD_REQUEST)
    }

    def experiment() {
        def sessionId = params.session
        def tempStory = params.tempStory
        def roundNumber
        if (params.roundNumber) {
            roundNumber = Integer.parseInt(params.roundNumber)
        }
        if (sessionId) {
            def user = springSecurityService.currentUser as User
            user.isReady = true
            user.save(flush: true)
            Session session = Session.get(Long.parseLong(sessionId))
            def room = Room.findBySession(session)
            if (User.countByRoomAndIsReady(room, true) == session.experiments.getAt(0).userCount) {
                def model = experimentService.experiment(session, roundNumber, tempStory)
                if (model instanceof JSON) {
                    return render(status: OK, text: model)
                } else if (model.tempStory) {
                    return render(template: '/home/experiment_content', model: model)
                } else {
                    return render(view: '/home/experiment', model: model)
                }
            } else {
                return render(view: 'waiting_room', model: [room: room])
            }
        }

        render(status: BAD_REQUEST)
    }

    def checkExperimentReadyState() {
        def sessionId = params.session
        if (sessionId) {
            def session = Session.get(sessionId)
            def room = Room.findBySession(session)
            if (User.countByRoomAndIsReady(room, true) == session.experiments.getAt(0).userCount) {
                return render(status: OK)
            } else {
                return render(status: NOT_FOUND)
            }
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

            redirect(action: 'experiment', params: [session: experiment?.session?.id, roundNumber: roundNumber, tempStory: tempExperiment?.currentTails])
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
                    def rightStory = Tail.findAllByStory(story)
                    def rightTextOrder = rightStory.text_order
                    def userStory = flash."${user.alias}-${experiment.id}"

//                    println "-----right story--------"
//                    println rightTextOrder
//                    println "::::::::::::::::::::::::::::::::::"
//                    println "-----user story--------"
//                    println userStory
//                    println "::::::::::::::::::::::::::::::::::"
                    def score = score(rightTextOrder, userStory)

                    def ets = ExperimentTask.findAllByExperiment(experiment)
                    ets.each { it.delete() }
                    if (score != -1) {
                        render(view: 'finish', model: [rightStory: rightStory, experiment: experiment, score: score])
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

            DecimalFormat df = new DecimalFormat("####0.00");
            return Float.parseFloat(df.format(accountedFor / (float) (truth.size() - 1)));
        } else {
            return -1;
        }
    }
}
