package edu.msu.mi.loom;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.util.logging.Slf4j() @groovy.transform.ToString(includeNames=true) public class Session
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.Object getRandomStringGenerator() { return null;}
public  void setRandomStringGenerator(java.lang.Object value) { }
public  java.lang.String getName() { return (java.lang.String)null;}
public  void setName(java.lang.String value) { }
public  java.lang.String getType() { return (java.lang.String)null;}
public  void setType(java.lang.String value) { }
public  java.util.Date getDateCreated() { return (java.util.Date)null;}
public  void setDateCreated(java.util.Date value) { }
public  edu.msu.mi.loom.TrainingSet getTrainingSet() { return (edu.msu.mi.loom.TrainingSet)null;}
public  void setTrainingSet(edu.msu.mi.loom.TrainingSet value) { }
public  edu.msu.mi.loom.Experiment getExp() { return (edu.msu.mi.loom.Experiment)null;}
public  void setExp(edu.msu.mi.loom.Experiment value) { }
public  edu.msu.mi.loom.Session.State getState() { return (edu.msu.mi.loom.Session.State)null;}
public  void setState(edu.msu.mi.loom.Session.State value) { }
public  java.lang.String getFullCode() { return (java.lang.String)null;}
public  void setFullCode(java.lang.String value) { }
public  java.lang.String getDoneCode() { return (java.lang.String)null;}
public  void setDoneCode(java.lang.String value) { }
public  java.lang.String getWaitingCode() { return (java.lang.String)null;}
public  void setWaitingCode(java.lang.String value) { }
public  java.lang.Long getStartPending() { return (java.lang.Long)null;}
public  void setStartPending(java.lang.Long value) { }
public  java.lang.Long getStartActive() { return (java.lang.Long)null;}
public  void setStartActive(java.lang.Long value) { }
public  java.util.List<java.lang.String> getHITId() { return (java.util.List<java.lang.String>)null;}
public  void setHITId(java.util.List<java.lang.String> value) { }
public static  java.lang.Object getHasMany() { return null;}
public static  void setHasMany(java.lang.Object value) { }
public static  java.lang.Object getConstraints() { return null;}
public static  void setConstraints(java.lang.Object value) { }
public  java.lang.Object getBeforeInsert() { return null;}
public  void setBeforeInsert(java.lang.Object value) { }
public  edu.msu.mi.loom.Session clone() { return (edu.msu.mi.loom.Session)null;}
public static enum State
  implements
    groovy.lang.GroovyObject {
PENDING, ACTIVE, FINISHED, CANCEL;
public static final edu.msu.mi.loom.Session.State MIN_VALUE = null;
public static final edu.msu.mi.loom.Session.State MAX_VALUE = null;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  edu.msu.mi.loom.Session.State next() { return (edu.msu.mi.loom.Session.State)null;}
public  edu.msu.mi.loom.Session.State previous() { return (edu.msu.mi.loom.Session.State)null;}
public static final  edu.msu.mi.loom.Session.State $INIT(java.lang.Object... para) { return (edu.msu.mi.loom.Session.State)null;}
}
}
