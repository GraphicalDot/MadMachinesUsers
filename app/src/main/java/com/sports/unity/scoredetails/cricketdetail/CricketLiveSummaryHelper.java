package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scoredetails.BallDetail;
import com.sports.unity.scoredetails.cricketdetail.JsonParsers.LiveCricketMatchSummaryParser;
import com.sports.unity.scores.model.ScoresContentHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;


public class CricketLiveSummaryHelper extends BasicVolleyRequestResponseViewHelper {

    private String title = null;
    private HashMap<String, String> requestParameters = null;
    private JSONObject response = null;

    private String recentOverValue;

    private Context context = null;
    private View contentLayout = null;
    private SwipeRefreshLayout swipeRefreshLayout = null;

    private ImageView ivBalls[] = new ImageView[6];
    private View dividerView[] = new View[6];

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
    private TextView vifirsttv[] = new TextView[6];

    private ImageView ivBowlerProfile;
    private TextView tvBowlerName;
    private TextView tvBowlerWRun;
    private TextView tvBowlerEcon;
    private TextView tvBowlerOver;

    public CricketLiveSummaryHelper(String title) {
        this.title = title;
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_cricket_live_match_summery;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return "CricketLiveSummary";
    }

    @Override
    public String getRequestTag() {
        return "CricketLiveSummaryRequestTag";
    }

