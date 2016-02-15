package com.sports.unity.scoredetails.footballdetail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sports.unity.R;
import com.sports.unity.scoredetails.FragementInterface;
import com.sports.unity.scores.model.ScoresJsonParser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FootballMatchTimelineFragment extends Fragment implements Response.ErrorListener, Response.Listener {

    public static final String REQUEST_TAG = "MainVolleyActivity";
    private TextView mTextView;
    private RequestQueue mQueue;
    private String url=  "http://52.74.75.79:8080/get_football_match_timeline?match_id=";

    public FootballMatchTimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mQueue = Volley.newRequestQueue(getContext());
        mQueue.start();


        return inflater.inflate(R.layout.fragment_football_match_timeline, container, false);
    }


    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(Object response) {

    }
}
