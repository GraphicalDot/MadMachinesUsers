package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.controller.FilterActivity;
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
public class MatchListFragment extends Fragment {

//    private static final String liveScore = "http://52.74.142.219:8080/get_league_fixtures?league_id=1204&date=" + formattedDate;

    private static final String LIST_LISTENER_KEY = "list_listener";
    private static final String LIST_OF_MATCHES_REQUEST_TAG = "list_request_tag";

    //private RecyclerView mRecyclerView;
     /*private MatchListAdapter mAdapter;*/
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ArrayList<JSONObject> matches = new ArrayList<>();

    private ScoresContentListener contentListener = new ScoresContentListener();


    private int sportsSelectedNum = 0;
    private ArrayList<String> sportSelected;
    private MatchListWrapperAdapter matchListWrapperAdapter;
    private RecyclerView mWraperRecyclerView;
    private List<MatchListWrapperDTO> matchList = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(com.sports.unity.R.layout.fragment_match_list, container, false);
        initView(view);
        sportsSelectedNum = UserUtil.getFilterSportsSelected().size();
        sportSelected = UserUtil.getFilterSportsSelected();
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
            startActivityForResult(i, Constants.REQUEST_CODE_SCORE);
            return true;
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

        handleIfSportsChanged();

    }


    @Override
    public void onPause() {
        super.onPause();
        sportsSelectedNum = UserUtil.getFilterSportsSelected().size();
        sportSelected = UserUtil.getFilterSportsSelected();
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
        mWraperRecyclerView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getContext(), VERTICAL, false));

        matchListWrapperAdapter = new MatchListWrapperAdapter(matchList,getActivity(),getContext());
        mWraperRecyclerView.setAdapter(matchListWrapperAdapter);


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
        if (sportsSelectedNum != UserUtil.getFilterSportsSelected().size()) {
            isSportsChanged = true;
        } else {
            for (int i = 0; i < sportSelected.size(); i++) {
                if (!UserUtil.getFilterSportsSelected().contains(sportSelected.get(i))) {
                    isSportsChanged = true;
                }
            }
        }
        if (isSportsChanged) {
            mSwipeRefreshLayout.setRefreshing(true);
            requestContent();
            sportSelected = UserUtil.getFilterSportsSelected();
            sportsSelectedNum = UserUtil.getFilterSportsSelected().size();
        }
    }

    private void renderContent() {
        Log.i("List of Matches", "Render Content");

        matchListWrapperAdapter.notifyDataSetChanged();
    }

    private boolean handleContent(String content) {
        Log.i("List of Matches", "Handle Content");
        boolean success = false;
        matchList.clear();
        matches.clear();
        ArrayList<JSONObject> list = ScoresJsonParser.parseListOfMatches(content);
        if (list.size() > 0) {

            if (UserUtil.getFilterSportsSelected().contains(Constants.SPORTS_TYPE_CRICKET) && UserUtil.getFilterSportsSelected().contains(Constants.SPORTS_TYPE_FOOTBALL)) {
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
                if (UserUtil.getFilterSportsSelected().contains(Constants.SPORTS_TYPE_CRICKET)) {
                    matches.addAll(cricket);
                } else {
                    matches.addAll(football);
                }
            }
            success = true;
            // added By Ashish For grouping
            String day = null;
            long epochTime= 0l;
            String leagueName = "";
            Map<String, Map<String, MatchListWrapperDTO>> daysMap = new HashMap<>();
            String sportsType= "";
            for(int i = 0; i<matches.size();i++){
                try{
                    JSONObject object = matches.get(i);
                    if(!object.isNull("match_datetime_epoch")){
                        epochTime = object.getLong("match_datetime_epoch");
                        day=  DateUtil.getDayFromEpochTime(epochTime * 1000, getContext());
                        leagueName = object.getString("league_name");
                        sportsType = object.getString("type");
                    } else if(!object.isNull("match_date_epoch")){
                        epochTime = object.getLong("match_date_epoch");
                        sportsType = object.getString("type");
                        day=  DateUtil.getDayFromEpochTime(epochTime * 1000, getContext());
                        if(!object.isNull("league_name")){
                            leagueName = object.getString("league_name");
                        }
                    }
                    //Log.i("League Name", "handleContent: "+leagueName);
                   if(daysMap.containsKey(day)){
                       Log.i("League Name", "handleContent: "+leagueName);
                       Log.i("Day Name", "handleContent: "+day);
                       Map<String, MatchListWrapperDTO> leagueMapTemp = daysMap.get(day);
                        if(leagueMapTemp.containsKey(leagueName)){

                            MatchListWrapperDTO dayGroupDto =    leagueMapTemp.get(leagueName);
                            ArrayList<JSONObject> dayGroupList = dayGroupDto.getList();
                            dayGroupList.add(object);
                            dayGroupDto.setList(dayGroupList);
                            dayGroupDto.setDay(day);
                            dayGroupDto.setEpochTime(epochTime);
                            dayGroupDto.setSportsType(sportsType);
                            dayGroupDto.setLeagueName(leagueName);
                            leagueMapTemp.put(leagueName, dayGroupDto);
                            daysMap.put(day,leagueMapTemp);
                        } else{
                            MatchListWrapperDTO dayGroupDto =   new MatchListWrapperDTO();
                            Map<String, MatchListWrapperDTO> leagueMapTempleagueNew = new HashMap<>();
                            ArrayList<JSONObject> dayGroupList = new ArrayList<>();
                            dayGroupList.add(object);
                            dayGroupDto.setList(dayGroupList);
                            dayGroupDto.setDay(day);
                            dayGroupDto.setEpochTime(epochTime);
                            dayGroupDto.setSportsType(sportsType);
                            dayGroupDto.setLeagueName(leagueName);
                            leagueMapTempleagueNew.put(leagueName, dayGroupDto);
                            daysMap.put(day,leagueMapTemp);
                        }
                    }else{

                        MatchListWrapperDTO dayGroupDto =   new MatchListWrapperDTO();
                        Map<String, MatchListWrapperDTO> leagueMapTemp = new HashMap<>();
                        ArrayList<JSONObject> dayGroupList = new ArrayList<>();
                        dayGroupList.add(object);
                        dayGroupDto.setList(dayGroupList);
                        dayGroupDto.setDay(day);
                        dayGroupDto.setEpochTime(epochTime);
                        dayGroupDto.setSportsType(sportsType);
                        dayGroupDto.setLeagueName(leagueName);
                        leagueMapTemp.put(leagueName, dayGroupDto);
                        daysMap.put(day,leagueMapTemp);
                    }







/*

                    if(daysMap.containsKey(day)){
                        if(leagueMap.containsKey(leagueName)){
                            MatchListWrapperDTO dayGroupDto =    daysMap.get(day);
                            if(dayGroupDto.getLeagueName().equals(leagueName)){
                                ArrayList<JSONObject> dayGroupList = dayGroupDto.getList();
                                dayGroupList.add(object);
                                dayGroupDto.setList(dayGroupList);
                                dayGroupDto.setDay(day);
                                dayGroupDto.setEpochTime(epochTime);
                                dayGroupDto.setSportsType(sportsType);
                                dayGroupDto.setLeagueName(leagueName);
                                daysMap.put(day, dayGroupDto);
                                leagueMap.put(leagueName,daysMap);
                            }
                        }else{



                            MatchListWrapperDTO dayGroupDto =   new MatchListWrapperDTO();

                            ArrayList<JSONObject> dayGroupList = new ArrayList<>();
                            dayGroupList.add(object);
                            dayGroupDto.setList(dayGroupList);
                            dayGroupDto.setDay(day);
                            dayGroupDto.setEpochTime(epochTime);
                            dayGroupDto.setSportsType(sportsType);
                            dayGroupDto.setLeagueName(leagueName);
                            daysMap.put(day, dayGroupDto);
                            leagueMap.put(leagueName,daysMap);
                        }
                    } else{
                        Map<String, MatchListWrapperDTO> daysMapTemp = new HashMap<>();

                        MatchListWrapperDTO dayGroupDto =   new MatchListWrapperDTO();

                        ArrayList<JSONObject> dayGroupList = new ArrayList<>();
                        dayGroupList.add(object);
                        dayGroupDto.setList(dayGroupList);
                        dayGroupDto.setDay(day);
                        dayGroupDto.setEpochTime(epochTime);
                        dayGroupDto.setSportsType(sportsType);
                        dayGroupDto.setLeagueName(leagueName);
                        daysMap.put(day,dayGroupDto);
                        leagueMap.put(leagueName,daysMap);

                    }
*/




                }catch (Exception  e)
                {
                    e.printStackTrace();
                }
            }
            matchList.clear();
            Log.i("Data Map", "handleContent: " + daysMap.keySet());

            Set<String> daySet = daysMap.keySet();

                  for(String dayKey :daySet) {
                      Map<String, MatchListWrapperDTO > leagueMaps = daysMap.get(dayKey);
                      Set<String> keySet = leagueMaps.keySet();
                      for (String key : keySet) {
                         int s = leagueMaps.get(key).getList().size();
                          Log.i("List Size", "handleContent: " + s);
                          if (s > 0) {
                              matchList.add(leagueMaps.get(key));
                          }

                      }
                  }
            Collections.sort(matchList);
            Log.i("Data Map", "handleContent: " + matchList);
            matchListWrapperAdapter.notifyDataSetChanged();
        } else {
            //nothing
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

        HashMap<String, String> parameters = new HashMap<>();
        ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_MATCHES_LIST, parameters, LIST_LISTENER_KEY, LIST_OF_MATCHES_REQUEST_TAG);
//        ScoresContentHandler.getInstance().requestListOfMatches(LIST_LISTENER_KEY, LIST_OF_MATCHES_REQUEST_TAG);

//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
//        String formattedDate = df.format(c.getTime());
//
//        String liveScore = "http://52.74.142.219:8080/get_league_fixtures?league_id=1204&date=" + formattedDate;
//        String URL_UPCOMING_MATCHES = "http://52.74.142.219:8080/get_football_upcoming_fixtures";
//
    }

    private class ScoresContentListener implements ScoresContentHandler.ContentListener {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            if (tag.equals(LIST_OF_MATCHES_REQUEST_TAG)) {
                boolean success = false;
                if (responseCode == 200) {
                    success = MatchListFragment.this.handleContent(content);
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
