package edu.msu.mi.loom

class SessionType {

    /**
     * Describes the policy used to populate a session
     */
    static final enum State {
        MIXED,  //balanced across different constraints
        SINGLE  //single group

    }

    State state = State.SINGLE
    List<ConstraintTest> constraintTests = []

    static hasMany = [constraintTests:ConstraintTest]
    static constraints = {
    }

    static SessionType create(String type, List<ConstraintTest> constraintTests) {
        def instance = new SessionType()
        instance.state = State.valueOf(type)
        instance.constraintTests = constraintTests
        instance.save(flush: true, insert: true)
        instance
    }

    String toString() {
        "${state}${state==State.MIXED?constraintTests:""}"
    }
}
