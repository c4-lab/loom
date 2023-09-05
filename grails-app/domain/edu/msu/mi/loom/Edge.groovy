package edu.msu.mi.loom

class Edge {

    String source = null

    static hasMany = [ends: String]
    static belongsTo = [session: Session]

    static constraints = {
        source nullable: true
    }


    boolean isDirected() {
        return source != null
    }

    String alter(String node) {
        if (ends.size() != 2) {
            throw new IllegalStateException("Edge must have exactly two ends")
        }
        if (ends.contains(node)) {
            throw new IllegalArgumentException("Edge does not contain node ${node}")
        }
        return ends.find { it != node }
    }
}
