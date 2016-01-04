package com.sports.unity.common.model;

import java.util.ArrayList;

/**
 * Created by Mad on 12/29/2015.
 */
public class FavouriteContentHandler {
    //public static FavouriteContentHandler favouriteContentHandler;

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

    public FavouriteContentHandler() {
        FOOTBALL_FILTER_LEAGUE = new ArrayList<String>();
        FOOTBALL_FILTER_PLAYER = new ArrayList<String>();
        FOOTBALL_FILTER_TEAM = new ArrayList<String>();
        CRICKET_FILTER_PLAYER = new ArrayList<String>();
        CRICKET_FILTER_TEAM = new ArrayList<String>();
        makeRequest();
    }

    /*public static FavouriteContentHandler getInstance() {
        favouriteContentHandler = new FavouriteContentHandler();
        return favouriteContentHandler;
    }*/

    /**
     * Request network API to get sports details
     */
    private void requestFootballLeagues() {
        FOOTBALL_FILTER_LEAGUE = initDataSet("FOOTBALL LEAGUE");
    }

    private void requestFootballPlayers() {

        FOOTBALL_FILTER_PLAYER = initDataSet("FOOTBALL Players");
    }

    private void requestFootballTeams() {
        FOOTBALL_FILTER_TEAM = initDataSet("Football Team");
    }

    private void requestCricketTeams() {
        CRICKET_FILTER_TEAM = initDataSet("Cricket Team");
    }

    private void requestCricketPlayers() {

        CRICKET_FILTER_PLAYER = initDataSet("Cricket Player");
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
        favCricketPlayers=prepareArrayList(CRICKET_FILTER_PLAYER, UserUtil.getFavouriteFilters());
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
        /*for (int i = 0; i < networkList.size(); i++) {
            FavouriteItem favouriteItem = new FavouriteItem();
            favouriteItem.setName(networkList.get(i));
            if (localList.contains(networkList.get(i))) {
                favouriteItem.setChecked(true);
            }
            favList.add(favouriteItem);

        }*/
        for(String s:networkList){
            FavouriteItem favouriteItem = new FavouriteItem();
            favouriteItem.setName(s);
            if(localList.contains(s)){
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
        prepareList();
    }

    private void prepareList() {
        prepareFootballLeagues();
        prepareFootballTeams();
        prepareFootballPlayers();
        prepareCricketTeams();
        prepareCricketPlayers();
    }
}
