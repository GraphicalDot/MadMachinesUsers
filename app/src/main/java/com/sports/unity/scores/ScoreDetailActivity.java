package com.sports.unity.scores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
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
import com.sports.unity.common.controller.ViewPagerCricketScoreDetailAdapter;
import com.sports.unity.common.controller.ViewPagerFootballScoreDetailAdapter;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.view.DonutProgress;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scores.controller.fragment.MatchListAdapter;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.football.CricketMatchJsonCaller;
import com.sports.unity.scores.model.football.FootballMatchJsonCaller;
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
import java.util.TimerTask;

import static com.sports.unity.util.Constants.INTENT_KEY_TYPE;

public class ScoreDetailActivity extends CustomVolleyCallerActivity implements DataServiceContract {

    private static final String REQUEST_LISTENER_KEY = "score_detail_listener";
    private static final String SCORE_DETAIL_REQUEST_TAG = "score_detail_request_tag";
    private static final String LIST_OF_COMMENTARIES_REQUEST_TAG = "list_commentaries_request_tag";
    private static final String LIST_OF_SUMMARY_REQUEST_TAG = "list_summary_request_tag";



//    private ScoresContentListener contentListener = new ScoresContentListener();

    private CricketMatchJsonCaller cricketMatchJsonCaller = new CricketMatchJsonCaller();
    private FootballMatchJsonCaller footballMatchJsonCaller = new FootballMatchJsonCaller();

    private JSONObject matchScoreDetails = null;
    private ArrayList<CommentriesModel> commentaries = new ArrayList<>();

    private String sportsType = null;
    private String matchId = null;
    private String matchStatus;
    private  String matchTime;
    private Boolean isLive;
    private String LeagueName;
    private View llMatchDetailLinear;

    private Timer timerToRefreshContent = null;

