package edu.msu.mi.loom

import groovy.transform.ToString

@ToString(includeNames = true)
class Simulation extends ConstraintProvider implements Trainable {
    String name
    int roundCount
    int roundTime
    Date dateCreated
    int userCount

    static hasMany = [stories: Story]

    static constraints = {
        name blank: false
        roundCount min: 1
        roundTime min: 1
        userCount min: 2
    }

    static mapping = {
    }



    public Simulation clone() {
        Simulation copy = new Simulation()

        copy.name = this.name
        copy.roundCount = this.roundCount
        copy.roundTime = this.roundTime
        copy.userCount = this.userCount

        for (Story story : stories) {
            copy.addToStories(story)
        }

        return copy
    }

    @Override
    String getViewName() {
        "simulation"
    }
}
