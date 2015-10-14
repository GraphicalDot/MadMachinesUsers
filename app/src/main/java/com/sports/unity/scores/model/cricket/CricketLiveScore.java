package com.sports.unity.scores.model.cricket;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 8/10/15.
 */
public class CricketLiveScore {

    private List<Result> result = new ArrayList<Result>();

    private Boolean success;

    private Boolean error;

    /**
     * @return The result
     */
    public List<Result> getResult() {
        return result;
    }

    /**
     * @param result The result
     */
    public void setResult(List<Result> result) {
        this.result = result;
    }

    /**
     * @return The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * @param success The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * @return The error
     */
    public Boolean getError() {
        return error;
    }

    /**
     * @param error The error
     */
    public void setError(Boolean error) {
        this.error = error;
    }

}
