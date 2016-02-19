package com.sports.unity.scoredetails.footballdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cfeindia on 4/2/16.
 */
public class FootballMatchDetailModel implements Parcelable {

    protected FootballMatchDetailModel(Parcel in) {
    }

    public static final Creator<FootballMatchDetailModel> CREATOR = new Creator<FootballMatchDetailModel>() {
        @Override
        public FootballMatchDetailModel createFromParcel(Parcel in) {
            return new FootballMatchDetailModel(in);
        }

        @Override
        public FootballMatchDetailModel[] newArray(int size) {
            return new FootballMatchDetailModel[size];
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
