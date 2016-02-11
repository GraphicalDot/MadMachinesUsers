package com.sports.unity.common.model;


import android.content.Context;

import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mad on 12/29/2015.
 */

/**
 * Helper class to handle the favourite filter selection.
 */
public class FavouriteContentHandler {
    public static FavouriteContentHandler favouriteContentHandler;

    private static List<String> FOOTBALL_FILTER_LEAGUE = null;
    private static List<String> FOOTBALL_FILTER_PLAYER = null;
    private static List<String> FOOTBALL_FILTER_TEAM = null;
    private static List<String> CRICKET_FILTER_PLAYER = null;
    private static List<String> CRICKET_FILTER_TEAM = null;

    private static List<String> SEARCH_FOOTBALL_LEAGUE = null;
    private static List<String> SEARCH_CRICKET_TEAM = null;
    private static List<String> SEARCH_CRICKET_PLAYER = null;
    private static List<String> SEARCH_FOOTBALL_TEAM = null;
    private static List<String> SEARCH_FOOTBALL_PLAYER = null;


    private List<FavouriteItem> favFootballLeagues;
    private List<FavouriteItem> favFootballTeams;
    private List<FavouriteItem> favFootballPlayers;

    private List<FavouriteItem> favCricketTeams;
    private List<FavouriteItem> favCricketPlayers;


    private List<FavouriteItem> favSearchFootballLeague;
    private List<FavouriteItem> favSearchCricketTeam;
    private List<FavouriteItem> favSearchCricketPlayer;
    private List<FavouriteItem> favSearchFootballTeam;
    private List<FavouriteItem> favSearchFootballPlayer;
    private ArrayList<FavouriteItem> savedFavList;

    private ArrayList<JSONObject> matches = new ArrayList<>();
    private ScoresContentListener contentListener = new ScoresContentListener();

    private static final String LISTENER_KEY = "favourite_listener";
    private static final String FOOTBALL_LEAGUE_REQUEST_TAG = "football_league_request_tag";
    private static final String FOOTBALL_TEAM_REQUEST_TAG = "football_team_request_tag";
    private static final String FOOTBALL_PLAYER_REQUEST_TAG = "football_player_request_tag";
    private static final String CRICKET_TEAM_REQUEST_TAG = "cricket_team_request_tag";
    private static final String CRICKET_PLAYER_REQUEST_TAG = "cricket_player_request_tag";

    private static final String SEARCH_REQUEST_TAG = "fav_search_request_";
    private static final String SEARCH_REQUEST_LEAGUE_TAG = "fav_search_request_league_tag";
    private static final String SEARCH_CRICKET_TEAM_TAG = "fav_search_request_cricket_team_tag";
    private static final String SEARCH_CRICKET_PLAYER_TAG = "fav_search_request_cricket_player_tag";

    private static final String SEARCH_FOOTBALL_TEAM_TAG = "fav_search_request_football_team_tag";
    private static final String SEARCH_FOOTBALL_PLAYER_TAG = "fav_search_request_football_player_tag";


    private static final String URL_FOOTBALL_LEAGUE = "http://52.74.75.79:8000/get_football_leagues";
    private static final String URL_FOOTBALL_PLAYER = "http://52.74.75.79:8000/get_top_football_players";
    private static final String URL_FOOTBALL_TEAM = "http://52.74.75.79:8000/get_top_football_teams";
    private static final String URL_CRICKET_TEAM = "http://52.74.75.79:8080/top_cricket_teams";
    private static final String URL_CRICKET_PLAYER = "http://52.74.75.79:8080/top_cricket_players";


    private static final String URL_FOOTBALL_LEAGUE_SEARCH = "http://52.76.74.188:9000/fav_league?league=";

    private static final String URL_CRICKET_TEAM_SEARCH = "http://52.76.74.188:9000/fav_team?sport_type=cricket&team=";
    private static final String URL_CRICKET_PLAYER_SEARCH = "http://52.76.74.188:9000/fav_player?sport_type=cricket&player=";
    private static final String URL_FOOTBALL_TEAM_SEARCH = "http://52.76.74.188:9000/fav_team?sport_type=football&team=";
    private static final String URL_FOOTBALL_PLAYER_SEARCH = "http://52.76.74.188:9000/fav_player?sport_type=football&player=";


