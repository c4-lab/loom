package edu.msu.mi.loom

class BarabassiAlbertNetwork extends NetworkTemplate {

    int seedNodeCount
    int degreeCount
    int totalDegree

    static constraints = {
    }

    String toString() {
        "BarabassiAlbert (seedNodeCount:${seedNodeCount}, degreeCount: ${degreeCount}, totalDegree:${totalDegree})"
    }
}
