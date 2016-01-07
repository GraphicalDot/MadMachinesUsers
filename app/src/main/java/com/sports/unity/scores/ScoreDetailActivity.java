package com.sports.unity.scores;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.FontTypeface;
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

public class ScoreDetailActivity extends CustomAppCompatActivity {

    private static final String REQUEST_LISTENER_KEY = "list_commentaries_listener";
    private static final String SCORE_DETAIL_REQUEST_TAG = "score_detail_request_tag";
    private static final String LIST_OF_COMMENTARIES_REQUEST_TAG = "list_commentaries_request_tag";

    private ScoresContentListener contentListener = new ScoresContentListener();

    private CricketMatchJsonCaller cricketMatchJsonCaller = new CricketMatchJsonCaller();
    private FootballMatchJsonCaller footballMatchJsonCaller = new FootballMatchJsonCaller();

    private JSONObject matchScoreDetails = null;
    private ArrayList<JSONObject> commentaries = new ArrayList<>();

    private String sportsType = null;
    private String matchId = null;

    private RecyclerView mRecyclerView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_detail);

        sportsType = getIntent().getStringExtra(Constants.INTENT_KEY_TYPE);
        matchId = getIntent().getStringExtra(Constants.INTENT_KEY_ID);

        initToolbar();
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();

        addResponseListener();

        {
            Log.i("Score Detail", "Through Resume");

            showProgress();
            requestMatchScoreDetails();
//            requestContent();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        removeResponseListener();
    }

    private void initView() {

        initProgress();
        initErrorLayout();

        ((TextView)findViewById(R.id.venue)).setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        BroadcastListAdapter mAdapter = new BroadcastListAdapter(commentaries, this);
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
    }

    private void setTitle(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        TextView title_text = (TextView) toolbar.findViewById(R.id.toolbar_title);

        StringBuilder stringBuilder = new StringBuilder();

        try {
            if (sportsType.equals(ScoresJsonParser.CRICKET)) {
                cricketMatchJsonCaller.setJsonObject(matchScoreDetails);

                stringBuilder.append(cricketMatchJsonCaller.getTeam1());
                stringBuilder.append(" V" +
                        "S ");
                stringBuilder.append(cricketMatchJsonCaller.getTeam2());

            } else if (sportsType.equals(ScoresJsonParser.FOOTBALL)) {
                footballMatchJsonCaller.setJsonObject(matchScoreDetails);

                stringBuilder.append(footballMatchJsonCaller.getHomeTeam());
                stringBuilder.append(" VS ");
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

    private void renderScores(){
        Log.i("Score Detail", "Render Scores");

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

                ((TextView)findViewById(R.id.venue)).setText(cricketMatchJsonCaller.getVenue());
                ((TextView)findViewById(R.id.date)).setText(dayOfTheWeek + ", " + month + " " + day + ", " + isttime + " (IST) ");

                if ( cricketMatchJsonCaller.getStatus().equals("notstarted") ) {

                } else {
                    if ( cricketMatchJsonCaller.getStatus().equals("completed") ) {

                    } else {

                    }

                    StringBuilder score = new StringBuilder();
                    score.append(cricketMatchJsonCaller.getTeam1Score());
                    score.append("-");
                    score.append(cricketMatchJsonCaller.getTeam2Score());
                    textView = (TextView) findViewById(R.id.score);
                    textView.setText(score.toString());
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        } else if ( sportsType.equals(ScoresJsonParser.FOOTBALL) ) {
            footballMatchJsonCaller.setJsonObject(matchScoreDetails);

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

                ((TextView)findViewById(R.id.venue)).setText(footballMatchJsonCaller.getStadium());
                ((TextView)findViewById(R.id.date)).setText(dayOfTheWeek + ", " + month + " " + day + ", " + isttime + " (IST) ");

                if ("?".equals(footballMatchJsonCaller.getAwayTeamScore())) {

                } else {
                    if( footballMatchJsonCaller.isLive() ){

                    } else {

                    }

                    StringBuilder score = new StringBuilder();
                    score.append(footballMatchJsonCaller.getHomeTeamScore());
                    score.append("-");
                    score.append(footballMatchJsonCaller.getAwayTeamScore());
                    textView = (TextView) findViewById(R.id.score);
                    textView.setText(score.toString());
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

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

        ScoresContentHandler.getInstance().requestScoresOfMatch( sportsType, matchId, REQUEST_LISTENER_KEY, SCORE_DETAIL_REQUEST_TAG);
    }

    private void requestMatchCommentaries() {
        Log.i("Score Detail", "Request Commentaries");

        hideErrorLayout();

        ScoresContentHandler.getInstance().requestCommentaryOnMatch( sportsType, matchId, REQUEST_LISTENER_KEY, LIST_OF_COMMENTARIES_REQUEST_TAG);
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

    private void showProgress(){
        if( commentaries.size() == 0 ) {
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
//                        showErrorLayout();
                    }
                } else {
                    Log.i("Score Detail", "Error In Response");
//                    showErrorLayout();
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
                        ScoreDetailActivity.this.renderScores();

                        ScoreDetailActivity.this.requestMatchCommentaries();
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

}
