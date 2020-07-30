package edu.msu.mi.loom

import grails.transaction.Transactional

@Transactional
class SessionService {

    def springSecurityService
    def experimentService

    def launchSession(Session session) {
            Session s
            Session.withNewTransaction {
                s = Session.get(session.id)
                s.state = Session.State.PENDING
                s.save(flush: true)
                //TODO why is this not the same as the experiment status?

            }

        experimentService.kickoffSession(s)

    }


    def lookupUserAlias(Session session, User user) {
        UserSession.findBySessionAndUser(session,user).userAlias
    }

    def assignAliasesAndMakeActive(Session session) {
        List<String> aliases = session.exp.initialStories.collect {it.alias}
        Collections.shuffle(aliases)
        List<UserSession> sessions = UserSession.findAllBySessionAndState(session,"WAITING").sort{it.started}

        aliases.each {
            UserSession s = sessions.pop()
            s.userAlias = it
            s.state="ACTIVE"
            s.save(flush:true)
        }
        session.state = Session.State.ACTIVE
        session.save(flush:true)

    }


    def leaveAllSessions() {
        log.debug("Leaving sessions....")
        UserSession.withSession {
            UserSession.findAllByUserAndStateInList(springSecurityService.currentUser as User, ["ACTIVE", "WAITING"]).each {
                it.state = "MISSING"
                it.save(flush:true)
            }
        }


    }

    def saveUserStory(Session session, int roundNumber, List<Tile> tiles, User user) {
        new UserRoundStory(time: new Date(), session: session, round: roundNumber, currentTails: tiles, userAlias: lookupUserAlias(session, user)).save(flush: true)
        experimentService.userSubmitted(user,session, roundNumber)
    }

    def userInSessionRun(User u, Session s) {
        UserSession.findByUserAndSessionAndUserAliasIsNotNull(u,s)
    }

    def hasTraining(User u, Session s) {
        u && s && UserTrainingSet.countByUserAndTrainingSetAndComplete(u,s.trainingSet,true)
    }
}
