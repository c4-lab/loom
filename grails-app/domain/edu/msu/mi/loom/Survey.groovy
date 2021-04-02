package edu.msu.mi.loom

class Survey {


    String question
    Date dateCreated

    static hasMany = [options: SurveyOption]

    static belongsTo = [trainingSet: TrainingSet]

    static mapping = {

    }


}
