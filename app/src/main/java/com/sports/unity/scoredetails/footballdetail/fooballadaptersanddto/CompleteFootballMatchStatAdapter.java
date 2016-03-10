package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.sports.unity.R;

import java.util.List;

/**
 * Created by cfeindia on 26/2/16.
 */
public class CompleteFootballMatchStatAdapter extends RecyclerView.Adapter<CompleteFootballMatchStatAdapter.ViewHolder> {

    private final List<CompleteFootballMatchStatDTO> mValues;
    private Context context;


    public CompleteFootballMatchStatAdapter(List<CompleteFootballMatchStatDTO> mValues, Context context) {
        this.mValues = mValues;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        try {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_football_match_stats_card, parent, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {

            TextDrawable drawable = null;
            holder.setIsRecyclable(false);
            holder.dto = mValues.get(position);
            String value = holder.dto.getTvLable();
            holder.tvLable.setText(value);
            drawable = getTextDrawable(holder.dto.getIvLeftStatus());
            holder.ivLeftStatus.setImageDrawable(drawable);
            drawable = getTextDrawable(holder.dto.getIvRightStatus());
            holder.ivRightStatus.setImageDrawable(drawable);
            holder.ivCenterStatus.setImageDrawable(getCentralImageResource(value));
            holder.redView.getLayoutParams().width = holder.dto.getLeftGraphValue();
            holder.blueView.getLayoutParams().width = holder.dto.getRightGraphValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Drawable getBackgroundDrawable(int width, int color) {
        int radius = context.getResources().getDimensionPixelSize(R.dimen.recent_ball_radius);
        Drawable drawable;
        drawable = TextDrawable.builder()
                .beginConfig().textColor(Color.BLACK)
                .withBorder(radius)
                .width(width * 2)
                .height(radius * 2)
                .bold()
                .endConfig()
                .buildRect(" ", color);
        return drawable;
    }


    private TextDrawable getTextDrawable(String strRightStatus) {
        int radius = context.getResources().getDimensionPixelSize(R.dimen.recent_ball_radius);
        TextDrawable drawable;
        drawable = TextDrawable.builder()
                .beginConfig().textColor(Color.BLACK)
                .withBorder(radius)
                .width(radius)
                .height(radius)
                .useFont(Typeface.DEFAULT_BOLD)
                .bold()
                .endConfig()
                .buildRound(strRightStatus, (Color.WHITE));
        return drawable;
    }

    private Drawable getCentralImageResource(String tvLable) {

        Resources.Theme theme = context.getTheme();
        int drwableId = R.drawable.ic_shots;
        switch (tvLable) {
            case "POSSESION (%)":
                drwableId = R.drawable.ic_possession;
                break;
            case "SHOTS":
                drwableId = R.drawable.ic_shots;
                break;
            case "SHOTS ON TARGET":
                drwableId = R.drawable.ic_shots_on_target;
                break;
            case "CORNERS":
                drwableId = R.drawable.ic_corner;
                break;
            case "FOULS":
                drwableId = R.drawable.ic_fouls;
                break;
            case "OFFSIDES":
                drwableId = R.drawable.ic_offsides;
                break;
        }
        Drawable drawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(drwableId, theme);
        } else {
            drawable = context.getResources().getDrawable(drwableId);
        }
        return drawable;
    }

    /* private String getLabelValue(String tvLable) {
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
 */
    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public CompleteFootballMatchStatDTO dto;
        private TextView tvLable;
        private ImageView ivLeftStatus;
        private ImageView ivRightStatus;
        private ImageView ivCenterStatus;
        private View redView;
        private View blueView;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvLable = (TextView) view.findViewById(R.id.tv_lable);
            ivLeftStatus = (ImageView) view.findViewById(R.id.iv_left_status);
            ivRightStatus = (ImageView) view.findViewById(R.id.iv_right_status);
            ivCenterStatus = (ImageView) view.findViewById(R.id.iv_center_status);
            redView = view.findViewById(R.id.vw_red);
            blueView = view.findViewById(R.id.vw_blue);


        }
    }
}
