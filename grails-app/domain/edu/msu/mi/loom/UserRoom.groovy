package edu.msu.mi.loom

class UserRoom {
    User user
    Room room
    boolean isTrainingPassed
    boolean isReady
    Integer userAlias

    static constraints = {
        userAlias nullable: true
    }
}
