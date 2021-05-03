package edu.msu.mi.loom

import edu.msu.mi.loom.utils.ConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Unroll

@TestFor(Training)
@Build(Training)
class TrainingSpec extends ConstraintUnitSpec {

    def setup() {
        mockForConstraintsTests(Training, [Training.build(name: "First experiment")])
    }

    @Unroll("test Training all constraints #field is #error")
    void "test Training all constraints"() {
        when:
        def training = new Training("$field": val)

        then:
        validateConstraints(training, field, error)

        where:
        error      | field  | val
        'nullable' | 'name' | getEmptyString()
        'nullable' | 'name' | null
    }
}
