package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by cfeindia on 27/2/16.
 */
public class UpCommingFootballMatchSquadDTO {
    private String id;
    private String tvPlayerName;
    private String tvPlayerAge;
    private String tvP;
    private String tvpl;
    private String tvgol;
    private String tvyellowcard;
    private String tvredcard;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTvPlayerName() {
        return tvPlayerName;
    }

    public void setTvPlayerName(String tvPlayerName) {
        this.tvPlayerName = tvPlayerName;
    }

    public String getTvPlayerAge() {
        return tvPlayerAge;
    }

    public void setTvPlayerAge(String tvPlayerAge) {
        this.tvPlayerAge = tvPlayerAge;
    }

    public String getTvP() {
        return tvP;
    }

    public void setTvP(String tvP) {
        this.tvP = tvP;
    }

    public String getTvpl() {
        return tvpl;
    }

    public void setTvpl(String tvpl) {
        this.tvpl = tvpl;
    }

    public String getTvgol() {
        return tvgol;
    }

    public void setTvgol(String tvgol) {
        this.tvgol = tvgol;
    }

    public String getTvyellowcard() {
        return tvyellowcard;
    }

    public void setTvyellowcard(String tvyellowcard) {
        this.tvyellowcard = tvyellowcard;
    }

    public String getTvredcard() {
        return tvredcard;
    }

    public void setTvredcard(String tvredcard) {
        this.tvredcard = tvredcard;
    }
}
