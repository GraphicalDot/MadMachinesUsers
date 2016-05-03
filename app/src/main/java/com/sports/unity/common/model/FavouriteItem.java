package com.sports.unity.common.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model class which contains all the info of single favourite item e.g. Name, ID, Flag Url, Sports Type and filter type.
 */
public class FavouriteItem implements Comparable<FavouriteItem> {
    private String name;
    private boolean isChecked;
    private String id;
    private String flagImageUrl;
    private String sportsType;
    private String filterType;

    /**
     * Blank constructor.
     */
    public FavouriteItem() {

    }

    /**
     * Public constructor to create favorite item
     * from Json String.
     * This Constructor helps to create the item
     * directly when passing between activities.
     *
     * @param jsonObject String came within intent from origin activity.
     */
    public FavouriteItem(String jsonObject) {
        JSONObject object = null;
        try {
            object = new JSONObject(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            this.setName(object.getString(FavouriteItemWrapper.name));
            this.setSportsType(object.getString(FavouriteItemWrapper.sportsType));
            this.setFilterType(object.getString(FavouriteItemWrapper.filterType));
            if (!object.isNull(FavouriteItemWrapper.flag)) {
                this.setFlagImageUrl(object.getString(FavouriteItemWrapper.flag));
            }
            if (!object.isNull(FavouriteItemWrapper.id)) {
                this.setId(object.getString(FavouriteItemWrapper.id));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set name of the favourite.
     *
     * @param name favourite name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the item as selected.
     *
     * @param isChecked weather this item is already selected.
     */
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    /**
     * Set ID of the favourite.
     *
     * @param id id of this item.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set the flag image uri of this item.
     *
     * @param flagImageUrl
     */
    public void setFlagImageUrl(String flagImageUrl) {
        this.flagImageUrl = flagImageUrl;
    }

    /**
     * Set sports type of this item. e.g. Cricket or Football.
     *
     * @param sportsType sports type of this item.
     */
    public void setSportsType(String sportsType) {
        this.sportsType = sportsType;
    }

    /**
     * Set filter type of this item e.g. League, Team, Player. See
     * {@link com.sports.unity.util.Constants FILTER_TYPES}
     *
     * @param filterType filter type of this item.
     */
    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    /**
     * Get name of this favourite.
     *
     * @return Name of favourite.
     */
    public String getName() {
        return name;
    }

    /**
     * Check if the item is already selected.
     *
     * @return Weather item is already selected.
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * Get item id of favourite.
     *
     * @return Id of item.
     */
    public String getId() {
        return id;
    }

    /**
     * Get flag url of favourite item.
     *
     * @return Flag url.
     */
    public String getFlagImageUrl() {
        return flagImageUrl;
    }

    /**
     * Get sport type of favourite. e.g. Cricket or Football.
     *
     * @return sports type.
     */
    public String getSportsType() {

        return sportsType;
    }

    /**
     * Get filter type of favourite item. e.g. League, Team or Player.
     *
     * @return Filter type.
     */
    public String getFilterType() {
        return filterType;
    }

    /**
     * Convert favorite item in to JSONObject.
     * See
     * {@link org.json.JSONObject}
     *
     * @return JSONObject of favourite item.
     */
    public JSONObject getJsonObject() {
        JSONObject object = new JSONObject();
        try {
            object.put(FavouriteItemWrapper.name, getName());
            object.put(FavouriteItemWrapper.sportsType, getSportsType());
            object.put(FavouriteItemWrapper.filterType, getFilterType());
            if (id != null) {
                object.put(FavouriteItemWrapper.id, getId());
            }
            if (flagImageUrl != null) {
                object.put(FavouriteItemWrapper.flag, getFlagImageUrl());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public int compareTo(FavouriteItem another) {
        return this.getName().compareTo(another.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FavouriteItem) {
            return this.getId().equalsIgnoreCase(((FavouriteItem) o).getId());
        }
        return false;
    }

}
