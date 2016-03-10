package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.graphics.drawable.Drawable;

/**
 * Created by cfeindia on 28/2/16.
 */
public class CompleteFootballTimeLineDTO implements  Comparable<CompleteFootballTimeLineDTO>{
    private int minute;
    private String teamName;
    private String tvTeamFirstTime;
    private String tvTeamSecondTime;
    private String tvTeamFirstOnPlayer;
    private String tvTeamSecondOnPlayer;
    private String tvTeamFirstOffPlayer;
    private String tvTeamSecondOffPlayer;
    private Drawable drwDrawable;
    private String matchStatus;

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getTvTeamFirstTime() {
        return tvTeamFirstTime;
    }

    public void setTvTeamFirstTime(String tvTeamFirstTime) {
        this.tvTeamFirstTime = tvTeamFirstTime;
    }

    public String getTvTeamSecondTime() {
        return tvTeamSecondTime;
    }

    public void setTvTeamSecondTime(String tvTeamSecondTime) {
        this.tvTeamSecondTime = tvTeamSecondTime;
    }

    public String getTvTeamFirstOnPlayer() {
        return tvTeamFirstOnPlayer;
    }

    public void setTvTeamFirstOnPlayer(String tvTeamFirstOnPlayer) {
        this.tvTeamFirstOnPlayer = tvTeamFirstOnPlayer;
    }

    public String getTvTeamSecondOnPlayer() {
        return tvTeamSecondOnPlayer;
    }

    public void setTvTeamSecondOnPlayer(String tvTeamSecondOnPlayer) {
        this.tvTeamSecondOnPlayer = tvTeamSecondOnPlayer;
    }

    public String getTvTeamFirstOffPlayer() {
        return tvTeamFirstOffPlayer;
    }

    public void setTvTeamFirstOffPlayer(String tvTeamFirstOffPlayer) {
        this.tvTeamFirstOffPlayer = tvTeamFirstOffPlayer;
    }

    public String getTvTeamSecondOffPlayer() {
        return tvTeamSecondOffPlayer;
    }

    public void setTvTeamSecondOffPlayer(String tvTeamSecondOffPlayer) {
        this.tvTeamSecondOffPlayer = tvTeamSecondOffPlayer;
    }

    public Drawable getDrwDrawable() {
        return drwDrawable;
    }

    public void setDrwDrawable(Drawable drwDrawable) {
        this.drwDrawable = drwDrawable;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    @Override
    public int compareTo(CompleteFootballTimeLineDTO another) {
        if (this.tvTeamFirstTime == null) {
            return 1;
        } else if (another.tvTeamFirstTime == null) {
            return 1;
        }
        return this.tvTeamFirstTime.compareTo(another.tvTeamFirstTime);
    }
}
