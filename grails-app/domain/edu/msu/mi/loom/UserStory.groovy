package edu.msu.mi.loom

class UserStory {
    int alias
    Story story
    Experiment experiment

    static constraints = {
        alias blank: false
    }
}
