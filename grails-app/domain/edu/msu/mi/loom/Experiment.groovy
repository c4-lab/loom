package edu.msu.mi.loom

import groovy.transform.ToString
import groovy.util.logging.Slf4j
@Slf4j
@ToString(includeNames = true)

/**
 * Experiments cover the selection of story, network, and interface.  Details about the specific subject population (including criteria for inclusion) are part of the session
 */
class Experiment {


    String name
    Date created
    SessionParameters defaultSessionParams

    static hasMany = [sessions:Session]


    static constraints = {
        name blank: false, unique: true
    }



}
