package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j
@Slf4j
@ToString(includeNames = true)
class Experiment {
    static final enum Network_type {

        Lattice,Newman_Watts,Barabassi_Albert

    }

    String name

    int min_node
    int max_node
    int initialNbrOfTiles
    Network_type network_type
    int roundCount
    int roundTime
    int m
    Float probability
    int min_degree
    int max_degree
    String qualifier
    Date dateCreated
    Story story
    TrainingSet training_set
    Float accepting
    Float completion
    Float waiting
    Float score
    int uiflag = 0

    static hasMany = [edges: Edge, sessions:Session, initialStories:ExperimentInitialUserStory,stories:Story]

    static constraints = {
        min_node nullable:false
        name blank: false, unique: true
        qualifier nullable: true
        probability nullable: true
        max_node min: 2
    }

    static mapping = {
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
