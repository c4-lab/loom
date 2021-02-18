package edu.msu.mi.loom;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.util.logging.Log4j() public class ExperimentRoundStatus
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public ExperimentRoundStatus
(int userCount, int roundCount) {
super ();
}
public ExperimentRoundStatus
(int userCount, int roundCount, long pauseLength) {}
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public final  int getUserCount() { return (int)0;}
public final  int getRoundCount() { return (int)0;}
public final  int getPauseLength() { return (int)0;}
public  int getRound() { return (int)0;}
public  void setRound(int value) { }
public final  java.util.Set getSubmitted() { return (java.util.Set)null;}
public  java.util.Date getRoundStart() { return (java.util.Date)null;}
public  void setRoundStart(java.util.Date value) { }
public  long getPauseStart() { return (long)0;}
public  void setPauseStart(long value) { }
public  edu.msu.mi.loom.ExperimentRoundStatus.Status getCurrentStatus() { return (edu.msu.mi.loom.ExperimentRoundStatus.Status)null;}
public  void setCurrentStatus(edu.msu.mi.loom.ExperimentRoundStatus.Status value) { }
public  edu.msu.mi.loom.ExperimentRoundStatus.Status checkPauseStatus() { return (edu.msu.mi.loom.ExperimentRoundStatus.Status)null;}
public  java.lang.Object submitUser(java.lang.Object userId) { return null;}
public  boolean isFinished() { return false;}
public  void pause() { }
public  java.lang.String toString() { return (java.lang.String)null;}
public static enum Status
  implements
    groovy.lang.GroovyObject {
ACTIVE, PAUSING, FINISHED;
public static final edu.msu.mi.loom.ExperimentRoundStatus.Status MIN_VALUE = null;
public static final edu.msu.mi.loom.ExperimentRoundStatus.Status MAX_VALUE = null;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  edu.msu.mi.loom.ExperimentRoundStatus.Status next() { return (edu.msu.mi.loom.ExperimentRoundStatus.Status)null;}
public  edu.msu.mi.loom.ExperimentRoundStatus.Status previous() { return (edu.msu.mi.loom.ExperimentRoundStatus.Status)null;}
public static final  edu.msu.mi.loom.ExperimentRoundStatus.Status $INIT(java.lang.Object... para) { return (edu.msu.mi.loom.ExperimentRoundStatus.Status)null;}
}
}
