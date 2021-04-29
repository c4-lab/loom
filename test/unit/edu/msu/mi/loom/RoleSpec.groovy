package edu.msu.mi.loom

import edu.msu.mi.loom.utils.ConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Unroll

@TestFor(Role)
@Build(Role)
class RoleSpec extends ConstraintUnitSpec {
    def setup() {
        mockForConstraintsTests(Role, [Role.build(authority: Roles.ROLE_USER)])
    }

    @Unroll("test Role all constraints #field is #error")
    void "test Role all constraints"() {
        when:
        def role = new Role("$field": val)

        then:
        validateConstraints(role, field, error)

        where:
        error      | field       | val
        'nullable' | 'authority' | getEmptyString()
        'nullable' | 'authority' | null
        'unique'   | 'authority' | Roles.ROLE_USER
    }
}
