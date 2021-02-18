package edu.msu.mi.loom;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@grails.transaction.Transactional() public class NetworkGenerateService
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.Object generateGraph(edu.msu.mi.loom.Experiment experiment, int userCount) { return null;}
public  java.lang.Object generateRingGraph(java.lang.String story, int min_degree, int max_degree) { return null;}
public  java.lang.Object generateNewWattsGraph(java.lang.Object completeGraph, java.lang.String story, int min_degree, int max_degree, float prob) { return null;}
public  java.lang.Object generateBarabasiAlbertGraph(java.lang.String story, int m0, int m, int n, int max_degree) { return null;}
public class RandomCollection
<E>  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public RandomCollection
() {
super ();
}
public RandomCollection
(java.util.Random random) {}
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  edu.msu.mi.loom.NetworkGenerateService.RandomCollection<E> add(double weight, E result) { return (edu.msu.mi.loom.NetworkGenerateService.RandomCollection<E>)null;}
public  E next() { return null;}
}
}
