package edu.msu.mi.loom

class TempExperiment {
    Experiment experiment
    User user
    List currentTails

    static hasMany = [currentTails: Integer]

    static constraints = {
    }
}
