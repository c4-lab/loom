import edu.msu.mi.loom.CustomAuthSuccessHandler
import edu.msu.mi.loom.process.RandomStringGenerator
import grails.plugin.springsecurity.SpringSecurityUtils

// Place your Spring DSL code here
beans = {
    randomStringGenerator(RandomStringGenerator)

    authenticationSuccessHandler(CustomAuthSuccessHandler) {
        def config = SpringSecurityUtils.securityConfig
        requestCache = ref('requestCache')
        defaultTargetUrl = config.successHandler.defaultTargetUrl
        alwaysUseDefaultTargetUrl = config.successHandler.alwaysUseDefault
        targetUrlParameter = config.successHandler.targetUrlParameter
        useReferer = config.successHandler.useReferer
        redirectStrategy = ref('redirectStrategy')
        adminUrl = "/admin/board"
        userUrl = "/home/index"
    }
}
