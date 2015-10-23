package edu.msu.mi.loom

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
}
