package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by cfeindia on 26/2/16.
 */
public class CompleteFootballMatchStatDTO {
    private String tvLable;
    private String ivLeftStatus;
    private String ivRightStatus;
    private String ivCenterStatus;
    private int leftGraphValue;
    private int rightGraphValue;


    public String getTvLable() {
        return tvLable;
    }

    public void setTvLable(String tvLable) {
        this.tvLable = tvLable;
    }

    public String getIvLeftStatus() {
        return ivLeftStatus;
    }

    public void setIvLeftStatus(String ivLeftStatus) {
        this.ivLeftStatus = ivLeftStatus;
    }

    public String getIvRightStatus() {
        return ivRightStatus;
    }

    public void setIvRightStatus(String ivRightStatus) {
        this.ivRightStatus = ivRightStatus;
    }

    public String getIvCenterStatus() {
        return ivCenterStatus;
    }

    public void setIvCenterStatus(String ivCenterStatus) {
        this.ivCenterStatus = ivCenterStatus;
    }

    public int getLeftGraphValue() {
        return leftGraphValue;
    }

    public void setLeftGraphValue(int leftGraphValue) {
        this.leftGraphValue = leftGraphValue;
    }

    public int getRightGraphValue() {
        return rightGraphValue;
    }

    public void setRightGraphValue(int rightGraphValue) {
        this.rightGraphValue = rightGraphValue;
    }

    @Override
    public String toString() {
        return "CompleteFootballMatchStatDTO{" +
                "tvLable='" + tvLable + '\'' +
                ", ivLeftStatus='" + ivLeftStatus + '\'' +
                ", ivRightStatus='" + ivRightStatus + '\'' +
                ", ivCenterStatus='" + ivCenterStatus + '\'' +
                '}';
    }
}