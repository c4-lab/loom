package edu.msu.mi.loom

import groovy.transform.ToString

@ToString(includeNames = true)
class Training extends ConstraintProvider implements Trainable {


    Story story

    static mapping = {
    }

    public Training clone() {
        Training copy = new Training()

        copy.name = this.name
        copy.story = story

        return copy
    }

    @Override
    String getViewName() {
        return "practice"
    }
}
