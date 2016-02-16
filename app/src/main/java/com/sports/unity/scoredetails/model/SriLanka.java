
package com.sports.unity.scoredetails.model;

import java.util.HashMap;
import java.util.Map;


public class SriLanka {

    private B1 b1;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The b1
     */
    public B1 getB1() {
        return b1;
    }

    /**
     * 
     * @param b1
     *     The b_1
     */
    public void setB1(B1 b1) {
        this.b1 = b1;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
