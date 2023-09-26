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


    ExperimentRoundStatus(int userCount, int roundCount, long pauseLength=10000l) {
        this.userCount = userCount
        this.roundCount = roundCount
        this.pauseLength = pauseLength
        this.roundStart = new Date()


    }

    boolean isOverTime() {
        (System.currentTimeMillis() - pauseStart) >= pauseLength
    }

    boolean isAllSubmitted() {
        log.debug("Query all submitted; currently have: ${submitted}")
        submitted.size()>=userCount
    }

    def advanceRound() {
        log.debug("ExperimentRoundStatus advancing round from $round to ${round+1}")
        round++
        if (isFinished()) {
            currentStatus = Status.FINISHED
        } else {
            currentStatus = Status.ACTIVE
            submitted.clear()
            this.roundStart = new Date()
        }
    }



    def submitUser(userId) {
        //TODO This was synchronized at some point; I've removed
        //TODO this to avoid lock contention, but uncertain if there was a good reason for
        //TODO synchronization originally.  Only possible other modification (afaict) is the "clear" that happens
        //TODO above
        submitted<<userId
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
