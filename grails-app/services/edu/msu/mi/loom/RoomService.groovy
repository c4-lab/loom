package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class RoomService {

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
}
