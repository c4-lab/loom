package edu.msu.mi.loom

class UserSession implements Serializable{

    static final enum State {
        WAITING,  //in the waiting room
        REJECTED, //rejected because too many players
        ACTIVE,   //playing the game
        COMPLETE, //finished the game
        STOP //was waiting, stopped

    }

    def randomStringGenerator

    User user
    Session session
    String userAlias
    String completionCode
    Date started
    Date stoppedWaiting
    State state = State.WAITING
    int wait_time
    MturkAssignment mturkAssignment
    boolean missing = false
    boolean selected = false

    static mapping = {
        id composite: ['user', 'session']
    }

    static constraints = {
        userAlias nullable: true
        completionCode nullable: true
        stoppedWaiting nullable: true
        state nullable: true
        started nullable: true
        wait_time nullable: true
        mturkAssignment nullable: true
    }

    static UserSession create(User user, Session session, boolean flush = false) {
        def instance = new UserSession(user: user, session: session)
        instance.save(flush: flush, insert: true)
        instance
    }

    static UserSession create(User user, Session session, Date started, MturkAssignment mturkAssignment, boolean flush = false) {
        def instance = new UserSession(user: user, session: session, started: started, mturkAssignment: mturkAssignment)
        if (mturkAssignment) {
            mturkAssignment.userSession = instance
            mturkAssignment.save(flush: flush, insert: true)
        }
        instance.save(flush: flush, insert: true)
        instance
    }



    def beforeInsert = {
        if (!completionCode) {
            completionCode = randomStringGenerator.generateLowercase(12)
        }
//        if (!started) {
//            started = new Date()
//        }
    }
}
