package edu.msu.mi.loom

class Reading implements HasQualification {


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

    @Override
    String getQualificationString() {
        return "Story Loom Reading Test"
    }

    @Override
    String getQualificationDescription() {
        return "Provides an indiecation of your reading comprehension ability"
    }
}
