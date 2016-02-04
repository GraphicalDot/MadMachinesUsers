package com.sports.unity.scoredetails.cricketdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cfeindia on 4/2/16.
 */
public class CricketMatchDetailModel implements Parcelable {

    protected CricketMatchDetailModel(Parcel in) {
    }

    public static final Creator<CricketMatchDetailModel> CREATOR = new Creator<CricketMatchDetailModel>() {
        @Override
        public CricketMatchDetailModel createFromParcel(Parcel in) {
            return new CricketMatchDetailModel(in);
        }

        @Override
        public CricketMatchDetailModel[] newArray(int size) {
            return new CricketMatchDetailModel[size];
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
