package edu.msu.mi.loom

class UserSession {

    def randomStringGenerator

    User user
    Session session
    String userAlias
    String completionCode
    String state = "WAITING"


    static constraints = {
        userAlias nullable: true
        completionCode nullable: true
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

    def beforeInsert = {
        if (!completionCode) {
            completionCode = randomStringGenerator.generateLowercase(12)
        }
    }
}
