package edu.msu.mi.loom;

import com.amazonaws.services.mturk.model.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@grails.transaction.Transactional() public class MturkService
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.String getSANDBOX_ENDPOINT() { return (java.lang.String)null;}
public  void setSANDBOX_ENDPOINT(java.lang.String value) { }
public  java.lang.String getPROD_ENDPOINT() { return (java.lang.String)null;}
public  void setPROD_ENDPOINT(java.lang.String value) { }
public  java.lang.String getSIGNING_REGION() { return (java.lang.String)null;}
public  void setSIGNING_REGION(java.lang.String value) { }
public  java.lang.String getAWS_ACCESS_KEY() { return (java.lang.String)null;}
public  void setAWS_ACCESS_KEY(java.lang.String value) { }
public  java.lang.String getAWS_SECRET_KEY() { return (java.lang.String)null;}
public  void setAWS_SECRET_KEY(java.lang.String value) { }
public  com.amazonaws.services.mturk.AmazonMTurk getClient() { return (com.amazonaws.services.mturk.AmazonMTurk)null;}
public  java.lang.Object getProductionClient() { return null;}
public  java.lang.Object getSandboxClient() { return null;}
public  java.lang.Object createQualification(edu.msu.mi.loom.TrainingSet ts, java.lang.String description) { return null;}
public  java.lang.Object createQualification(edu.msu.mi.loom.Story story, java.lang.String description) { return null;}
public  java.lang.Object createQualification(java.lang.String qualificationName, java.lang.String description) { return null;}
public  java.lang.Object assignQualification(java.lang.String workerId) { return null;}
public  java.lang.Object searchQualificationTypeId(java.lang.String qualificationType) { return null;}
public  java.lang.Object createExperimentHIT(edu.msu.mi.loom.Experiment exp, java.lang.String sessionId)throws java.io.IOException { return null;}
public  java.lang.Object createTrainingHIT(edu.msu.mi.loom.TrainingSet trainingSet)throws java.io.IOException { return null;}
public  java.lang.Object deleteHit(java.lang.String HITId) { return null;}
public  java.lang.Object sendExperimentBonus(java.lang.String assignmentId, java.lang.Object max_score, java.lang.Object total_score, java.lang.Object wait_time, java.lang.Object session_id, java.lang.Object worker_id) { return null;}
public  java.lang.Object getWorkerId(java.lang.String assignmentId) { return null;}
public  java.lang.Object listAssighmentsForHIT(java.lang.String HITId) { return null;}
public  java.lang.Object getHit(java.lang.String HITId) { return null;}
public  java.lang.Object completedAssignment(edu.msu.mi.loom.Session session) { return null;}
public  java.lang.Object updateExpirationForHit(edu.msu.mi.loom.Session session) { return null;}
public  java.lang.Object check_payable(edu.msu.mi.loom.Session session) { return null;}
public  java.lang.Object listBonus(java.lang.String HITId) { return null;}
public  java.lang.Object pay_HIT(edu.msu.mi.loom.Session session) { return null;}
}
