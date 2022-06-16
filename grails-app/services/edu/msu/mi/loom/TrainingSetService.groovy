package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import edu.msu.mi.loom.utils.ArrayUtil

@Slf4j
@Transactional
class TrainingSetService {

    def simulationService
    def mturkService

    def createTrainingSet(def json, def name, def qualifier, def training_payment, def uiflag) {
        Session.withNewTransaction { status ->

            def trainingSet = new TrainingSet(name: name, qualifier: qualifier, training_payment:training_payment, uiflag: uiflag)

            if (trainingSet.save(flush: true)) {
                trainingSet.save(flush: true)
                log.debug("New trainingSet with id ${trainingSet.id} has been created.")

//            Training creation
                if (json.training.practice != null) {
                    createTraining(json.training.practice, trainingSet)
                }

//            Simulation creation
                if (json.training.simulation != null) {
                    simulationService.createSimulation(json.training.simulation, trainingSet)
                }

                if (json.training.reading != null){
                    createReading(json.training.reading, trainingSet)
                }

                if (json.training.survey != null){
                    createSurvey(json.training.survey, trainingSet)
                }

                mturkService.createQualification(trainingSet,"loom training")
                return trainingSet
            } else {
                status.setRollbackOnly()
                log.error("Session creation attempt failed")
                log.error(trainingSet?.errors?.dump())
                return null
            }
        }
    }

    def createTraining(def json, TrainingSet trainingSet) {
        def tail
        def story
        Training training
        json.eachWithIndex { tr, idx ->
            training = new Training(name: "Training ${(idx + 1)}", trainingSet: trainingSet)
            //if (training.save(flush: true)) {
            trainingSet.addToTrainings(training)
            log.debug("New training with id ${training.id} has been created for trainingSet ${trainingSet.name}.")
            def storyId = Story.count() + 1
            story = new Story(title: "Story "+storyId.toString()).save(flush: true)
            mturkService.createQualification(story, "loom story")
            training.addToStories(story)
            for (int i = 0; i < tr.problem.size(); i++) {
                tail = new Tile(text: tr.solution.get(i), text_order: i)
                if (tail.save(flush: true)) {
                    story.addToTails(tail).save(flush: true)
                    log.debug("New task with id ${tail.id} has been created.")
                } else {
                    log.error("Task creation attempt failed")
                    log.error(training?.errors?.dump())
                }
            }

            def tails = Tile.findAllByStory(story)
            for (int i = 0; i < tr.problem.size(); i++) {
                new TrainingTask(training: training, tail: tails.get(tr.problem.get(i))).save(flush: true)
            }
            if (!trainingSet.save(flush: true)) {
                log.error("Training creation attempt failed")
                log.error(trainingSet?.errors?.dump())
                return null;
            }
        }
    }

    def createReading(def json, TrainingSet trainingSet){

        Reading reading
        json.eachWithIndex { read, idx ->
            def a = read.passage
            reading = new Reading(name: "Reading ${(idx + 1)}", trainingSet: trainingSet, passage: read.passage)
            read.questions.eachWithIndex {
                question, idxx ->
                    ArrayList<String> options = ArrayUtil.convert(question.options);
                    ArrayList<Integer> corrects = ArrayUtil.convert(question.corrects);
                    question = new ReadingQuestion(question: question.question, reading: reading, options: options,corrects: corrects)
                    reading.addToQuestions(question)
            }
//            if(!reading.save(flush: true)){
//                log.error(reading?.errors?.dump())
//            }
            trainingSet.addToReadings(reading)
            log.debug("New reading with id ${reading.id} has been created for trainingSet ${trainingSet.name}.")
            if (!trainingSet.save(flush: true)) {
                log.error("Reading creation attempt failed")
                log.error(trainingSet?.errors?.dump())
                return null;
            }
        }
    }

    def createSurvey(def json, TrainingSet trainingSet){

        Survey survey
        json.eachWithIndex { sur, idx ->
            survey = new Survey(question: sur.question, trainingSet: trainingSet)
            def options = sur.options
            options.eachWithIndex { opt, idxx ->
                SurveyOption so = new SurveyOption(answer: opt.answer, score: opt.score)
                survey.addToOptions(so)

            }


            trainingSet.addToSurveys(survey)
            log.debug("New survey with id ${survey.id} has been created for trainingSet ${trainingSet.name}.")
            if (!trainingSet.save(flush: true)) {
                log.error("Reading creation attempt failed")
                log.error(trainingSet?.errors?.dump())
                return null;
            }
        }
    }

    Training getNextTraining(User u, TrainingSet ts) {
        Training training
        log.debug("Trainings are $ts.trainings")
        def completed = UserTrainingSet.findByUserAndTrainingSet(u,ts)?.trainingsCompleted?:[]
        log.debug("Completed are $completed")
        def trainingLst = ts.trainings - completed
        if (trainingLst) {
            training = trainingLst.getAt(0)
        }
        return training
    }

    def changeTrainingState(User u, Training training, Simulation simulation, Reading reading, List<UserSurveyOption> surveyOptions, def trainingSetId=null, def score=null) {
        TrainingSet ts = null
        if (training) {
            ts = training.trainingSet
        } else if (simulation) {
            ts = simulation.trainingSet
        } else if (reading){
            ts = reading.trainingSet


        } else if (surveyOptions){
            println("Survey options are ${surveyOptions}")
            ts = surveyOptions.first().userTrainingSet.trainingSet

        } else if(trainingSetId){
            ts = TrainingSet.get(trainingSetId)
        }
        else {
            log.error("Cannot advance training state without a either a simulation or training")
        }
        UserTrainingSet uts = UserTrainingSet.findByUserAndTrainingSet(u, ts)


//        if (!uts) {
//
//            uts = new UserTrainingSet(user: u, trainingSet: ts, trainingStartTime: new Date(), isDemographicsComplete: true)
//            uts.save(flush: true)
//
//        }

        if (reading){
            uts.addToReadingCompleted(reading)
            uts.readingScore = score
//            mturkService.assignQualification(u.turkerId, "loomreadings",score)

        }
        if (surveyOptions){
            def myscore = 0
            surveyOptions.each {
                uts.addToSurveyAnswers(it)
                myscore+=it.surveyOption.score
            }

            (surveyOptions.collect {
                it.surveyOption.survey
            } as Set).each {
                uts.addToSurveyCompleted(it)
            }
            uts.surveyScore = myscore
//            mturkService.assignQualification(u.turkerId, "loomsurveys",score)
        }
        if (training && (!uts.trainingsCompleted || !uts.trainingsCompleted.contains(training))) {
            uts.addToTrainingsCompleted(training)
        }


        boolean simsCompleted = uts.simulationsCompleted && (uts.simulationsCompleted.first().scores.size() == ts.simulations.first().roundCount)


        def trainings = ts.trainings - uts.trainingsCompleted?:[]
        List<String> qualifiers = ts.qualifier.split(";")
        def readingcompleted = (qualifiers.get(1).contains("-") || (!qualifiers.get(1).contains("-") && uts.readingCompleted))
        def surveycompleted = (qualifiers.get(2).contains("-") || (!qualifiers.get(2).contains("-") && uts.surveyCompleted))
        if (simsCompleted && trainings.isEmpty() && readingcompleted && surveycompleted) {

            completeTraining(uts)

        }

        uts.save(flush: true)
    }

    def completeTraining(UserTrainingSet uts) {
        uts.complete = true
        uts.trainingEndTime = new Date()
        def scores = uts.simulationsCompleted.first().scores


    }


}


