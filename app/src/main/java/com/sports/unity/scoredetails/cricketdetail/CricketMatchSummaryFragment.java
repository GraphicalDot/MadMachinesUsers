package com.sports.unity.scoredetails.cricketdetail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sports.unity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CricketMatchSummaryFragment extends Fragment implements CricketMatchSummaryHandler.ContentListener {

    public CricketMatchSummaryFragment() {
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
    public void handleContent(final int responseCode,String content) {
      if(responseCode == 0){

      }
    }


}
