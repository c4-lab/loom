package edu.msu.mi.loom

import grails.plugin.springsecurity.annotation.Secured

@Secured('permitAll')
class AdminController {

    def index() {}

    def board() {
//        render "This is admin board!!!"
    }
}
