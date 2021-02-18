package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j

@Slf4j
@Transactional
class StatService {
    def springSecurityService

    def createStat(User user, Session session) {

        def userStat = new UserStatistic(session: session, user: user)
        if (userStat.save(flush: true)) {
            log.info("Created user stat for user with id ${user.id}")
            return userStat
        } else {
            log.error("Unable to create user stat.")
            log.error("Validation error: ${userStat.errors}")
            return userStat
        }
    }



    private User getCurrentUser() {
        return springSecurityService.currentUser as User
    }
}
