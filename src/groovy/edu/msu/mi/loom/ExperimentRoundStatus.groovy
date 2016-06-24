package edu.msu.mi.loom

/**
 * Created by josh on 6/19/16.
 */
class ExperimentRoundStatus {

    static final enum Status {
        ACTIVE, PAUSING
    }


    final int userCount
    final int roundCount
    final int pauseLength
    int round = 0
    Set submitted = [] as Set
    Date roundStart
    long pauseStart
    Status currentStatus = Status.ACTIVE


    public ExperimentRoundStatus(int userCount, int roundCount, long pauseLength=10000l) {
        this.userCount = userCount
        this.roundCount = roundCount
        this.pauseLength = pauseLength
        this.roundStart = new Date()


    }

    public Status checkPauseStatus() {
        if (currentStatus == Status.PAUSING &&
                (submitted.size() == userCount || (System.currentTimeMillis() - pauseStart) >= pauseLength)) {
            currentStatus = Status.ACTIVE
            submitted.clear()
            round++
            this.roundStart = new Date()
        }
        currentStatus

//        if (currentStatus == Status.PAUSING &&
//                ((System.currentTimeMillis() - pauseStart) >= pauseLength)) {
//            currentStatus = Status.ACTIVE
//            submitted.clear()
//            round++
//            this.roundStart = new Date()
//        }
//        currentStatus

    }

    public boolean isFinished() {
        round >= roundCount
    }

    public void pause() {
        pauseStart = System.currentTimeMillis()
        currentStatus = Status.PAUSING
    }


}
