package com.sports.unity.scoredetails;

/**
 * Created by madmachines on 24/2/16.
 */
public class BallDetail {
    private int ballId;
    private  String value = ".";
    private int type= 0;
    private int fontColor;
    private int backGroundColor;

    public String getValue() {
        return value;
    }

    public BallDetail() {
    }

    public int getBallId() {
        return ballId;
    }

    public void setBallId(int ballId) {
        this.ballId = ballId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    public int getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
    }
}
