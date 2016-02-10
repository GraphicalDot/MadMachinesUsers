package com.sports.unity.player.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.sports.unity.R;

import java.util.List;

/**
 * Created by Ashish Katiyar on 9/2/16.
 */
public class PlayerScorecardRecyclerView extends RecyclerView.Adapter<PlayerScorecardRecyclerView.ViewHolder> {

    private final List<PlayerScoreCardDTO> mValues;

    PlayerScorecardRecyclerView(List<PlayerScoreCardDTO> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_profile_card_recyclerview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dto = mValues.get(position);
        holder.leagueName.setText(holder.dto.getLeagueName());
        holder.teamName.setText(holder.dto.getTeamName());
        holder.gameCount.setText(holder.dto.getNoOfGames());
        holder.goalsCount.setText(holder.dto.getNoOfgoals());
        holder.assist.setText(holder.dto.getNoOfAssists());
        holder.yc.setText(holder.dto.getNoOfYellowCard());
        holder.rc.setText(holder.dto.getNoOfRedCard());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public final TextView leagueName;
        public final TextView    teamName;
        public final TextView    gameCount;
        public final TextView goalsCount;
        public final TextView    assist;
        public final TextView    yc;
        public final TextView    rc;
        public PlayerScoreCardDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            leagueName = (TextView) view.findViewById(R.id.league_name);
            teamName = (TextView) view.findViewById(R.id.team_name);
            gameCount = (TextView) view.findViewById(R.id.game_count);
            goalsCount = (TextView) view.findViewById(R.id.goals_count);
            assist = (TextView) view.findViewById(R.id.assist);
            yc = (TextView) view.findViewById(R.id.yc);
            rc = (TextView) view.findViewById(R.id.rc);
        }
    }

}
