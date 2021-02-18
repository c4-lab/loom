package edu.msu.mi.loom;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@grails.transaction.Transactional() public class AdminService
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public  java.lang.Object createSession(edu.msu.mi.loom.Experiment experiment, edu.msu.mi.loom.TrainingSet trainingSet) { return null;}
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.Object getRandomStringGenerator() { return null;}
public  void setRandomStringGenerator(java.lang.Object value) { }
public  java.lang.Object getNetworkGenerateService() { return null;}
public  void setNetworkGenerateService(java.lang.Object value) { }
public  java.lang.Object getMturkService() { return null;}
public  void setMturkService(java.lang.Object value) { }
public  java.lang.Object createSession(edu.msu.mi.loom.Experiment experiment, edu.msu.mi.loom.TrainingSet trainingSet, java.lang.String type) { return null;}
public  java.lang.Object createExperiment(java.lang.String name, edu.msu.mi.loom.Story story, int min_node, int max_nodes, int min_degree, int max_degree, int initialNbrOfTiles, edu.msu.mi.loom.Experiment.Network_type network_type, int rounds, int duration, java.lang.Object qualifier, edu.msu.mi.loom.TrainingSet training_set, int m, java.lang.Object probability, java.lang.Object accepting, java.lang.Object completion, java.lang.Object waiting, java.lang.Object score) { return null;}
public  java.lang.Object createStory(java.lang.Object title, java.lang.Object tails) { return null;}
public  java.lang.Object setExperimentNetwork(java.util.HashMap<java.lang.String, java.util.List<java.lang.String>> map, int experimentId) { return null;}
public  java.lang.Object cloneExperiment(edu.msu.mi.loom.Session session) { return null;}
public  java.lang.Object deleteExperiment(java.lang.Object id, java.lang.Object type) { return null;}
}
