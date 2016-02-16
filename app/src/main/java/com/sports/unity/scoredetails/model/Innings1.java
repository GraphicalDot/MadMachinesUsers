
package com.sports.unity.scoredetails.model;

import java.util.HashMap;
import java.util.Map;


public class Innings1 {

    private TeamA teamA;
    private TeamB teamB;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The teamA
     */
    public TeamA getTeamA() {
        return teamA;
    }

    /**
     * 
     * @param teamA
     *     The team_a
     */
    public void setTeamA(TeamA teamA) {
        this.teamA = teamA;
    }

    /**
     * 
     * @return
     *     The teamB
     */
    public TeamB getTeamB() {
        return teamB;
    }

    /**
     * 
     * @param teamB
     *     The team_b
     */
    public void setTeamB(TeamB teamB) {
        this.teamB = teamB;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
