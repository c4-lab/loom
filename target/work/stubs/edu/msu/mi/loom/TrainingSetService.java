package edu.msu.mi.loom;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.util.logging.Slf4j() @grails.transaction.Transactional() public class TrainingSetService
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.Object getSimulationService() { return null;}
public  void setSimulationService(java.lang.Object value) { }
public  java.lang.Object getMturkService() { return null;}
public  void setMturkService(java.lang.Object value) { }
public  java.lang.Object createTrainingSet(java.lang.Object json, java.lang.Object name, java.lang.Object qualifier, java.lang.Object HIT_num, java.lang.Object training_payment) { return null;}
public  java.lang.Object createTraining(java.lang.Object json, edu.msu.mi.loom.TrainingSet trainingSet) { return null;}
public  edu.msu.mi.loom.Training getNextTraining(edu.msu.mi.loom.User u, edu.msu.mi.loom.TrainingSet ts) { return (edu.msu.mi.loom.Training)null;}
public  java.lang.Object changeTrainingState(edu.msu.mi.loom.User u, edu.msu.mi.loom.Training training, edu.msu.mi.loom.Simulation simulation) { return null;}
public  java.lang.Object completeTraining(edu.msu.mi.loom.UserTrainingSet uts) { return null;}
}
