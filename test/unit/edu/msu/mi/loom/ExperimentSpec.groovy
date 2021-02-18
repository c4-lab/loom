package edu.msu.mi.loom

import edu.msu.mi.loom.utils.ConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Unroll

@TestFor(Experiment)
@Build(Experiment)
class ExperimentSpec extends ConstraintUnitSpec {

    def setup() {
        mockForConstraintsTests(Experiment, [Experiment.build(name: "First experiment")])
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
        'min'      | 'roundCount' | 0
        'min'      | 'roundTime'  | 0
    }
}
