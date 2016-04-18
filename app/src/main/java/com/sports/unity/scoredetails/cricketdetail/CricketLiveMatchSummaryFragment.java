package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.scoredetails.BallDetail;
import com.sports.unity.scoredetails.cricketdetail.JsonParsers.LiveCricketMatchSummaryParser;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Stack;
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
    private TextView  vifirsttv[] = new TextView[6];

    private View[] dividerView=new View[6];
    private ImageView ivBowlerProfile;
    private TextView tvBowlerName;
    private TextView tvBowlerWRun;
    private TextView tvBowlerEcon;
    private TextView tvBowlerOver;
    private ProgressBar progressBar;
    private String matchId;
    private Context context;
    private CricketLiveMatchSummaryHandler cricketLiveMatchSummaryHandler;
    private Timer timerToRefreshContent;
    private String seriesId;

    private SwipeRefreshLayout swLivSummary;
    private boolean autRefreshEnabled;
    private String  recentOverValue;
    public CricketLiveMatchSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        matchId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        seriesId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_SERIES);
        this.context = context;
        matchSummary();
        enableAutoRefreshContent();
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
            ivBowlerProfile = (ImageView) view.findViewById(R.id.iv_bowler_profile);
            tvBowlerName = (TextView) view.findViewById(R.id.tv_bowler_name);
            tvBowlerWRun = (TextView) view.findViewById(R.id.tv_bowler_W_Run);
            tvBowlerEcon = (TextView) view.findViewById(R.id.tv_bowler_econ);
            tvBowlerOver = (TextView) view.findViewById(R.id.tv_bowler_over);
            swLivSummary = (SwipeRefreshLayout) view.findViewById(R.id.live_summary);
            vifirsttv[0]=(TextView) view.findViewById(R.id.vi_start_tv);
            vifirsttv[1]=(TextView) view.findViewById(R.id.vi_first_tv);
            vifirsttv[2]=(TextView) view.findViewById(R.id.vi_second_tv);
            vifirsttv[3]=(TextView) view.findViewById(R.id.vi_third_tv);
            vifirsttv[4]=(TextView) view.findViewById(R.id.vi_four_tv);
            vifirsttv[5]=(TextView) view.findViewById(R.id.vi_five_tv);
            dividerView[0]= view.findViewById(R.id.vi_start);
            dividerView[1]= view.findViewById(R.id.vi_first);
            dividerView[2]=view.findViewById(R.id.vi_second);
            dividerView[3]=view.findViewById(R.id.vi_third);
            dividerView[4]=view.findViewById(R.id.vi_four);
            dividerView[5]=view.findViewById(R.id.vi_five);



            swLivSummary.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    matchSummary();
                    swLivSummary.setRefreshing(true);
                }
            });







            initProgress(view);
            initErrorLayout(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void matchSummary(){
        cricketLiveMatchSummaryHandler = CricketLiveMatchSummaryHandler.getInstance(context);
        cricketLiveMatchSummaryHandler.addListener(this);
        cricketLiveMatchSummaryHandler.requestLiveMatchSummary(seriesId, matchId);
        if(!autRefreshEnabled){
            enableAutoRefreshContent();
            autRefreshEnabled = true;
        }

    }


    private void enableAutoRefreshContent(){
        timerToRefreshContent = new Timer();
        if(autRefreshEnabled){
            timerToRefreshContent.schedule(new TimerTask() {

                @Override
                public void run() {
                    matchSummary();
                }

            }, Constants.TIMEINMILISECOND, Constants.TIMEINMILISECOND);
        }else{
            timerToRefreshContent.cancel();
        }

    }

    @Override
    public void handleContent(String content) {
        try {
            showProgress();
            JSONObject object = new JSONObject(content);
            boolean success = object.getBoolean("success");

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
        swLivSummary.setRefreshing(false);
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        if (!jsonObject.isNull("data")) {
            JSONArray dataArray = jsonObject.getJSONArray("data");
            JSONObject matchObject = dataArray.getJSONObject(0);
            final LiveCricketMatchSummaryParser liveCricketMatchSummaryParser = new LiveCricketMatchSummaryParser();
            liveCricketMatchSummaryParser.setJsonObject(matchObject);
            liveCricketMatchSummaryParser.setCricketSummary(liveCricketMatchSummaryParser.getMatchSummary());
            final JSONObject recentOver = liveCricketMatchSummaryParser.getRecentOver();
            JSONArray currentPartnershipDetails = liveCricketMatchSummaryParser.getCurrentPartnership();
            JSONArray yetToBatting = liveCricketMatchSummaryParser.getUpCommingBatsMan();
            JSONObject currentBowlerObject = liveCricketMatchSummaryParser.getCurentBowler();
            if (currentBowlerObject != null) {
            liveCricketMatchSummaryParser.setCurrentBowler(currentBowlerObject);
            liveCricketMatchSummaryParser.setCurrentPartnership(currentPartnershipDetails.getJSONObject(0));
            liveCricketMatchSummaryParser.setYetToBat(yetToBatting);
            liveCricketMatchSummaryParser.setRecentOver(recentOver);
               final Stack<JSONObject> ballsStack = new Stack<>();


                BallDetail defb = new BallDetail();
                final BallDetail[] balls = new BallDetail[]{defb, defb, defb, defb, defb, defb};
                int ballIndex = 5;

                Iterator<String> recentOverKeys = recentOver.keys();
                Integer keys[]= new Integer[2];
                int arrayCount= 0;

                while(recentOverKeys.hasNext()){
                    try{
                        keys[arrayCount++] = Integer.parseInt(recentOverKeys.next());
                    }catch (Exception e){e.printStackTrace();}

                }
                if(keys[0]<keys[1]){
                    recentOverValue = keys[1].toString();
                    JSONArray recentOverJSONArray = recentOver.getJSONArray(keys[0].toString());
                    for(int count=0;count<recentOverJSONArray.length();count++){
                        JSONObject ballObject = recentOverJSONArray.getJSONObject(count);
                        ballsStack.add(ballObject);
                    }

                    recentOverJSONArray = recentOver.getJSONArray(keys[1].toString());
                    for(int count=0;count<recentOverJSONArray.length();count++){
                        JSONObject ballObject = recentOverJSONArray.getJSONObject(count);
                        ballsStack.add(ballObject);
                    }

                }else{
                    recentOverValue = keys[0].toString();
                    JSONArray recentOverJSONArray = recentOver.getJSONArray(keys[1].toString());
                    for(int count=0;count<recentOverJSONArray.length();count++){
                        JSONObject ballObject = recentOverJSONArray.getJSONObject(count);
                        ballsStack.add(ballObject);
                    }

                    recentOverJSONArray = recentOver.getJSONArray(keys[0].toString());
                    for(int count=0;count<recentOverJSONArray.length();count++){
                        JSONObject ballObject = recentOverJSONArray.getJSONObject(count);
                        ballsStack.add(ballObject);
                    }
                }
                int queuSize = ballsStack.size();
                for (int i = 0; i < queuSize; i++) {
                    if(i==6){
                        break;
                    }

                    BallDetail curBall = null;
                    JSONObject object = ballsStack.pop();

                    int ballId = object.getInt("ball_id");
                    JSONArray eventArray = object.getJSONArray("event");
                    String event = eventArray.getString(0);
                    String wicket = eventArray.getString(1);
                    String run = eventArray.getString(2);
                    if (wicket!=null && !wicket.equals("")) {
                        curBall = getResolveBall(wicket);
                    }else if(event!=null && !event.equals("")) {
                        curBall = getResolveBall(event);
                    }else  {
                        curBall = getResolveBall(run);
                    }
                    curBall.setBallId(ballId);
                    balls[ballIndex] = curBall;
                    ballIndex--;
                }
                hideProgress();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Drawable drawable = null;
                            int count = 0;

                         if (!balls[0].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[count].getValue(), balls[count].getFontColor(), balls[count].getBackGroundColor());
                                ivFirstBall.setImageDrawable(drawable);
                                setBallOverWise(balls, count);

                         } else {
                                ivFirstBall.setImageResource(R.drawable.recent_dot_balls);
                                 setBallOverWise(balls, count);
                         }
                            count++;

                            if (!balls[count].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[count].getValue(), balls[count].getFontColor(), balls[count].getBackGroundColor());
                                ivSecondBall.setImageDrawable(drawable);
                                setBallOverWise(balls, count);
                            } else {
                                ivSecondBall.setImageResource(R.drawable.recent_dot_balls);
                                setBallOverWise(balls, count);
                            }
                            count++;
                            if (!balls[count].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[count].getValue(), balls[count].getFontColor(), balls[count].getBackGroundColor());
                                ivThirdBall.setImageDrawable(drawable);
                                setBallOverWise(balls, count);
                            } else {
                                ivThirdBall.setImageResource(R.drawable.recent_dot_balls);
                                setBallOverWise(balls,  count);
                            }
                            count++;
                            if (!balls[count].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[count].getValue(), balls[count].getFontColor(), balls[count].getBackGroundColor());
                                ivFourthBall.setImageDrawable(drawable);
                                setBallOverWise(balls,  count);
                            } else {
                                ivFourthBall.setImageResource(R.drawable.recent_dot_balls);
                                setBallOverWise(balls,  count);
                            }
                            count++;
                            if (!balls[count].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[count].getValue(), balls[count].getFontColor(), balls[count].getBackGroundColor());
                                ivFifthBall.setImageDrawable(drawable);
                                setBallOverWise(balls,  count);
                            } else {
                                ivFifthBall.setImageResource(R.drawable.recent_dot_balls);
                                setBallOverWise(balls,  count);
                            }
                            count++;
                            if (!balls[count].getValue().equals("0")) {
                                drawable = getTextDrawable(balls[count].getValue(), balls[count].getFontColor(), balls[count].getBackGroundColor());
                                ivSixthBall.setImageDrawable(drawable);
                                setBallOverWise(balls, count);
                            } else {
                                ivSixthBall.setImageResource(R.drawable.recent_dot_balls);
                                setBallOverWise(balls,  count);
                            }
                            int playerFirstRuns = liveCricketMatchSummaryParser.getPlayeFirstRuns();
                            int playerSecondRuns = liveCricketMatchSummaryParser.getPlayeSecondRuns();
                            int playerFirstBalls = liveCricketMatchSummaryParser.getPlayeFirstBalls();
                            int playerSecondBalls = liveCricketMatchSummaryParser.getPlayeSecondBalls();

                            tvFirstPlayerName.setText(liveCricketMatchSummaryParser.getPlayeFirstName());
                            tvSecondPlayerName.setText(liveCricketMatchSummaryParser.getPlayeSecondName());
                            DecimalFormat formate = new DecimalFormat();
                            formate.setMinimumFractionDigits(2);
                            formate.setMaximumFractionDigits(2);
                            if (playerFirstBalls == 0) {
                                tvFirstPlayerRunRate.setText(0 + "");
                            } else {


                                tvFirstPlayerRunRate.setText("SR" + " " + formate.format(playerFirstRuns * 100 / (float) playerFirstBalls) + "");
                            }
                            if (playerSecondBalls == 0) {
                                tvSecondPlayerRunRate.setText(0 + "");

                            } else {
                                tvSecondPlayerRunRate.setText("SR" + " " + formate.format(playerSecondRuns * 100 /(float) playerSecondBalls) + "");
                            }
                            tvFirstPlayerRunOnBall.setText(liveCricketMatchSummaryParser.getPlayeFirstRuns() + "(" + liveCricketMatchSummaryParser.getPlayeFirstBalls() + ")");
                            Glide.with(getContext()).load(liveCricketMatchSummaryParser.getPlayerFirstImage()).placeholder(R.drawable.ic_user).into(ivFirstPlayer);

                            tvSecondPlayerRunOnBall.setText(liveCricketMatchSummaryParser.getPlayeSecondRuns() + "(" + liveCricketMatchSummaryParser.getPlayeSecondBalls() + ")");
                            Glide.with(getContext()).load(liveCricketMatchSummaryParser.getPlayerSecondImage()).placeholder(R.drawable.ic_user).into(ivPlayerSecond);

                            tvPartnershipRecord.setText((playerFirstRuns + playerSecondRuns) + "(" + (playerFirstBalls + playerSecondBalls) + ")");
                            tvFirstUpComingPlayerName.setText(liveCricketMatchSummaryParser.getYetToPlayerName(0));
                            Glide.with(getContext()).load(liveCricketMatchSummaryParser.getYetToPlayerImage(0)).placeholder(R.drawable.ic_user).into(ivUppComingPlayerFirst);
                            tvSecondUpComingPlayerName.setText(liveCricketMatchSummaryParser.getYetToPlayerName(1));
                            Glide.with(getContext()).load(liveCricketMatchSummaryParser.getYetToPlayerImage(1)).placeholder(R.drawable.ic_user).into(ivUppComingPlayerSecond);
                            tvThirdUpComingPlayerName.setText(liveCricketMatchSummaryParser.getYetToPlayerName(2));
                            Glide.with(getContext()).load(liveCricketMatchSummaryParser.getYetToPlayerImage(2)).placeholder(R.drawable.ic_user).into(ivUppComingPlayerThird);
                            tvBowlerName.setText(liveCricketMatchSummaryParser.getCurentBowlerName());

                            tvBowlerEcon.setText("ECON " + liveCricketMatchSummaryParser.getCurentBowlerName());

                            tvBowlerOver.setText(liveCricketMatchSummaryParser.getCurentBowlerOvers());
                            tvBowlerWRun.setText(liveCricketMatchSummaryParser.getCurentBowlerWicket() + "/" + liveCricketMatchSummaryParser.getCurentBowlerRuns());
                            Glide.with(getContext()).load(liveCricketMatchSummaryParser.getCurentBowlerImage()).placeholder(R.drawable.ic_user).into(ivBowlerProfile);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showErrorLayout(getView());
                        }
                    }
                });
            }
        } else{
                showErrorLayout(getView());
            }
        }else {
            showErrorLayout(getView());
        }
    }

    private void setBallOverWise(BallDetail[] balls, int index) {
        if(balls[index].getBallId()==1){
            dividerView[index].setVisibility(View.VISIBLE);
            vifirsttv[index].setText(recentOverValue + " ovs");
        }else{
            dividerView[index].setVisibility(View.INVISIBLE);
            vifirsttv[index].setVisibility(View.INVISIBLE);
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
            case "0":
                ballDetail.setValue("0");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.grayBorder));
                break;
            case "1":
                ballDetail.setValue("1");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.grayBorder));
                break;
            case "2":
                ballDetail.setValue("2");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.grayBorder));
                break;
            case "3":
                ballDetail.setValue("3");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.grayBorder));
                break;
            case "4":
                ballDetail.setValue("4");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary));
                ballDetail.setBackGroundColor(getBallColor(R.color.app_theme_blue));
                break;
            case "5":
                ballDetail.setValue("5");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.font_color_wide_no));
                break;
            case "6":
                ballDetail.setValue("6");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary));
                ballDetail.setBackGroundColor(getBallColor(R.color.app_theme_blue));
                break;
            case "wd":
                ballDetail.setValue("WD");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary));
                break;
            case "e1,by":
                ballDetail.setValue("B");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary));
                break;
            case "e1,lb":
                ballDetail.setValue("LB");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary));
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
                ballDetail.setBackGroundColor(getBallColor(R.color.app_theme_blue));
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
        autRefreshEnabled = false;
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
        cricketLiveMatchSummaryHandler.requestLiveMatchSummary(seriesId,matchId);
        autRefreshEnabled = true;
    }

}
