package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.text.DecimalFormat

@Slf4j
@Transactional
class ExperimentService {


    def sessionService
    def springSecurityService
    Map<Object,ExperimentRoundStatus> experimentsRunning = [:]
    def waitingTimer = [:]




    private def getUserTilesForCurrentRound(String alias, Session s) {
        if (!alias) {
            return []
        }
        int round = getExperimentStatus(s).round
        if (round) {
            //log.debug("Trying to get tiles for ${alias} and session ${s} in round ${round}")
            //TODO This needs optimization
            def story = UserRoundStory.findAllByUserAliasAndSession(alias, s).max { it.round }
            if (story) {
                return story.currentTails
            }
        }
        ExperimentInitialUserStory.findByAliasAndExperiment(alias,s.exp).initialTiles


    }


    def getMyState(Session expSession) {
        def myAlias = sessionService.lookupUserAlias(expSession, springSecurityService.currentUser as User)
        getUserTilesForCurrentRound(myAlias, expSession)
    }



    def getNeighborModel(Session s) {
        User u = springSecurityService.currentUser as User
        ExperimentRoundStatus status = getExperimentStatus(s)
        boolean shouldPause = (status.currentStatus ==ExperimentRoundStatus.Status.PAUSING ||
                u.id in status.submitted)


        [neighborState: getNeighborsState(s), round: status.round,
         timeRemaining: Math.max(0f,s.exp.roundTime - (System.currentTimeMillis() - status.roundStart.time)/1000) as Integer,
         loomSession: s, paused: shouldPause]
    }

    def getNeighborsState(Session expSession) {
        def myAlias = sessionService.lookupUserAlias(expSession, springSecurityService.currentUser as User)
        Set<String> aliases = (Edge.findAllByExperimentAndTarget(expSession.exp,myAlias).source +
                Edge.findAllByExperimentAndSourceAndIsDirected(expSession.exp,myAlias,false).target) as Set

        int i = 1
        aliases.sort().collectEntries {
            [(i++), getUserTilesForCurrentRound(it,expSession)]
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
                if (count >= s.exp.userCount) {
                    log.debug("Ready to go!")
                    ((Timer) waitingTimer[s.id]).cancel()
                    waitingTimer.remove(s.id)
                    sessionService.assignAliasesAndMakeActive(s)
                    advanceRound(s)
                }
            }
        } as TimerTask, 0l, 3000l)
    }


    private def scheduleRoundPause(Session session) {
        new Timer().schedule({
            experimentsRunning[session.id].pause()
            scheduleRoundAdvance(session)
        } as TimerTask, 1000 * session.exp.roundTime as long)
    }

    private def scheduleRoundAdvance(Session session) {
        Timer t = new Timer()
        t.scheduleAtFixedRate({
            ExperimentRoundStatus status = experimentsRunning[session.id]
            if (status.checkPauseStatus() == ExperimentRoundStatus.Status.ACTIVE) {
                t.cancel()
                advanceRound(session)
            }

        } as TimerTask, 1000l, 1000l)
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
        if (!experimentsRunning.containsKey(session.id)) {
            experimentsRunning[session.id] = new ExperimentRoundStatus(session.exp.userCount,session.exp.roundCount)
        }
        if (experimentsRunning[session.id].isFinished()) {
            //experimentsRunning.remove(session.id)
            Session.withSession {
                def s = Session.get(session.id)
                s.state = Session.State.FINISHED
                s.save(flush: true)
            }
        } else {
            scheduleRoundPause(session)
        }
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
        //log.debug(experimentsRunning as String)
        experimentsRunning[session.id]
    }



    public static Float score(List truth, List sample) {
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
