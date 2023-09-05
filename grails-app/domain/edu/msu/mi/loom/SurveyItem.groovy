package edu.msu.mi.loom

/**
 * SurveyItem reflects a question in a survey. It may have several {@link SurveyOption}s
 */

class SurveyItem  {


    String question
    Date dateCreated
    List<SurveyOption> options

    static hasMany = [options: SurveyOption]

    static belongsTo = [survey: Survey]

    static mapping = {

    }


}
