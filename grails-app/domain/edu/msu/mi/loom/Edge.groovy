package edu.msu.mi.loom

class Edge {
    String source
    String target
    Boolean isDirected = false

    static belongsTo = [experiment: Experiment]

    static constraints = {
    }
}
