package com.sports.unity.common.controller;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;

public class Tour_News_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tour_news, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoSlabBold());

        TextView details = (TextView) view.findViewById(R.id.details);
        FrameLayout frame = (FrameLayout) view.findViewById(R.id.frame);
        ImageView img = (ImageView) view.findViewById(R.id.img);
    }

}
