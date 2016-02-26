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
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM2_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchSqadFragment extends Fragment implements UpCommingFootballMatchSqadHandler.UpCommingFootballMatchSqadContentListener{

    private ProgressBar progressBar;
    String toss = "";
    String matchName="";
    String date = "";
    private String teamFirstId;
    private String teamSecondId;
    private RecyclerView rcRecyclerView;
    private UpCommingFootballMatchSquadAdapter upCommingFootballMatchSquadAdapter;
    private List<UpCommingFootballMatchSquadDTO> list = new ArrayList<UpCommingFootballMatchSquadDTO>();



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

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        rcRecyclerView = (RecyclerView) view.findViewById(R.id.child_rv);
        rcRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        upCommingFootballMatchSquadAdapter = new UpCommingFootballMatchSquadAdapter(list,getContext());
        rcRecyclerView.setAdapter(upCommingFootballMatchSquadAdapter);
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
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        final JSONArray dataArray = jsonObject.getJSONArray("data");

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        UpCommingFootballMatchSquadDTO dto;
                      for (int i = 0; i< dataArray.length();i++){
                          JSONObject playerObject = dataArray.getJSONObject(i);
                          dto = new UpCommingFootballMatchSquadDTO();
                          if(!playerObject.isNull("name")){
                              dto.setTvPlayerName(playerObject.getString("name"));
                          }
                          if(!playerObject.isNull("age")){
                              dto.setTvPlayerAge(playerObject.getString("age"));
                          }
                          if(!playerObject.isNull("position")){
                              dto.setTvP(playerObject.getString("position"));
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
                          list.add(dto);
                      }
                       upCommingFootballMatchSquadAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

}
