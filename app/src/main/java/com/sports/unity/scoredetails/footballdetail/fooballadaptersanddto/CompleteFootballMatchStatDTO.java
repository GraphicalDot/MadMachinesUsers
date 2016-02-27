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
}
