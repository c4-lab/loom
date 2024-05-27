package edu.msu.mi.loom

import com.amazonaws.mturk.requester.QualificationRequirement
import grails.transaction.Transactional
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Transactional
class SessionService {

    def springSecurityService
    def experimentService
    def mturkService

    def launchSession(Session session,MturkTask task) {
        session.state = Session.State.WAITING
        session.startWaiting = new Date()

        if (task) {
            Collection<QualificationRequirement> qualRequirements = mturkService.getConstraintQualifications(session.sp("constraintTests"),task)
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
            UserSession.findAllBySessionAndStateInList(session,[UserSession.State.ACTIVE,UserSession.State.STOP,UserSession.State.WAITING]).each {
                it.stopWaiting(UserSession.State.CANCELLED)
            }
            session.save(flush: true)

        }
    }







    def leaveAllSessions() {
        log.debug("Leaving sessions....")
        UserSession.withSession {
            UserSession.findAllByUserAndStateInList(springSecurityService.currentUser as User, [UserSession.State.ACTIVE, UserSession.State.WAITING]).each {
                updatePresence(it.session,false)
            }
        }

    }

    def updatePresence(Session session, boolean state) {
        def user = springSecurityService.currentUser as User
        UserSession us = UserSession.findByUserAndSession(user, session)
        if (!us) {
            log.warn("No user session for ${user} and ${session}")
        } else {
            ReentrantLock lock = LockManager.getLock(us.presence.id);
            lock.lock();
            try {
                us.presence.refresh()
                us.presence.missing = !state
                if (!state) {
                    us.presence.lastSeen = new Date()
                }
                us.presence.save()
            } catch(Exception e) {
                log.warn("Could not update user presence: ${e.getMessage()} - ignoring")
            } finally {
                lock.unlock();
                LockManager.releaseLock(us.presence.id); // Optional based on usage patterns
            }
            synchronized (us) {

            }
        }
    }



    public static class LockManager {
        private static final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

        public static ReentrantLock getLock(Long id) {
            return lockMap.computeIfAbsent(id, { k -> new ReentrantLock() });
        }

        public static void releaseLock(Long id) {
            ReentrantLock lock = lockMap.get(id);
            if (lock != null && !lock.hasQueuedThreads()) {
                lockMap.remove(id, lock); // Clean up to prevent memory leak
            }
        }
    }
}
