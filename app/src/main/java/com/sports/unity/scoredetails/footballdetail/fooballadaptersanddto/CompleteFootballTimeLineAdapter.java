package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.sports.unity.R;

import java.text.Format;
import java.util.List;

/**
 * Created by cfeindia on 28/2/16.
 */
public class CompleteFootballTimeLineAdapter extends RecyclerView.Adapter<CompleteFootballTimeLineAdapter.ViewHolder> {

    private final List<CompleteFootballTimeLineDTO> mValues;
    private Context context;
    public CompleteFootballTimeLineAdapter(List<CompleteFootballTimeLineDTO> mValues,Context context) {
        this.mValues = mValues;
        this.context = context;
   }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        try{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_football_match_timeline_card,parent,false);

        }catch (Exception e){e.printStackTrace();}
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try{

            TextDrawable drawable = null;
            holder.dto = mValues.get(position);
            if(holder.dto.getTvTeamFirst()!= null) {
                holder.tvFirstTeam.setText(holder.dto.getTvTeamFirst());
                holder.tvSecondTeam.setVisibility(View.GONE);

            }else {
                holder.tvSecondTeam.setText(holder.dto.getTvTeamSecond());
                holder.tvFirstTeam.setVisibility(View.GONE);

            }
            if(holder.dto.getKey()!=null){
                drawable = TextDrawable.builder()
                        .beginConfig().textColor(Color.BLACK)
                        .withBorder(2)
                        .width(100)
                        .height(100)
                        .bold()
                        .endConfig()
                        .buildRound(holder.dto.getKey(), (Color.WHITE));
                holder.ivCenterStatus.setImageDrawable(drawable);
            }else {
                holder.ivCenterStatus.setImageResource(R.drawable.ic_football);
            }




        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;

        private TextView tvFirstTeam;
        private TextView tvSecondTeam;
        private ImageView ivCenterStatus;
        public CompleteFootballTimeLineDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvFirstTeam = (TextView) view.findViewById(R.id.iv_team_first_status);
            tvSecondTeam = (TextView) view.findViewById(R.id.iv_team_second_status);
            ivCenterStatus = (ImageView) view.findViewById(R.id.iv_center_status);
            }
    }
}
