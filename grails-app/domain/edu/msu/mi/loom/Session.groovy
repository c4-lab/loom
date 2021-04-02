package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString(includeNames = true)
class Session {

    static final enum State {

        PENDING,ACTIVE,FINISHED,CANCEL

    }

    def randomStringGenerator

    String name
    String type
    Date dateCreated
    TrainingSet trainingSet
    Experiment exp
    State state
    String fullCode
    String doneCode
    String waitingCode
    Long startPending
    Long startActive
    int paid = 0
    int total = 0

    List<String> HITId = new ArrayList<>()

    static hasMany = [HITId: String]

    static constraints = {
        name blank: false
        trainingSet nullable: true
        state nullable: true
        startPending nullable: true
        startActive nullable: true


    }

    public Session clone() {
        Session copy = new Session()
        def count = count()
        copy.name = "Session ${count + 1}"
        copy.trainingSet = trainingSet
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

    }


}


