package edu.msu.mi.loom

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.BAD_REQUEST

@Secured("ROLE_USER")
class ExperimentController {
    static allowedMethods = [
            submitTraining: 'POST'
    ]

    def experimentService

    def submitTraining() {
        def userTails = params.tails
        List<String> tailsList = Arrays.asList(userTails.split(";"));

        def trainingId = params.training

        if (trainingId) {
            def training = Training.findById(trainingId)
            String trainingName = training.name
            def seqNumber = trainingName.split("[^0-9]+")[1]

            def story = Story.findByTraining(training)
            def tails = Tail.findAllByStory(story)
            if (tails.text.equals(tailsList)) {
                redirect(action: 'nextTraining', params: [seqNumber: seqNumber, session: training?.session?.id])
                return
            }
        }

        render(status: BAD_REQUEST)
    }

    def nextTraining() {
        def sessionId = params.session
        if (sessionId) {
            def expSession = Session.get(Long.parseLong(sessionId))
            if (expSession && params.seqNumber) {
                def training = experimentService.getNextTraining(expSession, Integer.parseInt(params.seqNumber))
                if (training) {
                    session["seqNumber"] = params.seqNumber
                    def tts = TrainingTask.findAllByTraining(training).tail
                    render(template: '/home/content', model: [tts: tts, training: training])
                    return
                } else {
                    session["seqNumber"] = null
                    redirect(action: 'simulation', params: [session: sessionId])
                    return
                }
            }
        }

        redirect(uri: '/not-found')
    }

    def simulation() {
        def sessionId = params.session

        if (sessionId) {
            def session = Session.get(Long.parseLong(sessionId))
            if (session) {
            }
        }

        redirect(uri: '/not-found')
    }
}
