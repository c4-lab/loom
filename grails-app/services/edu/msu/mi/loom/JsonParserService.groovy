package edu.msu.mi.loom

import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.web.json.JSONObject

@Transactional(readOnly = true)
class JsonParserService {
    def parseToJSON(String text) {
        JSONObject json = JSON.parse(text)
        return json
    }
}
