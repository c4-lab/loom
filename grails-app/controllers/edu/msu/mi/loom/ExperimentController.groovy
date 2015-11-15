package edu.msu.mi.loom

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

@Secured("ROLE_USER")
class ExperimentController {
    static allowedMethods = [
            submitTraining: 'POST'
    ]

    def submitTraining() {
        def userTails = params.tails
        List<String> tailsList = Arrays.asList(userTails.split(";"));

        def trainingId = params.training

        if (trainingId) {
            def training = Training.findById(trainingId)
            def story = Story.findByTraining(training)
            def tails = Tail.findAllByStory(story)

            if (tails.text.equals(tailsList)) {
                render(status: OK)
                return
            }
        }

        render(status: BAD_REQUEST)
    }
}
