import edu.msu.mi.loom.*
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONElement

class BootStrap {
    def experimentService
    def grailsApplication
    def graphParserService
    def sessionService
    def trainingSetService
    def mturkService
    def adminService
    def graphGenerateService
    def init = { servletContext ->
        environments {
            development {

//                graphGenerateService.generateGraph()

                createInitialRecords()

                def trainingset = trainingSetService.createTrainingSet(parseTrainingSessionToText(),"A training set","", 2,0.1);

                mturkService.createQualification("loomnumHits", 'number of Hits')
                mturkService.createQualification("loomperformances", 'performance score')
                mturkService.createQualification("loomreadings", 'reading score')
                mturkService.createQualification("loomvaccines", 'vaccine score')
//                mturkService.createQualification(trainingset)
//                def experiment = adminService.createExperiment("exp1", Story.get(1),)

//                createExperiment(String name, Story story,int min_node,int max_nodes,int min_degree,int max_degree,
//                        int initialNbrOfTiles, Experiment.Network_type network_type,int rounds,int duration,
//                def qualifier,TrainingSet training_set, int m, def probability,
//                def accepting,def completion,def waiting,def score)
//                HashMap<String, List<String>> nodeStoryMap = parseNodeStoryMap("session_1/example1.graphml")
//
//                adminService.setExperimentNetwork(nodeStoryMap, experiment.id)
//
//                def session = adminService.createSession(experiment,trainingset)
//
//
//                sessionService.launchSession(session.id)
                createTestUsers(trainingset)


            }

            production {
                TrainingSet.list().each {
                    mturkService.createQualification(it, 'loom training qualification')
                }
            }
        }
    }
    def destroy = {
    }

    private void createInitialRecords() {
        def adminRole = Role.findWhere(authority: Roles.ROLE_ADMIN.name) ?: new Role(authority: Roles.ROLE_ADMIN.name).save(failOnError: true)
        def creatorRole = Role.findWhere(authority: Roles.ROLE_CREATOR.name) ?: new Role(authority: Roles.ROLE_CREATOR.name).save(failOnError: true)
        def userRole = Role.findWhere(authority: Roles.ROLE_USER.name) ?: new Role(authority: Roles.ROLE_USER.name).save(failOnError: true)

        def admin = User.findWhere(username: 'admin') ?: new User(username: 'admin', password: 'lji123').save(failOnError: true)


        if (!admin.authorities.contains(adminRole)) {
            UserRole.create(admin, adminRole)
        }

        if (!admin.authorities.contains(creatorRole)) {
            UserRole.create(admin, creatorRole)
        }
    }

    private void createTestUsers(TrainingSet ts) {
        mturkService.assignQualification("A39D6U8W1FJEJ3")
        (1..10).each { n ->
            def user = new User(username: "user-${n}", password: "pass", turkerId: "A39D6U8W1FJEJ3").save(failOnError: true)
            def role = Role.findByAuthority(Roles.ROLE_USER.name)
            UserRole.create(user, role, true)
            UserTrainingSet.create(user,ts,true,true)


        }
    }

    private HashMap<String, List<String>> parseNodeStoryMap(String name) {
        def filePath = "data/${name}"
        def inputStream = grailsApplication.getParentContext().getResource("classpath:$filePath").getInputStream()
        return graphParserService.parseGraph(inputStream)

    }

    private JSONElement parseTrainingSessionToText() {
        def filePath = "data/session_2/trainingset.json"
        def text = grailsApplication.getParentContext().getResource("classpath:$filePath").getInputStream().getText()
        def json = JSON.parse(text)

        return json
    }

    private JSONElement parseJSONToText() {
        def filePath = "data/session_1/experiment_short.json"
        def text = grailsApplication.getParentContext().getResource("classpath:$filePath").getInputStream().getText()
        def json = JSON.parse(text)

        return json
    }
}
