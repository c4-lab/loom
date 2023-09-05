package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.text.DecimalFormat

@Slf4j
@Transactional
class ExperimentService {


    def sessionService
    def springSecurityService
    def networkGenerateService
    Map<Object,ExperimentRoundStatus> experimentsRunning = [:]
    def waitingTimer = [:]




    def getUserTilesForCurrentRound(String alias, Session s) {
        if (!alias) {
            return []
        }
        def story = UserRoundStory.findAllByUserAliasAndSession(alias, s).max { it?.round }
        if (story) {
            //println("${alias} current tiles are ${story.currentTiles}")
            return story.currentTiles
        } else {
            return []
        }
        //TODO Prior logic appears to reach out beyond the current user if that user has not submitted, gathering tiles
        // from the user's neighbor's neighbor.  This is worth revisiting.
    }


    def getMyStoryState(Session expSession) {
        def myAlias = sessionService.lookupUserAlias(expSession, springSecurityService.currentUser as User)
        getUserTilesForCurrentRound(myAlias, expSession)
    }

    /**
     * Returns the user's private list of tiles
     *
     * @param expSession
     * @return
     */
    def getMyPrivateState(Session expSession) {
        def myAlias = sessionService.lookupUserAlias(expSession, springSecurityService.currentUser as User)
        Experiment e = expSession.exp
        SessionInitialUserStory.findByAliasAndSession(myAlias,expSession).initialTiles
    }

    /**
     * Walks through the edges that contain my alias and returns the state of all linked neighbors
     * @param expSession
     * @return
     */
    def getNeighborsState(Session expSession) {
        def myAlias = sessionService.lookupUserAlias(expSession, springSecurityService.currentUser as User)
        Set<String> aliases = Edge.findAllBySession(expSession).findAll {it.ends.contains(myAlias)}.collect {
            it.alter(myAlias)
        } as Set
        int i = 1
        aliases.sort().collectEntries {
            [(i++), getUserTilesForCurrentRound(it,expSession)]
        }
    }



    /**
     * Sets up a periodic check to see if the session has enough users to start.  Accommodates both single and mixed sessions.
     * @param session
     * @return
     */
    private def scheduleWaitingCheck(Session session) {

        waitingTimer[session.id] = new Timer()
        ((Timer)waitingTimer[session.id]).scheduleAtFixedRate({
            log.debug("Checking waiters...")
            Session s
            Session.withNewSession {
                s = Session.get(session.id)
                int count = countWaitingUsers(s)
                if (count >= s.sessionParameters.safeGetMinNode()) {
                    ((Timer) waitingTimer[s.id]).cancel()
                    waitingTimer.remove(s.id)
                    assignAliasesAndInitialTiles(s, (int) s.sessionParameters.safeGetMinNode())
                    advanceRound(s)
                }
            }
        } as TimerTask, 0l, 1000l)
    }

    def assignInitialTiles(List<UserSession> active) {
        if (active.isEmpty()) {
            // Handle the empty case
            return
        }

        Session session = active[0].session
        List<Tile> tileSource = new ArrayList<>(session.sessionParameters.safeGetStory().tiles)
        int tileIndex = 0
        int maxIterations = tileSource.size() * 10  // Some arbitrary large number

        def nextTiles = { List<Tile> exclude, int numTilesNeeded ->
            List<Tile> selectedTiles = []
            int iterationCount = 0
            while (selectedTiles.size() < numTilesNeeded && iterationCount < maxIterations) {
                Tile currentTile = tileSource[tileIndex]
                if (!exclude.contains(currentTile)) {
                    selectedTiles << currentTile
                }
                tileIndex = (tileIndex + 1) % tileSource.size()
                if (tileIndex == 0) {
                    Collections.shuffle(tileSource)
                }
                iterationCount++
            }

            if (iterationCount >= maxIterations) {
                throw new Exception("Could not find enough tiles to assign to users")
            }

            return selectedTiles
        }

        int numTilesPerUser = session.sessionParameters.safeGetInitialNbrOfTiles()
        active.each { UserSession userSession ->
            SessionInitialUserStory userStory = new SessionInitialUserStory(session: session, alias: userSession.userAlias)
            List<Tile> tilesForUser = nextTiles(userStory.initialTiles, numTilesPerUser)
            userStory.setInitialTiles(tilesForUser)

            if (!userStory.save(flush: true)) {
                throw new Exception("Could not save initial tiles for user ${userSession.userAlias}")
            }
        }
    }