    private final String errorMessage = "Something went wrong";
    private final String noResultMessage = "No result found";

    private ArrayList<ListPreparedListener> listPreparedListener;
    public int responseNum = 0;
    private ArrayList<Boolean> responseBool;
    public boolean isDisplay;

    public int searchNum = 0;

    private static Context context;
    private int responseSearchnum = 0;
    private ArrayList<Boolean> responseSearchBool = new ArrayList<Boolean>();

    /**
     * constructor
     */
    private FavouriteContentHandler() {
        FOOTBALL_FILTER_LEAGUE = new ArrayList<String>();
        FOOTBALL_FILTER_PLAYER = new ArrayList<String>();
        FOOTBALL_FILTER_TEAM = new ArrayList<String>();
        CRICKET_FILTER_PLAYER = new ArrayList<String>();
        CRICKET_FILTER_TEAM = new ArrayList<String>();
        SEARCH_FOOTBALL_LEAGUE = new ArrayList<String>();
        SEARCH_FOOTBALL_PLAYER = new ArrayList<String>();
        SEARCH_FOOTBALL_TEAM = new ArrayList<String>();
        SEARCH_CRICKET_PLAYER = new ArrayList<String>();
        SEARCH_CRICKET_TEAM = new ArrayList<String>();

        matches = new ArrayList<JSONObject>();
        listPreparedListener = new ArrayList<ListPreparedListener>();
        responseBool = new ArrayList<Boolean>();
        makeRequest();
    }


    /**
     * method to instantiate the {@link FavouriteContentHandler} class.
     *
     * @return single instance of {@link FavouriteContentHandler}
     */
    public static FavouriteContentHandler getInstance(Context context) {
        if (favouriteContentHandler == null) {
            favouriteContentHandler = new FavouriteContentHandler();
        }
        FavouriteContentHandler.context = context;
        return favouriteContentHandler;
    }

    /**
     * resets the {@link ListPreparedListener} attached to the FavouriteContentHandler class.
     */
    public void resetListener() {
        listPreparedListener = new ArrayList<>();
    }

    /**
     * Request network API for top football leagues.
     */
    public void requestFootballLeagues() {
        //FOOTBALL_FILTER_LEAGUE=initDataSet("League".concat(Constants.NAV_COMP));
        ScoresContentHandler.getInstance().requestFavouriteContent(URL_FOOTBALL_LEAGUE, LISTENER_KEY, FOOTBALL_LEAGUE_REQUEST_TAG);

    }

    /**
     * Request network API for top football players.
     */
    public void requestFootballPlayers() {
        ScoresContentHandler.getInstance().requestFavouriteContent(URL_FOOTBALL_PLAYER, LISTENER_KEY, FOOTBALL_PLAYER_REQUEST_TAG);
    }

    /**
     * Request network API for top football teams.
     */
    public void requestFootballTeams() {
        ScoresContentHandler.getInstance().requestFavouriteContent(URL_FOOTBALL_TEAM, LISTENER_KEY, FOOTBALL_TEAM_REQUEST_TAG);
    }

    /**
     * Request network API for top cricket teams.
     */
    public void requestCricketTeams() {
        ScoresContentHandler.getInstance().requestFavouriteContent(URL_CRICKET_TEAM, LISTENER_KEY, CRICKET_TEAM_REQUEST_TAG);
    }

    /**
     * Request network API for top cricket players.
     */
    public void requestCricketPlayers() {
        ScoresContentHandler.getInstance().requestFavouriteContent(URL_CRICKET_PLAYER, LISTENER_KEY, CRICKET_PLAYER_REQUEST_TAG);
    }

