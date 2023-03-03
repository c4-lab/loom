package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j
@Slf4j
@ToString(includeNames = true)

/**
 * Experiments cover the selection of story, network, and interface.  Details about the specific subject population (including criteria for inclusion) are part of the session
 */
class Experiment {


    String name

    int min_node
    int max_node
    int initialNbrOfTiles


    int roundCount
    int roundTime

    Date dateCreated

    int isInline = 0
    static hasMany = [edges: Edge, sessions:Session, initialStories:ExperimentInitialUserStory,stories:Story, constraintTests: ConstraintTest]

    static constraints = {
        name blank: false, unique: true
        max_node min: 2
    }

    def beforeInsert() {


        stories.each {
            ConstraintTest test = new ConstraintTest(operator: ConstraintTest.Operator.NOT_HAS, constraintProvider: it)
            println("Adding ${test}")
            this.addToConstraintTests(test)
        }
    }



//    public Experiment clone() {
//        Experiment copy = new Experiment()
//
//        copy.name = this.name
//        copy.roundCount = this.roundCount
//        copy.roundTime = this.roundTime
//        copy.userCount = this.userCount
//        copy.initialNbrOfTiles = this.initialNbrOfTiles
//        copy.enabled = this.enabled
//        copy.story = story
//
//        for (Edge edge : edges) {
//            copy.addToEdges(edge)
//        }
//
//        return copy
//    }
}
