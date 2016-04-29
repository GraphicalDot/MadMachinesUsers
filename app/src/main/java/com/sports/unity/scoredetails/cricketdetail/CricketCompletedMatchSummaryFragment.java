package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.playerprofile.cricket.PlayerCricketBioDataActivity;
import com.sports.unity.scoredetails.cricketdetail.JsonParsers.CompletedCricketMatchSummaryParser;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.football.CricketMatchJsonCaller;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_SERIES;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

public class CricketCompletedMatchSummaryFragment extends BasicVolleyRequestResponseViewHelper {

    private HashMap<String, String> parameters = null;

    private String matchId;
    private String seriesId;
    private String toss = "";
    private String matchName = "";
    private String date = "";

    private ImageView ivPlayerProfileView;
    private ImageView ivCountryImage;
    private TextView playerName;
    private TextView tvPlayerRun;
    private TextView tvPlayerPlayedBall;
    private TextView tvPlayerStrike_Rate;
    private TextView tvSeriesName;
    private TextView tvMatchDate;
    private TextView tvTossWinTeam;
    private TextView tvUmpiresName;
    private TextView tvMatchReferee;
    private TextView playedBallTag;
    private TextView playerStrikeRate;

    private JSONObject response = null;

    private CompletedCricketMatchSummaryParser cricketMatchSummaryParser;

    private String title = null;

    public CricketCompletedMatchSummaryFragment(String title) {
        this.title = title;
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
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress);

        MatchSummaryComponentListener componentListener = new MatchSummaryComponentListener( getRequestTag(), progressBar, errorLayout);
        return componentListener;
    }

    @Override
    public String getRequestTag() {
        return "CricketCompletedMatchRequestTag";
    }

    @Override
    public String getRequestCallName() {
        return ScoresContentHandler.CALL_NAME_CRICKET_MATCH_SUMMARY;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return parameters;
    }

    @Override
    public void initialiseViews(View view) {
        initView(view);
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    //    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        ScoreDetailActivity scoreDetail = (ScoreDetailActivity) getActivity();
//        Intent i = scoreDetail.getIntent();
//        matchId = i.getStringExtra(INTENT_KEY_ID);
//        matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
//        toss = i.getStringExtra(INTENT_KEY_TOSS);
//        date = i.getStringExtra(INTENT_KEY_DATE);
//        seriesId = i.getStringExtra(INTENT_KEY_SERIES);
//
////        cricketCompletedMatchSummaryHandler = CricketCompletedMatchSummaryHandler.getInstance(context);
////        cricketCompletedMatchSummaryHandler.addListener(this);
////        cricketCompletedMatchSummaryHandler.requestCompletedMatchSummary(seriesId, matchId);
//
//    }

    private void initView(View view) {
        ivPlayerProfileView = (ImageView) view.findViewById(R.id.iv_player_profile_image);
        ivCountryImage = (ImageView) view.findViewById(R.id.iv_country_image);
        playerName = (TextView) view.findViewById(R.id.tv_player_name);
        tvPlayerRun = (TextView) view.findViewById(R.id.tv_player_run);
        tvPlayerPlayedBall = (TextView) view.findViewById(R.id.tv_player_played_ball);
        tvPlayerStrike_Rate = (TextView) view.findViewById(R.id.tv_player_strike_rate);
        tvSeriesName = (TextView) view.findViewById(R.id.tv_series_name);
        tvMatchDate = (TextView) view.findViewById(R.id.tv_match_date);
        tvTossWinTeam = (TextView) view.findViewById(R.id.tv_toss_win_team);
        tvUmpiresName = (TextView) view.findViewById(R.id.tv_umpires_name);
        tvMatchReferee = (TextView) view.findViewById(R.id.tv_match_referee);
        playedBallTag = (TextView) view.findViewById(R.id.tv_player_ball_tag);
        playerStrikeRate = (TextView) view.findViewById(R.id.tv_player_sr_t);
        tvSeriesName.setText(matchName);
        tvTossWinTeam.setText(toss);

        ivPlayerProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cricketMatchSummaryParser != null) {
                    try {
                        String playerId = cricketMatchSummaryParser.getPlayerId();
                        Intent intent = PlayerCricketBioDataActivity.createIntent( v.getContext(), playerId, playerName.getText().toString());
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
                        Intent intent = PlayerCricketBioDataActivity.createIntent( v.getContext(), playerId, playerName.getText().toString());
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
                    final JSONObject matchObject = dataArray.getJSONObject(i);
                    cricketMatchSummaryParser = new CompletedCricketMatchSummaryParser();
                    cricketMatchSummaryParser.setJsonObject(matchObject);
                    JSONObject matchSummary = cricketMatchSummaryParser.getMatchSummary();
                    cricketMatchSummaryParser.setCricketSummary(matchSummary);
                    JSONObject manOftheMatch = cricketMatchSummaryParser.getManOfMatchDetails();
                    cricketMatchSummaryParser.setManOfTheMatch(manOftheMatch);
                    final JSONObject batting = cricketMatchSummaryParser.getBattingDetails();
                    cricketMatchSummaryParser.setBatting(batting);
                    final JSONObject umpire = cricketMatchSummaryParser.getUmpireDetails();
                    cricketMatchSummaryParser.setUmpire(umpire);
                    final JSONObject bowling = cricketMatchSummaryParser.getBowlingDetails();
                    if (bowling != null) {
                        cricketMatchSummaryParser.setBowling(bowling);
                    }

                    {
                        Glide.with( ivPlayerProfileView.getContext()).load(cricketMatchSummaryParser.getPlayerImage()).placeholder(R.drawable.ic_user).into(ivPlayerProfileView);
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

                        tvMatchDate.setText(DateUtil.getFormattedDate(date));
                        tvTossWinTeam.setText(toss);
                        tvSeriesName.setText(matchName);
                        tvUmpiresName.setText(cricketMatchSummaryParser.getFirstUmpire() + ", " + cricketMatchSummaryParser.secondFirstUmpire());
                        tvMatchReferee.setText(cricketMatchSummaryParser.getRefree());
                    }
                }
            } else {
                //nothing
            }

            success = true;
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return success;
    }


