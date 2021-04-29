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
    def init = { servletContext ->
        environments {
            development {

                createInitialRecords()

                def trainingset = trainingSetService.createTrainingSet(parseTrainingSessionToText(),"TrainingSet 1","simulation;-;-",0.1,1);
                trainingSetService.createTrainingSet(parseTrainingSessionToText(),"TrainingSet 2","simulation;read;survey",0.1,0);
                mturkService.createQualification("loomreadings", 'reading score')
                mturkService.createQualification("loomsurveys", 'survey score')

                adminService.createExperiment("Experiment 1",Story.get(1),2,2,1,1,
                        2,Experiment.Network_type.Lattice,3,10,
                "",TrainingSet.get(1), 2, 0,
                0.1,0.1,0.1,0.1,1)

                adminService.createExperiment("Experiment 2",Story.get(1),2,2,1,1,
                        2,Experiment.Network_type.Lattice,3,10,
                        "",TrainingSet.get(1), 2, 0,
                        0.1,0.1,0.1,0.1,0)

                def session1 = adminService.createSession(Experiment.get(1),trainingset)
                sessionService.launchSession(session1.id)
                def session2 = adminService.createSession(Experiment.get(2),trainingset)
                sessionService.launchSession(session2.id)

                createTestUsers(trainingset)


            }

            production {
                TrainingSet.list().each {
                    mturkService.createQualification(it as TrainingSet, 'loom training')
                }
                mturkService.createQualification("loomreadings", 'reading score')
                mturkService.createQualification("loomsurveys", 'survey score')
            }
        }
    }
    def destroy = {
    }

    private static void createInitialRecords() {
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
//        mturkService.assignQualification("A3FTY9DQKKJ002","3CNIZ8EIUVQZYD8YHMEU9ANVZY73BK",500)
        mturkService.assignQualification("A3FTY9DQKKJ002","loomsimulations1",1)
        mturkService.assignQualification("A3FTY9DQKKJ002","loomreadings",1)
        mturkService.assignQualification("A3FTY9DQKKJ002","loomsurveys",1)

        (1..10).each { n ->
//            def user = new User(username: "user-${n}", password: "pass", turkerId: "A39D6U8W1FJEJ3").save(failOnError: true)
            def user = new User(username: "user-${n}", password: "pass", turkerId: "A3FTY9DQKKJ002").save(failOnError: true)
            def role = Role.findByAuthority(Roles.ROLE_USER.name)
            UserRole.create(user, role, true)
//            UserTrainingSet.create(user,ts,true,true)
        }
        def user = new User(username: "user-${11}", password: "pass", turkerId: "A2YZSRSEBX1FDU").save(failOnError: true)
        def role = Role.findByAuthority(Roles.ROLE_USER.name)
        UserRole.create(user, role, true)
//        UserTrainingSet.create(user,ts,true,true)

        // create users without training
        user = new User(username: "user-${12}", password: "pass").save(failOnError: true)
        role = Role.findByAuthority(Roles.ROLE_USER.name)
        UserRole.create(user, role, true)
    }

    private HashMap<String, List<String>> parseNodeStoryMap(String name) {
        def filePath = "data/${name}"
        def inputStream = grailsApplication.getParentContext().getResource("classpath:$filePath").getInputStream()
        return graphParserService.parseGraph(inputStream)

    }

    private JSONElement parseTrainingSessionToText() {
        def filePath = "data/session_2/trainingset2.json"
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
