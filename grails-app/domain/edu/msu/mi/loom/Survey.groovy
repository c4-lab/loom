package edu.msu.mi.loom

class Survey implements HasQualification {


    String question
    Date dateCreated

    static hasMany = [options: SurveyOption]

    static belongsTo = [trainingSet: TrainingSet]

    static mapping = {

    }

    @Override
    String getQualificationString() {
        return "Story Loom Survey"
    }

    @Override
    String getQualificationDescription() {
        return "The qualification reflects your score on the Loom survey"
    }
}
