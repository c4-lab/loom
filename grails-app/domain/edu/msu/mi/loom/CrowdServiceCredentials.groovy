package edu.msu.mi.loom

class CrowdServiceCredentials {


    String access_key
    String secret_key
    String name

    CrowdService serviceType
    boolean sandbox


    static belongsTo = [user: User]


    static constraints = {

        sandbox nullable: true
    }

    static CrowdServiceCredentials create(User user, String name, String accessKey, String secretKey, CrowdService crowdService, boolean flush = false) {
        def instance = new CrowdServiceCredentials(user: user, name: name, access_key: accessKey, secret_key: secretKey, serviceType: crowdService)
        instance.save(flush: flush, insert: true)
        instance
    }

    String getFormattedName() {
        String postfix = serviceType==CrowdService.MTURK?"(${sandbox?'sandbox':'production'})":""
        "${name}${postfix}"
    }

    String toString() {
        return getFormattedName()
    }
}
