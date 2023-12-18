package edu.msu.mi.loom


/**
 * Collection of {@link SurveyItem}
 */
class Survey extends ConstraintProvider implements Trainable {

    static constraints = {
        instructions nullable: true
    }

    List<SurveyItem> surveyItems
    boolean likert = false
    String instructions

    static hasMany = [surveyItems:SurveyItem]


    @Override
    String getViewName() {
        "survey"
    }
}
