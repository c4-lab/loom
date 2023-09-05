package edu.msu.mi.loom

/**
 * Convenience class to bundle a set of trainings for users
 */
class TrainingSet extends ConstraintProvider {


    static final enum State {

        PENDING,  //unavailable
        AVAILABLE   //active, training is available
    }

    static hasMany = [simulations: Simulation, trainings: Training, readings:Reading, surveys:Survey, mturkTasks: MturkTask]



    List<Training> trainings
    int uiflag = 0
    State state = State.PENDING

    def countByHitStatus(String status) {
        mturkTasks.sum{ MturkTask task ->
            if (status) {
                task.hits.count { MturkHIT hit ->
                    hit.lastKnownStatus == status
                }
            } else {
                task.hits.size()
            }

        }
    }

    Collection<ConstraintProvider> allSubConstraints() {
        List<ConstraintProvider> result = [trainings, simulations, readings, surveys].flatten()
        result.removeAll{it == null}
        return result
    }
}
