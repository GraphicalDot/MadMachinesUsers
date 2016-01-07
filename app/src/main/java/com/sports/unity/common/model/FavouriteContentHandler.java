package com.sports.unity.common.model;

import android.util.Log;

import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mad on 12/29/2015.
 */
public class FavouriteContentHandler {
    public static FavouriteContentHandler favouriteContentHandler;

    private static ArrayList<String> FOOTBALL_FILTER_LEAGUE = null;
    private static ArrayList<String> FOOTBALL_FILTER_PLAYER = null;
    private static ArrayList<String> FOOTBALL_FILTER_TEAM = null;


    private static ArrayList<String> CRICKET_FILTER_PLAYER = null;
    private static ArrayList<String> CRICKET_FILTER_TEAM = null;

    private ArrayList<FavouriteItem> favFootballLeagues;
    private ArrayList<FavouriteItem> favFootballTeams;
    private ArrayList<FavouriteItem> favFootballPlayers;

    private ArrayList<FavouriteItem> favCricketTeams;
    private ArrayList<FavouriteItem> favCricketPlayers;
    private ArrayList<JSONObject> matches = new ArrayList<>();
    private ScoresContentListener contentListener = new ScoresContentListener();

    private static final String LISTENER_KEY = "favourite_listener";
    private static final String FOOTBALL_LEAGUE_REQUEST_TAG = "football_league_request_tag";
    private static final String FOOTBALL_TEAM_REQUEST_TAG = "football_team_request_tag";
    private static final String FOOTBALL_PLAYER_REQUEST_TAG = "football_player_request_tag";
    private static final String CRICKET_TEAM_REQUEST_TAG = "cricket_team_request_tag";
    private static final String CRICKET_PLAYER_REQUEST_TAG = "cricket_player_request_tag";
    private ArrayList<ListPreparedListener> listPreparedListener;
    public int responseNum = 0;
    private ArrayList<Boolean> responseBool;
    private static final String URL_FOOTBALL_LEAGUE = "http://52.74.142.219:8000/get_football_leagues";
    private static final String URL_FOOTBALL_PLAYER = "http://52.74.142.219:8000/get_top_football_players";
    private static final String URL_FOOTBALL_TEAM = "http://52.74.142.219:8000/get_top_football_teams";
    private static final String URL_CRICKET_TEAM = "http://52.74.142.219:8080/top_cricket_teams";
    private static final String URL_CRICKET_PLAYER = "http://52.74.142.219:8080/top_cricket_players";

    public boolean isDisplay;
    private FavouriteContentHandler() {
        FOOTBALL_FILTER_LEAGUE = new ArrayList<String>();
        FOOTBALL_FILTER_PLAYER = new ArrayList<String>();
        FOOTBALL_FILTER_TEAM = new ArrayList<String>();
        CRICKET_FILTER_PLAYER = new ArrayList<String>();
        CRICKET_FILTER_TEAM = new ArrayList<String>();
        matches = new ArrayList<JSONObject>();
        listPreparedListener=new ArrayList<ListPreparedListener>();
        responseBool = new ArrayList<Boolean>();
        makeRequest();
    }

    public static FavouriteContentHandler getInstance() {
        if (favouriteContentHandler == null) {
            favouriteContentHandler = new FavouriteContentHandler();
        }

        return favouriteContentHandler;
    }

    /**
     * Request network API to get sports details
     */
    public void requestFootballLeagues() {
        //FOOTBALL_FILTER_LEAGUE=initDataSet("League".concat(Constants.NAV_COMP));
        ScoresContentHandler.getInstance().requestFavouriteContent(URL_FOOTBALL_LEAGUE, LISTENER_KEY, FOOTBALL_LEAGUE_REQUEST_TAG);

    }

    public void requestFootballPlayers() {
        ScoresContentHandler.getInstance().requestFavouriteContent(URL_FOOTBALL_PLAYER, LISTENER_KEY, FOOTBALL_PLAYER_REQUEST_TAG);
    }

    public void requestFootballTeams() {
        ScoresContentHandler.getInstance().requestFavouriteContent(URL_FOOTBALL_TEAM, LISTENER_KEY, FOOTBALL_TEAM_REQUEST_TAG);
    }

    public void requestCricketTeams() {
        ScoresContentHandler.getInstance().requestFavouriteContent(URL_CRICKET_TEAM, LISTENER_KEY, CRICKET_TEAM_REQUEST_TAG);
    }

    public void requestCricketPlayers() {
        ScoresContentHandler.getInstance().requestFavouriteContent(URL_CRICKET_PLAYER, LISTENER_KEY, CRICKET_PLAYER_REQUEST_TAG);
    }


    /**
     * Handle response from API and setup the corresponding arraylist
     * by comparing them with saved arraylist in shared preference.
     */
    private void prepareFootballLeagues() {
        //TODO
        /**
         * handle the response and setup
         * the FOOTBALL_FILTER_LEAGUE first*/

        favFootballLeagues = new ArrayList<FavouriteItem>();
        favFootballLeagues = prepareArrayList(FOOTBALL_FILTER_LEAGUE, UserUtil.getFavouriteFilters());

    }

    private void prepareFootballPlayers() {
        //TODO
        /**
         * handle the response and setup
         * the FOOTBALL_FILTER_PLAYER first*/

        favFootballPlayers = new ArrayList<FavouriteItem>();
        favFootballPlayers = prepareArrayList(FOOTBALL_FILTER_PLAYER, UserUtil.getFavouriteFilters());

    }

