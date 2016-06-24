class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        "500"(view: '/error')
        "404"(view: '/not-found')
        "403"(view: '/not-found')
        "401"(view: '/not-found')
        "/not-found"(view: '/not-found')
        "/is-done" (view: '/is-done')
        "/is-full" (view: '/is-full')

        "/"(view: '/not-found')
        "/admin"(controller: 'login', action: 'auth')

        "/admin/session/$session" {
            controller = "admin"
            action = "view"
        }


        "/admin/session/complete" {
            controller = "admin"
            action = "completeExperimentCreation"
        }



        "/training/t/$trainingId/$seqNumber?" {
            controller = "training"
            action = "training"
        }

        "/training/score/$simulationId" {
            controller = "training"
            action = "score"
        }



        "/session/s/$session" {
            controller = "session"
            action = "experiment"
        }

        "/session/checkExperimentRoundState/$sessionId" {
            controller = "session"
            action = "checkExperimentRoundState"
        }

        "/session/finishExperiment/$session" {
            controller = "session"
            action = "finishExperiment"
        }

    }
}
