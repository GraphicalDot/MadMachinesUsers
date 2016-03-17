package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.sports.unity.R;
import com.sports.unity.scoredetails.BallDetail;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class CricketLiveMatchSummaryFragment extends Fragment implements  CricketLiveMatchSummaryHandler.LiveCricketMatchSummaryContentListener {

    private ImageView ivFirstBall;
    private ImageView ivSecondBall;
    private ImageView ivThirdBall;
    private ImageView ivFourthBall;
    private ImageView ivFifthBall;
    private ImageView ivSixthBall;
    private ImageView ivFirstPlayer;
    private TextView tvFirstPlayerName;
    private TextView tvFirstPlayerRunRate;
    private TextView tvFirstPlayerRunOnBall;
    private TextView tvPartnershipRecord;
    private TextView tvSecondPlayerName;
    private TextView tvSecondPlayerRunRate;
    private TextView tvSecondPlayerRunOnBall;
    private ImageView ivPlayerSecond;
    private ImageView ivUppComingPlayerFirst;
    private ImageView ivUppComingPlayerSecond;
    private ImageView ivUppComingPlayerThird;
    private TextView tvSecondUpComingPlayerName;
    private TextView tvThirdUpComingPlayerName;
    private TextView tvFirstUpComingPlayerName;
    private TextView tvFirstUpComingPlayerRunRate;
    private TextView tvSecondUpComingPlayerRunRate;
    private TextView tvThirdUpComingPlayerRunRate;
    private ImageView ivBowlerProfile;
    private TextView tvBowlerName;
    private TextView tvBowlerOverlabel;
    private TextView tvBowlerWRun;
    private TextView tvBowlerEcon;
    private TextView tvBowlerOver;
    private TextView tvBowlerWr;
    private ProgressBar progressBar;
    private String matchId;
    private Context context;
    private CricketLiveMatchSummaryHandler cricketLiveMatchSummaryHandler;
    private Timer timerToRefreshContent;
    private SwipeRefreshLayout swLivSummary;
    private boolean autRefreshEnabled;
    public CricketLiveMatchSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        matchId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        this.context = context;
        matchSummary();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cricket_live_match_summery, container, false);
        initView(view);

        return view;
    }
    private void initView(View view) {
        try {
            ivFirstBall = (ImageView) view.findViewById(R.id.iv_first_ball);
            ivSecondBall = (ImageView) view.findViewById(R.id.iv_second_ball);
            ivThirdBall = (ImageView) view.findViewById(R.id.iv_third_ball);
            ivFourthBall = (ImageView) view.findViewById(R.id.iv_fourth_ball);
            ivFifthBall = (ImageView) view.findViewById(R.id.iv_fifth_ball);
            ivSixthBall = (ImageView) view.findViewById(R.id.iv_sixth_ball);
            ivFirstPlayer = (ImageView) view.findViewById(R.id.iv_player_first);
            tvFirstPlayerName = (TextView) view.findViewById(R.id.tv_first_player_name);
            tvFirstPlayerRunRate = (TextView) view.findViewById(R.id.tv_first_player_run_rate);
            tvFirstPlayerRunOnBall = (TextView) view.findViewById(R.id.tv_first_player_run_on_ball);
            tvPartnershipRecord = (TextView) view.findViewById(R.id.tv_partnership_record);
            tvSecondPlayerName = (TextView) view.findViewById(R.id.tv_second_player_name);
            tvSecondPlayerRunRate = (TextView) view.findViewById(R.id.tv_second_player_run_rate);
            tvSecondPlayerRunOnBall = (TextView) view.findViewById(R.id.tv_second_player_run_on_ball);
            ivPlayerSecond = (ImageView) view.findViewById(R.id.iv_player_second);
            ivUppComingPlayerFirst = (ImageView) view.findViewById(R.id.iv_upp_coming_player_first);
            ivUppComingPlayerSecond = (ImageView) view.findViewById(R.id.iv_up_coming_player_second);
            ivUppComingPlayerThird = (ImageView) view.findViewById(R.id.iv_up_coming_player_third);
            tvSecondUpComingPlayerName = (TextView) view.findViewById(R.id.tv_second_up_coming_player_name);
            tvThirdUpComingPlayerName = (TextView) view.findViewById(R.id.tv_third_up_coming_player_name);
            tvFirstUpComingPlayerName = (TextView) view.findViewById(R.id.tv_first_up_coming_player_name);
            tvFirstUpComingPlayerRunRate = (TextView) view.findViewById(R.id.tv_first_up_coming_player_run_rate);
            tvSecondUpComingPlayerRunRate = (TextView) view.findViewById(R.id.tv_second_up_coming_player_run_rate);
            tvThirdUpComingPlayerRunRate = (TextView) view.findViewById(R.id.tv_third_up_coming_player_run_rate);
            ivBowlerProfile = (ImageView) view.findViewById(R.id.iv_bowler_profile);
            tvBowlerName = (TextView) view.findViewById(R.id.tv_bowler_name);
            tvBowlerOverlabel = (TextView) view.findViewById(R.id.tv_bowler_over_label);
            tvBowlerWRun = (TextView) view.findViewById(R.id.tv_bowler_W_Run);
            tvBowlerEcon = (TextView) view.findViewById(R.id.tv_bowler_econ);
            tvBowlerOver = (TextView) view.findViewById(R.id.tv_bowler_over);
            tvBowlerWr = (TextView) view.findViewById(R.id.tv_bowler_wr);
            swLivSummary = (SwipeRefreshLayout) view.findViewById(R.id.live_summary);
            initProgress(view);
            initErrorLayout(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
   }

    public void matchSummary(){
        cricketLiveMatchSummaryHandler = CricketLiveMatchSummaryHandler.getInstance(context);
        cricketLiveMatchSummaryHandler.addListener(this);
        cricketLiveMatchSummaryHandler.requestLiveMatchSummary(matchId);
        if(!autRefreshEnabled){
            enableAutoRefreshContent();
            autRefreshEnabled = true;
        }

    }


    private void enableAutoRefreshContent(){
        timerToRefreshContent = new Timer();
        timerToRefreshContent.schedule(new TimerTask() {

            @Override
            public void run() {
                matchSummary();
            }

        }, Constants.TIMEINMILISECOND, Constants.TIMEINMILISECOND);
    }

    @Override
    public void handleContent(String content) {
        try {
            showProgress();
            JSONObject object = new JSONObject(content);
            boolean success = object.getBoolean("success");
            boolean error = object.getBoolean("error");

            if (success) {

                renderDisplay(object);

            } else {

                showErrorLayout(getView());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorLayout(getView());
        }

    }
    private void initErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);

    }

    private void showErrorLayout(View view) {

       /* LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);*/

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

        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        if (!jsonObject.isNull("data")) {
        JSONArray dataArray = jsonObject.getJSONArray("data");
        JSONObject matchObject = dataArray.getJSONObject(0);
        final JSONArray recentOverArray = matchObject.getJSONArray("recent_overs");
        final JSONObject currentPartnershipDetails = matchObject.getJSONObject("current_partnership_details");
        final JSONArray yetToBatting = matchObject.getJSONArray("yet_to_bat");
        final JSONObject currentBowlerObject = matchObject.getJSONObject("current_bowler_details");
        final JSONArray bowlerStatsArray = currentBowlerObject.getJSONArray("stats");
        final JSONObject currentBowlerStatObject = bowlerStatsArray.getJSONObject(0);
        hideProgress();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BallDetail defb = new BallDetail();
                        BallDetail[] balls = new BallDetail[]{defb, defb, defb, defb, defb, defb,defb};
                        int ballIndex = 6;
                        Drawable drawable = null;
                        for (int i = 0; i < recentOverArray.length(); i++) {
                            JSONArray ballsArray = recentOverArray.getJSONArray(i);
                            JSONArray over = ballsArray.getJSONArray(1);
                            for (int j = over.length()-1; j >= 0; j--) {

                                balls[ballIndex] = getResolveBall(over.getString(j));
                                ballIndex--;
                                if (ballIndex < 0) {
                                    break;
                                }
                            }
                            if (ballIndex < 0) {
                                break;
                            }
                       }
                            if (!balls[0].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[0].getValue(), balls[0].getFontColor(), balls[0].getBackGroundColor());
                                ivFirstBall.setImageDrawable(drawable);
                            } else {
                                ivFirstBall.setImageResource(R.drawable.recent_dot_balls);
                            }
                            if (!balls[1].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[1].getValue(), balls[1].getFontColor(), balls[1].getBackGroundColor());
                                ivSecondBall.setImageDrawable(drawable);
                            } else {
                                ivSecondBall.setImageResource(R.drawable.recent_dot_balls);
                            }
                            if (!balls[2].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[2].getValue(), balls[2].getFontColor(), balls[2].getBackGroundColor());
                                ivThirdBall.setImageDrawable(drawable);
                            } else {
                                ivThirdBall.setImageResource(R.drawable.recent_dot_balls);
                            }

                            if (!balls[3].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[3].getValue(), balls[3].getFontColor(), balls[3].getBackGroundColor());
                                ivFourthBall.setImageDrawable(drawable);
                            } else {
                                ivFourthBall.setImageResource(R.drawable.recent_dot_balls);
                            }
                            if (!balls[4].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[4].getValue(), balls[4].getFontColor(), balls[4].getBackGroundColor());
                                ivFifthBall.setImageDrawable(drawable);
                            } else {
                                ivFifthBall.setImageResource(R.drawable.recent_dot_balls);
                            }
                            if (!balls[5].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[5].getValue(), balls[5].getFontColor(), balls[5].getBackGroundColor());
                                ivSixthBall.setImageDrawable(drawable);
                            } else {
                                ivSixthBall.setImageResource(R.drawable.recent_dot_balls);
                            }

                        //TextDrawable  playerNameDraw = null;


                        if (!currentPartnershipDetails.isNull("player_a")) {
                            tvFirstPlayerName.setText(currentPartnershipDetails.getString("player_a"));
                            /*playerNameDraw = getTextDrawable(currentPartnershipDetails.getString("player_a").substring(0,1),Color.WHITE,Color.BLUE);
                            ivFirstPlayer.setImageDrawable(playerNameDraw);*/
                        }

                        if (!currentPartnershipDetails.isNull("player_b")) {
                            tvSecondPlayerName.setText(currentPartnershipDetails.getString("player_b"));
                            /*playerNameDraw = getTextDrawable(currentPartnershipDetails.getString("player_b").substring(0,1),Color.WHITE,Color.BLUE);
                            ivPlayerSecond.setImageDrawable(playerNameDraw);*/
                        }

                        if (!currentPartnershipDetails.isNull("player_a_strikerate"))
                            tvFirstPlayerRunRate.setText(currentPartnershipDetails.getString("player_a_strikerate"));
                        if (!currentPartnershipDetails.isNull("player_b_strikerate"))
                            tvSecondPlayerRunRate.setText(currentPartnershipDetails.getString("player_b_strikerate"));
                        if (!currentPartnershipDetails.isNull("player_a_runs"))
                            tvFirstPlayerRunOnBall.setText(currentPartnershipDetails.getString("player_a_runs") + "(" + currentPartnershipDetails.getString("player_a_balls") + ")");
                        if (!currentPartnershipDetails.isNull("player_b_runs"))
                            tvSecondPlayerRunOnBall.setText(currentPartnershipDetails.getString("player_b_runs") + "(" + currentPartnershipDetails.getString("player_b_balls") + ")");
                        if (!currentPartnershipDetails.isNull("partnership_runs"))
                            tvPartnershipRecord.setText(currentPartnershipDetails.getString("partnership_runs") + "(" + currentPartnershipDetails.getString("partnership_balls") + ")");

                        if (yetToBatting.length() > 3) {
                            tvFirstUpComingPlayerName.setText(yetToBatting.getString(0));
                            /*playerNameDraw = getTextDrawable(yetToBatting.getString(0).substring(0,1),Color.WHITE,Color.BLUE);
                            ivUppComingPlayerFirst.setImageDrawable(playerNameDraw);*/
                            tvSecondUpComingPlayerName.setText(yetToBatting.getString(1));
                            /*playerNameDraw = getTextDrawable(yetToBatting.getString(1).substring(0,1),Color.WHITE,Color.BLUE);
                            ivUppComingPlayerSecond.setImageDrawable(playerNameDraw);*/
                            tvThirdUpComingPlayerName.setText(yetToBatting.getString(2));
                           /* playerNameDraw = getTextDrawable(yetToBatting.getString(2).substring(0,1),Color.WHITE,Color.BLUE);
                            ivUppComingPlayerThird.setImageDrawable(playerNameDraw);*/
                        } else if (yetToBatting.length() == 2) {
                            tvFirstUpComingPlayerName.setText(yetToBatting.getString(0));
                           /* playerNameDraw = getTextDrawable(yetToBatting.getString(0).substring(0,1),Color.WHITE,Color.BLUE);
                            ivUppComingPlayerFirst.setImageDrawable(playerNameDraw);*/
                            tvSecondUpComingPlayerName.setText(yetToBatting.getString(1));
                            /*playerNameDraw = getTextDrawable(yetToBatting.getString(1).substring(0,1),Color.WHITE,Color.BLUE);
                            ivUppComingPlayerSecond.setImageDrawable(playerNameDraw);*/
                            tvThirdUpComingPlayerName.setText("N/A");
                        } else if (yetToBatting.length() == 1) {
                            tvFirstUpComingPlayerName.setText(yetToBatting.getString(0));
                            /*playerNameDraw = getTextDrawable(yetToBatting.getString(0).substring(0,1),Color.WHITE,Color.RED);
                            ivUppComingPlayerFirst.setImageDrawable(playerNameDraw);*/
                            tvSecondUpComingPlayerName.setText("N/A");
                            tvThirdUpComingPlayerName.setText("N/A");
                        } else if (yetToBatting.length() == 0) {
                            tvFirstUpComingPlayerName.setText("N/A");
                            tvSecondUpComingPlayerName.setText("N/A");
                            tvThirdUpComingPlayerName.setText("N/A");
                        }
                        if (!currentBowlerObject.isNull("name")) {
                            tvBowlerName.setText(currentBowlerObject.getString("name"));
                            /*playerNameDraw = getTextDrawable(currentBowlerObject.getString("name").substring(0,1),Color.WHITE,Color.BLUE);
                            ivBowlerProfile.setImageDrawable(playerNameDraw);*/
                        }

                        if (!currentBowlerStatObject.isNull("economy"))
                            tvBowlerEcon.setText("ECON " + currentBowlerStatObject.getString("economy"));
                        if (!currentBowlerStatObject.isNull("overs"))
                            tvBowlerOver.setText(currentBowlerStatObject.getString("overs"));
                       /* if(!currentBowlerStatObject.isNull("wickets"))
                            tvBowlerWr.setText(currentBowlerStatObject.getString("wickets"));*/
                        if (!currentBowlerStatObject.isNull("runs_conceded") && !currentBowlerStatObject.isNull("wickets"))
                            tvBowlerWRun.setText(currentBowlerStatObject.getString("wickets") + "/" + currentBowlerStatObject.getString("runs_conceded"));


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




    private Drawable getTextDrawable(String text,int color, int backGroundColor){
        int radius = getContext().getResources().getDimensionPixelSize(R.dimen.recent_ball_radius);
        /*int border = getContext().getResources().getDimensionPixelSize(R.dimen.group_image_border);*/
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig().textColor(color)
                .width(radius)
                .height(radius)
                .bold()
                .endConfig()
                .buildRound(text, backGroundColor);
        return  drawable;
    }




    private BallDetail getResolveBall(String value){
        BallDetail ballDetail = new BallDetail();
        switch (value){
            case "r0":
                ballDetail.setValue("0");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.grayBorder));
                break;
            case "r1":
                ballDetail.setValue("1");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.grayBorder));
                break;
            case "r2":
                ballDetail.setValue("2");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.grayBorder));
                break;
            case "r3":
                ballDetail.setValue("3");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.grayBorder));
                break;
            case "b4":
                ballDetail.setValue("4");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary));
                break;
            case "r5":
                ballDetail.setValue("5");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.font_color_wide_no));
                break;
            case "b6":
                ballDetail.setValue("6");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary));
                break;
            case "e1,wd":
                ballDetail.setValue("WD");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary));
                break;
            case "e1,by":
                ballDetail.setValue("B");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary));
                break;
            case "e1,lb":
                ballDetail.setValue("LB");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary));
                break;
            case  "e1,nb":
                ballDetail.setValue("NB");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary_no));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary));
                break;

            case "w":
                ballDetail.setValue("W");
                ballDetail.setFontColor(getBallColor(R.color.font_color_wicket));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_wicket));
                break;
            case "r2,e1,nb":
                ballDetail.setValue("2NB");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary_no));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary_no));
                break;
            case "r2,nb,b4":
                ballDetail.setValue("4NB");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary_no));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary_no));
                break;
       }
        return    ballDetail;
    }

    private int getBallColor(int id) {

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return getContext().getResources().getColor(id, getActivity().getTheme());
            } else {
                return getContext().getResources().getColor(id);
            }*/
        return getContext().getResources().getColor(id);

    }



    @Override
    public void onPause() {
        super.onPause();
        if(cricketLiveMatchSummaryHandler != null){
            cricketLiveMatchSummaryHandler.addListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
       showProgress();
        if(cricketLiveMatchSummaryHandler != null){
            cricketLiveMatchSummaryHandler.addListener(this);

        }else {
            cricketLiveMatchSummaryHandler= CricketLiveMatchSummaryHandler.getInstance(getContext());
        }
        cricketLiveMatchSummaryHandler.requestLiveMatchSummary(matchId);
    }

}
