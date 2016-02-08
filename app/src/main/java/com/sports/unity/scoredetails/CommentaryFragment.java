package com.sports.unity.scoredetails;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sports.unity.R;
import com.sports.unity.scores.DataServiceContract;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.scores.controller.fragment.BroadcastListAdapter;
import com.sports.unity.util.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentaryFragment extends Fragment implements FragementInterface<CommentriesModel> ,DataServiceContract {
    private RecyclerView mRecyclerView;
    private JSONObject matchScoreDetails = null;
    private ArrayList<CommentriesModel> commentaries = new ArrayList<>();

    private String sportsType = null;
    private String matchId = null;

    private Timer timerToRefreshContent = null;
    BroadcastListAdapter mAdapter = null;

    public CommentaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DataServiceContract)
        {
            ScoreDetailActivity scoreDetailActivity = (ScoreDetailActivity)context;
            scoreDetailActivity.requestData(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_commentary, container, false);
        Bundle b = getArguments();
        sportsType = b.getString(Constants.INTENT_KEY_TYPE);
        matchId = getActivity().getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        commentaries = b.getParcelableArrayList("commentries");
        initView(view);
        return view;
    }


    private void initView(View view) {

        // ((TextView)view.findViewById(R.id.venue)).setTypeface(FontTypeface.getInstance(getContext()).getRobotoCondensedBold());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);

        mAdapter = new BroadcastListAdapter(sportsType, commentaries, getContext());
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public List<CommentriesModel> getItems() {
        return commentaries;
    }

    @Override
    public void dataChanged() {
        mRecyclerView.postInvalidate();
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void requestData(int methodType) {

    }
}
