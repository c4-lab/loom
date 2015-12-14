package edu.msu.mi.loom

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.time.LocalDateTime
import java.time.ZoneOffset

@Slf4j
@Transactional
class SimulationService {
    def springSecurityService

    def simulation(Session session, def roundNumber, def tempStory) {
        def simulation = session.simulations.getAt(0)
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
                        tailList.add(Tail.findById(it))
                    }
                }
                endDate = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(roundTime)

                println "+++++++++++++++++++++++++++"
                println endDate
                println "+++++++++++++++++++++++++++"
                return [roundNbr: roundNumber, simulation: simulation, userList: userList, tempStory: tailList]
            } else {
                def user = springSecurityService.currentUser as User
                user.isReady = true
                user.save(flush: true)

                return [experiment: 'experiment', sesId: session.id] as JSON
            }
        } else {
            roundNumber = 0
            for (int i = 1; i <= userCount; i++) {
                def tts = SimulationTask.findAllBySimulationAndUser_nbrAndRound_nbr(simulation, i, roundNumber).tail
                userList.put(i, [roundNbr: roundNumber, tts: tts])
            }
            endDate = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(roundTime)
            println "+++++++++++++++++++++++++++"
            println endDate
            println "+++++++++++++++++++++++++++"
            return [roundNbr: roundNumber, simulation: simulation, userList: userList]
        }
    }

    def createSimulation(def json, Session session) {
        def story
        Tail tail
        Simulation simulation = new Simulation(name: 'Simulation', roundTime: json.timeperround,
                roundCount: json.sequence.size(), userCount: json.sequence.get(0).size(), session: session)

        if (simulation.save(flush: true)) {
            session.addToSimulations(simulation)
            log.debug("New simulation with id ${simulation.id} has been created for session ${session.name}.")
            story = new Story(title: "Story").save(flush: true)
            simulation.addToStories(story)
            for (int i = 0; i < json.solution.size(); i++) {
                tail = new Tail(text: json.solution.get(i), text_order: i)
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
                        def simulationTask = SimulationTask.createSimulationTask(Tail.findByStoryAndText_order(story, userJSONArray.get(m)), j == 0 ? k : (k + 1), j, simulation)
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
}
