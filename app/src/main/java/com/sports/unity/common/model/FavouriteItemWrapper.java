package com.sports.unity.common.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper singleton class to save the favourite selection locally.
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
    public static FavouriteItemWrapper favouriteItemWrapper;
    private List<FavouriteItem> savedFootballLeagues;
    private List<FavouriteItem> savedFootballTeams;
    private List<FavouriteItem> savedFootballPlayers;
    private List<FavouriteItem> savedCricketTeams;
    private List<FavouriteItem> savedCricketPlayers;
    private List<FavouriteItem> savedFavlist;

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
                    if (!object.isNull(this.flag)) {
                        item.setFlagImageUrl(object.getString(this.flag));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    item.setId(object.getString(this.id));
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
        if (favItem != null) {
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
                        if (!object.isNull(this.flag)) {
                            item.setFlagImageUrl(object.getString(this.flag));
                        }
                        if (!object.isNull(this.id)) {
                            item.setId(object.getString(this.id));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    favouriteItems.add(item);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

        if (UserUtil.getSportsSelected()!=null && UserUtil.getSportsSelected().contains(Constants.GAME_KEY_CRICKET)) {
            savedTeams.addAll(savedCricketTeams);
        }
        if (UserUtil.getSportsSelected()!=null && UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL)) {
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
        ArrayList<FavouriteItem> savedPlayers = new ArrayList<FavouriteItem>();
        if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_CRICKET)) {
            savedPlayers.addAll(savedCricketPlayers);
        }
        if (UserUtil.getSportsSelected().contains(Constants.GAME_KEY_FOOTBALL)) {
            savedPlayers.addAll(savedFootballPlayers);
        }
        return savedPlayers;
    }


    /**
     * This method returns the favourite Football leagues.
     *
     * @return ArrayList of Favourite leagues.
     */
    public ArrayList<FavouriteItem> getAllLeagues() {
        ArrayList<FavouriteItem> savedLeagues = new ArrayList<FavouriteItem>();
        savedLeagues.addAll(savedFootballLeagues);
        Collections.sort(savedLeagues);
        return savedLeagues;
    }

    /**
     * This method returns list of sports selected.
     *
     * @return ArrayList of Favourite leagues.
     */
    public ArrayList<FavouriteItem> getAllSports(Context context) {
        ArrayList<String> sportsSelected = UserUtil.getSportsSelected();
        ArrayList<FavouriteItem> savedSports = new ArrayList<FavouriteItem>();
        for (String s : sportsSelected) {
            FavouriteItem f = new FavouriteItem();
            String sportsName = CommonUtil.capitalize(s);
            f.setName(sportsName);
           /* if(sportsName.toLowerCase().contains(Constants.GAME_KEY_CRICKET.toLowerCase())){
                Uri uri = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/ic_cricket");
                f.setFlagImageUrl(uri.toString());
            }else if(sportsName.toLowerCase().contains(Constants.GAME_KEY_FOOTBALL.toLowerCase())){
                Uri uri = Uri.parse("android.resource://"+context.getPackageName()+"/drawable/ic_football");
                f.setFlagImageUrl(uri.toString());
            }*/
            savedSports.add(f);
        }
        Collections.sort(savedSports);
        return savedSports;
    }

    /**
     * Get selected Favourite Cricket Team.
     *
     * @return ArrayList of Favourite Cricket Team.
     */
    public ArrayList<FavouriteItem> getCricketTeams() {
        Collections.sort(savedCricketTeams);
        return new ArrayList<FavouriteItem>(savedCricketTeams);
    }

    /**
     * Get selected Favourite Cricket players.
     *
     * @return ArrayList of Favourite Cricket players.
     */
    public ArrayList<FavouriteItem> getCricketPlayers() {
        Collections.sort(savedCricketPlayers);
        return new ArrayList<FavouriteItem>(savedCricketPlayers);
    }

    /**
     * Get selected Favourite Football leagues.
     *
     * @return ArrayList of Favourite Football leagues.
     */
    public ArrayList<FavouriteItem> getFootballLeagues() {
        Collections.sort(savedFootballLeagues);
        return new ArrayList<FavouriteItem>(savedFootballLeagues);
    }


    /**
     * Get selected Favourite Football Teams.
     *
     * @return ArrayList of Favourite Football Teams.
     */
    public ArrayList<FavouriteItem> getFootballTeams() {
        Collections.sort(savedFootballTeams);
        return new ArrayList<FavouriteItem>(savedFootballTeams);
    }


    /**
     * Get selected Favourite Football Players.
     *
     * @return ArrayList of Favourite Football Players.
     */
    public ArrayList<FavouriteItem> getFootballPlayers() {
        Collections.sort(savedFootballPlayers);
        Collections.sort(savedFootballPlayers);
        return new ArrayList<FavouriteItem>(savedFootballPlayers);
    }

    /**
     * This method retrieves the saved favourites string from shared preference.
     *
     * @param context Context of origin activity.
     * @return Saved favourites as {@link JSONObject} String.
     */
    public String getSavedFavouritesAsJsonString(Context context) {
        TinyDB tinyDB = TinyDB.getInstance(context);
        return tinyDB.getString(TinyDB.FAVOURITE_FILTERS);
    }
}
