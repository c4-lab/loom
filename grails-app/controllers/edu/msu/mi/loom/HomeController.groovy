package edu.msu.mi.loom

import grails.plugin.springsecurity.annotation.Secured

@Secured('permitAll')
class HomeController {
    def springSecurityService
    def userService

    static allowedMethods = [
            authenticate: 'POST'
    ]

    def index() {
    }

    def authenticate() {
        def username = params.j_username
        if (username) {
            def user = userService.createUser(username)
            if (user?.id) {
                springSecurityService.reauthenticate(username)
                redirect(action: "index")
            } else {
                log.error("Failed to register new user account")
            }
        }
    }
}
