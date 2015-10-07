package edu.msu.mi.loom

class Room {
    String name
    int userMaxCount

    static hasMany = [users: User]

    static constraints = {
        name blank: false, unique: true
        userMaxCount min: 2
        users nullable: true
    }
}
