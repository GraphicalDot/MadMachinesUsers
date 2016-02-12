package com.sports.unity.common.model;

import android.content.Context;

import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class to save the favourite selection locally.
 * It convert the {@link ArrayList} of
 * {@link FavouriteItem} to {@link JSONArray} and
 * save it {@link android.content.SharedPreferences} as {@link String}.
 * Also retrieve back the {@link ArrayList} of
 * {@link FavouriteItem} from {@link android.content.SharedPreferences}.
 */
public class FavouriteItemWrapper {

    public static final String sportsType = "sports_type";
    public static final String filterType = "filter_type";
    public static final String name = "obj_name";
    public static final String id = "obj_id";
    public static final String flag = "obj_flag";

    private List<FavouriteItem> savedFootballLeagues;
    private List<FavouriteItem> savedFootballTeams;
    private List<FavouriteItem> savedFootballPlayers;
    private List<FavouriteItem> savedCricketTeams;
    private List<FavouriteItem> savedCricketPlayers;
    private List<FavouriteItem> savedFavlist;
    public static FavouriteItemWrapper favouriteItemWrapper;

    /**
     * Static constructor to instantiate {@link #FavouriteItemWrapper} class.
     *
     * @return single instance of {@link #FavouriteItemWrapper}.
     */
    public static FavouriteItemWrapper getInstance(Context context) {
        if (favouriteItemWrapper == null) {
            favouriteItemWrapper = new FavouriteItemWrapper(context);
        }
        return favouriteItemWrapper;
    }

