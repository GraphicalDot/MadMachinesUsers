package com.sports.unity.scores;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.scores.controller.fragment.BroadcastListAdapter;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;

import org.json.JSONObject;

import java.util.ArrayList;

public class ScoreDetailActivity extends CustomAppCompatActivity {

    private static final String LIST_LISTENER_KEY = "list_commentaries_listener";
    private static final String LIST_OF_COMMENTARIES_REQUEST_TAG = "list_commentaries_request_tag";

    private ScoresContentListener contentListener = new ScoresContentListener();

    private ArrayList<JSONObject> broadcast= new ArrayList<>();

    private String sportsType = "football";
    private int matchId = 2157211;

    private RecyclerView mRecyclerView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_detail);

        initView();
    }

    @Override
    public void onResume() {
        super.onResume();

        addResponseListener();
        if( broadcast.size() == 0 ) {
            Log.i("List of Matches", "Through Resume");

//            showProgress(getView());
            requestContent();
        } else {
            //nothing
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        removeResponseListener();
//        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void initView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));
//        broadcast.add(this.getString(R.string.tour_news_details));

        BroadcastListAdapter mAdapter = new BroadcastListAdapter(broadcast, this);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void renderContent(){
        Log.i("List of Matches", "Render Content");

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private boolean handleContent(String content){
        Log.i("List of Matches", "Handle Content");
        boolean success = false;

        ArrayList<JSONObject> list = ScoresJsonParser.parseListOfMatchCommentaries(content);
        if( list.size() > 0 ){
            broadcast.clear();
            broadcast.addAll(list);
            success = true;
        } else {
            //nothing
        }
        return success;
    }

    private void requestContent() {
        Log.i("List of Matches", "Request Content");

//        hideErrorLayout(getView());

        ScoresContentHandler.getInstance().requestCommentaryOnMatch( matchId, LIST_LISTENER_KEY, LIST_OF_COMMENTARIES_REQUEST_TAG);
    }

    private void addResponseListener(){
        ScoresContentHandler.getInstance().addResponseListener(contentListener, LIST_LISTENER_KEY);
    }

    private void removeResponseListener(){
        ScoresContentHandler.getInstance().removeResponseListener(LIST_LISTENER_KEY);
    }

    private class ScoresContentListener implements ScoresContentHandler.ContentListener {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            if( tag.equals(LIST_OF_COMMENTARIES_REQUEST_TAG) ){
                boolean success = false;
                if( responseCode == 200 ){
                    success = ScoreDetailActivity.this.handleContent(content);
                    if (success) {
//                        hideErrorLayout(MatchListFragment.this.getView());
                        ScoreDetailActivity.this.renderContent();
                    } else {
                        Log.i("List of Matches", "Error In Handling Content");
//                        showErrorLayout(MatchListFragment.this.getView());
                    }
                } else {
                    Log.i("List of Matches", "Error In Response");
//                    showErrorLayout(MatchListFragment.this.getView());
                }

//                hideProgress(getView());
//                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                //nothing
            }
        }
    }

}
