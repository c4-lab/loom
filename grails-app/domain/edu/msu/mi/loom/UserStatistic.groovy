package edu.msu.mi.loom

class UserStatistic {
    User user
    Session session
    double trainingTime
    float simulationScore
    float experimentScore
    List<Integer> textOrder = new ArrayList<>()
    List<Float> experimentRoundScore = new ArrayList<>()

    static hasMany = [textOrder: Integer, experimentRoundScore: Float]

    static constraints = {
        simulationScore nullable: true
        trainingTime nullable: true
        simulationScore nullable: true
        experimentScore nullable: true
    }
}
