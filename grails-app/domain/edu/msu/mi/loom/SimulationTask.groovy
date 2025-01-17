package edu.msu.mi.loom

class SimulationTask {
    Simulation simulation
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

    static SimulationTask createSimulationTask(Tile tile, int user_nbr = 0, int round_nbr = 0, Simulation simulation, boolean flush = true) {
        def instance = new SimulationTask(tile: tile, user_nbr: user_nbr, round_nbr: round_nbr, simulation: simulation)
        instance.save(flush: flush, insert: true)
        instance
    }
}
