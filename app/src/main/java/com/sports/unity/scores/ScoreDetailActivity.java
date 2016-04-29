package com.sports.unity.scores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.common.viewhelper.GenericFragmentViewPagerAdapter;
import com.sports.unity.common.viewhelper.VolleyCallComponentHelper;
import com.sports.unity.scoredetails.cricketdetail.CricketCompletedMatchSummaryHelper;
import com.sports.unity.scores.controller.fragment.MatchListWrapperAdapter;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.football.CricketMatchJsonCaller;
import com.sports.unity.scores.model.football.FootballMatchJsonCaller;
import com.sports.unity.scores.viewhelper.MatchCommentaryHelper;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import static com.sports.unity.util.Constants.INTENT_KEY_TYPE;

public class ScoreDetailActivity extends CustomVolleyCallerActivity {

    private static final String REQUEST_LISTENER_KEY = "score_detail_listener";
    private static final String SCORE_DETAIL_REQUEST_TAG = "score_detail_request_tag";

    private CricketMatchJsonCaller cricketMatchJsonCaller = new CricketMatchJsonCaller();
    private FootballMatchJsonCaller footballMatchJsonCaller = new FootballMatchJsonCaller();

    private JSONObject matchScoreDetails = null;
//    private ArrayList<CommentriesModel> commentaries = new ArrayList<>();

    private String sportsType = null;
    private String seriesId;
    private String matchId = null;
    private String matchStatus;
    private String matchTime;
    private Boolean isLive;
    private String LeagueName;

    private Timer timerToRefreshContent = null;

    private ProgressBar mProgressBar = null;
    private ViewGroup errorLayout = null;

    private ViewPager mViewPager;
//    private ViewPagerCricketScoreDetailAdapter cricketScoreDetailAdapter;
//    private ViewPagerFootballScoreDetailAdapter footballScoreDetailAdapter;

