package edu.msu.mi.loom


/**
 * Collection of {@link SurveyItem}
 */
class Survey extends ConstraintProvider implements Trainable {

    static constraints = {
    }

    static hasMany = [surveyItems:SurveyItem]

    String name

    String constructConstraintTitle() {
        return "${super.constructConstraintTitle()} - ${name}"
    }

    @Override
    String getViewName() {
        "survey"
    }
}
