package com.sports.unity.scores;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.FriendsWatchingHandler;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.common.viewhelper.GenericFragmentViewPagerAdapter;
import com.sports.unity.common.viewhelper.VolleyCallComponentHelper;
import com.sports.unity.gcm.GCMConstants;
import com.sports.unity.gcm.UnRegisterMatch;
import com.sports.unity.scoredetails.cricketdetail.CricketScoreCardHelper;
import com.sports.unity.scoredetails.cricketdetail.CricketSummaryHelper;
import com.sports.unity.scoredetails.cricketdetail.CricketLiveSummaryHelper;
import com.sports.unity.scoredetails.footballdetail.CompletedFootballMatchLineUpFragment;
import com.sports.unity.scoredetails.footballdetail.CompletedFootballMatchStatFragment;
import com.sports.unity.scoredetails.footballdetail.CompletedFootballMatchTimeLineFragment;
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchFromFragment;
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchSqadFragment;
import com.sports.unity.scoredetails.footballdetail.UpCommingFootballMatchTableFargment;
import com.sports.unity.scores.controller.fragment.MatchListWrapperAdapter;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.ScoresUtil;
import com.sports.unity.scores.model.football.CricketMatchJsonCaller;
import com.sports.unity.scores.model.football.FootballMatchJsonCaller;
import com.sports.unity.scores.viewhelper.MatchCommentaryHelper;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.sports.unity.util.Constants.INTENT_KEY_DATE;
import static com.sports.unity.util.Constants.INTENT_KEY_MATCH_NAME;
import static com.sports.unity.util.Constants.INTENT_KEY_TOSS;
import static com.sports.unity.util.Constants.INTENT_KEY_TYPE;

public class ScoreDetailActivity extends CustomVolleyCallerActivity {

    private static final String REQUEST_LISTENER_KEY = "score_detail_listener";
    private static final String SCORE_DETAIL_REQUEST_TAG = "score_detail_request_tag";

    private ArrayList<BasicVolleyRequestResponseViewHelper> fragmentVolleyHelperList = new ArrayList<>();

    private JSONObject matchScoreDetails = null;
    private CricketMatchJsonCaller cricketMatchJsonCaller = new CricketMatchJsonCaller();
    private FootballMatchJsonCaller footballMatchJsonCaller = new FootballMatchJsonCaller();

    private String sportsType = null;
    private String seriesId;
    private String matchId = null;
    private String matchStatus;
    private String matchTime;
    private Boolean isLive;
    private String LeagueName;

    private Timer timerToRefreshContent = null;

    private ViewPager mViewPager;
    private ImageView refreshImage;
    private ProgressBar mProgressBar = null;

    private View llMatchDetailLinear;
    private TextView teamFirstOvers;
    private TextView teamSecondOvers;
    private TextView tvMatchTime;
    private TextView getTvMatchDay;
    Intent callerIntent;
    private LinearLayout friendsLayout;
    private TextView friendsCount;

    private FriendsWatchingHandler.FriendsContentListener friendsContentListener = new FriendsWatchingHandler.FriendsContentListener() {
        @Override
        public void handleFriendsContent() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayFriendsWatching();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnCreate(getIntent());
    }

