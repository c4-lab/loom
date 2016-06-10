package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.text.DecimalFormat

@Slf4j
@Transactional
class ExperimentService {

    static final enum Status {
        RUNNING, PAUSING
    }


    def sessionService
    def springSecurityService
    def experimentsRunning = [:]
    def waitingTimer = [:]


    def createSession(Experiment experiment, TrainingSet trainingSet, String type = "mturk") {
        Session.withNewTransaction { status ->
            def session = new Session(name: 'Session_' + (Session.count() + 1), experiment: experiment, trainingSet: trainingSet, type: type)


            if (session.save(flush: true)) {
                log.debug("New expSession with id ${session.id} has been created.")
                return session
            } else {
                status.setRollbackOnly()
                log.error("Session creation attempt failed")
                log.error(session?.errors?.dump())
                return null
            }
        }
    }

    /**
     * Create an initial experiment
     * @param json
     * @param session
     * @return
     */
    def createExperiment(def json) {
        def tail
        def story
        Experiment experiment

        Experiment.withSession { status ->
            def tr = json.stories.first()
            story = new Story(title: tr.title).save(flush: true)

            for (int i = 0; i < tr.data.size(); i++) {
                tail = new Tile(text: tr.data.get(i), text_order: i)
                story.addToTails(tail).save(flush: true)
                log.debug("New tail with id ${tail.id} has been created.")
            }
            experiment = new Experiment(name: "Experiment", story: story,
                    roundTime: json.timeperround, roundCount: json.numberofrounds, initialNbrOfTiles: json.initialnumberoftiles, userCount: 2)

            if (experiment.save(flush: true)) {

                log.debug("New experiment with id ${experiment.id} has been created.")


                experiment.save(flush: true)

                return experiment
            } else {
                log.error("Experiment creation attempt failed")
                log.error(experiment?.errors?.dump())
                return null;
            }
        }
    }

    def setExperimentNetwork(HashMap<String, List<String>> map, def experimentId) {
        def experiment = Experiment.get(experimentId)
        def idx = 0
        List<Tile> tileSrc = experiment.story.tails as List<Tile>
        def nextTile = {
            idx %= tileSrc.size()
            if (idx == 0) {
                Collections.shuffle(tileSrc)
            }
            tileSrc[idx++]
        }

        map.each { String node, List<String> data ->
            def userStory = new ExperimentInitialUserStory(experiment: experiment, alias: node)
            (1..experiment.initialNbrOfTiles).each {
                userStory.addToInitialTiles(nextTile())
            }
            if (userStory.save(flush: true)) {
                log.debug("New user story with id ${userStory.id} has been created.")
            }
            data.takeRight(data.size() - 1).each {
                def edge = new Edge(source: node, target: it, experiment: experiment).save(failOnError: true)
                log.debug("New edge with id ${edge.id} has been created.")
            }
        }

        experiment.userCount = map.size()
        experiment.enabled = true
        experiment.save(flush: true)
        return experiment
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
                deleteExperimentTasks(source?.experiments?.getAt(0))
                deleteUserStories(source?.experiments?.getAt(0))


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
        def us = ExperimentInitialUserStory.findAllByExperiment(source)
        us.each { it.delete() }
    }


    private def getUserTilesForCurrentRound(String alias, Session s) {
        int round = getExperimentStatus(s).round
        if (round) {
            log.debug("Trying to get tiles for ${alias} and session ${s} in round ${round}")
            def story = UserRoundStory.findAllByUserAliasAndSession(alias, s).max { it.round }
            if (story) {
                return story.currentTails
            }
        }
        ExperimentInitialUserStory.findByAlias(alias).initialTiles


    }


    def getUserStateModel(Session expSession) {
        //TODO handle the possibility that not all users have submitted
        def myAlias = sessionService.lookupUserAlias(expSession, springSecurityService.currentUser as User)

        int i = 1
        UserSession.findAllBySession(expSession).sort { it.userAlias }.collectEntries {
            [(myAlias == it.userAlias ? 0 : i++), getUserTilesForCurrentRound(it.userAlias, expSession)]
        }
    }


