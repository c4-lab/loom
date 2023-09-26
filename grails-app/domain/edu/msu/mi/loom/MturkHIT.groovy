package edu.msu.mi.loom

class MturkHIT {

    static constraints = {
        lastUpdate nullable: true
    }

    static belongsTo = [task: MturkTask]
    static hasMany = [assignments: MturkAssignment]

    String hitId
    String hitTypeId
    Date created = new Date()
    Date expires
    String lastKnownStatus
    String url
    Date lastUpdate


}
