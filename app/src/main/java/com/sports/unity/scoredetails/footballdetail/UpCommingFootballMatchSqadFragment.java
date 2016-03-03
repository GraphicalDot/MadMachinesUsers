package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommingFootballMatchSquadAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommingFootballMatchSquadDTO;
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
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM1_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM1_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM2_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM2_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchSqadFragment extends Fragment implements UpCommingFootballMatchSqadHandler.UpCommingFootballMatchSqadContentListener{

    String toss = "";
    String matchName="";
    String date = "";
    private ProgressBar progressBar;
    private String teamFirstName;
    private String teamSecondName;
    private String teamFirstId;
    private String teamSecondId;
    private RecyclerView rcRecyclerViewTeamFirst;
    private RecyclerView rcRecyclerViewTeamSecond;
    private UpCommingFootballMatchSquadAdapter upCommingFootballMatchSquadAdapterFirst;
    private List<UpCommingFootballMatchSquadDTO> listTeamFirst = new ArrayList<UpCommingFootballMatchSquadDTO>();
    private UpCommingFootballMatchSquadAdapter upCommingFootballMatchSquadAdapterSecond;
    private List<UpCommingFootballMatchSquadDTO> listTeamSecond = new ArrayList<UpCommingFootballMatchSquadDTO>();
    private TextView tvTeamFirst;
    private TextView tvTeamSecond;
    private LinearLayout squadParnetLinearLayout;



    public UpCommingFootballMatchSqadFragment() {
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
        teamFirstId = i.getStringExtra(INTENT_KEY_TEAM1_ID);
        teamSecondId=i.getStringExtra(INTENT_KEY_TEAM2_ID);
        teamFirstName = i.getStringExtra(INTENT_KEY_TEAM1_NAME);
        teamSecondName = i.getStringExtra(INTENT_KEY_TEAM2_NAME);

        UpCommingFootballMatchSqadHandler liveFootballMatchTimeLineHandler = UpCommingFootballMatchSqadHandler.getInstance(context);
        liveFootballMatchTimeLineHandler.addListener(this);
        liveFootballMatchTimeLineHandler.requestUpCommingMatchSquad(teamFirstId,teamSecondId);
        }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_football_match_squard_wrapper, container, false);
        initView(view);
        showProgressBar();
        return view;
    }
    private void initView(View view) {
        tvTeamFirst = (TextView) view.findViewById(R.id.tv_team_first_name);
        tvTeamSecond = (TextView) view.findViewById(R.id.tv_team_second_name);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        rcRecyclerViewTeamFirst = (RecyclerView) view.findViewById(R.id.rc_child1_rv);
        rcRecyclerViewTeamFirst.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        rcRecyclerViewTeamFirst.setNestedScrollingEnabled(false);
        rcRecyclerViewTeamSecond = (RecyclerView) view.findViewById(R.id.rc_child2_rv);
        rcRecyclerViewTeamSecond.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        rcRecyclerViewTeamSecond.setNestedScrollingEnabled(false);
        upCommingFootballMatchSquadAdapterFirst = new UpCommingFootballMatchSquadAdapter(listTeamFirst,getContext());
        rcRecyclerViewTeamFirst.setAdapter(upCommingFootballMatchSquadAdapterFirst);
        upCommingFootballMatchSquadAdapterSecond = new UpCommingFootballMatchSquadAdapter(listTeamFirst,getContext());
        rcRecyclerViewTeamSecond.setAdapter(upCommingFootballMatchSquadAdapterSecond);
        squadParnetLinearLayout = (LinearLayout) view.findViewById(R.id.squad_parnet_linearlayout);
        initErrorLayout(view);

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

    }

    private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        hideProgressBar();
        squadParnetLinearLayout.setVisibility(View.VISIBLE);
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
         JSONObject dataObject = jsonObject.getJSONObject("data");
         final JSONArray teamFirstSquadArray = dataObject.getJSONArray("team_1_squad");
         final JSONArray teamSecondSquadArray = dataObject.getJSONArray("team_2_squad");
           if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tvTeamFirst.setText(teamFirstName);
                        tvTeamSecond.setText(teamSecondName);
                        UpCommingFootballMatchSquadDTO dto;

                     for (int i = 0; i< teamFirstSquadArray.length();i++){
                          JSONObject playerObject = teamFirstSquadArray.getJSONObject(i);
                          dto = new UpCommingFootballMatchSquadDTO();
                          getSquadDetails(dto, playerObject);
                          listTeamFirst.add(dto);
                      }
                        for (int i = 0; i< teamSecondSquadArray.length();i++){
                            JSONObject playerObject = teamSecondSquadArray.getJSONObject(i);
                            dto = new UpCommingFootballMatchSquadDTO();
                            getSquadDetails(dto, playerObject);
                            listTeamSecond.add(dto);
                        }
                        upCommingFootballMatchSquadAdapterFirst.notifyDataSetChanged();
                        upCommingFootballMatchSquadAdapterSecond.notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

    private void getSquadDetails(UpCommingFootballMatchSquadDTO dto, JSONObject playerObject) throws JSONException {
        if(!playerObject.isNull("name")){
            dto.setTvPlayerName(playerObject.getString("name"));
        }
        if(!playerObject.isNull("age")){
            dto.setTvPlayerAge("("+playerObject.getString("age")+")");
        }
        if(!playerObject.isNull("position")){
            dto.setTvP(playerObject.getString("position").substring(0,1));
        }
        if(!playerObject.isNull("games_played")){
            dto.setTvpl(playerObject.getString("games_played"));
        }
        if(!playerObject.isNull("goals")){
            dto.setTvgol(playerObject.getString("goals"));
        }
        if(!playerObject.isNull("red_cards")){
            dto.setTvredcard(playerObject.getString("red_cards"));
        }
        if(!playerObject.isNull("yellow_cards")){
            dto.setTvyellowcard(playerObject.getString("yellow_cards"));
        }
    }

}
