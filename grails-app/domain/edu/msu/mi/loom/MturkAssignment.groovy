package edu.msu.mi.loom

class MturkAssignment {

    static constraints = {
        lastUpdate nullable: true
        lastKnownStatus nullable: true
        accepted nullable:true
        submitted nullable: true
        userTrainingSet nullable: true
        userSession nullable: true
    }

    static belongsTo = [hit: MturkHIT]

    Date created = new Date()
    Float basePaid = 0
    Float bonusPaid = 0

    String assignmentId
    Date accepted
    Date submitted
    String lastKnownStatus
    Date lastUpdate
    boolean duplicate = false

    UserTrainingSet userTrainingSet
    UserSession userSession

    def retrieveCredentials() {
        hit.task.credentials
    }

}
