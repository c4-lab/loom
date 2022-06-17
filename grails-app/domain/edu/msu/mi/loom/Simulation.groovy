package edu.msu.mi.loom

import groovy.transform.ToString

@ToString(includeNames = true)
class Simulation implements HasQualification {
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

    static String constructQualificationString(Simulation s) {
        return "Story Loom Simulation"
    }

    @Override
    String getQualificationString() {
        return constructQualificationString(this)
    }

    @Override
    String getQualificationDescription() {
        return "The qualification reflects your score on the Loom training simulation"
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
