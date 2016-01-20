package edu.msu.mi.loom

class SecurityFilters {
    def userService

    def filters = {
//        all(controller: 'logout', action: 'index') {
//            after = { Map model ->
//                if (session.currentUser) {
//                    userService.deleteUser(session.currentUser)
//                    session.currentUser = null
//                }
//            }
//        }
    }
}
