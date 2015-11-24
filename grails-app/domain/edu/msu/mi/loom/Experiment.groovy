package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString(includeNames = true)
class Experiment {
    String name
    int roundCount
    int roundTime
    Date dateCreated
    int userCount
    int initialNbrOfTiles
    boolean enabled = false

    static hasMany = [stories: Story, edges: Edge]
    static belongsTo = [session: Session]

    static constraints = {
        name blank: false
        roundCount min: 1
        roundTime min: 1
        userCount min: 2
        initialNbrOfTiles min: 2
    }

    static mapping = {
    }

    public Experiment clone() {
        Experiment copy = new Experiment()

        copy.name = this.name
        copy.roundCount = this.roundCount
        copy.roundTime = this.roundTime
        copy.userCount = this.userCount
        copy.initialNbrOfTiles = this.initialNbrOfTiles
        copy.enabled = this.enabled

        for (Story story : stories) {
            copy.addToStories(story)
        }

        for (Edge edge : edges) {
            copy.addToEdges(edge)
        }

        return copy
    }
}
