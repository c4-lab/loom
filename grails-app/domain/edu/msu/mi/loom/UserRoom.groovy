package edu.msu.mi.loom

class UserRoom {
    User user
    Room room
    boolean isReady
    Integer userAlias

    static hasMany = [isTrainingPassed: Long]

    static constraints = {
        userAlias nullable: true
    }
}
