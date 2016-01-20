package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class StatService {
    def springSecurityService

    def createStat(Session session, User user) {
        def userStat = UserStatistic.findOrCreateBySessionAndUser(session, user)
        if (userStat.save(flush: true)) {
            log.info("Created user stat for user with id ${user.id}")
            return userStat
        } else {
            log.error("Unable to create user stat.")
            log.error("Validation error: ${userStat.errors}")
            return userStat
        }
    }

    def setTrainingTime(Session session, def trainingTime) {
        def stat = UserStatistic.findBySessionAndUser(session, currentUser)

        if (stat) {
            stat.trainingTime = trainingTime / 1000
            stat.save(flush: true)
        }
    }

    def setSimulationScore(Session session, def simulationScore) {
        def stat = UserStatistic.findBySessionAndUser(session, currentUser)

        if (stat) {
            stat.setSimulationScore(simulationScore)
            stat.save(flush: true)
        }
    }

    private User getCurrentUser() {
        return springSecurityService.currentUser as User
    }
}
