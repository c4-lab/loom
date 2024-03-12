package edu.msu.mi.loom

import com.amazonaws.services.mturk.model.Comparator
import groovy.util.logging.Log4j

import java.util.regex.Matcher

@Log4j()
class ConstraintTest {



    static constraintPattern = ~/([\w_:\s]+?)\s*(EQUALS|LESS_THAN|GREATER_THAN|IN|NOT_IN|HAS|NOT_HAS)\s*([\d,]+)?/
    static enum Operator {

        EQUALS{
            @Override
            def generateForMturk() {
                return "="
            }

            @Override
            boolean evaluate(UserConstraintValue<? extends ConstraintProvider> value, String target) {
                if (!value) {
                    return false
                }
                else return value.value == Integer.parseInt(target)
            }
        },

        LESS_THAN{
            @Override
            def generateForMturk() {
                return "<"
            }

            @Override
            boolean evaluate(UserConstraintValue<? extends ConstraintProvider> value, String target) {
                if (!value) {
                    return false
                }
                else return value.value < Integer.parseInt(target)
            }
        },

        GREATER_THAN{
            @Override
            def generateForMturk() {
                return ">"
            }

            @Override
            boolean evaluate(UserConstraintValue<? extends ConstraintProvider> value, String target) {
                if (!value) {
                    return false
                }
                else return value.value > Integer.parseInt(target)
            }


        },

//        RANGE {
//            @Override
//            def generateForMturk() {
//                return super.generateForMturk()
//            }
//
//            @Override
//            evaluate(UserConstraintValue value, Object target) {
//                def nonnumeric = ~/[^\d,.]/
//                List<String> vals = (target as String).replaceAll(nonnumeric,'').split(",")
//                if (vals.size() != 2) {
//                    log.warn("RANGE constraint has invalid number of target values; returning false")
//                } else {
//                    return (target as Float) >= (vals[0] as Float) && (target as Float) < (vals[1] as Float)
//                }
//            }
//        },

        IN{
            @Override
            def generateForMturk() {
                return "IN"
            }

            @Override
            boolean evaluate(UserConstraintValue<? extends ConstraintProvider> value, String target) {
                if (!value) {
                    return false
                }
                else {
                    Matcher m = target =~ /\d+/
                    m.find {
                        Integer.parseInt(it) == value.value
                    } != null
                }
            }
        },

        NOT_IN{
            @Override
            def generateForMturk() {
                return "NOT_IN"
            }

            @Override
            boolean evaluate(UserConstraintValue<? extends ConstraintProvider> value, String target) {
                if (!value) {
                    return true
                } else {
                    Matcher m = target =~ /\d+/
                    m.find {
                        Integer.parseInt(it) == value.value
                    } == null
                }
            }
        },

        HAS{
            @Override
            def generateForMturk() {
                return super.generateForMturk()
            }

            @Override
            boolean evaluate(UserConstraintValue<? extends ConstraintProvider> value, String target) {
                return value.getConstraintProvider().getConstraintTitle() == target
            }
        },

        NOT_HAS{
            @Override
            def generateForMturk() {
                return super.generateForMturk()
            }

            @Override
            boolean evaluate(UserConstraintValue<? extends ConstraintProvider> value, String target) {
                return value.getConstraintProvider().getConstraintTitle() != target
            }
        }

        abstract generateForMturk();

        abstract boolean evaluate(UserConstraintValue<? extends ConstraintProvider> value, String target);

    }

    ConstraintProvider constraintProvider

    Operator operator
    String params


    static constraints = {
        params nullable: true
    }

    def constraintService

    boolean testUser(User u) {
        UserConstraintValue value = UserConstraintValue.findByUserAndConstraintProvider(u, constraintProvider)
        if (operator == Operator.NOT_HAS) {
            return(value==null)
        } else if (operator == Operator.HAS) {
            return(value!=null)
        } else {
            return operator.evaluate(value, params)
        }
    }

    String buildMturkString() {
        if (operator == Operator.NOT_HAS) {
            return "!"+constraintProvider.constraintTitle
        } else if (operator == Operator.HAS) {
            return constraintProvider.constraintTitle
        } else {
            return constraintProvider.constraintTitle + " " + operator + (params ? " " + params : "")
        }

    }

    String toString() {
        return buildMturkString()
    }

    static ConstraintTest create(ConstraintProvider provider, Operator op, String params = null) {
        ConstraintTest test
        if (params) {
            test = findByConstraintProviderAndOperatorAndParamsLike(provider, op, params)
        } else {
            test = findByConstraintProviderAndOperator(provider, op)

        }
        if (!test) {
            test = new ConstraintTest(constraintProvider: provider, operator: op, params: params)
            if (!test.save()) {
                log.error("Errers during save ${test.errors}")
            }
        }
        test

    }



}
