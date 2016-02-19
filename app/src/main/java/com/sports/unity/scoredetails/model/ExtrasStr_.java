
package com.sports.unity.scoredetails.model;

import java.util.HashMap;
import java.util.Map;


public class ExtrasStr_ {

    private String nb;
    private String b;
    private String lb;
    private String w;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The nb
     */
    public String getNb() {
        return nb;
    }

    /**
     * 
     * @param nb
     *     The nb
     */
    public void setNb(String nb) {
        this.nb = nb;
    }

    /**
     * 
     * @return
     *     The b
     */
    public String getB() {
        return b;
    }

    /**
     * 
     * @param b
     *     The b
     */
    public void setB(String b) {
        this.b = b;
    }

    /**
     * 
     * @return
     *     The lb
     */
    public String getLb() {
        return lb;
    }

    /**
     * 
     * @param lb
     *     The lb
     */
    public void setLb(String lb) {
        this.lb = lb;
    }

    /**
     * 
     * @return
     *     The w
     */
    public String getW() {
        return w;
    }

    /**
     * 
     * @param w
     *     The w
     */
    public void setW(String w) {
        this.w = w;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
