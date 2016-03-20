package com.sports.unity.playerprofile.football;

/**
 * Created by madmachines on 9/2/16.
 */
public class PlayerScoreCardDTO {
    private String leagueName;
    private String teamName;
    private String noOfGames;
    private String noOfgoals;
    private String noOfAssists;
    private String noOfYellowCard;
    private String noOfRedCard;

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

    public String getNoOfGames() {
        return noOfGames;
    }

    public void setNoOfGames(String noOfGames) {
        this.noOfGames = noOfGames;
    }

    public String getNoOfgoals() {
        return noOfgoals;
    }

    public void setNoOfgoals(String noOfgoals) {
        this.noOfgoals = noOfgoals;
    }

    public String getNoOfAssists() {
        return noOfAssists;
    }

    public void setNoOfAssists(String noOfAssists) {
        this.noOfAssists = noOfAssists;
    }

    public String getNoOfYellowCard() {
        return noOfYellowCard;
    }

    public void setNoOfYellowCard(String noOfYellowCard) {
        this.noOfYellowCard = noOfYellowCard;
    }

    public String getNoOfRedCard() {
        return noOfRedCard;
    }

    public void setNoOfRedCard(String noOfRedCard) {
        this.noOfRedCard = noOfRedCard;
    }
}
