package com.sports.unity.scoredetails;

/**
 * Created by madmachines on 24/2/16.
 */
public class BallDetail {
   private  String value = ".";
    private int type= 0;

    public String getValue() {
        return value;
    }

    public BallDetail() {
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
