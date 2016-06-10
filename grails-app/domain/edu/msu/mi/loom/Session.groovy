package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j

import java.text.Normalizer

@Slf4j
@ToString(includeNames = true)
class Session {

    static final enum State {

        PENDING,ACTIVE,FINISHED

    }

    String name
    String type
    Date dateCreated
    TrainingSet trainingSet
    Experiment experiment
    State state


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
        copy.experiment = experiment

        return copy
    }


}


