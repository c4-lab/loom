import edu.msu.mi.loom.Role
import edu.msu.mi.loom.Roles
import edu.msu.mi.loom.User
import edu.msu.mi.loom.UserRole
import edu.msu.mi.loom.UserTrainingSet
import edu.msu.mi.loom.TrainingSet
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

                def trainingset = trainingSetService.createTrainingSet(parseTrainingSessionToText(),"A training set");
                mturkService.createQualification(trainingset)
                def experiment = adminService.createExperiment(parseJSONToText().experiment)
                HashMap<String, List<String>> nodeStoryMap = parseNodeStoryMap("session_1/example1.graphml")

                adminService.setExperimentNetwork(nodeStoryMap, experiment.id)

                def session = adminService.createSession(experiment,trainingset)
                sessionService.launchSession(session)
                createTestUsers(trainingset)


            }

            production {
              TrainingSet.list().each {
                  mturkService.createQualification(it)
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
        (1..101).each { n ->
            def user = new User(username: "user-${n}", password: "pass").save(failOnError: true)
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
