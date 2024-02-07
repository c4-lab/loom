package edu.msu.mi.loom
import grails.util.Environment

import org.springframework.security.core.context.SecurityContextHolder

class SecurityFilters {
    def sessionService
    def springSecurityService

    def filters = {

        all() {
            before = { Map model ->
                User u = springSecurityService.currentUser
                String id = request.getParameter("workerId")?:request.getParameter("PROLIFIC_PID")

                if (u && id) {
                    if (id != u.workerId) {

                        request.logout()


                        String baseUrl = request.scheme + "://" + request.serverName
                        if ((request.scheme == "http" && request.serverPort != 80) || (request.scheme == "https" && request.serverPort != 443)) {
                            baseUrl += ":" + request.serverPort
                        }

                        baseUrl += request.forwardURI


                        String queryString = request.getQueryString(); // This could be null

                        if (queryString != null) {
                            baseUrl+="?"+queryString
                        }

                        response.sendRedirect(baseUrl)
                        return false // Stop further filter processing

                    }
                }
            }
        }

        all(controller: 'logout', action: 'index') {
            before = { Map model ->
                sessionService.leaveAllSessions()


            }

            after = {Map model ->
                println "Clearing context"
                SecurityContextHolder.clearContext()

            }
        }

        switchToHttps(uri: "/**") {
            //before = {
            //    if (!request.isSecure() && !Environment.isDevelopmentMode()) {
            //        def url = "https://" + request.serverName + request.forwardURI
            //        redirect(url: url, permanent: true)
            //        return false
            //    }
            //}

            after = {Map model ->
                if ([301,302].contains(response.getStatus())) {
                    def location = response.getHeader("Location")

                    if (location.startsWith("http://") && !location.contains("localhost")) {
                        
                        location = "https://${location.substring(7,location.length())}"
                        response.setHeader("Location",location)
                        
                    }
                }
                return true
            }
        }
    }
}