    private def scheduleWaitingCheck(Session session) {

        waitingTimer[session.id] = new Timer()
        waitingTimer[session.id].scheduleAtFixedRate({
            log.debug("Checking waiters...")
            Session s
            Session.withNewSession {
                s = Session.get(session.id)
                int count = UserSession.countBySessionAndState(s, "WAITING")
                if (count == s.experiment.userCount) {
                    log.debug("Ready to go!")
                    ((Timer) waitingTimer[s.id]).cancel()
                    waitingTimer.remove(s.id)
                    sessionService.assignAliasesAndMakeActive(s)
                    advanceRound(s)
                }
            }
        } as TimerTask, 0l, 3000l)
    }


    private def scheduleRoundFinished(Session session) {
        new Timer().schedule({
            experimentsRunning[session.id].status = Status.PAUSING
            scheduleRoundAdvance(session)
        } as TimerTask, 1000 * session.experiment.roundTime as long)
    }

    private def scheduleRoundAdvance(Session session) {
        Timer t = new Timer()
        t.schedule({
            advanceRound(session)
        } as TimerTask, 5000l)
    }


    def kickoffSession(Session session) {
        log.debug("Trying to kick off session")
        if (session.state == Session.State.PENDING) {
            log.debug("Should be kicking it off")
            scheduleWaitingCheck(session)
        } else {
            log.debug("${session.state}")
        }
    }

    /**
     * Begins the process of a
     * @param session
     * @return
     */
    def advanceRound(Session session) {
        log.debug("Advance round for ${session.id}")
        Experiment experiment = session.experiment
        int nextRound = experimentsRunning.containsKey(session.id) ? experimentsRunning[session.id].round + 1 : 0

        if (nextRound < experiment.roundCount) {
            experimentsRunning[session.id] = [round: nextRound, start: System.currentTimeMillis(), status: Status.RUNNING]
            scheduleRoundFinished(session)
        } else {
            experimentsRunning.remove(session.id)
            Session.withSession {
                def s = Session.get(session.id)
                s.state = Session.State.FINISHED
                s.save(flush: true)
            }
        }


    }

    def getExperimentStatus(Session session) {
        log.debug(experimentsRunning as String)

        experimentsRunning[session.id]
    }

    public static Float scoreOld(List truth, List sample) {
        log.debug("Checking truth:" + truth + " against sample:" + sample);
        Map<Object, Integer> tmap = new HashMap<Object, Integer>();
        int i = 0;
        for (def t : truth) {
            tmap.put(t, i++);
        }

        println tmap


        if (sample) {
            tmap.keySet().retainAll(sample);
            def last = -1;
            int accountedFor = 0;
            for (def s : sample) {
                if (last > -1) {
                    log.debug("Checking ${s}: ${tmap.get(last)} < ${tmap.get(s)}}")
                    if (tmap.get(last) < tmap.get(s)) {
                        accountedFor++;
                        println "$accountedFor"
                    }
                }
                last = s;

            }

            DecimalFormat df = new DecimalFormat("####0.00");
            return Float.parseFloat(df.format(accountedFor / (float) (truth.size() - 1)));
        } else {
            return 0.0;
        }
    }

    public static Float score(List truth, List sample) {
        log.debug("Checking truth:" + truth + " against sample:" + sample);
        if (sample && sample.size()>1) {
            def c2 = { (it.size() * (it.size() - 1)) / 2f }
            def tmap = [:]
            truth.eachWithIndex { Object entry, int i -> tmap[entry] = i }
            println tmap
            int bad = 0
            for (int i in (0..<sample.size())) {
                for (int j in ((1 + i)..<sample.size())) {
                    if (tmap[sample[i]] > tmap[sample[j]]) bad++
                }
            }
            def result =  (1 - (bad / c2(sample))) * (sample.size() / truth.size())
            DecimalFormat df = new DecimalFormat("####0.00");
            return Float.parseFloat(df.format(result))
        } else {
            0.0f
        }

    }

}
