package edu.msu.mi.loom


/**
 * Capture an activity that can serve as a constraint for a user
 */
class UserConstraintValue<T> {

    static belongsTo =  [user: User]
    ConstraintProvider constraintProvider
    float value
    Date created



    static constraints = {
        value nullable:true
        created nullable:true
    }

    T getConstraintProvider() {
        (T)constraintProvider
    }

}
