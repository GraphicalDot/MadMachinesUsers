
package com.sports.unity.scoredetails.model;

import java.util.HashMap;
import java.util.Map;



public class Batting_ {

    private int B;
    private double SR;
    private String playerStatus;
    private int _4s;
    private String player;
    private int _6s;
    private int R;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The B
     */
    public int getB() {
        return B;
    }

    /**
     * 
     * @param B
     *     The B
     */
    public void setB(int B) {
        this.B = B;
    }

    /**
     * 
     * @return
     *     The SR
     */
    public double getSR() {
        return SR;
    }

    /**
     * 
     * @param SR
     *     The SR
     */
    public void setSR(double SR) {
        this.SR = SR;
    }

    /**
     * 
     * @return
     *     The playerStatus
     */
    public String getPlayerStatus() {
        return playerStatus;
    }

    /**
     * 
     * @param playerStatus
     *     The player_status
     */
    public void setPlayerStatus(String playerStatus) {
        this.playerStatus = playerStatus;
    }

    /**
     * 
     * @return
     *     The _4s
     */
    public int get4s() {
        return _4s;
    }

    /**
     * 
     * @param _4s
     *     The 4s
     */
    public void set4s(int _4s) {
        this._4s = _4s;
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
     *     The _6s
     */
    public int get6s() {
        return _6s;
    }

    /**
     * 
     * @param _6s
     *     The 6s
     */
    public void set6s(int _6s) {
        this._6s = _6s;
    }

    /**
     * 
     * @return
     *     The R
     */
    public int getR() {
        return R;
    }

    /**
     * 
     * @param R
     *     The R
     */
    public void setR(int R) {
        this.R = R;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
