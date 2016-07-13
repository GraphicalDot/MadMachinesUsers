package com.sports.unity.common.model;

/**
 * Created by madmachines on 15/6/16.
 */
public class GlobalContentItemObject {

    private int type;
    private Object object;

    public GlobalContentItemObject(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public int getType() {
        return type;
    }

}
