package edu.msu.mi.loom

import grails.converters.JSON
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsHttpSession
import org.codehaus.groovy.grails.web.util.WebUtils

import java.text.DecimalFormat
import java.text.Normalizer

@Slf4j
@Transactional
class ExperimentService {
    def simulationService
    def springSecurityService

    def createSession(def json) {
        Session.withNewTransaction { status ->
            def session = new Session(name: 'Session_' + (Session.count() + 1))

            if (session.save(flush: true)) {
                log.debug("New expSession with id ${session.id} has been created.")

//            Training creation
                if (json.training.practice != null) {
                    createTraining(json.training.practice, session)
                }

//            Simulation creation
                if (json.training.simulation != null) {
                    simulationService.createSimulation(json.training.simulation, session)
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
                log.debug("New training with id ${training.id} has been created for expSession ${session.name}.")
                story = new Story(title: "Story").save(flush: true)
                training.addToStories(story)
                for (int i = 0; i < tr.problem.size(); i++) {
                    tail = new Tail(text: tr.solution.get(i), text_order: i)
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


    def createExperiment(def json, Session session) {
        def tail
        def story
        Experiment experiment
        experiment = new Experiment(name: "Experiment", session: session,
                roundTime: json.timeperround, roundCount: json.numberofrounds, initialNbrOfTiles: json.initialnumberoftiles, userCount: 2)

        if (experiment.save(flush: true)) {
            session.addToExperiments(experiment)
            log.debug("New experiment with id ${experiment.id} has been created for expSession ${session.name}.")
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

    def completeExperiment(HashMap<String, List<String>> map, def experimentId) {
        def experiment = Experiment.get(experimentId)
        def userStory
        def story
        def edge
        for (int i = 1; i <= map.size(); i++) {
            story = Story.findByExperimentAndTitle(experiment, map.get("n" + (i - 1)).get(0))
            userStory = new UserStory(experiment: experiment, alias: i, story: story)
            map.get("n" + (i - 1)).eachWithIndex { it, idx ->
                if (idx != 0) {
                    edge = new Edge(source: "n" + (i - 1), target: it, experiment: experiment).save(failOnError: true)
                    log.debug("New edge with id ${edge.id} has been created.")
                }
            }

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
        for (int userNbr = 1; userNbr <= experiment.userCount; userNbr++) {
            def story = UserStory.findByAliasAndExperiment(userNbr, experiment)?.story
            def text_order = Tail.findAllByStory(story).text_order
            Collections.shuffle(text_order)
            int item = 0
            for (int roundNbr = 0; roundNbr < experiment.roundCount; roundNbr++) {
                for (int numberOfTail = 0; numberOfTail < experiment.initialNbrOfTiles; numberOfTail++) {
                    def experimentTask = ExperimentTask.createForExperiment(Tail.findByStoryAndText_order(story, text_order.get(item)), userNbr, roundNbr, experiment)
                    if (++item >= text_order.size()) {
                        Collections.shuffle(text_order)
                        item = 0
                    }
                    if (experimentTask.save(flush: true)) {
                        log.debug("New experimentTask with id ${experimentTask.id} has been created for experiment ${experiment.id}.")
                    } else {
                        log.error("ExperimentTask creation attempt failed")
                        log.error(experimentTask?.errors?.dump())
                    }
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
            log.debug("There was problem with expSession cloning ")
            log.error(session?.errors?.dump())
            return null
        }
    }

    def deleteExperiment(def id, def type) {
        def source, ets
        switch (type) {
            case ExpType.TRAINING.toString():
                source = Training.get(id)
                deleteTrainingTasks(source)
                break;
            case ExpType.SIMULATION.toString():
                source = Simulation.get(id)
                deleteSimulationTasks(source)
                break
            case ExpType.EXPERIMENT.toString():
                source = Experiment.get(id)
                deleteExperimentTasks(source)
                deleteUserStories(source)
                break
            case ExpType.SESSION.toString():
                source = Session.get(id)
                source?.trainings?.each { training ->
                    deleteTrainingTasks(training)
                }
                deleteSimulationTasks(source?.simulations?.getAt(0))
                deleteExperimentTasks(source?.experiments?.getAt(0))
                deleteUserStories(source?.experiments?.getAt(0))

                deleteRooms(source)
                break
        }
        if (source) {
            source.delete(flush: true)
            log.info("Session with id ${id} has been deleted.")
            return true
        } else {
            return false
        }
    }

    private def deleteRooms(def source) {
        def rooms = Room.findAllBySession(source)
        rooms.each { room -> room.delete() }
    }

    private def deleteTrainingTasks(source) {
        def tts = TrainingTask.findAllByTraining(source)
        tts.each { tt ->
            tt.delete()
        }
    }

    private def deleteSimulationTasks(source) {
        def sts = SimulationTask.findAllBySimulation(source)
        sts.each { st ->
            st.delete()
        }
    }

    private def deleteExperimentTasks(source) {
        def ets = ExperimentTask.findAllByExperiment(source)
        ets.each { et ->
            et.delete()
        }
    }

    private def deleteUserStories(source) {
        def us = UserStory.findAllByExperiment(source)
        us.each { it.delete() }
    }

    def startExperiment(Room room) {
        def session = room.session
        def experiment = session.experiments.getAt(0)
        def stories = experiment.stories
        def userRooms = UserRoom.findAllByRoom(room)
        def roundCount = experiment.roundCount
        def nbrTiles = experiment.initialNbrOfTiles

        for (UserRoom userRoom : userRooms) {
            def story = UserStory.findByAliasAndStoryInList(userRoom.userAlias, stories as List).story
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

    def experiment(Session expSession, def roundNumber, def tempStory) {
        def experiment = expSession.experiments.getAt(0)
        def userList = [:]
        def currentUser = springSecurityService.currentUser as User
        def userRoom = UserRoom.findByUserAndRoom(currentUser, Room.findBySession(expSession))
        def alias = userRoom.userAlias
        def story = UserStory.findByExperimentAndAlias(experiment, alias)?.story
        def rightStory = Tail.findAllByStory(story)
        def rightTextOrder = rightStory.text_order
        def user = springSecurityService.currentUser as User
        def userStats = UserStatistic.findBySessionAndUserAndRoom(expSession, user, Room.findBySession(expSession))
        if (roundNumber) {
            List<Tail> tailList = []
            if (tempStory) {
                tempStory.each {
                    tailList.add(Tail.findById(it))
                }
            }

            if (roundNumber < experiment.roundCount) {
                def targets = Edge.findAllBySourceAndExperiment("n" + (alias - 1), experiment).target
                targets.eachWithIndex { String target, int index ->
                    def tts = ExperimentTask.findAllByExperimentAndUser_nbrAndRound_nbr(experiment, (Integer.parseInt(target.split("[^0-9]+")[1]) + 1), roundNumber).tail
                    userList.put((index + 1), [roundNbr: roundNumber, tts: tts])
                }

                def tts = ExperimentTask.findAllByExperimentAndUser_nbrAndRound_nbr(experiment, alias, roundNumber).tail
                userList.put(0, [roundNbr: roundNumber, tts: tts])
                def score = score(rightTextOrder, tailList.text_order)
                userStats.experimentRoundScore.add(score)
                userStats.save(flush: true)
                println "--------------------"
                println score
                println "--------------------"

                return [roundNbr: roundNumber, experiment: experiment, userList: userList, tempStory: tailList]
            } else {
                def flash = WebUtils.retrieveGrailsWebRequest().flashScope
                flash."${alias}-${experiment.id}" = tailList.text_order
                userStats.textOrder = tailList.text_order
//TODO: Deadlock found when trying to get lock; try restarting transaction. Stacktrace follows:
                userStats.save(flush: true)
                return [experiment: 'finishExperiment', sesId: expSession.id] as JSON
            }
        } else {
            roundNumber = 0
            def targets = Edge.findAllBySourceAndExperiment("n" + (alias - 1), experiment).target
            targets.eachWithIndex { String target, int index ->
                def tts = ExperimentTask.findAllByExperimentAndUser_nbrAndRound_nbr(experiment, (Integer.parseInt(target.split("[^0-9]+")[1]) + 1), roundNumber).tail
                userList.put((index + 1), [roundNbr: roundNumber, tts: tts])
            }

            def tts = ExperimentTask.findAllByExperimentAndUser_nbrAndRound_nbr(experiment, alias, roundNumber).tail
            userList.put(0, [roundNbr: roundNumber, tts: tts])
            return [roundNbr: roundNumber, experiment: experiment, userList: userList]
        }
    }

    private GrailsHttpSession getCurrentSession() {
        def webUtils = WebUtils.retrieveGrailsWebRequest()
        return webUtils.getSession()
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

    private List<Tail> shuffleTails(Story story) {
        def tails = Tail.findAllByStory(story)

        if (tails) {
            Collections.shuffle(tails)
        }

        return tails
    }
}
