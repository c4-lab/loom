package edu.msu.mi.loom

class UserTask {
    Task task
    int user_nbr
    Experiment experiment
    Simulation simulation
    int round_nbr

    static constraints = {
        experiment nullable: true
        simulation nullable: true
        user_nbr min: 1
        round_nbr min: 1
    }

    static mapping = {
        version false
    }

    static UserTask createForExperiment(Task task, Experiment experiment, boolean flush = true) {
        def instance = new UserTask(task: task, experiment: experiment)
        instance.save(flush: flush, insert: true)
        instance
    }

    static UserTask createForSimulation(Task task, Simulation simulation, int user_nbr, int round_nbr, boolean flush = true) {
        def instance = new UserTask(task: task, simulation: simulation, user_nbr: user_nbr, round_nbr: round_nbr)
        instance.save(flush: flush, insert: true)
        instance
    }
}
