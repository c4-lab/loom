package edu.msu.mi.loom

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER', 'ROLE_ADMIN'])
class HomeController {
    def springSecurityService
    def userService
    def experimentService
    def statService

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

    def joinRoom() {
        def roomId = params.id
        if (roomId) {
            def room = Room.get(roomId)
            def user = springSecurityService.currentUser as User
            user.alias = "neighbour" + (room.users.size() + 1)
            room.addToUsers(user)
            room.save(flush: true)

//            Create UserStatistics for current user
            statService.createStat(room.session, user)

            redirect(action: 'training', params: [session: room.session.id])
            return
        }

        redirect(uri: '/not-found')
    }

    def training() {
        def sessionId = params.session
        session.trainingStartTime = new Date().getTime()
        if (sessionId) {
            if (session["seqNumber"]) {
                redirect(controller: 'experiment', action: 'nextTraining', params: [session: sessionId, seqNumber: session["seqNumber"]])
                return
            }
            def session = Session.get(Long.parseLong(sessionId))
            if (session) {
                def training = experimentService.getNextTraining(session)
                def tts = TrainingTask.findAllByTraining(training).tail
                render(view: 'training', model: [tts: tts, training: training])
                return
            }
        }

        redirect(uri: '/not-found')
    }

    def stopWaiting() {
        def roomId = params.id
        if (roomId) {
            def room = Room.get(roomId)
            def user = springSecurityService.currentUser as User
            user.alias = null
            room.removeFromUsers(user)
            if (room.save(flush: true)) {
                redirect(action: 'index')
                return
            }
        }

        redirect(uri: '/not-found')
    }

    def room() {
        def roomId = params.room
        if (roomId) {
            def room = Room.get(roomId)

            if (room) {
                experimentService.startExperiment(room)
                render(view: 'room', model: [room: room])
                return
            }
        }

        redirect(uri: '/not-found')
    }

    @Secured('permitAll')
    def updateRooms() {
        def rooms = Room.list()
        render(template: 'rooms', model: [rooms: rooms])
    }
}
