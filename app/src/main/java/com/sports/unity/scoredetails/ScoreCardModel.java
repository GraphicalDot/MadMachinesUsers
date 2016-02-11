package com.sports.unity.scoredetails;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by madmachines on 10/2/16.
 */
public class ScoreCardModel implements Parcelable {

    protected ScoreCardModel(Parcel in) {
    }

    public static final Creator<ScoreCardModel> CREATOR = new Creator<ScoreCardModel>() {
        @Override
        public ScoreCardModel createFromParcel(Parcel in) {
            return new ScoreCardModel(in);
        }

        @Override
        public ScoreCardModel[] newArray(int size) {
            return new ScoreCardModel[size];
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
