package edu.msu.mi.loom

class CommonTagLib {
    static defaultEncodeAs = [taglib: 'raw']
//    static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    static namespace = "loom"

    def progressBar = { attrs ->
        def completePercent = attrs.userCount * 100 / attrs.userMaxCount

        out << '<div class="progress progress-sm active">'
        out << "<div class='progress-bar progress-bar-success progress-bar-striped' role='progressbar' aria-valuenow='${completePercent}' " +
                "aria-valuemin='0' aria-valuemax='100' style='width: ${completePercent}%'>"
        out << "<span class='sr-only'>${completePercent}% Complete</span>"
        out << '</div></div>'
        out << "<p>Number of users connected: ${attrs.userCount}</p>"
    }
}
