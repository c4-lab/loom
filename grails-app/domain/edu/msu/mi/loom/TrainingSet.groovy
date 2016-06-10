package edu.msu.mi.loom

class TrainingSet {


    static hasMany = [simulations: Simulation, trainings: Training]

    String name
    List<Training> trainings

    static constraints = {
        name blank: false, unique: true
        simulations nullable: true
        trainings nullable: true
    }


    static String constructQualificationString(TrainingSet ts) {
        "LoomQualification${ts.id}"
    }
}
