package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class StatService {
    def springSecurityService

    def createStat(User user, Room room) {
        Session session = room.session
        def userStat = new UserStatistic(session: session, user: user, room: room)
        if (userStat.save(flush: true)) {
            log.info("Created user stat for user with id ${user.id}")
            return userStat
        } else {
            log.error("Unable to create user stat.")
            log.error("Validation error: ${userStat.errors}")
            return userStat
        }
    }

    def setTrainingTime(Session session, def trainingTime, Room room) {
        def stat = UserStatistic.findBySessionAndUserAndRoom(session, currentUser, room)

        if (stat) {
            stat.trainingTime = trainingTime / 1000
            stat.save(flush: true)
        }
    }

    def setSimulationScore(Session session, def simulationScore, Room room) {
        def stat = UserStatistic.findBySessionAndUserAndRoom(session, currentUser, room)

        if (stat) {
            stat.setSimulationScore(simulationScore)
            stat.save(flush: true)
        }
    }

    private User getCurrentUser() {
        return springSecurityService.currentUser as User
    }
}