    /**
     * Assigns aliases to users in a session and marks users as selected for the round
     * @param session
     * @param numUsers
     * @return
     */
    def assignAliasesAndInitialTiles(Session session, int numUsers) {
        List<String> nodes = networkGenerateService.generateGraph(session,numUsers, session.sp("networkTemplate"))
        List<UserSession> sessions = UserSession.findAllBySessionAndMissingAndState(session,false,UserSession.State.WAITING)
        List<UserSession> active = []
        SessionType type = session.sp("sessionType")
        if (type.state == SessionType.State.SINGLE) {
            if (nodes.size() > sessions.size()) {
                log.error("Not enough users for nodes")
                throw new Exception("Not enough users for nodes")
            }
            nodes.each {
                UserSession userSession = sessions.pop()
                userSession.selected = true
                userSession.userAlias = it
                userSession.save(flush: true)
                active << userSession
            }
            sessions.each {
                it.state = UserSession.State.REJECTED
                it.save(flush: true)
            }

        } else if (type.state == SessionType.State.MIXED) {
            int maxByConstraint = nodes.size() / type.constraintTests.size()
            List partitions = [[]] * type.constraintTests.size()
            sessions.each { UserSession us ->
                int idx = type.constraintTests.findIndexOf { ConstraintTest ct ->
                    ct.testUser(us.user)
                }
                if (idx > -1) {
                    partitions[idx] << us
                } else {
                    log.error("User ${us.user.id} does not match any constraint test")
                }
            }
            while (!nodes.isEmpty()) {
                partitions.eachWithIndex { List<UserSession> partition, int idx ->
                    if (partition.size() < maxByConstraint) {
                        UserSession userSession = partition.pop()
                        userSession.selected = true
                        userSession.userAlias = nodes.pop()
                        userSession.save(flush: true)
                        active << userSession
                    }
                }
            }

            //TODO set user initial story
            partitions.flatten().each { UserSession us ->
                us.state = UserSession.State.REJECTED
                us.save(flush: true)
            }


        }
        assignInitialTiles(active)
    }


    /**
     * Counts the number of users waiting for a session to start, based on the session type
     * @param session
     * @return
     */
    int countWaitingUsers(Session session) {
        List<UserSession> sessions = UserSession.findAllBySessionAndMissingAndState(session,false,UserSession.State.WAITING)
        SessionType type = session.sp("sessionType")
        if (type.state == SessionType.State.SINGLE) {
            return sessions.size()
        } else if (type.state == SessionType.State.MIXED) {
            int maxByConstraint = session.sp("minNode") / type.constraintTests.size()
            return type.constraintTests.sum { ConstraintTest constraintTest ->
                int count = sessions.count { UserSession userSession ->
                    constraintTest.testUser(userSession.user)
                }
                Math.min(count, maxByConstraint)
            }
        }
        return 0
    }






    /**
     * This opens the session and starts the waiting timer
     * @param session
     * @return
     */
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
     * Advance the round - kicks off the experiment if it has not yet been started
     * When the experiment starts, a status object is created to track the progress of the experiment
     * If the experiment is not finished, a pause is scheduled
     * @param session
     * @return
     */
    def advanceRound(Session session) {
        log.debug("Advance round for ${session.id}")
        if (!experimentsRunning.containsKey(session.id)) {
            int selectedUserCount = UserSession.countBySessionAndSelected(session,true)
            experimentsRunning[session.id] = new ExperimentRoundStatus(selectedUserCount, session.sp("roundCount") as int)
        }
        if (experimentsRunning[session.id].isFinished()) {

            Session.withSession {
                def s = Session.get(session.id)
                s.state = Session.State.FINISHED
                s.save(flush: true)
            }
        } else {
            scheduleRoundPause(session)
        }
    }

    /**
     * Schedules a pause to being after the round time has elapsed
     * When the pause begins, the experiment is paused and a timer is scheduled to advance the round
     * @see ExperimentService#scheduleRoundAdvance
     * @param session
     * @return
     */
    private def scheduleRoundPause(Session session) {
        new Timer().schedule({
            experimentsRunning[session.id].pause()
            scheduleRoundAdvance(session)
        } as TimerTask, 1000 * session.sp("roundTime") as long)
    }

    /**
     * Schedules a round advance - this is a little non-intuitive.  We check every second to see if all users have
     * submitted (@see ExperimentRoundStatus#checkPauseStatus) or if the pause as gone on for too long.  If so, we advance the round.
     * If we're out of rounds, we finish the session by setting the ExperimentRoundStatus to FINISHED.
     *
     * @param session
     * @return
     */
    private def scheduleRoundAdvance(Session session) {
        Timer t = new Timer()
        t.scheduleAtFixedRate({
            ExperimentRoundStatus status = experimentsRunning[session.id]
            //TODO make this code clearer
            //ExperimentRoundState#checkPauseStatus() is not merely a check - if the pause has gone one for too long
            if (status.checkPauseStatus() == ExperimentRoundStatus.Status.ACTIVE || status.checkPauseStatus() == ExperimentRoundStatus.Status.FINISHED) {
                t.cancel()
                //Note that the last advanceRound checks the finished status and finishes the session
                advanceRound(session)
            }

        } as TimerTask, 1000l, 1000l)

    }

    def userSubmitted(User user,Session session,int round) {
        ExperimentRoundStatus status = getExperimentStatus(session)
        //only register the submission if it is for the current round
        if (!status || status.isFinished()) {
           log.debug("User ${user.id} submitting for session ${session.id}:${session.state} but is finished or not running; ignoring")
        } else if (status.round == round) {
            status.submitUser(user.id)
            log.debug("(${user.id}) Submitted ${round}")
        } else {
            log.debug("Submitted wrong round!")
        }
    }

    def getExperimentStatus(Session session) {
        experimentsRunning[session.id]

    }


    /**
     * Scoring procedure for a single story; roughly counts the number of inversions present
     *
     * @param truth
     * @param sample
     * @return
     */
    static Float score(List truth, List sample) {
        if (sample && sample.size()>1) {
            def c2 = { (it.size() * (it.size() - 1)) / 2f }
            def tmap = [:]
            truth.eachWithIndex { Object entry, int i -> tmap[entry] = i }

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
