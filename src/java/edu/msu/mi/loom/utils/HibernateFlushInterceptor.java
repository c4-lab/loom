package edu.msu.mi.loom.utils;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;


public class HibernateFlushInterceptor extends EmptyInterceptor {


    public HibernateFlushInterceptor() {
        // No-arg constructor
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        // Log or print entity and changed properties
        System.out.println("============>Flush dirty: " + entity);
        for (int i = 0; i < propertyNames.length; i++) {
            if (!currentState[i].equals(previousState[i])) {
                System.out.println("======>Changed property: " + propertyNames[i]);
            }
        }
        return false;
    }
}

