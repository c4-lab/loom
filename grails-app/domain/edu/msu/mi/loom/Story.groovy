package edu.msu.mi.loom

class Story extends ConstraintProvider {

    List<Tile> tiles
    static hasMany = [tiles: Tile]
    static constraints = {

        tiles nullable: true

    }

    static mapping = {
        version false
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
        return "${getConstraintTitle()}:${id}"
    }

    String storyText() {
        def text = this.tiles ? this.tiles.sort{it.text_order}.text.join(" "):"--none--"
        return text
    }

    def beforeUpdate() {
        println("---> Before update in Story#${this.id}")
        if (this.isDirty()) {
            println("---> is in fact dirty")

        }
    }


}
