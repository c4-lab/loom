import edu.msu.mi.loom.*
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.json.JSONElement
import grails.util.Metadata
import org.hibernate.metadata.ClassMetadata

class BootStrap {
    def experimentService
    def grailsApplication
    def graphParserService
    def sessionService
    def trainingSetService
    def mturkService
    def adminService
    def sessionFactory
    def init = { servletContext ->
        //GrailsApplication ga = (GrailsApplication)grailsApplication

       // def allClasses = ga.domainClasses


//        allClasses.each { domainClass ->
//            if (domainClass) {
//                DefaultGrailsDomainClass dc = (DefaultGrailsDomainClass)domainClass
//                Class clazz = domainClass.clazz
//                println "Domain class ${clazz}"
//                dc.associations.each {
//                    if (it.explicitSaveUpdateCascade) {
//                        print("--- Explicit Save or Update Cascade")
//                    }
//                }
//
//                println "Domain class class: ${domainClass.metaClass.class}"
//                println "Domain Meta Class ${domainClass.metaClass}"
//                println ""
//                ClassMetadata classMetadata = sessionFactory.getClassMetadata(domainClass)
//                println "Session Class Metadata ${classMetadata}"
////                domainClass.metaClass.hasMany.each { assocName, assocType ->
////                    def mappedBy = domainClass.metaClass."${assocName}MappedBy"
////                    def cascadeType = domainClass.metaClass."${assocName}Cascade"
////                    println "  hasMany association: ${assocName} (Type: ${assocType}, Mapped By: ${mappedBy}, Cascade: ${cascadeType})"
////                }
////
////                domainClass.metaClass.belongsTo.each { assocName, assocType ->
////                    def mappedBy = domainClass.metaClass."${assocName}MappedBy"
////                    def cascadeType = domainClass.metaClass."${assocName}Cascade"
////                    println "  belongsTo association: ${assocName} (Type: ${assocType}, Mapped By: ${mappedBy}, Cascade: ${cascadeType})"
////                }
////
////                domainClass.metaClass.hasOne.each { assocName, assocType ->
////                    def mappedBy = domainClass.metaClass."${assocName}MappedBy"
////                    def cascadeType = domainClass.metaClass."${assocName}Cascade"
////                    println "  hasOne association: ${assocName} (Type: ${assocType}, Mapped By: ${mappedBy}, Cascade: ${cascadeType})"
////                }
//
//                println() // Print a newline for better readability
//            }
//        }
        environments {
            development {



//                println("------->Mturk service is ${mturkService}")
//                println("------->Mturk client is ${mturkService.getMturkClient()}")
//                createInitialRecords()
//
//                def trainingset = trainingSetService.createTrainingSet(parseTrainingSessionToText(),"TrainingSet 1",1);
//                trainingSetService.createTrainingSet(parseTrainingSessionToText(),"TrainingSet 2","simulation;read;survey",0.1,0);
////                mturkService.createQualification(trainingset.readings.first())
////                mturkService.createQualification(trainingset.surveys.first())
////                mturkService.createQualification(trainingset.simulations.first())
//
//                adminService.createExperiment("Experiment 1",Story.get(1),2,2,1,1,
//                        2,Experiment.Network_type.Lattice,3,1,
//                "",TrainingSet.get(1), 2, 0,
//                0.1,0.1,0.1,0.1,1)
//
//                adminService.createExperiment("Experiment 2",Story.get(1),2,2,1,1,
//                        2,Experiment.Network_type.Lattice,3,1,
//                        "",TrainingSet.get(1), 2, 0,
//                        0.1,0.1,0.1,0.1,0)
//
//                def session1 = adminService.createSession(Experiment.get(1),trainingset)
//                sessionService.launchSession(session1.id)
//                def session2 = adminService.createSession(Experiment.get(2),trainingset)
//                sessionService.launchSession(session2.id)
//
//                createTestUsers(trainingset)
                createInitialRecords()
//                TrainingSet.list().each {
//                    mturkService.createMturkQualification(it as TrainingSet)
//                }
//                mturkService.createMturkQualification(Reading.first())
//                mturkService.createMturkQualification(SurveyItem.first())

            }

            production {
                createInitialRecords()
//                TrainingSet.list().each {
//                    mturkService.createMturkQualification(it as TrainingSet)
//                }
//                mturkService.createMturkQualification(Reading.first())
//                mturkService.createMturkQualification(SurveyItem.first())
            }
        }
    }
    def destroy = {
    }

