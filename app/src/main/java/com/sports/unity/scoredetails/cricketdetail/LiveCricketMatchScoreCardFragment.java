package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBattingCardAdapter;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBattingCardDTO;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBowlingCardAdapter;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBowlingCardDTO;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketFallOfWicketAdapter;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketFallOfWicketCardDTO;
import com.sports.unity.scoredetails.cricketdetail.livecompletedmatchadapters.LiveAndCompletedCricketBattingAdapter;
import com.sports.unity.scoredetails.model.CricketScoreCard;
import com.sports.unity.scoredetails.model.Scorecard;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


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

    public LiveCricketMatchScoreCardFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        String matchId =  getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        LivedMatchScoreCardHandler cricketPlayerbioHandler = LivedMatchScoreCardHandler.getInstance(context);
        cricketPlayerbioHandler.addListener(this);
        cricketPlayerbioHandler.requestMatchScoreCard(matchId);
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
        teamABowlingRecycler = (RecyclerView) view.findViewById(R.id.rv_team_first_bowling);
        teamAFallOfWicketRecycler = (RecyclerView) view.findViewById(R.id.rv_team_first_fall_wickets);
        teamBBattingRecycler = (RecyclerView) view.findViewById(R.id.rv_team_second_batting);
        teamBBowlingRecycler = (RecyclerView) view.findViewById(R.id.rv_team_second_bowling);
        teamBFallOfWicketRecycler = (RecyclerView) view.findViewById(R.id.rv_second_team_fall_wicket);
        teamABattingAdapter = new LiveAndCompletedCricketBattingCardAdapter(teamABattingCardList);
        teamABattingRecycler.setAdapter(teamABattingAdapter);
        teamBBattingAdapter = new LiveAndCompletedCricketBattingCardAdapter(teamBBattingCardList);
        teamBBattingRecycler.setAdapter(teamBBattingAdapter);
        teamABowlingAdapter = new LiveAndCompletedCricketBowlingCardAdapter(teamABowlingCardList);
        teamABowlingRecycler.setAdapter(teamABowlingAdapter);
        teamBBowlingAdapter = new LiveAndCompletedCricketBowlingCardAdapter(teamBBowlingCardList);
        teamBBowlingRecycler.setAdapter(teamBBowlingAdapter);
        teamAFallOfWicketAdapter = new LiveAndCompletedCricketFallOfWicketAdapter(teamAFallOfWicketCardList);
        teamAFallOfWicketRecycler.setAdapter(teamAFallOfWicketAdapter);
        teamBFallOfWicketAdapter = new LiveAndCompletedCricketFallOfWicketAdapter(teamBFallOfWicketCardList);
        teamBFallOfWicketRecycler.setAdapter(teamBFallOfWicketAdapter);
        initErrorLayout(view);

    }

    @Override
    public void handleContent(JSONObject jsonObject) {
        {
            try {
                boolean success = jsonObject.getBoolean("success");

                if( success ) {

                    renderDisplay(jsonObject);

                } else {
                    Toast.makeText(getActivity(), R.string.match_not_exist, Toast.LENGTH_SHORT).show();
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

        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        JSONObject dataObject = jsonArray.getJSONObject(0);
                        tvFirstTeamInning.setText(dataObject.getString("team_a") +" Innings");
                        tvSecondTeamInning.setText(dataObject.getString("team_b")+ " Innings");
                        JSONObject scoreCard= dataObject.getJSONObject("scorecard");
                        JSONObject teamAScoreCard = scoreCard.getJSONObject(dataObject.getString("team_a"));
                        JSONObject teamAFirstInning = teamAScoreCard.getJSONObject("a_1");
                        JSONObject teamASecondInning = teamAScoreCard.getJSONObject("a_2");
                        JSONObject teamBScoreCard = scoreCard.getJSONObject(dataObject.getString("team_b"));
                        JSONObject teamBFirstInning = teamBScoreCard.getJSONObject("b_1");
                        JSONObject teamBSecondInning = teamBScoreCard.getJSONObject("b_2");

                        JSONArray teamABattingArray = teamAFirstInning.getJSONArray("batting");
                        JSONArray teamABowlingArray = teamAFirstInning.getJSONArray("bowling");
                        JSONArray teamAFallWicketArray = teamAFirstInning.getJSONArray("fall_of_wickets");
                        JSONArray teamBBattingArray = teamBFirstInning.getJSONArray("batting");
                        JSONArray teamBBowlingArray = teamBFirstInning.getJSONArray("bowling");
                        JSONArray teamBFallWicketArray = teamAFirstInning.getJSONArray("fall_of_wickets");
                        tvTeamFirstNameAndScore.setText(dataObject.getString("team_a")+" "+teamAFirstInning.getString("team_runs")+"/"+teamAFirstInning.getString("team_wickets")+"("+teamAFirstInning.getString("team_overs")+")");
                        tvTeamSecondNameAndScore.setText(dataObject.getString("team_b")+" "+teamBFirstInning.getString("team_runs")+"/"+teamBFirstInning.getString("team_wickets")+"("+teamBFirstInning.getString("team_overs")+")");
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
    }
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

                        /*for (int k= 0 ; k<teamAFallWicketArray.length();k++){

                        }*/

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

                        /*for (int k= 0 ; k<teamAFallWicketArray.length();k++){

                        }*/


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


}
