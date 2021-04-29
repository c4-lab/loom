package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.text.DecimalFormat

@Slf4j
@Transactional
class ExperimentService {


    def sessionService
    def springSecurityService
    def mturkService
    Map<Object,ExperimentRoundStatus> experimentsRunning = [:]
    def waitingTimer = [:]




    private def getUserTilesForCurrentRound(String alias, Session s, int neiSize) {
        if (!alias) {
            return []
        }

        def round = getExperimentStatus(s)?.round
        if(round==1){
            println()
        }
        if (round) {
            def userMaxRound = UserRoundStory.findAllBySession(s).max { it.round }.round
            def currentUserRound = UserRoundStory.findAllByUserAliasAndSession(alias, s).max { it?.round }?.round

            // if its neighbor has not submitted, check its neighbor's neighbor, which should exclude itself, if that exists,
            if (currentUserRound && currentUserRound!=userMaxRound && neiSize == 1) {
                def myAlias = sessionService.lookupUserAlias(s, springSecurityService.currentUser as User)
                Set<String> aliases = (Edge.findAllByExperimentAndTarget(s.exp,alias).source +
                        Edge.findAllByExperimentAndSourceAndIsDirected(s.exp,alias,false).target) as Set
                if(aliases){
                    aliases.remove(myAlias)
                    int i = 1
                    aliases.sort().collectEntries {
                        [(i++), getUserTilesForCurrentRound(it as String,s, aliases.size())]
                    }
                }

            }
            //log.debug("Trying to get tiles for ${alias} and session ${s} in round ${round}")
            //TODO This needs optimization
            def story = UserRoundStory.findAllByUserAliasAndSession(alias, s).max { it?.round }
            if (story) {
                return story.currentTails
            }
        }
        ExperimentInitialUserStory.findByAliasAndExperiment(alias,s.exp).initialTiles


    }


    def getMyState(Session expSession) {
        def myAlias = sessionService.lookupUserAlias(expSession, springSecurityService.currentUser as User)
        println("mystateresrsarfs")
        println(getUserTilesForCurrentRound(myAlias, expSession, myAlias.size()))
        getUserTilesForCurrentRound(myAlias, expSession, myAlias.size())

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

            [(i++), getUserTilesForCurrentRound(it as String,expSession, aliases.size())]
        }


    }




    private def scheduleWaitingCheck(Session session) {

        waitingTimer[session.id] = new Timer()
        waitingTimer[session.id].scheduleAtFixedRate({
            log.debug("Checking waiters...")
            Session s
            Session.withNewSession {
                s = Session.get(session.id)
                int count = UserSession.countBySession(s)
                if (count >= s.exp.min_node) {
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
            if (status.checkPauseStatus() == ExperimentRoundStatus.Status.ACTIVE || status.checkPauseStatus() == ExperimentRoundStatus.Status.FINISHED) {
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
            experimentsRunning[session.id] = new ExperimentRoundStatus(session.exp.max_node,session.exp.roundCount)
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

    def userSubmitted(User user,Session session,int round) {
        ExperimentRoundStatus status = getExperimentStatus(session)
        //only register the submission if it is for the current round
        if (!status || status.isFinished()) {
           log.debug("User ${user.id} submitting for session ${session.id}:${session.state} but is finished or not running; ignoring")
        } else if (status.round == round) {
            status.submitUser(user.id)
//            if(round==1){
//                mturkService.assignQualification(user.turkerId, Story.constructQualificationString(session.exp.story),1)
//            }
            log.debug("(${user.id}) Submitted ${round}")

        } else {
            log.debug("Submitted wrong round!")
        }
    }

    def getExperimentStatus(Session session) {

        //log.debug(experimentsRunning as String)
        experimentsRunning[session.id]
//        if(experimentsRunning.containsKey(session.id)){
//            return experimentsRunning[session.id]
//        }else{
//            return null
//        }

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
