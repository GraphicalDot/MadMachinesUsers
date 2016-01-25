package com.sports.unity.scores;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.scores.controller.fragment.BroadcastListAdapter;
import com.sports.unity.scores.controller.fragment.MatchListAdapter;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.football.CricketMatchJsonCaller;
import com.sports.unity.scores.model.football.FootballMatchJsonCaller;
import com.sports.unity.util.Constants;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ScoreDetailActivity extends CustomVolleyCallerActivity {

    private static final String REQUEST_LISTENER_KEY = "score_detail_listener";
    private static final String SCORE_DETAIL_REQUEST_TAG = "score_detail_request_tag";
    private static final String LIST_OF_COMMENTARIES_REQUEST_TAG = "list_commentaries_request_tag";

    private ScoresContentListener contentListener = new ScoresContentListener();

    private CricketMatchJsonCaller cricketMatchJsonCaller = new CricketMatchJsonCaller();
    private FootballMatchJsonCaller footballMatchJsonCaller = new FootballMatchJsonCaller();

    private JSONObject matchScoreDetails = null;
    private ArrayList<JSONObject> commentaries = new ArrayList<>();

    private String sportsType = null;
    private String matchId = null;

    private Timer timerToRefreshContent = null;

    private RecyclerView mRecyclerView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_detail);

        sportsType = getIntent().getStringExtra(Constants.INTENT_KEY_TYPE);
        matchId = getIntent().getStringExtra(Constants.INTENT_KEY_ID);

//        sportsType = ScoresJsonParser.FOOTBALL;
//        matchId = "2138018";

//        sportsType = ScoresJsonParser.CRICKET;
//        matchId = "bblt20_2015_g22";

        initToolbar();
        initView();

