import edu.msu.mi.loom.ApplicationStarter
import edu.msu.mi.loom.CustomAuthSuccessHandler
import edu.msu.mi.loom.CustomLogoutSuccessHandler
import edu.msu.mi.loom.SessionService
import edu.msu.mi.loom.process.RandomStringGenerator
import edu.msu.mi.loom.Session
import grails.plugin.springsecurity.SpringSecurityUtils
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler

// Place your Spring DSL code here
beans = {
    randomStringGenerator(RandomStringGenerator)

    securityContextLogoutHandler(SecurityContextLogoutHandler) {
        invalidateHttpSession = true
    }


    authenticationSuccessHandler(CustomAuthSuccessHandler) {
        log.debug("authenticationSuccessHandler: should be handling a successful login")
        def config = SpringSecurityUtils.securityConfig
        requestCache = ref('requestCache')
        defaultTargetUrl = config.successHandler.defaultTargetUrl
        log.debug("authenticationSuccessHandler: should be handling a successful login with default url "+defaultTargetUrl)
        alwaysUseDefaultTargetUrl = config.successHandler.alwaysUseDefault
        targetUrlParameter = config.successHandler.targetUrlParameter
        useReferer = config.successHandler.useReferer
        redirectStrategy = ref('redirectStrategy')
        adminUrl = "/admin/board"
        userUrl = "/home/index"
    }

    logoutSuccessHandler(CustomLogoutSuccessHandler) {
        redirectStrategy = ref('redirectStrategy')
        grailsApplication = ref('grailsApplication')
    }

    session(Session) { bean ->
        bean.scope = "prototype"
        sessionService = ref('sessionService')
    }

    scheduledAnnotationBeanPostProcessor(ScheduledAnnotationBeanPostProcessor)




}
