package com.sports.unity.scoredetails.cricketdetail.livecompletedmatchadapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.scoredetails.cricketdetail.CricketPlayerMatchStatDTO;

import java.util.List;

/**
 * Created by madmachines on 20/2/16.
 */
public class LiveAndCompletedCricketBowlingAdapter extends RecyclerView.Adapter<LiveAndCompletedCricketBowlingAdapter.ViewHolder> {

    private final List<CricketPlayerMatchStatDTO> mValues;

    LiveAndCompletedCricketBowlingAdapter(List<CricketPlayerMatchStatDTO> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_live_cricket_bowling_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dto = mValues.get(position);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        private TextView tvBowlerName;
        private TextView tvOver;
        private TextView tvMaiddenOver;
        private TextView tvRuns;

        private TextView tvWicket;
        private TextView tvExtra;


        public CricketPlayerMatchStatDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvBowlerName = (TextView) view.findViewById(R.id.tv_bowler_name);
            tvOver = (TextView) view.findViewById(R.id.tv_over);
            tvMaiddenOver = (TextView) view.findViewById(R.id.tv_midden_over);
            tvRuns = (TextView) view.findViewById(R.id.tv_runs);
            tvWicket = (TextView) view.findViewById(R.id.tv_wicket);
            tvExtra = (TextView) view.findViewById(R.id.tv_extra);

             }
    }
}
