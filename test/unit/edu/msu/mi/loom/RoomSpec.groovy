package edu.msu.mi.loom

import edu.msu.mi.loom.utils.ConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Unroll

@TestFor(Room)
@Build(Room)
class RoomSpec extends ConstraintUnitSpec {

    def setup() {
        mockForConstraintsTests(Room, [Room.build(name: "First room")])
    }

    @Unroll("test Room all constraints #field is #error")
    void "test Room all constraints"() {
        when:
        def room = new Room("$field": val)

        then:
        validateConstraints(room, field, error)

        where:
        error      | field          | val
        'nullable' | 'name'         | getEmptyString()
        'nullable' | 'name'         | null
        'unique'   | 'name'         | "First room"
        'min'      | 'userMaxCount' | 1
    }
}
