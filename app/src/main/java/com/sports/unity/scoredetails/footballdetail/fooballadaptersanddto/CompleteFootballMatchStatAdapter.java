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
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.sports.unity.R;

import java.util.List;

/**
 * Created by cfeindia on 26/2/16.
 */
public class CompleteFootballMatchStatAdapter  extends RecyclerView.Adapter<CompleteFootballMatchStatAdapter.ViewHolder> {

    private final List<CompleteFootballMatchStatDTO> mValues;
    private Context context;



    public CompleteFootballMatchStatAdapter(List<CompleteFootballMatchStatDTO> mValues,Context context) {
        this.mValues = mValues;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        try{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_football_match_stats_card,parent,false);

        }catch (Exception e){e.printStackTrace();}
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try{

            TextDrawable drawable = null;
            holder.dto = mValues.get(position);
            holder.tvLable.setText(holder.dto.getTvLable());
            drawable = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(2)
                    .width(60)
                    .height(60)
                    .bold()
                    .endConfig()
                    .buildRound( holder.dto.getIvLeftStatus(),(Color.GREEN));

             holder.ivLeftStatus.setImageDrawable(drawable);
            holder.ivLeftStatus.setBackgroundColor(Color.TRANSPARENT);
            drawable = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(2)
                    .width(60)
                    .height(60)
                    .bold()
                    .endConfig()
                    .buildRound( holder.dto.getIvRightStatus(),(Color.GREEN));
             holder.ivRightStatus.setImageDrawable(drawable);
            holder.ivRightStatus.setBackgroundColor(Color.TRANSPARENT);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        private TextView tvLable;
        private ImageView ivLeftStatus;
        private ImageView ivRightStatus;
        public CompleteFootballMatchStatDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvLable = (TextView) view.findViewById(R.id.tv_lable);
            ivLeftStatus = (ImageView) view.findViewById(R.id.iv_left_status);
            ivRightStatus = (ImageView) view.findViewById(R.id.iv_right_status);


        }
    }
}
