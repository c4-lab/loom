package edu.msu.mi.loom

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
    }
}
