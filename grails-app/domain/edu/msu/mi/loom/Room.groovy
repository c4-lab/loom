package edu.msu.mi.loom

class Room {
    String name
    int userMaxCount
    Session session
    String url

    static constraints = {
        name blank: false, unique: true
        userMaxCount min: 2
        url blank: false, unique: true
    }
}
