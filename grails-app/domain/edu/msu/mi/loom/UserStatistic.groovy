package edu.msu.mi.loom

class UserStatistic {
    User user
    Session session
    double trainingTime
    float simulationScore
    float experimentScore
    HashMap<String, String> experimentRoundScore

    static hasMany = [textOrder: String]

    static constraints = {
        simulationScore nullable: true
        trainingTime nullable: true
        simulationScore nullable: true
        experimentScore nullable: true
        experimentRoundScore nullable: true
        textOrder nullable: true
    }
}
