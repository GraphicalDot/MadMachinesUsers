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
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.scores.model.football.CricketMatchJsonCaller;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

public class CricketCompletedMatchSummaryFragment extends Fragment implements CricketCompletedMatchSummaryHandler.CricketCompletedMatchSummaryContentListener {

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
    private ProgressBar progressBar;
    String toss = "";
    String matchName="";
    String date = "";
    CricketCompletedMatchSummaryHandler cricketCompletedMatchSummaryHandler;
    private String matchId;
    private CricketMatchJsonCaller cricketMatchJsonCaller;

    public CricketCompletedMatchSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ScoreDetailActivity scoreDetail = (ScoreDetailActivity) getActivity();
        Intent i = scoreDetail.getIntent();
        matchId =  i.getStringExtra(INTENT_KEY_ID);
        matchName = i.getStringExtra(INTENT_KEY_MATCH_NAME);
        toss = i.getStringExtra(INTENT_KEY_TOSS);
        date = i.getStringExtra(INTENT_KEY_DATE);

        cricketCompletedMatchSummaryHandler = CricketCompletedMatchSummaryHandler.getInstance(context);
        cricketCompletedMatchSummaryHandler.addListener(this);
        cricketCompletedMatchSummaryHandler.requestCompletedMatchSummary(matchId);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cricket_completed_match_summery, container, false);
        initView(view);

        return view;
    }
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
        tvSeriesName.setText(matchName);
        tvTossWinTeam.setText(toss);
        initProgress(view);
        initErrorLayout(view);

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
                    "      \"match_time\": 1458466200,\n" +
                    "      \"result\": \"Live: AFG 4/132 (14.0) Fol. SAF 5/209 (20.0)\",\n" +
                    "      \"series_id\": \"5166\",\n" +
                    "      \"series_name\": \"T20I: World '16\",\n" +
                    "      \"start_date\": \"2016-03-20T20:30:00\",\n" +
                    "      \"status\": \"L\",\n" +
                    "      \"summary\": {\n" +
                    "        \"current_bowler\": {\n" +
                    "          \"name\": \"Wiese, D\",\n" +
                    "          \"overs\": \"3.3\",\n" +
                    "          \"player_id\": \"15239\",\n" +
                    "          \"runs\": \"45\",\n" +
                    "          \"wicket\": \"0\"\n" +
                    "        },\n" +
                    "        \"current_partnership\": [\n" +
                    "          {\n" +
                    "            \"player_1\": \"Mohammad Nabi\",\n" +
                    "            \"player_1_balls\": \"7\",\n" +
                    "            \"player_1_id\": \"7796\",\n" +
                    "            \"player_1_index\": \"1\",\n" +
                    "            \"player_1_runs\": \"7\",\n" +
                    "            \"player_2\": \"Samiullah Shenwari\",\n" +
                    "            \"player_2_balls\": \"11\",\n" +
                    "            \"player_2_id\": \"7800\",\n" +
                    "            \"player_2_index\": \"2\",\n" +
                    "            \"player_2_runs\": \"22\"\n" +
                    "          }\n" +
                    "        ],\n" +
                    "        \"last_wicket\": \"Noor Ali Zadran,25(ST de Kock, Q)\",\n" +
                    "        \"man_of_the_match\": {},\n" +
                    "        \"recent_over\": [\n" +
                    "          {\n" +
                    "            \"ball_id\": \"1\",\n" +
                    "            \"over\": \"14\",\n" +
                    "            \"runs\": \"4\",\n" +
                    "            \"wicket\": \"false\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"ball_id\": \"2\",\n" +
                    "            \"over\": \"14\",\n" +
                    "            \"runs\": \"0\",\n" +
                    "            \"wicket\": \"false\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"ball_id\": \"3\",\n" +
                    "            \"over\": \"14\",\n" +
                    "            \"runs\": \"1\",\n" +
                    "            \"wicket\": \"false\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"ball_id\": \"3\",\n" +
                    "            \"over\": \"14\",\n" +
                    "            \"runs\": \"1\",\n" +
                    "            \"wicket\": \"false\"\n" +
                    "          }\n" +
                    "        ],\n" +
                    "        \"toss\": \"South Africa won the toss and elected to bat\",\n" +
                    "        \"umpires\": {\n" +
                    "          \"first_umpire\": \"Gaffaney, CB (NZL)\",\n" +
                    "          \"referee\": \"Boon, DC (AUS)\",\n" +
                    "          \"second_umpire\": \"Reiffel, PR (AUS)\",\n" +
                    "          \"third_umpire\": \"Ravi, S (IND)\"\n" +
                    "        },\n" +
                    "        \"upcoming_batsmen\": [\n" +
                    "          {\n" +
                    "            \"name\": \"Najibullah Zadran\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"name\": \"Rashid Khan\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"name\": \"Amir Hamza\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"name\": \"Dawlat Zadran\"\n" +
                    "          },\n" +
                    "          {\n" +
                    "            \"name\": \"Shapoor Zadran\"\n" +
                    "          }\n" +
                    "        ],\n" +
                    "        \"venue\": \"Wankhede Stadium\"\n" +
                    "      },\n" +
                    "      \"venue\": \"Wankhede Stadium\"\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"error\": false,\n" +
                    "  \"success\": true\n" +
                    "}";
            try {
                showProgress();
                JSONObject jsonObject = new JSONObject(content);
                boolean success = jsonObject.getBoolean("success");
                boolean error = jsonObject.getBoolean("error");

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
    private void initProgress(View view) {
         progressBar = (ProgressBar) view.findViewById(R.id.progress);
         progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
    }
    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

    }
    private void hideProgress() {
        progressBar.setVisibility(View.GONE);

    }
    private void renderDisplay(final JSONObject jsonObject) throws JSONException {
        final ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        hideProgress();
        if(!jsonObject.isNull("data")){
            JSONArray dataArray= jsonObject.getJSONArray("data");
            final JSONObject matchObject = dataArray.getJSONObject(0);

            final JSONObject manOftheMatch = matchObject.getJSONObject("man_of_match_details");
            JSONObject battingData = null;

              final JSONArray statsArray= manOftheMatch.getJSONArray("stats");
              final JSONObject statObject = statsArray.getJSONObject(0);



            if (activity != null) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                           // setComplatedCricketSummary(jsonObject, manOftheMatch, statObject);






                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showErrorLayout(getView());
                        }
                    }
                });
            }
        } else {
            showErrorLayout(getView());
        }
    }

    private void setComplatedCricketSummary(JSONObject jsonObject, JSONObject manOftheMatch, JSONObject statObject) throws JSONException {
        Log.i("run: ", jsonObject.toString());
        if( manOftheMatch != null &&  !manOftheMatch.isNull("image")){
            Glide.with(getContext()).load(manOftheMatch.getString("image")).placeholder(R.drawable.ic_no_img).into(ivPlayerProfileView);
            Glide.with(getContext()).load(manOftheMatch.getString("image")).placeholder(R.drawable.ic_no_img).into(ivCountryImage);
        }
        if( manOftheMatch!= null && !manOftheMatch.isNull("name")){
            playerName.setText(manOftheMatch.getString("name"));
        }
        if(!statObject.isNull("runs")){
            tvPlayerRun.setText(statObject.getString("runs"));
        }else {
            tvPlayerRun.setText("N/A");
        }

        if(!statObject.isNull("balls")){
            tvPlayerPlayedBall.setText(statObject.getString("balls"));
        }   else {
            tvPlayerPlayedBall.setText("N/A");
            }

        if(!statObject.isNull("strike_rate")) {
            tvPlayerStrike_Rate.setText(statObject.getString("strike_rate"));}else{
            tvPlayerStrike_Rate.setText("N/A");
        }
        tvMatchDate.setText(DateUtil.getFormattedDate(date));
        tvTossWinTeam.setText(toss);
        tvSeriesName.setText(matchName);
        tvUmpiresName.setText("N/A");
        tvMatchReferee.setText("N/A");
    }

    @Override
    public void onPause() {
        super.onPause();
        if(cricketCompletedMatchSummaryHandler != null){
            cricketCompletedMatchSummaryHandler.addListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress();
        if(cricketCompletedMatchSummaryHandler != null){
            cricketCompletedMatchSummaryHandler.addListener(this);

        }else {
            cricketCompletedMatchSummaryHandler= CricketCompletedMatchSummaryHandler.getInstance(getContext());
        }
        cricketCompletedMatchSummaryHandler.requestCompletedMatchSummary(matchId);
    }
    public void handleError(){
        showErrorLayout(getView());
    }

}
