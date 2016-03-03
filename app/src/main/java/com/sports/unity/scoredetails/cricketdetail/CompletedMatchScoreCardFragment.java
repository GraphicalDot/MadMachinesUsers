package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import org.solovyev.android.views.llm.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
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
        ivDwn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        ivDwnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
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
    public void handleContent(JSONObject object) {
        {
            showProgress();
              try {
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
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(!jsonObject.isNull("data")){
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONObject dataObject = jsonArray.getJSONObject(0);
                            tvFirstTeamInning.setText(dataObject.getString("team_a") + " Innings");
                            tvSecondTeamInning.setText(dataObject.getString("team_b") + " Innings");
                            JSONObject scoreCard = dataObject.getJSONObject("scorecard");
                            String teamsShortName ="";
                            if(!dataObject.isNull("short_name")){
                                teamsShortName = dataObject.getString("short_name");
                            }
                            String teamNamesArray[] = teamsShortName.split(" ");
                            if (!scoreCard.isNull(dataObject.getString("team_a"))){
                                JSONObject teamAScoreCard = scoreCard.getJSONObject(dataObject.getString("team_a"));

                                JSONObject teamAFirstInning = teamAScoreCard.getJSONObject("a_1");

                                JSONArray teamABattingArray = null;
                                if(!teamAFirstInning.isNull("batting")){
                                    teamABattingArray = teamAFirstInning.getJSONArray("batting");
                                }
                                JSONArray teamABowlingArray = null;
                                if(!teamAFirstInning.isNull("bowling")){
                                    teamABowlingArray = teamAFirstInning.getJSONArray("bowling");
                                }
                                JSONArray teamAFallWicketArray = null;
                                if(!teamAFirstInning.isNull("fall_of_wickets")){
                                    teamAFallWicketArray = teamAFirstInning.getJSONArray("fall_of_wickets");
                                }


                                tvFirstTeamOver.setText("("+teamAFirstInning.getString("team_overs")+")");
                                tvExtraRunTeamFirst.setText("Extras "+teamAFirstInning.getString("inning_extras"));
                                tvTotalRunFirstTeam.setText(teamAFirstInning.getString("team_runs"));
                                tvRunRateFirstTeam.setText(teamAFirstInning.getString("team_run_rate"));
                                tvTeamFirstNameAndScore.setText(teamNamesArray[0]+" "+teamAFirstInning.getString("team_runs")+"/"+teamAFirstInning.getString("team_wickets"));
                                if(teamABattingArray != null){
                                    for (int i= 0 ; i<teamABattingArray.length();i++){
                                        JSONObject battingObject = teamABattingArray.getJSONObject(i);
                                        LiveAndCompletedCricketBattingCardDTO liveAndCompletedCricketBattingCardDTO= new LiveAndCompletedCricketBattingCardDTO();
                                        liveAndCompletedCricketBattingCardDTO.setTvPlayerName(battingObject.getString("player"));
                                        liveAndCompletedCricketBattingCardDTO.setTvBallPlayByPlayer(battingObject.getString("B"));
                                        liveAndCompletedCricketBattingCardDTO.setTvSrRateOfPlayer(battingObject.getString("SR"));
                                        liveAndCompletedCricketBattingCardDTO.setTvFourGainByPlayer(battingObject.getString("4s"));
                                        liveAndCompletedCricketBattingCardDTO.setTvSixGainByPlayer(battingObject.getString("6s"));
                                        liveAndCompletedCricketBattingCardDTO.setTvPlayerRun(battingObject.getString("R"));
                                        liveAndCompletedCricketBattingCardDTO.setTvWicketBy(battingObject.getString("player_status"));
                                        teamABattingCardList.add(liveAndCompletedCricketBattingCardDTO);
                                    }}
                                if(teamABowlingArray != null){
                                    for (int j= 0 ; j<teamABowlingArray.length();j++){
                                        JSONObject bowlingArray = teamABowlingArray.getJSONObject(j);
                                        LiveAndCompletedCricketBowlingCardDTO bowling= new LiveAndCompletedCricketBowlingCardDTO();
                                        bowling.setTvRuns(bowlingArray.getString("runs"));
                                        bowling.setTvBowlerName(bowlingArray.getString("player"));
                                        bowling.setTvExtra(bowlingArray.getString("extras"));
                                        bowling.setTvMiddenOver(bowlingArray.getString("maiden"));
                                        bowling.setTvWicket(bowlingArray.getString("wickets"));
                                        bowling.setTvOver(bowlingArray.getString("overs"));
                                        teamABowlingCardList.add(bowling);
                                    }
                                }
                                if(teamAFallWicketArray!= null) {
                                    for (int k = 0; k < teamAFallWicketArray.length(); k++) {
                                        JSONObject fallOfWicketObject = teamAFallWicketArray.getJSONObject(k);
                                        LiveAndCompletedCricketFallOfWicketCardDTO fallOfWickets = new LiveAndCompletedCricketFallOfWicketCardDTO();
                                        fallOfWickets.setTvBowlerName(fallOfWicketObject.getString("name"));
                                        fallOfWickets.setTvOverNumber(fallOfWicketObject.getString("overs")+"ovs");
                                        fallOfWickets.setTvWicket(fallOfWicketObject.getString("runs").split(" ")[0]+"-"+(k+1));

                                        teamAFallOfWicketCardList.add(fallOfWickets);

                                    }
                                }
                            }

                            if (!scoreCard.isNull(dataObject.getString("team_b"))){
                                JSONObject teamBScoreCard = scoreCard.getJSONObject(dataObject.getString("team_b"));
                                JSONObject teamBFirstInning = teamBScoreCard.getJSONObject("b_1");
                                JSONArray teamBBattingArray = teamBFirstInning.getJSONArray("batting");
                                JSONArray teamBBowlingArray = teamBFirstInning.getJSONArray("bowling");
                                JSONArray teamBFallWicketArray = teamBFirstInning.getJSONArray("fall_of_wickets");
                                tvSecondTeamOver.setText("("+teamBFirstInning.getString("team_overs")+")");

                                tvExtraRunTeamSecond.setText("Extras "+teamBFirstInning.getString("inning_extras"));

                                tvTotalRunSecondTeam.setText(teamBFirstInning.getString("team_runs"));

                                tvRunRateSecondTeam.setText(teamBFirstInning.getString("team_run_rate"));

                                tvTeamSecondNameAndScore.setText(teamNamesArray[2]+" "+teamBFirstInning.getString("team_runs")+"/"+teamBFirstInning.getString("team_wickets"));

                                for (int i= 0 ; i<teamBBattingArray.length();i++){
                                    JSONObject battingObject = teamBBattingArray.getJSONObject(i);
                                    LiveAndCompletedCricketBattingCardDTO liveAndCompletedCricketBattingCardDTO= new LiveAndCompletedCricketBattingCardDTO();
                                    liveAndCompletedCricketBattingCardDTO.setTvPlayerName(battingObject.getString("player"));
                                    liveAndCompletedCricketBattingCardDTO.setTvBallPlayByPlayer(battingObject.getString("B"));
                                    liveAndCompletedCricketBattingCardDTO.setTvSrRateOfPlayer(battingObject.getString("SR"));
                                    liveAndCompletedCricketBattingCardDTO.setTvFourGainByPlayer(battingObject.getString("4s"));
                                    liveAndCompletedCricketBattingCardDTO.setTvSixGainByPlayer(battingObject.getString("6s"));
                                    liveAndCompletedCricketBattingCardDTO.setTvPlayerRun(battingObject.getString("R"));
                                    teamBBattingCardList.add(liveAndCompletedCricketBattingCardDTO);
                                }
                                for (int j= 0 ; j<teamBBowlingArray.length();j++){
                                    JSONObject bowlingArray = teamBBowlingArray.getJSONObject(j);
                                    LiveAndCompletedCricketBowlingCardDTO bowling= new LiveAndCompletedCricketBowlingCardDTO();
                                    bowling.setTvRuns(bowlingArray.getString("runs"));
                                    bowling.setTvBowlerName(bowlingArray.getString("player"));
                                    bowling.setTvExtra(bowlingArray.getString("extras"));
                                    bowling.setTvMiddenOver(bowlingArray.getString("maiden"));
                                    bowling.setTvWicket(bowlingArray.getString("wickets"));
                                    bowling.setTvOver(bowlingArray.getString("overs"));
                                    teamBBowlingCardList.add(bowling);
                                }

                                for (int k= 0 ; k<teamBFallWicketArray.length();k++){
                                    JSONObject fallOfWicketObject = teamBFallWicketArray.getJSONObject(k);
                                    LiveAndCompletedCricketFallOfWicketCardDTO fallOfWickets= new LiveAndCompletedCricketFallOfWicketCardDTO();
                                    fallOfWickets.setTvBowlerName(fallOfWicketObject.getString("name"));
                                    fallOfWickets.setTvOverNumber(fallOfWicketObject.getString("overs"));
                                    fallOfWickets.setTvWicket(fallOfWicketObject.getString("runs").split(" ")[0]+"-"+(k+1));

                                    teamBFallOfWicketCardList.add(fallOfWickets);

                                }
                            }
                        }

                        teamABattingAdapter.notifyDataSetChanged();
                        teamABowlingAdapter.notifyDataSetChanged();
                        teamAFallOfWicketAdapter.notifyDataSetChanged();
                        teamBBattingAdapter.notifyDataSetChanged();
                        teamBBowlingAdapter.notifyDataSetChanged();
                        teamBFallOfWicketAdapter.notifyDataSetChanged();

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
    public void handleError(){
        showErrorLayout(getView());
    }

}
