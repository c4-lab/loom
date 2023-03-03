package edu.msu.mi.loom

/**
 * SurveyItem reflects a question in a survey. It may have several {@link SurveyOption}s
 */

class SurveyItem  {


    String question
    Date dateCreated

    static hasMany = [options: SurveyOption]

    static belongsTo = [survey: Survey]

    static mapping = {

    }


}
