package edu.msu.mi.loom

import groovy.transform.AutoClone

@AutoClone
class Experiment {
    String name
    String url
    int roundCount
    int roundTime
    Date dateCreated
    int userCount

    static hasMany = [task: Task]
    static belongsTo = [session: Session]

    static constraints = {
        name blank: false
        url blank: false, unique: true
        roundCount min: 1
        roundTime min: 1
        userCount min: 2
    }

    static mapping = {
    }
}
