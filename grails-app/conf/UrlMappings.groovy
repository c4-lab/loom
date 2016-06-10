class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        "500"(view: '/error')
        "/not-found"(view: '/not-found')

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

        "/session/finishExperiment/$session" {
            controller = "session"
            action = "finishExperiment"
        }

    }
}
