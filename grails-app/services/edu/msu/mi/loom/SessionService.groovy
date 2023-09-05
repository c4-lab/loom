package edu.msu.mi.loom

import com.amazonaws.mturk.requester.QualificationRequirement
import grails.transaction.Transactional

@Transactional
class SessionService {

    def springSecurityService
    def experimentService
    def mturkService

    def launchSession(Session session,MturkTask task) {
        session.state = Session.State.WAITING
        session.startWaiting = new Date()

        if (task) {
            Collection<QualificationRequirement> qualRequirements = mturkService.getConstraintQualifications(session.sp("constraintTests"))
            mturkService.launchMturkTask(qualRequirements,task)
        }

        if (!session.save(flush: true)) {
            return session.errors
        } else {
            return null
        }
    }

    def cancelSession(Session session) {

        mturkService.forceHITExpiry(session.mturkTasks as MturkTask[])

        if (session.state == Session.State.WAITING) {
            session.state = Session.State.PENDING
            session.save(flush: true)
        } else if (session.state == Session.State.ACTIVE) {
            session.state = Session.State.CANCEL
            session.cancelled = new Date()
            session.save(flush: true)
        }
    }


    def lookupUserAlias(Session session, User user) {
        UserSession.findBySessionAndUser(session,user).userAlias
    }




    def leaveAllSessions() {
        log.debug("Leaving sessions....")
        UserSession.withSession {
            UserSession.findAllByUserAndStateInList(springSecurityService.currentUser as User, [UserSession.State.ACTIVE, UserSession.State.WAITING]).each {
                it.missing = true
                it.save(flush:true)
            }
        }


    }

    def saveUserStory(Session session, int roundNumber, List<Tile> tiles, User user) {
        new UserRoundStory(time: new Date(), session: session, round: roundNumber, currentTiles: tiles, userAlias: lookupUserAlias(session, user)).save(flush: true)
        experimentService.userSubmitted(user,session, roundNumber)
    }


}
