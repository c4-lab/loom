package edu.msu.mi.loom

class Session {
    String name

    static hasMany = [experiments: Experiment, simulations: Simulation, trainings: Training]

    static constraints = {
        name blank: false
        experiments nullable: true
        simulations nullable: true
        trainings nullable: true
    }
}
