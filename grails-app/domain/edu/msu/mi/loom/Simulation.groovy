package edu.msu.mi.loom

class Simulation {
    String name
    String text
    int roundCount
    int roundTime

    static constraints = {
        name blank: false, unique: true
        text blank: false, size: 1..10000
        roundCount min: 1
        roundTime min: 1
    }

    static mapping = {
        text type: "text"
    }
}
