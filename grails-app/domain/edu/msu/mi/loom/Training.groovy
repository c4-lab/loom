package edu.msu.mi.loom

import groovy.transform.ToString

@ToString(includeNames = true)
class Training {
    String name
    Date dateCreated

    static hasMany = [stories: Story]
    static belongsTo = [trainingSet: TrainingSet]

    static constraints = {
        name blank: false
    }

    static mapping = {
    }

    public Training clone() {
        Training copy = new Training()

        copy.name = this.name

        for (Story story : stories) {
            copy.addToStories(story)
        }

        return copy
    }
}
