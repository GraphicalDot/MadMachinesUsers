package com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.scoredetails.cricketdetail.CricketPlayerMatchStatDTO;

import java.util.List;

/**
 * Created by madmachines on 22/2/16.
 */
public class LiveAndCompletedCricketFallOfWicketAdapter extends RecyclerView.Adapter<LiveAndCompletedCricketFallOfWicketAdapter.ViewHolder> {

    private final List<LiveAndCompletedCricketFallOfWicketCardDTO> mValues;

    public LiveAndCompletedCricketFallOfWicketAdapter(List<LiveAndCompletedCricketFallOfWicketCardDTO> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_live_cricket_fall_of_wickets_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dto = mValues.get(position);
        holder.tvBowlerName.setText(holder.dto.getTvBowlerName());
        holder.tvOver.setText(holder.dto.getTvOver());
        holder.tvMiddenOver.setText(holder.dto.getTvMiddenOver());
        holder.tvRuns.setText(holder.dto.getTvRuns());
        holder.tvWicket.setText(holder.dto.getTvWicket());
        holder.tvExtra.setText(holder.dto.getTvExtra());
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
        private TextView tvMiddenOver;
        private TextView tvRuns;
        private TextView tvWicket;
        private TextView tvExtra;

        public LiveAndCompletedCricketFallOfWicketCardDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvBowlerName = (TextView) view.findViewById(R.id.tv_bowler_name);
            tvOver = (TextView) view.findViewById(R.id.tv_over);
            tvMiddenOver = (TextView) view.findViewById(R.id.tv_midden_over);
            tvRuns = (TextView) view.findViewById(R.id.tv_runs);
            tvWicket = (TextView) view.findViewById(R.id.tv_wicket);
            tvExtra = (TextView) view.findViewById(R.id.tv_extra);
        }
    }
}
