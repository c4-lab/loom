class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "home")
        "500"(view:'/error')

        "/admin"(controller: 'login', action: 'adminAuth')
	}
}
