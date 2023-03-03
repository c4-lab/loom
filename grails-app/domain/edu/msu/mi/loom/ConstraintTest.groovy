package edu.msu.mi.loom

class ConstraintTest {

    static enum Operator {

        EQUALS {
            @Override
            def generateForMturk() {
                return super.generateForMturk()
            }

            @Override
            evaluate(UserConstraintValue value, Object target) {
                return value.value == (target as Float)
            }
        },

        LESS_THAN {
            @Override
            def generateForMturk() {
                return super.generateForMturk()
            }

            @Override
            def evaluate(UserConstraintValue value, Object target) {
                return value.value < (target as Float)
            }
        },

        GREATER_THAN {
            @Override
            def generateForMturk() {
                return super.generateForMturk()
            }

            @Override
            evaluate(UserConstraintValue value, Object target) {
                return value.value > (target as Float)
            }


        },

        RANGE {
            @Override
            def generateForMturk() {
                return super.generateForMturk()
            }

            @Override
            evaluate(UserConstraintValue value, Object target) {
                def nonnumeric = ~/[^\d,.]/
                List<String> vals = (target as String).replaceAll(nonnumeric,'').split(",")
                if (vals.size() != 2) {
                    log.warn("RANGE constraint has invalid number of target values; returning false")
                } else {
                    return (target as Float) >= (vals[0] as Float) && (target as Float) < (vals[1] as Float)
                }
            }
        },

        IN {
            @Override
            def generateForMturk() {
                return super.generateForMturk()
            }

            @Override
            evaluate(UserConstraintValue value, Object target) {
                def nonnumeric = ~/[^\d,.]/
                List<String> vals = (target as String).replaceAll(nonnumeric,'').split(",")
                def result = vals.find {
                    Float f = it as Float
                    return f == target as Float
                }
                result != null
            }
        },

        HAS {
            @Override
            def generateForMturk() {
                return super.generateForMturk()
            }

            @Override
            evaluate(UserConstraintValue value, Object target) {
               return value.getConstraintProvider() == target as ConstraintProvider
            }
        },

        NOT_HAS {
            @Override
            def generateForMturk() {
                return super.generateForMturk()
            }

            @Override
            evaluate(UserConstraintValue value, Object target) {
                return value.getConstraintProvider() != target as ConstraintProvider
            }
        }

        abstract generateForMturk();
        abstract evaluate(UserConstraintValue<? extends ConstraintProvider> value, Object target);

    }

    ConstraintProvider constraintProvider

    Operator operator
    String params


    static constraints = {
        params nullable: true
    }

    boolean testUser(User u) {
        UserConstraintValue value = UserConstraintValue.findByUserAndConstraintProvider(u, constraintProvider)
        if (operator == Operator.NOT_HAS && value) {
            return false
        } else {
            return operator.evaluate(value,params)
        }
    }

    String buildMturkString() {
       constraintProvider.constraintTitle+" "+operator+" "+params
    }

    static ConstraintTest create(ConstraintProvider provider, Operator op, String params = null) {
        ConstraintTest test
        if (params) {
            test = findByConstraintProviderAndOperator(provider,op)
        } else {
            test = findByConstraintProviderAndOperatorAndParamsLike(provider,op,params)
        }
        test?:new ConstraintTest(constraintProvider: provider, operation: op, params: params).save(flush: true)


    }

}