    private RecyclerView mRecyclerView = null;
    private ViewPager mViewPager;
    private ViewPagerCricketScoreDetailAdapter cricketScoreDetailAdapter ;
    private ViewPagerFootballScoreDetailAdapter footballScoreDetailAdapter;
    private TextView teamFirstOvers;
    private TextView teamSecondOvers;
    private TextView tvMatchTime;
    private TextView getTvMatchDay;
    private DonutProgress donutProgress;
    private ImageView refreshImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_detail);
        Intent i = getIntent();
        sportsType = i.getStringExtra(INTENT_KEY_TYPE);
        matchId = i.getStringExtra(Constants.INTENT_KEY_ID);
        matchStatus= i.getStringExtra(Constants.INTENT_KEY_MATCH_STATUS);
        matchTime = i.getStringExtra(Constants.INTENT_KEY_MATCH_TIME);
        isLive = i.getBooleanExtra(Constants.INTENT_KEY_MATCH_LIVE, false);
        LeagueName= i.getStringExtra(Constants.LEAGUE_NAME);
        initView();
        setToolbar();

        {
            LinearLayout errorLayout = (LinearLayout) findViewById(R.id.error);
            errorLayout.setVisibility(View.GONE);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);

            ScoreDetailComponentListener createUserComponentListener = new ScoreDetailComponentListener(progressBar, errorLayout);
            MatchCommentariesComponentListener matchCommentariesComponentListener = new MatchCommentariesComponentListener(progressBar, errorLayout);
            MatchScoreCardComponentListener matchScoreCardComponentListener = new MatchScoreCardComponentListener(progressBar,errorLayout);
            ArrayList<CustomComponentListener> listeners = new ArrayList<>();
            listeners.add(createUserComponentListener);
            listeners.add(matchCommentariesComponentListener);
            listeners.add(matchScoreCardComponentListener);

            onComponentCreate(listeners, REQUEST_LISTENER_KEY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        onComponentResume();
        {
            Log.i("Score Detail", "Through Resume");
            requestMatchScoreDetails();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        disableAutoRefreshContent();
        onComponentPause();
    }

    private void initView() {

        /*((TextView)findViewById(R.id.venue)).setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);

        BroadcastListAdapter mAdapter = new BroadcastListAdapter(sportsType, commentaries, this);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);*/
        // Added by Ashish for tab on scroe details page
        try {
            mViewPager = (ViewPager) findViewById(R.id.pager);
            String cricketMatchtitles[] = {getString(R.string.summary), getString(R.string.commentary), getString(R.string.scorecard)};
            int numberOfCricketTabs = cricketMatchtitles.length;
            String footballMatchtitles[] = {getString(R.string.commentary), getString(R.string.matchstats), getString(R.string.timeline), getString(R.string.lineup)};
            int numberOfFootballTabs = footballMatchtitles.length;
            String footballMatchtitlesupcommingTitles[] = {getString(R.string.table), getString(R.string.form), getString(R.string.squad)};

            donutProgress = (DonutProgress) findViewById(R.id.donut_progress);




//<<<<<<< HEAD
//    private void initToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
//
//        ImageView back_arrow = (ImageView) toolbar.findViewById(R.id.back_img);
//        back_arrow.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
//        back_arrow.setOnClickListener(new View.OnClickListener() {
//=======
            // Creating The ViewPagerAdapterInMainActivity and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
            int tab_index = 0;
            if (sportsType.equalsIgnoreCase(ScoresJsonParser.CRICKET)) {
                cricketScoreDetailAdapter = new ViewPagerCricketScoreDetailAdapter(getSupportFragmentManager(), cricketMatchtitles, numberOfCricketTabs, commentaries, matchStatus);
                mViewPager.setAdapter(cricketScoreDetailAdapter);
                tab_index = getIntent().getIntExtra("tab_index", 1);
            } else {
                if(matchStatus.equals(matchTime) || "Postp.".equalsIgnoreCase(matchStatus) && !isLive){
                    footballScoreDetailAdapter = new ViewPagerFootballScoreDetailAdapter(getSupportFragmentManager(), footballMatchtitlesupcommingTitles, footballMatchtitlesupcommingTitles.length, commentaries,matchStatus,matchTime,isLive);
                    mViewPager.setAdapter(footballScoreDetailAdapter);
                    tab_index = getIntent().getIntExtra("tab_index", 0);
                } else {
                    footballScoreDetailAdapter = new ViewPagerFootballScoreDetailAdapter(getSupportFragmentManager(), footballMatchtitles, numberOfFootballTabs, commentaries,matchStatus,matchTime,isLive);
                    mViewPager.setAdapter(footballScoreDetailAdapter);
                    tab_index = getIntent().getIntExtra("tab_index", 1);

                }

                //>>>>>>> team2_dev_branch

            }
            // Assiging the Sliding Tab Layout View
            SlidingTabLayout tabs = (SlidingTabLayout) findViewById(com.sports.unity.R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
            tabs.setTabTextColor(R.color.filter_tab_selector);
            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.app_theme_blue);
                }
            });
            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(mViewPager);

            //set news pager as default

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

            refreshImage.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    int index = mViewPager.getCurrentItem();
                    List<Fragment> fargmentList = getSupportFragmentManager().getFragments();
                    Fragment fragment=  fargmentList.get(index);
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
        }catch (Exception e){
            Log.i("Exception Occured", "initView: ");
            Toast.makeText(this,"Error Occured",Toast.LENGTH_LONG);
            e.printStackTrace();
        }

//<<<<<<< HEAD
//        });
//
//        ImageView refreshImageView = (ImageView) toolbar.findViewById(R.id.refresh);
//        refreshImageView.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE,true));
//        refreshImageView.setOnClickListener(new View.OnClickListener() {
//=======
    }
