package edu.msu.mi.loom

import groovy.util.logging.Log4j

/**
 * Created by josh on 6/19/16.
 */
@Log4j
class ExperimentRoundStatus {

    static final enum Status {
        ACTIVE, PAUSING, FINISHED
    }


    final int userCount
    final int roundCount
    final int pauseLength
    int round = 0
    final Set submitted = [] as Set
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
            log.debug("Advancing round with ${submitted.size()}")
            round++
            if (isFinished()) {
                currentStatus = Status.FINISHED
            } else {
                currentStatus = Status.ACTIVE
                submitted.clear()
                this.roundStart = new Date()
            }


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

    public submitUser(userId) {
        synchronized (submitted){submitted<<userId}
    }

    public boolean isFinished() {
        round >= roundCount
    }

    public void pause() {
        pauseStart = System.currentTimeMillis()
        currentStatus = Status.PAUSING
    }

    public String toString() {
        "Status: Round ${round}, Status ${currentStatus} ${currentStatus==Status.PAUSING?", ${submitted.size()} of $userCount submitted":""}"
    }


}
