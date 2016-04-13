package com.sports.unity.scores.controller.fragment;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by madmachines on 8/3/16.
 */
public class MatchListWrapperItem implements Comparable<MatchListWrapperItem> {
    private String day;
    private String leagueName;
    private JSONObject object;
    private Long epochTime;
    private String sportsType;

    public MatchListWrapperItem(MatchListWrapperDTO dto) {
        this.setDay(dto.getDay());
        this.setEpochTime(dto.getEpochTime());
        this.setSportsType(dto.getSportsType());
        this.setLeagueName(dto.getLeagueName());
    }

    public MatchListWrapperItem() {
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public JSONObject getJsonObject() {
        return object;
    }

    public void setJsonObject(JSONObject object) {
        this.object = object;
    }

    public Long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(Long epochTime) {
        this.epochTime = epochTime;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getSportsType() {
        return sportsType;
    }

    public void setSportsType(String sportsType) {
        this.sportsType = sportsType;
    }

    @Override
    public int compareTo(MatchListWrapperItem another) {
        return this.epochTime.compareTo(another.epochTime);
    }

    @Override
    public String toString() {
        return "MatchListWrapperDTO{" +
                "day='" + day + '\'' +
                ", leagueName='" + leagueName + '\'' +
                ", list=" + object +
                ", epochTime=" + epochTime +
                ", sportsType='" + sportsType + '\'' +
                '}';
    }
}
