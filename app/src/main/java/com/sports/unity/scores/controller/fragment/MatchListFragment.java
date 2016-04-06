package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.controller.FilterActivity;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.controller.TeamLeagueDetails;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
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

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by Edwin on 15/02/2015.
 */
public class MatchListFragment extends Fragment implements MatchListWrapperNotify{

//    private static final String liveScore = "http://52.74.142.219:8080/get_league_fixtures?league_id=1204&date=" + formattedDate;

    private static final String LIST_LISTENER_KEY = "list_listener";
    private static final String LIST_OF_MATCHES_REQUEST_TAG = "list_request_tag";

    //private RecyclerView mRecyclerView;
    private MatchListAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<JSONObject> matches = new ArrayList<>();

    private ScoresContentListener contentListener = new ScoresContentListener();


    private int sportsSelectedNum = 0;
    private ArrayList<String> sportSelected;
    private MatchListWrapperAdapter matchListWrapperAdapter;
    private RecyclerView mWraperRecyclerView;
    private List<MatchListWrapperDTO> matchList = new ArrayList<>();

    private Bundle bundle;
    private String scoreDetailsId = "";
    private FavouriteItem favouriteItem;
    private boolean isStaffPicked;
    private LinearLayout staffView;
    private LayoutInflater inflater;
    private ArrayList<FavouriteItem> flagFavItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle != null) {
            scoreDetailsId = bundle.getString(Constants.INTENT_KEY_ID);
            isStaffPicked = bundle.getBoolean(Constants.SPORTS_TYPE_STAFF, false);
            favouriteItem = new FavouriteItem(scoreDetailsId);
            scoreDetailsId = favouriteItem.getId();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        this.inflater = inflater;
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
        menu.findItem(R.id.action_search).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
//            Intent intent = new Intent(getActivity(), NewsSearchActivity.class);
//            startActivity(intent);
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
                    Log.i("List of Matches", "Swipe Refresh Call");

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

        addResponseListener();
        if (matches.size() == 0) {
            Log.i("List of Matches", "Through Resume");

            showProgress(getView());
            requestContent();
        }
        if (getActivity() instanceof MainActivity) {
            handleStaffFavContent();
        }
        handleIfSportsChanged();

    }

    private void handleStaffFavContent() {
        String staffFavString = UserUtil.getStaffFlagUrl(getActivity());
        if (null != staffFavString && !TextUtils.isEmpty(staffFavString)) {
            flagFavItem = FavouriteItemWrapper.getInstance(getActivity()).getFavListOfOthers(staffFavString);
            if (flagFavItem != null && flagFavItem.size() > 0) {
                staffView.removeAllViews();
                for (FavouriteItem f : flagFavItem) {
                    LinearLayout scoreView = (LinearLayout) inflater.inflate(R.layout.score_staff_item, null);
                    ImageView flag = (ImageView) scoreView.findViewById(R.id.flag);
                    Glide.with(getActivity()).load(Uri.parse(f.getFlagImageUrl())).placeholder(R.drawable.ic_no_img).into(flag);
                    final String jsonString = f.getJsonObject().toString();
                    scoreView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), TeamLeagueDetails.class);
                            intent.putExtra(Constants.INTENT_TEAM_LEAGUE_DETAIL_EXTRA, jsonString);
                            intent.putExtra(Constants.SPORTS_TYPE_STAFF, true);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    staffView.addView(scoreView);
                }
                staffView.requestLayout();
                staffView.invalidate();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
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
        mWraperRecyclerView.setNestedScrollingEnabled(false);
        mWraperRecyclerView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getContext(), VERTICAL, false));

        mWraperRecyclerView.setNestedScrollingEnabled(false);
        mWraperRecyclerView.setFocusable(false);
        matchListWrapperAdapter = new MatchListWrapperAdapter(matchList, getActivity(), getContext(),this);
        mWraperRecyclerView.setAdapter(matchListWrapperAdapter);
        staffView = (LinearLayout) view.findViewById(R.id.staff_pick_ll);

        initErrorLayout(view);
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
                        Log.i("List of Matches", "Swipe Refresh Call");

                        requestContent();
                        mSwipeRefreshLayout.setRefreshing(true);
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
        Log.i("List of Matches", "Render Content");

        matchListWrapperAdapter.notifyDataSetChanged();
    }

    private boolean handleContentForSquad(String content) {
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
                        Log.i("League Name", "handleContent: " + leagueName);
                        Log.i("Day Name", "handleContent: " + day);
                        leagueMapTemp = daysMap.get(day);

                        if (leagueMapTemp.containsKey(leagueName)) {
                            Log.d("imax", "setting daygroup");
                            dayGroupDto = leagueMapTemp.get(leagueName);
                            dayGroupList = dayGroupDto.getList();
                        } else {
                            Log.d("imax", "resetting current daygroup");
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
            Log.i("DAYMAP", "handleContent: " + daysMap);
            for (String dayKey : daySet) {
                Map<String, MatchListWrapperDTO> leagueMaps = daysMap.get(dayKey);
                Log.i("LEAGUEMAP", "handleContent: " + leagueMaps);
                Set<String> keySet = leagueMaps.keySet();

                for (String key : keySet) {
                    int s = leagueMaps.get(key).getList().size();

                    if (s > 0) {
                        matchList.add(leagueMaps.get(key));
                    }

                }
            }
            Collections.sort(matchList);
           /* if (favouriteItem.getFilterType().equalsIgnoreCase(Constants.FILTER_TYPE_LEAGUE)) {
                matchListWrapperAdapter.setIsIndividualFixture();
            }*/
            matchListWrapperAdapter.notifyDataSetChanged();
        }
        return success;
    }

    private boolean handleContent(String content) {
        Log.i("List of Matches", "Handle Content");
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
            int todayIndexPosition=0;
            for (int i = 0; i < matches.size(); i++) {
                try {
                    JSONObject object = matches.get(i);

                    if (!object.isNull("match_time") && Constants.SPORTS_TYPE_CRICKET.equalsIgnoreCase(object.getString("type"))) {
                        epochTime = object.getLong("match_time");
                        Log.i("dayCount", "handleContent: "+dayCount);
                        day = DateUtil.getDayFromEpochTime(epochTime * 1000, getContext());
                        leagueName = object.getString("series_name");
                        sportsType = object.getString("type");

                      } else {
                        epochTime = object.getLong("match_date_epoch");
                        sportsType = object.getString("type");

                            Log.i("dayCount", "handleContent: "+dayCount);
                            day = DateUtil.getDayFromEpochTime(epochTime * 1000, getContext());
                            if (!object.isNull("league_name")) {
                                leagueName = object.getString("league_name");
                            }

                    }
                    MatchListWrapperDTO dayGroupDto = null;
                    ArrayList<JSONObject> dayGroupList = null;
                    Map<String, MatchListWrapperDTO> leagueMapTemp = null;
                    if (daysMap.containsKey(day)) {
                        Log.i("League Name", "handleContent: " + leagueName);
                        Log.i("Day Name", "handleContent: " + day);
                        leagueMapTemp = daysMap.get(day);

                        if (leagueMapTemp.containsKey(leagueName)) {
                            Log.d("imax", "setting daygroup");
                            dayGroupDto = leagueMapTemp.get(leagueName);
                            dayGroupList = dayGroupDto.getList();
                        } else {
                            Log.d("imax", "resetting current daygroup");
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
            Log.i("DAYMAP", "handleContent: " + daysMap);
            int c =0;
            for (String dayKey : daySet) {
                Map<String, MatchListWrapperDTO> leagueMaps = daysMap.get(dayKey);
                Log.i("LEAGUEMAP", "handleContent: " + leagueMaps);
                Set<String> keySet = leagueMaps.keySet();

                for (String key : keySet) {
                    MatchListWrapperDTO tempDTO= leagueMaps.get(key);
                    int s = tempDTO.getList().size();
                    dayCount = DateUtil.getDayFromEpochTimeDayCount(tempDTO.getEpochTime() * 1000, getContext());

                    if((( dayCount<3 && dayCount>-3) ) && s > 0 ) {

                            matchList.add(leagueMaps.get(key));

                    }
                }


            }





            Collections.sort(matchList);
            for(int i = 0 ; i<matchList.size();i++)
            { MatchListWrapperDTO tempDTO= matchList.get(i);
                dayCount = DateUtil.getDayFromEpochTimeDayCount(tempDTO.getEpochTime() * 1000, getContext());
                if(dayCount == 0){
                    todayIndexPosition= i;
                    break;
                }
            }
            Log.i("todayIndexPosition", "handleContent: " + todayIndexPosition);
             matchListWrapperAdapter.notifyDataSetChanged();
           // mWraperRecyclerView.getLayoutManager().moveView(todayIndexPosition, 0);
           // mWraperRecyclerView.getLayoutManager().scrollToPosition(todayIndexPosition);
          //mWraperRecyclerView.getLayoutManager().moveView(0, todayIndexPosition);
        }
        return success;
    }

    private void initErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);

        TextView oops = (TextView) errorLayout.findViewById(R.id.oops);
        oops.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoLight());

        TextView something_wrong = (TextView) errorLayout.findViewById(R.id.something_wrong);
        something_wrong.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoLight());
    }

    private void showErrorLayout(View view) {
        if (matches.size() == 0) {
            LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
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
//        ScoresContentHandler.getInstance().requestListOfMatches(LIST_LISTENER_KEY, LIST_OF_MATCHES_REQUEST_TAG);

//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
//        String formattedDate = df.format(c.getTime());
//
//        String liveScore = "http://52.74.142.219:8080/get_league_fixtures?league_id=1204&date=" + formattedDate;
//        String URL_UPCOMING_MATCHES = "http://52.74.142.219:8080/get_football_upcoming_fixtures";
//
    }





    @Override
    public void notifyParent() {
        matchListWrapperAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void refreshData() {
       mSwipeRefreshLayout.setRefreshing(true);
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
                        success = MatchListFragment.this.handleContentForSquad(content);
                    }
                    if (success) {
                        hideErrorLayout(MatchListFragment.this.getView());
                        MatchListFragment.this.renderContent();
                    } else {
                        Log.i("List of Matches", "Error In Handling Content");
                        showErrorLayout(MatchListFragment.this.getView());
                    }
                } else {
                    Log.i("List of Matches", "Error In Response");
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
