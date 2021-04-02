package edu.msu.mi.loom

class ReadingQuestion {

    String question
    Date dateCreated
    List<String> options
    List<Integer> corrects

    static belongsTo = [reading: Reading]
    static hasMany = [options: String, corrects: Integer]

    static constraints = {

        question maxSize:100000, minSize: 1
    }

    static mapping = {
    }

    static String constructQualificationString(Reading r) {
        "loomreadings${r.id}"
    }
}
