package com.sports.unity.scores.model.football;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 19/10/15.
 */
public class FootballLiveScore {

    @SerializedName("data")
    @Expose
    private List<FootballLiveScoreResult> footballLiveScoreResult = new ArrayList<FootballLiveScoreResult>();
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("error")
    @Expose
    private Boolean error;

    /**
     *
     * @return
     * The result
     */
    public List<FootballLiveScoreResult> getFootballLiveScoreResult() {
        return footballLiveScoreResult;
    }

    /**
     *
     * @param footballLiveScoreResult
     * The result
     */
    public void setFootballLiveScoreResult(List<FootballLiveScoreResult> footballLiveScoreResult) {
        this.footballLiveScoreResult = footballLiveScoreResult;
    }

    /**
     *
     * @return
     * The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The error
     */
    public Boolean getError() {
        return error;
    }

    /**
     *
     * @param error
     * The error
     */
    public void setError(Boolean error) {
        this.error = error;
    }

}