//>>>>>>> team2_dev_branch

    private void setToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
    }

    private void setTitle(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        TextView title_text = (TextView) toolbar.findViewById(R.id.toolbar_title);

        StringBuilder stringBuilder = new StringBuilder();

        try {
            if (sportsType.equals(ScoresJsonParser.CRICKET)) {
                cricketMatchJsonCaller.setJsonObject(matchScoreDetails);

  /*              stringBuilder.append(cricketMatchJsonCaller.getTeam1());
                stringBuilder.append(" vs ");
                stringBuilder.append(cricketMatchJsonCaller.getTeam2());*/
                title_text.setText(LeagueName);
            } else if (sportsType.equals(ScoresJsonParser.FOOTBALL)) {
                footballMatchJsonCaller.setJsonObject(matchScoreDetails);

                /*stringBuilder.append(footballMatchJsonCaller.getHomeTeam());
                stringBuilder.append(" vs ");
                stringBuilder.append(footballMatchJsonCaller.getAwayTeam());*/
                title_text.setText(LeagueName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void renderComments(){
        Log.i("Score Detail", "Render Comments");
        //mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private boolean renderScores(){
        Log.i("Score Detail", "Render Scores");
        boolean requestCommentaries = false;
        TextView tvNeededRun = (TextView) findViewById(R.id.tv_needed_run);
        TextView tvCurrentScore = (TextView) findViewById(R.id.tv_current_score);
        if ( sportsType.equals(ScoresJsonParser.CRICKET) ) {
            cricketMatchJsonCaller.setJsonObject(matchScoreDetails);

            try {
                JSONArray wigetTeamsArray = cricketMatchJsonCaller.getTeamsWiget();
                if(wigetTeamsArray!=null){
                    cricketMatchJsonCaller.setMatchWidgetHomeTeam(wigetTeamsArray.getJSONObject(0));
                    cricketMatchJsonCaller.setMatchWidgetAwayTeam(wigetTeamsArray.getJSONObject(1));
                }



                Date date = new Date(new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date(Long.valueOf(cricketMatchJsonCaller.getMatchDateTimeEpoch()) * 1000)));
                String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);
                String day = (String) android.text.format.DateFormat.format("dd", date);
                String month = MatchListAdapter.getMonth((String) android.text.format.DateFormat.format("MMM", date));
                String isttime = null;
                try {
                    isttime = MatchListAdapter.getLocalTime(cricketMatchJsonCaller.getMatchTime()).substring(0, 5);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ImageView flag1 = (ImageView)findViewById(R.id.team1_image);
                ImageView flag2 = (ImageView)findViewById(R.id.team2_image);

                Glide.with(this).load(cricketMatchJsonCaller.getTeam1Flag()).placeholder(R.drawable.ic_no_img).into(flag1);
                Glide.with(this).load(cricketMatchJsonCaller.getTeam2Flag()).placeholder(R.drawable.ic_no_img).into(flag2);

               // findViewById(R.id.central_score).setVisibility(View.GONE);

                ((TextView)findViewById(R.id.venue)).setText(cricketMatchJsonCaller.getVenue());
                ((TextView)findViewById(R.id.date)).setText(dayOfTheWeek + ", " + month + " " + day + ", " + isttime + " (IST) ");
                tvNeededRun.setText(cricketMatchJsonCaller.getTeam1() + " vs " + cricketMatchJsonCaller.getTeam2() + ", " + cricketMatchJsonCaller.getMatchNumber());

                if ( cricketMatchJsonCaller.getStatus().equalsIgnoreCase("N") ) {

                    tvCurrentScore.setText(cricketMatchJsonCaller.getMatchResult());
                    tvCurrentScore.setText(DateUtil.getDaysDiffrence(cricketMatchJsonCaller.getMatchDate(), this));
                    showNoCommentaries();
                    TextView  text1Score = (TextView) findViewById(R.id.team1_score);
                    TextView  team2Score = (TextView) findViewById(R.id.team2_score);
                    text1Score.setText(cricketMatchJsonCaller.getTeam1());
                    team2Score.setText(cricketMatchJsonCaller.getTeam2());

               } else {

                    TextView textView = (TextView) findViewById(R.id.team1_name);
                    textView.setText(cricketMatchJsonCaller.getTeam1());

                    textView = (TextView) findViewById(R.id.team2_name);
                    textView.setText(cricketMatchJsonCaller.getTeam2());
                    if ( cricketMatchJsonCaller.getStatus().equalsIgnoreCase("F") ) {
                         tvCurrentScore.setText(cricketMatchJsonCaller.getWinnerTeam(cricketMatchJsonCaller.getResult())+" Won The Match");
                        {
                            //JSONObject scoreJsonObject = cricketMatchJsonCaller.getTeam1Score();
                            StringBuilder stringBuilder = new StringBuilder("");
                            stringBuilder.append(cricketMatchJsonCaller.getTeam1Score());
                            stringBuilder.append("/");
                            stringBuilder.append(cricketMatchJsonCaller.getWicketsTeam1());

                            teamFirstOvers.setText("("+cricketMatchJsonCaller.getOversTeam1()+")");

                            textView = (TextView) findViewById(R.id.team1_score);
                            textView.setText(stringBuilder.toString());

                            textView.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());
                        }

                        {
                            JSONObject scoreJsonObject = cricketMatchJsonCaller.getTeam2Score();
                            StringBuilder stringBuilder = new StringBuilder("");
                            stringBuilder.append(cricketMatchJsonCaller.getScore(scoreJsonObject));
                            stringBuilder.append("/");
                            stringBuilder.append(cricketMatchJsonCaller.getWicketsTeam2());
                            teamSecondOvers.setText("("+cricketMatchJsonCaller.getOversTeam2()+")");
                            textView = (TextView) findViewById(R.id.team2_score);
                            textView.setText(stringBuilder.toString());
                            textView.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());
                        }

                    } else {

                        enableAutoRefreshContent();
                    }



                   

                    requestCommentaries = true;
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        } else if ( sportsType.equals(ScoresJsonParser.FOOTBALL) ) {
            footballMatchJsonCaller.setJsonObject(matchScoreDetails);





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
                if(footballMatchJsonCaller.getMatchTime().equals(footballMatchJsonCaller.getMatchStatus()) && !footballMatchJsonCaller.isLive()){
                    tvMatchTime.setText(DateUtil.getMatchTime(Long.valueOf(footballMatchJsonCaller.getMatchDateEpoch()) * 1000));
                    getTvMatchDay.setText(DateUtil.getMatchDays(Long.valueOf(footballMatchJsonCaller.getMatchDateEpoch()) * 1000, this));
                    llMatchDetailLinear.setVisibility(View.GONE);
                }
                if(!footballMatchJsonCaller.isLive() && footballMatchJsonCaller.getMatchStatus().equalsIgnoreCase("FT")){
                    refreshImage.setVisibility(View.GONE);
                    getTvMatchDay.setText(R.string.full_time);


                } else if(!footballMatchJsonCaller.isLive() && "Postp.".equalsIgnoreCase(footballMatchJsonCaller.getMatchStatus())){
                    refreshImage.setVisibility(View.GONE);
                    getTvMatchDay.setText(R.string.post_pond);

                }
                llMatchDetailLinear.setVisibility(View.GONE);
                Date date = new Date(new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date(Long.valueOf(footballMatchJsonCaller.getMatchDateEpoch()) * 1000)));
                String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);
                String day = (String) android.text.format.DateFormat.format("dd", date);
                String month = MatchListAdapter.getMonth((String) android.text.format.DateFormat.format("MMM", date));
                String isttime = null;
                try {
                    isttime = MatchListAdapter.getLocalTime(footballMatchJsonCaller.getMatchTime()).substring(0, 5);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                TextView textView = (TextView) findViewById(R.id.team1_name);
                textView.setText(footballMatchJsonCaller.getHomeTeam());

                textView = (TextView) findViewById(R.id.team2_name);
                textView.setText(footballMatchJsonCaller.getAwayTeam());

                ImageView flag1 = (ImageView)findViewById(R.id.team1_image);
                ImageView flag2 = (ImageView)findViewById(R.id.team2_image);

                Glide.with(this).load(footballMatchJsonCaller.getHomeTeamFlag()).placeholder(R.drawable.ic_no_img).into(flag1);
                Glide.with(this).load(footballMatchJsonCaller.getAwayTeamFlag()).placeholder(R.drawable.ic_no_img).into(flag2);

                findViewById(R.id.team1_score).setVisibility(View.GONE);
                findViewById(R.id.team2_score).setVisibility(View.GONE);

                ((TextView)findViewById(R.id.venue)).setText(footballMatchJsonCaller.getStadium());
                ((TextView) findViewById(R.id.date)).setText(dayOfTheWeek + ", " + month + " " + day + ", " + isttime + " (IST) ");
                if(footballMatchJsonCaller.getResult() != null && footballMatchJsonCaller.getResult().equalsIgnoreCase("home_team ")) {
                    tvNeededRun.setText(footballMatchJsonCaller.getHomeTeam());
                }
                tvCurrentScore.setText(footballMatchJsonCaller.getMatchStatus());
                if ("?".equals(footballMatchJsonCaller.getAwayTeamScore())) {
                    showNoCommentaries();
                } else {
                    if( footballMatchJsonCaller.isLive() ){
                        donutProgress.setVisibility(View.VISIBLE);
                        Integer minute = 0;
                        try{
                            minute = Integer.parseInt(footballMatchJsonCaller.getMatchStatus());
                        }catch (Exception e ){
                            e.printStackTrace();
                        }
                        donutProgress.setProgress(minute);
                        enableAutoRefreshContent();
                    }

                    StringBuilder score = new StringBuilder();
                    score.append(footballMatchJsonCaller.getHomeTeamScore());
                    score.append(" - ");
                    score.append(footballMatchJsonCaller.getAwayTeamScore());

                    textView = (TextView)findViewById(R.id.tv_match_time);
                    textView.setText(score.toString());

                    requestCommentaries = true;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return requestCommentaries;
    }

    private void disableAutoRefreshContent(){
        if( timerToRefreshContent != null ){
            timerToRefreshContent.cancel();
            timerToRefreshContent = null;
        }
    }

    private void enableAutoRefreshContent(){
        disableAutoRefreshContent();

        timerToRefreshContent = new Timer();
        timerToRefreshContent.schedule(new TimerTask() {

            @Override
            public void run() {
                autoRefreshCall();
            }

        }, 60000, 60000);
    }

    private void autoRefreshCall(){
        requestMatchScoreDetails();
    }

    private void showNoCommentaries(){
//        findViewById(R.id.no_comments).setVisibility(View.VISIBLE);
    }

    private boolean handleCommentaries(String content){
        Log.i("Score Detail", "Handle Content");
        boolean success = false;

        ArrayList<CommentriesModel> list = ScoresJsonParser.parseListOfMatchCommentaries(content);
        if( list.size() > 0 ){
            commentaries.clear();
            commentaries.addAll(list);
            
            success = true;
            Fragment fragment= null;
            if(sportsType.equals(ScoresJsonParser.CRICKET)) {
                fragment= cricketScoreDetailAdapter.getItem(mViewPager.getCurrentItem());
            } else {
                fragment = footballScoreDetailAdapter.getItem(mViewPager.getCurrentItem());
            }
            if(fragment instanceof DataServiceContract) {
                DataServiceContract listner = (DataServiceContract)fragment;
                listner.dataChanged();
            }
        }
        return success;
    }

    private boolean handleScoreDetails(String content){
        Log.i("Score Detail", "Handle Content");
        boolean success = false;

        JSONObject scoreDetails = ScoresJsonParser.parseScoreDetails(content);
        if( scoreDetails != null ){
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
        requestContent(ScoresContentHandler.CALL_NAME_MATCH_DETAIL, parameters, SCORE_DETAIL_REQUEST_TAG);
    }

    private void requestMatchCommentaries() {
        Log.i("Score Detail", "Request Commentaries");

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(ScoresContentHandler.PARAM_SPORTS_TYPE, sportsType);
        parameters.put(ScoresContentHandler.PARAM_ID, matchId);
        ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_MATCH_COMMENTARIES, parameters, REQUEST_LISTENER_KEY, LIST_OF_COMMENTARIES_REQUEST_TAG);
    }





    private void showProgress(boolean force){
        if( commentaries.size() == 0 || force ) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void dataChanged() {

    }

    @Override
    public void requestData(int methodType) {
        if(methodType== 0 ){
            requestMatchCommentaries();
        }
    }

    private boolean handleScoreCard(String content){
        Log.i("Score Detail", "Handle Content");
        boolean success = false;
        if(content != null){
            success = true;
        }
        return success;
    }



    private class ScoreDetailComponentListener extends CustomVolleyCallerActivity.CustomComponentListener {



        public ScoreDetailComponentListener(ProgressBar progressBar, ViewGroup errorLayout){
            super( SCORE_DETAIL_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            return ScoreDetailActivity.this.handleScoreDetails(content);
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        protected void hideErrorLayout() {
            super.hideErrorLayout();

//            ScoreDetailActivity.this.findViewById(R.id.no_comments).setVisibility(View.GONE);
        }

        @Override
        protected void showErrorLayout() {

            if( commentaries.size() == 0 ) {
                super.showErrorLayout();
            } else {
                Toast.makeText(getApplicationContext(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void changeUI() {
            ScoreDetailActivity.this.setTitle();
            boolean requestCommentaries = ScoreDetailActivity.this.renderScores();
            if( requestCommentaries ){
                ScoreDetailActivity.this.requestMatchCommentaries();
            }
        }
    }

    private class MatchCommentariesComponentListener extends CustomVolleyCallerActivity.CustomComponentListener {

        private boolean successfulResponse = false;

        public MatchCommentariesComponentListener(ProgressBar progressBar, ViewGroup errorLayout){
            super( LIST_OF_COMMENTARIES_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            successfulResponse = ScoreDetailActivity.this.handleCommentaries(content);
            return true;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        protected void hideErrorLayout() {
            super.hideErrorLayout();

//            ScoreDetailActivity.this.findViewById(R.id.no_comments).setVisibility(View.GONE);
        }

        @Override
        protected void showErrorLayout() {
            Fragment fragment= null;
            if(sportsType.equals(ScoresJsonParser.CRICKET)) {
                fragment= cricketScoreDetailAdapter.getItem(mViewPager.getCurrentItem());
            } else {
                fragment = footballScoreDetailAdapter.getItem(mViewPager.getCurrentItem());
            }
            if(fragment instanceof ErrorContract) {
                ErrorContract contract = (ErrorContract)fragment;
                contract.errorHandle();
            }
            if( commentaries.size() == 0 ) {
                super.showErrorLayout();
            } else {
                //Toast.makeText(getApplicationContext(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void changeUI() {
            if (successfulResponse) {
                ScoreDetailActivity.this.renderComments();
            } else {
                Log.i("Score Detail", "Error In Handling Content");
                showNoCommentaries();
            }
        }
    }


    private class MatchScoreCardComponentListener extends CustomVolleyCallerActivity.CustomComponentListener {

        private boolean successfulResponse = false;

        public MatchScoreCardComponentListener(ProgressBar progressBar, ViewGroup errorLayout){
            super( LIST_OF_SUMMARY_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            successfulResponse = ScoreDetailActivity.this.handleScoreCard(content);
            return true;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        protected void hideErrorLayout() {
            super.hideErrorLayout();

//            ScoreDetailActivity.this.findViewById(R.id.no_comments).setVisibility(View.GONE);
        }

        @Override
        protected void showErrorLayout() {
            Fragment fragment= null;
            if(sportsType.equals(ScoresJsonParser.CRICKET)) {
                fragment= cricketScoreDetailAdapter.getItem(mViewPager.getCurrentItem());
            } else {
                fragment = footballScoreDetailAdapter.getItem(mViewPager.getCurrentItem());
            }
            if(fragment instanceof ErrorContract) {
                ErrorContract contract = (ErrorContract)fragment;
                contract.errorHandle();
            }
            if( commentaries.size() == 0 ) {
                super.showErrorLayout();
            } else {
               // Toast.makeText(getApplicationContext(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void changeUI() {
            if (successfulResponse) {
                ScoreDetailActivity.this.renderComments();
            } else {
                Log.i("Score Detail", "Error In Handling Content");
                showNoCommentaries();
            }
        }
    }



}
