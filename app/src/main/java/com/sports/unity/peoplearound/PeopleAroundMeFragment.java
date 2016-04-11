package com.sports.unity.peoplearound;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.sports.unity.R;
import com.sports.unity.messages.controller.model.Person;
import com.sports.unity.peoplearound.adapters.PeopleAroundMeAdapter;
import com.sports.unity.util.Constants;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleAroundMeFragment extends Fragment implements DataNotifier {

    private ArrayList<Person> peoples = new ArrayList<>();
    private RecyclerView recyclerview;
    private PeopleAroundMeAdapter mAdapter;
    private Context context;

    public PeopleAroundMeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_people_around_me, container, false);
        initViews(v);
        initProgress(v);
        return v;
    }


    private void initViews(View v) {

        recyclerview=(RecyclerView) v.findViewById(R.id.recyclerview);
        mAdapter = new PeopleAroundMeAdapter(peoples,context);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, true));
        mAdapter.notifyDataSetChanged();

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


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void notifyPeoples(ArrayList<Person> peoples) {
        this.peoples = peoples;
        Log.i("notifyPeoples", "notifyPeoples: " + peoples);
        if(mAdapter==null){
            mAdapter = new PeopleAroundMeAdapter(peoples,context);


        }
        mAdapter.notifyDataSetChanged();
    }
}
