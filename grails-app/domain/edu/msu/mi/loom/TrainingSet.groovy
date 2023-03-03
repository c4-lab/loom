package edu.msu.mi.loom

/**
 * Convenience class to bundle a set of trainings for users
 */
class TrainingSet extends ConstraintProvider {


    static hasMany = [simulations: Simulation, trainings: Training, readings:Reading, surveys:Survey, tasks: CrowdServiceTask]

    String name
    List<Training> trainings
    int uiflag = 0

    static constraints = {
        name blank: false, unique: true
        simulations nullable: true
        trainings nullable: true
        readings nullable: true
        surveys nullable: true
    }


}
