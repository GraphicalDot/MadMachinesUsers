package com.sports.unity.scoredetails.footballdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cfeindia on 4/2/16.
 */
public class FootballMatchStatsModel implements Parcelable {
    protected FootballMatchStatsModel(Parcel in) {
    }

    public static final Creator<FootballMatchStatsModel> CREATOR = new Creator<FootballMatchStatsModel>() {
        @Override
        public FootballMatchStatsModel createFromParcel(Parcel in) {
            return new FootballMatchStatsModel(in);
        }

        @Override
        public FootballMatchStatsModel[] newArray(int size) {
            return new FootballMatchStatsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
