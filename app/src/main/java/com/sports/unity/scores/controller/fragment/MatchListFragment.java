package com.sports.unity.scores.controller.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sports.unity.R;
import com.sports.unity.common.controller.FilterActivity;
import com.sports.unity.common.controller.GlobalSearchActivity;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.DataChangeCounterHandler;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FriendsWatchingHandler;
import com.sports.unity.common.model.FriendsWatchingHandler.FriendsContentListener;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.news.controller.activity.NewsSearchActivity;
import com.sports.unity.scoredetails.MatchListScrollListener;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.ScoresUtil;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Edwin on 15/02/2015.
 */
public class MatchListFragment extends Fragment {

    private static final String LIST_LISTENER_KEY = "list_listener";
    private static final String LIST_OF_MATCHES_REQUEST_TAG = "list_request_tag";

    private static final String FRIENDS_WATCHING_LISTENER_KEY = "friends_watching_listener_key";
    private static final String FRIENDS_WATCHING_REQUEST_TAG = "friends_watching_request_tag";


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MatchListWrapperAdapter matchListWrapperAdapter;
    private RecyclerView mWraperRecyclerView;

    private ArrayList<JSONObject> matches = new ArrayList<>();
    private ScoresContentListener contentListener = new ScoresContentListener();

    private int sportsSelectedNum = 0;
    private ArrayList<String> sportSelected;
    private List<MatchListWrapperDTO> matchList = new ArrayList<>();

    private Bundle bundle;
    private String scoreDetailsId = "";
    private FavouriteItem favouriteItem;
    private boolean isStaffPicked;

    private boolean matchListSwitch = false;

    private ArrayList<FavouriteItem> flagFavItem;
    private ArrayList<MatchListWrapperItem> dataItem = new ArrayList<MatchListWrapperItem>();

    private GoogleApiClient mClient;
    private Uri mUrl;
    private String mTitle="Live Scores";
    private String mDescription="Live minute by minute commentary and updated scores of all the matches happening, plus get notified for the ones you love most.";

