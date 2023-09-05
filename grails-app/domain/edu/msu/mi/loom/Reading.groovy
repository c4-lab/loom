package edu.msu.mi.loom

import org.grails.datastore.mapping.model.types.Simple

class Reading extends ConstraintProvider implements Trainable {




    String passage

    List<ReadingQuestion> questions
    static hasMany = [questions: ReadingQuestion]


    static constraints = {
        name blank: false
        passage maxSize:100000, minSize: 1
    }

    static mapping = {
    }

    @Override
    String getViewName() {
        "reading"
    }
}
