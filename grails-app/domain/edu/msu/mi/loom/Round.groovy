package edu.msu.mi.loom

class Round {
    User user
    Story story
    int roundNbr

    static hasMany = [tails: Long]

    static constraints = {
        roundNbr min: 1
    }

    static def remove(User user, boolean flush = false) {
        if (user == null) return false

        int rowCount = Round.where {
            user == Round.load(user.id)
        }.deleteAll()

        if (flush) {
            Round.withSession { it.flush() }
        }

        rowCount > 0
    }
}
