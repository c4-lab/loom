package edu.msu.mi.loom

import grails.transaction.Transactional



@Transactional
class ConstraintService {

    def serviceMethod() {

    }

    List<ConstraintTest> failsConstraints(User user, Session session) {
        List<ConstraintTest> constraintTests = session.userConstraints + session.exp.constraintTests
        constraintTests.findAll {
            !it.testUser(user)
        }
    }

    List<ConstraintTest> getConstraintTests(List constraints,List constraintoperators, List constraintparams) {
        constraints.withIndex().collect { def entry, int i ->
            new ConstraintTest(constraintProvider: ConstraintProvider.get(entry),
                    operator: ConstraintTest.Operator.valueOf(constraintoperators[i] as String),
                    params: constraintparams[i] as String).save()
        }
    }



    ConstraintTest getStoryConstraint(Story s) {
       ConstraintTest.create(s,ConstraintTest.Operator.NOT_HAS)
    }

    def addConstraints(Experiment exp, List<ConstraintTest> tests) {
        tests.each {it->exp.addToConstraintTests(it)}
    }

    def addConstraints(Session session, List<ConstraintTest> tests) {
        tests.each {it->exp.addToConstraintTests(it)}
    }


}
