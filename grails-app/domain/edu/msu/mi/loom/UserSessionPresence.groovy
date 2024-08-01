package edu.msu.mi.loom

class UserSessionPresence {

    Date lastSeen = new Date()
    User user
    Long version

    boolean missing = false

    static constraints = {
    }
}
