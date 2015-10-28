package edu.msu.mi.loom

class UserStory {
    String alias
    Story story

    static constraints = {
        alias blank: false
    }
}
