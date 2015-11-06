package com.sports.unity.scores.model.football.tennis;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 3/11/15.
 */
public class TennisLiveScore {

    @SerializedName("data")
    @Expose
    private List<TennisLiveScoreResult> tennisLiveScoreResult = new ArrayList<TennisLiveScoreResult>();
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("error")
    @Expose
    private Boolean error;

    /**
     *
     * @return
     * The data
     */
    public List<TennisLiveScoreResult> getTennisLiveScoreResult() {
        return tennisLiveScoreResult;
    }

    /**
     *
     * @param tennisLiveScoreResult
     * The data
     */
    public void setData(List<TennisLiveScoreResult> tennisLiveScoreResult) {
        this.tennisLiveScoreResult = tennisLiveScoreResult;
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
