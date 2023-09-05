package edu.msu.mi.loom

import javax.persistence.Transient

class UserSimulationResponse extends UserConstraintValue<Simulation> {

    List<UserSimulationRoundScore> scores
    static hasMany = [scores:UserSimulationRoundScore]

    Float mean() {
        if (scores) {
            scores*.value.sum() / scores.size()
        } else {
            0f
        }
    }

}
