package com.sports.unity.messages.controller.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by madmachines on 15/3/16.
 */
public class ShareableData implements Parcelable {

    public String mimeType = "";
    public String textData = "";
    public String pathOrFileNameForMedia = "";

    public ShareableData(String mimeType, String textData, String pathOrFileNameForMedia) {

        this.mimeType = mimeType;
        this.textData = textData;
        this.pathOrFileNameForMedia = pathOrFileNameForMedia;
    }

    protected ShareableData(Parcel in) {
        this.mimeType = in.readString();
        this.textData = in.readString();
        this.pathOrFileNameForMedia = in.readString();
    }

    public static final Creator<ShareableData> CREATOR = new Creator<ShareableData>() {
        @Override
        public ShareableData createFromParcel(Parcel in) {
            return new ShareableData(in);
        }

        @Override
        public ShareableData[] newArray(int size) {
            return new ShareableData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mimeType);
        dest.writeString(textData);
        dest.writeString(pathOrFileNameForMedia);
    }
}