    /**
     * Request network API for search
     *
     * @param searchType Type of search e.g. League ,Team or Player.
     * @param sportsType Type of sport e.g. Cricket or Football.
     * @param param      String to be searched.
     */
    public void requestFavSearch(String searchType, String sportsType, String param) {
        searchNum++;
        clearSearchList();
        if (searchType.equals(Constants.FILTER_TYPE_LEAGUE)) {
            ScoresContentHandler.getInstance().requestFavouriteSearch(URL_FOOTBALL_LEAGUE_SEARCH, param, LISTENER_KEY, SEARCH_REQUEST_LEAGUE_TAG);
        } else if (searchType.equals(Constants.FILTER_TYPE_PLAYER)) {
            if (sportsType.equals(Constants.SPORTS_TYPE_CRICKET)) {
                ScoresContentHandler.getInstance().requestFavouriteSearch(URL_CRICKET_PLAYER_SEARCH, param, LISTENER_KEY, SEARCH_CRICKET_PLAYER_TAG);
            } else if (sportsType.equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                ScoresContentHandler.getInstance().requestFavouriteSearch(URL_FOOTBALL_PLAYER_SEARCH, param, LISTENER_KEY, SEARCH_FOOTBALL_PLAYER_TAG);
            }
        } else if (searchType.equals(Constants.FILTER_TYPE_TEAM)) {
            if (sportsType.equals(Constants.SPORTS_TYPE_CRICKET)) {
                ScoresContentHandler.getInstance().requestFavouriteSearch(URL_CRICKET_TEAM_SEARCH, param, LISTENER_KEY, SEARCH_CRICKET_TEAM_TAG);
            } else if (sportsType.equals(Constants.SPORTS_TYPE_FOOTBALL)) {
                ScoresContentHandler.getInstance().requestFavouriteSearch(URL_FOOTBALL_TEAM_SEARCH, param, LISTENER_KEY, SEARCH_FOOTBALL_TEAM_TAG);
            }
        }
    }

    /**
     * Setup the football league's ArrayList
     * by comparing them with saved ArrayList in shared preference.
     */
    private void prepareFootballLeagues(FavouriteItem item) {

        favFootballLeagues.add(item);

    }

    /**
     * Setup the football player's ArrayList
     * by comparing them with saved ArrayList in shared preference.
     */
    private void prepareFootballPlayers(FavouriteItem item) {
        favFootballPlayers.add(item);
    }

    /**
     * Setup the football team's ArrayList
     * by comparing them with saved ArrayList in shared preference.
     */
    private void prepareFootballTeams(FavouriteItem item) {

        favFootballTeams.add(item);


    }

    /**
     * Setup the cricket player's ArrayList
     * by comparing them with saved ArrayList in shared preference.
     */
    private void prepareCricketPlayers(FavouriteItem item) {

        favCricketPlayers.add(item);
    }

    /**
     * Setup the cricket team's ArrayList
     * by comparing them with saved ArrayList in shared preference.
     */
    private void prepareCricketTeams(FavouriteItem item) {
        favCricketTeams.add(item);
    }

    private void prepareSearchFootballLeague(FavouriteItem item) {
        favSearchFootballLeague.add(item);
    }

    private void prepareSearchFootballTeam(FavouriteItem item) {
        favSearchFootballTeam.add(item);
    }

    private void prepareSearchFootballPlayer(FavouriteItem item) {
        favSearchFootballPlayer.add(item);
    }

    private void prepareSearchCricketTeam(FavouriteItem item) {
        favSearchCricketTeam.add(item);
    }

    private void prepareSearchCricketPlayer(FavouriteItem item) {
        favSearchCricketPlayer.add(item);
    }

    /**
     * prepare the corresponding ArrayList of search result.
     *
     * @param searchTag tag to identify the corresponding ArrayList.
     * @param success   whether search query was success.
     */

