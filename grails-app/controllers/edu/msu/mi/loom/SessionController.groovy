package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

import static org.springframework.http.HttpStatus.*

@Slf4j
@Secured("ROLE_USER")
class SessionController {
    static allowedMethods = [
            submitTraining: 'POST'
    ]

    def experimentService
    def springSecurityService
    def statService
    def sessionService


    def experiment() {
        def sessionId = params.session
        Session session = sessionId ? Session.get(Long.parseLong(sessionId)) : null
        User user = springSecurityService.currentUser as User
        log.debug("Got user $user")
        if (!sessionService.checkSessionAvailability(user,session)) {
            return redirect(controller:"logout",action:"index")
        } else if (session.state == Session.State.PENDING) {

            UserSession us = UserSession.findByUserAndSession(user, session)
            if (!us) {
                //the session hasn't started yet
                log.debug("Attempt to register user $user with ${session.id}")
                us = new UserSession(user: user, session: session)
                if (!us.save(flush: true)) {
                    log.debug(us.errors as String)
                }
            } else if (!us.isWaiting()) {
                us.state = "WAITING"
                if (!us.save(flush: true)) {
                    log.debug(us.errors as String)
                }
            }

            return render(view: 'waiting_room', model: [session: session, username:user.username])

        } else if (session.state == Session.State.ACTIVE) {
            if (experimentService.getExperimentStatus(session)) {

                def state = experimentService.getExperimentStatus(session)

                //TODO allow a new user to join if one is missing
                UserSession userSession = UserSession.findByUserAndSession(user, session)
                int round = state.round

                if (!userSession?.userAlias) {
                    return redirect(controller:"logout",action:"index")
                }

                if (!userSession.isActive()) {
                    userSession.state = "ACTIVE"
                    userSession.save(flush: true)
                }

                boolean userSubmitted = UserRoundStory.countByUserAliasAndRound(userSession.userAlias, round) > 0

                if (state.status == ExperimentService.Status.PAUSING || userSubmitted) {
                    return render("WAITING")

                } else if (state.status == ExperimentService.Status.RUNNING) {
                    def model = [userList: experimentService.getUserStateModel(session), round: round, session: session]

                    if (params.internal) {
                        return render(template: 'experiment_content', model: model)
                    } else {
                        //this is the current round according to our internal clock
                        long timeRemaining = Math.max(0, (session.experiment.roundTime - (System.currentTimeMillis() - state.start as long) / 1000) as Integer)

                        //user is possibly coming back to the site after being away
                        //place the user back into the game at the right point

                        model.timeRemaining = timeRemaining
                        log.debug("Return model ${model.timeRemaining}")
                        return render(view: 'experiment', model: model)
                    }
                }

            } else {
                //shouldn't ever get here
                return render(status: BAD_REQUEST)
            }
        } else if (session.state == Session.State.FINISHED) {
            return render(["finishExperiment", [session: session.id]] as JSON)

        }
        render(status: BAD_REQUEST)
    }

    def checkExperimentReadyState() {
        def session = Session.get(params.session)
        if (session) {
            if (session.state == Session.State.PENDING) {
                log.debug("Still waiting")
                return render(["experiment_ready": false, count: UserSession.countBySessionAndState(session, "WAITING")] as JSON)

            } else {
                log.debug("Done waiting")
                return render(["experiment_ready": true, count: 0] as JSON)
            }
        }

        render(status: BAD_REQUEST)
    }

    def submitExperiment() {

        def userTails = params.tails
        def sessionId = params.session
        def roundNumber = Integer.parseInt(params.roundNumber)

        if (sessionId) {
            Session session = Session.get(sessionId)
            def user = springSecurityService.currentUser as User
            List submittedTails = userTails ? userTails.split(";").collect { Tile.get(Integer.parseInt(it)) } : []
            sessionService.saveUserStory(session, roundNumber, submittedTails, user)
            redirect(action: 'experiment', params: [session: session.id, internal: true])

        } else {
            render(status: BAD_REQUEST)
        }
    }

    def finishExperiment() {
        def session = Session.get(params.session)
        UserSession us = UserSession.findByUserAndSession(springSecurityService.currentUser as User, session)
        List scores = UserRoundStory.findAllBySessionAndUserAlias(session,us.userAlias).sort {it.round}.score



        if (us.isActive()) {
            us.state = "COMPLETE"
            us.save(flush: true)
        }

        if (session) {
            render(view: 'finish', model: [scores: scores, completionCode: us.completionCode])
            return
        }

        render(status: BAD_REQUEST)

    }


}
