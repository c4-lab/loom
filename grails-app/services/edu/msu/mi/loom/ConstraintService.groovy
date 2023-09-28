package edu.msu.mi.loom

import com.amazonaws.services.mturk.model.Comparator
import grails.transaction.Transactional

import java.util.regex.Matcher


@Transactional
class ConstraintService {

    def mturkService




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
            mturkService.assignQualification(user.turkerId, provider, value, creds)
        }
    }

    def addConstraints(Experiment exp, List<ConstraintTest> tests) {
        tests.each {it->exp.addToConstraintTests(it)}
    }

    def addConstraints(Session session, List<ConstraintTest> tests) {
        tests.each {it->exp.addToConstraintTests(it)}
    }


}
