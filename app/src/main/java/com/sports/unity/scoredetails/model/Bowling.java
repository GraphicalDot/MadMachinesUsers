
package com.sports.unity.scoredetails.model;

import java.util.HashMap;
import java.util.Map;


public class Bowling {

    private String runs;
    private String maiden;
    private String wickets;
    private String player;
    private String extras;
    private int overs;
    private String economy;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The runs
     */
    public String getRuns() {
        return runs;
    }

    /**
     * 
     * @param runs
     *     The runs
     */
    public void setRuns(String runs) {
        this.runs = runs;
    }

    /**
     * 
     * @return
     *     The maiden
     */
    public String getMaiden() {
        return maiden;
    }

    /**
     * 
     * @param maiden
     *     The maiden
     */
    public void setMaiden(String maiden) {
        this.maiden = maiden;
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

    /**
     * 
     * @return
     *     The player
     */
    public String getPlayer() {
        return player;
    }

    /**
     * 
     * @param player
     *     The player
     */
    public void setPlayer(String player) {
        this.player = player;
    }

    /**
     * 
     * @return
     *     The extras
     */
    public String getExtras() {
        return extras;
    }

    /**
     * 
     * @param extras
     *     The extras
     */
    public void setExtras(String extras) {
        this.extras = extras;
    }

    /**
     * 
     * @return
     *     The overs
     */
    public int getOvers() {
        return overs;
    }

    /**
     * 
     * @param overs
     *     The overs
     */
    public void setOvers(int overs) {
        this.overs = overs;
    }

    /**
     * 
     * @return
     *     The economy
     */
    public String getEconomy() {
        return economy;
    }

    /**
     * 
     * @param economy
     *     The economy
     */
    public void setEconomy(String economy) {
        this.economy = economy;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
