package com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;

import java.util.List;

/**
 * Created by madmachines on 22/2/16.
 */
public class LiveAndCompletedCricketBattingCardAdapter extends RecyclerView.Adapter<LiveAndCompletedCricketBattingCardAdapter.ViewHolder> {

    private final List<LiveAndCompletedCricketBattingCardDTO> mValues;

    public LiveAndCompletedCricketBattingCardAdapter(List<LiveAndCompletedCricketBattingCardDTO> mValues) {
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
        holder.tvPlayerName.setText(holder.dto.getTvPlayerName());
        holder.tvPlayerRun.setText(holder.dto.getTvPlayerRun());
        holder.tvBallPlayByPlayer.setText(holder.dto.getTvBallPlayByPlayer());
        holder.tvFourGainByPlayer.setText(holder.dto.getTvFourGainByPlayer());
        holder.tvSixGainByPlayer.setText(holder.dto.getTvSixGainByPlayer());
        holder.tvWicketBy.setText(holder.dto.getTvWicketBy());
        holder.tvSrRateOfPlayer.setText(holder.dto.getTvSrRateOfPlayer());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        private TextView tvPlayerName;
        private TextView tvPlayerRun;
        private TextView tvBallPlayByPlayer;
        private TextView tvFourGainByPlayer;
        private TextView tvSixGainByPlayer;
        private TextView tvWicketBy;
        private TextView tvSrRateOfPlayer;

        public LiveAndCompletedCricketBattingCardDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvPlayerName = (TextView) view.findViewById(R.id.tv_player_name);
            tvPlayerRun = (TextView) view.findViewById(R.id.tv_player_run);
            tvBallPlayByPlayer = (TextView) view.findViewById(R.id.tv_ball_play_by_player);
            tvFourGainByPlayer = (TextView) view.findViewById(R.id.tv_four_gain_by_player);
            tvSixGainByPlayer = (TextView) view.findViewById(R.id.tv_six_gain_by_player);
            tvWicketBy = (TextView) view.findViewById(R.id.tv_wicket_by);
            tvSrRateOfPlayer = (TextView) view.findViewById(R.id.tv_sr_rate_of_player);

        }
    }
}
