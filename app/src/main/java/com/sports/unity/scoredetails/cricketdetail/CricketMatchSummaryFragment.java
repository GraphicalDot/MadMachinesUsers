package com.sports.unity.scoredetails.cricketdetail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
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
public class CricketMatchSummaryFragment extends Fragment implements CricketMatchSummaryHandler.ContentListener {
  /*  private TextView tvBatting;
    private ImageView ivPlayerFrst;
    private TextView tvFirstPlayerName;
    private TextView tvFirstPlayerRunRate;
    private TextView tvSecondPlayerName;
    private TextView tvsecondplayerrunrate;
    private ImageView ivplayersecond;
    private TextView tvfirstplayerrunonball;
    private TextView tvpartnershiprecord;
    private TextView tvsecondplayerrunonball;
    private TextView tvupcoming;
    private ImageView ivupcomingplayerfirst;
    private ImageView ivupcomingplayersecond;
    private ImageView ivupcomingplayerthird;
    private TextView tvfirstupcomingplayername;
    private TextView tvsecondupcomingplayername;
    private TextView tvthirdupcomingplayername;
    private TextView tvfirstupcomingplayerrunrate;
    private TextView tvsecondupcomingplayerrunrate;
    private TextView tvthirdupcomingplayerrunrate;
    private TextView tvbowling;
    private ImageView ivupcomingbowler;
    private TextView tvbowlername;
    private TextView tvbowlerover;
    private TextView tvbowlerWRun;
    private TextView tvbowlerecon;
    private TextView tvbowlerovers;
    private TextView tvbowlerwr;
    private RecyclerView mreRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
*/
    // upcomming match summary card
    private TextView tvMatchName;
    private TextView tvMatchDate;
    private TextView tossStatus;



    public CricketMatchSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        CricketMatchSummaryHandler cricketLiveMatchSummaryHandler = CricketMatchSummaryHandler.getInstance(getContext());
        cricketLiveMatchSummaryHandler.requestCricketMatchSummary();
        return inflater.inflate(R.layout.fragment_cricket_summary, container, false);
    }

    @Override
    public void handleContent(final int responseCode) {
      if(responseCode == 0){

      }
    }


}
