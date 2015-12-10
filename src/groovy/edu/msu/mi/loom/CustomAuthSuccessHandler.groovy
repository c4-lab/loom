package edu.msu.mi.loom

import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.util.logging.Slf4j
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        boolean hasAdmin = SpringSecurityUtils.ifAllGranted(Roles.ROLE_ADMIN.name)
        boolean hasUser = SpringSecurityUtils.ifAllGranted(Roles.ROLE_USER.name)

        if (hasAdmin) {
            log.debug("Return admin URL: "+adminUrl)
            return adminUrl
        } else if (hasUser) {
            log.debug("Return user URL: "+userUrl)
            return userUrl
        } else {
            return super.determineTargetUrl(request, response)
        }
    }

    private String userUrl
    private String adminUrl

    void setUserUrl(String userUrl) {
        this.userUrl = userUrl
    }

    void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl
    }
}
