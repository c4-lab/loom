package edu.msu.mi.loom

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.text.DecimalFormat
import java.time.LocalDateTime

@Slf4j
@Transactional
class SimulationService {
    def springSecurityService
    def statService
    def experimentService
    def mturkService


    def simulation(TrainingSet ts, def roundNumber, def tempStory) {
        log.debug("Now in simulation with $tempStory")
        //TODO handle multiple simulations
        def simulation = ts.simulations.getAt(0)
        def userCount = simulation.userCount
        def roundTime = simulation.roundTime
        LocalDateTime endDate

        def userList = [:]
        if (roundNumber) {
            roundNumber = Integer.parseInt(roundNumber)
            if (roundNumber < simulation.roundCount) {
                for (int i = 1; i <= userCount; i++) {
                    def tts = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, i, roundNumber).tail
                    userList.put(i, [roundNbr: roundNumber, tts: tts])
                }

                def tailList = []
                if (tempStory) {
                    tempStory.each {
                        tailList.add(Tile.findByText_orderAndStory(it, simulation.stories.getAt(0)))
                    }
                }


                return [roundNbr: roundNumber, trainingSet:ts.id, simulation: simulation, userList: userList, tempStory: tailList]
            } else {
//                Story story = Story.findBySimulation(simulation)
//                def rightStory = Tile.findAllByStory(story)
//                def rightTextOrder = rightStory.text_order
//                def intList = []
//                for (String s : tempStory)
//                    intList.add(Integer.valueOf(s));
//                float simulationScore = simulationScore(rightTextOrder, intList)
                //TODO FIXME
                //statService.setSimulationScore(session, simulationScore, Room.findBySession(session))
                return [experiment: 'experiment_ready'] as JSON
            }
        } else {
            roundNumber = 0
            for (int i = 1; i <= userCount; i++) {
                def tts = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, i, roundNumber).tail
                userList.put(i, [roundNbr: roundNumber, tts: tts])
            }

            return [roundNbr: roundNumber, simulation: simulation, userList: userList]
        }
    }

    public static Float simulationScore(List<Integer> truth, List<Integer> sample) {
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

    def createSimulation(def json, TrainingSet trainingSet) {
        def story
        Tile tail
        Simulation simulation = new Simulation(name: 'Simulation', roundTime: json.timeperround,
                roundCount: json.sequence.size(), userCount: json.sequence.get(0).size(), trainingSet: trainingSet)

        if (simulation.save(flush: true)) {
            mturkService.createQualification(simulation,"loom simulation")
            trainingSet.addToSimulations(simulation)
            log.debug("New simulation with id ${simulation.id} has been created for session ${trainingSet.name}.")
            def storyId = Story.count() + 1
            story = new Story(title: "Story "+storyId.toString()).save(flush: true)
            mturkService.createQualification(story, "loom story")
            simulation.addToStories(story)
            for (int i = 0; i < json.solution.size(); i++) {
                tail = new Tile(text: json.solution.get(i), text_order: i)
                story.addToTails(tail).save(flush: true)
                log.debug("New task with id ${tail.id} has been created.")
            }

            def userJSONArray
            for (int j = 0; j < json.sequence.size(); j++) {
                for (int k = 1; k <= json.sequence.get(j).size(); k++) {
                    if (j == 0) {
                        if (k == 1) {
                            userJSONArray = json.sequence.get(j).getJSONArray("user")
                        } else {
                            userJSONArray = json.sequence.get(j).getJSONArray("neighbor" + (k - 1))
                        }
                    } else {
                        userJSONArray = json.sequence.get(j).getJSONArray("neighbor" + k)
                    }

                    for (int m = 0; m < userJSONArray.size(); m++) {
                        def simulationTask = SimulationTask.createSimulationTask(Tile.findByStoryAndText_order(story, userJSONArray.get(m)), j == 0 ? k : (k + 1), j, simulation)
                        if (simulationTask.save(flush: true)) {
                            log.debug("New simulationTask with id ${simulationTask.id} has been created for simulation ${simulation.id}.")
                        } else {
                            log.error("SimulationTask creation attempt failed")
                            log.error(simulationTask?.errors?.dump())
                        }
                    }
                }
            }
        } else {
            log.error("Simulation creation attempt failed")
            log.error(simulation?.errors?.dump())
            return null;
        }
    }

    def addRoundScore(List<Integer> integers, Simulation simulation) {
        UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(springSecurityService.currentUser,simulation.trainingSet)
        def correct = simulation.stories.first().tails.text_order.sort()

        SimulationScore ss = (uts.simulationsCompleted)?uts.simulationsCompleted.first():null
        if (!ss) {
            ss = new SimulationScore(simulation:simulation)
            uts.addToSimulationsCompleted(ss)
            uts.save(flush:true)
        }

        ss.addToScores(ExperimentService.score(correct,integers))
        ss.save(flush:true)

        if (ss.scores.size() == simulation.roundCount) {
            uts.simulationScore = ss.scores.sum()/ss.scores.size()
            uts.save()
        }

    }
}
