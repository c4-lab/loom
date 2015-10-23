package edu.msu.mi.loom

import edu.msu.mi.loom.utils.ConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Unroll

@Build(Session)
@TestFor(Session)
class SessionSpec extends ConstraintUnitSpec {

    def setup() {
        mockForConstraintsTests(Session, [Session.build(name: "Session1")])
    }

    @Unroll("test Session all constraints #field is #error")
    void "test Session all constraints"() {
        when:
        def room = new Session("$field": val)

        then:
        validateConstraints(room, field, error)

        where:
        error      | field         | val
        'nullable' | 'name'        | getEmptyString()
        'nullable' | 'name'        | null
        'unique'   | 'name'        | "Session1"
        'valid'    | 'experiments' | null
        'valid'    | 'simulations' | null
        'valid'    | 'trainings'   | null
    }
}
