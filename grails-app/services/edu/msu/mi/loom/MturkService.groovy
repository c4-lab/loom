package edu.msu.mi.loom

import com.amazonaws.mturk.requester.SearchQualificationTypesResult
import com.amazonaws.mturk.requester.SortDirection
import com.amazonaws.mturk.service.axis.RequesterService
import edu.msu.mi.mturk_utils.FilePropertiesConfig
import grails.transaction.Transactional

@Transactional
class MturkService {



    def createQualification(TrainingSet ts) {
        createQualification(TrainingSet.constructQualificationString(ts))
    }


    def createQualification(String qualificationName) {
        RequesterService svc = getRequesterService()
        runAsync {
            try {

                SearchQualificationTypesResult result = svc.searchQualificationTypes(qualificationName,false,true,null,null,null,null)
                if (result.numResults > 0) {
                    println("Qualification already exists")
                } else {
                    svc.createQualificationType(qualificationName,"loom,training,game","Indicates that the worker has completed training for the Loom game")
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    def assignQualification(String workerId, String qualificationName) {
        RequesterService svc = getRequesterService()
        runAsync {
            try {

                SearchQualificationTypesResult result = svc.searchQualificationTypes(qualificationName,false,true,null,null,null,null)
                if (result.numResults != 1) {
                    println("Could not identify a unique qualification type")
                } else {
                    svc.assignQualification(result.getQualificationType(0).qualificationTypeId, workerId, 0, false)
                }
            } catch (Exception e) {
               e.printStackTrace()
            }
        }
    }

    private RequesterService getRequesterService() {
        FilePropertiesConfig config
        try {
            InputStream stream = this.class.classLoader.getResourceAsStream("global.mturk.properties")
            if (!stream) {
                println("Uh oh, can't find resource!")
            }

            config = new FilePropertiesConfig(stream);
            new RequesterService(config);
        } catch (IOException e) {
            log.error("Could not read global properties file: global.mturk.properties");
            //config = new ClientConfig();
            return null
        }
    }
}
