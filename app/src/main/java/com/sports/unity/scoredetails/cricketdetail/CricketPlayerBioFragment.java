package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sports.unity.R;
import com.sports.unity.scores.controller.fragment.BroadcastListAdapter;
import com.sports.unity.util.Constants;

/**
 * Created by madmachines on 15/2/16.
 */
public class CricketPlayerBioFragment extends Fragment {
    public CricketPlayerBioFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_cricket_bio, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {

    }
}
