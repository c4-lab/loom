package edu.msu.mi.loom

class UserTrainingSet {


    def randomStringGenerator

    User user
    TrainingSet trainingSet


    boolean complete

    static hasMany = [trainingsCompleted:Training,
                      simulationsCompleted:SimulationScore,
                      readingCompleted:Reading,
                      surveyCompleted:Survey,
                        surveyAnswers:UserSurveyOption]

    Date trainingStartTime
    Date trainingEndTime
    Float simulationScore
    Float readingScore
    Float surveyScore
    Boolean isDemographicsComplete
    String confirmationCode
    String assignmentId


    static constraints = {
        complete nullable:true
        trainingStartTime nullable:true
        trainingEndTime nullable:true
        simulationScore nullable:true
        readingScore nullable: true
        surveyScore nullable: true
        isDemographicsComplete nullable: true
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
