import edu.msu.mi.loom.Role
import edu.msu.mi.loom.Roles
import edu.msu.mi.loom.User
import edu.msu.mi.loom.UserRole
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONElement

class BootStrap {
    def experimentService
    def grailsApplication
    def graphParserService
    def roomService

    def init = { servletContext ->
        environments {
            development {
                createInitialRecords()
                createTestUsers()
                def session = experimentService.createSession(parseJSONToText())
                final File file = new File("grails-app/conf/data/session_2/example.graphml")
                InputStream inputStream = new FileInputStream(file)
                HashMap<String, List<String>> nodeStoryMap = graphParserService.parseGraph(inputStream)

                experimentService.completeExperiment(nodeStoryMap, session.experiments.getAt(0).id)

                roomService.createRoom(session)
            }
        }
    }
    def destroy = {
    }

    private void createInitialRecords() {
        def adminRole = Role.findWhere(authority: Roles.ROLE_ADMIN.name) ?: new Role(authority: Roles.ROLE_ADMIN.name).save(failOnError: true)
        def creatorRole = Role.findWhere(authority: Roles.ROLE_CREATOR.name) ?: new Role(authority: Roles.ROLE_CREATOR.name).save(failOnError: true)
        def userRole = Role.findWhere(authority: Roles.ROLE_USER.name) ?: new Role(authority: Roles.ROLE_USER.name).save(failOnError: true)

        def admin = User.findWhere(username: 'admin') ?: new User(username: 'admin', password: '1').save(failOnError: true)


        if (!admin.authorities.contains(adminRole)) {
            UserRole.create(admin, adminRole)
        }

        if (!admin.authorities.contains(creatorRole)) {
            UserRole.create(admin, creatorRole)
        }
    }

    private void createTestUsers() {
        (1..11).each { n ->
            def user = new User(username: "user-${n}", password: "pass").save(failOnError: true)
            def role = Role.findByAuthority(Roles.ROLE_USER.name)
            UserRole.create(user, role, true)
        }
    }

    private JSONElement parseJSONToText() {
        def filePath = "data/session_2/experiment.json"
        def text = grailsApplication.getParentContext().getResource("classpath:$filePath").getInputStream().getText()
        def json = JSON.parse(text)

        return json
    }
}
