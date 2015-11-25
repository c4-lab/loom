package edu.msu.mi.loom

class UserStory {
    String alias
    Story story
    Experiment experiment

    static constraints = {
        alias blank: false
    }
}
