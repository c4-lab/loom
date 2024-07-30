package edu.msu.mi.loom

class UserSession implements Serializable {

    static final enum State {
        WAITING,  //in the waiting room
        REJECTED, //rejected because too many players
        ACTIVE,   //playing the game
        COMPLETE, //finished the game
        STOP, //was waiting, stopped
        CANCELLED //session was cancelled

    }

    def randomStringGenerator

    User user
    Session session
    String userAlias
    String completionCode
    Date started
    Date stoppedWaiting


    State state = State.WAITING
    int wait_time = 0
    MturkAssignment mturkAssignment

    boolean selected = false

    static mapping = {
        id composite: ['user', 'session']
    }

    static constraints = {
        userAlias nullable: true
        completionCode nullable: true
        stoppedWaiting nullable: true
        state nullable: true, lazy: false
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

        //TODO 07-22-24
        //TODO I've encountered a very strange problem here where the map constructor
        //TODO is failing for the session object when it attempts to establish he association
        //TODO I don't know what the problem is, but the following seems to fix it
        //TODO We might try looking into Hibernate SQL logging to see if there is a
        //TODO warning or error being squashed somewhere
        instance.session = session
        if (mturkAssignment) {
            mturkAssignment.userSession = instance
        }

        if (!instance.save(flush: flush, insert: true)) {
            print("Failed to save UserSession: ${instance.errors}")
            return null
        }

        instance
    }
    /**
     * Updates waiting time, clears the start time, and optionally sets a new time
     * @param newState
     * @return
     */
    def stopWaiting(State newState = null) {
        if (state == State.WAITING && started) {
            stoppedWaiting = new Date()
            wait_time += (stoppedWaiting.time - started.time) / (60 * 1000)
            started = null
            if (newState) {
                state = newState
            }
        }
    }

    def cancel() {
        if (state == State.WAITING && started) {
            stopWaiting()
        }

        state = State.CANCELLED
    }

    String toString() {
        return "UserSession: ${user}:${session.id}:${state}"
    }


//    def beforeInsert = {
//        if (!completionCode) {
//            completionCode = randomStringGenerator.generateLowercase(12)
//        }
////        if (!started) {
////            started = new Date()
////        }
//    }
}
