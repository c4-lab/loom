package edu.msu.mi.loom

import javax.persistence.Transient

class UserSimulationResponse extends UserConstraintValue<Simulation> {

    List<Float> scores = []
    static hasMany = [scores:Float]

}
