package edu.msu.mi.loom

class UserSession {

    def randomStringGenerator

    User user
    Session session
    String userAlias
    String completionCode
    Date started
    Date stoppedWaiting
    String state = "WAITING"


    static constraints = {
        userAlias nullable: true
        completionCode nullable: true
        stoppedWaiting nullable: true
        state nullable: true
    }


    boolean isWaiting() {
        state=="WAITING"
    }

    boolean isActive() {
        state=="ACTIVE"
    }

    boolean isMissing() {
        state=="MISSING"
    }

    boolean isCompleted() {
        state=="COMPLETED"
    }

    def beforeValidate = {
        if (!completionCode) {
            completionCode = randomStringGenerator.generateLowercase(12)
        }
        if (!started) {
            started = new Date()
        }
    }
}
