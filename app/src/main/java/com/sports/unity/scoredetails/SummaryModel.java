package com.sports.unity.scoredetails;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by madmachines on 10/2/16.
 */
public class SummaryModel implements Parcelable {
    protected SummaryModel(Parcel in) {
    }

    public static final Creator<SummaryModel> CREATOR = new Creator<SummaryModel>() {
        @Override
        public SummaryModel createFromParcel(Parcel in) {
            return new SummaryModel(in);
        }

        @Override
        public SummaryModel[] newArray(int size) {
            return new SummaryModel[size];
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
