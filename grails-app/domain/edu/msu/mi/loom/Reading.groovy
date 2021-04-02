package edu.msu.mi.loom

class Reading {


    String name
    Date dateCreated
    String passage


    static hasMany = [questions: ReadingQuestion]
    static belongsTo = [trainingSet: TrainingSet]


    static constraints = {
        name blank: false
        passage maxSize:100000, minSize: 1
    }

    static mapping = {
    }




}
