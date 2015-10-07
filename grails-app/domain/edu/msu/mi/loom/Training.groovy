package edu.msu.mi.loom

class Training {
    String name
    String text

    static constraints = {
        name blank: false, unique: true
        text blank: false, size: 1..10000
    }

    static mapping = {
        text type: "text"
    }
}
