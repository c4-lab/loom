package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Slf4j
@Secured(["ROLE_USER", "ROLE_MTURKER"])
//@Secured('permitAll')
class SessionController {
    static allowedMethods = [
            submitTraining: 'POST'
    ]

    def experimentService
    def springSecurityService
    def sessionService
    def mturkService
    def constraintService



    /**
     * User has clicked "stop waiting" button.  This is the only way to change the state to "STOP"
     * @return
     */
    def stopWaiting() {
        log.debug("In stop waiting")
        Session s = Session.get(params.session)
        User u = springSecurityService.currentUser as User
        UserSession us = UserSession.findBySessionAndUser(s, u)
        us.stoppedWaiting = new Date()
        int totalMinutes = (System.currentTimeMillis() - us.started.time) / (60 * 1000)
        us.started = null
        us.state = UserSession.State.STOP

        us.wait_time += totalMinutes
        us.save(flush: true)
        render(view: "stop_waiting", model: [time: totalMinutes, user: u, session: s])


    }


    /**
     * This is the main entry point for the experiment
     * All user state management is done here
     * @return
     */
    def experiment() {

        def sessionId = params.session
        def hitId = params?.hitId
        def assignmentId = params?.assignmentId
        if (assignmentId == "null") {
            assignmentId = null
        }
        MturkAssignment mturkAssignment = null

        if (hitId == "null") {
            hitId = null
        }

        //attach assignment to hit
        if (hitId && assignmentId) {
            mturkAssignment = mturkService.attachAssignment(hitId, assignmentId)
        }

        Session session = sessionId ? Session.get(Long.parseLong(sessionId)) : null
        User user = springSecurityService.currentUser as User

        if (!user || !session) {
            return render(status: BAD_REQUEST)
        }

        //Check if active user session exists
        UserSession us = UserSession.findBySessionAndUser(session, user)

        //Create a user session if the user is qualified
        if (!us) {
            // We haven't seen this user before, so let's make sure they are qualified
            def failures = constraintService.failsConstraints(user, session)
            if (failures) {
                log.debug("User ${user.username} is not qualified for session ${session.id}")
                return redirect(controller: "logout", action: "index", params: [reason: "You are not qualified for this session", sessionId: session.id])

            }
            //NOTE: User session is explicitly set to WAITING on creation
            us = UserSession.create(user, session, new Date(), mturkAssignment, true)
        }

        //User was marked as missing, but appears to have returned
        if (us.missing) {
            us.missing = false
            us.save(flush: true)
        }

        //Check for users coming in on separate assignments
        if (us && us.mturkAssignment) {
            if (!us.mturkAssignment && mturkAssignment && mturkAssignment!=us.mturkAssignment) {
                mturkAssignment.duplicate = true
                mturkAssignment.save(flush: true)
                return render(view: 'duplicate_us')
            }
        }

        if (session.state == Session.State.WAITING) {

            if (us.state==UserSession.State.STOP) {
                //User previously left, either deliberately or due to some client side error
                //Set the to waiting again and clear the "stoppedWaiting" field
                us.state = UserSession.State.WAITING
                us.stoppedWaiting = null
                us.save(flush: true)
            }
            return render(view: 'waiting_room', model: [session: session, username: user.username, assignmentId: assignmentId])

        } else if (session.state == Session.State.ACTIVE) {
            //User has been selected to play, but has not yet been made active and placed in a session
            if (us.selected) {
                if (us.state in [UserSession.State.WAITING, UserSession.State.STOP]) {
                    Story story = (Story) session.sp("story")
                    constraintService.setConstraintValueForUser(user, story, 1, user.isMturkWorker() ? us.mturkAssignment.hit.task.credentials : null)
                    us.state = UserSession.State.ACTIVE
                    us.save(flush: true)
                }

                //User is active, so just give them their current state
                if (us.state == UserSession.State.ACTIVE) {
                    LinkedHashMap<Object, Object> model = generateRoundModel(session, user)
                    return render(view: 'experiment', model: model)
                }
            }

            //User was never selected, so we don't really care about their state
            return redirect(controller: "logout", action: "index", params: [reason: "The session is full", sessionId: session.id])

        } else if (session.state == Session.State.FINISHED) {
            if (us.state == UserSession.State.ACTIVE) {
                us.state = UserSession.State.COMPLETE
                us.save(flush: true)
                redirect(action: 'finishExperiment', params: [session: session.id])
            } else {
                return redirect(controller: "logout", action: "index", params: [reason: "The session is done", sessionId: session.id])
            }


        } else if (session.state == Session.State.CANCEL) {


            int totalMinutes = 0
            if (us) {
                us.stoppedWaiting = new Date()
                if (us.started) {
                    totalMinutes = (System.currentTimeMillis() - us.started.time) / (60 * 1000)
                }

                us.started = null
                us.save(flush: true)
            }
            //TODO - if a session is canceled, it CANNOT be started again.  Need to verify that this is the case
            //TODO - STOPPED HERE 9/4/23 4:07PM - need to make sure  users can rejoin session
            return render(view: "cancel", model: [time: totalMinutes, user: user, session: session])
//                render(view: 'cancel')

        }

        //Something went wrong
        return render(view:"../not-found")

    }
    /**
     * Used to generate the model for the experiment page
     *
     * @param session
     * @param user
     * @return
     */
    private LinkedHashMap<Object, Object> generateRoundModel(Session session, User user) {
        def model = [:]
        ExperimentRoundStatus status = experimentService.getExperimentStatus(session)
        model['neighborState'] = experimentService.getNeighborsState(session)
        model['myState'] = experimentService.getMyStoryState(session)
        model['myInitialState'] = experimentService.getMyPrivateState(session)
        model['uiFlag'] = session.sessionParameters.safeGetIsInline() as int
        model['round'] = status.round
        model['paused'] = (status.currentStatus == ExperimentRoundStatus.Status.PAUSING ||
                user.id in status.submitted)
        model['timeRemaining'] = Math.max(0f, session.sessionParameters.safeGetRoundTime() - (System.currentTimeMillis() - status.roundStart.time) / 1000) as Integer
        model['loomSession'] = session
        model
    }


