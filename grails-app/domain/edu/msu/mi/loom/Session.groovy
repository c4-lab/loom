package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j

import java.text.Normalizer

@Slf4j
@ToString(includeNames = true)
class Session {
    String name
    Date dateCreated

    static hasMany = [experiments: Experiment, simulations: Simulation, trainings: Training]

    static constraints = {
        name blank: false, unique: true
        experiments nullable: true
        simulations nullable: true
        trainings nullable: true
    }

    public Session clone() {
        Session copy = new Session()
        def count = count()
        copy.name = "Session_${count + 1}"

        for (Experiment experiment : experiments) {
            copy.addToExperiments(experiment.clone())
        }

        for (Simulation simulation : simulations) {
            copy.addToSimulations(simulation.clone())
        }

        for (Training training : trainings) {
            copy.addToTrainings(training.clone())
        }

        return copy
    }
}
