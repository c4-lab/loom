package edu.msu.mi.loom

class UserStatistic {
    User user
    Session session
    Room room
    double trainingTime
    float simulationScore
    List<Integer> textOrder = new ArrayList<>()
    List<Float> experimentRoundScore = new ArrayList<>()

    static hasMany = [textOrder: Integer, experimentRoundScore: Float]

    static constraints = {
        simulationScore nullable: true
        trainingTime nullable: true
        simulationScore nullable: true
        room nullable: true
    }
}
