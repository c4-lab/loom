package edu.msu.mi.loom;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.util.logging.Slf4j() @grails.transaction.Transactional() public class ExperimentService
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.Object getSessionService() { return null;}
public  void setSessionService(java.lang.Object value) { }
public  java.lang.Object getSpringSecurityService() { return null;}
public  void setSpringSecurityService(java.lang.Object value) { }
public  java.util.Map<java.lang.Object, edu.msu.mi.loom.ExperimentRoundStatus> getExperimentsRunning() { return (java.util.Map<java.lang.Object, edu.msu.mi.loom.ExperimentRoundStatus>)null;}
public  void setExperimentsRunning(java.util.Map<java.lang.Object, edu.msu.mi.loom.ExperimentRoundStatus> value) { }
public  java.lang.Object getWaitingTimer() { return null;}
public  void setWaitingTimer(java.lang.Object value) { }
public  java.lang.Object getMyState(edu.msu.mi.loom.Session expSession) { return null;}
public  java.lang.Object getNeighborModel(edu.msu.mi.loom.Session s) { return null;}
public  java.lang.Object getNeighborsState(edu.msu.mi.loom.Session expSession) { return null;}
public  java.lang.Object kickoffSession(edu.msu.mi.loom.Session session) { return null;}
public  java.lang.Object advanceRound(edu.msu.mi.loom.Session session) { return null;}
public  java.lang.Object userSubmitted(edu.msu.mi.loom.User user, edu.msu.mi.loom.Session session, int round) { return null;}
public  java.lang.Object getExperimentStatus(edu.msu.mi.loom.Session session) { return null;}
public static  java.lang.Float score(java.util.List truth, java.util.List sample) { return (java.lang.Float)null;}
}
