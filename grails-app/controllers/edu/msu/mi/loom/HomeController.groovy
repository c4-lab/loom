package edu.msu.mi.loom

import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_USER')
class HomeController {
    def springSecurityService
    def userService

    static allowedMethods = [
            index: 'GET',
            authenticate: 'POST'
    ]

    def index() {
        def rooms = Room.list()
        render(view: 'index', model: [rooms: rooms])
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

    def waitingRoom() {
        def roomId = params.room
        if (roomId) {
            def room = Room.get(roomId)
            if (room) {
                render(view: 'waiting', model: [room: room])
                return
            }
        }
        redirect(uri: '/not-found')
    }

    def joinRoom() {
        def roomId = params.id
        if (roomId) {
            def room = Room.get(roomId)
            def user = springSecurityService.currentUser as User
            room.addToUsers(user)
            room.save(flush: true)

            if (room.users.size() != room.userMaxCount) {
                redirect(action: 'waitingRoom', params: [room: roomId])
                return
            }
        }

        redirect(uri: '/not-found')
    }

    def stopWaiting() {

    }
}
