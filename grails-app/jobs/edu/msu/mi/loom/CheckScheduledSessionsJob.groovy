package edu.msu.mi.loom

class CheckScheduledSessionsJob {
    def sessionService

    static triggers = {
        simple repeatInterval: 60000 // execute job once in 60 seconds
    }

    def execute(context) {
        sessionService.checkAndUpdateScheduledSessions(context)
    }
}