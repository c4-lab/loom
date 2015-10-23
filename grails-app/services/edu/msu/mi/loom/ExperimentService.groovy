package edu.msu.mi.loom

import grails.transaction.Transactional

@Transactional
class ExperimentService {

    def deleteExperiment(def id, def type) {
        if (id) {
            def experiment
            switch (type) {
                case ExpType.TRAINING.toString():
                    experiment = Training.get(id)
                    break;
                case ExpType.SIMULATION.toString():
                    experiment = Simulation.get(id)
                    break
                case ExpType.EXPERIMENT.toString():
                    experiment = Experiment.get(id)
                    break
            }
            if (experiment) {
                experiment.delete(flush: true)
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }
}
