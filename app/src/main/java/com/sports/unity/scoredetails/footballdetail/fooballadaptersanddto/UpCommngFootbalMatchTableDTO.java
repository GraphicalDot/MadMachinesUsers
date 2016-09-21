package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by madmachines on 25/2/16.
 */
public class UpCommngFootbalMatchTableDTO  {

    private int viewType = 0;
    private String ivTeamProfileImage;
    private String tvTeamName;
    private String tvP;
    private String tvW;
    private String tvD;
    private String tvL;
    private String tvDG;
    private String tvPts;
    private String rank;
    private String description;

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getViewType() {
        return viewType;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    private String teamId;

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

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getTvDG() {
        return tvDG;
    }

    public void setTvDG(String tvDG) {
        this.tvDG = tvDG;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
