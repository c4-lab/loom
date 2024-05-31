package edu.msu.mi.loom

class Story extends ConstraintProvider {

    List<Tile> tiles
    StorySeed seed
    static hasMany = [tiles: Tile]
    static constraints = {

        tiles nullable: true
        seed nullable: true

    }

    static mapping = {
        version false
    }

    Story clone() {
        Story copy = new Story()

        copy.name = this.name
        copy.seed = this.seed

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


}
