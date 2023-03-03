package edu.msu.mi.loom


/**
 * SurveyOption is a multiple choice answer in a survey
 */
class SurveyOption {
    Date dateCreated
    String answer
    Integer score
    static belongsTo = [surveyItem: SurveyItem]
    static constraints = {
    }
}
