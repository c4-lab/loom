package edu.msu.mi.loom


import grails.transaction.Transactional

@Transactional
class AdminService {


    def randomStringGenerator
    def networkGenerateService
    def mturkService


    def createSession(Experiment experiment, TrainingSet trainingSet, String type = "mturk") {
        Session.withNewTransaction { status ->
            def session = new Session(name: 'Session_' + (Session.count() + 1), exp: experiment, trainingSet: trainingSet, type: type, startPending: new Date().getTime())
            //adding these here because they don't seem to be called from BootStrap
            session.doneCode = randomStringGenerator.generateLowercase(12)
            session.fullCode = randomStringGenerator.generateLowercase(12)
            session.waitingCode = randomStringGenerator.generateLowercase(12)
            log.debug("About to save session...")

            if (session.save(flush: true)) {
                log.debug("New expSession with id ${session.id} has been created.")
                return session
            } else {
                status.setRollbackOnly()
                log.error("Session creation attempt failed")
                log.error(session?.errors?.dump())
                return null
            }
        }
    }

    /**
     * Create an initial experiment
     * @param json
     * @param session
     * @return
     */
    def createExperiment(String name, Story story, int min_node, int max_nodes, int min_degree, int max_degree,
                         int initialNbrOfTiles, Experiment.Network_type network_type, int rounds, int duration,
                         int m, def probability,
                        int uiflag) {


        Experiment experiment





        Experiment.withSession { status ->

            experiment = new Experiment(name: name,  network_type: network_type,
                    roundTime: duration, roundCount: rounds, initialNbrOfTiles: initialNbrOfTiles, max_node: max_nodes, min_node: min_node,
                    m: m, probability: probability, min_degree: min_degree, max_degree: max_degree, inline: uiflag)


            experiment.addToStories(story)

            if (experiment.save(flush: true)) {

                log.debug("New experiment with id ${experiment.id} has been created.")
                return experiment
            } else {
                log.error("Experiment creation attempt failed")
                log.error(experiment?.errors?.dump())
                return null;
            }
        }
    }

    def createStory(def title, def storyText) {
        def tile
        Story story = new Story(title: title)


        storyText.eachLine { line, count ->
            if (line && ((String)line.trim()).length()) {
                tile = new Tile(text: line, text_order: count)
                story.addToTiles(tile)
            }
        }

        story.save(flush: true)

        if (!story.id) {
            log.error("Story creation attempt failed")
            log.error(story?.errors?.dump())
            return null
        } else {
            return story
        }
    }

//    def createExperiment(def json) {
//        def tail
//        def story
//        Experiment experiment
//
//        Experiment.withSession { status ->
//            def tr = json.stories.first()
//            story = new Story(title: tr.title).save(flush: true)
//
//            for (int i = 0; i < tr.data.size(); i++) {
//                tail = new Tile(text: tr.data.get(i), text_order: i)
//                story.addToTails(tail).save(flush: true)
//                log.debug("New tail with id ${tail.id} has been created.")
//            }
//            experiment = new Experiment(name: "Experiment", story: story,
//                    roundTime: json.timeperround, roundCount: json.numberofrounds, initialNbrOfTiles: json.initialnumberoftiles, userCount: 2)
//
//            if (experiment.save(flush: true)) {
//
//                log.debug("New experiment with id ${experiment.id} has been created.")
//
//
//                experiment.save(flush: true)
//
//                return experiment
//            } else {
//                log.error("Experiment creation attempt failed")
//                log.error(experiment?.errors?.dump())
//                return null;
//            }
//        }
//    }

    def setExperimentNetwork(HashMap<String, List<String>> map, int experimentId) {
        def experiment = Experiment.get(experimentId)

        def idx = 0
        List<Tile> tileSrc = experiment.story.tiles as List<Tile>

        int numTilesPerUser = Math.min(
                Math.max(experiment.initialNbrOfTiles,
                        Math.ceil(tileSrc.size() / map.size())),
                tileSrc.size())


        def nextTile = {
            idx %= tileSrc.size()
            if (idx == 0) {
                Collections.shuffle(tileSrc)
            }
            tileSrc[idx++]
        }

        map.each { String node, List<String> data ->
            def userStory = new ExperimentInitialUserStory(experiment: experiment, alias: node)
            (1..numTilesPerUser).each {
                def t = nextTile()

                if (userStory?.getInitialTiles()?.contains(t)) {
                    log.debug("Not adding tile ${t}")
                    //do nothing?
                } else {
                    userStory.addToInitialTiles(t)
                }
            }
            if (userStory.save(flush: true)) {
                log.debug("New user story with id ${userStory.id} has been created.")
            }
            data.takeRight(data.size() - 1).each {
                def edge = new Edge(source: node, target: it, experiment: experiment).save(failOnError: true)
                log.debug("New edge with id ${edge.id} has been created.")
            }
        }

        experiment.max_node = map.size()
//        experiment.enabled = true
        experiment.save(flush: true)
        return experiment
    }


    def cloneExperiment(Session session) {
        Session sessionClone = session.clone()

        if (sessionClone.save(flush: true)) {
            log.debug("Session clone has been created with id " + sessionClone.id)
            return sessionClone
        } else {
            log.debug("There was problem with expSession cloning ")
            log.error(session?.errors?.dump())
            return null
        }
    }

    def deleteExperiment(def id, def type) {
        def source, ets
        switch (type) {
            case ExpType.TRAINING.toString():
                source = Training.get(id)
                deleteTrainingTasks(source)
                break;
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
            log.info("Session with id ${id} has been deleted.")
            return true
        } else {
            return false
        }
    }

    private def deleteTrainingTasks(source) {
        def tts = TrainingTask.findAllByTraining(source)
        tts.each { tt ->
            tt.delete()
        }
    }

    private def deleteSimulationTasks(source) {
        def sts = SimulationTask.findAllBySimulation(source)
        sts.each { st ->
            st.delete()
        }
    }

    private def deleteExperimentTasks(source) {
        def ets = ExperimentTask.findAllByExperiment(source)
        ets.each { et ->
            et.delete()
        }
    }

    private def deleteUserStories(source) {
        def us = ExperimentInitialUserStory.findAllByExperiment(source)
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
