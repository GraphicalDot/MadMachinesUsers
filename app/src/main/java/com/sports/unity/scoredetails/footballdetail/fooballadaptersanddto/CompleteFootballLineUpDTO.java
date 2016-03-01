package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

/**
 * Created by madmachines on 1/3/16.
 */
public class CompleteFootballLineUpDTO {
    private String playerPostionNumber;
    private String playerName;
    private String cardType;
    private String goal;
    private String enterExitImage;

    public String getPlayerPostionNumber() {
        return playerPostionNumber;
    }

    public void setPlayerPostionNumber(String playerPostionNumber) {
        this.playerPostionNumber = playerPostionNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getEnterExitImage() {
        return enterExitImage;
    }

    public void setEnterExitImage(String enterExitImage) {
        this.enterExitImage = enterExitImage;
    }
}
