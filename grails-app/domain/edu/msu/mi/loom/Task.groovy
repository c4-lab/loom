package edu.msu.mi.loom

class Task {
    String text
    int text_order

    static belongsTo = [training: Training]

    static constraints = {
        text blank: false
        text_order min: 0
        training nullable: true
    }
}
