package edu.msu.mi.loom

class SessionParameters {

    def constraintService

    Integer minNode
    Integer maxNode


    Integer initialNbrOfTiles
    Integer roundCount
    Integer roundTime //in seconds
    Integer isInline = 0

    NetworkTemplate networkTemplate
    Story story

    SessionParameters parentParameters


    //PAYMENT
    Integer paymentBase


    

    Integer paymentWaitingBonusPerMinute
    Integer paymentMaxScoreBonus

    SessionType sessionType


    static hasMany = [constraintTests:ConstraintTest,splitConstraints:ConstraintTest]
    static constraints = {
        maxNode min: 2, nullable:true
        minNode min: 2, nullable:true
        initialNbrOfTiles nullable: true
        roundCount nullable: true
        isInline nullable: true
        roundTime nullable: true
        networkTemplate nullable:true
        story nullable:true
        parentParameters nullable:true
        sessionType nullable: true

        //PAYMENT
        paymentBase nullable:true
        paymentWaitingBonusPerMinute nullable:true
        paymentMaxScoreBonus nullable:true



    }

    def beforeInsert() {
        if (story) {
            ConstraintTest storyConstraint = constraintService.getStoryConstraint(story)
            Set<ConstraintTest> existingTests = constraintTests?:[]
            if (!existingTests.contains(storyConstraint)) {
                this.addToConstraintTests(storyConstraint)
            }
        }
    }

    def defaultGetter(String propertyName) {
        this.properties.get(propertyName)?:parentParameters.properties.get(propertyName)
    }

    List<String> checkMissingValues() {
        this.gormPersistentEntity.persistentProperties.findAll { prop ->
            if (defaultGetter(prop.name as String)==null) {
                "${prop} cannot be null"
            } else {
                false
            }
        } as List<String>
    }



    Integer safeGetMinNode() {
        if (minNode!= null) {
            return minNode;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetMinNode();
        } else {
            return null;
        }
    }

    Integer safeGetMaxNode() {
        if (maxNode!= null) {
            return maxNode;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetMaxNode();
        } else {
            return null;
        }
    }

    Integer safeGetInitialNbrOfTiles() {
        if (initialNbrOfTiles!= null) {
            return initialNbrOfTiles;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetInitialNbrOfTiles();
        } else {
            return null;
        }
    }

    Integer safeGetRoundCount() {
        if (roundCount!= null) {
            return roundCount;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetRoundCount();
        } else {
            return null;
        }
    }

    Integer safeGetRoundTime() {
        if (roundTime!= null) {
            return roundTime;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetRoundTime();
        } else {
            return null;
        }
    }

    Integer safeGetIsInline() {
        if (isInline!= null) {
            return isInline;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetIsInline();
        } else {
            return null;
        }
    }

    Integer safeGetPaymentBase() {
        if (paymentBase!= null) {
            return paymentBase;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetPaymentBase();
        } else {
            return null;
        }
    }

    Integer safeGetPaymentWaitingBonusPerMinute() {
        if (paymentWaitingBonusPerMinute!= null) {
            return paymentWaitingBonusPerMinute;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetPaymentWaitingBonusPerMinute();
        } else {
            return null;
        }
    }

    Integer safeGetPaymentMaxScoreBonus() {
        if (paymentMaxScoreBonus!= null) {
            return paymentMaxScoreBonus;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetPaymentMaxScoreBonus();
        } else {
            return null;
        }
    }

    NetworkTemplate safeGetNetworkTemplate() {
        if (networkTemplate!= null) {
            return networkTemplate;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetNetworkTemplate();
        } else {
            return null;
        }
    }

    Story safeGetStory() {
        if (story!= null) {
            return story;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetStory();
        } else {
            return null;
        }
    }

    SessionType safeGetSessionType() {
        if (sessionType!= null) {
            return sessionType;
        } else if (parentParameters!= null) {
            return parentParameters.safeGetSessionType();
        } else {
            return null;
        }
    }

    Set<ConstraintTest> safeGetConstraintTests() {
        Set<ConstraintTest> results = [] as Set
        if (constraintTests!= null) {
            results += constraintTests
        }

        if (parentParameters!= null) {
            results += parentParameters.safeGetConstraintTests();
        }
        return results
    }

    Set<ConstraintTest> safeGetSplitConstraints() {
        Set<ConstraintTest> results = []
        if (splitConstraints!= null) {
            results += splitConstraints;
        }

        if (parentParameters!= null) {
            results+= parentParameters.safeGetSplitConstraints();
        }
        return results
    }



}
