package com.sports.unity.scoredetails.cricketdetail;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;


public class LiveCricketMatchScoreCardFragment extends Fragment implements LivedMatchScoreCardHandler.LiveMatchContentListener{

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
    private ProgressBar progressBar;
    private LivedMatchScoreCardHandler livedMatchScoreCardHandler;
    private String matchId;
    private LinearLayout linearLayout;
    private LinearLayout firstBattingLinearLayout;
    private LinearLayout firstBowlingLinearLayout;
    private LinearLayout firstFallofWicketsLinearLayout;
    private LinearLayout secondBattingLinearLayout;
    private LinearLayout secondBowlingLinearLayout;
    private LinearLayout secondFallofWicketsLinearLayout;
    private Timer timerToRefreshContent;
    private Context context;
    private RelativeLayout team1ScoreDetails;
    private RelativeLayout team2ScoreDetails;
    private String seriesId;

    public LiveCricketMatchScoreCardFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        matchId =  getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        seriesId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_SERIES);
        this.context = context;
        matchScoreCrad();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_match_score_card, container, false);
        initView(view);
        return view;
    }

    /* Tis method use to initialization view element of  fragment_completed_match_score_card*/
    private void initView(View view) {
        initializeView(view);
    }

    public void initializeView(View view) {
        firstBattingLinearLayout = (LinearLayout) view.findViewById(R.id.ll_first_view_visibility);
        firstBowlingLinearLayout = (LinearLayout) view.findViewById(R.id.ll_first_bowling_visibility);
        firstFallofWicketsLinearLayout = (LinearLayout) view.findViewById(R.id.first_layout_fall_wicket);
        secondBattingLinearLayout = (LinearLayout) view.findViewById(R.id.ll_batting_second);
        secondBowlingLinearLayout = (LinearLayout) view.findViewById(R.id.second_bowling_layout);
        secondFallofWicketsLinearLayout = (LinearLayout) view.findViewById(R.id.layout_fall_wicket_second);

        linearLayout = (LinearLayout) view.findViewById(R.id.scorecard_parent_layout);
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
        teamABattingAdapter = new LiveAndCompletedCricketBattingCardAdapter(teamABattingCardList,context);
        teamABattingRecycler.setAdapter(teamABattingAdapter);
        teamBBattingAdapter = new LiveAndCompletedCricketBattingCardAdapter(teamBBattingCardList,context);
        teamBBattingRecycler.setAdapter(teamBBattingAdapter);
        teamABowlingAdapter = new LiveAndCompletedCricketBowlingCardAdapter(teamABowlingCardList,context);
        teamABowlingRecycler.setAdapter(teamABowlingAdapter);
        teamBBowlingAdapter = new LiveAndCompletedCricketBowlingCardAdapter(teamBBowlingCardList,context);
        teamBBowlingRecycler.setAdapter(teamBBowlingAdapter);
        teamAFallOfWicketAdapter = new LiveAndCompletedCricketFallOfWicketAdapter(teamAFallOfWicketCardList,context);
        teamAFallOfWicketRecycler.setAdapter(teamAFallOfWicketAdapter);
        teamBFallOfWicketAdapter = new LiveAndCompletedCricketFallOfWicketAdapter(teamBFallOfWicketCardList,context);
        teamBFallOfWicketRecycler.setAdapter(teamBFallOfWicketAdapter);
        progressBar  = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        initErrorLayout(view);
        team1ScoreDetails = (RelativeLayout) view.findViewById(R.id.team1_scroll_details);
        team2ScoreDetails = (RelativeLayout) view.findViewById(R.id.team2_scroll_details);
        ivDwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHideTeamFirstScoreCard();
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
                    showHideTeamFirstScoreCard();
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

    private void showHideTeamFirstScoreCard() {
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

    private void enableAutoRefreshContent(){
        timerToRefreshContent = new Timer();
        timerToRefreshContent.schedule(new TimerTask() {

            @Override
            public void run() {
                matchScoreCrad();
            }

        }, Constants.TIMEINMILISECOND, Constants.TIMEINMILISECOND);
    }

    private void matchScoreCrad() {
        livedMatchScoreCardHandler = LivedMatchScoreCardHandler.getInstance(context);
        livedMatchScoreCardHandler.addListener(this);
        livedMatchScoreCardHandler.requestMatchScoreCard(seriesId,matchId);
    }


    @Override
    public void handleContent(String content) {
        {
            try {
                JSONObject object = new JSONObject(content);
                boolean success = object.getBoolean("success");

                if( success ) {

                    renderDisplay(object);

                } else {

                    showErrorLayout(getView());
                }
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getActivity(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
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
        hideProgress();
        teamABattingCardList.clear();
        teamABowlingCardList.clear();
        teamAFallOfWicketCardList.clear();
        teamBBattingCardList.clear();
        teamBBowlingCardList.clear();
        teamBFallOfWicketCardList.clear();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        final JSONObject dataObject = jsonArray.getJSONObject(0);
        final CricketMatchScoreJsonParser cricketMatchScoreJsonParser = new CricketMatchScoreJsonParser();
        cricketMatchScoreJsonParser.setJsonObject(dataObject);
        Activity activity =  getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setScoreCardNew(cricketMatchScoreJsonParser);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showErrorLayout(getView());
                    }
                }
            });
        }

    }
    private void setScoreCardNew(CricketMatchScoreJsonParser cricketMatchScoreJsonParser) throws JSONException {
        //tvFirstTeamInning.setText(cricketMatchScoreJsonParser.getHomeTeam() + " Innings");
        //tvSecondTeamInning.setText(cricketMatchScoreJsonParser.getAwayTeam() + " Innings");
        String teamNameFirst = null;
        String teamNameSecond = null;
        JSONObject scoreCard = cricketMatchScoreJsonParser.getScoreCrad();
        JSONObject teamFirst = cricketMatchScoreJsonParser.getTeamFirst(scoreCard);
        JSONObject teamSecond = cricketMatchScoreJsonParser.getTeamSecond(scoreCard);
        Iterator<String> iteratorTeamFirst = null;
        Iterator<String> iteratorTeamSecond = null;
        if(teamFirst!=null){
            iteratorTeamFirst = teamFirst.keys();
        }
        if(teamSecond!=null){
            iteratorTeamSecond = teamSecond.keys();
        }





        JSONObject teamFirstInnings [] = new JSONObject[2];
        JSONObject teamSecondInnings [] = new JSONObject[2];
        int i = 0;
        if(iteratorTeamFirst!=null){
            while(iteratorTeamFirst.hasNext()){
                String key = iteratorTeamFirst.next();
                teamNameFirst = key.split(" ")[2];
                teamFirstInnings[i++] = cricketMatchScoreJsonParser.getTeamFirstInnings(teamFirst, key);
            }
        }

        i= 0;
        if(iteratorTeamSecond!=null){
            while(iteratorTeamSecond.hasNext()){
                String key = iteratorTeamSecond.next();
                teamNameSecond = key.split(" ")[2];
                teamSecondInnings[i++] = cricketMatchScoreJsonParser.getTeamSecondInnings(teamSecond, key);
            }
        }
       /* tvFirstTeamInning.setText(cricketMatchScoreJsonParser.getHomeTeam().contains(teamNameFirst)?cricketMatchScoreJsonParser.getHomeTeam():cricketMatchScoreJsonParser.getAwayTeam()+" Innings");
        tvSecondTeamInning.setText(cricketMatchScoreJsonParser.getAwayTeam().contains(teamNameSecond)?cricketMatchScoreJsonParser.getAwayTeam():cricketMatchScoreJsonParser.getHomeTeam()+" Innings");
      */  /* String teamsShortName = "";
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
                tvTeamFirstNameAndScore.setText(teamNameFirst + " " + cricketMatchScoreJsonParser.getTeamRuns(teamFirstInnings[k]) + "/" + cricketMatchScoreJsonParser.getTeamWicket(teamFirstInnings[k]));
                tvFirstTeamInning.setText(teamNameFirst+ " Innings");
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
        }else{
            tvFirstTeamInning.setText("Yet To Batting");
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
                tvTeamSecondNameAndScore.setText(teamNameSecond + " " + cricketMatchScoreJsonParser.getTeamRuns(teamSecondInnings[k]) + "/" + cricketMatchScoreJsonParser.getTeamWicket(teamSecondInnings[k]));
                tvSecondTeamInning.setText(teamNameSecond + " Innings");
            }
            if(teamBBattingArray!=null){
            for ( i = 0; i < teamBBattingArray.length(); i++) {
                JSONObject battingObject = teamBBattingArray.getJSONObject(i);
                LiveAndCompletedCricketBattingCardDTO liveAndCompletedCricketBattingCardDTO = CricketMatchScoreCardUtil.getLiveAndCompletedCricketBattingCardDTO(cricketMatchScoreJsonParser, battingObject);
                teamBBattingCardList.add(liveAndCompletedCricketBattingCardDTO);
            }
            }

            if(teamBBowlingArray!=null) {
                for (int j = 0; j < teamBBowlingArray.length(); j++) {
                    JSONObject bowlingObject = teamBBowlingArray.getJSONObject(j);
                    LiveAndCompletedCricketBowlingCardDTO bowling = CricketMatchScoreCardUtil.getLiveAndCompletedCricketBowlingCardDTO(cricketMatchScoreJsonParser, bowlingObject);
                    teamBBowlingCardList.add(bowling);
                }
            }
            if(teamBFallWicketArray!=null) {
            for (int k = 0; k < teamBFallWicketArray.length(); k++) {
                JSONObject fallOfWicketObject = teamBFallWicketArray.getJSONObject(k);
                LiveAndCompletedCricketFallOfWicketCardDTO fallOfWickets = CricketMatchScoreCardUtil.getLiveAndCompletedCricketFallOfWicketCardDTO(cricketMatchScoreJsonParser, k, fallOfWicketObject);

                teamBFallOfWicketCardList.add(fallOfWickets);
            }
            }
        }else{
            tvFirstTeamInning.setText("Yet To Batting");
        }
        teamABattingAdapter.notifyDataSetChanged();
        teamABowlingAdapter.notifyDataSetChanged();
        teamAFallOfWicketAdapter.notifyDataSetChanged();
        teamBBattingAdapter.notifyDataSetChanged();
        teamBBowlingAdapter.notifyDataSetChanged();
        teamBFallOfWicketAdapter.notifyDataSetChanged();
    }
    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

    }
    private void hideProgress() {
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onPause() {
        super.onPause();
        if(livedMatchScoreCardHandler != null){
            livedMatchScoreCardHandler.addListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress();
        if(livedMatchScoreCardHandler != null){
            livedMatchScoreCardHandler.addListener(this);

        }else {
            livedMatchScoreCardHandler= LivedMatchScoreCardHandler.getInstance(getContext());
        }
        livedMatchScoreCardHandler.requestMatchScoreCard(seriesId,matchId);
    }
}
