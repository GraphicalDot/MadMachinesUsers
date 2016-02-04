package com.sports.unity.scoredetails.cricketdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cfeindia on 4/2/16.
 */
public class CricketMatchSummaryModel implements Parcelable {
    protected CricketMatchSummaryModel(Parcel in) {
    }

    public static final Creator<CricketMatchSummaryModel> CREATOR = new Creator<CricketMatchSummaryModel>() {
        @Override
        public CricketMatchSummaryModel createFromParcel(Parcel in) {
            return new CricketMatchSummaryModel(in);
        }

        @Override
        public CricketMatchSummaryModel[] newArray(int size) {
            return new CricketMatchSummaryModel[size];
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
