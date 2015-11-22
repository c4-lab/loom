package edu.msu.mi.loom

class SimulationTask {
    Simulation simulation
    Tail tail
    int user_nbr

    static constraints = {
        user_nbr min: 0
    }

    static mapping = {
        version false
    }

    static SimulationTask createForSimulation(Tail tail, int user_nbr = 0, Simulation simulation, boolean flush = true) {
        def instance = new SimulationTask(tail: tail, user_nbr: user_nbr, simulation: simulation)
        instance.save(flush: flush, insert: true)
        instance
    }
}
