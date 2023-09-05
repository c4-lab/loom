package edu.msu.mi.loom

class UserSurveyResponse extends UserConstraintValue<Survey> {


    static hasMany = [options:SurveyOption]

    static UserSurveyResponse completeSurvey(User user, List<SurveyOption> options) {

        UserSurveyResponse usr = new UserSurveyResponse(user: user, options: options)

        usr.value = options*.score.sum()
        usr.constraintProvider = options[0].surveyItem.survey
        usr.created = new Date()
        usr.save(flush: true, insert: true)
        usr
    }



}