//    private void setComplatedCricketSummary(JSONObject jsonObject, JSONObject manOftheMatch, JSONObject statObject) throws JSONException {
//        Log.i("run: ", jsonObject.toString());
//        if (manOftheMatch != null && !manOftheMatch.isNull("image")) {
//            Glide.with(getContext()).load(manOftheMatch.getString("image")).placeholder(R.drawable.ic_user).into(ivPlayerProfileView);
//            Glide.with(getContext()).load(manOftheMatch.getString("image")).placeholder(R.drawable.ic_user).into(ivCountryImage);
//        }
//        if (manOftheMatch != null && !manOftheMatch.isNull("name")) {
//            playerName.setText(manOftheMatch.getString("name"));
//        }
//        if (!statObject.isNull("runs")) {
//            tvPlayerRun.setText(statObject.getString("runs"));
//        } else {
//            tvPlayerRun.setText("N/A");
//        }
//
//        if (!statObject.isNull("balls")) {
//            tvPlayerPlayedBall.setText(statObject.getString("balls"));
//        } else {
//            tvPlayerPlayedBall.setText("N/A");
//        }
//
//        if (!statObject.isNull("strike_rate")) {
//            tvPlayerStrike_Rate.setText(statObject.getString("strike_rate"));
//        } else {
//            tvPlayerStrike_Rate.setText("N/A");
//        }
//        tvMatchDate.setText(DateUtil.getFormattedDate(date));
//        tvTossWinTeam.setText(toss);
//        tvSeriesName.setText(matchName);
//        tvUmpiresName.setText("N/A");
//        tvMatchReferee.setText("N/A");
//    }

    public class MatchSummaryComponentListener extends CustomComponentListener {

        public MatchSummaryComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
            super(requestTag, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = CricketCompletedMatchSummaryFragment.this.handleContent(content);
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderDisplay();
            if( success ){
                //nothing
            } else {
                showErrorLayout();
            }
        }

    }

}
