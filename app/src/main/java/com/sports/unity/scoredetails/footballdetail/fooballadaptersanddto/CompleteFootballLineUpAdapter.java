package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
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
 * Created by madmachines on 1/3/16.
 */
public class CompleteFootballLineUpAdapter  extends RecyclerView.Adapter<CompleteFootballLineUpAdapter.ViewHolder> {

    private final List<CompleteFootballLineUpDTO> mValues;
    private Context context;



    public CompleteFootballLineUpAdapter(List<CompleteFootballLineUpDTO> mValues,Context context) {
        this.mValues = mValues;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        try{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.football_live_match_lineups_card,parent,false);

        }catch (Exception e){e.printStackTrace();}
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try{

            TextDrawable drawable = null;
            holder.dto = mValues.get(position);

            drawable = TextDrawable.builder()
                    .beginConfig().textColor(Color.BLACK)
                    .withBorder(2)
                    .width(75)
                    .height(75)
                    .bold()
                    .endConfig()
                    .buildRound(holder.dto.getPlayerPostionNumber(), Color.WHITE);
            holder.ivPlayerPosition.setImageDrawable(drawable);
            holder.tvPlayerName.setText(holder.dto.getPlayerName());
            holder.ivCardType.setImageDrawable(getDrwableResource(holder.dto.getCardType()));
            holder.ivBallPass.setImageDrawable(getDrwableResource(holder.dto.getGoal()));
            holder.ivEnterExit.setImageDrawable(getDrwableResource(holder.dto.getEnterExitImage()));
        }catch (Exception e){e.printStackTrace();}
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        private ImageView ivPlayerPosition;
        private TextView tvPlayerName;
        private ImageView ivCardType;
        private ImageView ivBallPass;
        private ImageView ivEnterExit;
        public CompleteFootballLineUpDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ivPlayerPosition =(ImageView)view.findViewById(R.id.tv_player_number_or_replace);;
            tvPlayerName = (TextView)view.findViewById(R.id.tv_player_name);
            ivCardType =(ImageView)view.findViewById(R.id.iv_card_type);
            ivBallPass = (ImageView)view.findViewById(R.id.iv_ball_pass);
            ivEnterExit = (ImageView) view.findViewById(R.id.iv_enter_exit);
       }
    }
    private Drawable getDrwableResource(String event) {
        Resources.Theme theme = context.getTheme();
        int drwableId = R.drawable.ic_red_green_arrow;
        if("yellowcards".equalsIgnoreCase(event)){
            drwableId = R.drawable.ic_yellow_card;
        }else if("goals".equalsIgnoreCase(event)){
            drwableId = R.drawable.ic_football;
        }
        else if("redcards".equalsIgnoreCase(event)){
            drwableId = R.drawable.ic_red_card;
        }
        Drawable drawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(drwableId,theme);
        } else {
            drawable = context.getResources().getDrawable(drwableId);
        }
        return drawable;
    }


    private TextDrawable getTextDrawable(String value,int textColor,int color) {

        int radius = context.getResources().getDimensionPixelSize(R.dimen.recent_ball_radius);
        int border = context.getResources().getDimensionPixelSize(R.dimen.user_image_border);
        return TextDrawable.builder()
                .beginConfig()
                .textColor(textColor)
                .withBorder(border)
                .width(radius)
                .height(radius)
                .bold()
                .endConfig()
                .buildRound(value, color);
    }


}
