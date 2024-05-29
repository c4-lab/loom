package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Slf4j
@Secured(["ROLE_USER", "ROLE_MTURKER","ROLE_PROLIFIC"])
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
    def sessionFactory



    /**
     * User has clicked "stop waiting" button.  This is the only way to change the state to "STOP"
     * @return
     */
    def stopWaiting() {
        Session s = Session.get(params.session)
        User u = springSecurityService.currentUser as User
        UserSession us = UserSession.findBySessionAndUser(s, u)
        us.stopWaiting(UserSession.State.STOP)
        render(view: "stop_waiting", model: [time:  us.wait_time, user: u, session: s])
    }


    /**
     * This is the main entry point for the experiment
     * Note that this endpoint should really only get called from a direct navigation action
     *
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
            mturkAssignment = mturkService.attachAssignment(assignmentId, hitId)
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



        //Check for users coming in on separate assignments
        if (us && us.mturkAssignment) {
            if (!us.mturkAssignment && mturkAssignment && mturkAssignment!=us.mturkAssignment) {
                mturkAssignment.duplicate = true
                return render(view: 'duplicate_us')
            }
        }

        if (session.state == Session.State.WAITING) {
            sessionService.updatePresence(user,true)
            if (us.state==UserSession.State.STOP) {
                //User previously left, either deliberately or due to some client side error
                //Set the to waiting again and clear the "stoppedWaiting" field
                us.state = UserSession.State.WAITING
                us.stoppedWaiting = null
                us.started = new Date()
            }
            return render(view: 'waiting_room', model: [session: session, username: user.username, assignmentId: assignmentId])

        } else if (session.state == Session.State.ACTIVE) {
            //User has been selected to play, but has not yet been made active and placed in a session
            if (us.selected) {
                sessionService.updatePresence(user,true)
                //TODO 9-19-23 - this fails right now, probably because we've not finished updating users
                if (us.state in [UserSession.State.WAITING, UserSession.State.STOP]) {
                    us.state = UserSession.State.ACTIVE
                }

                //TODO 9-20-23 - Story panel is too small, and the story tiles are not coming through correctly
                //TODO - looks like the count is right, just the wrong tiles, so simply not displaying the correct
                //indices here n
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
                redirect(action: 'finishExperiment', params: [session: session.id])
            } else {
                return redirect(controller: "logout", action: "index", params: [reason: "The session is done", sessionId: session.id])
            }


        } else if (session.state == Session.State.CANCEL) {

            //NOTE: the calculation of total m
//            int totalMinutes = 0
//            if (us) {
//                us.stoppedWaiting = new Date()
//                if (us.started) {
//                    totalMinutes = (System.currentTimeMillis() - us.started.time) / (60 * 1000)
//                    us.wait_time += totalMinutes
//                }
//
//                us.started = null
//                us.state = UserSession.State.CANCELLED
//                us.save(flush: true)
//            }
            //TODO - if a session is canceled, it CANNOT be started again.  Need to verify that this is the case
            //
            // TODO - STOPPED HERE 9/4/23 4:07PM - need to make sure  users can rejoin session
            return render(view: "cancel_waiting", model: [time: us.wait_time, user: user, session: session])
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
        if (!status) {
            throw new RuntimeException("Missing experiment round status for ${session.id}!")
        }
        model['neighborState'] = experimentService.getNeighborsState(session)
        model['myState'] = experimentService.getMyStoryState(session)
        model['myInitialState'] = experimentService.getMyPrivateState(session)
        model['uiFlag'] = session.sessionParameters.safeGetIsInline() as int
        model['round'] = status.round
        model['paused'] = (status.currentStatus == ExperimentRoundStatus.Status.PAUSING ||
                user.id in status.submitted)

        int timeRemaining = Math.max(0f, session.sessionParameters.safeGetRoundTime() - (System.currentTimeMillis() - status.roundStart.time) / 1000) as Integer
        model['timeRemaining'] = timeRemaining
        model['startTime']=status.roundStart.time
        model['roundDuration']=session.sessionParameters.safeGetRoundTime()
        model['serverTime']=System.currentTimeMillis()

        model['loomSession'] = session
        log.debug("Returning model round ${status.round} to ${user}  with time remaining: $timeRemaining")
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
        def user = springSecurityService.currentUser as User
        Session s = Session.get(params.sessionId)
        if (!s) {
            return render(status: BAD_REQUEST)
        }

        if (s.state == Session.State.CANCEL) {
            render( "cancelled")
        } else {
            ExperimentRoundStatus status = experimentService.getExperimentStatus(s)
            if (s.state == Session.State.FINISHED || status?.currentStatus == ExperimentRoundStatus.Status.FINISHED) {
                render("finished")
            } else if (status?.currentStatus == ExperimentRoundStatus.Status.PAUSING) {
                sessionService.updatePresence(user,true)
                render("paused")
            } else {
                sessionService.updatePresence(user,true)
                redirect(action: "experimentContent", params: [session: params.sessionId])
            }
        }
    }
    /**
     * Called from JS while waiting to see if the experiment is ready to start
     * @return
     */
    def checkExperimentReadyState() {
        log.debug("Checking experiment ready")
        Session.withNewSession {
            def session = Session.get(params.session)
            if (!session) {
                return render(status: BAD_REQUEST)
            }

            def user = springSecurityService.currentUser as User

            if (session.state == Session.State.WAITING) {
                log.debug("${user.username} Still waiting")
                sessionService.updatePresence(user,true)
                return render(["experiment_ready": false, count: experimentService.countWaitingUsers(session)] as JSON)

            } else {
                log.debug("Done waiting")
                sessionService.updatePresence(user,true)
                return render(["experiment_ready": true, count: 0] as JSON)
            }
        }



    }

    def cancelNotification() {
        Session s = Session.get(params.loomsession)
        return render(view: 'cancel_running', model: [session: s])

    }


    def submitExperiment() {

        def userTiles = params.tails
        def sessionId = params.session
        def roundNumber = Integer.parseInt(params.roundNumber)


        Session loomSession = Session.get(sessionId)
        if (!loomSession) {
           return render(status: BAD_REQUEST)
        }


        def user = springSecurityService.currentUser as User
        sessionService.updatePresence(user,true)
        log.debug("User ${user.username} submitting for $roundNumber: $userTiles")
        List submittedTiles = userTiles ? userTiles.split(";").collect { Tile.get(Integer.parseInt(it)) } : []
        Map result = experimentService.userSubmitted(user, loomSession, roundNumber, submittedTiles)
        result.put("status",OK)
        render(result as JSON)
    }



    def finishExperiment() {
        def session = Session.get(params.session)
        if (!session) {
            return  render(status: BAD_REQUEST)
        }
        def user = springSecurityService.currentUser as User
        UserSession us = UserSession.findByUserAndSession(user, session)
        List<UserRoundStory> userRoundStories = UserRoundStory.findAllBySessionAndUserAlias(session, us.userAlias)
        Map<Integer, List<UserRoundStory>> groupedByRound = userRoundStories.groupBy { it.round }
        List<UserRoundStory> mostRecentStories = groupedByRound.collect { round, stories ->
            stories.max { it.time }
        }
        List<UserRoundStory> sortedStories = mostRecentStories.sort { it.round }


        List scores = sortedStories.collect {
            it.score
        }
        render(view: 'finish', model: [scores: scores, completionCode: us.completionCode, isTurker:user.isMturkWorker()])



    }



}