    /*private void prepareSearchList(String searchTag, Boolean success) {

        if (searchTag.equals(SEARCH_REQUEST_LEAGUE_TAG)) {
            favSearchFootballLeague = new ArrayList<FavouriteItem>();
            favSearchFootballLeague = prepareArrayList(SEARCH_FOOTBALL_LEAGUE, UserUtil.getFavouriteFilters());
        } else if (searchTag.equals(SEARCH_CRICKET_PLAYER_TAG)) {
            favSearchCricketPlayer = new ArrayList<FavouriteItem>();
            favSearchCricketPlayer = prepareArrayList(SEARCH_CRICKET_PLAYER, UserUtil.getFavouriteFilters());
        } else if (searchTag.equals(SEARCH_CRICKET_TEAM_TAG)) {
            favSearchCricketTeam = new ArrayList<FavouriteItem>();
            favSearchCricketTeam = prepareArrayList(SEARCH_CRICKET_TEAM, UserUtil.getFavouriteFilters());
        } else if (searchTag.equals(SEARCH_FOOTBALL_PLAYER_TAG)) {
            favSearchFootballPlayer = new ArrayList<FavouriteItem>();
            favSearchFootballPlayer = prepareArrayList(SEARCH_FOOTBALL_PLAYER, UserUtil.getFavouriteFilters());
        } else if (searchTag.equals(SEARCH_FOOTBALL_TEAM_TAG)) {
            favSearchFootballTeam = new ArrayList<FavouriteItem>();
            favSearchFootballTeam = prepareArrayList(SEARCH_FOOTBALL_TEAM, UserUtil.getFavouriteFilters());
        }
    }*/

    /**
     * resets all the ArrayLists related to search.
     */
    private void clearSearchList() {
        SEARCH_FOOTBALL_LEAGUE = new ArrayList<String>();
        SEARCH_FOOTBALL_PLAYER = new ArrayList<String>();
        SEARCH_FOOTBALL_TEAM = new ArrayList<String>();
        SEARCH_CRICKET_PLAYER = new ArrayList<String>();
        SEARCH_CRICKET_TEAM = new ArrayList<String>();
        favSearchFootballLeague = new ArrayList<FavouriteItem>();
        favSearchCricketPlayer = new ArrayList<FavouriteItem>();
        favSearchCricketTeam = new ArrayList<FavouriteItem>();
        favSearchFootballPlayer = new ArrayList<FavouriteItem>();
        favSearchFootballTeam = new ArrayList<FavouriteItem>();


    }

    /**
     * @return ArrayList of top football leagues.
     */
    public ArrayList<FavouriteItem> getFavFootballLeagues() {
        Collections.sort(favFootballLeagues);
        return new ArrayList<>(favFootballLeagues);
    }

    /**
     * @return ArrayList of top football teams.
     */
    public ArrayList<FavouriteItem> getFavFootballTeams() {
        Collections.sort(favFootballTeams);
        return new ArrayList<>(favFootballTeams);
    }

    /**
     * @return ArrayList of top football players.
     */
    public ArrayList<FavouriteItem> getFavFootballPlayers() {
        Collections.sort(favFootballPlayers);
        return new ArrayList<>(favFootballPlayers);
    }

    /**
     * @return ArrayList of top cricket teams.
     */
    public ArrayList<FavouriteItem> getFavCricketTeams() {
        Collections.sort(favCricketTeams);
        return new ArrayList<>(favCricketTeams);
    }

    /**
     * @return ArrayList of top cricket players.
     */
    public ArrayList<FavouriteItem> getFavCricketPlayers() {
        Collections.sort(favCricketPlayers);
        return new ArrayList<>(favCricketPlayers);
    }

    /**
     * @return ArrayList of search results related to football league.
     */
    public ArrayList<FavouriteItem> getSearchedFootballLeague() {
        Collections.sort(favSearchFootballLeague);
        return new ArrayList<>(favSearchFootballLeague);
    }

    /**
     * @return List of search results related to football league.
     */
    public List<String> getSearchedFootballLeagueStringList() {

        return SEARCH_FOOTBALL_LEAGUE;
    }

    /**
     * @return ArrayList of search results related to cricket player.
     */
    public ArrayList<FavouriteItem> getSearchedCricketPlayer() {
        Collections.sort(favSearchCricketPlayer);
        return new ArrayList<>(favSearchCricketPlayer);
    }

    /**
     * @return List of search results related to cricket player.
     */
    public List<String> getSearchedCricketPlayerStringList() {
        return SEARCH_CRICKET_PLAYER;
    }

