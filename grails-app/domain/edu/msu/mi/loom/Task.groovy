package edu.msu.mi.loom

class Task {
    String text
    int text_order

    static belongsTo = [training: Training, experiment: Experiment, simulation: Simulation]

    static constraints = {
        text blank: false
        text_order min: 0
        training nullable: true
        experiment nullable: true
        simulation nullable: true
    }
}
