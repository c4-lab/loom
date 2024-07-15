/* Copyright 2013-2015 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.msu.mi.loom

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import groovy.util.logging.Slf4j
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.web.WebAttributes
import javax.servlet.http.HttpServletResponse

@Slf4j
@Secured('permitAll')
class LoginController {

    /**
     * Dependency injection for the authenticationTrustResolver.
     */
    def authenticationTrustResolver

    /**
     * Dependency injection for the springSecurityService.
     */
    def springSecurityService

    def userService

    /**
     * Default action; redirects to 'defaultTargetUrl' if logged in, /login/auth otherwise.
     */
    def index() {
        //log.debug("${request.contextPath}${config.apf.filterProcessesUrl}")
        if (springSecurityService.isLoggedIn()) {
            log.debug("We are here in login controller")
            redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
        } else {
            redirect action: 'auth', params: params
        }
    }

    /**
     * Show the login page.
     */
    def auth() {


        println params
        def orig = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST")
        String original = orig?.requestURL


        //forward to admin url
        if (request.forwardURI.contains('admin')) {
            println("Authenticating for admin...")
            def config = SpringSecurityUtils.securityConfig
            String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
            println("Sending posturl ${postUrl}")
            return render(view: "admin_auth", model: [postUrl: postUrl, rememberMeParameter: config.rememberMe.parameter])
        }

        //attempt to pull id params from request and determine role
        String workerId = null
        Roles role = null
        String assignmentId = null

        if (orig?.parameters?.workerId) {
            workerId = orig.parameters.workerId[0]
            if (orig.parameters.assignmentId && orig.parameters.assignmentId[0] != "null") {
                assignmentId = orig.parameters?.assignmentId[0]
                role = Roles.ROLE_MTURKER
            } else {
                role = Roles.ROLE_USER
            }
        } else if (orig?.parameters?.PROLIFIC_PID) {
            workerId = orig.parameters.PROLIFIC_PID[0]
            role = Roles.ROLE_PROLIFIC
        }
        String postUrl = "${request.contextPath}/login/workerAuth"
        //couldn't access a parameter that works for a login
        if (!workerId) {
            println("Authenticating for worker with no parameters...")
            return render(view: "worker_auth", model: [postUrl: postUrl,  origURI: original])
        } else {
            User u = User.findByWorkerId(workerId) ?: User.findByUsername(workerId)
            //if we don't have a user, but we're trying to train, create the user
            if (!u) {
                if (original && original.contains("training")) {
                    println("Authenticating for new worker ${workerId} for training")
                    u = userService.createUserByWorkerId(workerId, role)
                } else {
                    //we can't find a user, but there was an id in the parameters.  We should probably just flash a message and direct
                    //back to worker login page?
                    flash.message = "Authentication failed. Please check your id."
                    return render(view: "worker_auth", model: [postUrl: postUrl,  origURI: original])
                }
            }
            //now attempt to login
            if (u?.id) {
                springSecurityService.reauthenticate(u.username)
            } else {
                //some unknown problem creating the user.  Bail.
                flash.message = "Authentication failed. Please check your id."
                return render(view: "worker_auth", model: [postUrl: postUrl,  origURI: original])
            }


            if (springSecurityService.isLoggedIn()) {
                original = original ?: "/session/available"

                if (workerId) {
                    log.debug("Redirecting with parameters")
                    def params = "workerId=$workerId"
                    if (assignmentId) {
                        params += "&assignmentId=$assignmentId"
                    }
                    if (orig?.parameters?.hitId) {
                        params += "&hitId=${orig.parameters.hitId[0]}"
                    }
                    println("Sending worker ${workerId} to ${original}?${params}")
                    return redirect(url: "$original?$params")
                } else {
                    println("Sending worker ${workerId} to ${original} (no params)")
                    return redirect(url: "$original")
                }
            }
        }
        //Should never get here
        log.warn("Authentication path found unmet condition; please check the auth method in the login controller")
        flash.message = "Authentication failed. Please check your id."
        return redirect(url: '/')

    }

    def workerAuth() {
        String workerId = params.workerId
        String originalUri = params.origURI ?: "/session/available"
        def u = User.findByWorkerId(workerId)?:User.findByUsername(workerId)
        String postUrl = "${request.contextPath}/login/workerAuth"
        if (u) {
            if (u.isAdmin()) {
                return redirect(url: "/admin")
            }

            springSecurityService.reauthenticate(u.username)
            if (springSecurityService.isLoggedIn()) {
                def params = "workerId=$u.workerId"
                return redirect(url: "$originalUri?$params")
            }
        }
        flash.message = "Authentication failed. Please check your id."
        render(view: "worker_auth", model: [postUrl: postUrl, origURI: originalUri])


    }

    //String view = "auth"

    /**
     * The redirect action for Ajax requests.
     */
    def authAjax() {
        response.setHeader 'Location', SpringSecurityUtils.securityConfig.auth.ajaxLoginFormUrl
        response.sendError HttpServletResponse.SC_UNAUTHORIZED
    }

    /**
     * Show denied page.
     */
    def denied() {
        if (springSecurityService.isLoggedIn() &&
                authenticationTrustResolver.isRememberMe(SCH.context?.authentication)) {
            // have cookie but the page is guarded with IS_AUTHENTICATED_FULLY
            redirect action: 'full', params: params
        }
    }

    /**
     * Login page for users with a remember-me cookie but accessing a IS_AUTHENTICATED_FULLY page.
     */
    def full() {
        def config = SpringSecurityUtils.securityConfig
        render view: 'auth', params: params,
                model: [hasCookie: authenticationTrustResolver.isRememberMe(SCH.context?.authentication),
                        postUrl  : "${request.contextPath}${config.apf.filterProcessesUrl}"]
    }

    /**
     * Callback after a failed login. Redirects to the auth page with a warning message.
     */
    def authfail() {

        String msg = ''
        def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
        if (exception) {
            if (exception instanceof AccountExpiredException) {
                msg = g.message(code: "springSecurity.errors.login.expired")
            } else if (exception instanceof CredentialsExpiredException) {
                msg = g.message(code: "springSecurity.errors.login.passwordExpired")
            } else if (exception instanceof DisabledException) {
                msg = g.message(code: "springSecurity.errors.login.disabled")
            } else if (exception instanceof LockedException) {
                msg = g.message(code: "springSecurity.errors.login.locked")
            } else {
                msg = g.message(code: "springSecurity.errors.login.fail")
            }
        }

        if (springSecurityService.isAjax(request)) {
            render([error: msg] as JSON)
        } else {
            flash.message = msg
            redirect action: 'auth', params: params
        }
    }

    /**
     * The Ajax success redirect url.
     */
    def ajaxSuccess() {
        render([success: true, username: springSecurityService.authentication.name] as JSON)
    }

    /**
     * The Ajax denied redirect url.
     */
    def ajaxDenied() {
        render([error: 'access denied'] as JSON)
    }
}
