package edu.msu.mi.loom

class Story extends ConstraintProvider {
    String title

    static hasMany = [tiles: Tile]
    static belongsTo = [experiment: Experiment, simulation: Simulation, training: Training]

    static constraints = {
        title blank: false
        tiles nullable: true
        experiment nullable: true
        simulation nullable: true
        training nullable: true
    }

    public Story clone() {
        Story copy = new Story()

        copy.title = this.title

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
