package edu.msu.mi.loom

class Story {
    String title

    static hasMany = [tails: Tile]
    static belongsTo = [experiment: Experiment, simulation: Simulation, training: Training]

    static constraints = {
        title blank: false
        tails nullable: true
        experiment nullable: true
        simulation nullable: true
        training nullable: true
    }

    public Story clone() {
        Story copy = new Story()

        copy.title = this.title

        for (Tile tail : tails) {
            copy.addToTails(tail)
        }

        return copy
    }

    static String constructQualificationString(Story s) {
        "loomstory${s.id}"
    }

    public String toString() {
        def text = tails?tails.sort{it.text_order}.text.join(" "):"--none--"
        return text
    }
}
