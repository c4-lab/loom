package edu.msu.mi.loom
import grails.util.Environment

import org.springframework.security.core.context.SecurityContextHolder

class SecurityFilters {
    def sessionService

    def filters = {
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
            before = {
                if (!request.isSecure() && !Environment.isDevelopmentMode()) {
                    def url = "https://" + request.serverName + request.forwardURI
                    redirect(url: url, permanent: true)
                    return false
                }
            }

            after = {Map model ->
                if ([301,302].contains(response.getStatus())) {
                    def location = response.getHeader("Location")
                    if (location.startsWith("http://")) {
                        if (request.isSecure()) {
                            location = "https://${location.substring(7,location.length())}"
                            log.info("Security filter rewrite to ${response.getHeader("Location")}")
                            response.setHeader("Location",location)
                        }
                    }
                }
                return true
            }
        }
    }
}
