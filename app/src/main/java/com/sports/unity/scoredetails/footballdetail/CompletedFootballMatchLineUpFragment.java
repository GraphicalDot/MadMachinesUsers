package com.sports.unity.scoredetails.footballdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballLineUpAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballLineUpDTO;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.scores.model.ScoresContentHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class CompletedFootballMatchLineUpFragment extends BasicVolleyRequestResponseViewHelper {

    private String title;
    private HashMap<String,String> requestParameters;
    private JSONObject response;

    private View manageRootView;

    private TextView tvCaptainFirst;
    private TextView tvCaptainSecond;



    private RecyclerView rvLineup;
    private RecyclerView rvSubstitutes;

    private SwipeRefreshLayout swipeRefreshLayout;

    private CompleteFootballLineUpAdapter completeFootballLineUpAdapter;
    private CompleteFootballLineUpAdapter completeFootballSubstituteUpAdapter;

    private List<CompleteFootballLineUpDTO> substitutesList = new ArrayList<>();
    private List<CompleteFootballLineUpDTO> lineUpList = new ArrayList<>();

    public CompletedFootballMatchLineUpFragment(String title) {
        this.title = title;
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_football_live_match_lineups;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return "FootballLineUpRequestListener";
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress);

        MatchLineUpComponentListener  componentListener = new MatchLineUpComponentListener( getRequestTag(), progressBar, errorLayout);
        return componentListener;
    }

    @Override
    public String getRequestTag() {
        return "MatchLineUpRequestTag";
    }

    @Override
    public String getRequestCallName() {
        return ScoresContentHandler.CALL_NAME_FOOTBALL_LINE_UP;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public void initialiseViews(View view) {
        initView(view);
    }


    public void setRequestParameters(HashMap<String,String> params ) {
        this.requestParameters = params;
    }

    private void initView(View view) {
        try {

            Context context = view.getContext();

            manageRootView = view.findViewById(R.id.parent_layout);
            manageRootView.setVisibility(View.GONE);

            tvCaptainFirst = (TextView) view.findViewById(R.id.tv_team_first_captain);
            tvCaptainSecond = (TextView) view.findViewById(R.id.tv_team_second_captain);
            rvLineup = (RecyclerView) view.findViewById(R.id.rv_lineup);
            rvLineup.setLayoutManager(new LinearLayoutManager(context, VERTICAL, true));
            rvSubstitutes = (RecyclerView) view.findViewById(R.id.rv_substitutes);
            rvSubstitutes.setLayoutManager(new LinearLayoutManager(context, VERTICAL, true));
            rvSubstitutes.setNestedScrollingEnabled(false);
            completeFootballLineUpAdapter = new CompleteFootballLineUpAdapter(lineUpList, context);
            rvLineup.setAdapter(completeFootballLineUpAdapter);
            rvLineup.setNestedScrollingEnabled(false);
            completeFootballSubstituteUpAdapter = new CompleteFootballLineUpAdapter(substitutesList, context);
            rvSubstitutes.setAdapter(completeFootballSubstituteUpAdapter);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sv_swipe_football_match_lineup);
            swipeRefreshLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    requestContent();
                }

            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean handleContent(String content) {
        boolean success = false;
        try {
            JSONObject jsonObject = new JSONObject(content);
            success = jsonObject.getBoolean("success");
            if( success ) {
                response = jsonObject;
            } else {
                //nothing
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return success;
    }

    private boolean renderDisplay() {
        boolean success = false;

        if( ! response.isNull("data") ) {
            try{
                JSONObject dataObject = response.getJSONObject("data");
                JSONArray subsArray = dataObject.getJSONArray("subs");
                JSONArray teamsObjectArray = dataObject.getJSONArray("teams");
                JSONArray substitutionsArray = dataObject.getJSONArray("substitutions");
                JSONArray matchEventsArray = dataObject.getJSONArray("match_events");

                lineUpList.clear();
                substitutesList.clear();

                tvCaptainFirst.setText("N/A");
                tvCaptainSecond.setText("N/A");

                CompleteFootballLineUpDTO completeFootballLineUpDTO = null;
                int length = subsArray.length();
                int tempLength = length/2;
                for (int i = 0; i < length/2; i++) {
                    try {
                        JSONObject  teamSecondObject = subsArray.getJSONObject(i);
                        JSONObject teamFirstObject = subsArray.getJSONObject(tempLength);
                        completeFootballLineUpDTO = new CompleteFootballLineUpDTO();
                        setPlayerDetails(completeFootballLineUpDTO, teamFirstObject, substitutionsArray, matchEventsArray);
                        setSecondTeamDetails(completeFootballLineUpDTO, teamSecondObject, matchEventsArray, substitutionsArray);
                        substitutesList.add(completeFootballLineUpDTO);
                        tempLength++;
                    }catch (Exception e){e.printStackTrace();}
                }
                length = teamsObjectArray.length();
                tempLength = length/2;
                for (int i = 0; i < length/2; i++) {
                    try{
                        JSONObject teamSecondObject = teamsObjectArray.getJSONObject(i);
                        JSONObject teamFirstObject = teamsObjectArray.getJSONObject(tempLength);
                        completeFootballLineUpDTO = new CompleteFootballLineUpDTO();
                        setTeamFirstLineUps(completeFootballLineUpDTO, teamFirstObject, matchEventsArray, substitutionsArray);
                        setTeamSecondLineDetails(completeFootballLineUpDTO, teamSecondObject, matchEventsArray, substitutionsArray);
                        lineUpList.add(completeFootballLineUpDTO);
                        tempLength++;
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                completeFootballLineUpAdapter.notifyDataSetChanged();
                completeFootballSubstituteUpAdapter.notifyDataSetChanged();

                success = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            //nothing
        }
        return success;
    }

    private void setTeamSecondLineDetails(CompleteFootballLineUpDTO completeFootballLineUpDTO, JSONObject teamSecondObject, JSONArray matchEventsArray, JSONArray substitutionsArray) throws JSONException {
        completeFootballLineUpDTO.setPlayerNameSecond(teamSecondObject.getString("name"));
        completeFootballLineUpDTO.setPlayerPostionNumberSecond(teamSecondObject.getString("jersey_number"));
        //getMatchEventsSecond(matchEventsArray, teamSecondObject.getString("name"), completeFootballLineUpDTO);

        completeFootballLineUpDTO.setEnterExitImageSecond(getOnOffPlayer(substitutionsArray, teamSecondObject.getString("name")));
        String playerOnName = getOnOffPlayer(substitutionsArray, teamSecondObject.getString("name"));
        if (playerOnName != null) {
            completeFootballLineUpDTO.setEnterExitImageSecond("OFF");
            completeFootballLineUpDTO.setOffEnterExitImageSecond("ON");
            completeFootballLineUpDTO.setOffPlayerNameSecond(playerOnName);
        }
    }

    private void setTeamFirstLineUps(CompleteFootballLineUpDTO completeFootballLineUpDTO, JSONObject teamFirstObject, JSONArray matchEventsArray, JSONArray substitutionsArray) throws JSONException {
        completeFootballLineUpDTO.setPlayerName(teamFirstObject.getString("name"));
        completeFootballLineUpDTO.setPlayerPostionNumber(teamFirstObject.getString("jersey_number"));
//        getMatchEventsFirst(matchEventsArray, teamFirstObject.getString("name"), completeFootballLineUpDTO);

        String playerOnName = getOnOffPlayer(substitutionsArray, teamFirstObject.getString("name"));
        if (playerOnName != null) {
            completeFootballLineUpDTO.setEnterExitImage("OFF");
            completeFootballLineUpDTO.setOffEnterExitImage("ON");
            completeFootballLineUpDTO.setOffPlayerName(playerOnName);
        }
    }

    private void setSecondTeamDetails(CompleteFootballLineUpDTO completeFootballLineUpDTO, JSONObject teamSecondObject, JSONArray matchEventsArray, JSONArray substitutionsArray) throws JSONException {

        completeFootballLineUpDTO.setPlayerNameSecond(teamSecondObject.getString("player_name"));
        completeFootballLineUpDTO.setPlayerPostionNumberSecond(teamSecondObject.getString("jersey_number"));
        //getMatchEventsSecond(matchEventsArray, teamSecondObject.getString("player_name"), completeFootballLineUpDTO);

        completeFootballLineUpDTO.setEnterExitImageSecond(getOnOffPlayer(substitutionsArray, teamSecondObject.getString("player_name")));
        String playerOnName = getOnOffPlayer(substitutionsArray, teamSecondObject.getString("player_name"));
        if (playerOnName != null) {
            completeFootballLineUpDTO.setEnterExitImage("OFF");
            completeFootballLineUpDTO.setOffEnterExitImage("ON");
            completeFootballLineUpDTO.setOffPlayerName(playerOnName);
        }


    }

    private void setPlayerDetails(CompleteFootballLineUpDTO completeFootballLineUpDTO, JSONObject teamFirstObject, JSONArray substitutionsArray, JSONArray matchEventsArray) throws JSONException {
        completeFootballLineUpDTO.setPlayerName(teamFirstObject.getString("player_name"));
        completeFootballLineUpDTO.setPlayerPostionNumber(teamFirstObject.getString("jersey_number"));
        completeFootballLineUpDTO.setEnterExitImage(getOnOffPlayer(substitutionsArray, teamFirstObject.getString("player_name")));
        //getMatchEventsFirst(matchEventsArray, teamFirstObject.getString("player_name"), completeFootballLineUpDTO);
        String playerOnName = getOnOffPlayer(substitutionsArray, teamFirstObject.getString("player_name"));
        if (playerOnName != null) {
            completeFootballLineUpDTO.setEnterExitImage("OFF");
            completeFootballLineUpDTO.setOffEnterExitImage("ON");
            completeFootballLineUpDTO.setOffPlayerName(playerOnName);
        }
    }

    private String getOnOffPlayer(JSONArray substitutionsArray, String playerName) {
        String playerOn= null;
        try{
            for(int i = 0;i<substitutionsArray.length();i++){
                JSONObject substitutesObject = substitutionsArray.getJSONObject(i);
                if(!substitutesObject.isNull("player_off")){
                    if(playerName.equals(substitutesObject.getString("player_off"))){
                        if(!substitutesObject.isNull("player_on")){
                            playerOn = substitutesObject.getString("player_on");
                        }
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return playerOn;
    }

    public class MatchLineUpComponentListener extends CustomComponentListener {

        public MatchLineUpComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
            super(requestTag, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = CompletedFootballMatchLineUpFragment.this.handleContent(content);
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        protected void showErrorLayout() {
            if( manageRootView.getVisibility() == View.VISIBLE ) {
                //nothing
            } else {
                super.showErrorLayout();
            }
        }

        @Override
        protected void showProgress() {
            if( manageRootView.getVisibility() == View.VISIBLE ) {
                //nothing
            } else {
                super.showProgress();
            }
        }

        @Override
        protected void hideProgress() {
            super.hideProgress();

            if( swipeRefreshLayout != null ){
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderDisplay();
            if( success ){
                manageRootView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
            } else {
                showErrorLayout();
            }
        }

    }

}
