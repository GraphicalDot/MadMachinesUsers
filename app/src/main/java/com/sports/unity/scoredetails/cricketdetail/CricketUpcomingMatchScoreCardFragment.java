package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.scores.ScoreDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class CricketUpcomingMatchScoreCardFragment extends Fragment {
    private TextView textView;
    public CricketUpcomingMatchScoreCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_empty_view, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {
        textView = (TextView) view.findViewById(R.id.tv_empty_view);
        textView.setText(R.string.scorecard_not_exist);
         }



    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();
    }
}
