package edu.msu.mi.loom

class Tile {
    String text
    int text_order


    static belongsTo = [story: Story]

    static mapping = {
        story cascade: "none"
    }

    static constraints = {
        text blank: false
        text_order min: 0
        story nullable: true
        text maxSize:100000, minSize: 1
    }

    public Tile clone() {
        Tile copy = new Tile()

        copy.text = this.text
        copy.text_order = this.text_order

        return copy
    }
}
