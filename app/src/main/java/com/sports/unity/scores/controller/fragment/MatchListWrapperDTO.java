package com.sports.unity.scores.controller.fragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 8/3/16.
 */
public class MatchListWrapperDTO  implements  Comparable<MatchListWrapperDTO>{
    private String day;
    private String leagueName;
    private ArrayList<JSONObject> list;
    private Long epochTime;
    private String sportsType;
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<JSONObject> getList() {
        return list;
    }

    public void setList(ArrayList<JSONObject> list) {
        this.list = list;
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
    public int compareTo(MatchListWrapperDTO another) {
        return this.epochTime.compareTo(another.epochTime);
    }

    @Override
    public String toString() {
        return "MatchListWrapperDTO{" +
                "day='" + day + '\'' +
                ", leagueName='" + leagueName + '\'' +
                ", list=" + list +
                ", epochTime=" + epochTime +
                ", sportsType='" + sportsType + '\'' +
                '}';
    }
}
