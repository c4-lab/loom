package edu.msu.mi.loom

/**
 * Reflects a task that is posted on a crowd service
 */
class CrowdServiceTask {

    static constraints = {
        description nullable: true
    }

    CrowdServiceCredentials serviceCredentials
    String title
    String description
    Date launchDate
    Date expireDate





}
