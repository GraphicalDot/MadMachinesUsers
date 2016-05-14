package com.sports.unity.scoredetails.cricketdetail;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.playerprofile.cricket.PlayerCricketBioDataActivity;
import com.sports.unity.scoredetails.cricketdetail.JsonParsers.CompletedCricketMatchSummaryParser;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

public class CricketSummaryHelper extends BasicVolleyRequestResponseViewHelper {

    private String title = null;
    private HashMap<String, String> parameters = null;
    private JSONObject response = null;

    private String toss = "";
    private String matchName = "";
    private String date = "";
    private String matchStatus = "";

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private View contentLayout = null;

    private TextView tvUmpiresName;
    private TextView tvMatchReferee;

    private ImageView ivPlayerProfileView;
    private TextView playerName;
    private TextView tvPlayerRun;
    private TextView tvPlayerPlayedBall;
    private TextView tvPlayerStrike_Rate;

    private TextView playedBallTag;
    private TextView playerStrikeRate;

    private CompletedCricketMatchSummaryParser cricketMatchSummaryParser;

    public CricketSummaryHelper(String title, Intent intent) {
        this.title = title;

        toss = intent.getStringExtra(INTENT_KEY_TOSS);
        date = intent.getStringExtra(INTENT_KEY_DATE);
        matchName = intent.getStringExtra(INTENT_KEY_MATCH_NAME);
        matchStatus = intent.getStringExtra(Constants.INTENT_KEY_MATCH_STATUS);

    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_cricket_completed_match_summery;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return "CricketCompletedMatchSummary";
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        MatchSummaryComponentListener componentListener = new MatchSummaryComponentListener(getRequestTag(), progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        return componentListener;
    }

    @Override
    public String getRequestTag() {
        return "CricketCompletedMatchRequestTag";
    }

    @Override
    public String getRequestCallName() {
        boolean completed = ScoresUtil.isCricketMatchCompleted(matchStatus);
        String callName = null;
        if (completed) {
            callName = ScoresContentHandler.CALL_NAME_CRICKET_MATCH_SUMMARY;
        } else {
            //nothing
        }
        return callName;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return parameters;
    }

    @Override
    public void initialiseViews(View view) {
        initView(view);
    }

    @Override
    public void requestContent() {
        boolean completed = ScoresUtil.isCricketMatchCompleted(matchStatus);
        if (completed) {
            super.requestContent();
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    private void initView(View view) {
        boolean isUpcoming = ScoresUtil.isCricketMatchUpcoming(matchStatus);
        if (isUpcoming) {
            View playerOfTheMatchLayout = view.findViewById(R.id.player_of_the_match_layout);
            playerOfTheMatchLayout.setVisibility(View.GONE);
        }

        contentLayout = view.findViewById(R.id.content_layout);
        contentLayout.setVisibility(View.GONE);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        ivPlayerProfileView = (ImageView) view.findViewById(R.id.iv_player_profile_image);
        playerName = (TextView) view.findViewById(R.id.tv_player_name);
        tvPlayerRun = (TextView) view.findViewById(R.id.tv_player_run);
        tvPlayerPlayedBall = (TextView) view.findViewById(R.id.tv_player_played_ball);
        tvPlayerStrike_Rate = (TextView) view.findViewById(R.id.tv_player_strike_rate);
        playedBallTag = (TextView) view.findViewById(R.id.tv_player_ball_tag);
        playerStrikeRate = (TextView) view.findViewById(R.id.tv_player_sr_t);

        tvUmpiresName = (TextView) view.findViewById(R.id.tv_umpires_name);
        tvMatchReferee = (TextView) view.findViewById(R.id.tv_match_referee);

        {
            TextView tvSeriesName = (TextView) view.findViewById(R.id.tv_series_name);
            TextView tvMatchDate = (TextView) view.findViewById(R.id.tv_match_date);
            TextView tvTossWinTeam = (TextView) view.findViewById(R.id.tv_toss_win_team);

            tvMatchDate.setText(DateUtil.getFormattedDate(date));
            tvTossWinTeam.setText(toss);
            tvSeriesName.setText(matchName);

            if (isUpcoming) {
                view.findViewById(R.id.umpires_layout).setVisibility(View.GONE);
                view.findViewById(R.id.refree_layout).setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(false);
            }


            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    requestContent();
                }

            });
        }

        ivPlayerProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cricketMatchSummaryParser != null) {
                    try {
                        String playerId = cricketMatchSummaryParser.getPlayerId();
                        Intent intent = PlayerCricketBioDataActivity.createIntent(v.getContext(), playerId, playerName.getText().toString());
                        v.getContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        playerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cricketMatchSummaryParser != null) {
                    try {
                        String playerId = cricketMatchSummaryParser.getPlayerId();
                        Intent intent = PlayerCricketBioDataActivity.createIntent(v.getContext(), playerId, playerName.getText().toString());
                        v.getContext().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    public boolean handleContent(String content) {
        boolean success = false;
        try {
            content = "{\"status\": 200, \"data\": [{\"status\": \"F\", \"home_team\": \"Bangalore\", \"away_team\": \"Gujarat\", \"match_id\": \"44\", \"venue\": \"M.Chinnaswamy Stadium\", \"series_name\": \"IPL\", \"summary\": {\"recent_over\": {\"18\": [{\"event\": [\"\", \"\", \"0\"], \"ball_id\": \"1\"}, {\"event\": [\"\", \"\", \"4\"], \"ball_id\": \"2\"}, {\"event\": [\"\", \"w\", \"0\"], \"ball_id\": \"3\"}, {\"event\": [\"\", \"w\", \"0\"], \"ball_id\": \"4\"}], \"17\": [{\"event\": [\"nb\", \"\", \"1\"], \"ball_id\": \"1\"}, {\"event\": [\"\", \"\", \"0\"], \"ball_id\": \"1\"}, {\"event\": [\"\", \"\", \"1\"], \"ball_id\": \"2\"}, {\"event\": [\"\", \"\", \"0\"], \"ball_id\": \"3\"}, {\"event\": [\"\", \"\", \"0\"], \"ball_id\": \"4\"}, {\"event\": [\"\", \"\", \"0\"], \"ball_id\": \"5\"}, {\"event\": [\"\", \"\", \"0\"], \"ball_id\": \"6\"}]}, \"upcoming_batsmen\": null, \"current_partnership\": [{\"player_2_runs\": \"0\", \"player_1_runs\": \"0\", \"player_1_id\": \"15431\", \"player_1_image\": \"\", \"player_1\": \"Tambe, PV\", \"player_2_balls\": \"1\", \"player_1_index\": \"1\", \"player_2_index\": \"2\", \"player_2\": \"Kaushik, S\", \"player_2_id\": \"16657\", \"player_1_balls\": \"0\", \"player_2_image\": \"\"}], \"toss\": \"Gujarat won the toss and elected to bowl\", \"man_of_the_match\": {}, \"venue\": \"M.Chinnaswamy Stadium\", \"umpires\": {\"first_umpire\": \"Dandekar, AN (IND)\", \"third_umpire\": \"Shamsuddin, C (IND)\", \"referee\": \"Madugalle, RS (SLA)\", \"second_umpire\": \"Tucker, RJ (AUS)\"}, \"current_bowler\": {\"runs\": \"4\", \"name\": \"Sachin Baby\", \"wicket\": \"2\", \"player_id\": \"15430\", \"overs\": \"0.4\", \"player_image\": \"\", \"economy\": 6.0}, \"last_wicket\": \"Kaushik, S,0(c:Aravind, S and b:Sachin Baby)\"}, \"series_id\": \"5181\", \"match_time\": 1463221800, \"result\": \"Live: GUJ 8/100 (17.5) Fol. RCB 3/248 (20.0)\", \"start_date\": \"2016-05-14T20:30:00\"}], \"success\": true, \"error\": false}";

            JSONObject jsonObject = new JSONObject(content);
            success = jsonObject.getBoolean("success");
            if (success) {
                response = jsonObject;
            } else {
                //nothing
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private boolean renderDisplay() {
        boolean success = false;
        try {
            if (!response.isNull("data")) {
                JSONArray dataArray = response.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject matchObject = dataArray.getJSONObject(i);
                    cricketMatchSummaryParser = new CompletedCricketMatchSummaryParser();
                    cricketMatchSummaryParser.setJsonObject(matchObject);
                    JSONObject matchSummary = cricketMatchSummaryParser.getMatchSummary();
                    cricketMatchSummaryParser.setCricketSummary(matchSummary);

                    JSONObject manOfTheMatch = cricketMatchSummaryParser.getManOfMatchDetails();
                    cricketMatchSummaryParser.setManOfTheMatch(manOfTheMatch);
                    if( manOfTheMatch != null && manOfTheMatch.length() > 0 ){
                        JSONObject batting = cricketMatchSummaryParser.getBattingDetails();
                        cricketMatchSummaryParser.setBatting(batting);
                        JSONObject bowling = cricketMatchSummaryParser.getBowlingDetails();
                        if (bowling != null) {
                            cricketMatchSummaryParser.setBowling(bowling);
                        }

                        Glide.with(ivPlayerProfileView.getContext()).load(cricketMatchSummaryParser.getPlayerImage()).placeholder(R.drawable.ic_user).dontAnimate().into(ivPlayerProfileView);
                        playerName.setText(cricketMatchSummaryParser.getPlayerName());
                        tvPlayerRun.setText(cricketMatchSummaryParser.getruns());
                        if (cricketMatchSummaryParser.getBalls().trim().equalsIgnoreCase("0".trim())) {
                            playedBallTag.setText("WICKET");
                            playerStrikeRate.setText("ECO");
                            tvPlayerPlayedBall.setText(cricketMatchSummaryParser.getBowling().getString("wickets"));
                            tvPlayerStrike_Rate.setText(cricketMatchSummaryParser.getBowling().getString("economy"));
                        } else {
                            playedBallTag.setText("BALL");
                            playerStrikeRate.setText("SR");
                            tvPlayerPlayedBall.setText(cricketMatchSummaryParser.getBalls());
                            tvPlayerStrike_Rate.setText(cricketMatchSummaryParser.getstrikerate());
                        }
                    }

                    {
                        JSONObject umpire = cricketMatchSummaryParser.getUmpireDetails();
                        cricketMatchSummaryParser.setUmpire(umpire);
                        if (umpire != null && umpire.length() > 0) {
                            tvUmpiresName.setText(cricketMatchSummaryParser.getFirstUmpire() + ", " + cricketMatchSummaryParser.secondFirstUmpire());
                            tvMatchReferee.setText(cricketMatchSummaryParser.getRefree());
                        }
                    }

                }
            } else {
                //nothing
            }

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return success;
    }

    public class MatchSummaryComponentListener extends CustomComponentListener {

        public MatchSummaryComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout, View contentLayout, SwipeRefreshLayout swipeRefreshLayout) {
            super(requestTag, progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = CricketSummaryHelper.this.handleContent(content);
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderDisplay();
            if (success) {
                contentLayout.setVisibility(View.VISIBLE);
            } else {
                showErrorLayout();
            }
        }

    }

}
