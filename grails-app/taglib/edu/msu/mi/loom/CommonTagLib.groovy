package edu.msu.mi.loom

class CommonTagLib {
    def springSecurityService
    static defaultEncodeAs = [taglib: 'raw']
//    static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    static namespace = "loom"
    static returnObjectForTags = ['checkForCurrentUser', 'checkCurrentUserAndRoomConnection']

    def progressBar = { attrs ->
        def completePercent = attrs.userCount * 100 / attrs.userMaxCount

        out << '<div class="progress progress-sm active">'
        out << "<div class='progress-bar progress-bar-striped progress-bar-light-blue' role='progressbar' aria-valuenow='${completePercent}' " +
                "aria-valuemin='0' aria-valuemax='100' style='width: ${completePercent}%'>"
        out << "<span class='sr-only'>${completePercent}% Complete</span>"
        out << '</div></div>'
        out << "<p>Number of users connected: <span class='prog-bar-count'>${attrs.userCount}</span></p>"
    }



    def checkForCurrentUser = { attrs ->
        def currentUser = springSecurityService.currentUser as User
        if (!("neighbour" + attrs.userKey).trim().equals(currentUser.alias)) {
            return true
        }

        return false
    }

    def currentUserTab = { attrs ->
        def currentUser = springSecurityService.currentUser as User
        out << "<li class='${!("neighbour" + attrs.userKey).trim().equals(currentUser.alias) ?: 'active'}'>"
        out << "<a href='#neighbour${attrs.userKey}' data-toggle='tab'>${!("neighbour" + attrs.userKey).trim().equals(currentUser.alias) ? 'neighbour ' + (attrs.userKey) : "You"}</a>"
        out << "</li>"
    }

    def currentUserTabContent = { attrs ->
        def currentUser = springSecurityService.currentUser as User

        out << "<div class='tab-pane ${!("neighbour" + attrs.userKey).trim().equals(currentUser.alias) ?: 'active'}' id='neighbour${attrs.userKey}'>"
        out << '<ul class="dvSource">'
        attrs.userValue.tts.each { tt ->
            out << "<li class='ui-state-default' id='${tt.id}'>${tt.text}</li>"
        }
        out << '</ul></div>'
    }
}
