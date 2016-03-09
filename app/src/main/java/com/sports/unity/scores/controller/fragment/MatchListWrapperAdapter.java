package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.MatchDay;

import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;


import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by madmachines on 3/3/16.
 */
public class MatchListWrapperAdapter extends RecyclerView.Adapter<MatchListWrapperAdapter.ViewHolder> {

    private ArrayList<JSONObject> matchDay;
    private Activity activity;
    private MatchListAdapter mAdapter;
    public MatchListWrapperAdapter(ArrayList<JSONObject> matchDay, Activity context) {
        this.matchDay = matchDay;
        this.activity = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_score_wrapper,parent,false);

        return new ViewHolder(view);
    }
   @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       holder.tvDayName.setText("Day");
       mAdapter.notifyDataSetChanged();
   }

    @Override
    public int getItemCount() {
        return matchDay.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        private TextView tvDayName;
        private RecyclerView rvChild;


        public MatchDay dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvDayName = (TextView) view.findViewById(R.id.id_day_name);
            rvChild = (RecyclerView) view.findViewById(R.id.child_rv);
            mAdapter = new MatchListAdapter(matchDay,activity);
            rvChild.setLayoutManager(new LinearLayoutManager(activity, VERTICAL, false));
            rvChild.setAdapter(mAdapter);

        }
    }

}
