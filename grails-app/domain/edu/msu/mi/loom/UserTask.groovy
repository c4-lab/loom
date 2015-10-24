package edu.msu.mi.loom

class UserTask {
    Task task
    int user_nbr
    int round_nbr

    static constraints = {
        user_nbr min: 0
        round_nbr min: 0
    }

    static mapping = {
        version false
    }

    static UserTask createForExperiment(Task task, Experiment experiment, boolean flush = true) {
        def instance = new UserTask(task: task)
        instance.save(flush: flush, insert: true)
        instance
    }

    static UserTask createForSimulation(Task task, int user_nbr = 0, int round_nbr = 0, boolean flush = true) {
        def instance = new UserTask(task: task, user_nbr: user_nbr, round_nbr: round_nbr)
        instance.save(flush: flush, insert: true)
        instance
    }
}
