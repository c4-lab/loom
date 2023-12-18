package edu.msu.mi.loom


/**
 * SurveyOption is a multiple choice answer in a survey
 */
class SurveyOption {

    String answer
    Integer score
    static belongsTo = [surveyItem: SurveyItem]
    static constraints = {
        answer nullable: true
    }
}
