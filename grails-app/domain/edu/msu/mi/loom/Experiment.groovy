package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j

@Slf4j
@ToString(includeNames = true)
class Experiment {
    String name
    String nodes
    int roundCount
    int roundTime
    Date dateCreated
    int userCount
    int initialNbrOfTiles
    Story story
    boolean enabled = false
    String exp

    static hasMany = [edges: Edge, sessions:Session, initialStories:ExperimentInitialUserStory]

    static constraints = {
        nodes nullable:true
        exp nullable:true
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
        copy.story = story

        for (Edge edge : edges) {
            copy.addToEdges(edge)
        }

        return copy
    }
}
