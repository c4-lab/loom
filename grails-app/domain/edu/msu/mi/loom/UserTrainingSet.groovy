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

    static hasMany = [trainingResponse: UserTrainingResponse,
                      simulationResponse: UserSimulationResponse,
            readingResponse: UserReadingResponse,
            surveyReponse: UserSurveyResponse]


    boolean intro
    Date trainingStartTime
    Date trainingEndTime
    Float simulationScore
    Float readingScore
    Float surveyScore
    String confirmationCode
    String assignmentId


    static constraints = {
        complete nullable:true
        intro defaultValue: false
        trainingStartTime nullable:true
        trainingEndTime nullable:true
        confirmationCode nullable:true
        assignmentId nullable: true
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
