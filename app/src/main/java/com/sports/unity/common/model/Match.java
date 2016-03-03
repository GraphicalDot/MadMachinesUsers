package com.sports.unity.common.model;

import java.util.Date;

/**
 * Created by manish on 26/02/16.
 */
public class Match {

    private Date matchDateEpoch;
    private String matchStatus;
    private String homeTeam;
    private String awayTeam;
    private String stadium;
    private int matchId;
    private int homeTeamId;
    private Integer leagueId;
    private int homeTeamFlag;
    private String matchDate;
    private String awayTeamFlag;
    private boolean live;
    private String matchTime;
    private String leagueName;
    private String result;
    private int awayTeamId;
    private String awayTeamScore;
    private String homeTeamScore;

    public Date getMatchDateEpoch() {
        return matchDateEpoch;
    }

    public void setMatchDateEpoch(Date matchDateEpoch) {
        this.matchDateEpoch = matchDateEpoch;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getHomeTeamId() {
        return homeTeamId;
    }

    public void setHomeTeamId(int homeTeamId) {
        this.homeTeamId = homeTeamId;
    }

    public Integer getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Integer leagueId) {
        this.leagueId = leagueId;
    }

    public int getHomeTeamFlag() {
        return homeTeamFlag;
    }

    public void setHomeTeamFlag(int homeTeamFlag) {
        this.homeTeamFlag = homeTeamFlag;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getAwayTeamFlag() {
        return awayTeamFlag;
    }

    public void setAwayTeamFlag(String awayTeamFlag) {
        this.awayTeamFlag = awayTeamFlag;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getAwayTeamId() {
        return awayTeamId;
    }

    public void setAwayTeamId(int awayTeamId) {
        this.awayTeamId = awayTeamId;
    }

    public String getAwayTeamScore() {
        return awayTeamScore;
    }

    public void setAwayTeamScore(String awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public String getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setHomeTeamScore(String homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }
}
