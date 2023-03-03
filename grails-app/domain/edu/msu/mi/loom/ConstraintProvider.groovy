package edu.msu.mi.loom

/**
 * Mixin to identify classes that can serve as constraints
 */
class ConstraintProvider {


    static hasMany = [qualifications:CrowdServiceQualification]


    static constraints = {

    }


    String getConstraintTitle() {
        return "Story Loom ${this.class.simpleName}"
    }

    String getConstraintDescription() {
        return "Qualification for the Story Loom platform"
    }




}
