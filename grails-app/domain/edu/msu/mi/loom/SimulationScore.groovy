package edu.msu.mi.loom

class SimulationScore {


    Simulation simulation
    List<Float> scores = []

    static hasMany = [scores:Float]

    static belongsTo = [uts:UserTrainingSet]


}
