package com.sports.unity.scoredetails.cricketdetail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;

import java.util.List;

/**
 * Created by madmachines on 18/2/16.
 */
public class CricketPlayerMatchBowlingStatAdapter extends RecyclerView.Adapter<CricketPlayerMatchBowlingStatAdapter.ViewHolder> {

    private final List<CricketPlayerMatchStatDTO> mValues;

    CricketPlayerMatchBowlingStatAdapter(List<CricketPlayerMatchStatDTO> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_profile_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dto = mValues.get(position);
        holder.innings.setText(holder.dto.getInnings());
        holder.runs.setText(holder.dto.getRuns());
        holder.format.setText(holder.dto.getFormat());
        holder.matches.setText(holder.dto.getMatches());
        holder.average.setText(holder.dto.getAverage());
        holder.strikeRate.setText(holder.dto.getStrikeRate());
        holder.highest.setText(holder.dto.getHighest());
        holder.hundreds.setText(holder.dto.getHundreds());
        holder.notOut.setText(holder.dto.getNotOut());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        private TextView innings;
        private TextView runs;
        private TextView format;
        private TextView matches;
        private TextView average;
        private TextView strikeRate;
        private TextView highest;
        private TextView hundreds;
        private TextView notOut;

        public CricketPlayerMatchStatDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            /*leagueName = (TextView) view.findViewById(R.id.league_name);
            teamName = (TextView) view.findViewById(R.id.team_name);
            gameCount = (TextView) view.findViewById(R.id.game_count);
            goalsCount = (TextView) view.findViewById(R.id.goals_count);
            assist = (TextView) view.findViewById(R.id.assist);
            yc = (TextView) view.findViewById(R.id.yc);
            rc = (TextView) view.findViewById(R.id.rc);*/
        }
    }
}
