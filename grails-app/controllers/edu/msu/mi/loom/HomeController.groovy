package edu.msu.mi.loom

import grails.plugin.springsecurity.annotation.Secured

@Secured('IS_AUTHENTICATED_ANONYMOUSLY')
class HomeController {

    def index() {
        redirect(controller: 'login', action: 'auth')
    }
}
