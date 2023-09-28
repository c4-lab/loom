package edu.msu.mi.loom

class UserSessionPresence {

    Date lastSeen = new Date()
    boolean missing = false

    static constraints = {
    }

    static belongsTo = [userSession: UserSession]

    def beforeUpdate() {
        println("---> Before update in $this")
        if (this.isDirty()) {
            println("---> is in fact dirty")

        }
    }
}