    /**
     * @return ArrayList of search results related to cricket team.
     */
    public ArrayList<FavouriteItem> getSearchedCricketTeam() {
        Collections.sort(favSearchCricketTeam);
        return new ArrayList<>(favSearchCricketTeam);
    }

    /**
     * @return List of search results related to cricket team.
     */
    public List<String> getSearchedCricketTeamStringList() {
        return SEARCH_CRICKET_TEAM;
    }

    /**
     * @return ArrayList of search results related to football player.
     */
    public ArrayList<FavouriteItem> getSearchedFootballPlayer() {
        Collections.sort(favSearchFootballPlayer);
        return new ArrayList<>(favSearchFootballPlayer);
    }

    /**
     * @return List of search results related to football player.
     */
    public List<String> getSearchedFootballPlayerStringList() {
        return SEARCH_FOOTBALL_PLAYER;
    }

    /**
     * @return ArrayList of search results related to football team.
     */

    public ArrayList<FavouriteItem> getSearchedFootballTeam() {
        Collections.sort(favSearchFootballTeam);
        return new ArrayList<>(favSearchFootballTeam);
    }

    /**
     * @return List of search results related to football team.
     */
    public List<String> getSearchedFootballTeamStringList() {
        return SEARCH_FOOTBALL_TEAM;
    }

