package edu.msu.mi.loom

class Experiment {
    String name
    String text
    String url
    int roundCount
    int roundTime

    static constraints = {
        name blank: false, unique: true
        text blank: false, size: 1..10000
        url blank: false, unique: true
        roundCount min: 1
        roundTime min: 1
    }

    static mapping = {
        text type: "text"
    }
}