    private static void createInitialRecords() {
        def adminRole = Role.findWhere(authority: Roles.ROLE_ADMIN.name) ?: new Role(authority: Roles.ROLE_ADMIN.name).save(failOnError: true)
        def creatorRole = Role.findWhere(authority: Roles.ROLE_CREATOR.name) ?: new Role(authority: Roles.ROLE_CREATOR.name).save(failOnError: true)
        def userRole = Role.findWhere(authority: Roles.ROLE_USER.name) ?: new Role(authority: Roles.ROLE_USER.name).save(failOnError: true)
        def mturkerRole = Role.findWhere(authority: Roles.ROLE_MTURKER.name) ?: new Role(authority: Roles.ROLE_MTURKER.name).save(failOnError: true)

        def admin = User.findWhere(username: 'admin') ?: new User(username: 'admin', password: 'lji123').save(failOnError: true)


        if (!admin.authorities.contains(adminRole)) {
            UserRole.create(admin, adminRole)
        }

        if (!admin.authorities.contains(creatorRole)) {
            UserRole.create(admin, creatorRole)
        }

//        if (!admin.authorities.contains(userRole)) {
//            UserRole.create(admin, userRole)
//        }
//
//        if (!admin.authorities.contains(mturkerRole)) {
//            UserRole.create(admin, mturkerRole)
//        }
    }

    private void createTestUsers(TrainingSet ts) {
//        mturkService.assignQualification("A3FTY9DQKKJ002","3CNIZ8EIUVQZYD8YHMEU9ANVZY73BK",500)
        mturkService.assignQualification("A3FTY9DQKKJ002",ts.simulations.first(),1)
        mturkService.assignQualification("A3FTY9DQKKJ002",ts.readings.first(),1)
        mturkService.assignQualification("A3FTY9DQKKJ002",ts.surveys.first(),1)

        (1..10).each { n ->
//            "A3FTY9DQKKJ002"
            def user = new User(username: "user-${n}", password: "pass").save(failOnError: true)
//            def user = new User(username: "user-${n}", password: "pass").save(failOnError: true)
            def role = Role.findByAuthority(Roles.ROLE_USER.name)
            UserRole.create(user, role, true)
            UserTrainingSet.create(user,ts,true,true)
        }
        def user = new User(username: "user-${11}", password: "pass", workerId:"A3FTY9DQKKJ002").save(failOnError: true)
        def role = Role.findByAuthority(Roles.ROLE_MTURKER.name)
        UserRole.create(user, role, true)
        UserTrainingSet.create(user,ts,true,true)

        user = new User(username: "user-${12}", password: "pass", workerId: "A39D6U8W1FJEJ3").save(failOnError: true)
        role = Role.findByAuthority(Roles.ROLE_MTURKER.name)
        UserRole.create(user, role, true)
        UserTrainingSet.create(user,ts,true,true)

        // create users without training
        user = new User(username: "user-${13}", password: "pass").save(failOnError: true)
        role = Role.findByAuthority(Roles.ROLE_USER.name)
        UserRole.create(user, role, true)
        UserTrainingSet.create(user,ts,true,true)
    }

    private HashMap<String, List<String>> parseNodeStoryMap(String name) {
        def filePath = "data/${name}"
        def inputStream = grailsApplication.getParentContext().getResource("classpath:$filePath").getInputStream()
        return graphParserService.parseGraph(inputStream)

    }

    private JSONElement parseTrainingSessionToText() {
        def filePath = "data/session_2/trainingset_sf.json"
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
