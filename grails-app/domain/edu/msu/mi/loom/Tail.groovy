package edu.msu.mi.loom

import groovy.transform.AutoClone

@AutoClone
class Tail {
    String text
    int text_order

    static belongsTo = [story: Story]

    static constraints = {
        text blank: false
        text_order min: 0
        story nullable: true
    }
}
