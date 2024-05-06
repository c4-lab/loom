package edu.msu.mi.loom

/**
 * Mixin to identify classes that can serve as constraints
 */
class ConstraintProvider {


    static hasMany = [serviceIds: CrowdServiceScopedId]

    Date dateCreated = new Date()

    String name

    String constraintTitle

    static mapping = {
        version false
    }

    static constraints = {

    }

    String getConstraintTitle() {
        return "${this.class.simpleName}:${this.name}"
    }

    String getConstraintDescription() {
        return "Qualification for the Story Loom platform"
    }

    def beforeInsert() {
        constraintTitle = getConstraintTitle()
    }
}
