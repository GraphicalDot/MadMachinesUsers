package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            String value = getLabelValue(holder.dto.getTvLable());
               holder.tvLable.setText(getLabelValue(holder.dto.getTvLable()));
                drawable = getTextDrawable(holder.dto.getIvLeftStatus());
                holder.ivLeftStatus.setImageDrawable(drawable);
                drawable = getTextDrawable(holder.dto.getIvRightStatus());
                holder.ivRightStatus.setImageDrawable(drawable);
                holder.ivCenterStatus.setImageDrawable(getCentralImageResource(holder.dto.getTvLable()));
                try{
                    Integer redValue= Integer.parseInt(holder.dto.getIvLeftStatus());
                    Integer blueValue= Integer.parseInt(holder.dto.getIvRightStatus());
                    holder.redView.getLayoutParams().width = (redValue+blueValue)/blueValue*100;
                    holder.blueView.getLayoutParams().width =(redValue+blueValue)/redValue*100;

                }catch (Exception e){e.printStackTrace();}
          }catch (Exception e){e.printStackTrace();}
    }

    private TextDrawable getTextDrawable(String strRightStatus) {
        int radius = context.getResources().getDimensionPixelSize(R.dimen.recent_ball_radius);
        TextDrawable drawable;
        drawable = TextDrawable.builder()
                .beginConfig().textColor(Color.BLACK)
                .withBorder(radius)
                .width(radius)
                .height(radius)
                .bold()
                .endConfig()
                .buildRound(strRightStatus, (Color.WHITE));
        return drawable;
    }

    private Drawable getCentralImageResource(String tvLable) {

        Resources.Theme theme = context.getTheme();
        int drwableId =R.drawable.ic_football;
        switch (tvLable){
            case "possestiontimetotal":
                drwableId = R.drawable.ic_possession;
                break;
            case "shotsgoal":
                drwableId = R.drawable.ic_shots;
                break;
            case "shotsongoal":
                drwableId = R.drawable.ic_shots_on_target;
                break;
            case "cornerstotal":
                drwableId = R.drawable.ic_corner;
                break;
            case "foulstotal":
                drwableId = R.drawable.ic_fouls;
                break;
            case "offsidestotal":
                drwableId = R.drawable.ic_offsides;
                break;
        }
        Drawable drawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(drwableId,theme);
        } else {
            drawable = context.getResources().getDrawable(drwableId);
        }
        return  drawable;
    }

    private String getLabelValue(String tvLable) {
        String lableValue = null;

        switch (tvLable){
            case "possestiontimetotal":
                lableValue = "POSSESION (%)";
                break;
            case "shotstotal":
                lableValue = "SHOTS";
                break;
            case "shotsongoal":
                lableValue = "SHOTS ON TARGET";
                break;
            case "cornerstotal":
                lableValue = "CORNERS";
                break;
            case "foulstotal":
                lableValue = "FOULS";
                break;
            case "offsidestotal":
                lableValue = "OFFSIDES";
                break;
        }



        return lableValue;
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
        private ImageView ivCenterStatus;
        private ImageView redView;
        private ImageView blueView;
        public CompleteFootballMatchStatDTO dto;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvLable = (TextView) view.findViewById(R.id.tv_lable);
            ivLeftStatus = (ImageView) view.findViewById(R.id.iv_left_status);
            ivRightStatus = (ImageView) view.findViewById(R.id.iv_right_status);
            ivCenterStatus = (ImageView) view.findViewById(R.id.iv_center_status);
            redView = (ImageView) view.findViewById(R.id.vw_red);
            blueView = (ImageView) view.findViewById(R.id.vw_blue);

        }
    }
}
