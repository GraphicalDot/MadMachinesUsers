package com.sports.unity.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manish on 26/02/16.
 */
public class MatchDay implements Comparable<MatchDay> {
    private long matchDay;
    private String dayTitle;
    private List<League> leagues = new ArrayList<>();

    public long getMatchDay() {
        return matchDay;
    }

    public void setMatchDay(long matchDay) {
        this.matchDay = matchDay;
    }

    public String getDayTitle() {
        return dayTitle;
    }

    public void setDayTitle(String dayTitle) {
        this.dayTitle = dayTitle;
    }

    public List<League> getLeagues() {
        return leagues;
    }

    public void setLeagues(List<League> leagues) {
        this.leagues = leagues;
    }

    @Override
    public int hashCode() {
        return (int) matchDay;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof MatchDay && o != null) {
            MatchDay m = (MatchDay) o;
            equals = (m.getMatchDay() == this.matchDay);
        }
        return equals;
    }

    @Override
    public int compareTo(MatchDay another) {

        return (int) (this.matchDay - another.matchDay);
    }
}
