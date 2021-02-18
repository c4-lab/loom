package edu.msu.mi.loom.utils

import spock.lang.Specification

/**
 * Developer: Emil Matevosyan
 * Date: 10/7/15.
 */
abstract class ConstraintUnitSpec extends Specification {
    void validateConstraints(obj, field, error) {
        def validated = obj.validate()
        if (error && error != 'valid') {
            assert !validated
            assert obj.errors[field]
            assert error == obj.errors[field]
        } else {
            assert !obj.errors[field]
        }
    }

    public String getEmptyString() { '' }

    public String getLongString(Integer length) {
        return 'a' * length
    }

    String getEmail(Boolean valid) {
        valid ? "test@example.com" : "example@test"
    }
}
