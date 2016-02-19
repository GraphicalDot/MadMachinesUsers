
package com.sports.unity.scoredetails.model;

import java.util.HashMap;
import java.util.Map;



public class India {

    private A1 a1;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The a1
     */
    public A1 getA1() {
        return a1;
    }

    /**
     * 
     * @param a1
     *     The a_1
     */
    public void setA1(A1 a1) {
        this.a1 = a1;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
