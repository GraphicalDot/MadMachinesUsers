package com.sports.unity.news.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 13/10/15.
 */
public class NewsList {

    private List<News> result = new ArrayList<News>();
    private Boolean success;
    private Boolean error;

    /**
     * @returnThe result
     */
    public List<News> getResult() {
        return result;
    }

    /**
     *@param result The result
     */
    public void setResult(List<News> result) {
        this.result = result;
    }

    /**
     *@return The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     *@param success The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     *@return The error
     */
    public Boolean getError() {
        return error;
    }

    /**
     *@param error The error
     */
    public void setError(Boolean error) {
        this.error = error;
    }

}
