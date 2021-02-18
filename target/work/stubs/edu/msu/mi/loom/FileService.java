package edu.msu.mi.loom;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

@groovy.util.logging.Slf4j() @grails.transaction.Transactional() public class FileService
  extends java.lang.Object  implements
    edu.msu.mi.loom.file.IFileService,    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.Object getUniqueHashService() { return null;}
public  void setUniqueHashService(java.lang.Object value) { }
@java.lang.Override() public  java.lang.String uploadFile(org.springframework.web.multipart.MultipartFile file, java.lang.String filename) { return (java.lang.String)null;}
@java.lang.Override() public  void deleteFile(java.lang.String location) { }
@java.lang.Override() public  java.lang.String readFile(org.springframework.web.multipart.MultipartFile file) { return (java.lang.String)null;}
@java.lang.Override() public  java.lang.String buildFileLocation(java.lang.String filename) { return (java.lang.String)null;}
@java.lang.Override() public  java.lang.String buildFileLocation(java.lang.String uniqueHash, java.lang.String filename) { return (java.lang.String)null;}
}
