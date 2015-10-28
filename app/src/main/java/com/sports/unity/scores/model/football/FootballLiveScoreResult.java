package com.sports.unity.scores.model.football;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by madmachines on 8/10/15.
 */
public class FootballLiveScoreResult {


    @SerializedName("match_date_epoch")
    @Expose
    private Integer matchDateEpoch;
    @SerializedName("match_status")
    @Expose
    private String matchStatus;
    @SerializedName("home_team")
    @Expose
    private String homeTeam;
    @SerializedName("away_team")
    @Expose
    private String awayTeam;
    @SerializedName("match_id")
    @Expose
    private Integer matchId;
    @SerializedName("league_id")
    @Expose
    private Integer leagueId;
    @SerializedName("match_date")
    @Expose
    private String matchDate;
    @SerializedName("away_team_flag")
    @Expose
    private String awayTeamFlag;
    @SerializedName("home_team_flag")
    @Expose
    private String homeTeamFlag;
    @SerializedName("match_time")
    @Expose
    private String matchTime;
    @SerializedName("stadium")
    @Expose
    private String stadium;
    @SerializedName("away_team_score")
    @Expose
    private String awayTeamScore;
    @SerializedName("home_team_score")
    @Expose
    private String homeTeamScore;

    /**
     * @return The matchDateEpoch
     */
    public Integer getMatchDateEpoch() {
        return matchDateEpoch;
    }

    /**
     * @param matchDateEpoch The match_date_epoch
     */
    public void setMatchDateEpoch(Integer matchDateEpoch) {
        this.matchDateEpoch = matchDateEpoch;
    }

    /**
     * @return The matchStatus
     */
    public String getMatchStatus() {
        return matchStatus;
    }

    /**
     * @param matchStatus The match_status
     */
    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    /**
     * @return The homeTeam
     */
    public String getHomeTeam() {
        return homeTeam;
    }

    /**
     * @param homeTeam The home_team
     */
    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    /**
     * @return The awayTeam
     */
    public String getAwayTeam() {
        return awayTeam;
    }

    /**
     * @param awayTeam The away_team
     */
    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    /**
     * @return The matchId
     */
    public Integer getMatchId() {
        return matchId;
    }

    /**
     * @param matchId The match_id
     */
    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    /**
     * @return The leagueId
     */
    public Integer getLeagueId() {
        return leagueId;
    }

    /**
     * @param leagueId The league_id
     */
    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    /**
     * @return The matchDate
     */
    public String getMatchDate() {
        return matchDate;
    }

    /**
     * @param matchDate The match_date
     */
    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    /**
     * @return The awayTeamFlag
     */
    public String getAwayTeamFlag() {
        return awayTeamFlag;
    }

    /**
     * @param awayTeamFlag The away_team_flag
     */
    public void setAwayTeamFlag(String awayTeamFlag) {
        this.awayTeamFlag = awayTeamFlag;
    }

    /**
     * @return The homeTeamFlag
     */
    public String getHomeTeamFlag() {
        return homeTeamFlag;
    }

    /**
     * @param homeTeamFlag The home_team_flag
     */
    public void setHomeTeamFlag(String homeTeamFlag) {
        this.homeTeamFlag = homeTeamFlag;
    }

    /**
     * @return The matchTime
     */
    public String getMatchTime() {
        return matchTime;
    }

    /**
     * @param matchTime The match_time
     */
    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    /**
     * @return The stadium
     */
    public String getStadium() {
        return stadium;
    }

    /**
     * @param stadium The stadium
     */
    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    /**
     * @return The awayTeamScore
     */
    public String getAwayTeamScore() {
        return awayTeamScore;
    }

    /**
     * @param awayTeamScore The away_team_score
     */
    public void setAwayTeamScore(String awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    /**
     * @return The homeTeamScore
     */
    public String getHomeTeamScore() {
        return homeTeamScore;
    }

    /**
     * @param homeTeamScore The home_team_score
     */
    public void setHomeTeamScore(String homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

}