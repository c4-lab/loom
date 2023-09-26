package edu.msu.mi.loom

class UserSimulationRoundScore {

    static belongsTo = [userSimulationResponse: UserSimulationResponse]

    Float value
    Integer round

    static constraints = {
    }
}