    //Just neighbors!
    def experimentContent() {
        def sessionId = params.session
        User user = springSecurityService.currentUser as User
        Session session = sessionId ? Session.get(Long.parseLong(sessionId)) : null

        if (!user || !session) {
            return render(status: BAD_REQUEST)
        }
        def model = generateRoundModel(session, user)

        return render(template: 'experiment_content', model: model)
    }


    /**
     * Called from JS to see if the round has been processed - occurs during a pause
     *
     * @return
     */
    def checkExperimentRoundState() {
        Session s = Session.get(params.sessionId)
        ExperimentRoundStatus status = experimentService.getExperimentStatus(s)
        if (s.state == Session.State.FINISHED || status?.currentStatus == ExperimentRoundStatus.Status.FINISHED) {

            render("finishExperiment")
        } else {

            if (status?.currentStatus == ExperimentRoundStatus.Status.PAUSING) {

                render("pausing")
            } else {

                redirect(action: "experimentContent", params: [session: params.sessionId])
            }
        }
    }
    /**
     * Called from JS while waiting to see if the experiment is ready to start
     * @return
     */
    def checkExperimentReadyState() {
        def session = Session.get(params.session)
        def user = springSecurityService.currentUser as User
        if (session) {
            if (session.state == Session.State.WAITING) {
                log.debug("${user.username} Still waiting")
                return render(["experiment_ready": false, count: experimentService.countWaitingUsers(session)] as JSON)

            } else {
                log.debug("Done waiting")
                return render(["experiment_ready": true, count: 0] as JSON)
            }
        }

        render(status: BAD_REQUEST)
    }


    def submitExperiment() {

        def userTiles = params.tails
        def sessionId = params.session
        def roundNumber = Integer.parseInt(params.roundNumber)

        if (sessionId) {
            Session session = Session.get(sessionId)
            def user = springSecurityService.currentUser as User
            List submittedTiles = userTiles ? userTiles.split(";").collect { Tile.get(Integer.parseInt(it)) } : []
            log.debug("${user.username} submitted round ${roundNumber}")
            sessionService.saveUserStory(session, roundNumber, submittedTiles, user)
            render(status: OK)

        } else {
            render(status: BAD_REQUEST)
        }
    }

    def finishExperiment() {
        def session = Session.get(params.session)
        def user = springSecurityService.currentUser as User
        UserSession us = UserSession.findByUserAndSession(user, session)
        List scores = UserRoundStory.findAllBySessionAndUserAlias(session, us.userAlias).sort { it.round }.score

        if (session) {
            render(view: 'finish', model: [scores: scores, completionCode: us.completionCode, isTurker:user.isMturkWorker()])
            return
        }

        render(status: BAD_REQUEST)

    }

}
