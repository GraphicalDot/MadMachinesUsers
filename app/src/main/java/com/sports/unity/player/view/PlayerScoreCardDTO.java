package com.sports.unity.player.view;

/**
 * Created by madmachines on 9/2/16.
 */
public class PlayerScoreCardDTO {
    private String leagueName;
    private String teamName;
    private int noOfGames;
    private int noOfgoals;
    private int noOfAssists;
    private int noOfYellowCard;
    private int noOfRedCard;

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getNoOfGames() {
        return noOfGames;
    }
    public void setNoOfGames(int noOfGames) {
        this.noOfGames = noOfGames;
    }

    public int getNoOfgoals() {
        return noOfgoals;
    }

    public void setNoOfgoals(int noOfgoals) {
        this.noOfgoals = noOfgoals;
    }

    public int getNoOfAssists() {
        return noOfAssists;
    }

    public void setNoOfAssists(int noOfAssists) {
        this.noOfAssists = noOfAssists;
    }

    public int getNoOfYellowCard() {
        return noOfYellowCard;
    }

    public void setNoOfYellowCard(int noOfYellowCard) {
        this.noOfYellowCard = noOfYellowCard;
    }

    public int getNoOfRedCard() {
        return noOfRedCard;
    }

    public void setNoOfRedCard(int noOfRedCard) {
        this.noOfRedCard = noOfRedCard;
    }
}
