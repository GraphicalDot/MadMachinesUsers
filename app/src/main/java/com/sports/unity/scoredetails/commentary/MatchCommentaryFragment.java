package com.sports.unity.scoredetails.commentary;

import android.content.Context;
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
    private RelativeLayout team1ScoreDetails;
    private RelativeLayout team2ScoreDetails;
    private String sportsType;
    private ProgressBar progressBar;

    public MatchCommentaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        matchId =  getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        seriesId=  getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_SERIES);
        sportsType = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_TYPE);
        mContext = context;
        getCommentary();
    }

    private void getCommentary() {
        matchCommentaryFragmentHandler = MatchCommentaryFragmentHandler.getInstance(mContext);
        matchCommentaryFragmentHandler.addListener(this);
        matchCommentaryFragmentHandler.requestMatchCommentary(seriesId, matchId, sportsType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_commentary, container, false);
        initView(view);
        initProgress(view);
        return view;
    }

    private void initView(View view) {
        // ((TextView)view.findViewById(R.id.venue)).setTypeface(FontTypeface.getInstance(getContext()).getRobotoCondensedBold());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new org.solovyev.android.views.llm.LinearLayoutManager(getContext(), VERTICAL, false));
        mRecyclerView.setNestedScrollingEnabled(false);
        mAdapter = new BroadcastListAdapter(sportsType, commentaries, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.commentary_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCommentary();
            }
        });


    }


    private void initProgress(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);

    }
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);

    }
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);

    }
    @Override
    public void handleContent(String content) {
        {
            showProgress();
            try {
                ArrayList<CommentriesModel> list = ScoresJsonParser.parseListOfMatchCommentaries(content);
                if( list.size()>0 ) {
                    renderDisplay(list);
                } else {

                    showErrorLayout(getView());
                }
            }catch (Exception ex){
                ex.printStackTrace();
                showErrorLayout(getView());
            }
        }
    }

    private void initErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);

    }

    private void showErrorLayout(View view) {

        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.VISIBLE);

    }

    private void renderDisplay(final ArrayList<CommentriesModel> list) throws JSONException {
        ScoreDetailActivity activity = (ScoreDetailActivity) getActivity();
      commentaries.clear();


            hideProgress();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            commentaries.addAll(list);
                            mAdapter.notifyDataSetChanged();
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
        if(matchCommentaryFragmentHandler != null){
            matchCommentaryFragmentHandler.addListener(null);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        showProgress();
        if(matchCommentaryFragmentHandler != null){
            matchCommentaryFragmentHandler.addListener(this);

        }else {
            matchCommentaryFragmentHandler= MatchCommentaryFragmentHandler.getInstance(getContext());
        }
        getCommentary();
    }
}
