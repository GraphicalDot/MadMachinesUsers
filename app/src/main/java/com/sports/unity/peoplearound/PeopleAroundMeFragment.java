package com.sports.unity.peoplearound;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.sports.unity.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleAroundMeFragment extends Fragment {

private RecyclerView recyclerview;

    public PeopleAroundMeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_people_around_me, container, false);
        initViews(v);
        return v;
    }


    private void initViews(View v) {

        recyclerview=(RecyclerView) v.findViewById(R.id.recyclerview);

    }

    private void showErrorLayout(View view) {

            LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
            errorLayout.setVisibility(View.VISIBLE);

    }

    private void hideErrorLayout(View view) {
        LinearLayout errorLayout = (LinearLayout) view.findViewById(R.id.error);
        errorLayout.setVisibility(View.GONE);
    }

    private void initProgress(View view) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void showProgress(View view) {

            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgress(View view) {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
    }


    public void handleContent() {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

        @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

}
