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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_ID;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;

public class CricketCompletedMatchSummeryFragment extends Fragment implements CricketCompletedMatchSummaryHandler.CricketCompletedMatchSummaryContentListener {

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

    public CricketCompletedMatchSummeryFragment() {
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
        showProgress();
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
        initProgress(view);
        initErrorLayout(view);

    }
    @Override
    public void handleContent(JSONObject object) {
        {
            try {
                boolean success = object.getBoolean("success");
                boolean error = object.getBoolean("error");

                if( success ) {

                    renderDisplay(object);

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
        hideProgress();
        final ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        JSONArray dataArray= jsonObject.getJSONArray("data");
        final JSONObject matchObject = dataArray.getJSONObject(0);
        final JSONObject manOftheMatch = matchObject.getJSONObject("man_of_match_details");
        JSONObject inningObject= manOftheMatch.getJSONObject("innings");
        JSONObject battingData = inningObject.getJSONObject("1");
        final JSONObject battingObject= battingData.getJSONObject("batting");
        if (activity != null) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i("run: ", jsonObject.toString());
                        Glide.with(getContext()).load(manOftheMatch.getString("image")).placeholder(R.drawable.ic_no_img).into(ivPlayerProfileView);
                        Glide.with(getContext()).load(manOftheMatch.getString("image")).placeholder(R.drawable.ic_no_img).into(ivCountryImage);
                        playerName.setText(matchObject.getString("man_of_match"));
                        if(!battingObject.isNull("runs"))
                        tvPlayerRun.setText(battingObject.getString("runs"));
                        if(!battingObject.isNull("balls"))
                        tvPlayerPlayedBall.setText(battingObject.getString("balls"));
                        if(!battingObject.isNull("strike_rate"))
                        tvPlayerStrike_Rate.setText(battingObject.getString("strike_rate"));
                        tvMatchDate.setText(date);
                        tvTossWinTeam.setText(toss);
                        tvSeriesName.setText(matchName);
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
}
