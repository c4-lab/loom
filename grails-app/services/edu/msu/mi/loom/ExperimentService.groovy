package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.text.Normalizer

@Slf4j
@Transactional
class ExperimentService {
    def createSession(def json) {
        Session.withNewTransaction { status ->
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
                status.setRollbackOnly()
                log.error("Session creation attempt failed")
                log.error(session?.errors?.dump())
                return null
            }
        }
    }

    def createTraining(def json, Session session) {
        def tail
        def story
        Training training
        json.eachWithIndex { tr, idx ->
            training = new Training(name: "Training ${(idx + 1)}", session: session)
            if (training.save(flush: true)) {
                session.addToTrainings(training)
                log.debug("New training with id ${training.id} has been created for session ${session.name}.")
                story = new Story(title: "Story").save(flush: true)
                training.addToStories(story)
                for (int i = 0; i < tr.problem.size(); i++) {
                    tail = new Tail(text: tr.solution.get(i), text_order: tr.problem.get(i))
                    if (tail.save(failOnError: true)) {
                        story.addToTails(tail)
                        log.debug("New task with id ${tail.id} has been created.")
                    } else {
                        log.error("Task creation attempt failed")
                        log.error(training?.errors?.dump())
                    }
                }
            } else {
                log.error("Training creation attempt failed")
                log.error(training?.errors?.dump())
                return null;
            }
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

            for (int j = 0; j < json.sequence.size(); j++) {
                for (int k = 1; k <= json.sequence.get(j).size(); k++) {
                    for (int m = 0; m < json.sequence.get(j).getJSONArray("neighbor" + k).size(); m++) {
                        def userTask = SimulationTask.createForSimulation(Tail.findByStoryAndText_order(story, json.sequence.get(j).getJSONArray("neighbor" + k).get(m)), k, j)
                        if (userTask.save(flush: true)) {
                            log.debug("New simulationTask with id ${userTask.id} has been created for simulation ${simulation.id}.")
                        } else {
                            log.error("SimulationTask creation attempt failed")
                            log.error(userTask?.errors?.dump())
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

    def createExperiment(def json, Session session) {
        def tail
        def story
        Experiment experiment
        experiment = new Experiment(name: "Experiment", url: createExperimentUrl(session, "Experiment"), session: session,
                roundTime: json.timeperround, roundCount: json.numberofrounds, initialNbrOfTiles: json.initialnumberoftiles, userCount: 2)

        if (experiment.save(flush: true)) {
            session.addToExperiments(experiment)
            log.debug("New experiment with id ${experiment.id} has been created for session ${session.name}.")
            json.stories.each { tr ->
                story = new Story(title: tr.title).save(flush: true)
                experiment.addToStories(story)
                for (int i = 0; i < tr.data.size(); i++) {
                    tail = new Tail(text: tr.data.get(i), text_order: i)
//                    if (tail.save(failOnError: true)) {
                    story.addToTails(tail).save(flush: true)
                    log.debug("New tail with id ${tail.id} has been created.")
//                    } else {
//                        log.error("Task creation attempt failed")
//                        log.error(experiment?.errors?.dump())
//                    }
                }
            }
            return experiment
        } else {
            log.error("Experiment creation attempt failed")
            log.error(experiment?.errors?.dump())
            return null;
        }
    }

    def completeExperiment(def map, def experimentId) {
        def experiment = Experiment.get(experimentId)
        def userStory
        def story
        for (int i = 1; i <= map.size(); i++) {
            story = Story.findByExperimentAndTitle(experiment, map.get("n" + (i - 1)))
            userStory = new UserStory(alias: "neighbour" + i, story: story)
            if (userStory.save(flush: true)) {
                log.debug("New user story with id ${userStory.id} has been created.")
            }
        }

        experiment.userCount = map.size()
        experiment.enabled = true
        experiment.save(flush: true)

        return experiment
    }

    def cloneExperiment(Session session) {
        Session sessionClone = new Session()
        def count = Session.count()
        sessionClone.id = null
        sessionClone.name = "Session_" + (count + 1)
        sessionClone.dateCreated = new Date()
        session.experiments.each { experiment ->
            Experiment expClone = experiment.clone()
            expClone.id = null
            expClone.url = createExperimentUrl(sessionClone, expClone.name)
            expClone.task = null
            experiment.task.each { task ->
                Tail taskClone = task.clone()
                taskClone.id = null
                expClone.addToTask(taskClone).save(flush: true)
            }
            sessionClone.addToExperiments(expClone).save(flush: true)
        }
        session.trainings.each { training ->
            Training trainingClone = training.clone()
            trainingClone.id = null
            trainingClone.task = null
            training.task.each { task ->
                Tail taskClone = task.clone()
                taskClone.id = null
                trainingClone.addToTask(taskClone).save(flush: true)
            }
            sessionClone.addToTrainings(trainingClone).save(flush: true)
        }
        session.simulations.each { simulation ->
            Simulation simulationClone = simulation.clone()
            simulationClone.id = null
            simulationClone.task = null
            simulation.task.each { task ->
                Tail taskClone = task.clone()
                taskClone.id = null
                simulationClone.addToTask(taskClone).save(flush: true)
            }
            sessionClone.addToSimulations(simulationClone).save(flush: true)
        }


        if (sessionClone.save(flush: true)) {
            log.debug("Session clone has been created with id " + sessionClone.id)
            return sessionClone
        } else {
            log.debug("There was problem with session cloning ")
            log.error(session?.errors?.dump())
            return null
        }
    }

    def deleteExperiment(def id, def type) {
        def source
        switch (type) {
            case ExpType.TRAINING.toString():
                source = Training.get(id)
                break;
            case ExpType.SIMULATION.toString():
                source = Simulation.get(id)
                break
            case ExpType.EXPERIMENT.toString():
                source = Experiment.get(id)
                break
            case ExpType.SESSION.toString():
                source = Session.get(id)
                break
        }
        if (source) {
            source.delete(flush: true)
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

        "/" + session.name.toLowerCase() + "/" + expUrl
    }
}
