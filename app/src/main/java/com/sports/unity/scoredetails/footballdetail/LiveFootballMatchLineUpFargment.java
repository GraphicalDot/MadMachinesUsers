package com.sports.unity.scoredetails.footballdetail;

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
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
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
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class LiveFootballMatchLineUpFargment extends Fragment implements LiveFootballMatchLineUpHandler.LiveMatchContentListener {

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
    private RecyclerView rvLineup;
    private RecyclerView rvSubstitutes;
    private CompleteFootballLineUpAdapter completeFootballLineUpAdapter;
    private List<CompleteFootballLineUpDTO> lineUpList = new ArrayList<>();
    private CompleteFootballLineUpAdapter completeFootballSubstituteUpAdapter;
    private List<CompleteFootballLineUpDTO> substitutesList = new ArrayList<>();

    public LiveFootballMatchLineUpFargment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        String matchId =  i.getStringExtra(INTENT_KEY_ID);
        matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
        toss = i.getStringExtra(INTENT_KEY_TOSS);
        date = i.getStringExtra(INTENT_KEY_DATE);
        LiveFootballMatchLineUpHandler liveFootballMatchLineUpHandler = LiveFootballMatchLineUpHandler.getInstance(context);
        liveFootballMatchLineUpHandler.addListener(this);
        liveFootballMatchLineUpHandler.requestLiveMatchLineUp(matchId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        rvLineup.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        rvSubstitutes = (RecyclerView) view.findViewById(R.id.rv_substitutes);
        rvSubstitutes.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        rvSubstitutes.setNestedScrollingEnabled(false);
        completeFootballLineUpAdapter = new CompleteFootballLineUpAdapter(lineUpList ,getContext());
        rvLineup.setAdapter(completeFootballLineUpAdapter);
        rvLineup.setNestedScrollingEnabled(false);
        completeFootballSubstituteUpAdapter = new CompleteFootballLineUpAdapter(substitutesList ,getContext());
        rvSubstitutes.setAdapter(completeFootballSubstituteUpAdapter);


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
                JSONObject data = jsonObject.getJSONObject("data");
                boolean success = data.getBoolean("success");
                boolean error = data.getBoolean("error");

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


                        boolean first= true;
                        CompleteFootballLineUpDTO completeFootballLineUpDTO = new CompleteFootballLineUpDTO();;
                        for(int i = 0; i<subsArray.length();i++){
                            JSONObject subsObject = subsArray.getJSONObject(i);


                            if(first){
                                completeFootballLineUpDTO = new CompleteFootballLineUpDTO();

                                completeFootballLineUpDTO.setPlayerName(subsObject.getString("player_name"));
                                completeFootballLineUpDTO.setPlayerPostionNumber(subsObject.getString("jersey_number"));
                                completeFootballLineUpDTO.setEnterExitImage(getOnOffPlayer(substitutionsArray, subsObject.getString("player_name")));
                                String event = getMatchEventNumber(matchEventsArray, subsObject.getString("player_name"));
                                if(event!=null){
                                    if("goals".equalsIgnoreCase(event)){
                                        completeFootballLineUpDTO.setGoal(event);
                                    }else {
                                        completeFootballLineUpDTO.setCardType(event);
                                    }

                                }
                                String playerOnName= getOnOffPlayer(substitutionsArray, subsObject.getString("player_name"));
                                if(playerOnName!=null){
                                    completeFootballLineUpDTO.setEnterExitImage("OFF");
                                    completeFootballLineUpDTO.setOffEnterExitImage("ON");
                                    completeFootballLineUpDTO.setOffPlayerName(playerOnName);
                                }



                                first= false;
                            }else {
                                completeFootballLineUpDTO.setPlayerNameSecond(subsObject.getString("player_name"));
                                completeFootballLineUpDTO.setPlayerPostionNumberSecond(subsObject.getString("jersey_number"));
                                String event = getMatchEventNumber(matchEventsArray, subsObject.getString("player_name"));
                                if(event!=null){
                                    if("goals".equalsIgnoreCase(event)){
                                        completeFootballLineUpDTO.setGoal(event);
                                    }else {
                                        completeFootballLineUpDTO.setCardType(event);
                                    }

                                }
                                completeFootballLineUpDTO.setEnterExitImageSecond(getOnOffPlayer(substitutionsArray, subsObject.getString("player_name")));
                                String playerOnName= getOnOffPlayer(substitutionsArray, subsObject.getString("player_name"));
                                if(playerOnName!=null){
                                    completeFootballLineUpDTO.setEnterExitImage("OFF");
                                    completeFootballLineUpDTO.setOffEnterExitImage("ON");
                                    completeFootballLineUpDTO.setOffPlayerName(playerOnName);
                                }
                                first= true;
                            }
                            if(first){
                                substitutesList.add(completeFootballLineUpDTO);
                            }



                        }
                        first= true;
                        for(int i = 0; i<teamsObjectArray.length();i++){
                            JSONObject teamsObject = teamsObjectArray.getJSONObject(i);
                            if(first){
                                completeFootballLineUpDTO = new CompleteFootballLineUpDTO();
                                completeFootballLineUpDTO.setPlayerName(teamsObject.getString("name"));
                                completeFootballLineUpDTO.setPlayerPostionNumber(teamsObject.getString("jersey_number"));
                                String event = getMatchEventNumber(matchEventsArray, teamsObject.getString("name"));
                                if(event!=null){
                                    if("goals".equalsIgnoreCase(event)){
                                        completeFootballLineUpDTO.setGoal(event);
                                    }else {
                                        completeFootballLineUpDTO.setCardType(event);
                                    }

                                }
                                String playerOnName= getOnOffPlayer(substitutionsArray, teamsObject.getString("name"));
                                if(playerOnName!=null){
                                    completeFootballLineUpDTO.setEnterExitImage("OFF");
                                    completeFootballLineUpDTO.setOffEnterExitImage("ON");
                                    completeFootballLineUpDTO.setOffPlayerName(playerOnName);
                                }
                                first = false;
                            }else {
                                completeFootballLineUpDTO.setPlayerNameSecond(teamsObject.getString("name"));
                                completeFootballLineUpDTO.setPlayerPostionNumberSecond(teamsObject.getString("jersey_number"));
                                String event = getMatchEventNumber(matchEventsArray, teamsObject.getString("name"));
                                if(event!=null){
                                    if("goals".equalsIgnoreCase(event)){
                                        completeFootballLineUpDTO.setGoal(event);
                                    }else {
                                        completeFootballLineUpDTO.setCardType(event);
                                    }

                                }
                                completeFootballLineUpDTO.setEnterExitImageSecond(getOnOffPlayer(substitutionsArray, teamsObject.getString("name")));
                                String playerOnName = getOnOffPlayer(substitutionsArray, teamsObject.getString("name"));
                                if(playerOnName!=null){
                                    completeFootballLineUpDTO.setEnterExitImageSecond("OFF");
                                    completeFootballLineUpDTO.setOffEnterExitImageSecond("ON");
                                    completeFootballLineUpDTO.setOffPlayerNameSecond(playerOnName);
                                }
                                first= true;
                            }
                            if(first){
                                lineUpList.add(completeFootballLineUpDTO);
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
    private String getOnOffPlayer(JSONArray substitutionsArray, String playerName) {
        String playerOn= null;
        try{
            for(int i = 0;i<substitutionsArray.length();i++){
                JSONObject substitutesObject = substitutionsArray.getJSONObject(i);
                if(playerName.equalsIgnoreCase(substitutesObject.getString("player_off"))){
                    if(!substitutesObject.isNull("player_on")){
                        playerOn = substitutesObject.getString("player_on");
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
            for(int i = 0;i<matchEventsArray.length();i++){
                JSONObject eventObject = matchEventsArray.getJSONObject(i);
                if(playerName.equalsIgnoreCase(eventObject.getString("player_name"))){
                    if(!eventObject.isNull("event")){
                        event = eventObject.getString("event");
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return event;
    }

}
