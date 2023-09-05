package edu.msu.mi.loom

class SessionInitialUserStory {

    String alias
    List<Tile> initialTiles


    static hasMany = [initialTiles:Tile]
    static belongsTo = [session:Session]
    static constraints = {
        alias blank: false
    }
}