    /**
     * prepares the corresponding ArrayList of favourites by
     * comparing them with the saved list.
     *
     * @param networkList ArrayList of favourites from API.
     * @param localList   saved ArrayList of favourites.
     * @return
     */
    private ArrayList<FavouriteItem> prepareArrayList(List<String> networkList, ArrayList<String> localList) {
        Collections.sort(networkList);
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

    /**
     * If after selecting the favourite if user presses back
     * or skip button this method invalidate all the favourites lists.
     *
     * @param context COntext of origin activity
     */
    public void invalidate(Context context) {
        if (UserUtil.isFilterCompleted()) {
            ArrayList<FavouriteItem> favList = FavouriteItemWrapper.getInstance().getFavList(context);
            for (FavouriteItem f : favCricketTeams) {
                if (favList.contains(f)) {
                    f.setChecked(true);
                } else {
                    f.setChecked(false);
                }
            }
            for (FavouriteItem f : favCricketPlayers) {
                if (favList.contains(f)) {
                    f.setChecked(true);
                } else {
                    f.setChecked(false);
                }
            }
            for (FavouriteItem f : favFootballTeams) {
                if (favList.contains(f)) {
                    f.setChecked(true);
                } else {
                    f.setChecked(false);
                }
            }
            for (FavouriteItem f : favFootballPlayers) {
                if (favList.contains(f)) {
                    f.setChecked(true);
                } else {
                    f.setChecked(false);
                }
            }
            for (FavouriteItem f : favFootballLeagues) {
                if (favList.contains(f)) {
                    f.setChecked(true);
                } else {
                    f.setChecked(false);
                }
            }
        } else {
            for (FavouriteItem f : favCricketTeams) {
                f.setChecked(false);
            }
            for (FavouriteItem f : favCricketPlayers) {
                f.setChecked(false);
            }
            for (FavouriteItem f : favFootballTeams) {
                f.setChecked(false);
            }
            for (FavouriteItem f : favFootballPlayers) {
                f.setChecked(false);
            }
            for (FavouriteItem f : favFootballLeagues) {
                f.setChecked(false);
            }
        }
    }

    /**
     * Make request to network API for favourites.
     */
    public void makeRequest() {
        savedFavList = FavouriteItemWrapper.getInstance().getFavList(context);
        favFootballLeagues = new ArrayList<FavouriteItem>();
        favFootballTeams = new ArrayList<FavouriteItem>();
        favFootballPlayers = new ArrayList<FavouriteItem>();
        favCricketTeams = new ArrayList<FavouriteItem>();
        favCricketPlayers = new ArrayList<FavouriteItem>();
        FOOTBALL_FILTER_LEAGUE = new ArrayList<String>();
        FOOTBALL_FILTER_PLAYER = new ArrayList<String>();
        FOOTBALL_FILTER_TEAM = new ArrayList<String>();
        CRICKET_FILTER_PLAYER = new ArrayList<String>();
        CRICKET_FILTER_TEAM = new ArrayList<String>();
        requestFootballLeagues();
        requestFootballPlayers();
        requestFootballTeams();
        requestCricketPlayers();
        requestCricketTeams();
    }

    /**
     * Add response listener to handle the response from network.
     */
    private void addResponseListener() {
        ScoresContentHandler.getInstance().addResponseListener(contentListener, LISTENER_KEY);
    }

    /**
     * remove network response listener .
     */
    private void removeResponseListener() {
        ScoresContentHandler.getInstance().removeResponseListener(LISTENER_KEY);
    }

    /**
     * handle the content from response.
     *
     * @param content content from response.
     * @param Tag     corresponding request tag.
     * @return
     */
    private boolean handleContent(String content, String Tag) {
        boolean success = false;
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        list = FavouriteJsonParser.parseFavouriteList(content);
        if (list.size() > 0) {
            matches = new ArrayList<JSONObject>(list);
            /*matches.addAll(list);*/
            success = true;
        } else {
            //nothing
        }
        return success;
    }

    /**
     * add the list prepared listener.
     *
     * @param listPreparedListener listener to be added.
     */
    public void addPreparedListener(ListPreparedListener listPreparedListener) {
        this.listPreparedListener.add(listPreparedListener);
    }

    /**
     * remove the list prepared listener.
     *
     * @param l listener to be removed.
     */
    public void removePreparedListener(ListPreparedListener l) {
        this.listPreparedListener.remove(l);
    }

    /**
     * handles the activity resume callbacks.
     */
    public void onResume() {
        addResponseListener();
    }

    /**
     * handles the activity pause callbacks.
     */
    public void onPause() {
        removeResponseListener();
    }

    /**
     * Response handler interface which handles the response from favourites and search request.
     */
    private class ScoresContentListener implements ScoresContentHandler.ContentListener {

        @Override
        public void handleContent(String searchTag, String content, int responseCode) {
            if (!searchTag.contains(SEARCH_REQUEST_TAG)) {
                responseNum++;
            } else {
                responseSearchnum++;
            }
            boolean success = false;
            if (responseCode == 200) {
                success = FavouriteContentHandler.this.handleContent(content, searchTag);


                if (!searchTag.contains(SEARCH_REQUEST_TAG)) {
                    responseBool.add(success);
                } else {
                    responseSearchBool.add(success);
                }
                if (success) {
                    if (searchTag.equals(FOOTBALL_LEAGUE_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                FavouriteItem item = new FavouriteItem();
                                String s = obj.getString("league_name");
                                s = s.concat(", " + obj.getString("region"));
                                FOOTBALL_FILTER_LEAGUE.add(s);
                                item.setName(s);
                                s = obj.getString("league_id");
                                item.setId(s);
                                item.setSportsType(Constants.SPORTS_TYPE_FOOTBALL);
                                item.setFilterType(Constants.FILTER_TYPE_LEAGUE);
                                if (savedFavList.contains(item)) {
                                    item.setChecked(true);
                                }
                                prepareFootballLeagues(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (searchTag.equals(FOOTBALL_TEAM_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                FavouriteItem item = new FavouriteItem();
                                String s = obj.getString("team_name");
                                item.setName(s);
                                FOOTBALL_FILTER_TEAM.add(s);
                                s = obj.getString("team_id");
                                item.setId(s);
                                s = obj.getString("flag_image");
                                item.setFlagImageUrl(s);
                                item.setSportsType(Constants.SPORTS_TYPE_FOOTBALL);
                                item.setFilterType(Constants.FILTER_TYPE_TEAM);
                                if (savedFavList.contains(item)) {
                                    item.setChecked(true);
                                }
                                prepareFootballTeams(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (searchTag.equals(FOOTBALL_PLAYER_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                FavouriteItem item = new FavouriteItem();
                                String s = obj.getString("player_name");
                                item.setName(s);
                                item.setSportsType(Constants.SPORTS_TYPE_FOOTBALL);
                                item.setFilterType(Constants.FILTER_TYPE_PLAYER);
                                if (savedFavList.contains(item)) {
                                    item.setChecked(true);
                                }
                                prepareFootballPlayers(item);
                                FOOTBALL_FILTER_PLAYER.add(s);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (searchTag.equals(CRICKET_TEAM_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                FavouriteItem item = new FavouriteItem();
                                String s = obj.getString("team");
                                item.setName(s);
                                s = obj.getString("flag_image");
                                item.setFlagImageUrl(s);
                                item.setSportsType(Constants.SPORTS_TYPE_CRICKET);
                                item.setFilterType(Constants.FILTER_TYPE_TEAM);
                                if (savedFavList.contains(item)) {
                                    item.setChecked(true);
                                }
                                prepareCricketTeams(item);
                                CRICKET_FILTER_TEAM.add(s);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (searchTag.equals(CRICKET_PLAYER_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                FavouriteItem item = new FavouriteItem();
                                String s = obj.getString("name");
                                item.setName(s);
                                item.setSportsType(Constants.SPORTS_TYPE_CRICKET);
                                item.setFilterType(Constants.FILTER_TYPE_PLAYER);
                                if (savedFavList.contains(item)) {
                                    item.setChecked(true);
                                }
                                prepareCricketPlayers(item);
                                CRICKET_FILTER_PLAYER.add(s);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (searchTag.contains(SEARCH_REQUEST_TAG)) {
                        //TODO
                        for (JSONObject obj : matches) {
                            try {
                                String s = null;
                                if (searchTag.equals(SEARCH_REQUEST_LEAGUE_TAG)) {
                                    FavouriteItem item = new FavouriteItem();
                                    s = obj.getString("league_name");
                                    s = s.concat(", " + obj.getString("region"));
                                    item.setName(s);
                                    if (!SEARCH_FOOTBALL_LEAGUE.contains(s)) {
                                        SEARCH_FOOTBALL_LEAGUE.add(s);
                                    }
                                    s = obj.getString("league_id");
                                    item.setId(s);
                                    item.setSportsType(Constants.SPORTS_TYPE_FOOTBALL);
                                    item.setFilterType(Constants.FILTER_TYPE_LEAGUE);
                                    if (savedFavList.contains(item)) {
                                        item.setChecked(true);
                                    }
                                    if (!favSearchFootballLeague.contains(item)) {
                                        prepareSearchFootballLeague(item);
                                    }
                                } else if (searchTag.equals(SEARCH_CRICKET_PLAYER_TAG)) {
                                    FavouriteItem item = new FavouriteItem();
                                    s = obj.getString("name");
                                    item.setName(s);
                                    if (!SEARCH_CRICKET_PLAYER.contains(s)) {
                                        SEARCH_CRICKET_PLAYER.add(s);
                                    }
                                    s = obj.getString("player_id");
                                    item.setId(s);
                                    item.setSportsType(Constants.SPORTS_TYPE_CRICKET);
                                    item.setFilterType(Constants.FILTER_TYPE_PLAYER);
                                    if (savedFavList.contains(item)) {
                                        item.setChecked(true);
                                    }
                                    if (!favSearchCricketPlayer.contains(item)) {
                                        prepareSearchCricketPlayer(item);
                                    }
                                } else if (searchTag.equals(SEARCH_CRICKET_TEAM_TAG)) {
                                    FavouriteItem item = new FavouriteItem();
                                    s = obj.getString("team_name");
                                    item.setName(s);
                                    if (!SEARCH_CRICKET_TEAM.contains(s)) {
                                        SEARCH_CRICKET_TEAM.add(s);
                                    }
                                    s = obj.getString("team_id");
                                    item.setId(s);
                                    s = obj.getString("flag_image");
                                    item.setFlagImageUrl(s);
                                    item.setSportsType(Constants.SPORTS_TYPE_CRICKET);
                                    item.setFilterType(Constants.FILTER_TYPE_TEAM);
                                    if (savedFavList.contains(item)) {
                                        item.setChecked(true);
                                    }
                                    if (!favSearchCricketTeam.contains(item)) {
                                        prepareSearchCricketTeam(item);
                                    }
                                } else if (searchTag.equals(SEARCH_FOOTBALL_PLAYER_TAG)) {
                                    FavouriteItem item = new FavouriteItem();
                                    s = obj.getString("name");
                                    item.setName(s);
                                    if (!SEARCH_FOOTBALL_PLAYER.contains(s)) {
                                        SEARCH_FOOTBALL_PLAYER.add(s);
                                    }
                                    s = obj.getString("player_id");
                                    item.setId(s);
                                    item.setSportsType(Constants.SPORTS_TYPE_FOOTBALL);
                                    item.setFilterType(Constants.FILTER_TYPE_PLAYER);
                                    if (savedFavList.contains(item)) {
                                        item.setChecked(true);
                                    }
                                    if (!favSearchFootballPlayer.contains(item)) {
                                        prepareSearchFootballPlayer(item);
                                    }
                                } else if (searchTag.equals(SEARCH_FOOTBALL_TEAM_TAG)) {
                                    FavouriteItem item = new FavouriteItem();
                                    s = obj.getString("team_name");
                                    item.setName(s);
                                    if (!SEARCH_FOOTBALL_TEAM.contains(s)) {
                                        SEARCH_FOOTBALL_TEAM.add(s);
                                    }
                                    s = obj.getString("team_id");
                                    item.setId(s);
                                    s = obj.getString("flag_image");
                                    item.setFlagImageUrl(s);
                                    item.setSportsType(Constants.SPORTS_TYPE_FOOTBALL);
                                    item.setFilterType(Constants.FILTER_TYPE_TEAM);
                                    if (savedFavList.contains(item)) {
                                        item.setChecked(true);
                                    }
                                    if (!favSearchFootballTeam.contains(item)) {
                                        prepareSearchFootballTeam(item);
                                    }
                                }
                            } catch (JSONException e) {
                                for (ListPreparedListener l : listPreparedListener) {
                                    l.onListPrepared(false, noResultMessage);
                                }
                                e.printStackTrace();
                            }
                        }
                        matches = new ArrayList<JSONObject>();
                    } else {
                        //nothing
                    }
                } else {
                    /*for (ListPreparedListener l : listPreparedListener) {
                        Log.d("max","sending response");
                        l.onListPrepared(false, noResultMessage);
                    }*/
                }
            } else {
                if (!searchTag.contains(SEARCH_REQUEST_TAG)) {
                    for (ListPreparedListener l : listPreparedListener) {
                        l.onListPrepared(false, errorMessage);
                    }
                    return;
                }
            }

            if (!searchTag.contains(SEARCH_REQUEST_TAG)) {
                if (responseNum >= 5) {
                    boolean x = true;
                    for (boolean b : responseBool) {
                        if (!b) {
                            x = false;
                        }
                    }
                    for (ListPreparedListener l : listPreparedListener) {
                        isDisplay = x;
                        l.onListPrepared(x, "");
                    }
                    responseBool = new ArrayList<Boolean>();
                }
            } else {
                if (responseSearchnum == searchNum) {
                    try {
                        for (int i = 0; i < listPreparedListener.size(); i++) {
                            listPreparedListener.get(i).onListPrepared(true, noResultMessage);
                        }
                    } catch (Exception e) {
                        for (ListPreparedListener l : listPreparedListener) {
                            l.onListPrepared(false, errorMessage);
                        }
                    }
                    searchNum = 0;
                    responseSearchnum = 0;
                    responseSearchBool = new ArrayList<Boolean>();
                }
            }
        }

    }

    /**
     * Interface definition for a callback to be invoked when a favourite list is prepared.
     */
    public interface ListPreparedListener {
        /**
         * called when a favourite list is prepared.
         *
         * @param success      success result of list preparing.
         * @param responseCode response code from network.
         */
        public void onListPrepared(Boolean success, String responseCode);

    }

}
