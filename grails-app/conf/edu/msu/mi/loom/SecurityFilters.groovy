package edu.msu.mi.loom

class SecurityFilters {
    def roomService

    def filters = {
        all(controller: 'logout', action: 'index') {
            after = { Map model ->
                roomService.leaveAllRooms()
            }
        }
    }
}
