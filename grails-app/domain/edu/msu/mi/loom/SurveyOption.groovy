package edu.msu.mi.loom

class SurveyOption {
    Date dateCreated
    String answer
    Integer score
    static belongsTo = [survey: Survey]
    static constraints = {
    }
}
