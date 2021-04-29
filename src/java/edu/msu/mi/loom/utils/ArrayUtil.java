package edu.msu.mi.loom.utils;
import java.util.ArrayList;
import java.util.Collection;
import grails.converters.JSON;
import org.codehaus.groovy.grails.web.json.JSONArray;
import org.codehaus.groovy.grails.web.json.JSONException;

public class ArrayUtil {
    public static ArrayList<Object> convert(JSONArray jArr)
    {
        ArrayList<Object> list = new ArrayList<Object>();
        try {
            for (int i=0, l=jArr.length(); i<l; i++){
                list.add(jArr.get(i));
            }
        } catch (JSONException e) {}

        return list;
    }

    public static JSONArray convert(Collection<Object> list)
    {
        return new JSONArray(list);
    }

}
