package edu.msu.mi.loom

class UserRoundStory {

    def experimentService

    Date time
    Session session
    List<Tile> currentTails
    int round
    String userAlias
    float score

    static hasMany = [currentTails: Tile]

    static constraints = {
        score nullable: true
    }

    def float updateScore() {
        if (!currentTails) {
            score = 0
        } else {
            List<Long> correct =(session.exp.story.tails as List).sort {it.text_order}.collect {it.id}
            List<Long> mine = currentTails.collect {it.id}
            score = experimentService.score(correct,mine)
        }

    }

    def beforeInsert = {
        updateScore()
    }
    def beforeUpdate = {
       updateScore()
    }





}
