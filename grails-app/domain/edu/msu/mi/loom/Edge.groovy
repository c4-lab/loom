package edu.msu.mi.loom

class Edge {
    User source
    User target
    Boolean isDirected

    static belongsTo = [experiment: Experiment]

    static constraints = {
    }
}
