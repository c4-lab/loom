package edu.msu.mi.loom

import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.support.GenericApplicationContext
import grails.util.Holders

class ApplicationStarter implements ApplicationListener<ContextRefreshedEvent> {

    def sessionService
    private boolean initialized = false

    @Override
    void onApplicationEvent(ContextRefreshedEvent event) {
        println("RECEIVING INIT EVENT!!!")
//        if (!initialized) {
//            println "Scheduling application initialization"
//            // Schedule the initialization to occur after a short delay
//            Thread.start {
//                // Wait for Grails to be fully initialized
//                while (!Holders.grailsApplication) {
//                    Thread.sleep(1000)
//                }
//                // Wait a bit more to ensure all is ready
//                Thread.sleep(5000)
//
//                // Now initialize
//                Holders.grailsApplication.mainContext.sessionService.initializeScheduledSessions()
//            }
//            initialized = true
//        } else {
//            println "Application already initialized, skipping (subsequent ContextRefreshedEvent)"
//        }
    }
}
