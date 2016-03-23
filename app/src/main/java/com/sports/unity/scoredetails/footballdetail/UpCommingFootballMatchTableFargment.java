package com.sports.unity.scoredetails.footballdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.scoredetails.cricketdetail.CricketLiveMatchSummaryHandler;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommingFootballMatchTableAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommngFootbalMatchTableDTO;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM1_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TEAM2_NAME;

/**
 * Created by madmachines on 23/2/16.
 */
public class UpCommingFootballMatchTableFargment extends Fragment implements UpCommingFootballMatchTableHandler.UpCommingFootballMatchTableContentListener{




    private String date = "";
    private String matchId ="";
    private String leagueId = "";
    private String team1;
    private String team2;
    private UpCommingFootballMatchTableAdapter adapter;
    private List<UpCommngFootbalMatchTableDTO> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView tvMatchDate;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UpCommingFootballMatchTableHandler upCommingFootballMatchTableHandler;
    private View llTeamSummary;
    private ProgressBar progressBar;
    private Context context;

    public UpCommingFootballMatchTableFargment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Intent i = getActivity().getIntent();
        matchId = i.getStringExtra(INTENT_KEY_ID);
        leagueId = i.getStringExtra(Constants.INTENT_KEY_LEAGUE_ID);
        date = i.getStringExtra(INTENT_KEY_DATE);
        team1 = i.getStringExtra(INTENT_KEY_TEAM1_NAME);
        team2 = i.getStringExtra(INTENT_KEY_TEAM2_NAME);
        upCommingFootballMatchTableHandler = UpCommingFootballMatchTableHandler.getInstance(context);
        upCommingFootballMatchTableHandler.addListener(this);
        upCommingFootballMatchTableHandler.requestUpcommingMatchTableContent(leagueId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_football_match_table, container, false);
        initView(view);

        return view;
    }
    private void initView(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        tvMatchDate = (TextView) view.findViewById(R.id.tv_match_date);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.sv_swipe_football_match_table);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_football_match_table);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        adapter = new UpCommingFootballMatchTableAdapter(list, getContext(),team1,team2);
        recyclerView.setAdapter(adapter);
        initErrorLayout(view);
        llTeamSummary = view.findViewById(R.id.sv_football_match_table);
        llTeamSummary.setVisibility(View.GONE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (upCommingFootballMatchTableHandler != null) {
                    upCommingFootballMatchTableHandler.requestUpcommingMatchTableContent(leagueId);
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });

    }
    @Override
    public void handleContent(String object) {
        {
           try {
               showProgressBar();
                JSONObject jsonObject = new JSONObject(object);
                boolean success = jsonObject.getBoolean("success");
                boolean error = jsonObject.getBoolean("error");

                if( success ) {

                    renderDisplay(jsonObject);

                } else {
                    llTeamSummary.setVisibility(View.VISIBLE);
                    hideProgressBar();
                    showErrorLayout(getView());
                }
            }catch (Exception ex){
                ex.printStackTrace();
               llTeamSummary.setVisibility(View.VISIBLE);
               hideProgressBar();
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
        PercentRelativeLayout percentRelativeLayout=(PercentRelativeLayout)view.findViewById(R.id.pl_team_table);
        percentRelativeLayout.setVisibility(View.INVISIBLE);
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);
    }
    private void  showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void  hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }
    private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        Activity activity =  getActivity();
        final JSONArray dataArray = jsonObject.getJSONArray("data");
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        list.clear();
        hideProgressBar();
        llTeamSummary.setVisibility(View.VISIBLE);
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        UpCommngFootbalMatchTableDTO upCommngFootbalMatchTableDTO = null;
                        for (int i = 0;i< dataArray.length();i++){
                            upCommngFootbalMatchTableDTO = new UpCommngFootbalMatchTableDTO();
                            JSONObject teamObject = dataArray.getJSONObject(i);
                            if(!teamObject.isNull("stand_season"))
                                tvMatchDate.setText(teamObject.getString("stand_season"));
                            if(!teamObject.isNull("flag_image"))
                                upCommngFootbalMatchTableDTO.setIvTeamProfileImage(teamObject.getString("flag_image"));
                            if(!teamObject.isNull("team_name"))
                            {
                                upCommngFootbalMatchTableDTO.setTvTeamName(teamObject.getString("team_name"));

                            }
                            if(!teamObject.isNull("games_drawn"))
                                upCommngFootbalMatchTableDTO.setTvD(teamObject.getString("games_drawn"));
                            if(!teamObject.isNull("games_lost"))
                                upCommngFootbalMatchTableDTO.setTvL(teamObject.getString("games_lost"));
                            if(!teamObject.isNull("games_played"))
                                upCommngFootbalMatchTableDTO.setTvP(teamObject.getString("games_played"));
                            if(!teamObject.isNull("games_won"))
                                upCommngFootbalMatchTableDTO.setTvW(teamObject.getString("games_won"));
                            if(!teamObject.isNull("team_points"))
                                upCommngFootbalMatchTableDTO.setTvPts(teamObject.getString("team_points"));
                            list.add(upCommngFootbalMatchTableDTO);

                        }

                        adapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(upCommingFootballMatchTableHandler != null){
            upCommingFootballMatchTableHandler.addListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressBar();
        if(upCommingFootballMatchTableHandler != null){
            upCommingFootballMatchTableHandler.addListener(this);

        }else {
            upCommingFootballMatchTableHandler = UpCommingFootballMatchTableHandler.getInstance(context);
        }
        upCommingFootballMatchTableHandler.requestUpcommingMatchTableContent(leagueId);
    }
}
