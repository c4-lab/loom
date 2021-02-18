package edu.msu.mi.loom;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.util.logging.Slf4j() @groovy.transform.ToString(includeNames=true) public class Experiment
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.String getName() { return (java.lang.String)null;}
public  void setName(java.lang.String value) { }
public  int getMin_node() { return (int)0;}
public  void setMin_node(int value) { }
public  int getMax_node() { return (int)0;}
public  void setMax_node(int value) { }
public  int getInitialNbrOfTiles() { return (int)0;}
public  void setInitialNbrOfTiles(int value) { }
public  edu.msu.mi.loom.Experiment.Network_type getNetwork_type() { return (edu.msu.mi.loom.Experiment.Network_type)null;}
public  void setNetwork_type(edu.msu.mi.loom.Experiment.Network_type value) { }
public  int getRoundCount() { return (int)0;}
public  void setRoundCount(int value) { }
public  int getRoundTime() { return (int)0;}
public  void setRoundTime(int value) { }
public  int getM() { return (int)0;}
public  void setM(int value) { }
public  java.lang.Float getProbability() { return (java.lang.Float)null;}
public  void setProbability(java.lang.Float value) { }
public  int getMin_degree() { return (int)0;}
public  void setMin_degree(int value) { }
public  int getMax_degree() { return (int)0;}
public  void setMax_degree(int value) { }
public  java.lang.String getQualifier() { return (java.lang.String)null;}
public  void setQualifier(java.lang.String value) { }
public  java.util.Date getDateCreated() { return (java.util.Date)null;}
public  void setDateCreated(java.util.Date value) { }
public  edu.msu.mi.loom.Story getStory() { return (edu.msu.mi.loom.Story)null;}
public  void setStory(edu.msu.mi.loom.Story value) { }
public  edu.msu.mi.loom.TrainingSet getTraining_set() { return (edu.msu.mi.loom.TrainingSet)null;}
public  void setTraining_set(edu.msu.mi.loom.TrainingSet value) { }
public  java.lang.Float getAccepting() { return (java.lang.Float)null;}
public  void setAccepting(java.lang.Float value) { }
public  java.lang.Float getCompletion() { return (java.lang.Float)null;}
public  void setCompletion(java.lang.Float value) { }
public  java.lang.Float getWaiting() { return (java.lang.Float)null;}
public  void setWaiting(java.lang.Float value) { }
public  java.lang.Float getScore() { return (java.lang.Float)null;}
public  void setScore(java.lang.Float value) { }
public static  java.lang.Object getHasMany() { return null;}
public static  void setHasMany(java.lang.Object value) { }
public static  java.lang.Object getConstraints() { return null;}
public static  void setConstraints(java.lang.Object value) { }
public static  java.lang.Object getMapping() { return null;}
public static  void setMapping(java.lang.Object value) { }
public static enum Network_type
  implements
    groovy.lang.GroovyObject {
Lattice, Newman_Watts, Barabassi_Albert;
public static final edu.msu.mi.loom.Experiment.Network_type MIN_VALUE = null;
public static final edu.msu.mi.loom.Experiment.Network_type MAX_VALUE = null;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  edu.msu.mi.loom.Experiment.Network_type next() { return (edu.msu.mi.loom.Experiment.Network_type)null;}
public  edu.msu.mi.loom.Experiment.Network_type previous() { return (edu.msu.mi.loom.Experiment.Network_type)null;}
public static final  edu.msu.mi.loom.Experiment.Network_type $INIT(java.lang.Object... para) { return (edu.msu.mi.loom.Experiment.Network_type)null;}
}
}
