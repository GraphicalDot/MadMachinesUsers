package com.sports.unity.scoredetails.cricketdetail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sports.unity.R;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scoredetails.FragementInterface;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CricketMatchSummaryFragment extends Fragment implements FragementInterface<CricketMatchSummaryModel> {


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
