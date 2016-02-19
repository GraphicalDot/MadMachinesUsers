
package com.sports.unity.scoredetails.model;

import java.util.HashMap;
import java.util.Map;



public class Scorecard {

    private com.sports.unity.scoredetails.model.SriLanka SriLanka;
    private com.sports.unity.scoredetails.model.India India;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The SriLanka
     */
    public com.sports.unity.scoredetails.model.SriLanka getSriLanka() {
        return SriLanka;
    }

    /**
     * 
     * @param SriLanka
     *     The Sri Lanka
     */
    public void setSriLanka(com.sports.unity.scoredetails.model.SriLanka SriLanka) {
        this.SriLanka = SriLanka;
    }

    /**
     * 
     * @return
     *     The India
     */
    public com.sports.unity.scoredetails.model.India getIndia() {
        return India;
    }

    /**
     * 
     * @param India
     *     The India
     */
    public void setIndia(com.sports.unity.scoredetails.model.India India) {
        this.India = India;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
