package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class RoomService {
    def springSecurityService

    def createRoom(Session session) {
        def room = new Room(name: 'Room ' +
                (Room.count() + 1), userMaxCount: session.experiments.getAt(0).userCount, session: session)

        if (room.save(flush: true)) {
            log.debug("New room has been created with id " + room.id + ".")
            return room
        } else {
            log.error("Room creation attempt failed")
            log.error(room?.errors?.dump())
            return null;
        }
    }

    def deleteRoom(def roomId) {
        def room = Room.get(roomId)
        room.delete(flush: true)
    }

    def joinRoom(Room room, User user) {
        def userRoom = new UserRoom(room: room, user: user, userAlias: getAlias(room))

        if (userRoom.save(flush: true)) {
            log.info("UserRoom with id ${userRoom.id} has been created.")
        }
    }

    def changeTrainingState(Room room) {
        def userRoom = UserRoom.findByRoomAndUser(room, currentUser)

        if (userRoom) {
            userRoom.isTrainingPassed = true
            userRoom.save(flush: true)
        }
    }

    def changeSimulationAndUserState(Room room) {
        def userRoom = UserRoom.findByRoomAndUser(room, currentUser)

        if (userRoom) {
            userRoom.isSimulationPassed = true
            userRoom.isReady = true
            userRoom.save(flush: true)
        }
    }

    private def getAlias(Room room) {
        def userCount = UserRoom.countByRoom(room)
        def alias = userCount + 1
        return alias
    }

    private User getCurrentUser() {
        return springSecurityService.currentUser as User
    }
}
