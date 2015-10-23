package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class ExperimentService {
    def createSession(def json) {
        def session = new Session(name: 'Session_' + (Session.count() + 1))

        if (session.save(flush: true)) {
            log.debug("New session with id ${session.id} has been created.")

            if (json.training.practice != null) {
                createTraining(json.training.practice, session)
            }

            return session
        } else {
            log.error("Session creation attempt failed")
            log.error(session?.errors?.dump())
            return null
        }
    }

    def createTraining(def json, Session session) {
        def task
        Training training
        json.each { tr ->
            training = new Training(name: 'Training', session: session)
            for (int i = 0; i < tr.problem.size(); i++) {
                task = new Task(text: tr.solution.get(i), text_order: tr.problem.get(i)).save(failOnError: true)
                training.addToTask(task)
            }

            if (training.save(flush: true)) {
                log.info("New training with id ${training.id} has been created for session ${session.name}.")
                return training
            } else {
                log.error("Training creation attempt failed")
                log.error(training?.errors?.dump())
                return null;
            }

        }
    }

    def deleteExperiment(def id, def type) {
        if (id) {
            def experiment
            switch (type) {
                case ExpType.TRAINING.toString():
                    experiment = Training.get(id)
                    break;
                case ExpType.SIMULATION.toString():
                    experiment = Simulation.get(id)
                    break
                case ExpType.EXPERIMENT.toString():
                    experiment = Experiment.get(id)
                    break
            }
            if (experiment) {
                experiment.delete(flush: true)
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }
}
