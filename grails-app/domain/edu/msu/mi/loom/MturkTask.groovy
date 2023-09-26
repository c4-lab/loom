package edu.msu.mi.loom

/**
 * An MTurk task is a collection of HITs that are fundamentally the same;
 * may differ by qualification
 */
class MturkTask {



    static hasMany = [hits: MturkHIT]
    static belongsTo = [session:Session, training:TrainingSet]

    //MTURK PARAMETERS
    Integer mturkNumberHits
    Integer mturkHitLifetimeInSeconds
    Integer mturkAssignmentLifetimeInSeconds
    String mturkAdditionalQualifications
    String basePayment
    String title
    String description
    String keywords

    CrowdServiceCredentials credentials

    static constraints = {
        session nullable: true
        training nullable: true
        mturkAdditionalQualifications nullable: true
    }


    def owner() {
        session?session:training
    }

}
