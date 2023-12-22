package edu.msu.mi.loom

/**
 * This captures the results of a user who has participated in a training set.  There is, unfortunatley, no good way to capture training
 * outside of a training set at this point.  TODO - make it possible to save training that is not associated with a training set
 */
class UserTrainingSet {


    def randomStringGenerator

    User user
    TrainingSet trainingSet


    boolean complete

    static hasMany = [trainingResponses: UserTrainingResponse,
                      simulationResponses: UserSimulationResponse,
            readingResponses: UserReadingResponse,
            surveyReponses: UserSurveyResponse]


    boolean intro
    boolean simIntro
    Date trainingStartTime
    Date trainingEndTime
    String confirmationCode
    MturkAssignment mturkAssignment


    static constraints = {
        complete nullable:true
        intro defaultValue: false
        simIntro defaultValue: false
        trainingStartTime nullable:true
        trainingEndTime nullable:true
        confirmationCode nullable:true
        mturkAssignment nullable: true
    }

    static UserTrainingSet create(User user, TrainingSet ts, boolean complete = false, boolean flush = false) {
        def instance = new UserTrainingSet(user:user,trainingSet: ts,complete: complete)
        instance.save(flush: flush, insert: true)
        instance
    }

    static UserTrainingSet create(User user, TrainingSet ts, trainingStartTime, MturkAssignment mturkAssignment, boolean complete = false, boolean flush = false) {
        def instance = new UserTrainingSet(user: user, trainingSet: ts, complete: complete, mturkAssignment: mturkAssignment, trainingStartTime: trainingStartTime)

        if (instance.save(flush: flush, insert: true)) {
            if (mturkAssignment) {
                mturkAssignment.userTrainingSet = instance
                mturkAssignment.save(flush: flush, insert: true)
            }
            return instance
        } else {
            throw new RuntimeException("Failed to save UserTrainingSet: ${instance.errors.allErrors}")
        }
    }

    def beforeInsert = {
        if (!confirmationCode) {
            confirmationCode = randomStringGenerator.generateLowercase(12)
        }
    }





}
