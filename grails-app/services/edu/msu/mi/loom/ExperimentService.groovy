package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.text.Normalizer

@Slf4j
@Transactional
class ExperimentService {
    def createSession(def json) {
        def session = new Session(name: 'Session_' + (Session.count() + 1))

        if (session.save(flush: true)) {
            log.debug("New session with id ${session.id} has been created.")

//            Training creation
            if (json.training.practice != null) {
                createTraining(json.training.practice, session)
            }

//            Simulation creation
            if (json.training.simulation != null) {
                createSimulation(json.training.simulation, session)
            }

//            Experiment creation
            if (json.experiment != null) {
                createExperiment(json.experiment, session)
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
                task = new Task(text: tr.solution.get(i), text_order: tr.problem.get(i))
                if (task.save(failOnError: true)) {
                    training.addToTask(task)
                    log.debug("New task with id ${task.id} has been created.")
                } else {
                    log.error("Task creation attempt failed")
                    log.error(training?.errors?.dump())
                }
            }

            if (training.save(flush: true)) {
                log.debug("New training with id ${training.id} has been created for session ${session.name}.")
                return training
            } else {
                log.error("Training creation attempt failed")
                log.error(training?.errors?.dump())
                return null;
            }
        }
    }

    def createSimulation(def json, Session session) {
        Task task
        Simulation simulation = new Simulation(name: 'Simulation', roundTime: json.timeperround, roundCount: json.sequence.size(), userCount: json.sequence.get(0).size(), session: session)

        for (int i = 0; i < json.solution.size(); i++) {
            task = new Task(text: json.solution.get(i), text_order: i)
            if (task.save(failOnError: true)) {
                simulation.addToTask(task)
                log.debug("New task with id ${task.id} has been created.")
            } else {
                log.error("Task creation attempt failed")
                log.error(task?.errors?.dump())
            }
        }
        simulation.save(flush: true)

        for (int j = 0; j < json.sequence.size(); j++) {
            for (int k = 1; k <= json.sequence.get(j).size(); k++) {
                for (int m = 0; m < json.sequence.get(j).getJSONArray("neighbor" + k).size(); m++) {
                    def userTask = UserTask.createForSimulation(Task.findBySimulationAndText_order(simulation, json.sequence.get(j).getJSONArray("neighbor" + k).get(m)), k, j)
                    if (userTask.save(flush: true)) {
                        log.debug("New userTask with id ${userTask.id} has been created for simulation ${simulation.id}.")
                    } else {
                        log.error("UserTask creation attempt failed")
                        log.error(userTask?.errors?.dump())
                    }
                }
            }
        }
    }

    def createExperiment(def json, Session session) {
        def task
        Experiment experiment
        json.stories.each { tr ->
            experiment = new Experiment(name: tr.title, url: createExperimentUrl(session, tr.title), session: session, roundTime: json.timeperround, roundCount: json.numberofrounds, userCount: json.initialnumberoftiles)
            for (int i = 0; i < tr.data.size(); i++) {
                task = new Task(text: tr.data.get(i), text_order: i)
                if (task.save(failOnError: true)) {
                    experiment.addToTask(task)
                    log.debug("New task with id ${task.id} has been created.")
                } else {
                    log.error("Task creation attempt failed")
                    log.error(experiment?.errors?.dump())
                }
            }

            if (experiment.save(flush: true)) {
                log.debug("New experiment with id ${experiment.id} has been created for session ${session.name}.")
                return experiment
            } else {
                log.error("Experiment creation attempt failed")
                log.error(experiment?.errors?.dump())
                return null;
            }
        }
    }

    def deleteExperiment(def id, def type) {
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
            log.info("Experiment with id ${id} has been deleted.")
            return true
        } else {
            return false
        }
    }

    private static String createExperimentUrl(Session session, String title) {
        def expUrl = Normalizer.normalize(title?.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-")
                .replace("--", "-").replace("--", "-")
                .replaceAll('[^a-z0-9]+$', "")
                .replaceAll("^[^a-z0-9]+", "")

        log.info("Generated url: " + "/" + session.name + "/" + expUrl)

        "/" + session.name + "/" + expUrl
    }
}
