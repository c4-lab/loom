package edu.msu.mi.loom

class Story implements HasQualification {
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
        return "Loom Story Participation-${s.title}-${s.id}"
    }

    public String toString() {
        def text = tails?tails.sort{it.text_order}.text.join(" "):"--none--"
        return text
    }

    @Override
    String getQualificationString() {
        return constructQualificationString(this)
    }

    @Override
    String getQualificationDescription() {
        return "This qualification indicates that you have participated in a Loom session using this story"
    }
}
