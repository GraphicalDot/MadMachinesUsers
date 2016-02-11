package com.sports.unity.scoredetails.cricketdetail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scoredetails.FragementInterface;
import com.sports.unity.scores.ErrorContract;
import com.sports.unity.scores.model.ScoresJsonParser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CricketMatchSummaryFragment extends Fragment implements FragementInterface<CricketMatchSummaryModel> {
    private TextView tvBatting;
    private ImageView ivPlayerFrst;
    private TextView tvFirstPlayerName;
    private TextView tvFirstPlayerRunRate;
    private TextView tv_second_player_name;
    private TextView tv_second_player_run_rate;
    private ImageView iv_player_second;
    private TextView tv_first_player_run_on_ball;
    private TextView tv_partnership_record;
    private TextView tv_second_player_run_on_ball;
    private TextView tv_upcoming;
    private ImageView iv_up_coming_player_first;
    private ImageView iv_up_coming_player_second;
    private ImageView iv_up_coming_player_third;
    private TextView tv_first_up_coming_player_name;
    private TextView tv_second_up_coming_player_name;
    private TextView tv_third_up_coming_player_name;
    private TextView tv_first_up_coming_player_run_rate;
    private TextView tv_second_up_coming_player_run_rate;
    private TextView tv_third_up_coming_player_run_rate;
    private TextView tv_bowling;
    private ImageView iv_up_coming_bowler;
    private TextView tv_bowler_name;
    private TextView tv_bowler_over;
    private TextView tv_bowler_W_Run;
    private TextView tv_bowler_econ;
    private TextView tv_bowler_overs;
    private TextView tv_bowler_wr;


    public CricketMatchSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cricket_summary, container, false);
    }

    @Override
    public List<CricketMatchSummaryModel> getItems() {
        return null;
    }






}
