
package com.sports.unity.scoredetails.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Playing11 {

    private List<String> SriLanka = new ArrayList<String>();
    private List<String> India = new ArrayList<String>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The SriLanka
     */
    public List<String> getSriLanka() {
        return SriLanka;
    }

    /**
     * 
     * @param SriLanka
     *     The Sri Lanka
     */
    public void setSriLanka(List<String> SriLanka) {
        this.SriLanka = SriLanka;
    }

    /**
     * 
     * @return
     *     The India
     */
    public List<String> getIndia() {
        return India;
    }

    /**
     * 
     * @param India
     *     The India
     */
    public void setIndia(List<String> India) {
        this.India = India;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
