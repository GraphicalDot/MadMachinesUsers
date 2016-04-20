package com.sports.unity.scoredetails.commentary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scoredetails.cricketdetail.CricketMatchScoreCardUtil;
import com.sports.unity.scoredetails.cricketdetail.JsonParsers.CricketMatchScoreJsonParser;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBattingCardAdapter;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBattingCardDTO;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBowlingCardAdapter;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBowlingCardDTO;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketFallOfWicketAdapter;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketFallOfWicketCardDTO;
import com.sports.unity.scores.DataRequestService;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.scores.controller.fragment.BroadcastListAdapter;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;


public class MatchCommentaryFragment extends Fragment implements MatchCommentaryFragmentHandler.CommentaryListener {
    private ArrayList<CommentriesModel> commentaries = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private BroadcastListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DataRequestService dataRequestService;
    private Context mContext;
    private MatchCommentaryFragmentHandler matchCommentaryFragmentHandler;
    private String matchId;
    private String seriesId;
    private String sportsType;

    private String matchStatus;
    private  Timer timerToRefreshContent;
    private  boolean reloadFlag;
    private View tvEmptyView;
    private  LinearLayout errorLayout;

    public MatchCommentaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent i = getActivity().getIntent();
        matchId =  i.getStringExtra(Constants.INTENT_KEY_ID);
        seriesId=  i.getStringExtra(Constants.INTENT_KEY_SERIES);
        sportsType = i.getStringExtra(Constants.INTENT_KEY_TYPE);
        matchStatus= i.getStringExtra(Constants.INTENT_KEY_MATCH_STATUS);
        mContext = context;
        getCommentary();
    }

    private void getCommentary() {
        matchCommentaryFragmentHandler = MatchCommentaryFragmentHandler.getInstance(mContext);
        matchCommentaryFragmentHandler.addListener(this);
        matchCommentaryFragmentHandler.requestMatchCommentary(seriesId, matchId, sportsType);
        if(matchStatus.equalsIgnoreCase("L")){
            reloadCommentary();
        }
    }






    private void reloadCommentary() {
        timerToRefreshContent = new Timer();
        if(reloadFlag){
            timerToRefreshContent.schedule(new TimerTask() {

                @Override
                public void run() {
                    getCommentary();
                }

            }, Constants.TIMEINMILISECOND, Constants.TIMEINMILISECOND);

        }else{
            timerToRefreshContent.cancel();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_commentary, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getContext(), VERTICAL, false));
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new BroadcastListAdapter(sportsType, commentaries, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.commentary_refresh);
        swipeRefreshLayout.setRefreshing(true);
        tvEmptyView = view.findViewById(R.id.tv_empty_view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getCommentary();
            }
        });
        errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);
    }



    @Override
    public void handleContent(String content) {
        {

            try {
                ArrayList<CommentriesModel> list = ScoresJsonParser.parseListOfMatchCommentaries(content);
                if( list.size()>0 ) {
                    renderDisplay(list);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    tvEmptyView.setVisibility(View.VISIBLE);

                }
            }catch (Exception ex){
                ex.printStackTrace();
                showErrorLayout(getView());
            }
        }
    }

    private void showErrorLayout(View view) {

        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);

    }

    private void renderDisplay(final ArrayList<CommentriesModel> list) throws JSONException {
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
        commentaries.clear();
        tvEmptyView.setVisibility(View.GONE);
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            commentaries.addAll(list);
                            mAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showErrorLayout(getView());
                        }
                    }
                });
            }
    }

    @Override
    public void onPause() {
        super.onPause();
        reloadFlag = false;
        if(matchCommentaryFragmentHandler != null){
            matchCommentaryFragmentHandler.addListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        reloadFlag = true;
        if(matchCommentaryFragmentHandler != null){
            matchCommentaryFragmentHandler.addListener(this);
        }else {
            matchCommentaryFragmentHandler= MatchCommentaryFragmentHandler.getInstance(getContext());
        }
        getCommentary();
    }




}
