package com.sports.unity.scoredetails;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by madmachines on 10/2/16.
 */
public class MatchLineupModel implements Parcelable {
    protected MatchLineupModel(Parcel in) {
    }

    public static final Creator<MatchLineupModel> CREATOR = new Creator<MatchLineupModel>() {
        @Override
        public MatchLineupModel createFromParcel(Parcel in) {
            return new MatchLineupModel(in);
        }

        @Override
        public MatchLineupModel[] newArray(int size) {
            return new MatchLineupModel[size];
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
