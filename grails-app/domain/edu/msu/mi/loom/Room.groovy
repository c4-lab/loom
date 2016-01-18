package edu.msu.mi.loom

class Room {
    String name
    int userMaxCount
    Session session

    static constraints = {
        name blank: false, unique: true
        userMaxCount min: 2
    }
}
