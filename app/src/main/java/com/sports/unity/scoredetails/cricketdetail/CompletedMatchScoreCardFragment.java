package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import org.solovyev.android.views.llm.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.sports.unity.R;
import com.sports.unity.scoredetails.cricketdetail.JsonParsers.CricketMatchScoreJsonParser;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBattingCardAdapter;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBattingCardDTO;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBowlingCardAdapter;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBowlingCardDTO;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketFallOfWicketAdapter;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketFallOfWicketCardDTO;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;


public class CompletedMatchScoreCardFragment extends Fragment implements CompletedMatchScoreCardHandler.CompletedMatchContentListener{

    private TextView tvFirstTeamInning;
    private TextView tvSecondTeamInning;
    private ImageView ivDwn;
    private TextView tvTeamFirstNameAndScore;
    private TextView tvFirstTeamOver;
    private TextView tvExtraRunTeamFirst;
    private TextView tvTotalRunFirstTeam;
    private TextView tvRunRateFirstTeam;
    private ImageView ivDwnSecond;
    private TextView tvTeamSecondNameAndScore;
    private TextView tvSecondTeamOver;
    private TextView tvExtraRunTeamSecond;
    private TextView tvTotalRunSecondTeam;
    private TextView tvRunRateSecondTeam;
    private LiveAndCompletedCricketBattingCardAdapter teamABattingAdapter;
    private LiveAndCompletedCricketBattingCardAdapter teamBBattingAdapter;
    private LiveAndCompletedCricketBowlingCardAdapter teamABowlingAdapter;
    private LiveAndCompletedCricketBowlingCardAdapter teamBBowlingAdapter;
    private LiveAndCompletedCricketFallOfWicketAdapter teamAFallOfWicketAdapter;
    private LiveAndCompletedCricketFallOfWicketAdapter teamBFallOfWicketAdapter;
    private List<LiveAndCompletedCricketBattingCardDTO> teamABattingCardList = new ArrayList<>();
    private List<LiveAndCompletedCricketBattingCardDTO> teamBBattingCardList = new ArrayList<>();
    private List<LiveAndCompletedCricketBowlingCardDTO> teamABowlingCardList = new ArrayList<>();
    private List<LiveAndCompletedCricketBowlingCardDTO> teamBBowlingCardList = new ArrayList<>();
    private List<LiveAndCompletedCricketFallOfWicketCardDTO> teamAFallOfWicketCardList = new ArrayList<>();
    private List<LiveAndCompletedCricketFallOfWicketCardDTO> teamBFallOfWicketCardList = new ArrayList<>();
    private RecyclerView teamABattingRecycler;
    private RecyclerView teamBBattingRecycler;
    private RecyclerView teamABowlingRecycler;
    private RecyclerView teamBBowlingRecycler;
    private RecyclerView teamAFallOfWicketRecycler;
    private RecyclerView teamBFallOfWicketRecycler;
    private LinearLayout linearLayout;
    private LinearLayout firstBattingLinearLayout;
    private LinearLayout firstBowlingLinearLayout;
    private LinearLayout firstFallofWicketsLinearLayout;
    private LinearLayout secondBattingLinearLayout;
    private LinearLayout secondBowlingLinearLayout;
    private LinearLayout secondFallofWicketsLinearLayout;
    private ProgressBar progressBar;
   private  CompletedMatchScoreCardHandler completedMatchScoreCardHandler;
    private String matchId;
    private RelativeLayout team1ScoreDetails;
    private RelativeLayout team2ScoreDetails;
    public CompletedMatchScoreCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         matchId =  getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);

        completedMatchScoreCardHandler = CompletedMatchScoreCardHandler.getInstance(context);
        completedMatchScoreCardHandler.addListener(this);
        completedMatchScoreCardHandler.requestCompletdMatchScoreCard(matchId);
      }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_completed_match_score_card, container, false);
        initView(view);
        initProgress(view);
        return view;
    }

    private void initView(View view) {
        firstBattingLinearLayout = (LinearLayout) view.findViewById(R.id.ll_first_view_visibility);
        firstBowlingLinearLayout = (LinearLayout) view.findViewById(R.id.ll_first_bowling_visibility);
        firstFallofWicketsLinearLayout = (LinearLayout) view.findViewById(R.id.first_layout_fall_wicket);
        secondBattingLinearLayout = (LinearLayout) view.findViewById(R.id.ll_batting_second);
        secondBowlingLinearLayout = (LinearLayout) view.findViewById(R.id.second_bowling_layout);
        secondFallofWicketsLinearLayout = (LinearLayout) view.findViewById(R.id.layout_fall_wicket_second);

        linearLayout = (LinearLayout) view.findViewById(R.id.scorecard_parent_layout);
        linearLayout.setVisibility(View.GONE);
        tvFirstTeamInning = (TextView) view.findViewById(R.id.tv_first_team_inning);
        tvSecondTeamInning = (TextView) view.findViewById(R.id.tv_Second_team_inning);
        ivDwn = (ImageView) view.findViewById(R.id.iv_down);
        tvTeamFirstNameAndScore = (TextView) view.findViewById(R.id.tv_team_first_name);
        tvFirstTeamOver = (TextView) view.findViewById(R.id.tv_match_over);
        tvExtraRunTeamFirst = (TextView) view.findViewById(R.id.tv_extra_run_team_first);
        tvTotalRunFirstTeam = (TextView) view.findViewById(R.id.tv_total_run_first_team);
        tvRunRateFirstTeam = (TextView) view.findViewById(R.id.tv_run_rate_first_team);
        ivDwnSecond = (ImageView) view.findViewById(R.id.iv_down_second);
        tvTeamSecondNameAndScore = (TextView) view.findViewById(R.id.tv_team_second_name);
        tvSecondTeamOver = (TextView) view.findViewById(R.id.tv_match_over_second_team);
        tvExtraRunTeamSecond = (TextView) view.findViewById(R.id.tv_extra_run_team_second);
        tvTotalRunSecondTeam = (TextView) view.findViewById(R.id.tv_total_run_second_team);
        tvRunRateSecondTeam = (TextView) view.findViewById(R.id.tv_run_rate_second_team);
        teamABattingRecycler = (RecyclerView) view.findViewById(R.id.rv_team_first_batting);
        teamABattingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        teamABowlingRecycler = (RecyclerView) view.findViewById(R.id.rv_team_first_bowling);
        teamABowlingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        teamAFallOfWicketRecycler = (RecyclerView) view.findViewById(R.id.rv_team_first_fall_wickets);
        teamAFallOfWicketRecycler.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        teamBBattingRecycler = (RecyclerView) view.findViewById(R.id.rv_team_second_batting);
        teamBBattingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        teamBBowlingRecycler = (RecyclerView) view.findViewById(R.id.rv_team_second_bowling);
        teamBBowlingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        teamBFallOfWicketRecycler = (RecyclerView) view.findViewById(R.id.rv_second_team_fall_wicket);
        teamBFallOfWicketRecycler.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        teamABattingAdapter = new LiveAndCompletedCricketBattingCardAdapter(teamABattingCardList);
        teamABattingRecycler.setAdapter(teamABattingAdapter);
        teamABattingRecycler.setNestedScrollingEnabled(false);
        teamBBattingAdapter = new LiveAndCompletedCricketBattingCardAdapter(teamBBattingCardList);
        teamBBattingRecycler.setAdapter(teamBBattingAdapter);
        teamBBattingRecycler.setNestedScrollingEnabled(false);
        teamABowlingAdapter = new LiveAndCompletedCricketBowlingCardAdapter(teamABowlingCardList);
        teamABowlingRecycler.setAdapter(teamABowlingAdapter);
        teamABowlingRecycler.setNestedScrollingEnabled(false);
        teamBBowlingAdapter = new LiveAndCompletedCricketBowlingCardAdapter(teamBBowlingCardList);
        teamBBowlingRecycler.setAdapter(teamBBowlingAdapter);
        teamAFallOfWicketAdapter = new LiveAndCompletedCricketFallOfWicketAdapter(teamAFallOfWicketCardList);
        teamAFallOfWicketRecycler.setAdapter(teamAFallOfWicketAdapter);
        teamAFallOfWicketRecycler.setNestedScrollingEnabled(false);
        teamBFallOfWicketAdapter = new LiveAndCompletedCricketFallOfWicketAdapter(teamBFallOfWicketCardList);
        teamBFallOfWicketRecycler.setAdapter(teamBFallOfWicketAdapter);
        teamBFallOfWicketRecycler.setNestedScrollingEnabled(false);
        initErrorLayout(view);

        team1ScoreDetails = (RelativeLayout) view.findViewById(R.id.team1_scroll_details);
        team2ScoreDetails = (RelativeLayout) view.findViewById(R.id.team2_scroll_details);
        ivDwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowHideTeamFirstScoreCard();
            }
        });
        ivDwnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showhideTeamSecondScoreCard();
            }
        });

        team1ScoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    ShowHideTeamFirstScoreCard();
                }
            }
        });

        team2ScoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showhideTeamSecondScoreCard();
            }
        });

  }

    private void showhideTeamSecondScoreCard() {
        if (secondBattingLinearLayout.getVisibility() == View.GONE) {
            secondBattingLinearLayout.setVisibility(View.VISIBLE);
            secondBowlingLinearLayout.setVisibility(View.VISIBLE);
            secondFallofWicketsLinearLayout.setVisibility(View.VISIBLE);
            ivDwnSecond.setImageResource(R.drawable.ic_down_arrow_gray);
        } else {
            secondBattingLinearLayout.setVisibility(View.GONE);
            secondBowlingLinearLayout.setVisibility(View.GONE);
            secondFallofWicketsLinearLayout.setVisibility(View.GONE);
            ivDwnSecond.setImageResource(R.drawable.ic_up_arrow_gray);
        }
    }

    private void ShowHideTeamFirstScoreCard() {
        if (firstBattingLinearLayout.getVisibility() == View.GONE) {
            firstBattingLinearLayout.setVisibility(View.VISIBLE);
            firstBowlingLinearLayout.setVisibility(View.VISIBLE);
            firstFallofWicketsLinearLayout.setVisibility(View.VISIBLE);
             ivDwn.setImageResource(R.drawable.ic_down_arrow_gray);
        } else {
            firstBattingLinearLayout.setVisibility(View.GONE);
            firstBowlingLinearLayout.setVisibility(View.GONE);
            firstFallofWicketsLinearLayout.setVisibility(View.GONE);
            ivDwn.setImageResource(R.drawable.ic_up_arrow_gray);
        }
    }

    private void initProgress(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);

    }
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

    }
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);

    }
    @Override
    public void handleContent(String content) {
        {
content = "{\n" +
        "  \"data\": [\n" +
        "    {\n" +
        "      \"away_team\": \"South Africa\",\n" +
        "      \"home_team\": \"Afghanistan\",\n" +
        "      \"match_id\": \"20\",\n" +
        "      \"match_name\": \"Match 20\",\n" +
        "      \"match_time\": 1458466200,\n" +
        "      \"result\": \"Live: AFG 4/132 (14.0) Fol. SAF 5/209 (20.0)\",\n" +
        "      \"scorecard\": {\n" +
        "        \"1\": {\n" +
        "          \"1st Inn SAF\": {\n" +
        "            \"batting\": [\n" +
        "              {\n" +
        "                \"balls\": \"31\",\n" +
        "                \"batsman_id\": \"15092\",\n" +
        "                \"batsman_name\": \"Q de Kock\",\n" +
        "                \"four\": \"6\",\n" +
        "                \"how_out\": \"c: Shahzad b: Hamza Hotak\",\n" +
        "                \"runs\": \"45\",\n" +
        "                \"six\": \"2\",\n" +
        "                \"strike_rate\": \"145.16\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"5\",\n" +
        "                \"batsman_id\": \"3340\",\n" +
        "                \"batsman_name\": \"HM Amla\",\n" +
        "                \"four\": \"1\",\n" +
        "                \"how_out\": \"c: Stanikzai b: Zadran\",\n" +
        "                \"runs\": \"5\",\n" +
        "                \"six\": \"0\",\n" +
        "                \"strike_rate\": \"100.00\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"27\",\n" +
        "                \"batsman_id\": \"7531\",\n" +
        "                \"batsman_name\": \"F du Plessis\",\n" +
        "                \"four\": \"7\",\n" +
        "                \"how_out\": \"run out (Nabi)\",\n" +
        "                \"runs\": \"41\",\n" +
        "                \"six\": \"1\",\n" +
        "                \"strike_rate\": \"151.85\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"29\",\n" +
        "                \"batsman_id\": \"3352\",\n" +
        "                \"batsman_name\": \"AB de Villiers\",\n" +
        "                \"four\": \"4\",\n" +
        "                \"how_out\": \"c: Ali Zadran b: Nabi\",\n" +
        "                \"runs\": \"64\",\n" +
        "                \"six\": \"5\",\n" +
        "                \"strike_rate\": \"220.69\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"20\",\n" +
        "                \"batsman_id\": \"3314\",\n" +
        "                \"batsman_name\": \"JP Duminy\",\n" +
        "                \"four\": \"2\",\n" +
        "                \"how_out\": \"not out\",\n" +
        "                \"runs\": \"29\",\n" +
        "                \"six\": \"1\",\n" +
        "                \"strike_rate\": \"145.00\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"8\",\n" +
        "                \"batsman_id\": \"7524\",\n" +
        "                \"batsman_name\": \"DA Miller\",\n" +
        "                \"four\": \"2\",\n" +
        "                \"how_out\": \"c: Naib b: Zadran\",\n" +
        "                \"runs\": \"19\",\n" +
        "                \"six\": \"1\",\n" +
        "                \"strike_rate\": \"237.50\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"0\",\n" +
        "                \"batsman_id\": \"15239\",\n" +
        "                \"batsman_name\": \"D Wiese\",\n" +
        "                \"four\": \"0\",\n" +
        "                \"how_out\": \"not out\",\n" +
        "                \"runs\": \"0\",\n" +
        "                \"six\": \"0\",\n" +
        "                \"strike_rate\": \"\"\n" +
        "              }\n" +
        "            ],\n" +
        "            \"bowling\": [\n" +
        "              {\n" +
        "                \"bowler_id\": \"14477\",\n" +
        "                \"bowler_name\": \"Amir Hamza\",\n" +
        "                \"economy\": \"8.33\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"3.0\",\n" +
        "                \"runs\": \"25\",\n" +
        "                \"strike_rate\": \"18.0\",\n" +
        "                \"wickets\": \"1\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"bowler_id\": \"14475\",\n" +
        "                \"bowler_name\": \"Dawlat Zadran\",\n" +
        "                \"economy\": \"15.33\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"3.0\",\n" +
        "                \"runs\": \"46\",\n" +
        "                \"strike_rate\": \"18.0\",\n" +
        "                \"wickets\": \"1\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"bowler_id\": \"7802\",\n" +
        "                \"bowler_name\": \"Shapoor Zadran\",\n" +
        "                \"economy\": \"9.33\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"3.0\",\n" +
        "                \"runs\": \"28\",\n" +
        "                \"strike_rate\": \"18.0\",\n" +
        "                \"wickets\": \"1\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"bowler_id\": \"7796\",\n" +
        "                \"bowler_name\": \"Mohammad Nabi\",\n" +
        "                \"economy\": \"8.75\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"4.0\",\n" +
        "                \"runs\": \"35\",\n" +
        "                \"strike_rate\": \"24.0\",\n" +
        "                \"wickets\": \"1\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"bowler_id\": \"16588\",\n" +
        "                \"bowler_name\": \"Rashid Khan\",\n" +
        "                \"economy\": \"12.75\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"4.0\",\n" +
        "                \"runs\": \"51\",\n" +
        "                \"strike_rate\": \"\",\n" +
        "                \"wickets\": \"0\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"bowler_id\": \"7800\",\n" +
        "                \"bowler_name\": \"Samiullah Shenwari\",\n" +
        "                \"economy\": \"7.33\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"3.0\",\n" +
        "                \"runs\": \"22\",\n" +
        "                \"strike_rate\": \"\",\n" +
        "                \"wickets\": \"0\"\n" +
        "              }\n" +
        "            ],\n" +
        "            \"bye\": \"0\",\n" +
        "            \"did_not_bat\": [\n" +
        "              {\n" +
        "                \"name\": \"CH Morris\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"name\": \"KJ Abbott\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"name\": \"K Rabada\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"name\": \"Imran Tahir\"\n" +
        "              }\n" +
        "            ],\n" +
        "            \"extra\": \"6\",\n" +
        "            \"fall_of_wickets\": [\n" +
        "              {\n" +
        "                \"fow_order\": 1,\n" +
        "                \"fow_over\": \"2.4\",\n" +
        "                \"fow_score\": \"1-25\",\n" +
        "                \"name\": \"HM Amla\",\n" +
        "                \"runs\": \"5\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"fow_order\": 2,\n" +
        "                \"fow_over\": \"9.4\",\n" +
        "                \"fow_score\": \"2-90\",\n" +
        "                \"name\": \"F du Plessis\",\n" +
        "                \"runs\": \"41\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"fow_order\": 3,\n" +
        "                \"fow_over\": \"11.4\",\n" +
        "                \"fow_score\": \"3-97\",\n" +
        "                \"name\": \"Q de Kock\",\n" +
        "                \"runs\": \"45\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"fow_order\": 4,\n" +
        "                \"fow_over\": \"17.3\",\n" +
        "                \"fow_score\": \"4-173\",\n" +
        "                \"name\": \"AB de Villiers\",\n" +
        "                \"runs\": \"64\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"fow_order\": 5,\n" +
        "                \"fow_over\": \"19.5\",\n" +
        "                \"fow_score\": \"5-203\",\n" +
        "                \"name\": \"DA Miller\",\n" +
        "                \"runs\": \"19\"\n" +
        "              }\n" +
        "            ],\n" +
        "            \"legbye\": \"2\",\n" +
        "            \"noball\": \"0\",\n" +
        "            \"overs\": \"20.0\",\n" +
        "            \"required_runrate\": \"\",\n" +
        "            \"run_rate\": \"10.45\",\n" +
        "            \"runs\": \"209\",\n" +
        "            \"wickets\": \"5\",\n" +
        "            \"wide\": \"4\"\n" +
        "          }\n" +
        "        },\n" +
        "        \"2\": {\n" +
        "          \"1st Inn AFG\": {\n" +
        "            \"batting\": [\n" +
        "              {\n" +
        "                \"balls\": \"19\",\n" +
        "                \"batsman_id\": \"8031\",\n" +
        "                \"batsman_name\": \"Mohammad Shahzad\",\n" +
        "                \"four\": \"3\",\n" +
        "                \"how_out\": \"b: Morris\",\n" +
        "                \"runs\": \"44\",\n" +
        "                \"six\": \"5\",\n" +
        "                \"strike_rate\": \"231.58\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"24\",\n" +
        "                \"batsman_id\": \"7798\",\n" +
        "                \"batsman_name\": \"Noor Ali Zadran\",\n" +
        "                \"four\": \"1\",\n" +
        "                \"how_out\": \"st: de Kock b: Tahir\",\n" +
        "                \"runs\": \"25\",\n" +
        "                \"six\": \"1\",\n" +
        "                \"strike_rate\": \"104.17\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"6\",\n" +
        "                \"batsman_id\": \"7790\",\n" +
        "                \"batsman_name\": \"Asghar Stanikzai\",\n" +
        "                \"four\": \"0\",\n" +
        "                \"how_out\": \"c: de Kock b: Morris\",\n" +
        "                \"runs\": \"7\",\n" +
        "                \"six\": \"1\",\n" +
        "                \"strike_rate\": \"116.67\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"18\",\n" +
        "                \"batsman_id\": \"14476\",\n" +
        "                \"batsman_name\": \"Gulbadin Naib\",\n" +
        "                \"four\": \"3\",\n" +
        "                \"how_out\": \"c: de Kock b: Abbott\",\n" +
        "                \"runs\": \"26\",\n" +
        "                \"six\": \"1\",\n" +
        "                \"strike_rate\": \"144.44\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"11\",\n" +
        "                \"batsman_id\": \"7796\",\n" +
        "                \"batsman_name\": \"Mohammad Nabi\",\n" +
        "                \"four\": \"1\",\n" +
        "                \"how_out\": \"not out\",\n" +
        "                \"runs\": \"10\",\n" +
        "                \"six\": \"0\",\n" +
        "                \"strike_rate\": \"90.91\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"balls\": \"9\",\n" +
        "                \"batsman_id\": \"7800\",\n" +
        "                \"batsman_name\": \"Samiullah Shenwari\",\n" +
        "                \"four\": \"3\",\n" +
        "                \"how_out\": \"not out\",\n" +
        "                \"runs\": \"20\",\n" +
        "                \"six\": \"0\",\n" +
        "                \"strike_rate\": \"222.22\"\n" +
        "              }\n" +
        "            ],\n" +
        "            \"bowling\": [\n" +
        "              {\n" +
        "                \"bowler_id\": \"15607\",\n" +
        "                \"bowler_name\": \"K Rabada\",\n" +
        "                \"economy\": \"12.00\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"2.0\",\n" +
        "                \"runs\": \"24\",\n" +
        "                \"strike_rate\": \"\",\n" +
        "                \"wickets\": \"0\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"bowler_id\": \"14455\",\n" +
        "                \"bowler_name\": \"KJ Abbott\",\n" +
        "                \"economy\": \"11.00\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"3.0\",\n" +
        "                \"runs\": \"33\",\n" +
        "                \"strike_rate\": \"18.0\",\n" +
        "                \"wickets\": \"1\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"bowler_id\": \"15003\",\n" +
        "                \"bowler_name\": \"CH Morris\",\n" +
        "                \"economy\": \"5.67\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"3.0\",\n" +
        "                \"runs\": \"17\",\n" +
        "                \"strike_rate\": \"9.0\",\n" +
        "                \"wickets\": \"2\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"bowler_id\": \"7532\",\n" +
        "                \"bowler_name\": \"Imran Tahir\",\n" +
        "                \"economy\": \"6.00\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"3.0\",\n" +
        "                \"runs\": \"18\",\n" +
        "                \"strike_rate\": \"18.0\",\n" +
        "                \"wickets\": \"1\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"bowler_id\": \"15239\",\n" +
        "                \"bowler_name\": \"D Wiese\",\n" +
        "                \"economy\": \"12.86\",\n" +
        "                \"maidens\": \"0\",\n" +
        "                \"overs\": \"3.3\",\n" +
        "                \"runs\": \"45\",\n" +
        "                \"strike_rate\": \"\",\n" +
        "                \"wickets\": \"0\"\n" +
        "              }\n" +
        "            ],\n" +
        "            \"bye\": \"0\",\n" +
        "            \"did_not_bat\": [\n" +
        "              {\n" +
        "                \"name\": \"Najibullah Zadran\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"name\": \"Rashid Khan\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"name\": \"Amir Hamza\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"name\": \"Dawlat Zadran\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"name\": \"Shapoor Zadran\"\n" +
        "              }\n" +
        "            ],\n" +
        "            \"extra\": \"6\",\n" +
        "            \"fall_of_wickets\": [\n" +
        "              {\n" +
        "                \"fow_order\": 1,\n" +
        "                \"fow_over\": \"3.6\",\n" +
        "                \"fow_score\": \"1-52\",\n" +
        "                \"name\": \"Mohammad Shahzad\",\n" +
        "                \"runs\": \"44\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"fow_order\": 2,\n" +
        "                \"fow_over\": \"5.2\",\n" +
        "                \"fow_score\": \"2-60\",\n" +
        "                \"name\": \"Asghar Stanikzai\",\n" +
        "                \"runs\": \"7\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"fow_order\": 3,\n" +
        "                \"fow_over\": \"10.3\",\n" +
        "                \"fow_score\": \"3-105\",\n" +
        "                \"name\": \"Gulbadin Naib\",\n" +
        "                \"runs\": \"26\"\n" +
        "              },\n" +
        "              {\n" +
        "                \"fow_order\": 4,\n" +
        "                \"fow_over\": \"11.5\",\n" +
        "                \"fow_score\": \"4-109\",\n" +
        "                \"name\": \"Noor Ali Zadran\",\n" +
        "                \"runs\": \"25\"\n" +
        "              }\n" +
        "            ],\n" +
        "            \"legbye\": \"1\",\n" +
        "            \"noball\": \"0\",\n" +
        "            \"overs\": \"14.3\",\n" +
        "            \"required_runrate\": \"13.09\",\n" +
        "            \"run_rate\": \"9.52\",\n" +
        "            \"runs\": \"138\",\n" +
        "            \"wickets\": \"4\",\n" +
        "            \"wide\": \"5\"\n" +
        "          }\n" +
        "        }\n" +
        "      },\n" +
        "      \"series_id\": \"5166\",\n" +
        "      \"series_name\": \"T20I: World '16\",\n" +
        "      \"status\": \"L\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"error\": false,\n" +
        "  \"success\": true\n" +
        "}";
            showProgress();
              try {
              JSONObject object = new JSONObject(content);
              boolean success = object.getBoolean("success");
              boolean error = object.getBoolean("error");

                if( success ) {
                    renderDisplay(object);
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
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);

    }

    private void showErrorLayout(View view) {

        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);

    }

    private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        hideProgress();
        teamABattingCardList.clear();
        teamABowlingCardList.clear();
        teamAFallOfWicketCardList.clear();
        teamBBattingCardList.clear();
        teamBBowlingCardList.clear();
        teamBFallOfWicketCardList.clear();
        linearLayout.setVisibility(View.VISIBLE);
        if(!jsonObject.isNull("data")) {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            final JSONObject dataObject = jsonArray.getJSONObject(0);
            final CricketMatchScoreJsonParser cricketMatchScoreJsonParser = new CricketMatchScoreJsonParser();
            cricketMatchScoreJsonParser.setJsonObject(dataObject);

            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setScoreCardNew(cricketMatchScoreJsonParser);
                            //setScoreCardOld(dataObject);


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



    private void setScoreCardNew(CricketMatchScoreJsonParser cricketMatchScoreJsonParser) throws JSONException {
        tvFirstTeamInning.setText(cricketMatchScoreJsonParser.getHomeTeam() + " Innings");
        tvSecondTeamInning.setText(cricketMatchScoreJsonParser.getAwayTeam() + " Innings");
        JSONObject scoreCard = cricketMatchScoreJsonParser.getScoreCrad();
        JSONObject teamFirst = cricketMatchScoreJsonParser.getTeamFirst(scoreCard);
        JSONObject teamSecond = cricketMatchScoreJsonParser.getTeamSecond(scoreCard);
        Iterator<String> iteratorTeamFirst = teamFirst.keys();
        Iterator<String> iteratorTeamSecond = teamSecond.keys();
        JSONObject teamFirstInnings [] = new JSONObject[2];
        JSONObject teamSecondInnings [] = new JSONObject[2];
        int i = 0;
         while(iteratorTeamFirst.hasNext()){
             String key = iteratorTeamFirst.next();
             teamFirstInnings[i++] = cricketMatchScoreJsonParser.getTeamFirstInnings(teamFirst,key);
         }
        i= 0;
        while(iteratorTeamSecond.hasNext()){
            String key = iteratorTeamSecond.next();
            teamSecondInnings[i++] = cricketMatchScoreJsonParser.getTeamSecondInnings(teamSecond, key);
        }
      /* String teamsShortName = "";
        if (!dataObject.isNull("short_name")) {
            teamsShortName = dataObject.getString("short_name");
        }
        String teamNamesArray[] = teamsShortName.split(" ");*/
        JSONArray teamABattingArray = null;
        JSONArray teamABowlingArray = null;
        JSONArray teamAFallWicketArray = null;
        if (teamFirstInnings!=null) {


              for (int k = 0;k<teamFirstInnings.length-1;k++){

                  teamABattingArray = cricketMatchScoreJsonParser.getTeamBatting(teamFirstInnings[k]);
                  teamABowlingArray = cricketMatchScoreJsonParser.getTeamBowlling(teamFirstInnings[k]);
                  teamAFallWicketArray = cricketMatchScoreJsonParser.getTeamFallOfWickets(teamFirstInnings[k]);
                  tvFirstTeamOver.setText("(" + cricketMatchScoreJsonParser.getOvers(teamFirstInnings[k]) + ")");
                  tvExtraRunTeamFirst.setText("Extras " + cricketMatchScoreJsonParser.getExtra(teamFirstInnings[k]));
                  tvTotalRunFirstTeam.setText(cricketMatchScoreJsonParser.getTeamRuns(teamFirstInnings[k]));
                  tvRunRateFirstTeam.setText(cricketMatchScoreJsonParser.getTeamRunsRate(teamFirstInnings[k]));
                  tvTeamFirstNameAndScore.setText(cricketMatchScoreJsonParser.getHomeTeam() + " " + cricketMatchScoreJsonParser.getTeamRuns(teamFirstInnings[k]) + "/" + cricketMatchScoreJsonParser.getTeamWicket(teamFirstInnings[k]));
              }
           if (teamABattingArray != null) {
                for (i = 0; i < teamABattingArray.length(); i++) {

                    JSONObject battingObject = teamABattingArray.getJSONObject(i);
                    LiveAndCompletedCricketBattingCardDTO liveAndCompletedCricketBattingCardDTO = CricketMatchScoreCardUtil.getLiveAndCompletedCricketBattingCardDTO(cricketMatchScoreJsonParser, battingObject);
                    teamABattingCardList.add(liveAndCompletedCricketBattingCardDTO);
                }
            }
            if (teamABowlingArray != null) {
                for (int j = 0; j < teamABowlingArray.length(); j++) {
                    JSONObject bowlingObject = teamABowlingArray.getJSONObject(j);
                    LiveAndCompletedCricketBowlingCardDTO bowling = CricketMatchScoreCardUtil.getLiveAndCompletedCricketBowlingCardDTO(cricketMatchScoreJsonParser, bowlingObject);
                    teamABowlingCardList.add(bowling);
                }
            }
            if (teamAFallWicketArray != null) {
                for (int k = 0; k < teamAFallWicketArray.length(); k++) {
                    JSONObject fallOfWicketObject = teamAFallWicketArray.getJSONObject(k);
                    LiveAndCompletedCricketFallOfWicketCardDTO fallOfWickets = CricketMatchScoreCardUtil.getLiveAndCompletedCricketFallOfWicketCardDTO(cricketMatchScoreJsonParser, k, fallOfWicketObject);

                    teamAFallOfWicketCardList.add(fallOfWickets);

                }
            }
        }

        if (teamSecondInnings!=null) {

            JSONArray teamBBattingArray =  null;
            JSONArray teamBBowlingArray =  null;
            JSONArray teamBFallWicketArray =  null;
            for (int k = 0;k<teamSecondInnings.length-1;k++){

                teamBBattingArray = cricketMatchScoreJsonParser.getTeamBatting(teamSecondInnings[k]);
                teamBBowlingArray = cricketMatchScoreJsonParser.getTeamBowlling(teamSecondInnings[k]);
                teamBFallWicketArray = cricketMatchScoreJsonParser.getTeamFallOfWickets(teamSecondInnings[k]);
                tvSecondTeamOver.setText("(" + cricketMatchScoreJsonParser.getOvers(teamSecondInnings[k]) + ")");
                tvExtraRunTeamSecond.setText("Extras " + cricketMatchScoreJsonParser.getExtra(teamSecondInnings[k]));
                tvTotalRunSecondTeam.setText(cricketMatchScoreJsonParser.getTeamRuns(teamSecondInnings[k]));
                tvRunRateSecondTeam.setText(cricketMatchScoreJsonParser.getTeamRunsRate(teamSecondInnings[k]));
                tvTeamSecondNameAndScore.setText(cricketMatchScoreJsonParser.getHomeTeam() + " " + cricketMatchScoreJsonParser.getTeamRuns(teamSecondInnings[k]) + "/" + cricketMatchScoreJsonParser.getTeamWicket(teamSecondInnings[k]));
            }

            for ( i = 0; i < teamBBattingArray.length(); i++) {
                JSONObject battingObject = teamBBattingArray.getJSONObject(i);
                LiveAndCompletedCricketBattingCardDTO liveAndCompletedCricketBattingCardDTO = CricketMatchScoreCardUtil.getLiveAndCompletedCricketBattingCardDTO(cricketMatchScoreJsonParser, battingObject);
                teamBBattingCardList.add(liveAndCompletedCricketBattingCardDTO);
            }
            for (int j = 0; j < teamBBowlingArray.length(); j++) {
                JSONObject bowlingObject = teamBBowlingArray.getJSONObject(j);
                LiveAndCompletedCricketBowlingCardDTO bowling = CricketMatchScoreCardUtil.getLiveAndCompletedCricketBowlingCardDTO(cricketMatchScoreJsonParser, bowlingObject);
                teamBBowlingCardList.add(bowling);
            }

            for (int k = 0; k < teamBFallWicketArray.length(); k++) {
                JSONObject fallOfWicketObject = teamBFallWicketArray.getJSONObject(k);
                LiveAndCompletedCricketFallOfWicketCardDTO fallOfWickets = CricketMatchScoreCardUtil.getLiveAndCompletedCricketFallOfWicketCardDTO(cricketMatchScoreJsonParser, k, fallOfWicketObject);

                teamBFallOfWicketCardList.add(fallOfWickets);

            }
        }
        teamABattingAdapter.notifyDataSetChanged();
        teamABowlingAdapter.notifyDataSetChanged();
        teamAFallOfWicketAdapter.notifyDataSetChanged();
        teamBBattingAdapter.notifyDataSetChanged();
        teamBBowlingAdapter.notifyDataSetChanged();
        teamBFallOfWicketAdapter.notifyDataSetChanged();
    }



    private void setScoreCardOld(JSONObject dataObject) throws JSONException {
        tvFirstTeamInning.setText(dataObject.getString("team_a") + " Innings");
        tvSecondTeamInning.setText(dataObject.getString("team_b") + " Innings");
        JSONObject scoreCard = dataObject.getJSONObject("scorecard");
        String teamsShortName = "";
        if (!dataObject.isNull("short_name")) {
            teamsShortName = dataObject.getString("short_name");
        }
        String teamNamesArray[] = teamsShortName.split(" ");
        if (!scoreCard.isNull(dataObject.getString("team_a"))) {
            JSONObject teamAScoreCard = scoreCard.getJSONObject(dataObject.getString("team_a"));

            JSONObject teamAFirstInning = teamAScoreCard.getJSONObject("a_1");

            JSONArray teamABattingArray = null;
            if (!teamAFirstInning.isNull("batting")) {
                teamABattingArray = teamAFirstInning.getJSONArray("batting");
            }
            JSONArray teamABowlingArray = null;
            if (!teamAFirstInning.isNull("bowling")) {
                teamABowlingArray = teamAFirstInning.getJSONArray("bowling");
            }
            JSONArray teamAFallWicketArray = null;
            if (!teamAFirstInning.isNull("fall_of_wickets")) {
                teamAFallWicketArray = teamAFirstInning.getJSONArray("fall_of_wickets");
            }


            tvFirstTeamOver.setText("(" + teamAFirstInning.getString("team_overs") + ")");
            tvExtraRunTeamFirst.setText("Extras " + teamAFirstInning.getString("inning_extras"));
            tvTotalRunFirstTeam.setText(teamAFirstInning.getString("team_runs"));
            tvRunRateFirstTeam.setText(teamAFirstInning.getString("team_run_rate"));
            tvTeamFirstNameAndScore.setText(teamNamesArray[0] + " " + teamAFirstInning.getString("team_runs") + "/" + teamAFirstInning.getString("team_wickets"));
            if (teamABattingArray != null) {
                for (int i = 0; i < teamABattingArray.length(); i++) {
                    JSONObject battingObject = teamABattingArray.getJSONObject(i);
                    LiveAndCompletedCricketBattingCardDTO liveAndCompletedCricketBattingCardDTO = new LiveAndCompletedCricketBattingCardDTO();
                    liveAndCompletedCricketBattingCardDTO.setTvPlayerName(battingObject.getString("player"));
                    liveAndCompletedCricketBattingCardDTO.setTvBallPlayByPlayer(battingObject.getString("B"));
                    liveAndCompletedCricketBattingCardDTO.setTvSrRateOfPlayer(battingObject.getString("SR"));
                    liveAndCompletedCricketBattingCardDTO.setTvFourGainByPlayer(battingObject.getString("4s"));
                    liveAndCompletedCricketBattingCardDTO.setTvSixGainByPlayer(battingObject.getString("6s"));
                    liveAndCompletedCricketBattingCardDTO.setTvPlayerRun(battingObject.getString("R"));
                    liveAndCompletedCricketBattingCardDTO.setTvWicketBy(battingObject.getString("player_status"));
                    teamABattingCardList.add(liveAndCompletedCricketBattingCardDTO);
                }
            }
            if (teamABowlingArray != null) {
                for (int j = 0; j < teamABowlingArray.length(); j++) {
                    JSONObject bowlingArray = teamABowlingArray.getJSONObject(j);
                    LiveAndCompletedCricketBowlingCardDTO bowling = new LiveAndCompletedCricketBowlingCardDTO();
                    bowling.setTvRuns(bowlingArray.getString("runs"));
                    bowling.setTvBowlerName(bowlingArray.getString("player"));
                    bowling.setTvExtra(bowlingArray.getString("extras"));
                    bowling.setTvMiddenOver(bowlingArray.getString("maiden"));
                    bowling.setTvWicket(bowlingArray.getString("wickets"));
                    bowling.setTvOver(bowlingArray.getString("overs"));
                    teamABowlingCardList.add(bowling);
                }
            }
            if (teamAFallWicketArray != null) {
                for (int k = 0; k < teamAFallWicketArray.length(); k++) {
                    JSONObject fallOfWicketObject = teamAFallWicketArray.getJSONObject(k);
                    LiveAndCompletedCricketFallOfWicketCardDTO fallOfWickets = new LiveAndCompletedCricketFallOfWicketCardDTO();
                    fallOfWickets.setTvBowlerName(fallOfWicketObject.getString("name"));
                    fallOfWickets.setTvOverNumber(fallOfWicketObject.getString("overs") + "ovs");
                    fallOfWickets.setTvWicket(fallOfWicketObject.getString("runs").split(" ")[0] + "-" + (k + 1));

                    teamAFallOfWicketCardList.add(fallOfWickets);

                }
            }
        }

        if (!scoreCard.isNull(dataObject.getString("team_b"))) {
            JSONObject teamBScoreCard = scoreCard.getJSONObject(dataObject.getString("team_b"));
            JSONObject teamBFirstInning = teamBScoreCard.getJSONObject("b_1");
            JSONArray teamBBattingArray = teamBFirstInning.getJSONArray("batting");
            JSONArray teamBBowlingArray = teamBFirstInning.getJSONArray("bowling");
            JSONArray teamBFallWicketArray = teamBFirstInning.getJSONArray("fall_of_wickets");
            tvSecondTeamOver.setText("(" + teamBFirstInning.getString("team_overs") + ")");

            tvExtraRunTeamSecond.setText("Extras " + teamBFirstInning.getString("inning_extras"));

            tvTotalRunSecondTeam.setText(teamBFirstInning.getString("team_runs"));

            tvRunRateSecondTeam.setText(teamBFirstInning.getString("team_run_rate"));

            tvTeamSecondNameAndScore.setText(teamNamesArray[2] + " " + teamBFirstInning.getString("team_runs") + "/" + teamBFirstInning.getString("team_wickets"));

            for (int i = 0; i < teamBBattingArray.length(); i++) {
                JSONObject battingObject = teamBBattingArray.getJSONObject(i);
                LiveAndCompletedCricketBattingCardDTO liveAndCompletedCricketBattingCardDTO = new LiveAndCompletedCricketBattingCardDTO();
                liveAndCompletedCricketBattingCardDTO.setTvPlayerName(battingObject.getString("player"));
                liveAndCompletedCricketBattingCardDTO.setTvBallPlayByPlayer(battingObject.getString("B"));
                liveAndCompletedCricketBattingCardDTO.setTvSrRateOfPlayer(battingObject.getString("SR"));
                liveAndCompletedCricketBattingCardDTO.setTvFourGainByPlayer(battingObject.getString("4s"));
                liveAndCompletedCricketBattingCardDTO.setTvSixGainByPlayer(battingObject.getString("6s"));
                liveAndCompletedCricketBattingCardDTO.setTvPlayerRun(battingObject.getString("R"));
                liveAndCompletedCricketBattingCardDTO.setTvWicketBy(battingObject.getString("player_status"));
                teamBBattingCardList.add(liveAndCompletedCricketBattingCardDTO);
            }
            for (int j = 0; j < teamBBowlingArray.length(); j++) {
                JSONObject bowlingArray = teamBBowlingArray.getJSONObject(j);
                LiveAndCompletedCricketBowlingCardDTO bowling = new LiveAndCompletedCricketBowlingCardDTO();
                bowling.setTvRuns(bowlingArray.getString("runs"));
                bowling.setTvBowlerName(bowlingArray.getString("player"));
                bowling.setTvExtra(bowlingArray.getString("extras"));
                bowling.setTvMiddenOver(bowlingArray.getString("maiden"));
                bowling.setTvWicket(bowlingArray.getString("wickets"));
                bowling.setTvOver(bowlingArray.getString("overs"));
                teamBBowlingCardList.add(bowling);
            }

            for (int k = 0; k < teamBFallWicketArray.length(); k++) {
                JSONObject fallOfWicketObject = teamBFallWicketArray.getJSONObject(k);
                LiveAndCompletedCricketFallOfWicketCardDTO fallOfWickets = new LiveAndCompletedCricketFallOfWicketCardDTO();
                fallOfWickets.setTvBowlerName(fallOfWicketObject.getString("name"));
                fallOfWickets.setTvOverNumber(fallOfWicketObject.getString("overs"));
                fallOfWickets.setTvWicket(fallOfWicketObject.getString("runs").split(" ")[0] + "-" + (k + 1));

                teamBFallOfWicketCardList.add(fallOfWickets);

            }
        }
        teamABattingAdapter.notifyDataSetChanged();
        teamABowlingAdapter.notifyDataSetChanged();
        teamAFallOfWicketAdapter.notifyDataSetChanged();
        teamBBattingAdapter.notifyDataSetChanged();
        teamBBowlingAdapter.notifyDataSetChanged();
        teamBFallOfWicketAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(completedMatchScoreCardHandler != null){
            completedMatchScoreCardHandler.addListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress();
        if(completedMatchScoreCardHandler != null){
            completedMatchScoreCardHandler.addListener(this);

        }else {
            completedMatchScoreCardHandler= CompletedMatchScoreCardHandler.getInstance(getContext());
        }
        completedMatchScoreCardHandler.requestCompletdMatchScoreCard(matchId);
    }
    /*public void handleError(){
        showErrorLayout(getView());
    }
*/
}
