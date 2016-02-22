
package com.sports.unity.scoredetails.model;

import java.util.HashMap;
import java.util.Map;



public class Team2Score {

    private String score;
    private double overs;
    private String wickets;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The score
     */
    public String getScore() {
        return score;
    }

    /**
     * 
     * @param score
     *     The score
     */
    public void setScore(String score) {
        this.score = score;
    }

    /**
     * 
     * @return
     *     The overs
     */
    public double getOvers() {
        return overs;
    }

    /**
     * 
     * @param overs
     *     The overs
     */
    public void setOvers(double overs) {
        this.overs = overs;
    }

    /**
     * 
     * @return
     *     The wickets
     */
    public String getWickets() {
        return wickets;
    }

    /**
     * 
     * @param wickets
     *     The wickets
     */
    public void setWickets(String wickets) {
        this.wickets = wickets;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}