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
        }
        experimentService.scheduleWaitingCheck(session)

    }

    def cancelSession(Session session) {

        mturkService.forceHITExpiry(session.mturkTasks as MturkTask[])

        if (session.state == Session.State.WAITING  && UserSession.countBySession(session) ==0) {
            session.state = Session.State.PENDING
            session.save(flush: true)
        } else if (session.state in [Session.State.WAITING, Session.State.ACTIVE]) {
            session.state = Session.State.CANCEL
            session.cancelled = new Date()
            UserSession.findAllBySession(session).each {
                it.stopWaiting(UserSession.State.CANCELLED)
            }
            session.save(flush: true)

        }
    }







    def leaveAllSessions() {
        log.debug("Leaving sessions....")
        UserSession.withSession {
            UserSession.findAllByUserAndStateInList(springSecurityService.currentUser as User, [UserSession.State.ACTIVE, UserSession.State.WAITING]).each {
                it.presence.missing = true
            }
        }

    }

}
