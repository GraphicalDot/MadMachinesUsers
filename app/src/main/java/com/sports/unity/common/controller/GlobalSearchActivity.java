package com.sports.unity.common.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.GlobalContentItemObject;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.common.viewhelper.VolleyCallComponentHelper;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GlobalSearchActivity extends CustomVolleyCallerActivity {

    private static final String REQUEST_LISTENER_KEY = "GLOBAL_SEARCH_LISTENER_KEY";
    private static final String GLOBAL_SEARCH_REQUEST_TAG = "GLOBAL_SEARCH";
    private JSONObject searchResponse = null;
    private ProgressBar progressBar;
    private FrameLayout errorLayout = null;
    private RecyclerView globalSearchRecyclerView;
    private GlobalSearchListAdapter globalSearchListAdapter;
    private ArrayList<GlobalContentItemObject> content = new ArrayList<>();
    private String keyword = "";
    private static final String GLOBAL_SEARCH_TYPE = "&search_type=";
    public static final String TEAM_TYPE = "team";
    public static final String LEAGUE_TYPE = "league";
    public static final String PLAYER_TYPE = "player";
    public static final String NEWS_TYPE = "news";
    public static final String MATCH_TYPE = "match";
    public static final String ALL_TYPE = "all";
    boolean localDataAdded = false;
    public static final String KEYWORD = "keyword";

    private int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_favoriates);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        errorLayout = (FrameLayout) findViewById(R.id.error_layout);
        position = getIntent().getIntExtra(Constants.INTENT_KEY_GLOBAL_POSITION, 0);
        initRecyclerView();
        initToolbar();
    }

    private void initRecyclerView() {
        globalSearchRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        globalSearchRecyclerView.setLayoutManager(manager);
        globalSearchListAdapter = new GlobalSearchListAdapter(content, this);
        globalSearchRecyclerView.setAdapter(globalSearchListAdapter);
    }

    @Override
    public VolleyCallComponentHelper getVolleyCallComponentHelper() {
        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper(REQUEST_LISTENER_KEY, new GlobalSarchComponentListener(progressBar, errorLayout));
        return volleyCallComponentHelper;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_search);
        setSupportActionBar(toolbar);

        ImageView back = (ImageView) toolbar.findViewById(R.id.img_back);
        final EditText search = (EditText) toolbar.findViewById(R.id.search_view);
        final ImageView clear_search = (ImageView) toolbar.findViewById(R.id.search_clear);

        back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, true));
        clear_search.setVisibility(View.INVISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clear_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clear_search.setVisibility(View.VISIBLE);
                } else {
                    clear_search.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    hideKeyboard();
                    performSearch(v.getText().toString(), ALL_TYPE);

                    return true;
                }
                return false;
            }
        });


    }

    public void performSpecificSearch(String text, String type) {
        performSearch(text, type);
    }

    private void performSearch(String text, String type) {
        this.keyword = text;
        onComponentCreate();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Constants.GLOBAL_SEARCH_KEYWORD, text);
        parameters.put(Constants.GLOBAL_SEARCH_ENDPOINT, GLOBAL_SEARCH_TYPE);
        parameters.put(Constants.GLOBAL_SEARCH_TYPE, type);
        requestContent(ScoresContentHandler.CALL_NAME_GLOBAL_SEARCH, parameters, GLOBAL_SEARCH_REQUEST_TAG);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private class GlobalSarchComponentListener extends CustomComponentListener {

        public GlobalSarchComponentListener(ProgressBar progressBar, ViewGroup errorLayout) {
            super(GLOBAL_SEARCH_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public boolean handleContent(String tag, String content) {
            return GlobalSearchActivity.this.handleResponse(content);
        }

        @Override
        public void changeUI(String tag) {
            boolean success = GlobalSearchActivity.this.renderContent();
            if (!success) {
                showErrorLayout();
            } else {
                //TODO
            }
        }
    }

    private boolean renderContent() {
        boolean success = false;
        try {
            JSONObject dataObject = searchResponse.getJSONObject("data");
            JSONArray newsArray = dataObject.getJSONArray(NEWS_TYPE);
            JSONArray matchesArray = dataObject.getJSONArray(MATCH_TYPE);
            JSONArray teamsArray = dataObject.getJSONArray(TEAM_TYPE);
            JSONArray playersArray = dataObject.getJSONArray(PLAYER_TYPE);
            JSONArray leagueArray = dataObject.getJSONArray(LEAGUE_TYPE);

            HashMap<String, JSONArray> dataMap = new HashMap<>();
            dataMap.put(NEWS_TYPE, newsArray);
            dataMap.put(MATCH_TYPE, matchesArray);
            dataMap.put(TEAM_TYPE, teamsArray);
            dataMap.put(PLAYER_TYPE, playersArray);
            dataMap.put(LEAGUE_TYPE, leagueArray);

            content.clear();

            localDataAdded = false;
            
            if (position == 0) {

                if (matchesArray.length() > 0) {
                    for (int i = 0; i < matchesArray.length(); i++) {
                        if (i == 0) {
                            content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_HEADER, "Matches"));
                        }
                        content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_MATCH, matchesArray.getJSONObject(i)));
                    }
                }
                dataMap.remove(MATCH_TYPE);
            } else if (position == 1) {

                if (newsArray.length() > 0) {
                    for (int i = 0; i < newsArray.length(); i++) {
                        if (i == 0) {
                            content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_HEADER, "News"));
                        }
                        content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_NEWS, newsArray.getJSONObject(i)));
                    }
                }
                dataMap.remove(NEWS_TYPE);
            } else {
                addContactsAndMessagesToMap();
                localDataAdded = true;
            }

            Iterator it = dataMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (((String) pair.getKey()).equals(LEAGUE_TYPE)) {

                    if (leagueArray.length() > 0) {
                        for (int i = 0; i < leagueArray.length(); i++) {
                            if (i == 0) {
                                content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_HEADER, "League"));
                            }
                            content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_LEAGUE, leagueArray.getJSONObject(i)));
                        }
                    }
                    it.remove();

                } else if (((String) pair.getKey()).equals(TEAM_TYPE)) {

                    if (teamsArray.length() > 0) {
                        for (int i = 0; i < teamsArray.length(); i++) {
                            if (i == 0) {
                                content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_HEADER, "Teams"));
                            }
                            content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_TEAM, teamsArray.getJSONObject(i)));
                        }
                    }
                    it.remove();

                } else if (((String) pair.getKey()).equals(PLAYER_TYPE)) {

                    if (playersArray.length() > 0) {
                        for (int i = 0; i < playersArray.length(); i++) {
                            if (i == 0) {
                                content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_HEADER, "Player Profile"));
                            }
                            content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_PLAYER_PROFILE, playersArray.getJSONObject(i)));
                        }
                    }
                    it.remove();

                } else if (((String) pair.getKey()).equals(MATCH_TYPE)) {

                    if (matchesArray.length() > 0) {
                        for (int i = 0; i < matchesArray.length(); i++) {
                            if (i == 0) {
                                content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_HEADER, "Matches"));
                            }
                            content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_MATCH, matchesArray.getJSONObject(i)));
                        }
                    }
                    it.remove();
                } else if (((String) pair.getKey()).equals(NEWS_TYPE)) {
                    if (newsArray.length() > 0) {
                        for (int i = 0; i < newsArray.length(); i++) {
                            if (i == 0) {
                                content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_HEADER, "News"));
                            }
                            content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_NEWS, newsArray.getJSONObject(i)));
                        }
                    }
                    it.remove();
                }
            }

            if (!localDataAdded) {
                addContactsAndMessagesToMap();
            }
            success = true;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        globalSearchListAdapter.updateData(content, keyword);
        return success;
    }

    private void addContactsAndMessagesToMap() {
        ArrayList<Message> messages = SportsUnityDBHelper.getInstance(getApplicationContext()).getMatchingChat(keyword);
        ArrayList<Contacts> contacts = SportsUnityDBHelper.getInstance(getApplicationContext()).getMatchingContacts(keyword);
        for (int i = 0; i < messages.size(); i++) {
            if (i == 0) {
                content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_HEADER, "Messages"));
            }
            content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_MESSAGE, messages.get(i)));
        }
        for (int i = 0; i < contacts.size(); i++) {
            if (i == 0) {
                content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_HEADER, "Contacts"));
            }
            content.add(new GlobalContentItemObject(GlobalSearchListAdapter.VIEW_TYPE_CONTACT, contacts.get(i)));
        }


    }

    private boolean handleResponse(String response) {
        boolean success = false;
        try {
            searchResponse = new JSONObject(response);
            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

}
