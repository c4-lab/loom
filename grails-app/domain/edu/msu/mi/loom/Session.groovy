package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j

import java.text.Normalizer

@Slf4j
@ToString(includeNames = true)
class Session {
    String name
    Date dateCreated
    String url

    static hasMany = [experiments: Experiment, simulations: Simulation, trainings: Training]

    static constraints = {
        name blank: false, unique: true
        url blank: false, unique: true
        experiments nullable: true
        simulations nullable: true
        trainings nullable: true
    }

    public Session clone() {
        Session copy = new Session()
        def count = count()
        copy.name = "Session_${count + 1}"
        copy.url = createExperimentUrl(copy.name)

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

    private static String createExperimentUrl(String sessionName) {
        def expUrl = Normalizer.normalize(sessionName?.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-")
                .replace("--", "-").replace("--", "-")
                .replaceAll('[^a-z0-9]+$', "")
                .replaceAll("^[^a-z0-9]+", "")

        log.info("Generated url: " + "/" + expUrl)

        "/" + expUrl
    }
}
