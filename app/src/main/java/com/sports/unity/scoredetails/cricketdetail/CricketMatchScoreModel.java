package com.sports.unity.scoredetails.cricketdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cfeindia on 4/2/16.
 */
public class CricketMatchScoreModel implements Parcelable {
    protected CricketMatchScoreModel(Parcel in) {
    }

    public static final Creator<CricketMatchScoreModel> CREATOR = new Creator<CricketMatchScoreModel>() {
        @Override
        public CricketMatchScoreModel createFromParcel(Parcel in) {
            return new CricketMatchScoreModel(in);
        }

        @Override
        public CricketMatchScoreModel[] newArray(int size) {
            return new CricketMatchScoreModel[size];
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
