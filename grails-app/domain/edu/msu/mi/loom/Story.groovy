package edu.msu.mi.loom

class Story extends ConstraintProvider {


    List<Tile> tiles
    static hasMany = [tiles: Tile]
    static belongsTo = [experiment: Experiment, simulation: Simulation, training: Training]

    static constraints = {

        tiles nullable: true
        experiment nullable: true
        simulation nullable: true
        training nullable: true
    }

    Story clone() {
        Story copy = new Story()

        copy.name = this.name

        for (Tile tail : this.tiles) {
            copy.addToTiles(tail)
        }

        return copy
    }


    public String toString() {
        def text = this.tiles ? this.tiles.sort{it.text_order}.text.join(" "):"--none--"
        return text
    }


}
