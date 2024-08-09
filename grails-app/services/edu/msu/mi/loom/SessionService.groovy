package edu.msu.mi.loom

import com.amazonaws.mturk.requester.QualificationRequirement
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import org.quartz.JobExecutionContext
import org.springframework.transaction.annotation.Propagation
import org.springframework.validation.Errors;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
//import org.quartz.JobExecutionContext
import org.springframework.scheduling.annotation.Scheduled

@Slf4j
@Transactional
class SessionService {

    def springSecurityService
    def experimentService
    def mturkService




    def scheduleSession(Session session, MturkTask task, Date scheduledDateTime) {
        Session.withTransaction { status ->
            try {
                session.state = Session.State.SCHEDULED
                session.scheduled = scheduledDateTime
                if (!session.save(flush: true)) {
                    return session.errors
                }
            } catch (Exception e) {
                status.setRollbackOnly()
                log.error("Error scheduling session", e)
                return e
            }
        }
        return null
    }

    def launchSession(Session session, MturkTask task) {
        println("Set session ${session.name} to waiting")
        Session.withTransaction { status ->
            try {
                session.state = Session.State.WAITING
                session.startWaiting = new Date()

                if (task) {
                    List<QualificationRequirement> qualRequirements = mturkService.getConstraintQualifications(session.sp("constraintTests"), task) as List<QualificationRequirement>
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
        log.debug("Scheduling waiting check on ${session.name}")
        experimentService.scheduleWaitingCheck(session.id)
        return null
    }

    /**
     * This is called by the quartz scheduling service
     * @see edu.msu.mi.loom.CheckScheduledSessionsJob
     * @param context
     * @return
     */
    @Transactional
    def checkAndUpdateScheduledSessions(JobExecutionContext context) {

        log.debug("Running Scheduled Tasks")
        Session.findAllByState(Session.State.SCHEDULED).each { session ->
            if (session.scheduled < new Date()) {
                log.debug("Would launch session ${session.name}")
                launchSession(session, session.mturkTasks.find())
            }
        }
    }

    def cancelScheduledSession(Long sessionId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.remove(sessionId)
        if (scheduledTask) {
            scheduledTask.cancel(false)
        }
    }



    def cancelSession(Session session) {

        mturkService.forceHITExpiry(session.mturkTasks as MturkTask[])

        if (session.state == Session.State.SCHEDULED || (session.state == Session.State.WAITING  && UserSession.countBySession(session) ==0)) {
            session.state = Session.State.PENDING
            session.scheduled = null
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
            UserSessionPresence.withNewSession { session ->
                UserSessionPresence us = UserSessionPresence.findByUser(user)
                if (!us) {
                    log.debug("Creating new user presence for ${user}")
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
                        session.flush()
                    } catch (Exception e) {
                        log.error("Could not update user presence: ${e.getMessage()} - ignoring")
                        session.clear()
                    } finally {
                        lock.unlock();
                        LockManager.releaseLock(us.id); // Optional based on usage patterns
                    }
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
