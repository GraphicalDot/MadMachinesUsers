package com.sports.unity.common.model;

/**
 * Created by Mad on 12/29/2015.
 */
public class FavouriteItem {
    private String name;
    private boolean isChecked;

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
