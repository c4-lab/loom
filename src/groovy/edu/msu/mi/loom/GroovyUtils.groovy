package edu.msu.mi.loom

import java.util.regex.Matcher

class GroovyUtils {

    static findAll(Matcher m) {
        def result = []
        while (m.find()) {
            result.add(m.group())
        }

    }

}