    @Override
    public String getRequestCallName() {
        return ScoresContentHandler.CALL_NAME_CRICKET_MATCH_SUMMARY;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        SummaryComponentListener matchCommentaryComponentListener = new SummaryComponentListener(getRequestTag(), progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        return matchCommentaryComponentListener;
    }

    @Override
    public void initialiseViews(View view) {
        initViews(view);
    }

    public void setRequestParameters(HashMap<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    private void initViews(View view) {
        try {
            context = view.getContext();

            contentLayout = view.findViewById(R.id.content_layout);
            contentLayout.setVisibility(View.GONE);

            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestContent();
                }
            });

            ivBalls[0] = (ImageView) view.findViewById(R.id.iv_first_ball);
            ivBalls[1] = (ImageView) view.findViewById(R.id.iv_second_ball);
            ivBalls[2] = (ImageView) view.findViewById(R.id.iv_third_ball);
            ivBalls[3] = (ImageView) view.findViewById(R.id.iv_fourth_ball);
            ivBalls[4] = (ImageView) view.findViewById(R.id.iv_fifth_ball);
            ivBalls[5] = (ImageView) view.findViewById(R.id.iv_sixth_ball);

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

            vifirsttv[0] = (TextView) view.findViewById(R.id.vi_start_tv);
            vifirsttv[1] = (TextView) view.findViewById(R.id.vi_first_tv);
            vifirsttv[2] = (TextView) view.findViewById(R.id.vi_second_tv);
            vifirsttv[3] = (TextView) view.findViewById(R.id.vi_third_tv);
            vifirsttv[4] = (TextView) view.findViewById(R.id.vi_four_tv);
            vifirsttv[5] = (TextView) view.findViewById(R.id.vi_five_tv);

            dividerView[0] = view.findViewById(R.id.vi_start);
            dividerView[1] = view.findViewById(R.id.vi_first);
            dividerView[2] = view.findViewById(R.id.vi_second);
            dividerView[3] = view.findViewById(R.id.vi_third);
            dividerView[4] = view.findViewById(R.id.vi_four);
            dividerView[5] = view.findViewById(R.id.vi_five);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean handleContent(String content) {
        boolean success = false;
        try {
            JSONObject object = new JSONObject(content);
            success = object.getBoolean("success");

            if (success) {
                success = false;
                if (!object.isNull("data")) {
                    JSONArray dataArray = object.getJSONArray("data");
                    JSONObject matchObject = dataArray.getJSONObject(0);

                    response = matchObject;
                    success = true;
                } else {
                    //nothing
                }
            } else {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private boolean renderResponse() {
        boolean success = false;
        try {
            LiveCricketMatchSummaryParser liveCricketMatchSummaryParser = new LiveCricketMatchSummaryParser();
            liveCricketMatchSummaryParser.setJsonObject(response);
            liveCricketMatchSummaryParser.setCricketSummary(liveCricketMatchSummaryParser.getMatchSummary());

            JSONObject currentPartnershipDetails = null;
            JSONArray yetToBatting = null;
            JSONObject currentBowlerObject = null;
            JSONObject recentOver = null;
            {
                JSONArray array = liveCricketMatchSummaryParser.getCurrentPartnership();
                if( array!= null && array.length() > 0 ) {
                    currentPartnershipDetails = (JSONObject)array.get(0);
                    liveCricketMatchSummaryParser.setCurrentPartnership(currentPartnershipDetails);
                }

                yetToBatting = liveCricketMatchSummaryParser.getUpCommingBatsMan();
                currentBowlerObject = liveCricketMatchSummaryParser.getCurentBowler();
                recentOver = liveCricketMatchSummaryParser.getRecentOver();

                liveCricketMatchSummaryParser.setYetToBat(yetToBatting);
                liveCricketMatchSummaryParser.setCurrentBowler(currentBowlerObject);
            }

            if( recentOver != null && recentOver.length() > 0 ) {
                try {
                    Stack<JSONObject> ballsStack = new Stack<>();
                    BallDetail defb = new BallDetail();
                    BallDetail[] balls = new BallDetail[]{defb, defb, defb, defb, defb, defb};
                    int ballIndex = 5;

                    Iterator<String> recentOverKeys = recentOver.keys();
                    Integer keys[] = new Integer[2];
                    int arrayCount = 0;

                    while (recentOverKeys.hasNext()) {
                        keys[arrayCount++] = Integer.parseInt(recentOverKeys.next());
                    }

                    if (keys[0] < keys[1]) {
                        recentOverValue = keys[1].toString();
                        JSONArray recentOverJSONArray = recentOver.getJSONArray(keys[0].toString());
                        getAllBalls(ballsStack, recentOverJSONArray);

                        recentOverJSONArray = recentOver.getJSONArray(keys[1].toString());
                        getAllBalls(ballsStack, recentOverJSONArray);

                    } else {
                        recentOverValue = keys[0].toString();
                        JSONArray recentOverJSONArray = recentOver.getJSONArray(keys[1].toString());
                        getAllBalls(ballsStack, recentOverJSONArray);

                        recentOverJSONArray = recentOver.getJSONArray(keys[0].toString());
                        getAllBalls(ballsStack, recentOverJSONArray);
                    }

                    int queuSize = ballsStack.size();
                    for (int i = 0; i < queuSize; i++) {
                        if (i == 6) {
                            break;
                        }

                        BallDetail curBall = null;
                        JSONObject object = ballsStack.pop();

                        int ballId = object.getInt("ball_id");
                        JSONArray eventArray = object.getJSONArray("event");
                        String event = eventArray.getString(0);
                        String wicket = eventArray.getString(1);
                        String run = eventArray.getString(2);
                        if (wicket != null && !wicket.equals("")) {
                            curBall = getResolveBall(wicket);
                        } else if (event != null && !event.equals("")) {
                            curBall = getResolveBall(event);
                        } else {
                            curBall = getResolveBall(run);
                        }
                        curBall.setBallId(ballId);
                        Drawable drawable = getTextDrawable(curBall.getValue(), curBall.getFontColor(), curBall.getBackGroundColor());
                        curBall.setDrawable(drawable);
                        balls[ballIndex] = curBall;
                        ballIndex--;
                    }

                    for (int i = 0; i < 6; i++) {
                        if (!balls[i].getValue().equals("0")) {
                            ivBalls[i].setImageDrawable(balls[i].getDrawable());
                            setBallOverWise(balls, i);
                        } else {
                            ivBalls[i].setImageResource(R.drawable.recent_dot_balls);
                            setBallOverWise(balls, i);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if( currentPartnershipDetails != null && currentPartnershipDetails.length() > 0 ){
                int playerFirstRuns = liveCricketMatchSummaryParser.getPlayeFirstRuns();
                int playerSecondRuns = liveCricketMatchSummaryParser.getPlayeSecondRuns();
                int playerFirstBalls = liveCricketMatchSummaryParser.getPlayeFirstBalls();
                int playerSecondBalls = liveCricketMatchSummaryParser.getPlayeSecondBalls();

                DecimalFormat formate = new DecimalFormat();
                formate.setMinimumFractionDigits(2);
                formate.setMaximumFractionDigits(2);

                tvFirstPlayerName.setText(liveCricketMatchSummaryParser.getPlayeFirstName());
                tvSecondPlayerName.setText(liveCricketMatchSummaryParser.getPlayeSecondName());

                if (playerFirstBalls == 0) {
                    tvFirstPlayerRunRate.setText(0 + "");
                } else {
                    tvFirstPlayerRunRate.setText("SR" + " " + formate.format(playerFirstRuns * 100 / (float) playerFirstBalls) + "");
                }
                if (playerSecondBalls == 0) {
                    tvSecondPlayerRunRate.setText(0 + "");
                } else {
                    tvSecondPlayerRunRate.setText("SR" + " " + formate.format(playerSecondRuns * 100 / (float) playerSecondBalls) + "");
                }

                tvFirstPlayerRunOnBall.setText(liveCricketMatchSummaryParser.getPlayeFirstRuns() + "(" + liveCricketMatchSummaryParser.getPlayeFirstBalls() + ")");
                Glide.with(context).load(liveCricketMatchSummaryParser.getPlayerFirstImage()).placeholder(R.drawable.ic_user).dontAnimate().into(ivFirstPlayer);

                tvSecondPlayerRunOnBall.setText(liveCricketMatchSummaryParser.getPlayeSecondRuns() + "(" + liveCricketMatchSummaryParser.getPlayeSecondBalls() + ")");
                Glide.with(context).load(liveCricketMatchSummaryParser.getPlayerSecondImage()).placeholder(R.drawable.ic_user).dontAnimate().into(ivPlayerSecond);

                tvPartnershipRecord.setText((playerFirstRuns + playerSecondRuns) + "(" + (playerFirstBalls + playerSecondBalls) + ")");
            }

            if( yetToBatting != null && yetToBatting.length() > 0 ){
                {
                    tvFirstUpComingPlayerName.setText(liveCricketMatchSummaryParser.getYetToPlayerName(0));
                    Glide.with(context).load(liveCricketMatchSummaryParser.getYetToPlayerImage(0)).placeholder(R.drawable.ic_user).dontAnimate().into(ivUppComingPlayerFirst);
                }
                if( yetToBatting.length() > 1 ) {
                    tvSecondUpComingPlayerName.setText(liveCricketMatchSummaryParser.getYetToPlayerName(1));
                    Glide.with(context).load(liveCricketMatchSummaryParser.getYetToPlayerImage(1)).placeholder(R.drawable.ic_user).dontAnimate().into(ivUppComingPlayerSecond);

                } else {
                    ivUppComingPlayerSecond.setVisibility(View.GONE);
                }
                if( yetToBatting.length() > 2 ) {
                    tvThirdUpComingPlayerName.setText(liveCricketMatchSummaryParser.getYetToPlayerName(2));
                    Glide.with(context).load(liveCricketMatchSummaryParser.getYetToPlayerImage(2)).placeholder(R.drawable.ic_user).dontAnimate().into(ivUppComingPlayerThird);
                } else {
                    ivUppComingPlayerThird.setVisibility(View.GONE);
                }
            }

            if( currentBowlerObject != null && currentBowlerObject.length() > 0 ){
                tvBowlerName.setText(liveCricketMatchSummaryParser.getCurentBowlerName());
                tvBowlerEcon.setText(TextUtils.isEmpty(liveCricketMatchSummaryParser.getCurentBowlerEconomy()) ? "Not Available" : liveCricketMatchSummaryParser.getCurentBowlerEconomy());

                tvBowlerOver.setText(liveCricketMatchSummaryParser.getCurentBowlerOvers());
                tvBowlerWRun.setText(liveCricketMatchSummaryParser.getCurentBowlerWicket() + "/" + liveCricketMatchSummaryParser.getCurentBowlerRuns());
                Glide.with(context).load(liveCricketMatchSummaryParser.getCurentBowlerImage()).placeholder(R.drawable.ic_user).dontAnimate().into(ivBowlerProfile);
            }

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private void getAllBalls(Stack<JSONObject> ballsStack, JSONArray recentOverJSONArray) throws JSONException {
        for (int count = 0; count < recentOverJSONArray.length(); count++) {
            JSONObject ballObject = recentOverJSONArray.getJSONObject(count);
            ballsStack.add(ballObject);
        }
    }

    private void setBallOverWise(BallDetail[] balls, int index) {
        if (balls[index].getBallId() == 1) {
            dividerView[index].setVisibility(View.VISIBLE);
            vifirsttv[index].setText(recentOverValue + " ovs");
        } else {
            dividerView[index].setVisibility(View.INVISIBLE);
            vifirsttv[index].setText("");
        }
    }

    private Drawable getTextDrawable(String text, int color, int backGroundColor) {
        int radius = context.getResources().getDimensionPixelSize(R.dimen.recent_ball_radius);
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig().textColor(color)
                .width(radius)
                .height(radius)
                .bold()
                .endConfig()
                .buildRound(text, backGroundColor);
        return drawable;
    }

    private BallDetail getResolveBall(String value) {
        BallDetail ballDetail = new BallDetail();
        switch (value) {
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

            case "e1,nb":
                ballDetail.setValue("NB");
                ballDetail.setFontColor(getBallColor(R.color.font_color_boundary_no));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_boundary));
                break;
            case "w":
                ballDetail.setValue("W");
                ballDetail.setFontColor(getBallColor(R.color.font_color_wicket));
                ballDetail.setBackGroundColor(getBallColor(R.color.balls_color_wicket));
                break;
            default:
                ballDetail.setValue("0");
                ballDetail.setFontColor(getBallColor(R.color.balls_color_odd_font));
                ballDetail.setBackGroundColor(getBallColor(R.color.grayBorder));
        }
        return ballDetail;
    }

    private int getBallColor(int id) {
        if (id != 0) {
            return context.getResources().getColor(id);
        } else {
            return 0;
        }

    }

    public class SummaryComponentListener extends CustomComponentListener {

        public SummaryComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout, View contentLayout, SwipeRefreshLayout swipeRefreshLayout) {
            super(requestTag, progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = CricketLiveSummaryHelper.this.handleContent(content);
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderResponse();
            if (success) {
                contentLayout.setVisibility(View.VISIBLE);
            } else {
                showErrorLayout();
            }
        }

    }


}
