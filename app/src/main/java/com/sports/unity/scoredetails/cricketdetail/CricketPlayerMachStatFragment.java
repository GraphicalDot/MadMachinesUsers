package com.sports.unity.scoredetails.cricketdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sports.unity.R;

/**
 * Created by madmachines on 15/2/16.
 */
public class CricketPlayerMachStatFragment extends Fragment  implements CricketPlayerMatchStatHandler.ContentListener{
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CricketPlayerMatchStatHandler cricketPlayerMatchStatHandler = CricketPlayerMatchStatHandler.getInstance(context);
        cricketPlayerMatchStatHandler.addListener(this);
        cricketPlayerMatchStatHandler.requestData();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_players_cricket_stat_batting, container, false);
        initView(view);
        return view;
    }
    private void initView(View view) {


    }

    @Override
    public void handleContent( String content) {

    }
}
