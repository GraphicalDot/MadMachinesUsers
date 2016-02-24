package com.sports.unity.scoredetails.footballdetail;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;

/**
 * Created by madmachines on 23/2/16.
 */
public class LiveFootballMatchStatFragment extends Fragment{
    private TextView textView;
    public LiveFootballMatchStatFragment() {
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
        textView.setText(R.string.foot_match_stat_not_exists);
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
