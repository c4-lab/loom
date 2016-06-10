package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class TrainingSetService {

    def simulationService
    def mturkService

    def createTrainingSet(def json, def name) {
        Session.withNewTransaction { status ->

            def trainingSet = new TrainingSet(name: name)

            if (trainingSet.save(flush: true)) {
                log.debug("New trainingSet with id ${trainingSet.id} has been created.")

//            Training creation
                if (json.training.practice != null) {
                    createTraining(json.training.practice, trainingSet)
                }

//            Simulation creation
                if (json.training.simulation != null) {
                    simulationService.createSimulation(json.training.simulation, trainingSet)
                }

                mturkService.createQualification(trainingSet)

//
                return trainingSet
            } else {
                status.setRollbackOnly()
                log.error("Session creation attempt failed")
                log.error(trainingSet?.errors?.dump())
                return null
            }
        }
    }

    def createTraining(def json, TrainingSet trainingSet) {
        def tail
        def story
        Training training
        json.eachWithIndex { tr, idx ->
            training = new Training(name: "Training ${(idx + 1)}", trainingSet: trainingSet)
            //if (training.save(flush: true)) {
            trainingSet.addToTrainings(training)
            log.debug("New training with id ${training.id} has been created for trainingSet ${trainingSet.name}.")
            story = new Story(title: "Story").save(flush: true)
            training.addToStories(story)
            for (int i = 0; i < tr.problem.size(); i++) {
                tail = new Tile(text: tr.solution.get(i), text_order: i)
                if (tail.save(flush: true)) {
                    story.addToTails(tail).save(flush: true)
                    log.debug("New task with id ${tail.id} has been created.")
                } else {
                    log.error("Task creation attempt failed")
                    log.error(training?.errors?.dump())
                }
            }

            def tails = Tile.findAllByStory(story)
            for (int i = 0; i < tr.problem.size(); i++) {
                new TrainingTask(training: training, tail: tails.get(tr.problem.get(i))).save(flush: true)
            }
            if (!trainingSet.save(flush: true)) {
                log.error("Training creation attempt failed")
                log.error(trainingSet?.errors?.dump())
                return null;
            }
        }
    }

    Training getNextTraining(User u, TrainingSet ts) {
        Training training
        log.debug("Trainings are $ts.trainings")
        def completed = UserTrainingSet.findByUserAndTrainingSet(u,ts)?.trainingsCompleted?:[]
        log.debug("Completed are $completed")
        def trainingLst = ts.trainings - completed
        if (trainingLst) {
            training = trainingLst.getAt(0)
        }
        return training
    }

    def changeTrainingState(User u, Training training, Simulation simulation) {

        TrainingSet ts = null
        if (training) {
            ts = training.trainingSet
        } else if (simulation) {
            ts = simulation.trainingSet
        } else {
            log.error("Cannot advance training state without a either a simulation or training")
        }

        UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(u, ts)
        if (!uts) {
            uts = new UserTrainingSet(user: u, trainingSet: ts, trainingStartTime: new Date())
            uts.save(flush: true)
        }
        if (training && (!uts.trainingsCompleted || !uts.trainingsCompleted.contains(training))) {
            uts.addToTrainingsCompleted(training)
        }
        boolean simsCompleted = uts.simulationsCompleted && (uts.simulationsCompleted.first().scores.size() == ts.simulations.first().roundCount)


        def trainings = ts.trainings - uts.trainingsCompleted?:[]

        if (simsCompleted && trainings.isEmpty()) {
            completeTraining(uts)

        }

        uts.save(flush: true)
    }

    def completeTraining(UserTrainingSet uts) {
        uts.complete = true
        uts.trainingEndTime = new Date()
        mturkService.assignQualification(uts.user.username,TrainingSet.constructQualificationString(uts.trainingSet))

    }


}


