package com.sports.unity.scoredetails.footballdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cfeindia on 4/2/16.
 */
public class FootballMatchTimelineModel implements Parcelable {
    protected FootballMatchTimelineModel(Parcel in) {
    }

    public static final Creator<FootballMatchTimelineModel> CREATOR = new Creator<FootballMatchTimelineModel>() {
        @Override
        public FootballMatchTimelineModel createFromParcel(Parcel in) {
            return new FootballMatchTimelineModel(in);
        }

        @Override
        public FootballMatchTimelineModel[] newArray(int size) {
            return new FootballMatchTimelineModel[size];
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
