package com.sports.unity.scoredetails.cricketdetail.livecompletedmatchadapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.playerprofile.cricket.CricketPlayerMatchStatDTO;

import java.util.List;

/**
 * Created by madmachines on 20/2/16.
 */
public class LiveAndCompletedCricketBattingAdapter extends RecyclerView.Adapter<LiveAndCompletedCricketBattingAdapter.ViewHolder> {

    private final List<CricketPlayerMatchStatDTO> mValues;

    LiveAndCompletedCricketBattingAdapter(List<CricketPlayerMatchStatDTO> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_live_cricket_batting_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dto = mValues.get(position);
        holder.tvHeads.setText(holder.dto.getTitles());
        holder.tvTests.setText(holder.dto.getTestsMatch());
        holder.tvOdis.setText(holder.dto.getOdis());
        holder.tvT20s.setText(holder.dto.getT20s());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        private TextView tvHeads;
        private TextView tvTests;
        private TextView tvOdis;
        private TextView tvT20s;


        public CricketPlayerMatchStatDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvHeads = (TextView) view.findViewById(R.id.tv_head);
            tvTests = (TextView) view.findViewById(R.id.tv_tests);
            tvOdis = (TextView) view.findViewById(R.id.tv_odis);
            tvT20s = (TextView) view.findViewById(R.id.tv_t20s);
        }
    }
}
