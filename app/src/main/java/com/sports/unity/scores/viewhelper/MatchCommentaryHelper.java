package com.sports.unity.scores.viewhelper;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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

    private SwipeRefreshLayout swipeRefreshLayout = null;
    private View contentLayout = null;

    private BroadcastListAdapter mAdapter;

    private View tvEmptyView;

    public MatchCommentaryHelper(String title, String matchStatus) {
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
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        MatchCommentaryComponentListener matchCommentaryComponentListener = new MatchCommentaryComponentListener(getRequestTag(), progressBar, errorLayout, contentLayout, swipeRefreshLayout);
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
//        boolean upcoming = ScoresUtil.isCricketMatchUpcoming(matchStatus);

        tvEmptyView = view.findViewById(R.id.tv_empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        contentLayout = view.findViewById(R.id.content_layout);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(view.getContext());
        RecyclerView mRecyclerView = (RecyclerView) contentLayout;
        mRecyclerView.setLayoutManager(manager);

        String sportsType = getRequestParameters().get(ScoresContentHandler.PARAM_SPORTS_TYPE);
        mAdapter = new BroadcastListAdapter(sportsType, commentaries, view.getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                requestContent();
            }

        });
    }

    private void renderDisplay() {
        if (response.size() > 0) {
            commentaries.clear();
            commentaries.addAll(response);
            mAdapter.notifyDataSetChanged();

            response.clear();
            response = null;

            tvEmptyView.setVisibility(View.GONE);
        } else {
            if( commentaries.size() > 0 ){
                //nothing
            } else {
                tvEmptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    public class MatchCommentaryComponentListener extends CustomComponentListener {

        public MatchCommentaryComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout, View contentLayout, SwipeRefreshLayout swipeRefreshLayout) {
            super(requestTag, progressBar, errorLayout, contentLayout, swipeRefreshLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = false;
            try {
                response = ScoresJsonParser.parseListOfMatchCommentaries(content);
                success = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return success;
        }

        @Override
        protected boolean isContentLayoutAvailable() {
            return commentaries.size() > 0;
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
