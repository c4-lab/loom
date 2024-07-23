package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString(includeNames = true)

/**
 * Sessions include one trial in an experiment.  In addition to including settings regarding the subject population,
 * sessions are "active" objects, in that they maintain the state of execution.
 */
class Session {


    static final enum State {

        PENDING,  //default start state, not yet listening
        SCHEDULED,
        WAITING,  //listening, waiting for users
        ACTIVE,   //active, users playing game
        FINISHED, //finished, game has been completed
        CANCEL //cancelled by admin

    }

    //def mturkService
    def randomStringGenerator
    def sessionService

    //Bean fields
    String name
    Date created = new Date()
    Date scheduled
    Experiment exp
    Session clonedFrom
    boolean deleted = false

    SessionParameters sessionParameters




    //State management
    State state = State.PENDING
    String fullCode
    String doneCode
    String waitingCode


    Date startWaiting
    Date startActive
    Date finished
    Date cancelled

    int paid = 0
    int total = 0

    static hasMany = [mturkTasks: MturkTask]

    static constraints = {
        name blank: false
        state nullable: true
        startWaiting nullable: true
        startActive nullable: true
        finished nullable: true
        cancelled nullable: true
        mturkTasks nullable: true
        clonedFrom nullable: true
        scheduled nullable: true
   }

    Session clone() {
        Session copy = new Session()
        copy.exp = exp
        copy.sessionParameters = sessionParameters
        copy.generateCodes()
        copy.clonedFrom = retrieveCloneAncestor()
        copy.name = "${copy.clonedFrom.name} : Clone"

        return copy
    }

    Session retrieveCloneAncestor() {
        if (clonedFrom) {
            return clonedFrom.retrieveCloneAncestor()
        } else {
            return this
        }
    }

    def beforeInsert() {
        generateCodes()
    }

//    def beforeUpdate() {
//        if (isDirty('state') && state != State.SCHEDULED) {
//            sessionService.onSessionUpdate(this)
//        }
//    }
//
//    def beforeDelete() {
//        sessionService.onSessionDelete(this)
//    }

    def generateCodes() {
        if (!randomStringGenerator) {
            throw new RuntimeException("Missing autowired service in Session object")
        }
        if (!fullCode) {
            fullCode = randomStringGenerator.generateLowercase(12)
        }
        if (!doneCode) {
            doneCode = randomStringGenerator.generateLowercase(12)
        }
        if (!waitingCode) {
            waitingCode = randomStringGenerator.generateLowercase(12)
        }

    }
    /**
     * Mysteriously named to reduce typing; simply looks up parameter in the underlying session params object
     * @param prop
     */
    def sp(String prop) {
        sessionParameters.defaultGetter(prop)
    }

    def allConstraintTests() {
        (Collection<ConstraintTest>)sessionParameters.safeGetConstraintTests()
    }

    def countByHitStatus(String status) {
        mturkTasks.sum{ MturkTask task ->
            if (status) {
                task.hits.count { MturkHIT hit ->
                    hit.lastKnownStatus == status
                }
            } else {
                task.hits.size()
            }

        }
    }


}


