package edu.msu.mi.loom


import grails.transaction.Transactional

import java.util.regex.Matcher

@Transactional
class ConstraintService {

    def mturkService

    static CONSTRAINT_VALUE_PATTERH = /(\S+)\s*([\d.]*)/


    UserConstraintValue parseConstraintValue(User u, String str) {
        Matcher m = str=~CONSTRAINT_VALUE_PATTERH
        if (!m.matches()) {
            throw new RuntimeException("Malformed constraint value string: $str")

        }

        ConstraintProvider provider = ConstraintProvider.findByConstraintTitle(m[0][1])
        if (!provider) {
            throw new RuntimeException("Non-existent constraint priovider: ${m[0][1]}")
        }


        Float value = null
        if (m[0][2]!=null) {
            try {
                value = Float.parseFloat(m[0][2])
            } catch (Exception e) {
                throw new RuntimeException("Could not parse value for user constraint: $value")
            }
        }

        return new UserConstraintValue(user: u, constraintProvider:  provider, value: value)

    }

    Collection<ConstraintTest> failsConstraints(User user, Session session) {
        Collection<ConstraintTest> tests = session.allConstraintTests()
        tests.findAll {
            !it.testUser(user)
        }

        //TODO
    }

    List<ConstraintTest> getConstraintTests(List constraints,List constraintoperators, List constraintparams) {
        constraints.withIndex().collect { def entry, int i ->
            new ConstraintTest(constraintProvider: ConstraintProvider.get(entry),
                    operator: ConstraintTest.Operator.valueOf(constraintoperators[i] as String),
                    params: constraintparams[i] as String).save()
        }
    }

    ConstraintTest getConstraintTest(String constraintProvider, String operator, String params) {
        String[] pieces = constraintProvider.split(":")
        String clazz = "${getClass().package.getName()}.${pieces[0]}"
        def results = ConstraintProvider.findAll {
            eq("class",clazz) && name == pieces[1]
        }

        if (!results) {
            throw new Exception("Constraint for ${constraintProvider} could not be identified")
        } else {

            ConstraintTest.Operator op = ConstraintTest.Operator.valueOf(operator)
            getConstraintTest(results[0], op, params)
        }
    }

    ConstraintTest getConstraintTest(ConstraintProvider provider, ConstraintTest.Operator operator, String params) {
        //this is a comment
        ConstraintTest test = ConstraintTest.findByConstraintProviderAndOperatorAndParams(provider,operator,params)
        if (!test) {
            test = new ConstraintTest(constraintProvider: provider,operator: operator,params:params)
            test.save()
        }
        return test
    }



    ConstraintTest getStoryConstraint(Story s) {
       ConstraintTest.create(s,ConstraintTest.Operator.NOT_HAS)
    }

    //TODO Handle the revocation of a constraint - possibly via a null parameter on the value?
    def setConstraintValueForUser(User user, ConstraintProvider provider, Integer value, CrowdServiceCredentials creds) {
        log.debug("Setting constraint value for ${provider}")
        UserConstraintValue uc = UserConstraintValue.findByUserAndConstraintProvider(user,provider)
        if (!uc) {
            uc = new UserConstraintValue(user: user, constraintProvider: provider)
        }
        uc.value = value
        uc.save()
        if (creds) {
            mturkService.assignQualification(user.workerId, provider, value, creds)
        }
    }

    def addConstraints(Experiment exp, List<ConstraintTest> tests) {
        tests.each {it->exp.addToConstraintTests(it)}
    }

    def addConstraints(Session session, List<ConstraintTest> tests) {
        tests.each {it->exp.addToConstraintTests(it)}
    }


}
