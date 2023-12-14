package edu.msu.mi.loom

class UserSessionPresence {

    Date lastSeen = new Date()
    boolean missing = false

    static constraints = {
    }

    static belongsTo = [userSession: UserSession]

}
