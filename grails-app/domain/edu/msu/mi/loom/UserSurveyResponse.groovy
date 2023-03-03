package edu.msu.mi.loom

class UserSurveyResponse extends UserConstraintValue<Survey> {


    static hasMany = [options:SurveyOption]

    static UserSurveyResponse completeSurvey(User user, List<SurveyOption> options) {

        UserSurveyResponse usr = new UserSurveyResponse(user: user, options: options)

        usr.value = options.sum { SurveyOption opt ->
            opt.score.intValue()
        } as Float

        usr.constraintProvider = options[0].surveyItem.survey
        usr.created = new Date()
        usr.save(flush: true, insert: true)
        usr
    }



}
