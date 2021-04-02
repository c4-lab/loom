package edu.msu.mi.loom.file

import edu.msu.mi.loom.UniqueHash
import edu.msu.mi.loom.UniqueHashService
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(UniqueHashService)
@Build(UniqueHash)
class UniqueHashServiceSpec extends Specification {
    void "test getUniqueHash method"() {
        given:
        String hash = "as223as23sdq45"
        service.randomStringGenerator = [generateLowercase: { def length -> return hash }]

        expect:
        service.getUniqueHash() == hash
    }
}
