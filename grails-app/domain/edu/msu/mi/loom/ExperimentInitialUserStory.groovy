package edu.msu.mi.loom

class ExperimentInitialUserStory {
    String alias
    Experiment experiment
    List<Tile> initialTiles


    static hasMany = [initialTiles:Tile]
    static belongsTo = [experiment:Experiment]
    static constraints = {
        alias blank: false
    }
}
