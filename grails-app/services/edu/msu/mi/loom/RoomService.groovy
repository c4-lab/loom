package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

import java.text.Normalizer

@Slf4j
@Transactional
class RoomService {
    def springSecurityService

    def createRoom(Session session) {
        def room = new Room(name: 'Room ' +
                (Room.count() + 1), userMaxCount: session.experiments.getAt(0).userCount, session: session, url: createRoomUrl('Room ' + (Room.count() + 1)))

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
        def userRoom = UserRoom.findOrCreateByRoomAndUser(room, user)
        userRoom.userAlias = getAlias(room)

        if (userRoom.save(flush: true)) {
            log.info("UserRoom with id ${userRoom.id} has been created.")
        }
    }

    def changeTrainingState(Room room, Training training) {
        def userRoom = UserRoom.findByRoomAndUser(room, currentUser)

        if (userRoom) {
            userRoom.isTrainingPassed.add(training.id)
            userRoom.save(flush: true)
        }
    }

    def changeSimulationAndUserState(Room room) {
        def userRoom = UserRoom.findByRoomAndUser(room, currentUser)

        if (userRoom) {
            userRoom.isReady = true
            userRoom.save(flush: true)
        }
    }

    def leaveAllRooms() {
        def userRooms = UserRoom.findAllByUser(currentUser)

        if (userRooms) {
            userRooms.each { userRoom ->
                userRoom.userAlias = null
                userRoom.save(flush: true)
                reorderAliases(userRoom.room)
                log.info("User with id ${currentUser.id} left the room.")
            }
        }
    }

    def leaveRoom(Room room) {
        def userRoom = UserRoom.findByRoomAndUser(room, currentUser)

        if (userRoom) {
            userRoom.userAlias = null
            userRoom.save(flush: true)
            reorderAliases(room)
            log.info("User with id ${currentUser.id} left the room with id ${room.id}.")
        }
    }

    private def getAlias(Room room) {
        def userCount = UserRoom.countByRoomAndUserAliasIsNotNull(room)
        def alias = userCount + 1
        return alias
    }

    private User getCurrentUser() {
        return springSecurityService.currentUser as User
    }

    private void reorderAliases(Room room) {
        def userRooms = UserRoom.findAllByRoomAndUserAliasIsNotNull(room)

        userRooms.eachWithIndex { UserRoom userRoom, Integer i ->
            userRoom.userAlias = i + 1
            userRoom.save(flush: true)
        }
    }

    private static String createRoomUrl(String roomName) {
        def roomUrl = Normalizer.normalize(roomName?.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-")
                .replace("--", "-").replace("--", "-")
                .replaceAll('[^a-z0-9]+$', "")
                .replaceAll("^[^a-z0-9]+", "")

        log.info("Generated url: " + "/" + roomUrl)

        roomUrl
    }
}
