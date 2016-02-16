
package com.sports.unity.scoredetails.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class A1 {

    private String teamRunRate;
    private List<String> didNotBat = new ArrayList<String>();
    private int teamWickets;
    private int teamRuns;
    private int inningExtras;
    private List<Bowling_> bowling = new ArrayList<Bowling_>();
    private double teamOvers;
    private List<String> fallOfWickets = new ArrayList<String>();
    private List<Batting_> batting = new ArrayList<Batting_>();
    private List<ExtrasStr_> extrasStr = new ArrayList<ExtrasStr_>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The teamRunRate
     */
    public String getTeamRunRate() {
        return teamRunRate;
    }

    /**
     * 
     * @param teamRunRate
     *     The team_run_rate
     */
    public void setTeamRunRate(String teamRunRate) {
        this.teamRunRate = teamRunRate;
    }

    /**
     * 
     * @return
     *     The didNotBat
     */
    public List<String> getDidNotBat() {
        return didNotBat;
    }

    /**
     * 
     * @param didNotBat
     *     The did_not_bat
     */
    public void setDidNotBat(List<String> didNotBat) {
        this.didNotBat = didNotBat;
    }

    /**
     * 
     * @return
     *     The teamWickets
     */
    public int getTeamWickets() {
        return teamWickets;
    }

    /**
     * 
     * @param teamWickets
     *     The team_wickets
     */
    public void setTeamWickets(int teamWickets) {
        this.teamWickets = teamWickets;
    }

    /**
     * 
     * @return
     *     The teamRuns
     */
    public int getTeamRuns() {
        return teamRuns;
    }

    /**
     * 
     * @param teamRuns
     *     The team_runs
     */
    public void setTeamRuns(int teamRuns) {
        this.teamRuns = teamRuns;
    }

    /**
     * 
     * @return
     *     The inningExtras
     */
    public int getInningExtras() {
        return inningExtras;
    }

    /**
     * 
     * @param inningExtras
     *     The inning_extras
     */
    public void setInningExtras(int inningExtras) {
        this.inningExtras = inningExtras;
    }

    /**
     * 
     * @return
     *     The bowling
     */
    public List<Bowling_> getBowling() {
        return bowling;
    }

    /**
     * 
     * @param bowling
     *     The bowling
     */
    public void setBowling(List<Bowling_> bowling) {
        this.bowling = bowling;
    }

    /**
     * 
     * @return
     *     The teamOvers
     */
    public double getTeamOvers() {
        return teamOvers;
    }

    /**
     * 
     * @param teamOvers
     *     The team_overs
     */
    public void setTeamOvers(double teamOvers) {
        this.teamOvers = teamOvers;
    }

    /**
     * 
     * @return
     *     The fallOfWickets
     */
    public List<String> getFallOfWickets() {
        return fallOfWickets;
    }

    /**
     * 
     * @param fallOfWickets
     *     The fall_of_wickets
     */
    public void setFallOfWickets(List<String> fallOfWickets) {
        this.fallOfWickets = fallOfWickets;
    }

    /**
     * 
     * @return
     *     The batting
     */
    public List<Batting_> getBatting() {
        return batting;
    }

    /**
     * 
     * @param batting
     *     The batting
     */
    public void setBatting(List<Batting_> batting) {
        this.batting = batting;
    }

    /**
     * 
     * @return
     *     The extrasStr
     */
    public List<ExtrasStr_> getExtrasStr() {
        return extrasStr;
    }

    /**
     * 
     * @param extrasStr
     *     The extras_str
     */
    public void setExtrasStr(List<ExtrasStr_> extrasStr) {
        this.extrasStr = extrasStr;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
