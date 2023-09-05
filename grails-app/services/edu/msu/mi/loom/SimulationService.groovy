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
    def adminService



    def simulation(Simulation simulation, Integer roundNumber, Integer[] tempStory) {
        def userCount = simulation.userCount
        def userList = [:]

        //TODO using "user number 0" is weird - should do something else
        def privateTiles = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, 0, 0).tile

        for (int i = 1; i <= userCount; i++) {
            def tts = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, i, roundNumber).tile
            userList.put(i, [roundNbr: roundNumber, tts: tts])
        }


        def tileList = []
        if (tempStory) {
            tempStory.each {
                tileList.add(Tile.get(it))
            }
        }
        return [simulation:simulation, roundNbr: roundNumber, userList: userList, tempStory: tileList, privateTiles: privateTiles]
    }



    def createSimulations(def json, TrainingSet trainingSet = null) {
        def story
        Tile tile
        json.each { sim ->

            story = adminService.createStory(sim.name,sim.solution)
            Simulation simulation = new Simulation(name: sim.name, roundTime: sim.timeperround,
                    roundCount: sim.sequence.size(), userCount: sim.sequence.get(0).size()-1, story: story)

            if (simulation.save(flush: true)) {
                sim.sequence.eachWithIndex { Map item, Integer idx ->
                    //print(item)
                    item.each { ent ->
                        Integer userNumber
                        if (ent.key == "user") {
                            userNumber = 0
                        } else {
                            def matcher = ent.key=~/(\d+)/
                            userNumber = Integer.parseInt(matcher[0][1])
                        }

                        ent.value.each {
                            def simulationTask = SimulationTask.createSimulationTask(story.tiles[it], userNumber, idx, simulation)
                            if (simulationTask.save(flush: true)) {

                                log.debug("New simulationTask with id ${simulationTask.id} has been created for simulation ${simulation.id}.")
                            } else {
                                println("Did not save ${simulationTask} due to ${simulationTask.errors}")
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

    def addRoundScore(List<Integer> integers, Simulation simulation, Integer round) {
        User user = springSecurityService.currentUser as User
        UserSimulationResponse usr = UserSimulationResponse.findByUserAndConstraintProvider(user, simulation)
        if (!usr) {
            usr = new UserSimulationResponse(constraintProvider: simulation, user: user)
        }


        def correct = simulation.story.tiles.sort {
            it.text_order
        }.collect {
            it.id
        }
        UserSimulationRoundScore score = new UserSimulationRoundScore(round: round, value: ExperimentService.score(correct, integers))
        usr.addToScores(score)
        usr.save(flush: true)

    }
}
