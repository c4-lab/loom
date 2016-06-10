package edu.msu.mi.loom

import groovy.transform.ToString

@ToString(includeNames = true)
class Simulation {
    String name
    int roundCount
    int roundTime
    Date dateCreated
    int userCount

    static hasMany = [stories: Story]
    static belongsTo = [trainingSet: TrainingSet]

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
}
