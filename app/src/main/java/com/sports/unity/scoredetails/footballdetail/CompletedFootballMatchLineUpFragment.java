package com.sports.unity.scoredetails.footballdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballLineUpAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.CompleteFootballLineUpDTO;
import com.sports.unity.scores.ScoreDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class CompletedFootballMatchLineUpFragment extends Fragment implements CompletedFootballMatchLineUpHandler.CompletedMatchContentListener{


    private ProgressBar progressBar;
    String toss = "";
    String matchName="";
    String date = "";
    private String matchId;
    private TextView tvCaptainFirst;
    private TextView tvCaptainSecond;
    private TextView tvCarlesPayol;
    private TextView tvlineup;
    private GridLayout rcLineup;
    private TextView tvsubstitutes;
    private RecyclerView recyclerView;
    private RecyclerView rvLineup;
    private RecyclerView rvSubstitutes;
    private CompleteFootballLineUpAdapter completeFootballLineUpAdapter;
    private List<CompleteFootballLineUpDTO> lineUpList = new ArrayList<>();
    private CompleteFootballLineUpAdapter completeFootballSubstituteUpAdapter;
    private List<CompleteFootballLineUpDTO> substitutesList = new ArrayList<>();

    private CompletedFootballMatchLineUpHandler cricketUpcomingMatchSummaryHandler;

    private View manageRootView;
    private View layoutLineUpView;
    private View layoutSubstitutesView;
    private Context context;


    public CompletedFootballMatchLineUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);this.context= context;
        Intent i = getActivity().getIntent();
        matchId =  i.getStringExtra(INTENT_KEY_ID);
        matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
        toss = i.getStringExtra(INTENT_KEY_TOSS);
        date = i.getStringExtra(INTENT_KEY_DATE);
        cricketUpcomingMatchSummaryHandler = CompletedFootballMatchLineUpHandler.getInstance(context);
        cricketUpcomingMatchSummaryHandler.addListener(this);
        cricketUpcomingMatchSummaryHandler.requestCompletdMatchLineUps(matchId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_football_live_match_lineups, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {
        progressBar = (ProgressBar)view.findViewById(R.id.progress);
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
        rvSubstitutes.setNestedScrollingEnabled(false);
        completeFootballLineUpAdapter = new CompleteFootballLineUpAdapter(lineUpList ,getContext());
        rvLineup.setAdapter(completeFootballLineUpAdapter);
        rvLineup.setNestedScrollingEnabled(false);
        completeFootballSubstituteUpAdapter = new CompleteFootballLineUpAdapter(substitutesList ,getContext());
        rvSubstitutes.setAdapter(completeFootballSubstituteUpAdapter);
        manageRootView = view.findViewById(R.id.parent_layout);
        manageRootView.setVisibility(View.GONE);
        layoutLineUpView  = view.findViewById(R.id.layout_line_up);
        layoutLineUpView.setVisibility(View.GONE);
        layoutSubstitutesView = view.findViewById(R.id.layout_substitutes);
        layoutSubstitutesView.setVisibility(View.GONE);


   }
    private void  showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        manageRootView.setVisibility(View.GONE);
        layoutLineUpView.setVisibility(View.GONE);
        layoutSubstitutesView.setVisibility(View.GONE);
    }
    private void  hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void handleContent(String object) {
        {
            showProgressBar();

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
        manageRootView.setVisibility(View.GONE);
        layoutLineUpView.setVisibility(View.GONE);
        layoutSubstitutesView.setVisibility(View.GONE);
        hideProgressBar();

    }

    private void renderDisplay(final JSONObject jsonObject) throws JSONException {

        lineUpList.clear();
        substitutesList.clear();


        Activity activity =  getActivity();
        if(!jsonObject.isNull("data")) {
            final JSONObject dataObject = jsonObject.getJSONObject("data");
            final JSONArray subsArray = dataObject.getJSONArray("subs");
            final JSONArray teamsObjectArray = dataObject.getJSONArray("teams");
            final JSONArray substitutionsArray = dataObject.getJSONArray("substitutions");
            final JSONArray matchEventsArray = dataObject.getJSONArray("match_events");
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            manageRootView.setVisibility(View.VISIBLE);
                            layoutLineUpView.setVisibility(View.VISIBLE);
                            layoutSubstitutesView.setVisibility(View.VISIBLE);
                            hideProgressBar();
                            tvCaptainFirst.setText("N/A");
                            tvCaptainSecond.setText("N/A");

                            CompleteFootballLineUpDTO completeFootballLineUpDTO = null;
                            int length = subsArray.length();
                            int tempLength = length/2;
                            for (int i = 0; i < length/2; i++) {
                                try {
                                    JSONObject teamSecondObject  = subsArray.getJSONObject(i);
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
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showErrorLayout(getView());
                        }
                    }
                });
            }
        }else {
            showErrorLayout(getView());
        }
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


    @Override
    public void onResume() {
        super.onResume();
        if (cricketUpcomingMatchSummaryHandler != null) {
            cricketUpcomingMatchSummaryHandler.addListener(this);
        } else {
            cricketUpcomingMatchSummaryHandler = CompletedFootballMatchLineUpHandler.getInstance(context);
            cricketUpcomingMatchSummaryHandler.addListener(this);
        }
        cricketUpcomingMatchSummaryHandler.requestCompletdMatchLineUps(matchId);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cricketUpcomingMatchSummaryHandler != null)
            cricketUpcomingMatchSummaryHandler = null;

    }


}
