package edu.msu.mi.loom

class UserRoundStory {

    def experimentService

    List<Tile> currentTiles
    static hasMany = [currentTiles: Tile]

    Date time
    Session session

    int round
    String userAlias
    float score



    static constraints = {
        score nullable: true
    }

    def float updateScore() {
        if (!currentTiles) {
            score = 0
        } else {
            List<Long> correct =(((Story)session.sp("story")).tiles as List<Tile>).sort {it.text_order}.collect {it.id}
            List<Long> mine = currentTiles.collect {it.id}
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
