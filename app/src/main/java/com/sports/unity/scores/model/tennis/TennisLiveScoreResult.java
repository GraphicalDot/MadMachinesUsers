package com.sports.unity.scores.model.tennis;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 3/11/15.
 */
public class TennisLiveScoreResult {

    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("tournament")
    @Expose
    private String tournament;
    @SerializedName("server")
    @Expose
    private Integer server;
    @SerializedName("player2")
    @Expose
    private String player2;
    @SerializedName("player1")
    @Expose
    private String player1;
    @SerializedName("sets")
    @Expose
    private List<String> sets = new ArrayList<String>();
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("match_staus")
    @Expose
    private String matchStaus;

    /**
     *
     * @return
     * The score
     */
    public String getScore() {
        return score;
    }

    /**
     *
     * @param score
     * The score
     */
    public void setScore(String score) {
        this.score = score;
    }

    /**
     *
     * @return
     * The tournament
     */
    public String getTournament() {
        return tournament;
    }

    /**
     *
     * @param tournament
     * The tournament
     */
    public void setTournament(String tournament) {
        this.tournament = tournament;
    }

    /**
     *
     * @return
     * The server
     */
    public Integer getServer() {
        return server;
    }

    /**
     *
     * @param server
     * The server
     */
    public void setServer(Integer server) {
        this.server = server;
    }

    /**
     *
     * @return
     * The player2
     */
    public String getPlayer2() {
        return player2;
    }

    /**
     *
     * @param player2
     * The player2
     */
    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    /**
     *
     * @return
     * The player1
     */
    public String getPlayer1() {
        return player1;
    }

    /**
     *
     * @param player1
     * The player1
     */
    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    /**
     *
     * @return
     * The sets
     */
    public List<String> getSets() {
        return sets;
    }

    /**
     *
     * @param sets
     * The sets
     */
    public void setSets(List<String> sets) {
        this.sets = sets;
    }

    /**
     *
     * @return
     * The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     * The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     * The matchStaus
     */
    public String getMatchStaus() {
        return matchStaus;
    }

    /**
     *
     * @param matchStaus
     * The match_staus
     */
    public void setMatchStaus(String matchStaus) {
        this.matchStaus = matchStaus;
    }

}
