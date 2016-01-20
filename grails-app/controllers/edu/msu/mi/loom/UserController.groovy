package edu.msu.mi.loom

import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

@Slf4j
@Secured(['ROLE_USER'])
class UserController {
    def userService
    def springSecurityService

    @Secured('permitAll')
    def registration() {
        if (request.method == 'GET') {
            render(view: 'registration')
        } else {
            def username = params.username
            def password = params.password
            def confirmPass = params.confPassword

            def model = userService.createUser(username, password, confirmPass)

            if (model.user?.id) {
                springSecurityService.reauthenticate(username, password)
                redirect(controller: 'home')
            } else {
                log.error("Failed to register new user account.")
                render(view: 'registration', model: [user: model.user, message: model.message])
            }
        }
    }
}
