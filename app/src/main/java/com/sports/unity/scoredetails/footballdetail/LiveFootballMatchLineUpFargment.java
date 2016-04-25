package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.sports.unity.R;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballLineUpAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballLineUpDTO;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class LiveFootballMatchLineUpFargment extends Fragment implements LiveFootballMatchLineUpHandler.LiveMatchContentListener {

    private TextView tvCaptainFirst;
    private TextView tvCaptainSecond;
    private View tvCarlesPayol;
    private TextView tvlineup;
    private TextView tvsubstitutes;

    private ProgressBar progressBar;
    private RecyclerView rvLineup;
    private RecyclerView rvSubstitutes;
    private CompleteFootballLineUpAdapter completeFootballLineUpAdapter;
    private CompleteFootballLineUpAdapter completeFootballSubstituteUpAdapter;

    private List<CompleteFootballLineUpDTO> lineUpList = new ArrayList<>();
    private List<CompleteFootballLineUpDTO> substitutesList = new ArrayList<>();

    private Timer timerToRefreshContent;

    private String matchId;
    private String matchName="";
    private String date = "";

    private Context context;
    private View managerView;
    private View lineupView;
    private View subsView;

    public LiveFootballMatchLineUpFargment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        matchId =  i.getStringExtra(INTENT_KEY_ID);
        matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
        date = i.getStringExtra(INTENT_KEY_DATE);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_football_live_match_lineups, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        initErrorLayout(view);
        tvCaptainFirst=(TextView)view.findViewById(R.id.tv_team_first_captain);
        tvCaptainSecond=(TextView)view.findViewById(R.id.tv_team_second_captain);
        tvlineup=(TextView)view.findViewById(R.id.tv_line_up);
        tvsubstitutes=(TextView)view.findViewById(R.id.tv_substitutes);
        rvLineup = (RecyclerView) view.findViewById(R.id.rv_lineup);
        rvLineup.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, true));
        rvSubstitutes = (RecyclerView) view.findViewById(R.id.rv_substitutes);
        rvSubstitutes.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, true));
        completeFootballLineUpAdapter = new CompleteFootballLineUpAdapter(lineUpList ,getContext());
        rvLineup.setAdapter(completeFootballLineUpAdapter);
        completeFootballSubstituteUpAdapter = new CompleteFootballLineUpAdapter(substitutesList ,getContext());
        rvSubstitutes.setAdapter(completeFootballSubstituteUpAdapter);
        managerView = view.findViewById(R.id.manager_root);
        lineupView = view.findViewById(R.id.layout_line_up);
        subsView = view.findViewById(R.id.layout_substitutes);
    }

    private void startTimer() {
        cancelTimer();

        timerToRefreshContent = new Timer();
        timerToRefreshContent.schedule(new TimerTask() {
            @Override
            public void run() {
                getFootballmatchLineUps();
            }
        }, 0, Constants.TIMEINMILISECOND);
    }

    private void cancelTimer() {
        if (timerToRefreshContent != null) {
            timerToRefreshContent.cancel();
            timerToRefreshContent.purge();
            timerToRefreshContent = null;
        }
    }

    private void  showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void  hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void handleContent(String object) {
        {
            hideProgressBar();

            try {
                JSONObject jsonObject = new JSONObject(object);

                boolean success = jsonObject.getBoolean("success");
                boolean error = jsonObject.getBoolean("error");

                if( success ) {

                    renderDisplay(jsonObject);

                } else {
                    showErrorLayout(getView());
                }
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getActivity(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
                showErrorLayout(getView());
            }
        }
    }

    private void initErrorLayout(View view) {
        try {
            LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.GONE);
        }catch (Exception e){e.printStackTrace();}
    }

    private void showErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);
        managerView.setVisibility(View.GONE);
        lineupView.setVisibility(View.GONE);
        subsView.setVisibility(View.GONE);
    }

    private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        lineUpList.clear();
        substitutesList.clear();
        hideProgressBar();
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        final JSONObject dataObject = jsonObject.getJSONObject("data");
        final  JSONArray subsArray = dataObject.getJSONArray("subs");
        final JSONArray teamsObjectArray = dataObject.getJSONArray("teams");
        final JSONArray substitutionsArray = dataObject.getJSONArray("substitutions");
        final JSONArray matchEventsArray = dataObject.getJSONArray("match_events");
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        tvCaptainFirst.setText("NA");
                        tvCaptainSecond.setText("NA");

                        CompleteFootballLineUpDTO completeFootballLineUpDTO = null;
                        int length = subsArray.length();
                        int tempLength = length / 2;
                        for (int i = 0; i < length / 2; i++) {
                            try {
                                JSONObject teamFirstObject = subsArray.getJSONObject(i);
                                JSONObject teamSecondObject = subsArray.getJSONObject(tempLength);
                                completeFootballLineUpDTO = new CompleteFootballLineUpDTO();
                                setPlayerDetails(completeFootballLineUpDTO, teamSecondObject, substitutionsArray, matchEventsArray);
                                setSecondTeamDetails(completeFootballLineUpDTO, teamFirstObject, matchEventsArray, substitutionsArray);
                                substitutesList.add(completeFootballLineUpDTO);
                                tempLength++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        length = teamsObjectArray.length();
                        tempLength = length / 2;
                        for (int i = 0; i < length / 2; i++) {
                            try {
                                JSONObject teamFirstObject = teamsObjectArray.getJSONObject(i);
                                JSONObject teamSecondObject = teamsObjectArray.getJSONObject(tempLength);
                                completeFootballLineUpDTO = new CompleteFootballLineUpDTO();
                                setTeamFirstLineUps(completeFootballLineUpDTO, teamSecondObject, matchEventsArray, substitutionsArray);
                                setTeamSecondLineDetails(completeFootballLineUpDTO, teamFirstObject, matchEventsArray, substitutionsArray);
                                lineUpList.add(completeFootballLineUpDTO);
                                tempLength++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        completeFootballLineUpAdapter.notifyDataSetChanged();
                        completeFootballSubstituteUpAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

    private void setTeamSecondLineDetails(CompleteFootballLineUpDTO completeFootballLineUpDTO, JSONObject teamSecondObject, JSONArray matchEventsArray, JSONArray substitutionsArray) throws JSONException {
        completeFootballLineUpDTO.setPlayerNameSecond(teamSecondObject.getString("name"));
        completeFootballLineUpDTO.setPlayerPostionNumberSecond(teamSecondObject.getString("jersey_number"));
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
                    if(playerName.equalsIgnoreCase(substitutesObject.getString("player_off"))){
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

    private String getMatchEventNumber(JSONArray matchEventsArray ,String playerName) {
        String event = null;
        try{
            if(playerName != null){
                for(int i = 0;i<matchEventsArray.length();i++){
                    JSONObject eventObject = matchEventsArray.getJSONObject(i);
                    if(!eventObject.isNull("player_name")) {
                        if (playerName.equalsIgnoreCase(eventObject.getString("player_name"))) {
                            if (!eventObject.isNull("event")) {
                                event = eventObject.getString("event");
                            }
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return event;
    }

    private void getFootballmatchLineUps() {
        LiveFootballMatchLineUpHandler.getInstance(context).requestLiveMatchLineUp(matchId);
    }

    @Override
    public void onResume() {
        super.onResume();

        LiveFootballMatchLineUpHandler.getInstance(context).addListener(this);
        startTimer();
    }

    @Override
    public void onPause() {
        super.onPause();

        cancelTimer();
        LiveFootballMatchLineUpHandler.getInstance(context).addListener(null);
    }

}
