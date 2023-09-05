package edu.msu.mi.loom

import groovy.transform.ToString

@ToString(includeNames = true)
class Simulation extends ConstraintProvider implements Trainable {

    int roundCount
    int roundTime

    int userCount
    Story story

    static constraints = {

        roundCount min: 1
        roundTime min: 1
        userCount min: 2
    }

    static mapping = {
    }



    Simulation clone() {
        Simulation copy = new Simulation()

        copy.name = this.name
        copy.roundCount = this.roundCount
        copy.roundTime = this.roundTime
        copy.userCount = this.userCount
        copy.story = this.story


        return copy
    }

    @Override
    String getViewName() {
        "simulation"
    }
}
