package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Slf4j
@Secured(["ROLE_USER","ROLE_MTURKER"])
//@Secured('permitAll')
class SessionController {
    static allowedMethods = [
            submitTraining: 'POST'
    ]

    def experimentService
    def springSecurityService
    def sessionService
    def adminService
    def networkGenerateService
    def mturkService

    def startWaiting() {

        User user = springSecurityService.currentUser as User

        Session session = Session.get(params.session)

        String assignmentId = params.assignmentId

        UserSession us = UserSession.findByUserAndSession(user, session)


        if (!us) {
            //the session hasn't started yet
            log.debug("Attempt to register user $user with ${session.id}")
            us = new UserSession(user: user, session: session, started: new Date(), assignmentId: assignmentId)
            if (!us.save(flush: true)) {
                log.debug(us.errors as String)
            }
        } else if(us && us.assignmentId != assignmentId){
            return render(view: 'duplicate_us')
        }

        if(sessionService.reachMaximumUser(session)) {
            int count = UserSession.countBySession(session)
            if(!ExperimentInitialUserStory.findByExperiment(session.exp)){
                HashMap<String, List<String>> nodeStoryMap = networkGenerateService.generateGraph(session.exp, count)
                if (nodeStoryMap){

                    adminService.setExperimentNetwork(nodeStoryMap, session.exp.id as int)
                }
            }


            experimentService.kickoffSession(session)
//            if (sessionService.userInSessionRun(user, session)) {
//                def model = [myState: experimentService.getMyState(session)]+experimentService.getNeighborModel(session)
//                return render(view: 'experiment', model: model+[uiflag: session.exp.uiflag as int, assignmentId: assignmentId])
//            }
//            else {
//                return redirect(controller: "logout", action: "index", params: [reason: "session be gone", sessionId: session.id])
//            }
        }
        else {
            if (us.state != "WAITING") {
                us.state = "WAITING"
                us.started = new Date()
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
        int totalMinutes = (System.currentTimeMillis() - us.started.time) / (60*1000)
        us.started = null
        us.state = "STOP"
        us.wait_time += totalMinutes
        us.save(flush:true)
        render(view:"stop_waiting",model:[time:totalMinutes,user:u, session:s])


    }

    //Just neighbors!
    def experimentContent() {
        def sessionId = params.session
        Session session = sessionId ? Session.get(Long.parseLong(sessionId)) : null
        def model =  experimentService.getNeighborModel(session)

        return render(template: 'experiment_content', model: model+[uiflag:session.exp.uiflag as int])
    }


    def experiment() {

        def sessionId = params.session
        Session session = sessionId ? Session.get(Long.parseLong(sessionId)) : null
        User user = springSecurityService.currentUser as User

        if(user && session){

//            sessionService.reachMaximumUser(session)
            if (session.state == Session.State.PENDING) {
                if (sessionService.hasTraining(user, session)) {

                    return redirect(action: "startWaiting", params: [session:session.id])
                } else {
                    log.debug("User ${user.username} lacks training")
                    return redirect(controller: "logout", action: "index", params: [reason: "you are not trained", sessionId: session.id])
                }

            } else if (session.state == Session.State.ACTIVE) {

                if (sessionService.userInSessionRun(user, session)) {
                    def model = [myState: experimentService.getMyState(session)]+experimentService.getNeighborModel(session)
                    return render(view: 'experiment', model: model+[uiflag: session.exp.uiflag as int])
                } else {

                    return redirect(controller: "logout", action: "index", params: [reason: "The session is full", sessionId: session.id])
                }

            } else if (session.state == Session.State.FINISHED) {
                if (sessionService.userInSessionRun(user, session)) {
                    log.debug("User finished...")
                    boolean isTurker = false
                    def role = UserRole.findByUser(springSecurityService.currentUser as User).role.authority
                    if(role==Roles.ROLE_MTURKER.name){
                        isTurker = true
                    }
                    redirect(action: 'finishExperiment', params: [session: session.id, isTurker:isTurker])
                } else {
                    return redirect(controller: "logout", action: "index", params: [reason: "The session is done", sessionId: session.id])
                }


            } else if (session.state == Session.State.CANCEL){


                UserSession us = UserSession.findBySessionAndUser(session,user)
                int totalMinutes = 0
                if(us){
                    us.stoppedWaiting = new Date()
                    if(us.started){
                        totalMinutes = (System.currentTimeMillis() - us.started.time) / (60*1000)
                    }

                    us.started = null
                    us.save(flush:true)
                }

                render(view:"cancel",model:[time:totalMinutes,user:user, session:session])
//                render(view: 'cancel')

            } else {
                render(status: BAD_REQUEST)
            }
        }else{
            return redirect(action: "startWaiting", params: [session:session.id])

        }


    }

    def checkExperimentRoundState() {
        Session s = Session.get(params.sessionId)
        ExperimentRoundStatus status = experimentService.getExperimentStatus(s)
        if (s.state == Session.State.FINISHED || status?.currentStatus == ExperimentRoundStatus.Status.FINISHED) {

            render("finishExperiment")
        } else {

            if (status?.currentStatus == ExperimentRoundStatus.Status.PAUSING) {

                render("pausing")
            } else {

                redirect(action:"experimentContent",params:[session:params.sessionId]+[uiflag: s.exp.uiflag as int])
            }
        }

    }

    def checkExperimentReadyState() {
        def session = Session.get(params.session)
        def user = springSecurityService.currentUser as User
        if (session) {
            if (session.state == Session.State.PENDING) {
                log.debug("${user.username} Still waiting")
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
            log.debug("${user.username} submitted round ${roundNumber}")
            sessionService.saveUserStory(session, roundNumber, submittedTails, user)
            render(status:OK)

        } else {
            render(status: BAD_REQUEST)
        }
    }

    def finishExperiment() {
        def session = Session.get(params.session)

        boolean isTurker = false
        def role = UserRole.findByUser(springSecurityService.currentUser as User).role.authority
        if(role==Roles.ROLE_MTURKER.name){
            isTurker = true
        }
        UserSession us = UserSession.findByUserAndSession(springSecurityService.currentUser as User, session)
        List scores = UserRoundStory.findAllBySessionAndUserAlias(session,us.userAlias).sort {it.round}.score


        if (us.isActive()) {
            us.state = "COMPLETE"
            us.save(flush: true)
        }

        if (session) {
            render(view: 'finish', model: [scores: scores, completionCode: us.completionCode, isTurker:isTurker])
            return
        }

        render(status: BAD_REQUEST)

    }



}