    private View llMatchDetailLinear;
    private TextView teamFirstOvers;
    private TextView teamSecondOvers;
    private TextView tvMatchTime;
    private TextView getTvMatchDay;
    private ImageView refreshImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score_detail);

        getExtras();
        initView();
        setToolbar();
        setTitle();

        {
            errorLayout = (ViewGroup) findViewById(R.id.error);
            errorLayout.setVisibility(View.GONE);

            mProgressBar = (ProgressBar) findViewById(R.id.progress);
            mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

            onComponentCreate();
            requestMatchScoreDetails();
        }
    }

    @Override
    public VolleyCallComponentHelper getVolleyCallComponentHelper() {
        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper( REQUEST_LISTENER_KEY, new ScoreDetailComponentListener(mProgressBar, errorLayout));
        return volleyCallComponentHelper;
    }

    private void getExtras() {
        Intent i = getIntent();
        sportsType = i.getStringExtra(INTENT_KEY_TYPE);
        matchId = i.getStringExtra(Constants.INTENT_KEY_ID);
        matchStatus = i.getStringExtra(Constants.INTENT_KEY_MATCH_STATUS);
        matchTime = i.getStringExtra(Constants.INTENT_KEY_MATCH_TIME);
        isLive = i.getBooleanExtra(Constants.INTENT_KEY_MATCH_LIVE, false);
        LeagueName = i.getStringExtra(Constants.LEAGUE_NAME);
        seriesId = i.getStringExtra(Constants.INTENT_KEY_SERIES);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

//        disableAutoRefreshContent();
        onComponentPause();
    }

    private void initView() {
        try {
            int tab_index = getIntent().getIntExtra("tab_index", 1);

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put(ScoresContentHandler.PARAM_SERIESID, seriesId);
            parameters.put(ScoresContentHandler.PARAM_SPORTS_TYPE, sportsType);
            parameters.put(ScoresContentHandler.PARAM_ID, matchId);

            MatchCommentaryHelper matchCommentaryHelper = new MatchCommentaryHelper(getString(R.string.commentary));
            matchCommentaryHelper.setRequestParameters(parameters);

            CricketCompletedMatchSummaryHelper cricketCompletedMatchSummaryHelper = new CricketCompletedMatchSummaryHelper(getString(R.string.summary), getIntent());
            cricketCompletedMatchSummaryHelper.setParameters(parameters);

            ArrayList<BasicVolleyRequestResponseViewHelper> fragmentHelperList = new ArrayList<>();
            fragmentHelperList.add(cricketCompletedMatchSummaryHelper);
            fragmentHelperList.add( matchCommentaryHelper);
//            fragmentHelperList.add( new MatchCommentaryHelper());

            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter( new GenericFragmentViewPagerAdapter(getSupportFragmentManager(), fragmentHelperList));

//            String cricketMatchtitles[] = {getString(R.string.summary), getString(R.string.commentary), getString(R.string.scorecard)};
//            int numberOfCricketTabs = cricketMatchtitles.length;
//            String footballMatchtitles[] = {getString(R.string.commentary), getString(R.string.matchstats), getString(R.string.timeline), getString(R.string.lineup)};
//            int numberOfFootballTabs = footballMatchtitles.length;
//            String footballMatchtitlesupcommingTitles[] = {getString(R.string.table), getString(R.string.form), getString(R.string.squad)};
//
//
//            int tab_index = 0;
            if (sportsType.equalsIgnoreCase(ScoresJsonParser.CRICKET)) {
//                cricketScoreDetailAdapter = new ViewPagerCricketScoreDetailAdapter(getSupportFragmentManager(), cricketMatchtitles, numberOfCricketTabs, commentaries, matchStatus);
//                mViewPager.setAdapter(cricketScoreDetailAdapter);
//                tab_index = getIntent().getIntExtra("tab_index", 1);
            } else {
                if (matchStatus.equals(matchTime) || "Postp.".equalsIgnoreCase(matchStatus) && !isLive) {
//                    footballScoreDetailAdapter = new ViewPagerFootballScoreDetailAdapter(getSupportFragmentManager(), footballMatchtitlesupcommingTitles, footballMatchtitlesupcommingTitles.length, commentaries, matchStatus, matchTime, isLive);
//                    mViewPager.setAdapter(footballScoreDetailAdapter);
//                    tab_index = getIntent().getIntExtra("tab_index", 0);
                } else {
//                    footballScoreDetailAdapter = new ViewPagerFootballScoreDetailAdapter(getSupportFragmentManager(), footballMatchtitles, numberOfFootballTabs, commentaries, matchStatus, matchTime, isLive);
//                    mViewPager.setAdapter(footballScoreDetailAdapter);
//                    tab_index = getIntent().getIntExtra("tab_index", 1);
//
                }
            }

            SlidingTabLayout tabs = (SlidingTabLayout) findViewById(com.sports.unity.R.id.tabs);
            tabs.setDistributeEvenly(false);
            tabs.setTabTextColor(R.color.filter_tab_selector);
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.app_theme_blue);
                }
            });
            tabs.setViewPager(mViewPager);

            mViewPager.setCurrentItem(tab_index);
            teamFirstOvers = (TextView) findViewById(R.id.team1_over);
            teamSecondOvers = (TextView) findViewById(R.id.team2_over);
            ImageView img = (ImageView) findViewById(R.id.back_img);
            img.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    finish();
                }
            });
            refreshImage = (ImageView) findViewById(R.id.refresh);
            refreshImage.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));

            refreshImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int index = mViewPager.getCurrentItem();
                    List<Fragment> fargmentList = getSupportFragmentManager().getFragments();
                    Fragment fragment = fargmentList.get(index);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .detach(fragment)
                            .attach(fragment)
                            .commit();
                }
            });

            llMatchDetailLinear = findViewById(R.id.ll_match_detail_linear);
            tvMatchTime = (TextView) findViewById(R.id.tv_match_time);
            getTvMatchDay = (TextView) findViewById(R.id.tv_game_day);
            getTvMatchDay.setTextColor(getResources().getColor(R.color.score_light_grey));
            getTvMatchDay.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        } catch (Exception e) {
            Log.i("Exception Occured", "initView: ");
            Toast.makeText(this, "Error Occured", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
    }

    private void displayMatchTimer(Integer currenttime) {
        if (currenttime > 90 && currenttime <= 105) {
            mProgressBar.setMax(105);
        } else if (currenttime > 105 && currenttime <= 120) {
            mProgressBar.setMax(120);
        } else {
            mProgressBar.setMax(90);
        }
        if (currenttime != 0) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mProgressBar.setProgress(currenttime);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        ImageView backArrow = (ImageView) toolbar.findViewById(R.id.back_img);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScoreDetailActivity.this.finish();
            }
        });
    }

    private void setTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        TextView title_text = (TextView) toolbar.findViewById(R.id.toolbar_title);
        try {
            if (sportsType.equals(ScoresJsonParser.CRICKET)) {
                cricketMatchJsonCaller.setJsonObject(matchScoreDetails);
                if (LeagueName != null) {
                    title_text.setText(LeagueName);
                } else {
                    title_text.setText(cricketMatchJsonCaller.getSeriesName());
                }
            } else if (sportsType.equals(ScoresJsonParser.FOOTBALL)) {
                footballMatchJsonCaller.setJsonObject(matchScoreDetails);
                if (LeagueName != null) {
                    title_text.setText(LeagueName);
                } else {
                    title_text.setText(cricketMatchJsonCaller.getSeriesName());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean renderScores() {
        Log.i("Score Detail", "Render Scores");
        boolean requestCommentaries = false;
        TextView tvNeededRun = (TextView) findViewById(R.id.tv_needed_run);
        TextView tvCurrentScore = (TextView) findViewById(R.id.tv_current_score);
        if (sportsType.equals(ScoresJsonParser.CRICKET)) {
            cricketMatchJsonCaller.setJsonObject(matchScoreDetails);

            try {


                ImageView flag1 = (ImageView) findViewById(R.id.team1_image);
                ImageView flag2 = (ImageView) findViewById(R.id.team2_image);




                /*JSONArray widgetTeamsArray = cricketMatchJsonCaller.getTeamsWiget();
                String homeTeam = cricketMatchJsonCaller.getTeam1();
                String awayTeam  = cricketMatchJsonCaller.getTeam2();

                for(int i = 0 ; i< widgetTeamsArray.length();i++){
                    JSONObject teamData= widgetTeamsArray.getJSONObject(i);
                    if(homeTeam.equalsIgnoreCase(teamData.getString("team_name"))){
                        cricketMatchJsonCaller.setMatchWidgetHomeTeam(teamData);
                        Glide.with(this).load(cricketMatchJsonCaller.getTeam1Flag()).placeholder(R.drawable.ic_no_img).into(flag1);
                    }else if(awayTeam.equalsIgnoreCase(teamData.getString("team_name"))){
                        cricketMatchJsonCaller.setMatchWidgetAwayTeam(teamData);
                        Glide.with(this).load(cricketMatchJsonCaller.getTeam2Flag()).placeholder(R.drawable.ic_no_img).into(flag2);
                    }
                }
*/
                // findViewById(R.id.central_score).setVisibility(View.GONE);


                JSONObject widgetTeamsObject = cricketMatchJsonCaller.getTeamsWiget();
                JSONArray widgetTeamsFirst = null;
                JSONArray widgetTeamSecond = null;
                if (!widgetTeamsObject.isNull("1")) {
                    widgetTeamsFirst = widgetTeamsObject.getJSONArray("1");
                }
                if (!widgetTeamsObject.isNull("2")) {
                    widgetTeamSecond = widgetTeamsObject.getJSONArray("2");
                }

                String homeTeam = cricketMatchJsonCaller.getTeam1();
                String awayTeam = cricketMatchJsonCaller.getTeam2();
                Glide.with(this).load(cricketMatchJsonCaller.getTeam1Flag()).placeholder(R.drawable.ic_no_img).into(flag1);
                Glide.with(this).load(cricketMatchJsonCaller.getTeam2Flag()).placeholder(R.drawable.ic_no_img).into(flag2);
                if (widgetTeamsFirst != null) {
                    for (int i = 0; i < widgetTeamsFirst.length(); i++) {
                        JSONObject teamData = widgetTeamsFirst.getJSONObject(i);
                        if (awayTeam.equalsIgnoreCase(teamData.getString("team_name"))) {
                            cricketMatchJsonCaller.setMatchWidgetAwayTeam(teamData);
                        }


                        if (homeTeam.equalsIgnoreCase(teamData.getString("team_name"))) {
                            cricketMatchJsonCaller.setMatchWidgetHomeTeam(teamData);

                        }

                    }
                }


                if (widgetTeamSecond != null) {

                    for (int i = 0; i < widgetTeamSecond.length(); i++) {
                        JSONObject teamData = widgetTeamSecond.getJSONObject(i);
                        if (homeTeam.equalsIgnoreCase(teamData.getString("team_name"))) {
                            cricketMatchJsonCaller.setMatchWidgetHomeTeam(teamData);
                        }


                        if (awayTeam.equalsIgnoreCase(teamData.getString("team_name"))) {
                            cricketMatchJsonCaller.setMatchWidgetAwayTeam(teamData);
                        }
                    }
                }


                ((TextView) findViewById(R.id.venue)).setText(cricketMatchJsonCaller.getVenue());
                ((TextView) findViewById(R.id.date)).setText(DateUtil.getDateFromEpochTime(Long.valueOf(cricketMatchJsonCaller.getMatchDateTimeEpoch()) * 1000));
                tvNeededRun.setText(cricketMatchJsonCaller.getTeam1() + " vs " + cricketMatchJsonCaller.getTeam2() + ", " + cricketMatchJsonCaller.getMatchNumber());

                if (cricketMatchJsonCaller.getStatus().equalsIgnoreCase("N")) {

                    tvCurrentScore.setText(cricketMatchJsonCaller.getMatchResult());
                    tvCurrentScore.setText(DateUtil.getDayFromEpochTime((Long.valueOf(cricketMatchJsonCaller.getMatchDateTimeEpoch()) * 1000), this));
//                    showNoCommentaries();
                    TextView text1Score = (TextView) findViewById(R.id.team1_score);
                    TextView team2Score = (TextView) findViewById(R.id.team2_score);
                    text1Score.setText(cricketMatchJsonCaller.getTeam1());
                    team2Score.setText(cricketMatchJsonCaller.getTeam2());
                    refreshImage.setVisibility(View.GONE);
                } else {

                    TextView textView = (TextView) findViewById(R.id.team1_name);
                    textView.setText(cricketMatchJsonCaller.getTeam1());

                    textView = (TextView) findViewById(R.id.team2_name);
                    textView.setText(cricketMatchJsonCaller.getTeam2());
                    if (cricketMatchJsonCaller.getStatus().equalsIgnoreCase("L")) {

                        tvCurrentScore.setVisibility(View.GONE);
                        refreshImage.setVisibility(View.VISIBLE);
                    } else {
                        tvCurrentScore.setText(cricketMatchJsonCaller.getMatchResult());
                        tvCurrentScore.setVisibility(View.VISIBLE);
                        refreshImage.setVisibility(View.GONE);
                    }


                    if (cricketMatchJsonCaller.getStatus().equalsIgnoreCase("F") || cricketMatchJsonCaller.getStatus().equalsIgnoreCase("L")) {

                        {
                            StringBuilder stringBuilder = new StringBuilder("");
                            stringBuilder.append(cricketMatchJsonCaller.getTeam1Score());
                            stringBuilder.append("/");
                            stringBuilder.append(cricketMatchJsonCaller.getWicketsTeam1());

                            //   teamFirstOvers.setText("("+cricketMatchJsonCaller.getOversTeam1()!=null?cricketMatchJsonCaller.getOversTeam1():"0"+")");

                            textView = (TextView) findViewById(R.id.team1_score);
                            textView.setText(stringBuilder.toString());

                            textView.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());

                            StringBuilder stringBuilder1 = new StringBuilder("");
                            stringBuilder1.append("(");
                            stringBuilder1.append(cricketMatchJsonCaller.getOversTeam1());
                            stringBuilder1.append(")");
                            teamFirstOvers.setText(stringBuilder1.toString());

                        }

                        {
                            StringBuilder stringBuilder = new StringBuilder("");
                            stringBuilder.append(cricketMatchJsonCaller.getTeam2Score());
                            stringBuilder.append("/");
                            stringBuilder.append(cricketMatchJsonCaller.getWicketsTeam2());

                            // teamSecondOvers.setText("("+cricketMatchJsonCaller.getOversTeam2()!=null?cricketMatchJsonCaller.getOversTeam2():"0"+")");

                            textView = (TextView) findViewById(R.id.team2_score);
                            textView.setText(stringBuilder.toString());
                            textView.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());

                            StringBuilder stringBuilder1 = new StringBuilder("");
                            stringBuilder1.append("(");
                            stringBuilder1.append(cricketMatchJsonCaller.getOversTeam2());
                            stringBuilder1.append(")");
                            teamSecondOvers.setText(stringBuilder1.toString());

                        }

                    } else {
//                        enableAutoRefreshContent();
                    }
                    requestCommentaries = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (sportsType.equals(ScoresJsonParser.FOOTBALL)) {
            footballMatchJsonCaller.setJsonObject(matchScoreDetails);
            // mProgressBar.setVisibility(View.INVISIBLE);

            {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
                params.gravity = Gravity.CENTER;
                ((ViewGroup) findViewById(R.id.flag1_parent_layout)).setLayoutParams(params);
            }
            {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
                params.gravity = Gravity.CENTER;
                ((ViewGroup) findViewById(R.id.flag2_parent_layout)).setLayoutParams(params);
            }


            try {
                if (footballMatchJsonCaller.getMatchTime().equals(footballMatchJsonCaller.getMatchStatus()) && !footballMatchJsonCaller.isLive()) {
                    //  displayMatchTimer(12);
                    tvMatchTime.setText(DateUtil.getMatchTime(Long.valueOf(footballMatchJsonCaller.getMatchDateEpoch()) * 1000));
                    getTvMatchDay.setText(DateUtil.getMatchDays(Long.valueOf(footballMatchJsonCaller.getMatchDateEpoch()) * 1000, this));
                    llMatchDetailLinear.setVisibility(View.GONE);
                }
                if (!footballMatchJsonCaller.isLive() && footballMatchJsonCaller.getMatchStatus().equalsIgnoreCase("FT")) {
                    refreshImage.setVisibility(View.GONE);
                    getTvMatchDay.setText(R.string.full_time);


                } else if (!footballMatchJsonCaller.isLive() && "Postp.".equalsIgnoreCase(footballMatchJsonCaller.getMatchStatus())) {
                    refreshImage.setVisibility(View.GONE);
                    getTvMatchDay.setText(R.string.post_pond);

                }
                llMatchDetailLinear.setVisibility(View.GONE);
                Date date = new Date(new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date(Long.valueOf(footballMatchJsonCaller.getMatchDateEpoch()) * 1000)));
                String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);
                String day = (String) android.text.format.DateFormat.format("dd", date);
                String month = MatchListWrapperAdapter.getMonth((String) android.text.format.DateFormat.format("MMM", date));
                String isttime = null;
                try {
                    isttime = MatchListWrapperAdapter.getLocalTime(footballMatchJsonCaller.getMatchTime()).substring(0, 5);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TextView textView = (TextView) findViewById(R.id.team1_name);
                textView.setText(footballMatchJsonCaller.getHomeTeam());

                textView = (TextView) findViewById(R.id.team2_name);
                textView.setText(footballMatchJsonCaller.getAwayTeam());

                ImageView flag1 = (ImageView) findViewById(R.id.team1_image);
                ImageView flag2 = (ImageView) findViewById(R.id.team2_image);

                Glide.with(this).load(footballMatchJsonCaller.getHomeTeamFlag()).placeholder(R.drawable.ic_no_img).into(flag1);
                Glide.with(this).load(footballMatchJsonCaller.getAwayTeamFlag()).placeholder(R.drawable.ic_no_img).into(flag2);

                findViewById(R.id.team1_score).setVisibility(View.GONE);
                findViewById(R.id.team2_score).setVisibility(View.GONE);

                ((TextView) findViewById(R.id.venue)).setText(footballMatchJsonCaller.getStadium());
                ((TextView) findViewById(R.id.date)).setText(dayOfTheWeek + ", " + month + " " + day + ", " + isttime + " (IST) ");
                if (footballMatchJsonCaller.getResult() != null && footballMatchJsonCaller.getResult().equalsIgnoreCase("home_team ")) {
                    tvNeededRun.setText(footballMatchJsonCaller.getHomeTeam());
                }
                tvCurrentScore.setText(footballMatchJsonCaller.getMatchStatus());
                if ("?".equals(footballMatchJsonCaller.getAwayTeamScore())) {
//                    showNoCommentaries();
                } else {
                    if (footballMatchJsonCaller.isLive()) {
                        // cloackTimer.setVisibility(View.VISIBLE);
                        /*donutProgress.setVisibility(View.VISIBLE);*/
                        String timer;
                        String FORMAT = Constants.FOOTBALL_TIMER;
                        int hours = 0;
                        int minute = 0;
                        getTvMatchDay.setTextColor(getResources().getColor(R.color.app_theme_blue));
                        getTvMatchDay.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());
                        String time = footballMatchJsonCaller.getMatchStatus();
                        getTvMatchDay.setText(time + "'");
                        try {
                            int progress = (int) (Integer.parseInt(time));
                            mProgressBar.setVisibility(View.VISIBLE);
                            if (progress > 90 && progress <= 105) {
                                mProgressBar.setMax(105);
                            } else if (progress > 105 && progress <= 120) {
                                mProgressBar.setMax(120);
                            } else {
                                mProgressBar.setMax(90);
                            }
                            mProgressBar.setProgress(progress);

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (time.equalsIgnoreCase("HT")) {
                                getTvMatchDay.setText(time);
                                mProgressBar.setVisibility(View.VISIBLE);
                                mProgressBar.setMax(90);
                                mProgressBar.setProgress(45);
                            } else if (time.equalsIgnoreCase("FT")) {
                                getTvMatchDay.setText(time);
                                mProgressBar.setVisibility(View.VISIBLE);
                                mProgressBar.setMax(90);
                                mProgressBar.setProgress(mProgressBar.getMax());
                            }
                        }
                        timer = String.format(FORMAT, hours, minute);
                        //donutProgress.setProgress(minute);
                        //enableAutoRefreshContent();
                    }
                    StringBuilder score = new StringBuilder();
                    score.append(footballMatchJsonCaller.getHomeTeamScore());
                    score.append(" - ");
                    score.append(footballMatchJsonCaller.getAwayTeamScore());

                    textView = (TextView) findViewById(R.id.tv_match_time);
                    textView.setText(score.toString());

                    requestCommentaries = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return requestCommentaries;
    }

//    private void disableAutoRefreshContent() {
//        if (timerToRefreshContent != null) {
//            timerToRefreshContent.cancel();
//            timerToRefreshContent = null;
//        }
//    }
//
//    private void enableAutoRefreshContent() {
//        disableAutoRefreshContent();
//
//        timerToRefreshContent = new Timer();
//        timerToRefreshContent.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                autoRefreshCall();
//            }
//
//        }, Constants.TIMEINMILISECOND, Constants.TIMEINMILISECOND);
//    }
//
//    private void autoRefreshCall() {
//        requestMatchScoreDetails();
//    }

    private boolean handleScoreDetails(String content) {
        Log.i("Score Detail", "Handle Content");
        boolean success = false;

        JSONObject scoreDetails = ScoresJsonParser.parseScoreDetails(content);
        if (scoreDetails != null) {
            this.matchScoreDetails = scoreDetails;
            success = true;
        }
        return success;
    }

    private void requestMatchScoreDetails() {
        Log.i("Score Detail", "Request Score Details");

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(ScoresContentHandler.PARAM_SPORTS_TYPE, sportsType);
        parameters.put(ScoresContentHandler.PARAM_ID, matchId);
        parameters.put(ScoresContentHandler.PARAM_SERIESID, seriesId);
        requestContent(ScoresContentHandler.CALL_NAME_MATCH_DETAIL, parameters, SCORE_DETAIL_REQUEST_TAG);
    }

    private boolean handleScoreCard(String content) {
        Log.i("Score Detail", "Handle Content");
        boolean success = false;
        if (content != null) {
            success = true;
        }
        return success;
    }

    private class ScoreDetailComponentListener extends CustomComponentListener {

        public ScoreDetailComponentListener(ProgressBar progressBar, ViewGroup errorLayout) {
            super(SCORE_DETAIL_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            return ScoreDetailActivity.this.handleScoreDetails(content);
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {
            ScoreDetailActivity.this.setTitle();
            ScoreDetailActivity.this.renderScores();
        }

    }

}
