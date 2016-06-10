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
        User u = User.findByUsername(params.workerId)
        def active = UserSession.findByUser(u)?.room
        if (session?.state) {
            redirect(controller: "session", action: "experiment", params: [sessionId: active.session.id])
        } else {
            redirect(controller: "training", action: "index")
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
