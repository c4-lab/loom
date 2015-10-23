package edu.msu.mi.loom

class Simulation {
    String name
    int roundCount
    int roundTime
    Date dateCreated
    int userCount

//    static hasMany = [task: Task]

    static constraints = {
        name blank: false, unique: true
        roundCount min: 1
        roundTime min: 1
        userCount min: 2
    }

    static mapping = {
    }
}
