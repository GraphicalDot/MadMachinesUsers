package com.sports.unity.scoredetails;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by madmachines on 10/2/16.
 */
public class MatchStatModel implements Parcelable {
    protected MatchStatModel(Parcel in) {
    }

    public static final Creator<MatchStatModel> CREATOR = new Creator<MatchStatModel>() {
        @Override
        public MatchStatModel createFromParcel(Parcel in) {
            return new MatchStatModel(in);
        }

        @Override
        public MatchStatModel[] newArray(int size) {
            return new MatchStatModel[size];
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
