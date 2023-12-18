package edu.msu.mi.loom

import com.amazonaws.mturk.requester.QualificationRequirement
import grails.transaction.Transactional
import groovy.util.logging.Slf4j
import edu.msu.mi.loom.utils.ArrayUtil

@Slf4j
@Transactional
class TrainingSetService {

    def simulationService
    def mturkService
    def adminService
    def experimentService

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


        Training training
        json.eachWithIndex { tr, idx ->

            def story = adminService.createStory(tr.name, tr.solution)
            training = new Training(name: tr.name, story: story)
            training.save(flush: true)

            log.debug("New training with id ${training.id} has been created for trainingSet ${trainingSet.name}.")
            def tiles = story.tiles
            for (int i in tr.problem) {
                new TrainingTask(training: training, tile: tiles[i]).save(flush: true)
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
            log.debug("Process ${surveyInstance.name}")
            Survey survey = new Survey(name: surveyInstance.name,
                    likert: surveyInstance?.likert?:false,
                    instructions: surveyInstance?.instructions
            )

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

        log.debug("Training responses thus far are ${uts.trainingResponses*.constraintProvider}")

        if (uts.trainingResponses) {
            trainables.removeAll(uts.trainingResponses*.constraintProvider)
        }
        if (uts.simulationResponses) {
            trainables.removeAll(uts.simulationResponses*.constraintProvider)
        }
        if (uts.readingResponses) {
            trainables.removeAll(uts.readingResponses*.constraintProvider)
        }
        if (uts.surveyReponses) {
            trainables.removeAll(uts.surveyReponses*.constraintProvider)
        }

        return trainables.size()>0?trainables[0]:null


    }


    def completeTrainingSet(UserTrainingSet uts) {
        uts.complete = true
        uts.trainingEndTime = new Date()
        uts.save(flush: true)

//        if (uts.mturkAssignment) {
//            CrowdServiceCredentials creds = uts.mturkAssignment.getHit().getTask().credentials
//            uts.getTrainingSet()
//            mturkService.assignQualification()
//
//        }

    }

    def launchTrainingSet(TrainingSet ts, MturkTask task) {
        ts.state = TrainingSet.State.AVAILABLE

        if (task) {
            Collection<QualificationRequirement> qualRequirements = mturkService.getDefaultQualifications()
            mturkService.launchMturkTask(qualRequirements,task)
        }

        if (!ts.save(flush: true)) {
            return ts.errors
        } else {
            return null
        }
    }

    def cancelTrainingSet(TrainingSet ts) {
        ts.state = TrainingSet.State.PENDING
        mturkService.forceHITExpiry(ts.mturkTasks as MturkTask[])
        if (!ts.save(flush: true)) {
            return ts.errors
        } else {
            return null
        }
    }

    def calculateTrainingScore(List<Long> tileorder,Collection<Tile> tiles) {
       List<Long> correctorder = tiles.sort {
           it.text_order
       }.collect {
           it.id
       }
        return experimentService.score(correctorder,tileorder)
    }



}


