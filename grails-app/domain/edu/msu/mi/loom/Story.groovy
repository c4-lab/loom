package edu.msu.mi.loom

class Story {
    String title

    static hasMany = [tails: Tail]
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

        for (Tail tail : tails) {
            copy.addToTails(tail)
        }

        return copy
    }
}
