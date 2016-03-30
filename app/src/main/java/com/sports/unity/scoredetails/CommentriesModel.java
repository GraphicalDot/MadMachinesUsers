package com.sports.unity.scoredetails;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cfeindia on 4/2/16.
 */
public class CommentriesModel implements Parcelable, Comparable<CommentriesModel> {
    private  Integer commentaryId;
    private String comment;
    private String over;
    private String minute;

    public Integer getCommentaryId() {
        return commentaryId;
    }

    public void setCommentaryId(Integer commentaryId) {
        this.commentaryId = commentaryId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOver() {
        return over;
    }

    public void setOver(String over) {
        this.over = over;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public CommentriesModel(){

    }
    protected CommentriesModel(Parcel in) {
        comment = in.readString();
        over = in.readString();
        minute = in.readString();
    }

    public static final Creator<CommentriesModel> CREATOR = new Creator<CommentriesModel>() {
        @Override
        public CommentriesModel createFromParcel(Parcel in) {

            return new CommentriesModel(in);
        }

        @Override
        public CommentriesModel[] newArray(int size) {
            return new CommentriesModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comment);
        dest.writeString(over);
        dest.writeString(minute);


    }

    @Override
    public int compareTo(CommentriesModel another) {
        return another.getCommentaryId().compareTo(this.getCommentaryId());
    }
}
