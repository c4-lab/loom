package edu.msu.mi.mturk_utils;

import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import groovy.lang.*;
import groovy.util.*;

public class FilePropertiesConfig
  extends com.amazonaws.mturk.util.ClientConfig  implements
    groovy.lang.GroovyObject {
;
public static final java.lang.String ACCESS_KEY_ID = "access_key";
public static final java.lang.String SECRET_ACCESS_KEY = "secret_key";
public static final java.lang.String SERVICE_URL = "service_url";
public static final java.lang.String LOG_LEVEL = "log_level";
public static final java.lang.String RETRY_ATTEMPTS = "retry_attempts";
public static final java.lang.String RETRY_DELAY_MILLIS = "retry_delay_millis";
public static final java.lang.String RETRIABLE_ERRORS = "retriable_errors";
public static final java.lang.String NOT_CONFIGURED_PREFIX = "[insert";
public static final java.lang.String NOT_CONFIGURED_POSTFIX = null;
public static final java.lang.String SANDBOX = "sandbox";
public FilePropertiesConfig
(java.lang.String propertiesFilename) {
super ();
}
public FilePropertiesConfig
(java.io.InputStream stream) {
super ();
}
public  groovy.lang.MetaClass getMetaClass() { return (groovy.lang.MetaClass)null;}
public  void setMetaClass(groovy.lang.MetaClass mc) { }
public  java.lang.Object invokeMethod(java.lang.String method, java.lang.Object arguments) { return null;}
public  java.lang.Object getProperty(java.lang.String property) { return null;}
public  void setProperty(java.lang.String property, java.lang.Object value) { }
}
