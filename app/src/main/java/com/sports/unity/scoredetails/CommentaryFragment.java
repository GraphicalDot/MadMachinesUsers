package com.sports.unity.scoredetails;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.scores.controller.fragment.BroadcastListAdapter;
import com.sports.unity.util.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentaryFragment extends Fragment implements FragementInterface<CommentriesModel> {
private RecyclerView mRecyclerView;
    private JSONObject matchScoreDetails = null;
    private ArrayList<CommentriesModel> commentaries = new ArrayList<>();

    private String sportsType = null;
    private String matchId = null;

    private Timer timerToRefreshContent = null;

    public CommentaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_commentary, container, false);
        Intent i = getActivity().getIntent();
        sportsType = i.getStringExtra(Constants.INTENT_KEY_TYPE);
        matchId = i.getStringExtra(Constants.INTENT_KEY_ID);
        commentaries = i.getParcelableArrayListExtra("commentries");
        initView(view);
        return view;
    }


    private void initView(View view) {

        ((TextView)view.findViewById(R.id.venue)).setTypeface(FontTypeface.getInstance(getContext()).getRobotoCondensedBold());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(false);

        BroadcastListAdapter mAdapter = new BroadcastListAdapter(sportsType, commentaries, getContext());
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public List<CommentriesModel> getItems() {
        return commentaries;
    }
}
