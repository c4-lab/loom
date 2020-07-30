package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString(includeNames = true)
class Session {

    static final enum State {

        PENDING,ACTIVE,FINISHED

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


    static constraints = {
        name blank: false, unique: true
        trainingSet nullable: true
        state nullable: true
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


