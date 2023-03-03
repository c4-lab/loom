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



    def simulation(Simulation simulation, Integer roundNumber, Integer[] tempStory) {
        def userCount = simulation.userCount
        def userList = [:]

        //TODO why is user_nbr 1?  Should be zero or should provide a unique accessor
        def privateTiles = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, 1, 0).tile

        for (int i = 1; i <= userCount; i++) {
            def tts = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, i, roundNumber).tile
            userList.put(i, [roundNbr: roundNumber, tts: tts])
        }

        def tileList = []
        if (tempStory) {
            tempStory.each {
                tileList.add(Tile.findByText_orderAndStory(it, simulation.stories.getAt(0)))
            }
        }
        return [simulation:simulation, roundNbr: roundNumber, userList: userList, tempStory: tileList, privateTiles: privateTiles]
    }



    def createSimulations(def json, TrainingSet trainingSet = null) {
        def story
        Tile tile
        json.each { sim ->
            Simulation simulation = new Simulation(name: sim.name, roundTime: sim.timeperround,
                    roundCount: sim.sequence.size(), userCount: sim.sequence.get(0).size())

            if (simulation.save(flush: true)) {


                simulation.save()
                story = new Story(title: "Story: " + sim.name).save(flush: true)


                story.save()
                simulation.addToStories(story)
                for (int i = 0; i < sim.solution.size(); i++) {
                    tile = new Tile(text: sim.solution.get(i), text_order: i)
                    story.addToTiles(tile).save(flush: true)
                    log.debug("New task with id ${tile.id} has been created.")
                }

                def userJSONArray
                for (int j = 0; j < sim.sequence.size(); j++) {
                    for (int k = 1; k <= sim.sequence.get(j).size(); k++) {
                        if (j == 0) {
                            if (k == 1) {
                                userJSONArray = sim.sequence.get(j).getJSONArray("user")
                            } else {
                                userJSONArray = sim.sequence.get(j).getJSONArray("neighbor" + (k - 1))
                            }
                        } else {
                            userJSONArray = sim.sequence.get(j).getJSONArray("neighbor" + k)
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
            if (trainingSet) {
                trainingSet.addToSimulations(simulation)

            }
        }
        if (trainingSet) {
            if (!trainingSet.save(flush: true)) {
                log.error("Error adding trainings to training set")
                log.error(trainingSet?.errors?.dump())
            }
        }
    }

    def addRoundScore(List<Integer> integers, Simulation simulation) {
        User user = springSecurityService.currentUser as User
        UserSimulationResponse usr = UserSimulationResponse.findByUserAndSimulation(user, simulation)
        if (!usr) {
            new UserSimulationResponse(simulation: simulation, user: user)
        }


        def correct = simulation.stories.first().tiles.text_order.sort()

        usr.addToScores(ExperimentService.score(correct, integers))
        usr.save(flush: true)

    }
}
