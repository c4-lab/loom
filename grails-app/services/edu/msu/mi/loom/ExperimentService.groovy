package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.text.Normalizer

@Slf4j
@Transactional
class ExperimentService {
    def createSession(def json) {
        Session.withNewTransaction { status ->
            def session = new Session(name: 'Session_' + (Session.count() + 1), url: createExperimentUrl('Session_' + (Session.count() + 1)))

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
                    tail = new Tail(text: tr.solution.get(i))
                    if (tail.save(flush: true)) {
                        story.addToTails(tail).save(flush: true)
                        log.debug("New task with id ${tail.id} has been created.")
                    } else {
                        log.error("Task creation attempt failed")
                        log.error(training?.errors?.dump())
                    }
                }

                def tails = Tail.findAllByStory(story)
                for (int i = 0; i < tr.problem.size(); i++) {
                    new TrainingTask(training: training, tail: tails.get(tr.problem.get(i))).save(flush: true)
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
                        def userTask = SimulationTask.createForSimulation(Tail.findByStoryAndText_order(story, json.sequence.get(j).getJSONArray("neighbor" + k).get(m)), k, j, simulation)
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
        experiment = new Experiment(name: "Experiment", session: session,
                roundTime: json.timeperround, roundCount: json.numberofrounds, initialNbrOfTiles: json.initialnumberoftiles, userCount: 2)

        if (experiment.save(flush: true)) {
            session.addToExperiments(experiment)
            log.debug("New experiment with id ${experiment.id} has been created for session ${session.name}.")
            json.stories.each { tr ->
                story = new Story(title: tr.title).save(flush: true)
                experiment.addToStories(story)
                for (int i = 0; i < tr.data.size(); i++) {
                    tail = new Tail(text: tr.data.get(i), text_order: i)
                    story.addToTails(tail).save(flush: true)
                    log.debug("New tail with id ${tail.id} has been created.")
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
        if (experiment.save(flush: true)) {
            shuffleTails(experiment)
        }

        return experiment
    }

    private def shuffleTails(Experiment experiment) {
        for (int roundNbr = 0; roundNbr < experiment.roundCount; roundNbr++) {
            for (int userNbr = 1; userNbr <= experiment.userCount; userNbr++) {
                def experimentTask = ExperimentTask.createForExperiment(Tail.findByStoryAndText_order(story, json.sequence.get(j).getJSONArray("neighbor" + userNbr).get(m)),
                        userNbr, roundNbr, experiment)
                if (experimentTask.save(flush: true)) {
                    log.debug("New experimentTask with id ${experimentTask.id} has been created for experiment ${experiment.id}.")
                } else {
                    log.error("ExperimentTask creation attempt failed")
                    log.error(experimentTask?.errors?.dump())
                }
            }
        }
    }

    def cloneExperiment(Session session) {
        Session sessionClone = session.clone()
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

    private static String createExperimentUrl(String sessionName) {
        def expUrl = Normalizer.normalize(sessionName?.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-")
                .replace("--", "-").replace("--", "-")
                .replaceAll('[^a-z0-9]+$', "")
                .replaceAll("^[^a-z0-9]+", "")

        log.info("Generated url: " + "/" + expUrl)

        "/" + expUrl
    }

    def startExperiment(Room room) {
        def session = room.session
        def experiment = session.experiments.getAt(0)
        def stories = experiment.stories
        def users = room.users
        def roundCount = experiment.roundCount
        def nbrTiles = experiment.initialNbrOfTiles

        for (User user : users) {
            def story = UserStory.findByAliasAndStoryInList(user.alias, stories as List).story
            def tails = shuffleTails(story)
            Round.withNewTransaction { status ->
                try {
                    if (tails.size() > nbrTiles) {
                        for (int i = 1; i <= roundCount; i++) {
                            def tailsList = []
                            for (int j = 0; j < nbrTiles; j++) {
                                tailsList.add(tails.get(j).id)
                            }
                            def round = new Round(roundNbr: i, user: user, story: story, tails: tailsList)
                            if (round.save(flush: true)) {
                                log.debug("New round has been created with id " + round.id)
                            } else {
                                log.debug("There was problem with round creation.")
                                log.error(round?.errors?.dump())
                                return null
                            }
                        }
                    }
                } catch (Exception exp) {
                    status.setRollbackOnly()
                }
            }
        }
    }

    Training getNextTraining(Session session, int number = -1) {
        Training training
        def trainingLst = Training.findAllBySession(session)
        if (number == -1) {
            training = trainingLst.getAt(0)
        } else if (session.trainings.size() >= number) {
            training = trainingLst.getAt(number)
        }

        return training
    }

    private List<Tail> shuffleTails(Story story) {
        def tails = Tail.findAllByStory(story)

        if (tails) {
            Collections.shuffle(tails)
        }

        return tails
    }
}
