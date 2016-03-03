package com.sports.unity.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manish on 26/02/16.
 */
public class League {
    private String sportType;
    private String leagueName;
    private int leagueId;
    private List<Match> matches = new ArrayList<>();

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
