package com.sports.unity.scores.controller.fragment;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sports.unity.R;
import com.sports.unity.scores.model.football.FootballLiveScore;
import com.sports.unity.scores.model.football.FootballLiveScoreResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Edwin on 15/02/2015.
 */
public class ScoresFragment extends Fragment implements Response.Listener<String>, Response.ErrorListener {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<FootballLiveScoreResult> matches = null;
    private int volleyPendingRequests = 0;
    private String formattedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(com.sports.unity.R.layout.scores, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_scores);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        formattedDate = df.format(c.getTime());
        Log.i("currdate :", formattedDate);


        requestContent();
        return v;
    }

    private void requestContent() {
        matches = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String liveScore = "http://52.74.142.219:8080/get_league_fixtures?league_id=1204&date=" + formattedDate;
        String upcomingMatches = "http://52.74.142.219:8080/get_football_upcoming_fixtures";

        StringRequest stringRequest = null;
        stringRequest = new StringRequest(Request.Method.GET, liveScore, this, this);
        queue.add(stringRequest);

        stringRequest = new StringRequest(Request.Method.GET, upcomingMatches, this, this);
        queue.add(stringRequest);

        volleyPendingRequests = 2;
    }

    @Override
    public void onResponse(String response) {

        if ("[]".equals(response)) {
            volleyPendingRequests--;
            Log.i("data extracted : ", response);
        } else {
            volleyPendingRequests--;
            Log.i("data extracted : ", response);
            FootballLiveScore footballLiveScore = new Gson().fromJson(response, FootballLiveScore.class);
            ArrayList<FootballLiveScoreResult> list = (ArrayList<FootballLiveScoreResult>) footballLiveScore.getFootballLiveScoreResult();
            matches.addAll(list);

        }
        if (volleyPendingRequests == 0 || matches != null) {
            mAdapter = new ScoresAdapter(matches, getActivity(), getActivity());
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
