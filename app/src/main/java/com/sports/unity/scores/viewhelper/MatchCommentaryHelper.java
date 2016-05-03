package com.sports.unity.scores.viewhelper;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.viewhelper.BasicVolleyRequestResponseViewHelper;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scores.controller.fragment.BroadcastListAdapter;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.ScoresUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by amandeep on 27/4/16.
 */
public class MatchCommentaryHelper extends BasicVolleyRequestResponseViewHelper {

    private String title = null;
    private String matchStatus = "";
    private HashMap<String, String> requestParameters = null;

    private ArrayList<CommentriesModel> response = null;
    private ArrayList<CommentriesModel> commentaries = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private BroadcastListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private View tvEmptyView;

    public MatchCommentaryHelper(String title, String matchStatus){
        this.title = title;
        this.matchStatus = matchStatus;
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_commentary;
    }

    @Override
    public String getFragmentTitle() {
        return title;
    }

    @Override
    public String getRequestListenerKey() {
        return "Commentary";
    }

    @Override
    public String getRequestTag() {
        return "CommentaryRequestTag";
    }

    @Override
    public String getRequestCallName() {
//        boolean upcoming = ScoresUtil.isCricketMatchUpcoming(matchStatus);
//        String callName = null;
//        if( upcoming ) {
//            callName = null;
//        } else {
//            callName = ScoresContentHandler.CALL_NAME_MATCH_COMMENTARIES;
//        }
        return ScoresContentHandler.CALL_NAME_MATCH_COMMENTARIES;
    }

    @Override
    public HashMap<String, String> getRequestParameters() {
        return requestParameters;
    }

    @Override
    public CustomComponentListener getCustomComponentListener(View view) {
        ViewGroup errorLayout = (ViewGroup) view.findViewById(R.id.error);
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progress);

        MatchCommentaryComponentListener matchCommentaryComponentListener = new MatchCommentaryComponentListener( getRequestTag(), progressBar, errorLayout);
        return matchCommentaryComponentListener;
    }

    @Override
    public void initialiseViews(View view){
        initViews(view);
    }

    public void setRequestParameters(HashMap<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    private void initViews(View view){
//        boolean upcoming = ScoresUtil.isCricketMatchUpcoming(matchStatus);

        tvEmptyView = view.findViewById(R.id.tv_empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.commentary_refresh);

//        if( upcoming ){
//            tvEmptyView.setVisibility(View.VISIBLE);
//            swipeRefreshLayout.setVisibility(View.GONE);
//        } else {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
            mRecyclerView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(view.getContext(), VERTICAL, false));
            mRecyclerView.setNestedScrollingEnabled(false);

            String sportsType = getRequestParameters().get(ScoresContentHandler.PARAM_SPORTS_TYPE);
            mAdapter = new BroadcastListAdapter(sportsType, commentaries, view.getContext());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

//        swipeRefreshLayout.setRefreshing(true);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(true);
                    requestContent();
                }

            });
//        }
    }

    private void renderDisplay() {
//        swipeRefreshLayout.setRefreshing(false);

        if ( response.size() > 0 ) {
            commentaries.clear();
            commentaries.addAll(response);
            mAdapter.notifyDataSetChanged();

            response.clear();
            response = null;

            tvEmptyView.setVisibility(View.GONE);
        } else {
            tvEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public class MatchCommentaryComponentListener extends CustomComponentListener {

        public MatchCommentaryComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
            super(requestTag, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = false;
            try{
                response = ScoresJsonParser.parseListOfMatchCommentaries(content);
                success = true;
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return success;
        }

        @Override
        protected void showProgress() {
            if( commentaries.size() == 0 ) {
                super.showProgress();
            }

            if( swipeRefreshLayout != null ) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        protected void hideProgress() {
            super.hideProgress();

            if( swipeRefreshLayout != null ) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

        @Override
        protected void showErrorLayout() {
            if( commentaries.size() == 0 ){
                super.showErrorLayout();
            } else {
                Toast.makeText( tvEmptyView.getContext(), R.string.common_message_internet_not_available, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {
            renderDisplay();
        }

    }

}
