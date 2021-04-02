package edu.msu.mi.loom

import edu.msu.mi.loom.utils.ConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Unroll

@TestFor(User)
@Build(User)
class UserSpec extends ConstraintUnitSpec {
    def setup() {
        mockForConstraintsTests(User, [User.build(username: 'user', password: '1')])
    }

    @Unroll("test Role all constraints #field is #error")
    void "test Role all constraints"() {
        when:
        def user = new User("$field": val)

        then:
        validateConstraints(user, field, error)

        where:
        error      | field      | val
        'nullable' | 'username' | getEmptyString()
        'nullable' | 'username' | null
        'unique'   | 'username' | 'user'
        'maxSize'  | 'username' | getLongString(50)
        'nullable' | 'password' | getEmptyString()
        'nullable' | 'password' | null
    }
}
