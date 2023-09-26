package edu.msu.mi.loom

class NewmanWattsNetwork extends NetworkTemplate {

    int minDegree
    int maxDegree
    float prob

    static constraints = {
    }

    String toString() {
        "NewmanWatts (minDegree:${minDegree}, maxDegree: ${maxDegree}, prob:${prob}"
    }
}
