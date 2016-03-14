package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;

import com.sports.unity.playerprofile.football.PlayerProfileView;
import com.sports.unity.util.Constants;

import java.util.List;

/**
 * Created by cfeindia on 27/2/16.
 */
public class UpCommingFootballMatchSquadAdapter  extends RecyclerView.Adapter<UpCommingFootballMatchSquadAdapter.ViewHolder>{

    private final List<UpCommingFootballMatchSquadDTO> mValues;
    private Context context;
    public UpCommingFootballMatchSquadAdapter(List<UpCommingFootballMatchSquadDTO> mValues,Context context) {
        this.mValues = mValues;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        try{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_foot_ball_match_squqard_card,parent,false);

        }catch (Exception e){e.printStackTrace();}
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try{
            holder.dto = mValues.get(position);
            holder.tvPlayerName.setText(holder.dto.getTvPlayerName());
            final  String id = holder.dto.getId();
            holder.tvPlayerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context, PlayerProfileView.class);
                    i.putExtra(Constants.INTENT_KEY_ID,id);
                    context.startActivity(i);
                }
            });

            holder.tvPlayerAge.setText(holder.dto.getTvPlayerAge());
            holder.tvP.setText(holder.dto.getTvP());
            holder.tvpl.setText(holder.dto.getTvpl());
            holder.tvgol.setText(holder.dto.getTvgol());
            holder.tvyellowcard.setText(holder.dto.getTvyellowcard());
            holder.tvredcard.setText(holder.dto.getTvredcard());


        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        private TextView tvPlayerName;
        private TextView tvPlayerAge;
        private TextView tvP;
        private TextView tvpl;
        private TextView tvgol;
        private TextView tvyellowcard;
        private TextView tvredcard;
        public UpCommingFootballMatchSquadDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvPlayerName = (TextView) view.findViewById(R.id.tv_player_name);
            tvPlayerAge = (TextView) view.findViewById(R.id.tv_player_age);
            tvP = (TextView) view.findViewById(R.id.tv_p);
            tvpl = (TextView) view.findViewById(R.id.tv_pl);
            tvgol = (TextView) view.findViewById(R.id.tv_gol);
            tvyellowcard = (TextView) view.findViewById(R.id.tv_yellow_card);
            tvredcard = (TextView) view.findViewById(R.id.tv_red_card);

        }
    }
}
