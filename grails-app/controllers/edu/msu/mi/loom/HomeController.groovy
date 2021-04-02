package edu.msu.mi.loom
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

@Slf4j
@Secured(['ROLE_USER', 'ROLE_ADMIN'])
class HomeController {
    def springSecurityService
    def userService
    def experimentService
    def statService
    def roomService

    static allowedMethods = [
            index       : 'GET',
            authenticate: 'POST'
    ]


    def index() {
        def user = springSecurityService.currentUser as User
        log.debug("In home/index")
        List uts = UserTrainingSet.findAllByUser(user)
        int flag = 1
        for(ut in uts){
            if(!uts.complete){
                flag = 0
            }
        }
        List userSessions = UserSession.findAllByUser(user)
        userSessions = userSessions.sort {it.state}
        userSessions = userSessions.sort {UserSession.countBySession(it.session)}

//        def orig = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST")
//        def original = orig?.requestURL

//        if(params.session)
        if (userSessions) {
            UserSession userSession = userSessions.first()
            redirect(controller: "session", action: "experiment", params: [session: userSession?.session?.id])
        } else {
//            redirect(controller: "training", action: "index")
                render(view: 'nosession')
        }
    }


    @Secured('permitAll')
    def authenticate() {
        def username = params.j_username
        if (username) {
            def user = userService.createUser(username)
            if (user?.id) {
                springSecurityService.reauthenticate(username)
                redirect(action: "index")
            } else {
                log.error("Failed to register new user account")
                flash.message = "page.auth.already.exists"
                redirect(uri: "/")
            }
        } else {
            redirect(uri: "/")
        }
    }


    @Secured('permitAll')
    def joinByEmail() {
        def user = userService.createUserWithRandomUsername()
        if (user?.id) {
            springSecurityService.reauthenticate(user.username)
            redirect(action: "joinRoom", params: [id: params.id])
        } else {
            log.error("Failed to register new user account")
            flash.message = "page.auth.already.exists"
            redirect(uri: "/")
        }
    }
}