    /**
     * Private constructor so that no one can instantiate this class.
     */
    private FavouriteItemWrapper(Context context) {
        savedFootballLeagues = new ArrayList<FavouriteItem>();
        savedFootballLeagues = new ArrayList<FavouriteItem>();
        savedFootballTeams = new ArrayList<FavouriteItem>();
        savedFootballPlayers = new ArrayList<FavouriteItem>();
        savedCricketTeams = new ArrayList<FavouriteItem>();
        savedCricketPlayers = new ArrayList<FavouriteItem>();
        savedFavlist = new ArrayList<FavouriteItem>();
        List<FavouriteItem> favouriteItems = new ArrayList<FavouriteItem>();
        TinyDB tinyDB = TinyDB.getInstance(context);
        String favItem = tinyDB.getString(TinyDB.FAVOURITE_FILTERS);
        try {
            JSONArray favArray = new JSONArray(favItem);
            for (int i = 0; i < favArray.length(); i++) {
                FavouriteItem item = new FavouriteItem();
                JSONObject object = favArray.getJSONObject(i);
                item.setName(object.getString(name));
                String sportsType = object.getString(this.sportsType);
                String filterType = object.getString(this.filterType);
                item.setSportsType(sportsType);
                item.setFilterType(filterType);

                try {
                    item.setFlagImageUrl(object.getString(this.flag));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                favouriteItems.add(item);

                if (sportsType.equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                    if (filterType.equals(Constants.FILTER_TYPE_LEAGUE)) {
                        savedFootballLeagues.add(item);
                    } else if (filterType.equals(Constants.FILTER_TYPE_TEAM)) {
                        savedFootballTeams.add(item);
                    } else if (filterType.equals(Constants.FILTER_TYPE_PLAYER)) {
                        savedFootballPlayers.add(item);
                    }
                } else {
                    if (sportsType.equals(Constants.SPORTS_TYPE_CRICKET)) {
                        if (filterType.equals(Constants.FILTER_TYPE_TEAM)) {
                            savedCricketTeams.add(item);
                        } else if (filterType.equals(Constants.FILTER_TYPE_PLAYER)) {
                            savedCricketPlayers.add(item);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(favouriteItems);
        savedFavlist.addAll(favouriteItems);
    }


    /**
     * This method converts the {@link ArrayList} of
     * {@link FavouriteItem} to {@link JSONArray} and
     * save it to {@link android.content.SharedPreferences} as {@link String}.
     *
     * @param context        Context of the origin {@link android.app.Activity}.
     * @param favouriteItems {@link ArrayList} of {@link FavouriteItem}
     */
    public void saveList(Context context, ArrayList<FavouriteItem> favouriteItems) {
        JSONArray jsonArray = new JSONArray();
        for (FavouriteItem f : favouriteItems) {
            jsonArray.put(f.getJsonObject());
        }
        updateFavList(favouriteItems);
        UserUtil.setFavouriteFilters(context, jsonArray.toString());
    }

    /**
     * When saving the current favourite list this
     * method helps to update the corresponding
     * lists of {@link #FavouriteItemWrapper} class without
     * retrieving them from shared preference.
     *
     * @param favouriteItems {@link ArrayList} of {@link FavouriteItem}
     */
    private void updateFavList(ArrayList<FavouriteItem> favouriteItems) {
        {
            savedFootballLeagues = new ArrayList<FavouriteItem>();
            savedFootballTeams = new ArrayList<FavouriteItem>();
            savedFootballPlayers = new ArrayList<FavouriteItem>();
            savedCricketTeams = new ArrayList<FavouriteItem>();
            savedCricketPlayers = new ArrayList<FavouriteItem>();
            savedFavlist = new ArrayList<>(favouriteItems);
            Collections.sort(savedFavlist);
            for (FavouriteItem f : favouriteItems) {

                if (f.getSportsType().equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                    if (f.getFilterType().equals(Constants.FILTER_TYPE_LEAGUE)) {
                        savedFootballLeagues.add(f);
                    } else if (f.getFilterType().equals(Constants.FILTER_TYPE_TEAM)) {
                        savedFootballTeams.add(f);
                    } else if (f.getFilterType().equals(Constants.FILTER_TYPE_PLAYER)) {
                        savedFootballPlayers.add(f);
                    }
                } else if (f.getSportsType().equals(Constants.SPORTS_TYPE_CRICKET)) {
                    if (f.getFilterType().equals(Constants.FILTER_TYPE_TEAM)) {
                        savedCricketTeams.add(f);
                    } else if (f.getFilterType().equals(Constants.FILTER_TYPE_PLAYER)) {
                        savedCricketPlayers.add(f);
                    }
                }
            }
        }
    }

    /**
     * This method helps to retrieve the favorite selection of other users.
     * it parse the Json String back to ArrayList of {@link FavouriteItem}.
     *
     * @param favItem
     * @return
     */
    public ArrayList<FavouriteItem> getFavListOfOthers(String favItem) {
        List<FavouriteItem> favouriteItems = new ArrayList<FavouriteItem>();
        try {
            JSONArray favArray = new JSONArray(favItem);
            for (int i = 0; i < favArray.length(); i++) {
                FavouriteItem item = new FavouriteItem();
                JSONObject object = favArray.getJSONObject(i);
                item.setName(object.getString(name));
                String sportsType = object.getString(this.sportsType);
                String filterType = object.getString(this.filterType);
                item.setSportsType(sportsType);
                item.setFilterType(filterType);

                try {
                    item.setFlagImageUrl(object.getString(this.flag));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                favouriteItems.add(item);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.sort(favouriteItems);
        return new ArrayList<>(favouriteItems);
    }

    /**
     * This method returns list of all selected favourites.
     *
     * @return ArrayList of {@link FavouriteItem}
     */
    public ArrayList<FavouriteItem> getFavList() {
        return new ArrayList<FavouriteItem>(savedFavlist);
    }

    /**
     * This method returns the combined favourite Teams of Cricket and Football.
     *
     * @return ArrayList of Favourite teams.
     */
    public ArrayList<FavouriteItem> getAllTeams() {
        Collections.sort(savedCricketTeams);
        Collections.sort(savedFootballTeams);
        ArrayList<FavouriteItem> savedTeams = new ArrayList<FavouriteItem>();
        if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_CRICKET)) {
            savedTeams.addAll(savedCricketTeams);
        }
        if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL)) {
            savedTeams.addAll(savedFootballTeams);
        }
        return savedTeams;
    }

    /**
     * This method returns the combined favourite players of Cricket and Football.
     *
     * @return ArrayList of Favourite teams.
     */
    public ArrayList<FavouriteItem> getAllPlayers() {
        Collections.sort(savedCricketPlayers);
        Collections.sort(savedFootballPlayers);
        ArrayList<FavouriteItem> savedTeams = new ArrayList<FavouriteItem>();
        if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_CRICKET)) {
            savedTeams.addAll(savedCricketPlayers);
        }
        if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL)) {
            savedTeams.addAll(savedFootballPlayers);
        }
        return savedTeams;
    }


    /**
     * This method returns the favourite Football leagues.
     *
     * @return ArrayList of Favourite leagues.
     */
    public ArrayList<FavouriteItem> getAllLeagues() {
        Collections.sort(savedFootballLeagues);
        ArrayList<FavouriteItem> savedLeagues = new ArrayList<FavouriteItem>();
        savedLeagues.addAll(savedFootballLeagues);
        return savedLeagues;
    }

    /**
     * Get selected Favourite Cricket Team.
     *
     * @return ArrayList of Favourite Cricket Team.
     */
    public ArrayList<FavouriteItem> getCricketTeams() {
        return new ArrayList<FavouriteItem>(savedCricketTeams);
    }

    /**
     * Get selected Favourite Cricket players.
     *
     * @return ArrayList of Favourite Cricket players.
     */
    public ArrayList<FavouriteItem> getCricketPlayers() {
        return new ArrayList<FavouriteItem>(savedCricketPlayers);
    }

    /**
     * Get selected Favourite Football leagues.
     *
     * @return ArrayList of Favourite Football leagues.
     */
    public ArrayList<FavouriteItem> getFootballLeagues() {
        return new ArrayList<FavouriteItem>(savedFootballLeagues);
    }


    /**
     * Get selected Favourite Football Teams.
     *
     * @return ArrayList of Favourite Football Teams.
     */
    public ArrayList<FavouriteItem> getFootballTeams() {
        return new ArrayList<FavouriteItem>(savedFootballTeams);
    }


    /**
     * Get selected Favourite Football Players.
     *
     * @return ArrayList of Favourite Football Players.
     */
    public ArrayList<FavouriteItem> getFootballPlayers() {
        return new ArrayList<FavouriteItem>(savedFootballPlayers);
    }
}
