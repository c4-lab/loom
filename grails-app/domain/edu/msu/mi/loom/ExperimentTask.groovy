package edu.msu.mi.loom

class ExperimentTask {
    Experiment experiment
    Tile tile
    int user_nbr
    int round_nbr

    static constraints = {
        user_nbr min: 0
        round_nbr: 0
    }

    static mapping = {
        version false
    }

    static ExperimentTask createForExperiment(Tile tile, int user_nbr = 0, int round_nbr = 0, Experiment experiment, boolean flush = true) {
        def instance = new ExperimentTask(tile: tile, user_nbr: user_nbr, round_nbr: round_nbr, experiment: experiment)
        instance.save(flush: flush, insert: true)
        instance
    }
}
