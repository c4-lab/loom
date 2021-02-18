package edu.msu.mi.loom

class UserTrainingSet {


    def randomStringGenerator

    User user
    TrainingSet trainingSet


    boolean complete

    static hasMany = [trainingsCompleted:Training, simulationsCompleted:SimulationScore]

    Date trainingStartTime
    Date trainingEndTime
    Float simulationScore
    String confirmationCode


    static constraints = {
        complete nullable:true
        trainingStartTime nullable:true
        trainingEndTime nullable:true
        simulationScore nullable:true
        confirmationCode nullable:true
    }

    static UserTrainingSet create(User user, TrainingSet ts, boolean complete = false, boolean flush = false) {
        def instance = new UserTrainingSet(user:user,trainingSet: ts,complete: complete)
        instance.save(flush: flush, insert: true)
        instance
    }

    def beforeInsert = {
        if (!confirmationCode) {
            confirmationCode = randomStringGenerator.generateLowercase(12)
        }
    }





}
