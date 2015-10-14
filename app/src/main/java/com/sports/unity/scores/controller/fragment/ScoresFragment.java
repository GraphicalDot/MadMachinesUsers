package com.sports.unity.scores.controller.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sports.unity.R;
import com.sports.unity.scores.model.cricket.CricketLiveScore;
import com.sports.unity.scores.model.cricket.Result;

import java.util.ArrayList;

/**
 * Created by Edwin on 15/02/2015.
 */
public class ScoresFragment extends Fragment implements Response.Listener<String>, Response.ErrorListener {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    TextView cricket;

    private ArrayList<Result> matches = null;
    private int volleyPendingRequests = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(com.sports.unity.R.layout.scores, container, false);
        cricket = (TextView) v.findViewById(R.id.cricket);
        cricket.setTypeface(Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "RobotoCondensed-Bold.ttf"));
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_scores);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        requestContent();
        return v;
    }

    private void requestContent(){
        matches = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String liveScore = "http://52.74.250.156:5000/live_score";
        String upcomingMatches = "http://52.74.250.156:5000/upcoming_fixtures";

        StringRequest stringRequest = null;
        stringRequest = new StringRequest(Request.Method.GET, liveScore, this, this);
        queue.add(stringRequest);

        stringRequest = new StringRequest(Request.Method.GET, upcomingMatches, this, this);
        queue.add(stringRequest);

        volleyPendingRequests = 2;
    }

    @Override
    public void onResponse(String response) {
        Log.i("data extracted : ", response);

        volleyPendingRequests--;

        CricketLiveScore cricketLiveScore = new Gson().fromJson(response, CricketLiveScore.class);
        ArrayList<Result> list = (ArrayList<Result>) cricketLiveScore.getResult();
        matches.addAll( list);

        if( volleyPendingRequests == 0 ){
            mAdapter = new ScoresAdapter( matches, getActivity().getApplicationContext(), getActivity());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
           //nothing
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        //TODO
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_scores_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
