package edu.msu.mi.loom

import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification

@Build([User])
@TestFor(HomeController)
class HomeControllerSpec extends Specification {
    void "test the index action"() {
    }

    void "test the authenticate action when username is not found"() {
        setup:
        request.method = 'POST'
        def user = User.build(username: 'e-mate')
        controller.springSecurityService = [reauthenticate: { def username -> true }]
        controller.userService = [createUser: { def username -> return user }]

        when:
        controller.authenticate()

        then:
        response.redirectedUrl == '/'
    }

    void "test the authenticate action when user is created"() {
        setup:
        request.method = 'POST'
        controller.params.j_username = 'e-mate'
        def user = User.build(username: 'e-mate')
        controller.springSecurityService = [reauthenticate: { def username -> true }]
        controller.userService = [createUser: { def username -> return user }]

        when:
        controller.authenticate()

        then:
        response.redirectedUrl == '/home/index'
    }

    void "test the authenticate action when user is not created"() {
        setup:
        request.method = 'POST'
        controller.params.j_username = 'e-mate'
        controller.springSecurityService = [reauthenticate: { def username -> true }]
        controller.userService = [createUser: { def username -> return null }]

        when:
        controller.authenticate()

        then:
        response.redirectedUrl == '/'
    }
}