    public void initOnCreate(Intent intent) {
        setContentView(R.layout.activity_score_detail);

        getExtras(intent);
        boolean isPushNotification = false;
        try {
            isPushNotification = intent.getExtras().getBoolean(Constants.INTENT_KEY_PUSH, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!checkIfDeepLinked(intent) || !isPushNotification) {
            initView();
        }
        setToolbar();
        setTitle();

        {
            mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

            onComponentCreate();
            requestMatchScoreDetails();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initOnCreate(intent);
    }

    private boolean checkIfDeepLinked(Intent intent) {
        boolean isDeepLinked = false;
        try {
            isDeepLinked = intent.getAction().equalsIgnoreCase(Intent.ACTION_VIEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeepLinked;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isMatchLive()) {
            enableAutoRefreshContent();
        } else {
            //nothing
        }
        FriendsWatchingHandler.getInstance(this).addFriendsContentListener(friendsContentListener, REQUEST_LISTENER_KEY);
    }

    @Override
    public void onPause() {
        super.onPause();
        FriendsWatchingHandler.getInstance(this).removeFriendsContentListener(REQUEST_LISTENER_KEY);
        disableAutoRefreshContent();
    }

    @Override
    public VolleyCallComponentHelper getVolleyCallComponentHelper() {
        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper(REQUEST_LISTENER_KEY, new ScoreDetailComponentListener(null, null));
        return volleyCallComponentHelper;
    }

    private void getExtras(Intent intent) {
        Intent i = intent;
        if (!checkIfDeepLinked(i)) {
            callerIntent = intent;
            sportsType = i.getStringExtra(INTENT_KEY_TYPE);
            matchId = i.getStringExtra(Constants.INTENT_KEY_ID);
            matchStatus = i.getStringExtra(Constants.INTENT_KEY_MATCH_STATUS);
            matchTime = i.getStringExtra(Constants.INTENT_KEY_MATCH_TIME);
            isLive = i.getBooleanExtra(Constants.INTENT_KEY_MATCH_LIVE, false);
            LeagueName = i.getStringExtra(Constants.LEAGUE_NAME);
            if (Constants.SPORTS_TYPE_FOOTBALL.equals(sportsType)) {
                seriesId = i.getStringExtra(Constants.INTENT_KEY_LEAGUE_ID);
            } else {
                seriesId = i.getStringExtra(Constants.INTENT_KEY_SERIES);
            }
        } else {
            String uri = i.getDataString();
            decodeDataFromURL(uri);
        }
    }

    private void initFriendsWatching() {
        String id = matchId + "|" + seriesId;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String subsMatch = preferences.getString(id, "");
        if (isMatchLive() && id.equalsIgnoreCase(subsMatch)) {
            if (FriendsWatchingHandler.getInstance(this).isMatchExist(id)) {
                displayFriendsWatching();
            } else {
                FriendsWatchingHandler.getInstance(this).addMatch(id);
                FriendsWatchingHandler.getInstance(this).requestContent(REQUEST_LISTENER_KEY, SCORE_DETAIL_REQUEST_TAG);
            }
        }
    }

    private void displayFriendsWatching() {
        String id = matchId + "|" + seriesId;
        int friendsWatching = FriendsWatchingHandler.getInstance(this).getNoOfFriends(id);
        if (friendsWatching > 0) {
            friendsLayout = (LinearLayout) findViewById(R.id.friends_watching);
            friendsCount = (TextView) findViewById(R.id.friends_text);
            friendsLayout.setVisibility(View.VISIBLE);
            String count = "";
            if (friendsWatching < 99) {
                count = this.getResources().getQuantityString(R.plurals.friends_watching, friendsWatching, friendsWatching);
                friendsCount.setText(count);
            } else {
                count = "99<sup>+</sup> FRIENDS WATCHING";
                friendsCount.setText(Html.fromHtml(count));
            }
        } else {
            //nothing
        }
    }

    private void decodeDataFromURL(String uri) {
        String jsonString = uri.substring(uri.lastIndexOf("/") + 1);
        String data = null;
        try {
            data = URLDecoder.decode(jsonString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {

            if (data != null) {
                JSONObject notification = new JSONObject(data);
                if (!notification.isNull(GCMConstants.SPORTS_ID)) {
                    int sportsId = notification.getInt(GCMConstants.SPORTS_ID);
                    if (sportsId == 1 || sportsId == 2) {
                        sportsType = sportsId == 1 ? Constants.SPORTS_TYPE_CRICKET : Constants.SPORTS_TYPE_FOOTBALL;
                    } else {
                        sportsType = Constants.APP_NOTIFICATION;
                    }
                }
                if (!notification.isNull(GCMConstants.MATCH_ID)) {
                    matchId = notification.getString(GCMConstants.MATCH_ID);
                }
                if (!notification.isNull(GCMConstants.LEAGUE_SERIES_ID)) {
                    seriesId = notification.getString(GCMConstants.LEAGUE_SERIES_ID);
                }
                if (!notification.isNull(GCMConstants.MATCH_STATUS)) {

                    matchStatus = notification.getString(GCMConstants.MATCH_STATUS);
                }
                if (Constants.SPORTS_TYPE_FOOTBALL.equalsIgnoreCase(sportsType)) {
                    isLive = matchStatus.equalsIgnoreCase("L") ? true : false;
                }
            }
            callerIntent = new Intent(this, ScoreDetailActivity.class);
            callerIntent.putExtra(INTENT_KEY_TYPE, sportsType);
            callerIntent.putExtra(Constants.INTENT_KEY_ID, matchId);
            callerIntent.putExtra(Constants.INTENT_KEY_MATCH_STATUS, matchStatus);
            callerIntent.putExtra("tab_index", 1);
            if (Constants.SPORTS_TYPE_CRICKET.equalsIgnoreCase(sportsType)) {
                callerIntent.putExtra(Constants.INTENT_KEY_SERIES, seriesId);
            } else if (Constants.SPORTS_TYPE_FOOTBALL.equalsIgnoreCase(sportsType)) {
                callerIntent.putExtra(Constants.LEAGUE_NAME, seriesId);
                callerIntent.putExtra(Constants.INTENT_KEY_MATCH_LIVE, matchStatus.equalsIgnoreCase("L") ? true : false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isMatchLive() {
        boolean success = false;
        if (sportsType.equalsIgnoreCase(ScoresJsonParser.CRICKET)) {
            if (ScoresUtil.isCricketMatchLive(matchStatus)) {
                success = true;
            } else {

            }
        } else {
            if (isLive) {
                success = true;
            } else {

            }
        }
        return success;
    }

    private void disableAutoRefreshContent() {
        if (timerToRefreshContent != null) {
            timerToRefreshContent.cancel();
            timerToRefreshContent = null;
        }
    }

    private void enableAutoRefreshContent() {
        disableAutoRefreshContent();

        timerToRefreshContent = new Timer();
        timerToRefreshContent.schedule(new TimerTask() {

            @Override
            public void run() {
                autoRefreshCall();
            }

        }, Constants.SCORE_REFRESH_TIME_DURATION, Constants.SCORE_REFRESH_TIME_DURATION);
    }

    private void autoRefreshCall() {
        requestMatchScoreDetails();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                for (int index = 0; index < fragmentVolleyHelperList.size(); index++) {
                    BasicVolleyRequestResponseViewHelper helper = fragmentVolleyHelperList.get(index);
                    helper.requestContent();
                }
            }

        });
    }

    private void initView() {
        try {
            int tab_index = 0;
            if (!checkIfDeepLinked(getIntent())) {
                tab_index = getIntent().getIntExtra("tab_index", 1);
            }

            if (sportsType.equalsIgnoreCase(ScoresJsonParser.CRICKET)) {
                fragmentVolleyHelperList = getListOfViewHelpersForCricket(sportsType, matchStatus);
            } else {
                fragmentVolleyHelperList = getListOfViewHelpersForFootball(sportsType, matchStatus, matchTime, isLive);
            }

            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(new GenericFragmentViewPagerAdapter(getSupportFragmentManager(), fragmentVolleyHelperList));
            mViewPager.setOffscreenPageLimit(3);

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
                    autoRefreshCall();
                }
            });
            if (isMatchLive()) {
                refreshImage.setVisibility(View.VISIBLE);
            }


            llMatchDetailLinear = findViewById(R.id.ll_match_detail_linear);
            tvMatchTime = (TextView) findViewById(R.id.tv_match_time);

            getTvMatchDay = (TextView) findViewById(R.id.tv_game_day);
            getTvMatchDay.setTextColor(getResources().getColor(R.color.score_light_grey));
            getTvMatchDay.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        } catch (Exception e) {
            Toast.makeText(this, "Error Occured", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
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
                    title_text.setText(footballMatchJsonCaller.getLeagueName());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<BasicVolleyRequestResponseViewHelper> getListOfViewHelpersForCricket(String sportsType, String matchStatus) {
        ArrayList<BasicVolleyRequestResponseViewHelper> fragmentHelperList = new ArrayList<>();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(ScoresContentHandler.PARAM_SERIESID, seriesId);
        parameters.put(ScoresContentHandler.PARAM_SPORTS_TYPE, sportsType);
        parameters.put(ScoresContentHandler.PARAM_ID, matchId);

        if (ScoresUtil.isCricketMatchLive(matchStatus)) {
            CricketLiveSummaryHelper helper = new CricketLiveSummaryHelper(getString(R.string.summary));
            helper.setRequestParameters(parameters);
            fragmentHelperList.add(helper);
        } else {
            CricketSummaryHelper helper = new CricketSummaryHelper(getString(R.string.summary), callerIntent);
            helper.setParameters(parameters);
            fragmentHelperList.add(helper);
        }

        {
            MatchCommentaryHelper helper = new MatchCommentaryHelper(getString(R.string.commentary), matchStatus);
            helper.setRequestParameters(parameters);
            fragmentHelperList.add(helper);
        }

        {
            CricketScoreCardHelper helper = new CricketScoreCardHelper(getString(R.string.scorecard), matchStatus);
            helper.setRequestParameters(parameters);
            fragmentHelperList.add(helper);
        }

        return fragmentHelperList;
    }

    private ArrayList<BasicVolleyRequestResponseViewHelper> getListOfViewHelpersForFootball(String sportsType, String matchStatus, String matchTime, boolean live) {
        ArrayList<BasicVolleyRequestResponseViewHelper> fragmentHelperList = new ArrayList<>();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(ScoresContentHandler.PARAM_SERIESID, seriesId);
        parameters.put(ScoresContentHandler.PARAM_SPORTS_TYPE, sportsType);
        parameters.put(ScoresContentHandler.PARAM_ID, matchId);

        {
            Intent intent = callerIntent;
            String teamId1 = intent.getStringExtra(Constants.INTENT_KEY_TEAM1_ID);
            String teamId2 = intent.getStringExtra(Constants.INTENT_KEY_TEAM2_ID);

            parameters.put(ScoresContentHandler.PARAM_TEAM_1, teamId1);
            parameters.put(ScoresContentHandler.PARAM_TEAM_2, teamId2);
        }

        if (live) {
            {
                MatchCommentaryHelper helper = new MatchCommentaryHelper(getString(R.string.commentary), matchStatus);
                helper.setRequestParameters(parameters);
                fragmentHelperList.add(helper);
            }
            {
                CompletedFootballMatchStatFragment helper = new CompletedFootballMatchStatFragment(getString(R.string.matchstats));
                helper.setRequestParameters(parameters);
                fragmentHelperList.add(helper);
            }
        } else if (ScoresUtil.isFootballMatchCompleted(matchStatus, matchTime, live)) {
            {
                MatchCommentaryHelper helper = new MatchCommentaryHelper(getString(R.string.commentary), matchStatus);
                helper.setRequestParameters(parameters);
                fragmentHelperList.add(helper);
            }
            {
                CompletedFootballMatchStatFragment helper = new CompletedFootballMatchStatFragment(getString(R.string.matchstats));
                helper.setRequestParameters(parameters);
                fragmentHelperList.add(helper);
            }
        } else {
            UpCommingFootballMatchTableFargment helper = new UpCommingFootballMatchTableFargment(getString(R.string.table));
            helper.setRequestParameters(parameters);
            fragmentHelperList.add(helper);
        }

        if (live) {
            CompletedFootballMatchTimeLineFragment helper = new CompletedFootballMatchTimeLineFragment(getString(R.string.timeline));
            helper.setRequestParameters(parameters);
            fragmentHelperList.add(helper);
        } else if (ScoresUtil.isFootballMatchCompleted(matchStatus, matchTime, live)) {
            CompletedFootballMatchTimeLineFragment helper = new CompletedFootballMatchTimeLineFragment(getString(R.string.timeline));
            helper.setRequestParameters(parameters);
            fragmentHelperList.add(helper);
        } else {
            UpCommingFootballMatchFromFragment helper = new UpCommingFootballMatchFromFragment(getString(R.string.form), callerIntent);
            helper.setRequestParameters(parameters);
            fragmentHelperList.add(helper);
        }

        if (live) {
            CompletedFootballMatchLineUpFragment helper = new CompletedFootballMatchLineUpFragment(getString(R.string.lineup));
            helper.setRequestParameters(parameters);
            fragmentHelperList.add(helper);
        } else if (ScoresUtil.isFootballMatchCompleted(matchStatus, matchTime, live)) {
            CompletedFootballMatchLineUpFragment helper = new CompletedFootballMatchLineUpFragment(getString(R.string.lineup));
            helper.setRequestParameters(parameters);
            fragmentHelperList.add(helper);
        } else {
            UpCommingFootballMatchSqadFragment helper = new UpCommingFootballMatchSqadFragment(getString(R.string.squad), callerIntent, null);
            helper.setRequestParameters(parameters);
            fragmentHelperList.add(helper);
        }

        return fragmentHelperList;
    }

    private boolean renderScores() {
        Log.i("Score Detail", "Render Scores");
        boolean requestCommentaries = false;
        TextView tvNeededRun = (TextView) findViewById(R.id.tv_needed_run);
        TextView tvCurrentScore = (TextView) findViewById(R.id.tv_current_score);
        if (sportsType.equals(ScoresJsonParser.CRICKET)) {
            cricketMatchJsonCaller.setJsonObject(matchScoreDetails);

            try {
                matchStatus = cricketMatchJsonCaller.getStatus();
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
                Glide.with(this).load(cricketMatchJsonCaller.getTeam1Flag()).placeholder(R.drawable.ic_no_img).dontAnimate().into(flag1);
                Glide.with(this).load(cricketMatchJsonCaller.getTeam2Flag()).placeholder(R.drawable.ic_no_img).dontAnimate().into(flag2);
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
                String matchDate = DateUtil.getDateFromEpochTime(Long.valueOf(cricketMatchJsonCaller.getMatchDateTimeEpoch()) * 1000);
                ((TextView) findViewById(R.id.date)).setText(matchDate);
                String matchName = cricketMatchJsonCaller.getTeam1() + " vs " + cricketMatchJsonCaller.getTeam2();
                if (!TextUtils.isEmpty(cricketMatchJsonCaller.getMatchNumber())) {
                    matchName = matchName + ", " + cricketMatchJsonCaller.getMatchNumber();
                }
                tvNeededRun.setText(matchName);
                callerIntent.putExtra(INTENT_KEY_DATE, matchDate);
                callerIntent.putExtra(INTENT_KEY_MATCH_NAME, matchName);
                callerIntent.putExtra(Constants.INTENT_KEY_MATCH_STATUS, matchStatus);
                if (cricketMatchJsonCaller.getStatus().equalsIgnoreCase("N")) {

                    tvCurrentScore.setText(cricketMatchJsonCaller.getMatchResult());
                    tvCurrentScore.setText(DateUtil.getDayFromEpochTime((Long.valueOf(cricketMatchJsonCaller.getMatchDateTimeEpoch()) * 1000), this));
//                    showNoCommentaries();
                    TextView text1Score = (TextView) findViewById(R.id.team1_score);
                    TextView team2Score = (TextView) findViewById(R.id.team2_score);
                    text1Score.setText(cricketMatchJsonCaller.getTeam1());
                    team2Score.setText(cricketMatchJsonCaller.getTeam2());

                } else {

                    TextView textView = (TextView) findViewById(R.id.team1_name);
                    textView.setText(cricketMatchJsonCaller.getTeam1());

                    textView = (TextView) findViewById(R.id.team2_name);
                    textView.setText(cricketMatchJsonCaller.getTeam2());
                    if (cricketMatchJsonCaller.getStatus().equalsIgnoreCase("L")) {
                        tvCurrentScore.setVisibility(View.GONE);
                    } else {
                        tvCurrentScore.setText(cricketMatchJsonCaller.getMatchResult());
                        tvCurrentScore.setVisibility(View.VISIBLE);
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
                matchStatus = footballMatchJsonCaller.getMatchStatus();
                isLive = footballMatchJsonCaller.isLive();
                if (footballMatchJsonCaller.getMatchTime().equals(footballMatchJsonCaller.getMatchStatus()) && !footballMatchJsonCaller.isLive()) {
                    //  displayMatchTimer(12);
                    tvMatchTime.setText(DateUtil.getMatchTime(Long.valueOf(footballMatchJsonCaller.getMatchDateEpoch()) * 1000));
                    getTvMatchDay.setText(DateUtil.getMatchDays(Long.valueOf(footballMatchJsonCaller.getMatchDateEpoch()) * 1000, this));
                    llMatchDetailLinear.setVisibility(View.GONE);
                }
                if (!footballMatchJsonCaller.isLive() && (footballMatchJsonCaller.getMatchStatus().equalsIgnoreCase("FT") || footballMatchJsonCaller.getMatchStatus().equalsIgnoreCase("AET"))) {
                    getTvMatchDay.setText(R.string.full_time);


                } else if (!footballMatchJsonCaller.isLive() && "Postp.".equalsIgnoreCase(footballMatchJsonCaller.getMatchStatus())) {
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

                Glide.with(this).load(footballMatchJsonCaller.getHomeTeamFlag()).placeholder(R.drawable.ic_no_img).dontAnimate().into(flag1);
                Glide.with(this).load(footballMatchJsonCaller.getAwayTeamFlag()).placeholder(R.drawable.ic_no_img).dontAnimate().into(flag2);

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
                            mProgressBar.setMax(90);
                            if (progress > 90 && progress <= 105) {
                                mProgressBar.setMax(105);
                            } else if (progress > 105 && progress <= 120) {
                                mProgressBar.setMax(120);
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
                            } else if (time.equalsIgnoreCase("AET")) {
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
        if (!isMatchLive()) {
            disableAutoRefreshContent();
        }

        initFriendsWatching();
        return requestCommentaries;
    }

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
            refreshImage.setVisibility(View.VISIBLE);
        }

        @Override
        public void changeUI(String tag) {
            ScoreDetailActivity.this.setTitle();
            ScoreDetailActivity.this.renderScores();
            initView();
            if (!isMatchLive()) {
                refreshImage.setVisibility(View.GONE);
            }
        }

    }

}
