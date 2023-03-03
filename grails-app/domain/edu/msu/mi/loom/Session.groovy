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

    static final enum Network_type {

        Lattice,Newman_Watts,Barabassi_Albert

    }

    static final enum State {

        PENDING,WAITING,ACTIVE,FINISHED,CANCEL

    }

    def mturkService
    def randomStringGenerator

    //Bean fields
    String name
    Date dateCreated
    Experiment exp
    Network_type network_type
    int m
    Float probability
    int min_degree
    int max_degree



    //State management
    State state
    String fullCode
    String doneCode
    String waitingCode


    Long startPending
    Long startActive

    int paid = 0
    int total = 0

    static hasMany = [serviceTasks: CrowdServiceTask, userConstraints: ConstraintTest]

    static constraints = {
        name blank: false
        state nullable: true
        startPending nullable: true
        startActive nullable: true


    }

    public Session clone() {
        Session copy = new Session()
        def count = count()
        copy.name = "Session ${count + 1}"
        copy.exp = exp

        return copy
    }

    def beforeInsert = {
        println("Executing before insert...")
        if (!fullCode) {
            fullCode = randomStringGenerator.generateLowercase(12)
        }
        if (!doneCode) {
            doneCode = randomStringGenerator.generateLowercase(12)
        }
        if (!waitingCode) {
            waitingCode = randomStringGenerator.generateLowercase(12)
        }

        if (serviceTasks) {
            serviceTasks.each {CrowdServiceTask cst ->
                mturkService.setBasicQualifications(cst.serviceCredentials)
                userConstraints.each { ConstraintTest ci ->
                    mturkService.verifyQualification(cst.serviceCredentials, ci)
                }

            }
        }

    }


}


