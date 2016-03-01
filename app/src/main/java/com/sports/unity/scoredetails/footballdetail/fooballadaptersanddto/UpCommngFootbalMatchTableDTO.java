package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by madmachines on 25/2/16.
 */
public class UpCommngFootbalMatchTableDTO  {

    private String ivTeamProfileImage;
    private String tvTeamName;
    private String tvP;
    private String tvW;
    private String tvD;
    private String tvL;
    private String tvPts;


    public String getIvTeamProfileImage() {
        return ivTeamProfileImage;
    }

    public void setIvTeamProfileImage(String ivTeamProfileImage) {
        this.ivTeamProfileImage = ivTeamProfileImage;
    }

    public String getTvTeamName() {
        return tvTeamName;
    }

    public void setTvTeamName(String tvTeamName) {
        this.tvTeamName = tvTeamName;
    }

    public String getTvP() {
        return tvP;
    }

    public void setTvP(String tvP) {
        this.tvP = tvP;
    }

    public String getTvW() {
        return tvW;
    }

    public void setTvW(String tvW) {
        this.tvW = tvW;
    }

    public String getTvD() {
        return tvD;
    }

    public void setTvD(String tvD) {
        this.tvD = tvD;
    }

    public String getTvL() {
        return tvL;
    }

    public void setTvL(String tvL) {
        this.tvL = tvL;
    }

    public String getTvPts() {
        return tvPts;
    }

    public void setTvPts(String tvPts) {
        this.tvPts = tvPts;
    }
}
