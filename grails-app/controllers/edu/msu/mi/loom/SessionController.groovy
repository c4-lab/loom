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
    def sessionService





    def startWaiting() {
        User user = springSecurityService.currentUser as User
        Session session = Session.get(params.session)
        UserSession us = UserSession.findByUserAndSession(user, session)
        if (!us) {
            //the session hasn't started yet
            log.debug("Attempt to register user $user with ${session.id}")
            us = new UserSession(user: user, session: session)
            if (!us.save(flush: true)) {
                log.debug(us.errors as String)
            }
        } else {
            if (us.state != "WAITING") {
                us.state = "WAITING"
                us.save(flush:true)
            }
        }

        return render(view: 'waiting_room', model: [session: session, username: user.username])
    }

    def stopWaiting() {
        log.debug("In stop waiting")
        Session s = Session.get(params.session)
        User u = springSecurityService.currentUser as User
        UserSession us = UserSession.findBySessionAndUser(s,u)
        us.stoppedWaiting = new Date()
        us.save(flush:true)

        int totalMinutes = (System.currentTimeMillis() - us.started.time) / (60*1000)

        render(view:"stop_waiting",model:[time:totalMinutes,user:u, session:s])
    }




    //Just neighbors!
    def experimentContent() {
        def sessionId = params.session
        Session session = sessionId ? Session.get(Long.parseLong(sessionId)) : null
        def model =  experimentService.getNeighborModel(session)

        return render(template: 'experiment_content', model: model)
    }

    def experiment() {

        def sessionId = params.session
        Session session = sessionId ? Session.get(Long.parseLong(sessionId)) : null
        User user = springSecurityService.currentUser as User

        //SessionAvailability availability = sessionService.checkSessionAvailability(user, session)
        if (session.state == Session.State.PENDING) {
            if (sessionService.hasTraining(user, session)) {
                return redirect(action: "startWaiting", params: [session:session.id])
            } else {
                log.debug("User ${user.username} lacks training")
                return redirect(controller: "logout", action: "index", params: [reason: "training", sessionId: session.id])
            }

        } else if (session.state == Session.State.ACTIVE) {
            if (sessionService.userInSession(user, session)) {
                def model = [myState: experimentService.getMyState(session)]+experimentService.getNeighborModel(session)
                return render(view: 'experiment', model: model)
            }
        } else if (session.state == Session.State.FINISHED) {
            redirect(action: 'finished', params: [session: session.id])
        } else {
            render(status: BAD_REQUEST)
        }
    }

    def checkExperimentRoundState() {
        Session s = Session.get(params.sessionId)
        if (s.state == Session.State.FINISHED) {
            render("finishExperiment")
        } else {
            ExperimentRoundStatus status = experimentService.getExperimentStatus(s)
            if (status.currentStatus == ExperimentRoundStatus.Status.PAUSING) {

                render("pausing")
            } else {

                redirect(action:"experimentContent",params:[session:params.sessionId])
            }
        }


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
            render(status:OK)

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