    private void prepareFootballTeams() {
        //TODO
        /**
         * handle the response and setup
         * the FOOTBALL_FILTER_TEAM first*/

        favFootballTeams = new ArrayList<FavouriteItem>();
        favFootballTeams = prepareArrayList(FOOTBALL_FILTER_TEAM, UserUtil.getFavouriteFilters());


    }


    private void prepareCricketPlayers() {
        //TODO
        /**
         * handle the response and setup
         * the CRICKET_FILTER_PLAYER first*/

        favCricketPlayers = new ArrayList<FavouriteItem>();
        favCricketPlayers = prepareArrayList(CRICKET_FILTER_PLAYER, UserUtil.getFavouriteFilters());
    }

    private void prepareCricketTeams() {
        //TODO
        /**
         * handle the response and setup
         * the CRICKET_FILTER_TEAM first*/

        favCricketTeams = new ArrayList<FavouriteItem>();
        favCricketTeams = prepareArrayList(CRICKET_FILTER_TEAM, UserUtil.getFavouriteFilters());
    }


    public ArrayList<FavouriteItem> getFavFootballLeagues() {
        return favFootballLeagues;
    }

    public ArrayList<FavouriteItem> getFavFootballTeams() {
        return favFootballTeams;
    }

    public ArrayList<FavouriteItem> getFavFootballPlayers() {
        return favFootballPlayers;
    }

    public ArrayList<FavouriteItem> getFavCricketTeams() {
        return favCricketTeams;
    }

    public ArrayList<FavouriteItem> getFavCricketPlayers() {
        return favCricketPlayers;
    }

    private ArrayList<FavouriteItem> prepareArrayList(ArrayList<String> networkList, ArrayList<String> localList) {
        ArrayList<FavouriteItem> favList = new ArrayList<FavouriteItem>();
        for (String s : networkList) {
            FavouriteItem favouriteItem = new FavouriteItem();
            favouriteItem.setName(s);
            if (localList.contains(s)) {
                favouriteItem.setChecked(true);
            }
            favList.add(favouriteItem);
        }
        return favList;
    }

    private ArrayList<String> initDataSet(String s) {
        ArrayList<String> itemDataSet = new ArrayList<String>();
        for (int i = 0; i < 11; i++) {
            itemDataSet.add(s + (i + 1));
        }
        return itemDataSet;
    }

    private void makeRequest() {
        requestFootballLeagues();
        requestFootballPlayers();
        requestFootballTeams();
        requestCricketPlayers();
        requestCricketTeams();
    }

    private void addResponseListener() {
        ScoresContentHandler.getInstance().addResponseListener(contentListener, LISTENER_KEY);
    }

    private void removeResponseListener() {
        ScoresContentHandler.getInstance().removeResponseListener(LISTENER_KEY);
    }

    private boolean handleContent(String content, String Tag) {
        boolean success = false;
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        list = FavouriteJsonParser.parseFavouriteList(content);
        if (list.size() > 0) {
            matches=new ArrayList<JSONObject>(list);
            /*matches.addAll(list);*/
            success = true;
        } else {
            //nothing
        }
        return success;
    }

    public void addPrepareListener(ListPreparedListener listPreparedListener) {
        this.listPreparedListener.add(listPreparedListener);
    }

    public void onResume() {
        addResponseListener();
    }

    public void onPause() {
        removeResponseListener();
    }

    private class ScoresContentListener implements ScoresContentHandler.ContentListener {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            responseNum++;
            boolean success = false;
            if (responseCode == 200) {
                success = FavouriteContentHandler.this.handleContent(content, tag);
                responseBool.add(success);
                if (success) {
                    if (tag.equals(FOOTBALL_LEAGUE_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                String s = obj.getString("league_name");
                                s = s.concat(Constants.NAV_COMP);
                                FOOTBALL_FILTER_LEAGUE.add(s);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        prepareFootballLeagues();
                    } else if (tag.equals(FOOTBALL_TEAM_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                String s = obj.getString("team_name");
                                s = s.concat(Constants.NAV_TEAM);
                                FOOTBALL_FILTER_TEAM.add(s);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        prepareFootballTeams();
                    } else if (tag.equals(FOOTBALL_PLAYER_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                String s = obj.getString("player_name");
                                FOOTBALL_FILTER_PLAYER.add(s);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        prepareFootballPlayers();
                    } else if (tag.equals(CRICKET_TEAM_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                String s = obj.getString("team");
                                s = s.concat(Constants.NAV_TEAM);
                                CRICKET_FILTER_TEAM.add(s);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        prepareCricketTeams();
                    } else if (tag.equals(CRICKET_PLAYER_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                String s = obj.getString("player_name");
                                CRICKET_FILTER_PLAYER.add(s);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        prepareCricketPlayers();
                    } else {
                        //nothing
                    }
                } else {
                }
            } else {
            }
            if(responseNum==5){
                boolean x=true;
                for(boolean b:responseBool){
                    if(!b){
                        x=false;
                    }
                }
                for(ListPreparedListener l:listPreparedListener){
                    isDisplay=x;
                    l.onListPrepared(x);
                }
            }
        }
    }

    public interface ListPreparedListener {
        public void onListPrepared(Boolean b);
    }

}
