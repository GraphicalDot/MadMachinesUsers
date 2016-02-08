package com.sports.unity.scores;

/**
 * Created by cfeindia on 6/2/16.
 */
public interface DataServiceContract {
    void dataChanged();
    void requestData(int methodType);
}
