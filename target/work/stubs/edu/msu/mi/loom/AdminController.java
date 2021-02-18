package edu.msu.mi.loom;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@groovy.util.logging.Slf4j() @grails.plugin.springsecurity.annotation.Secured(value="ROLE_ADMIN") public class AdminController
  extends java.lang.Object  implements
    groovy.lang.GroovyObject {
;
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
public  java.lang.Object getFileService() { return null;}
public  void setFileService(java.lang.Object value) { }
public  java.lang.Object getJsonParserService() { return null;}
public  void setJsonParserService(java.lang.Object value) { }
public  java.lang.Object getExperimentService() { return null;}
public  void setExperimentService(java.lang.Object value) { }
public  java.lang.Object getGraphParserService() { return null;}
public  void setGraphParserService(java.lang.Object value) { }
public  java.lang.Object getEmailService() { return null;}
public  void setEmailService(java.lang.Object value) { }
public  java.lang.Object getUserService() { return null;}
public  void setUserService(java.lang.Object value) { }
public  java.lang.Object getSpringSecurityService() { return null;}
public  void setSpringSecurityService(java.lang.Object value) { }
public  java.lang.Object getExportService() { return null;}
public  void setExportService(java.lang.Object value) { }
public  java.lang.Object getTrainingSetService() { return null;}
public  void setTrainingSetService(java.lang.Object value) { }
public  java.lang.Object getSessionService() { return null;}
public  void setSessionService(java.lang.Object value) { }
public  java.lang.Object getAdminService() { return null;}
public  void setAdminService(java.lang.Object value) { }
public  java.lang.Object getNetworkGenerateService() { return null;}
public  void setNetworkGenerateService(java.lang.Object value) { }
public  java.lang.Object getMturkService() { return null;}
public  void setMturkService(java.lang.Object value) { }
public static  java.lang.Object getAllowedMethods() { return null;}
public static  void setAllowedMethods(java.lang.Object value) { }
public  java.lang.Object index() { return null;}
public  java.lang.Object board() { return null;}
public  java.lang.Object refresh() { return null;}
public  java.lang.Object launchExperiment() { return null;}
public  java.lang.Object launchTraining() { return null;}
public  java.lang.Object uploadExperiment() { return null;}
public  java.lang.Object uploadTrainingSet() { return null;}
public  java.lang.Object uploadStorySet() { return null;}
public  java.lang.Object startSession() { return null;}
public  java.lang.Object cancelSession() { return null;}
public  java.lang.Object validateSession() { return null;}
public  java.lang.Object paySession() { return null;}
public  java.lang.Object deleteExperiment() { return null;}
public  java.lang.Object view() { return null;}
public  java.lang.Object exportCSV() { return null;}
public  java.lang.Object cloneSession() { return null;}
@grails.plugin.springsecurity.annotation.Secured(value="permitAll") public  java.lang.Object deleteUser() { return null;}
}