    MatchListScrollListener matchListScrollListener = new MatchListScrollListener() {
        @Override
        public void scroll(int position) {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                LinearLayoutManager manager = (LinearLayoutManager) mWraperRecyclerView.getLayoutManager();
                manager.scrollToPosition(position);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.sendAnalyticsData(getActivity().getApplication(), "ScoresScreen");
        bundle = getArguments();
        if (bundle != null) {
            scoreDetailsId = bundle.getString(Constants.INTENT_KEY_ID);
            isStaffPicked = bundle.getBoolean(Constants.SPORTS_TYPE_STAFF, false);
            favouriteItem = new FavouriteItem(scoreDetailsId);
            scoreDetailsId = favouriteItem.getId();
        }

        mUrl=Uri.parse("android-app://co.sports.unity/mobileapp/sportsunity.co/matches");
        mClient = CommonUtil.getAppIndexingClient(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        Action action = CommonUtil.getAction(mTitle, mDescription, mUrl);
        CommonUtil.startAppIndexing(mClient, action);
    }

    @Override
    public void onStop() {
        Action action = CommonUtil.getAction(mTitle, mDescription, mUrl);
        CommonUtil.stopAppIndexing(mClient, action);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(com.sports.unity.R.layout.fragment_match_list, container, false);
        initView(view);
        if (UserUtil.getScoreFilterSportsSelected() != null) {
            sportsSelectedNum = UserUtil.getScoreFilterSportsSelected().size();
            sportSelected = UserUtil.getScoreFilterSportsSelected();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_scores_menu, menu);

        MenuItem item = menu.findItem(R.id.myswitch);
        item.setActionView(R.layout.switch_matchlist);

        RelativeLayout relativeLayout = (RelativeLayout) item.getActionView();
        final SwitchCompat sw = (SwitchCompat) relativeLayout.findViewById(R.id.switchForActionBar);
        sw.setChecked(matchListSwitch);
        if (matchListSwitch) {
            sw.setThumbDrawable(getResources().getDrawable(R.drawable.ic_fav_matches));
        } else {
            sw.setThumbDrawable(getResources().getDrawable(R.drawable.ic_all_matches));
        }
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    matchListSwitch = isChecked;
                    sw.setThumbDrawable(getResources().getDrawable(R.drawable.ic_fav_matches));
                    updateMatchList(isChecked);
                } else {
                    matchListSwitch = isChecked;
                    sw.setThumbDrawable(getResources().getDrawable(R.drawable.ic_all_matches));
                    updateMatchList(isChecked);
                }
            }
        });
    }

    private void updateMatchList(boolean isChecked) {
        matchListWrapperAdapter.updateMatches(isChecked);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent intent = new Intent(getActivity(), GlobalSearchActivity.class);
            intent.putExtra(Constants.INTENT_KEY_GLOBAL_POSITION, 0);
            startActivity(intent);
            return true;
        }

        if (id == com.sports.unity.R.id.action_filter) {
            Intent i = new Intent(getActivity(), FilterActivity.class);
            i.putExtra(Constants.KEY_ORIGIN_ACTIVITY, Constants.SCORE_ACTIVITY);
            startActivityForResult(i, Constants.REQUEST_CODE_SCORE);
            return true;
        }
        if (id == R.id.refresh) {
            new Handler().post(new Runnable() {

                @Override
                public void run() {

                    requestContent();
                    mSwipeRefreshLayout.setRefreshing(true);

                }

            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        FriendsWatchingHandler.getInstance(getContext()).addFriendsContentListener(friendsContentListener, FRIENDS_WATCHING_LISTENER_KEY);
        addResponseListener();
        if (matches.size() == 0) {
            showProgress(getView());
            requestContent();
        }
        handleIfSportsChanged();
        if (FavouriteItemWrapper.getInstance(getActivity()).isFavouriteChanged()) {
            ((MatchListWrapperAdapter) mWraperRecyclerView.getAdapter()).notifyFavIconChanged();
            FavouriteItemWrapper.getInstance(getActivity()).setFavouriteChanged(false);
        }

        DataChangeCounterHandler dataChangeCounterHandler = FavouriteItemWrapper.getInstance(getContext()).getDataChangeCounterHandler();
        if (dataChangeCounterHandler.isContentChanged(LIST_LISTENER_KEY)) {
            matchListWrapperAdapter.notifyAdapter();
        } else {
            // do nothing
        }
        dataChangeCounterHandler.setContentCounter(LIST_LISTENER_KEY);
    }

    private boolean handleStaffFavContent() {
        String staffFavString = UserUtil.getStaffSelectedData(getActivity());
        ArrayList<FavouriteItem> favouriteItems = new ArrayList<FavouriteItem>();
        if (null != staffFavString && !TextUtils.isEmpty(staffFavString)) {
            flagFavItem = FavouriteItemWrapper.getInstance(getActivity()).getFavListOfOthers(staffFavString);
            if (flagFavItem != null && flagFavItem.size() > 0) {
                for (FavouriteItem f : flagFavItem) {
                    final String id = f.getId();
                    if (!TinyDB.getInstance(getActivity()).getBoolean(id, false)) {
                        favouriteItems.add(f);
                    }
                }
                if (favouriteItems.size() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        FriendsWatchingHandler.getInstance(getContext()).removeFriendsContentListener(FRIENDS_WATCHING_LISTENER_KEY);
        sportsSelectedNum = UserUtil.getScoreFilterSportsSelected().size();
        sportSelected = UserUtil.getScoreFilterSportsSelected();
        removeResponseListener();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initView(View view) {
/*
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_scores);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MatchListAdapter(matches, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);*/

        mWraperRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_scores);
        mWraperRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        mWraperRecyclerView.setLayoutManager(manager);
        mWraperRecyclerView.setFocusable(false);
        boolean shouldShowBanner = false;
        if (getActivity() instanceof MainActivity) {
            shouldShowBanner = handleStaffFavContent();
        }
        matchListWrapperAdapter = new MatchListWrapperAdapter(dataItem, getActivity(), shouldShowBanner, matchListSwitch, matchListScrollListener);
        mWraperRecyclerView.setAdapter(matchListWrapperAdapter);

        hideErrorLayout(view);

        initProgress(view);
        hideProgress(view);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.app_theme_blue));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                        requestContent();

                    }

                });
            }
        });
    }

    private void handleIfSportsChanged() {
        boolean isSportsChanged = false;
        if (sportsSelectedNum != UserUtil.getScoreFilterSportsSelected().size()) {
            isSportsChanged = true;
        } else {
            for (int i = 0; i < sportSelected.size(); i++) {
                if (!UserUtil.getScoreFilterSportsSelected().contains(sportSelected.get(i))) {
                    isSportsChanged = true;
                }
            }
        }
        if (isSportsChanged) {
            mSwipeRefreshLayout.setRefreshing(true);
            requestContent();
            sportSelected = UserUtil.getScoreFilterSportsSelected();
            sportsSelectedNum = UserUtil.getScoreFilterSportsSelected().size();
        }
    }


    private void renderContent() {
        matchListWrapperAdapter.notifyAdapter();
    }

    private boolean handleContentForIndividuals(String content) {
        boolean success = false;
        matchList.clear();
        matches.clear();
        ArrayList<JSONObject> list = ScoresJsonParser.parseIndividualListOfMatches(content, favouriteItem.getSportsType());
        if (list.size() > 0) {
            matches.addAll(list);
            success = true;
            // added By Ashish For grouping
            String day = null;
            long epochTime = 0l;
            String leagueName = "";
            Map<String, Map<String, MatchListWrapperDTO>> daysMap = new HashMap<>();
            String sportsType = "";
            for (int i = 0; i < matches.size(); i++) {
                try {
                    JSONObject object = matches.get(i);

                    if (!object.isNull("match_time") && Constants.SPORTS_TYPE_CRICKET.equalsIgnoreCase(object.getString("type"))) {
                        epochTime = object.getLong("match_time");
                        day = DateUtil.getDayFromEpochTime(epochTime * 1000, getContext());
                        if (!object.isNull("series_name")) {
                            leagueName = object.getString("series_name");
                        }
                        sportsType = object.getString("type");
                    } else {
                        epochTime = object.getLong("match_date_epoch");
                        sportsType = object.getString("type");
                        day = DateUtil.getDayFromEpochTime(epochTime * 1000, getContext());
                        if (!object.isNull("league_name")) {
                            leagueName = object.getString("league_name");
                        }
                    }
                    MatchListWrapperDTO dayGroupDto = null;
                    ArrayList<JSONObject> dayGroupList = null;
                    Map<String, MatchListWrapperDTO> leagueMapTemp = null;
                    if (daysMap.containsKey(day)) {
                        leagueMapTemp = daysMap.get(day);

                        if (leagueMapTemp.containsKey(leagueName)) {
                            dayGroupDto = leagueMapTemp.get(leagueName);
                            dayGroupList = dayGroupDto.getList();
                        } else {
                            dayGroupDto = new MatchListWrapperDTO();
                            dayGroupList = new ArrayList<>();

                        }
                    } else {

                        leagueMapTemp = new HashMap<>();
                        dayGroupDto = new MatchListWrapperDTO();
                        dayGroupList = new ArrayList<>();


                    }
                    dayGroupList.add(object);
                    dayGroupDto.setList(dayGroupList);
                    dayGroupDto.setDay(day);
                    dayGroupDto.setEpochTime(epochTime);
                    dayGroupDto.setSportsType(sportsType);
                    dayGroupDto.setLeagueName(leagueName);
                    leagueMapTemp.put(leagueName, dayGroupDto);
                    daysMap.put(day, leagueMapTemp);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            matchList.clear();
            Set<String> daySet = daysMap.keySet();
            for (String dayKey : daySet) {
                Map<String, MatchListWrapperDTO> leagueMaps = daysMap.get(dayKey);
                Set<String> keySet = leagueMaps.keySet();

                for (String key : keySet) {
                    int s = leagueMaps.get(key).getList().size();

                    if (s > 0) {
                        matchList.add(leagueMaps.get(key));
                    }

                }
            }
            dataItem.clear();
            for (MatchListWrapperDTO f : matchList) {

                ArrayList<JSONObject> object = f.getList();
                for (JSONObject jsonObject : object) {
                    MatchListWrapperItem wrapperItem = new MatchListWrapperItem(f);
                    wrapperItem.setJsonObject(jsonObject);
                    dataItem.add(wrapperItem);
                }
            }


            Collections.sort(dataItem);
            matchListWrapperAdapter.updateGlobalList(dataItem);
            if (favouriteItem.getFilterType().equalsIgnoreCase(Constants.FILTER_TYPE_LEAGUE) || (isStaffPicked && favouriteItem.getSportsType().equalsIgnoreCase(Constants.SPORTS_TYPE_CRICKET))) {
                matchListWrapperAdapter.setIsIndividualFixture();
            }
        }
        return success;
    }

    private boolean handleContent(String content) {
        boolean success = false;
        matchList.clear();
        matches.clear();
        ArrayList<JSONObject> list = ScoresJsonParser.parseListOfMatches(content);
        if (list.size() > 0) {

            if (UserUtil.getScoreFilterSportsSelected().contains(Constants.SPORTS_TYPE_CRICKET) && UserUtil.getScoreFilterSportsSelected().contains(Constants.SPORTS_TYPE_FOOTBALL)) {
                matches.addAll(list);
            } else {
                ArrayList<JSONObject> cricket = new ArrayList<>();
                ArrayList<JSONObject> football = new ArrayList<>();
                for (JSONObject obj : list) {
                    try {
                        String s = obj.getString(ScoresJsonParser.SPORTS_TYPE_PARAMETER);
                        if (s.equals(ScoresJsonParser.CRICKET)) {
                            cricket.add(obj);
                        } else {
                            football.add(obj);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (UserUtil.getScoreFilterSportsSelected().contains(Constants.SPORTS_TYPE_CRICKET)) {
                    matches.addAll(cricket);
                } else {
                    matches.addAll(football);
                }
            }
            success = true;
            // added By Ashish For grouping
            String day = null;
            long epochTime = 0l;
            String leagueName = "";
            Map<String, Map<String, MatchListWrapperDTO>> daysMap = new HashMap<>();
            String sportsType = "";
            int dayCount = 0;
            int todayIndexPosition = 0;
            for (int i = 0; i < matches.size(); i++) {
                try {
                    JSONObject object = matches.get(i);

                    if (!object.isNull("match_time") && Constants.SPORTS_TYPE_CRICKET.equalsIgnoreCase(object.getString("type"))) {
                        epochTime = object.getLong("match_time");
                        day = DateUtil.getDayFromEpochTime(epochTime * 1000, getContext());
                        leagueName = object.getString("series_name");
                        sportsType = object.getString("type");

                    } else {
                        epochTime = object.getLong("match_date_epoch");
                        sportsType = object.getString("type");
                        day = DateUtil.getDayFromEpochTime(epochTime * 1000, getContext());
                        if (!object.isNull("league_name")) {
                            leagueName = object.getString("league_name");
                        }

                    }
                    MatchListWrapperDTO dayGroupDto = null;
                    ArrayList<JSONObject> dayGroupList = null;
                    Map<String, MatchListWrapperDTO> leagueMapTemp = null;
                    if (daysMap.containsKey(day)) {
                        leagueMapTemp = daysMap.get(day);

                        if (leagueMapTemp.containsKey(leagueName)) {
                            dayGroupDto = leagueMapTemp.get(leagueName);
                            dayGroupList = dayGroupDto.getList();
                        } else {
                            dayGroupDto = new MatchListWrapperDTO();
                            dayGroupList = new ArrayList<>();

                        }
                    } else {

                        leagueMapTemp = new HashMap<>();
                        dayGroupDto = new MatchListWrapperDTO();
                        dayGroupList = new ArrayList<>();


                    }
                    dayGroupList.add(object);
                    dayGroupDto.setList(dayGroupList);
                    dayGroupDto.setDay(day);
                    dayGroupDto.setEpochTime(epochTime);
                    dayGroupDto.setSportsType(sportsType);
                    dayGroupDto.setLeagueName(leagueName);
                    leagueMapTemp.put(leagueName, dayGroupDto);
                    daysMap.put(day, leagueMapTemp);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

//            enterDummyContent(daysMap);

            matchList.clear();
            Set<String> daySet = daysMap.keySet();
            int c = 0;
            for (String dayKey : daySet) {
                Map<String, MatchListWrapperDTO> leagueMaps = daysMap.get(dayKey);
                Set<String> keySet = leagueMaps.keySet();
                for (String key : keySet) {
                    MatchListWrapperDTO tempDTO = leagueMaps.get(key);
                    int s = tempDTO.getList().size();
                    if (s > 0) {
                        leagueMaps.get(key).reorderList();
                        matchList.add(leagueMaps.get(key));
                    }


                }
            }
            dataItem.clear();
            ArrayList<String> ids = new ArrayList<String>();
            for (MatchListWrapperDTO f : matchList) {

                ArrayList<JSONObject> object = f.getList();
                for (JSONObject jsonObject : object) {
                    MatchListWrapperItem wrapperItem = new MatchListWrapperItem(f);
                    wrapperItem.setJsonObject(jsonObject);
                    try {
                        String id = wrapperItem.getJsonObject().getString("match_id");
                        String seriesId = null;
                        boolean isLive = false;
                        if (!wrapperItem.getJsonObject().isNull("series_id")) {
                            seriesId = wrapperItem.getJsonObject().getString("series_id");
                            String matchStatus = wrapperItem.getJsonObject().getString("status");
                            isLive = ScoresUtil.isCricketMatchLive(matchStatus);
                        } else {
                            seriesId = wrapperItem.getJsonObject().getString("league_id");
                            isLive = wrapperItem.getJsonObject().getBoolean("live");
                        }
                        if (isLive) {
                            id = id + "|" + seriesId;
                            ids.add(id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dataItem.add(wrapperItem);
                }
            }
            if (ids.size() > 0) {
                FriendsWatchingHandler.getInstance(getContext()).setMatchId(ids);
                FriendsWatchingHandler.getInstance(getContext()).requestContent(FRIENDS_WATCHING_LISTENER_KEY, FRIENDS_WATCHING_REQUEST_TAG);
            }
            Collections.sort(dataItem);
            matchListWrapperAdapter.updateGlobalList(dataItem);
        }
        return success;
    }

    private FriendsContentListener friendsContentListener = new FriendsContentListener() {
        @Override
        public void handleFriendsContent() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    matchListWrapperAdapter.notifyDataSetChanged();
                }
            });
        }
    };
//    private void enterDummyContent(Map<String, Map<String, MatchListWrapperDTO>> daysMap){
//        Map<String, MatchListWrapperDTO> dto = daysMap.get("Today");
//
//        MatchListWrapperDTO.count = 0;
//        Iterator<String> leaguesIterator = dto.keySet().iterator();
//        while( leaguesIterator.hasNext() ){
//            String league = leaguesIterator.next();
//            MatchListWrapperDTO matchListWrapperDTO = dto.get(league);
//            matchListWrapperDTO.createDummyContent();
//        }
//
//    }

    private void showErrorLayout(View view) {
        if (matches.size() == 0) {
            ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.VISIBLE);
            CustomComponentListener.renderAppropriateErrorLayout(errorLayout);
        } else {
            Toast.makeText(getActivity(), R.string.common_message_internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void hideErrorLayout(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);
    }

    private void addResponseListener() {
        ScoresContentHandler.getInstance().addResponseListener(contentListener, LIST_LISTENER_KEY);
    }

    private void removeResponseListener() {
        ScoresContentHandler.getInstance().removeResponseListener(LIST_LISTENER_KEY);
    }

    private void initProgress(View view) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void showProgress(View view) {
        if (matches.size() == 0) {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress(View view) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
    }

    private void requestContent() {
        Log.i("List of Matches", "Request Content");

        hideErrorLayout(getView());

        if (TextUtils.isEmpty(scoreDetailsId)) {
            HashMap<String, String> parameters = new HashMap<>();
            ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_MATCHES_LIST, parameters, LIST_LISTENER_KEY, LIST_OF_MATCHES_REQUEST_TAG);
        } else {
            HashMap<String, String> parameters = new HashMap<>();
            parameters.put(Constants.INTENT_KEY_ID, favouriteItem.getJsonObject().toString());
            parameters.put(Constants.SPORTS_TYPE_STAFF, String.valueOf(isStaffPicked));
            ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_MATCHES_LIST, parameters, LIST_LISTENER_KEY, LIST_OF_MATCHES_REQUEST_TAG);
        }
    }

    private class ScoresContentListener implements ScoresContentHandler.ContentListener {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            if (tag.equals(LIST_OF_MATCHES_REQUEST_TAG)) {
                boolean success = false;
                if (responseCode == 200) {
                    if (TextUtils.isEmpty(scoreDetailsId)) {
                        success = MatchListFragment.this.handleContent(content);
                    } else {
                        success = MatchListFragment.this.handleContentForIndividuals(content);
                    }
                    if (success) {
                        hideErrorLayout(MatchListFragment.this.getView());
                        MatchListFragment.this.renderContent();
                    } else {
                        showErrorLayout(MatchListFragment.this.getView());
                    }
                } else {
                    showErrorLayout(MatchListFragment.this.getView());
                }

                hideProgress(getView());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
