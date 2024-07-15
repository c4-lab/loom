package edu.msu.mi.loom

import com.amazonaws.mturk.requester.QualificationRequirement
import grails.transaction.Transactional
import org.springframework.transaction.annotation.Propagation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Transactional
class SessionService {

    def springSecurityService
    def experimentService
    def mturkService

    def launchSession(Session session,MturkTask task) {
        Session.withTransaction { status ->
            try {
                session.state = Session.State.WAITING
                session.startWaiting = new Date()

                if (task) {
                    Collection<QualificationRequirement> qualRequirements = mturkService.getConstraintQualifications(session.sp("constraintTests"), task)
                    mturkService.launchMturkTask(qualRequirements, task)
                }

                if (!session.save(flush: true)) {
                    return session.errors
                }
            } catch (Exception e) {
                status.setRollbackOnly()
                log.error("Error launching session", e)
                return e
            }
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
        User user = springSecurityService.currentUser as User
        if (user) {
            updatePresence(user, false)
        }

    }

    def countMissing(List<UserSession> userSessions) {
        return userSessions.count {
            UserSessionPresence presence = UserSessionPresence.findByUser(it.user)
            return presence != null && presence.missing
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    def updatePresence(User user, boolean state) {

        //def user = springSecurityService.currentUser as User
        //new Exception().printStackTrace()
        if (user == null) {
            return
        } else {
            UserSessionPresence us = UserSessionPresence.findByUser(user)
            if (!us) {
                println("No user presence")
                log.warn("No user session for ${user} and ${User}")
                us = new UserSessionPresence(user: user)
                us.save(flush: true)

            } else {
                ReentrantLock lock = LockManager.getLock(us.id);
                lock.lock();
                try {
                    us.refresh()
                    us.missing = !state
                    if (!us.missing) {
                        us.lastSeen = new Date()
                    }
                    us.save(flush: true)
                } catch (Exception e) {
                    log.error("Could not update user presence: ${e.getMessage()} - ignoring")
                } finally {
                    lock.unlock();
                    LockManager.releaseLock(us.id); // Optional based on usage patterns
                }
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
