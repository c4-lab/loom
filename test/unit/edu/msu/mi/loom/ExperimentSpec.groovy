package edu.msu.mi.loom

import edu.msu.mi.loom.utils.ConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Unroll

@TestFor(Experiment)
@Build(Experiment)
class ExperimentSpec extends ConstraintUnitSpec {

    def setup() {
        mockForConstraintsTests(Experiment, [Experiment.build(name: "First experiment", url: "http://loom.com")])
    }

    @Unroll("test Experiment all constraints #field is #error")
    void "test Experiment all constraints"() {
        when:
        def experiment = new Experiment("$field": val)

        then:
        validateConstraints(experiment, field, error)

        where:
        error      | field        | val
        'nullable' | 'name'       | getEmptyString()
        'nullable' | 'name'       | null
        'unique'   | 'name'       | "First experiment"
        'nullable' | 'text'       | getEmptyString()
        'nullable' | 'text'       | null
        'size'     | 'text'       | getLongString(1000000)
        'nullable' | 'url'        | null
        'nullable' | 'url'        | getEmptyString()
        'unique'   | 'url'        | "http://loom.com"
        'min'      | 'roundCount' | 0
        'min'      | 'roundTime'  | 0

    }
}
