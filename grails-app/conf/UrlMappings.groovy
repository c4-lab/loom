class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        "500"(view: '/error')
        "/not-found"(view: '/not-found')

        "/"(controller: 'login', action: 'auth')
        "/admin"(controller: 'login', action: 'auth')
        "/session/$session" {
            controller = "admin"
            action = "view"
        }
        "/session/clone/" {
            controller = "admin"
            action = "cloneSession"
        }

        "/session/complete" {
            controller = "admin"
            action = "completeExperimentCreation"
        }

        "/room/join/$id" {
            controller = "home"
            action = "joinRoom"
        }

        "/room/$room" {
            controller = "home"
            action = "room"
        }

        "/training/$session" {
            controller = "home"
            action = "training"
        }

        "/simulation/$session/$roundNumber" {
            controller = "experiment"
            action = "simulation"
        }
    }
}
