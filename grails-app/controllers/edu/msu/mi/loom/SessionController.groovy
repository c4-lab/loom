package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

import java.text.SimpleDateFormat

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

import java.time.LocalDate
import java.time.ZoneId

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

    static String SESSION_NOT_EXIST = "The session you have tried to access does not exist."
    static String SESSION_NOT_BEGUN = "The session you have tried to access has not yet begun."
    static String SESSION_CANCELLED =  "The session you have tried to access has been cancelled."
    static String SESSION_FINISHED =  "The session you have tried to access has already finished."
    static String SESSION_NOT_QUALIFIED =  "You are not qualified for the requestsed session."



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

    def available() {
        User u = springSecurityService.currentUser as User
        def today = new Date().clearTime()
        def tomorrow = today + 1

        def sessions = (List<Session>)Session.createCriteria().list {
            or {
                and {
                    ne('state', Session.State.PENDING)
                    or {
                        between('scheduled', today, tomorrow)
                        between('startWaiting',today, tomorrow)
                    }
                }
                and {
                    eq('state',Session.State.SCHEDULED)
                    ge('scheduled', today)
                }
            }
            order('scheduled', 'asc')
        }

        sessions.sort {
            it.state == Session.State.SCHEDULED?it.scheduled:it.startWaiting
        }

        def dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        def timeFormat = new SimpleDateFormat("HH:mm:ss")

        def sessionList = sessions.collect { session ->
            def failures = constraintService.failsConstraints(u, session)
            boolean canJoin = session.state == Session.State.ACTIVE && UserSession.countByUserAndSession(u, session) > 0
            boolean isQualified = !failures

            Date sessionTime = session.state == Session.State.SCHEDULED?session.scheduled:session.startWaiting

            [
                    id: session.id,
                    state: session.state.toString(),
                    scheduledDate: dateFormat.format(sessionTime),
                    scheduledTime: timeFormat.format(sessionTime),
                    qualified: isQualified,
                    canJoin: canJoin,
                    link: canJoin || (isQualified && session.state == Session.State.WAITING) ? "/loom/session/s/${session.id}?workerId=${u.username}" : null,
                    message: getSessionMessage(session, isQualified, canJoin)
            ]
        }

        def participatedSessions = UserSession.createCriteria().list {
            eq('user', u)
            isNotNull('userAlias')
            session {
                eq('state', Session.State.FINISHED)
            }
            projections {
                property('session')
            }
        }.sort {
            it.startWaiting
        }

        def remainingCount = 3-UserConstraintValue.createCriteria().get {
            eq('user', u)
            constraintProvider {
                eq('class', 'edu.msu.mi.loom.StorySeed')
                'in'('name', ['Vaccine', 'Swimmer', 'Earthquake'])
            }
            projections {
                countDistinct('constraintProvider.id')
            }
        }




        if (request.xhr) {
            render sessionList as JSON
        } else {
            render(view: "session_list", model: [
                    sessionList: sessionList,
                    participatedSessions: participatedSessions,
                    participationCount: participatedSessions.size(),
                    remainingCount: remainingCount

            ])
        }

    }



    private String getSessionMessage(Session session, boolean isQualified, boolean canJoin) {
        switch (session.state) {
            case Session.State.SCHEDULED:
                return "Not yet available"
            case Session.State.WAITING:
                return isQualified ? "Available to join" : "Not qualified"
            case Session.State.ACTIVE:
                return canJoin ? "Rejoin session" : "Session is full"
            case Session.State.CANCEL:
                return "Session cancelled"
            case Session.State.FINISHED:
                return "Session finished"
            default:
                return "Unavailable"
        }
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
            log.debug("Have an mturk assignment: ${mturkAssignment}")
        }

        Session session = sessionId ? Session.get(Long.parseLong(sessionId)) : null
        User user = springSecurityService.currentUser as User

        if (!user) {
            return render(status: BAD_REQUEST)
        }

        if (!session) {
            flash.message = SESSION_NOT_EXIST
            return render(view:"../not-found")
        }

        //Check if active user session exists
        UserSession us = UserSession.findBySessionAndUser(session, user)

        if (session.state == Session.State.PENDING) {
            flash.message = SESSION_NOT_BEGUN
            return render(view:"../not-found")
        }

        if (session.state == Session.State.CANCEL) {
            flash.message = SESSION_CANCELLED
            return render(view:"../not-found")
        }

        if (session.state == Session.State.FINISHED && !us) {
            flash.message = SESSION_FINISHED
            return render(view:"../not-found")
        }





        //Create a user session if the user is qualified
        if (!us) {
            // We haven't seen this user before, so let's make sure they are qualified
            def failures = constraintService.failsConstraints(user, session)
            if (failures) {
                log.debug("User ${user.username} is not qualified for session ${session.id}")
                flash.message = SESSION_NOT_QUALIFIED
                return render(view:"../not-found")

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
        def userRound = params.currentRound as Integer

        Session s = Session.get(params.sessionId)
        if (!s) {
            return render(status: BAD_REQUEST)
        }

        if (s.state == Session.State.CANCEL) {
            render( "cancelled")
        } else {
            sessionService.updatePresence(user,true)
            ExperimentRoundStatus status = experimentService.getExperimentStatus(s)
            log.debug("${user} with round ${userRound} pinging for status ${status.toString()}")
            if (s.state == Session.State.FINISHED || status?.currentStatus == ExperimentRoundStatus.Status.FINISHED) {
                render("finished")
            } else if (status?.currentStatus == ExperimentRoundStatus.Status.PAUSING) {
                render("paused")
            } else if (status?.currentStatus == ExperimentRoundStatus.Status.ACTIVE && status?.round == userRound) {
                log.debug("User is pinging early; respond as pause")
                render("paused")
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
        log.debug("Checking experiment ready")
        Session.withNewSession {
            def session = Session.get(params.session)
            if (!session) {
                return render(status: BAD_REQUEST)
            }

            def user = springSecurityService.currentUser as User
            sessionService.updatePresence(user,true)
            if (session.state == Session.State.WAITING) {
                log.debug("${user.username} Still waiting")
                return render(["experiment_ready": false, count: experimentService.totalCountWaitingUsers(session)] as JSON)

            } else {
                log.debug("Done waiting")
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
        log.debug("User ${user.username} submitting for $roundNumber")
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
