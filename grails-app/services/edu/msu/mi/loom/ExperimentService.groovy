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
    def constraintService
    Map<Object, ExperimentRoundStatus> experimentsRunning = [:]
    def waitingTimer = [:]

    long WAITING_PERIOD = 1000l


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
        def myAlias = lookupUserAlias(expSession, springSecurityService.currentUser as User)
        getUserTilesForCurrentRound(myAlias, expSession)
    }

    /**
     * Returns the user's private list of tiles
     *
     * @param expSession
     * @return
     */
    def getMyPrivateState(Session expSession) {
        def myAlias = lookupUserAlias(expSession, springSecurityService.currentUser as User)
        Experiment e = expSession.exp
        SessionInitialUserStory.findByAliasAndSession(myAlias, expSession).initialTiles
    }

    /**
     * Walks through the edges that contain my alias and returns the state of all linked neighbors
     * @param expSession
     * @return
     */
    def getNeighborsState(Session expSession) {
        def myAlias = lookupUserAlias(expSession, springSecurityService.currentUser as User)
        Set<String> aliases = Edge.findAllBySession(expSession).findAll { it.ends.contains(myAlias) }.collect {
            it.alter(myAlias)
        } as Set
        int i = 1
        aliases.sort().collectEntries {
            [(i++), getUserTilesForCurrentRound(it, expSession)]
        }
    }


    /**
     * Counts the number of users waiting for a session to start, based on the session type.
     * Note that this will also flag users as missing if they have not checked in for some period of time..
     * @param session
     * @return
     */
    int countWaitingUsers(Session session) {

        List<UserSession> sessions = UserSession.findAllBySessionAndState(session, UserSession.State.WAITING)
        SessionType type = session.sessionParameters.safeGetSessionType()

        sessions.removeAll {
            if (System.currentTimeMillis() - it.presence.lastSeen.time > 10 * WAITING_PERIOD) {
                log.debug("removing user")
                it.presence.missing = true
            }
            return it.presence.missing
        }

        if (type.state == SessionType.State.SINGLE) {
            return sessions.size()
        } else if (type.state == SessionType.State.MIXED) {
            int maxByConstraint = session.sp("minNode") / type.constraintTests.size()
            return type.constraintTests.sum { ConstraintTest constraintTest ->
                int count = sessions.count { UserSession userSession -> constraintTest.testUser(userSession.user)
                }
                Math.min(count, maxByConstraint)
            }
        }
        return 0
    }


    private cancelWaitingTimer(Session session) {
        ((Timer) waitingTimer[session.id]).cancel()
        waitingTimer.remove(session.id)
        log.debug("Waiting timers are now: ${waitingTimer}")
    }

    @Transactional
    private makeSessionActive(Session session) {
        log.debug("Entering transactional block for session activation")
        assignAliasesAndInitialTiles(session, (int) session.sessionParameters.safeGetMinNode())
        session.state = Session.State.ACTIVE
        session.startActive = new Date()
        log.debug("Leaving transactional block for session activation")
        return session
    }

    /**
     * Assigns aliases to users in a session and marks users as selected for the round
     * @param session
     * @param numUsers
     * @return
     */
    def assignAliasesAndInitialTiles(Session session, int numUsers) {
        log.debug("Assigning aliases")
        List<String> nodes = networkGenerateService.generateGraph(session, numUsers, session.sp("networkTemplate"))
        //List<UserSession> sessions = UserSession.findAllBySessionAndMissingAndState(session,false,UserSession.State.WAITING)

        List<UserSession> sessions = UserSession.executeQuery('select u from UserSession as u where u.session=:sess and' + ' u.state=:stat and' + ' u.presence.missing=:missing',
                [sess: session, stat: UserSession.State.WAITING, missing: false])
        List<UserSession> active = []
        SessionType type = session.sessionParameters.safeGetSessionType()
        if (type.state == SessionType.State.SINGLE) {
            if (nodes.size() > sessions.size()) {
                log.error("Not enough users for nodes")
                throw new Exception("Not enough users for nodes")
            }
            nodes.each {
                UserSession userSession = sessions.pop()
                userSession.selected = true
                userSession.userAlias = it
                active << userSession
            }
            sessions.each {
                it.state = UserSession.State.REJECTED
            }


        } else if (type.state == SessionType.State.MIXED) {
            //TODO need to either handle uneven splits or prevent from happening
            int maxByConstraint = nodes.size() / type.constraintTests.size()
            List partitions = type.constraintTests.collect {[]}
            List<UserSession> rejects = []
            sessions.each { UserSession us ->
                int idx = type.constraintTests.findIndexOf {
                    ConstraintTest ct -> ct.testUser(us.user)
                }
                if (idx > -1) {
                    log.debug("${us.user.workerId} passes test ${type.constraintTests[idx]}")
                    if (partitions[idx].size() < maxByConstraint) {
                        partitions[idx] << us
                    } else {
                        rejects << us
                    }
                } else {
                    log.error("User ${us.user.id} does not match any constraint test")
                }
            }

            log.debug("Partitions filled: ${partitions}")
            log.debug("Attempt to assign nodes: "+nodes)
            //TODO This block is failing
            //TODO shuffle assignment for homophilous networks?
            //TODO In is unclear right now if it is possible for us to have unbalanced assignments.  That *shouldn't* happen, unless
            //TODO someone leaves in the midst of this assignment process AND we have somehow
            while (!nodes.isEmpty()) {

                partitions.each{ List<UserSession> partition ->

                    if (partition.size() > 0) {
                        UserSession userSession = partition.pop()
                        userSession.selected = true
                        userSession.userAlias = nodes.pop()
                        active << userSession
                    }
                }
            }
            //TODO ok, we should really place other users in a separate holder for rejection
            rejects.each {
                UserSession us -> us.state = UserSession.State.REJECTED
            }
        }
        assignInitialTiles(active)
        active.each {
            it.state = UserSession.State.ACTIVE
            User user = it.user
            Story story = it.session.sessionParameters.safeGetStory()
            log.debug("Setting constraint value for $story for $user.username")
            constraintService.setConstraintValueForUser(user, story, 1, user.isMturkWorker() ? it.mturkAssignment.hit.task.credentials : null)

        }
    }

    /**
     * Assigns initial tiles to the recently
     * @param active
     * @return
     */
    def assignInitialTiles(List<UserSession> active) {
        if (active.isEmpty()) {
            return  // Handle the empty case
        }

        Session session = active[0].session
        List<Tile> tileSource = new ArrayList<>(session.sessionParameters.safeGetStory().tiles)
        Collections.shuffle(tileSource)  // Initial shuffle

        int numTilesPerUser = session.sessionParameters.safeGetInitialNbrOfTiles()
        int maxIterations = tileSource.size() * 10  // Some arbitrary large number
        int tileIndex = 0

        active.each { UserSession userSession ->
            List<Tile> tilesForUser = []
            List<Tile> usedTiles = [] // For exclusion

            int iterationCount = 0
            while (tilesForUser.size() < numTilesPerUser && iterationCount < maxIterations) {
                Tile currentTile = tileSource[tileIndex]

                if (!usedTiles.contains(currentTile)) {
                    tilesForUser << currentTile
                    usedTiles << currentTile  // Add to exclusion list
                }

                tileIndex = (tileIndex + 1) % tileSource.size()
                if (tileIndex == 0) {
                    Collections.shuffle(tileSource)
                }
                iterationCount++
            }

            if (iterationCount >= maxIterations) {
                throw new RuntimeException("Could not find enough tiles to assign to users")
            }

            SessionInitialUserStory userStory = new SessionInitialUserStory(session: session, alias: userSession.userAlias)
            userStory.setInitialTiles(tilesForUser)
            userStory.save()

        }
    }


    /**
     * Sets up a periodic check to see if the session has enough users to start.  Accommodates both single and mixed sessions.
     * @param session
     * @return
     */
    def scheduleWaitingCheck(Session session) {

        waitingTimer[session.id] = new Timer()
        ((Timer) waitingTimer[session.id]).scheduleAtFixedRate({
            // log.debug("Checking waiters...")
            Session s
            //TDOD uncertain if a new session is really necessary here
            Session.withNewSession {
                s = Session.get(session.id)
                int count = countWaitingUsers(s)
                log.debug("Now have ${count} waiting users")
                if (count >= s.sessionParameters.safeGetMinNode()) {
                    cancelWaitingTimer(s)
                    s = makeSessionActive(s)
                    log.debug("Waiting timer advancing round.  Session status ${s.state}")
                    advanceSessionLifecycle(s)

                } else if (s.state != Session.State.WAITING) {
                    log.debug("Session state is no longer WAITING")
                    cancelWaitingTimer(s)
                }
            }
        } as TimerTask, 0l, WAITING_PERIOD)
    }

    /**
     * Advance the round - kicks off the experiment if it has not yet been started
     * When the experiment starts, a status object is created to track the progress of the experiment
     * If the experiment is not finished, a pause is scheduled
     * @param session
     * @return
     */
    def advanceSessionLifecycle(Session session) {
        if (session.state != Session.State.ACTIVE) {
            log.warn("advanceSessionLifecycle: Session is no longer active!")
            experimentsRunning.remove(session.id)
        } else {
            log.debug("Advance lifecycle for ${session.id}")
            ExperimentRoundStatus currentStatus = experimentsRunning.get(session.id)
            if (!currentStatus) {
                int selectedUserCount = UserSession.countBySessionAndSelected(session, true)
                currentStatus = new ExperimentRoundStatus(selectedUserCount, session.sp("roundCount") as int)
                log.debug("Initialized status for $session.id with $currentStatus")
                experimentsRunning[session.id] = currentStatus
            }
            if (currentStatus.isFinished()) {
                Session.withTransaction {
                    def s = Session.get(session.id)
                    s.state = Session.State.FINISHED
                }
            } else {
                log.debug("Schedule round pause for $session.id")
                scheduleRoundPause(session)
            }
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
        session.refresh()
        if (session.state != Session.State.ACTIVE) {
            log.warn("scheduleRoundPause: Session is no longer active!")
            experimentsRunning.remove(session.id)
        } else {
            new Timer().schedule({
                experimentsRunning[session.id].pause()
                scheduleRoundAdvance(session)
            } as TimerTask, 1000 * session.sessionParameters.safeGetRoundTime() as long)
        }
    }

    /**
     * Schedules a round advance while paused.  We check every second to see if all users have
     * submitted (@see ExperimentRoundStatus#checkPauseStatus) or if the pause as gone on for too long.  If so, we
     * cancel the advance timer and advance the round.
     *
     * @param session
     * @return
     */
    private def scheduleRoundAdvance(Session session) {
        Timer t = new Timer()
        t.scheduleAtFixedRate({
            Session.withNewSession {
                session.refresh()
            }

            if (session.state != Session.State.ACTIVE) {
                log.warn("scheduleRoundAdvance: Session is no longer active!")
                experimentsRunning.remove(session.id)
                t.cancel()
            } else {
                ExperimentRoundStatus status = experimentsRunning[session.id]
                if (status.currentStatus == ExperimentRoundStatus.Status.PAUSING) {
                    boolean advanceFlag = false
                    if (status.isAllSubmitted()) {
                        advanceFlag = true
                        log.debug("Is all submitted")
                    } else if (status.isOverTime()) {
                        updateUnsubmittedUsers(session)
                        advanceFlag = true
                        log.debug("Is over time")
                    }
                    if (advanceFlag) {
                        t.cancel()
                        status.advanceRound()
                        advanceSessionLifecycle(session)
                    }
                }
            }
        } as TimerTask, 1000l, 1000l)
    }

    def updateUnsubmittedUsers(Session session) {

        ExperimentRoundStatus status = experimentsRunning[session.id]
        List<UserSession> userSessions = UserSession.findAllBySessionAndSelected(session, true)
        userSessions.findAll {
            !(it.user.id in status.submitted)
        }.each {
            UserRoundStory userRoundStory = UserRoundStory.findByUserAliasAndSession(it.userAlias,session)
            userRoundStory.copyForRound(status.round)
        }
    }


    def userSubmitted(User user, Session session, int round, List<Tile> tiles) {
        ExperimentRoundStatus status = getExperimentStatus(session)
        //only register the submission if it is for the current round
        if (session.state == Session.State.CANCEL) {
            log.debug("User ${user.id} submitting for session ${session.id}:${session.state} but is cancelled")
            return (["continue": false, "reason": "cancellation"])
        }
        if (session.state == Session.State.FINISHED) {
            log.debug("User ${user.id} submitting for session ${session.id}:${session.state} but is finished or not running; ignoring")
            new UserRoundStory(time: new Date(), session: session, round: round, currentTiles: tiles, userAlias: lookupUserAlias(session, user)).save()
            status.submitUser(user.id)
            return (["continue": false, "reason": "finished"])
        }
        if (session.state == Session.State.WAITING) {
            return (["continue": false, "reason": "waiting"])
        }
        if (status.round != round) {
            log.debug("Submitted wrong round!")
            new UserRoundStory(time: new Date(), session: session, round: round, currentTiles: tiles, userAlias: lookupUserAlias(session, user)).save()
            return (["continue": true])
        } else {
            new UserRoundStory(time: new Date(), session: session, round: round, currentTiles: tiles, userAlias: lookupUserAlias(session, user)).save()
            status.submitUser(user.id)
            log.debug("(${user.username}) story registered for ${round}")
            return (["continue": true])

        }
    }

    def getExperimentStatus(Session session) {
        experimentsRunning[session.id]

    }

    def lookupUserAlias(Session session, User user) {
        UserSession.findBySessionAndUser(session, user).userAlias
    }


    /**
     * Scoring procedure for a single story; roughly counts the number of inversions present
     *
     * @param truth
     * @param sample
     * @return
     */
    static Float score(List truth, List sample) {
        if (sample && sample.size() > 1) {
            def c2 = { (it.size() * (it.size() - 1)) / 2f }
            def tmap = [:]
            truth.eachWithIndex { Object entry, int i -> tmap[entry] = i }

            int bad = 0
            for (int i in (0..<sample.size())) {
                for (int j in ((1 + i)..<sample.size())) {
                    if (tmap[sample[i]] > tmap[sample[j]]) bad++
                }
            }
            def result = (1 - (bad / c2(sample))) * (sample.size() / truth.size())
            DecimalFormat df = new DecimalFormat("####0.00")
            return Float.parseFloat(df.format(result))
        } else {
            0.0f
        }

    }


}