//        {
//            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
//            ScoreDetailComponentListener createUserComponentListener = new ScoreDetailComponentListener(progressBar);
//            MatchCommentariesComponentListener resendOtpComponentListener = new MatchCommentariesComponentListener(progressBar);
//
//            ArrayList<CustomComponentListener> listeners = new ArrayList<>();
//            listeners.add(createUserComponentListener);
//            listeners.add(resendOtpComponentListener);
//
//            onComponentCreate(listeners, REQUEST_LISTENER_KEY);
//        }

    }

    @Override
    public void onResume() {
        super.onResume();

        addResponseListener();

        {
            Log.i("Score Detail", "Through Resume");

            showProgress(false);
            requestMatchScoreDetails();
//            requestContent();
        }

        onComponentResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        removeResponseListener();
        disableAutoRefreshContent();

        onComponentPause();
    }

    private void initView() {

        initProgress();
        initErrorLayout();

        ((TextView)findViewById(R.id.venue)).setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);

        BroadcastListAdapter mAdapter = new BroadcastListAdapter(sportsType, commentaries, this);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        ImageView back_arrow = (ImageView) toolbar.findViewById(R.id.back_img);
        back_arrow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });

        ImageView refreshImageView = (ImageView) toolbar.findViewById(R.id.refresh);
        refreshImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestMatchScoreDetails();
                showProgress(true);
            }

        });
    }

    private void setTitle(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        TextView title_text = (TextView) toolbar.findViewById(R.id.toolbar_title);

        StringBuilder stringBuilder = new StringBuilder();

        try {
            if (sportsType.equals(ScoresJsonParser.CRICKET)) {
                cricketMatchJsonCaller.setJsonObject(matchScoreDetails);

                stringBuilder.append(cricketMatchJsonCaller.getTeam1());
                stringBuilder.append(" v/s ");
                stringBuilder.append(cricketMatchJsonCaller.getTeam2());

            } else if (sportsType.equals(ScoresJsonParser.FOOTBALL)) {
                footballMatchJsonCaller.setJsonObject(matchScoreDetails);

                stringBuilder.append(footballMatchJsonCaller.getHomeTeam());
                stringBuilder.append(" v/s ");
                stringBuilder.append(footballMatchJsonCaller.getAwayTeam());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        title_text.setText(stringBuilder.toString());
    }

    private void renderComments(){
        Log.i("Score Detail", "Render Comments");

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private boolean renderScores(){
        Log.i("Score Detail", "Render Scores");

        boolean requestCommentaries = false;
        if ( sportsType.equals(ScoresJsonParser.CRICKET) ) {
            cricketMatchJsonCaller.setJsonObject(matchScoreDetails);

            try {
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

                TextView textView = (TextView) findViewById(R.id.team1_name);
                textView.setText(cricketMatchJsonCaller.getTeam1());

                textView = (TextView) findViewById(R.id.team2_name);
                textView.setText(cricketMatchJsonCaller.getTeam2());

                ImageView flag1 = (ImageView)findViewById(R.id.team1_image);
                ImageView flag2 = (ImageView)findViewById(R.id.team2_image);

                Glide.with(this).load(cricketMatchJsonCaller.getTeam1Flag()).placeholder(R.drawable.ic_no_img).into(flag1);
                Glide.with(this).load(cricketMatchJsonCaller.getTeam2Flag()).placeholder(R.drawable.ic_no_img).into(flag2);

                findViewById(R.id.central_score).setVisibility(View.GONE);

                ((TextView)findViewById(R.id.venue)).setText(cricketMatchJsonCaller.getVenue());
                ((TextView)findViewById(R.id.date)).setText(dayOfTheWeek + ", " + month + " " + day + ", " + isttime + " (IST) ");

                if ( cricketMatchJsonCaller.getStatus().equals("notstarted") ) {
                    showNoCommentaries();

                } else {
                    if ( cricketMatchJsonCaller.getStatus().equals("completed") ) {

                    } else {
                        enableAutoRefreshContent();
                    }

                    {
                        JSONObject scoreJsonObject = cricketMatchJsonCaller.getTeam1Score();
                        StringBuilder stringBuilder = new StringBuilder("");
                        stringBuilder.append(cricketMatchJsonCaller.getScore(scoreJsonObject));
                        stringBuilder.append("/");
                        stringBuilder.append(cricketMatchJsonCaller.getWickets(scoreJsonObject));
                        stringBuilder.append(" (");
                        stringBuilder.append(cricketMatchJsonCaller.getOvers(scoreJsonObject));
                        stringBuilder.append(")");

                        textView = (TextView) findViewById(R.id.team1_score);
                        textView.setText(stringBuilder.toString());

                        textView.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());
                    }

                    {
                        JSONObject scoreJsonObject = cricketMatchJsonCaller.getTeam2Score();
                        StringBuilder stringBuilder = new StringBuilder("");
                        stringBuilder.append(cricketMatchJsonCaller.getScore(scoreJsonObject));
                        stringBuilder.append("/");
                        stringBuilder.append(cricketMatchJsonCaller.getWickets(scoreJsonObject));
                        stringBuilder.append(" (");
                        stringBuilder.append(cricketMatchJsonCaller.getOvers(scoreJsonObject));
                        stringBuilder.append(")");

                        textView = (TextView) findViewById(R.id.team2_score);
                        textView.setText(stringBuilder.toString());

                        textView.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());
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
                ((TextView)findViewById(R.id.date)).setText(dayOfTheWeek + ", " + month + " " + day + ", " + isttime + " (IST) ");

                if ("?".equals(footballMatchJsonCaller.getAwayTeamScore())) {
                    showNoCommentaries();
                } else {
                    if( footballMatchJsonCaller.isLive() ){
                        enableAutoRefreshContent();
                    } else {

                    }

                    StringBuilder score = new StringBuilder();
                    score.append(footballMatchJsonCaller.getHomeTeamScore());
                    score.append(" - ");
                    score.append(footballMatchJsonCaller.getAwayTeamScore());

                    textView = (TextView)findViewById(R.id.central_score);
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
        findViewById(R.id.no_comments).setVisibility(View.VISIBLE);
    }

    private boolean handleCommentaries(String content){
        Log.i("Score Detail", "Handle Content");
        boolean success = false;

        ArrayList<JSONObject> list = ScoresJsonParser.parseListOfMatchCommentaries(content);
        if( list.size() > 0 ){
            commentaries.clear();
            commentaries.addAll(list);
            success = true;
        } else {
            //nothing
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
        } else {
            //nothing
        }
        return success;
    }

    private void requestMatchScoreDetails() {
        Log.i("Score Detail", "Request Score Details");

        hideErrorLayout();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(ScoresContentHandler.PARAM_SPORTS_TYPE, sportsType);
        parameters.put(ScoresContentHandler.PARAM_ID, matchId);
        ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_MATCH_DETAIL, parameters, REQUEST_LISTENER_KEY, SCORE_DETAIL_REQUEST_TAG);
//        ScoresContentHandler.getInstance().requestScoresOfMatch( sportsType, matchId, REQUEST_LISTENER_KEY, SCORE_DETAIL_REQUEST_TAG);
    }

    private void requestMatchCommentaries() {
        Log.i("Score Detail", "Request Commentaries");

        hideErrorLayout();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(ScoresContentHandler.PARAM_SPORTS_TYPE, sportsType);
        parameters.put(ScoresContentHandler.PARAM_ID, matchId);
        ScoresContentHandler.getInstance().requestCall(ScoresContentHandler.CALL_NAME_MATCH_DETAIL, parameters, REQUEST_LISTENER_KEY, LIST_OF_COMMENTARIES_REQUEST_TAG);
//        ScoresContentHandler.getInstance().requestCommentaryOnMatch(sportsType, matchId, REQUEST_LISTENER_KEY, LIST_OF_COMMENTARIES_REQUEST_TAG);
    }

    private void initErrorLayout(){
        LinearLayout errorLayout = (LinearLayout) findViewById(R.id.error);

        TextView oops = (TextView)errorLayout.findViewById(R.id.oops);
        oops.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoLight());

        TextView something_wrong = (TextView) errorLayout.findViewById(R.id.something_wrong);
        something_wrong.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoLight());
    }

    private void showErrorLayout(){
        if( commentaries.size() == 0 ) {
            LinearLayout errorLayout = (LinearLayout) findViewById(R.id.error);
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideErrorLayout(){
        LinearLayout errorLayout = (LinearLayout) findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);

        findViewById(R.id.no_comments).setVisibility(View.GONE);
    }

    private void addResponseListener(){
        ScoresContentHandler.getInstance().addResponseListener(contentListener, REQUEST_LISTENER_KEY);
    }

    private void removeResponseListener(){
        ScoresContentHandler.getInstance().removeResponseListener(REQUEST_LISTENER_KEY);
    }

    private void initProgress(){
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void showProgress(boolean force){
        if( commentaries.size() == 0 || force ) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress(){
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
    }

    private class ScoresContentListener implements ScoresContentHandler.ContentListener {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            if( tag.equals(LIST_OF_COMMENTARIES_REQUEST_TAG) ){
                boolean success = false;
                if( responseCode == 200 ){
                    success = ScoreDetailActivity.this.handleCommentaries(content);
                    if (success) {
                        hideErrorLayout();
                        ScoreDetailActivity.this.renderComments();
                    } else {
                        Log.i("Score Detail", "Error In Handling Content");
                        showNoCommentaries();
                    }
                } else {
                    Log.i("Score Detail", "Error In Response");
                    showErrorLayout();
                }

                hideProgress();

//                mSwipeRefreshLayout.setRefreshing(false);
            } else if( tag.equals(SCORE_DETAIL_REQUEST_TAG) ) {
                boolean success = false;
                if( responseCode == 200 ){
                    success = ScoreDetailActivity.this.handleScoreDetails(content);
                    if (success) {
                        hideErrorLayout();

                        ScoreDetailActivity.this.setTitle();
                        boolean requestCommentaries = ScoreDetailActivity.this.renderScores();
                        if( requestCommentaries ){
                            ScoreDetailActivity.this.requestMatchCommentaries();
                        } else {
                            ScoreDetailActivity.this.requestMatchCommentaries();
//                            hideProgress();
                        }
                    } else {
                        Log.i("Score Detail", "Error In Handling Content");
                        showErrorLayout();
                        hideProgress();
                    }
                } else {
                    Log.i("Score Detail", "Error In Response");
                    showErrorLayout();
                    hideProgress();
                }
            } else {
                //nothing
            }
        }
    }

    private class ScoreDetailComponentListener extends CustomVolleyCallerActivity.CustomComponentListener {

        public ScoreDetailComponentListener(ProgressBar progressBar, ViewGroup errorLayout){
            super( SCORE_DETAIL_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            return false;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI() {

        }
    }

    private class MatchCommentariesComponentListener extends CustomVolleyCallerActivity.CustomComponentListener {

        public MatchCommentariesComponentListener(ProgressBar progressBar, ViewGroup errorLayout){
            super( LIST_OF_COMMENTARIES_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            return false;
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI() {

        }
    }

}
