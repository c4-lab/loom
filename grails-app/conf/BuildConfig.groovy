grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    // configure settings for the run-app JVM
    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        // runtime 'mysql:mysql-connector-java:5.1.29'
        // runtime 'org.postgresql:postgresql:9.3-1101-jdbc41'
        runtime 'com.tinkerpop.blueprints:blueprints-core:2.6.0'
        runtime group: 'org.mariadb.jdbc', name: 'mariadb-java-client', version: '2.6.0'
        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
        compile 'net.sf.opencsv:opencsv:2.3'
        compile group: 'org.apache.axis', name: 'axis', version: '1.4'
        compile group: 'org.apache.axis', name: 'axis-jaxrpc', version: '1.4'
        compile group: 'org.apache.httpcomponents', name: 'httpclient', version: '4.1.2'
        compile group: 'org.apache.velocity', name: 'velocity', version: '1.5', ext: 'pom'
        compile group: 'velocity-tools', name: 'velocity-tools', version: '1.4'
        compile group: 'commons-httpclient', name: 'commons-httpclient', version: '3.1'
        compile group: 'commons-logging', name: 'commons-logging', version: '1.0.4'
        compile group: 'commons-lang', name: 'commons-lang', version: '2.3'
        compile group: 'commons-digester', name: 'commons-digester', version: '1.8'
        compile group: 'commons-dbcp', name: 'commons-dbcp', version: '1.2.2'
        compile group: 'commons-collections', name: 'commons-collections', version: '3.2'
        compile group: 'commons-pool', name: 'commons-pool', version: '1.3'
        compile group: 'commons-beanutils', name: 'commons-beanutils', version: '1.7.0'
        compile group: 'commons-discovery', name: 'commons-discovery', version: '0.2'

        compile group: 'log4j', name: 'log4j', version: '1.2.15', {
            excludes  group:'com.sun.jmx', module: 'jmxri'
            excludes  group:'com.sun.jdmk', module: 'jmxtools'
            excludes group:'javax.jms', module: 'jms'
        }

        compile group: 'wsdl4j', name: 'wsdl4j', version: '1.5.1'
        compile group: 'org.codehaus.woodstox', name: 'wstx-asl', version: '3.2.3'
        // https://mvnrepository.com/artifact/org.jgrapht/jgrapht-core
        compile group: 'org.jgrapht', name: 'jgrapht-core', version: '1.4.0'
        compile group: 'org.jgrapht', name: 'jgrapht-ext', version: '1.4.0'
        compile group: 'org.jgrapht', name: 'jgrapht-demo', version: '1.4.0'

        // https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk
        compile group: 'com.amazonaws', name: 'aws-java-sdk', version: '1.11.940'
        compile 'org.apache.httpcomponents:httpcore:4.4.13'
        compile 'org.apache.httpcomponents:httpclient:4.5.13'

    }

    plugins {
        // plugins for the build system only
        build ":tomcat:7.0.55.3" // or ":tomcat:8.0.22"

        // plugins for the compile step
        compile ":scaffolding:2.1.2"
        compile ':cache:1.1.8'
        // asset-pipeline 2.0+ requires Java 7, use version 1.9.x with Java 6
        compile ":asset-pipeline:2.14.1"
        // compile group: 'com.bertramlabs.plugins', name:'asset-pipeline-grails', version: '3.3.1'


        // plugins needed at runtime but not for compilation


        compile ':spring-security-core:2.0.0'
        compile ":build-test-data:2.4.0"
        compile ":mail:1.0.7"
        compile ":executor:0.3"

        runtime ":hibernate4:4.3.10" // or ":hibernate:3.6.10.18"
        runtime ":database-migration:1.4.0"
        runtime ":jquery:1.11.1"

        // Uncomment these to enable additional asset-pipeline capabilities
        //compile ":sass-asset-pipeline:1.9.0"
        //compile ":less-asset-pipeline:1.10.0"
        //compile ":coffee-asset-pipeline:1.8.0"
        //compile ":handlebars-asset-pipeline:1.3.0.3"
    }
}
