package edu.msu.mi.loom

import groovy.transform.AutoClone
import groovy.transform.ToString

@AutoClone
@ToString(includeNames = true)
class Training {
    String name
    Date dateCreated

    static hasMany = [task: Task]
    static belongsTo = [session: Session]

    static constraints = {
        name blank: false
    }

    static mapping = {
    }
}
