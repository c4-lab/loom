package edu.msu.mi.loom


/**
 * Collection of {@link SurveyItem}
 */
class Survey extends ConstraintProvider implements Trainable {

    static constraints = {
    }

    List<SurveyItem> surveyItems
    static hasMany = [surveyItems:SurveyItem]


    @Override
    String getViewName() {
        "survey"
    }
}
