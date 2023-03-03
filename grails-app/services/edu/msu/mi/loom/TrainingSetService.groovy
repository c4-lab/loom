package edu.msu.mi.loom

import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import edu.msu.mi.loom.utils.ArrayUtil

@Slf4j
@Transactional
class TrainingSetService {

    def simulationService
    def mturkService

    def createTrainingSet(def json, def name, def uiflag) {
        Session.withNewTransaction { status ->

            def trainingSet = new TrainingSet(name: name, uiflag: uiflag)


            trainingSet.save(flush: true)

//            Training creation
            if (json.training.practices != null) {
                createTrainings(json.training.practices, trainingSet)
            }

//            Simulation creation
            if (json.training.simulations != null) {
                simulationService.createSimulations(json.training.simulations, trainingSet)
            }

            if (json.training.readings != null) {
                createReading(json.training.readings, trainingSet)
            }

            if (json.training.surveys != null) {
                createSurvey(json.training.surveys, trainingSet)
            }


            if (trainingSet.save(flush: true)) {
                trainingSet.save(flush: true)
                log.debug("New trainingSet with id ${trainingSet.id} has been created.")
                return trainingSet
            } else {
                status.setRollbackOnly()
                log.error("Session creation attempt failed")
                log.error(trainingSet?.errors?.dump())
                return null
            }
        }
    }

    /**
     * Accepts a json list containing one or more training practice instances
     * @param json
     * @param trainingSet
     * @return
     */
    def createTrainings(def json, TrainingSet trainingSet) {
        def tile
        def story
        Training training
        json.eachWithIndex { tr, idx ->
            training = new Training(name: tr.name).save(flush: true)


            log.debug("New training with id ${training.id} has been created for trainingSet ${trainingSet.name}.")
            def storyId = Story.count() + 1

            story = new Story(title: "Story " + tr.name).save(flush: true)

            story.save()

            training.addToStories(story)
            for (int i = 0; i < tr.problem.size(); i++) {
                tile = new Tile(text: tr.solution.get(i), text_order: i)
                if (tile.save(flush: true)) {
                    story.addToTiles(tile).save(flush: true)
                    log.debug("New task with id ${tile.id} has been created.")
                } else {
                    log.error("Task creation attempt failed")
                    log.error(training?.errors?.dump())
                }
            }

            def tiles = Tile.findAllByStory(story)
            for (int i = 0; i < tr.problem.size(); i++) {
                new TrainingTask(training: training, tile: tiles.get(tr.problem.get(i))).save(flush: true)
            }
            if (!training.save(flush: true)) {
                log.error("Training creation attempt failed")
                log.error(training?.errors?.dump())
                return null;
            }
            if (trainingSet) {
                trainingSet.addToTrainings(training)
            }
        }
    }

    def createReading(def readingJson, TrainingSet trainingSet = null) {

        readingJson.each { readingInstance ->


            Reading reading = null
            reading = new Reading(name: readingInstance.name, passage: readingInstance.passage, dateCreated: new Date())
            readingInstance.questions.eachWithIndex {
                question, idxx ->
                    ArrayList<String> options = ArrayUtil.convert(question.options);
                    ArrayList<Integer> corrects = ArrayUtil.convert(question.corrects);
                    question = new ReadingQuestion(question: question.question, reading: reading, options: options, corrects: corrects)
                    reading.addToQuestions(question)
            }
            if (!reading.save(flush: true)) {
                log.error("Reading creation attempt failed")
                log.error(reading?.errors?.dump())
                return null;
            }
            if (trainingSet) {
                trainingSet.addToReadings(reading)
            }
        }

    }

    def createSurvey(def surveyJson, TrainingSet trainingSet = null) {

        surveyJson.each { surveyInstance ->
            Survey survey = new Survey(name: surveyInstance.name)

            surveyInstance.items.eachWithIndex { item, idx ->
                SurveyItem surveyitem = new SurveyItem(survey: survey, question: item.question)
                survey.addToSurveyItems(surveyitem)
                def options = item.options
                options.eachWithIndex { opt, idxx ->
                    SurveyOption so = new SurveyOption(answer: opt.answer, score: opt.score)
                    surveyitem.addToOptions(so)
                }
            }


            if (!survey.save(flush: true)) {
                log.error("Survey creation attempt failed")
                log.error(survey?.errors?.dump())
                return null;
            }

            if (trainingSet) {
                trainingSet.addToSurveys(survey)
            }
        }
    }


    /**
     * Gets the next available traininable item in a training set
     *
     * @param uts
     * @return
     */
    Trainable getNextTraining(UserTrainingSet uts) {
        TrainingSet ts = uts.trainingSet
        List<Trainable> trainables = []

        trainables.addAll(ts.readings)
        trainables.addAll(ts.surveys)
        trainables.addAll(ts.trainings)
        trainables.addAll(ts.simulations)

        trainables.removeAll(uts.trainingResponse)
        Collection<Simulation> simsCompleted = uts.simulationsCompleted.collect {
            it.simulation
        }
        trainables.removeAll(simsCompleted)
        trainables.removeAll(uts.readingCompleted)
        trainables.removeAll(uts.surveyResponse.collect{it.survey} as Set)

        return trainables.size()>0?trainables[0]:null


    }


    def completeTrainingSet(UserTrainingSet uts) {
        uts.complete = true
        uts.trainingEndTime = new Date()
        uts.save(flush: true)
    }


}


