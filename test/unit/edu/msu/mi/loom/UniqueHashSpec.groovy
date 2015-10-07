package edu.msu.mi.loom

import edu.msu.mi.loom.utils.ConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Unroll

@TestFor(UniqueHash)
@Build(UniqueHash)
class UniqueHashSpec extends ConstraintUnitSpec {

    def setup() {
        mockForConstraintsTests(UniqueHash, [UniqueHash.build()])
    }

    @Unroll("test UniqueHash all constraints #field is #error")
    void "test UniqueHash all constraints"() {
        when:
        def uniqueHash = new UniqueHash("$field": val)

        then:
        validateConstraints(uniqueHash, field, error)

        where:
        error      | field  | val
        'nullable' | 'hash' | getEmptyString()
        'nullable' | 'hash' | null
    }
}
