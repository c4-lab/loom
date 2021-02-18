package edu.msu.mi.loom;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.util.logging.Slf4j() @grails.transaction.Transactional() public class SimulationService
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.Object getSpringSecurityService() { return null;}
public  void setSpringSecurityService(java.lang.Object value) { }
public  java.lang.Object getStatService() { return null;}
public  void setStatService(java.lang.Object value) { }
public  java.lang.Object getExperimentService() { return null;}
public  void setExperimentService(java.lang.Object value) { }
public  java.lang.Object getMturkService() { return null;}
public  void setMturkService(java.lang.Object value) { }
public  java.lang.Object simulation(edu.msu.mi.loom.TrainingSet ts, java.lang.Object roundNumber, java.lang.Object tempStory) { return null;}
public static  java.lang.Float simulationScore(java.util.List<java.lang.Integer> truth, java.util.List<java.lang.Integer> sample) { return (java.lang.Float)null;}
public  java.lang.Object createSimulation(java.lang.Object json, edu.msu.mi.loom.TrainingSet trainingSet) { return null;}
public  java.lang.Object addRoundScore(java.util.List<java.lang.Integer> integers, edu.msu.mi.loom.Simulation simulation) { return null;}
}
