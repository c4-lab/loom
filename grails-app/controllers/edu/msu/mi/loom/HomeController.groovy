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
        def rooms = Room.list()
        render(view: 'index', model: [rooms: rooms])
    }

    def leaveExperiment() {
        def roomId = params.room
        def currentUser = springSecurityService.currentUser as User
        if (roomId) {
            def room = Room.get(roomId)

            def userRoom = UserRoom.findByRoomAndUser(room, currentUser)
            if (userRoom) {
                userRoom.delete(flush: true)
                if (UserRoom.countByRoomAndUserAliasIsNotNull(room) == 0) {
                    room.delete(flush: true)
                }
                redirect(action: 'index')
            }
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

            roomService.joinRoom(room, user)

//            Create UserStatistics for current user
            statService.createStat(room.session, user)

            redirect(action: 'training', params: [session: room.session.id, trainingNumber: 0])
            return
        }

        redirect(uri: '/not-found')
    }

    def rejoinRoom() {
        def roomId = params.id
        if (roomId) {
            def room = Room.get(roomId)
            def user = springSecurityService.currentUser as User
            def userRoom = UserRoom.findByRoomAndUser(room, user)
            roomService.joinRoom(room, user)

            if (userRoom.isTrainingPassed.size() < room.session.trainings.size()) {
                redirect(action: 'training', params: [session: room.session.id, trainingNumber: userRoom.isTrainingPassed.size()])
            } else {
                redirect(controller: 'experiment', action: 'simulation', params: [roundNumber: 0, session: room.session.id])
            }
            return
        }

        redirect(uri: '/not-found')
    }

    def training() {
        def sessionId = params.session
        def trainingNumber = params.trainingNumber
        session.trainingStartTime = new Date().getTime()
        if (sessionId) {
            if (trainingNumber) {
                redirect(controller: 'experiment', action: 'nextTraining', params: [session: sessionId, seqNumber: trainingNumber])
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
            roomService.leaveRoom(room)
            redirect(action: 'index')
            return
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
