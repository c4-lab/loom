package edu.msu.mi.loom


import grails.transaction.Transactional
import org.springframework.web.multipart.MultipartFile

@Transactional
class AdminService {

    def fileService
    def jsonParserService

    static String APPLICATION_BASE_URL = null
    //This should get populated on the very first request from @AdminController


    def createSessionParameters(Map initParams) {

        SessionParameters params = new SessionParameters(initParams)
        def tests = initParams.get("constraints")
        if (tests) {
            tests.each {
                params.addToConstraintTests(it)
            }

        }
        if (!params.save(flush: true)) {
            log.error("Session params creation attempt failed")
            log.error(params.errors)
            return null
        } else {
            return params
        }
    }


    /**
     * Create an initial experiment
     * @param json
     * @param session
     * @return
     */
    def createExperiment(String name, SessionParameters defaultParams) {

        Experiment experiment = new Experiment(name: name, created: new Date(), defaultSessionParams: defaultParams).save()

    }

    def createStory(def title, def storyText, def storySeed) {
        println("Creating story with ${storySeed}")
        def tile
        Story story = new Story(name: title)


        ((List) storyText).eachWithIndex { line, count ->
            if (line && ((String) line).trim().length()) {
                tile = new Tile(text: line, text_order: count).save()
                story.addToTiles(tile)
            }
        }
        StorySeed seed = null
        if (storySeed != null) {
            seed = StorySeed.findByName(storySeed)
            if (!seed) {
                seed = new StorySeed(name: storySeed)
                //seed.save(flush:true)

            }

        }


        story.save(flush: true)
        if (seed) {
            seed.save(flush: true)
            story.seed = seed
            story.save(flush: true)
        }

        if (!story.id) {
            log.error("Story creation attempt failed")
            log.error(story?.errors?.dump())
            return null
        } else {
            return story
        }
    }


    def cloneExperiment(Session session) {
        Session sessionClone = session.clone()

        if (sessionClone.save(flush: true)) {
            log.debug("Session clone has been created with id " + sessionClone.id)
            return sessionClone
        } else {
            log.debug("There was problem with expSession cloning ")
            log.error(sessionClone?.errors?.dump())
            return null
        }
    }

    def deleteExperiment(def id, def type) {
        def source
        switch (type) {
            case ExpType.TRAINING.toString():
                source = Training.get(id)
                deleteTrainingTasks(source)
                break
            case ExpType.SIMULATION.toString():
                source = Simulation.get(id)
                deleteSimulationTasks(source)
                break
            case ExpType.EXPERIMENT.toString():
                source = Experiment.get(id)
                deleteExperimentTasks(source)
                deleteUserStories(source)
                break
            case ExpType.SESSION.toString():
                source = Session.get(id)
                deleteExperimentTasks(source?.exp)
                deleteUserStories(source?.exp)
                deleteUserRoundStories(source)
                deleteUserSeesion(source)


                break
        }
        if (source) {
            source.delete(flush: true)
            log.info("Object ${type} with id ${id} has been deleted.")
            return true
        } else {
            return false
        }
    }

    def uploadJsonFile(MultipartFile file) {
        def text = fileService.readFile(file as MultipartFile)
        def json = jsonParserService.parseToJSON(text)
        return json

    }

    /**
     * This is a one-off method, intended to fix duplication with identically names constraints in the database
     * It should really only be executed if you know what you're doing!*/
    def fixDuplicateConstraints() {
        println("Fixing duplicates - this might take a while")
        log.debug("In fix duplicates...")
        int value_updated = 0
        int test_updated = 0
        int providerCount = 0
        List<ConstraintProvider> toDelete = []

        Map<String, List<ConstraintProvider>> providers = [:]
        ConstraintProvider.findAll().each { ConstraintProvider cp ->
            if (!cp.name.startsWith("x_")) {
                if (!(cp.constraintTitle in providers)) {
                    providers[cp.constraintTitle] = [cp]
                } else {
                    providers[cp.constraintTitle] << cp
                }
            }
        }


        providers.each {
            if (it.value.size() > 1) {
                providerCount += 1
                List<ConstraintProvider> allProviders = it.value
                ConstraintProvider target = allProviders.min {
                    it.id
                }

                allProviders.remove(target)
                log.debug("${allProviders.size()} duplicate providers for ${target.constraintTitle}")
                allProviders.each {
                    toDelete << it
                    UserConstraintValue.findAllByConstraintProvider(it).each {
                        if (it.constraintProvider != target) {
                            value_updated += 1
                            log.debug("Updating value of ${it.constraintProvider.constraintTitle} for user ${it.user.username}")
                            it.constraintProvider = target
                            it.save()
                        }
                    }

                    ConstraintTest.findAllByConstraintProvider(it).each {
                        if (it.constraintProvider != target) {
                            test_updated += 1
                            log.debug("Updating value of ${it.constraintProvider.constraintTitle} for test ${it}")
                            it.constraintProvider = target
                            it.save()
                        }
                    }

                }
            }
        }

        toDelete.each {
            it.constraintTitle = "x_${it.constraintTitle}"
            it.name = "x_${it.name}"
            it.save()
        }


        println("Updated ${value_updated} constraint value associations and ${test_updated} tests for ${providerCount} providers")
        println("Can delete: ")
        toDelete.each {
            println("${it.constraintTitle}:${it.id}")
        }

    }

    private def deleteTrainingTasks(source) {
        def tts = TrainingTask.findAllByTraining(source)
        tts.each { tt -> tt.delete()
        }
    }

    private def deleteSimulationTasks(source) {
        def sts = SimulationTask.findAllBySimulation(source)
        sts.each { st -> st.delete()
        }
    }

    private def deleteExperimentTasks(source) {
        def ets = ExperimentTask.findAllByExperiment(source)
        ets.each { et -> et.delete()
        }
    }

    private def deleteUserStories(source) {
        def us = SessionInitialUserStory.findAllByExperiment(source)
        us.each { it.delete() }
    }

    private def deleteUserRoundStories(source) {
        def us = UserRoundStory.findAllBySession(source)
        us.each { it.delete() }
    }

    private def deleteUserSeesion(source) {
        def us = UserSession.findAllBySession(source)
        us.each { it.delete() }
    }

    void deleteCredential(def id) {
        CrowdServiceCredentials.get(id).delete()
    }

}
