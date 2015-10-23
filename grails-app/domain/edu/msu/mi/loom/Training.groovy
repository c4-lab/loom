package edu.msu.mi.loom

import groovy.transform.ToString

@ToString(includeNames = true)
class Training {
    String name
    Date dateCreated

    static hasMany = [task: Task]

    static constraints = {
        name blank: false
    }

    static mapping = {
    }
}
