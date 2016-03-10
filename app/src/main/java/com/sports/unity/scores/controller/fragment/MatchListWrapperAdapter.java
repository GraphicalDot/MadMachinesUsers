package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.MatchDay;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;


import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by madmachines on 3/3/16.
 */
public class MatchListWrapperAdapter extends RecyclerView.Adapter<MatchListWrapperAdapter.ViewHolder> {

    private List<MatchListWrapperDTO>  matchDay;
    private ArrayList<JSONObject> matchList = new ArrayList<>();
    private Activity activity;
    private  Context context;

    public MatchListWrapperAdapter(List<MatchListWrapperDTO> matchDay, Activity activity,Context context) {
        this.matchDay = matchDay;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_score_wrapper,parent,false);

        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try{
            MatchListWrapperDTO previousDTO = null;
            if(position == 0 || position == 1){
                previousDTO = matchDay.get(position);
            }else {
                previousDTO = matchDay.get(position-1);
            }

            MatchListWrapperDTO dto = matchDay.get(position);
            List<JSONObject>  list = holder.mAdapter.getList();
            list.clear();
            list.addAll(dto.getList());
            if(previousDTO.getDay().equalsIgnoreCase(dto.getDay()) && position!=0){
                holder.tvDayName.setVisibility(View.GONE);
            }else{
                holder.tvDayName.setVisibility(View.VISIBLE);
                holder.tvDayName.setText(dto.getDay());
            }

            holder.tvLeagueName.setText(dto.getLeagueName());
            if(dto.getSportsType().equals(Constants.SPORTS_TYPE_CRICKET)){
                holder.ivSportsIcon.setImageResource(R.drawable.ic_cricket_group);
            }else{
                holder.ivSportsIcon.setImageResource(R.drawable.ic_football_group);
            }
            holder.mAdapter.notifyDataSetChanged();
        }catch (Exception  e){e.printStackTrace();}

    }

    @Override
    public int getItemCount() {
        return matchDay.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        private TextView tvDayName;
        private TextView tvLeagueName;
        private RecyclerView rvChild;
        private MatchListAdapter mAdapter;
        private ImageView ivSportsIcon;


        public MatchDay dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvDayName = (TextView) view.findViewById(R.id.id_day_name);
            tvLeagueName = (TextView) view.findViewById(R.id.league_name);
            ivSportsIcon = (ImageView) view.findViewById(R.id.iv_league);
            rvChild = (RecyclerView) view.findViewById(R.id.child_rv);
            mAdapter = new MatchListAdapter(new ArrayList<JSONObject>() ,activity);
            rvChild.setLayoutManager(new LinearLayoutManager(context, VERTICAL, false));
            rvChild.setAdapter(mAdapter);

        }
    }

}
