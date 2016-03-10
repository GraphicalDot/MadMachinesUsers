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
import com.sports.unity.common.model.FontTypeface;

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
            int color = context.getResources().getColor(R.color.app_theme_blue);
            Drawable drawable = null;
            holder.dto = mValues.get(position);
            drawable = getTextDrawable(holder.dto.getPlayerPostionNumber(),Color.WHITE,color);
            holder.ivPlayerPosition.setImageDrawable(drawable);
            holder.tvPlayerName.setText(holder.dto.getPlayerName());
            if(holder.dto.getCardType()!=null) {
                holder.ivCardType.setImageDrawable(getDrwableResource(holder.dto.getCardType()));
            }else {
                holder.ivCardType.setVisibility(View.GONE);
            }
            if(holder.dto.getGoal()!=null) {
                holder.ivBallPass.setImageDrawable(getDrwableResource(holder.dto.getGoal()));
            }else {
                holder.ivBallPass.setVisibility(View.GONE);
            }
            if(holder.dto.getOffPlayerName()!= null) {
                holder.tvOffPlayerName.setText(holder.dto.getOffPlayerName());
                holder.ivEnterExit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_red));
                holder.ivOffCardType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_green));
                holder.ivOffPlayerPosition.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_substitue));
            } else {
                holder.tvOffPlayerName.setVisibility(View.GONE);
                holder.ivEnterExit.setVisibility(View.GONE);
                holder.ivOffCardType.setVisibility(View.GONE);
                holder.ivOffPlayerPosition.setVisibility(View.GONE);
                holder.prOffLeftPercentRelativeLayout.setVisibility(View.INVISIBLE);
            }

            // right side player postion
            drawable = getTextDrawable(holder.dto.getPlayerPostionNumberSecond(),Color.WHITE,color);
            holder.ivPlayerPositionSecond.setImageDrawable(drawable);

            if(holder.dto.getPlayerNameSecond()!= null && !holder.dto.getPlayerNameSecond().equals("")) {
                holder.tvPlayerNameSecond.setText(holder.dto.getPlayerNameSecond());
            }else {
                holder.tvPlayerNameSecond.setVisibility(View.GONE);
            }
            if(holder.dto.getCardTypeSecond()!= null && !holder.dto.getCardTypeSecond().equals("")) {
                holder.ivCardTypeSecond.setImageDrawable(getDrwableResource(holder.dto.getCardTypeSecond()));
            }else{
                holder.ivCardTypeSecond.setVisibility(View.GONE);
            }

            if(holder.dto.getGoalSecond()!= null && !holder.dto.getGoalSecond().equals("")) {
                holder.ivBallPassSecond.setImageDrawable(getDrwableResource(holder.dto.getGoalSecond()));
            }else{
                holder.ivBallPassSecond.setVisibility(View.GONE);
            }
            if(holder.dto.getOffPlayerNameSecond()!= null) {
                holder.tvOffPlayerNameSecond.setText(holder.dto.getOffPlayerNameSecond());
                holder.ivEnterExitSecond.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_red));
                holder.ivOffCardTypeSecond.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_green));
                holder.ivOffPlayerPositionSecond.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_substitue));
            }else {
                holder.tvOffPlayerNameSecond.setVisibility(View.GONE);
                holder.ivEnterExitSecond.setVisibility(View.GONE);
                holder.ivOffCardTypeSecond.setVisibility(View.GONE);
                holder.ivOffPlayerPositionSecond.setVisibility(View.GONE);
                holder.proffRightPercentRelativeLayout.setVisibility(View.INVISIBLE);
            }
            /*if(holder.dto.getOffPlayerName()!=null) {
                drawable = context.getResources().getDrawable(R.drawable.ic_substitue);
                holder.ivOffPlayerPosition.setImageDrawable(drawable);
                holder.tvOffPlayerName.setText(holder.dto.getOffPlayerName());

                if (holder.dto.getOffCardType() != null && !holder.dto.getOffCardType().equals("")) {
                    holder.ivOffCardType.setImageDrawable(getDrwableResource(holder.dto.getOffCardType()));
                } else {
                    holder.ivOffCardType.setVisibility(View.GONE);
                }
                if (holder.dto.getOffEnterExitImage() != null) {


                    if(holder.dto.getOffEnterExitImage().equalsIgnoreCase("OFF")){
                        holder.ivOffEnterExit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_red));
                    }else {
                        holder.ivOffEnterExit.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_green));
                        holder.ivOffPlayerPositionSecond.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_substitue));
                    }
                  } else {
                    holder.ivOffEnterExit.setVisibility(View.GONE);
                }
            }{
                holder.ivOffPlayerPosition.setVisibility(View.GONE);
                holder.tvOffPlayerName.setVisibility(View.GONE);
                holder.ivOffCardType.setVisibility(View.GONE);
                holder.ivOffEnterExit.setVisibility(View.GONE);
            }*/

            /*if(holder.dto.getOffPlayerNameSecond()!=null){
                drawable = context.getResources().getDrawable(R.drawable.ic_substitue);
                holder.ivOffPlayerPositionSecond.setImageDrawable(drawable);
                holder.tvOffPlayerNameSecond.setText(holder.dto.getOffPlayerNameSecond());
                if (holder.dto.getOffCardTypeSecond() != null && !holder.dto.getOffCardTypeSecond().equals("")) {
                    holder.ivOffCardTypeSecond.setImageDrawable(getDrwableResource(holder.dto.getOffCardTypeSecond()));
                } else {
                    holder.ivOffCardTypeSecond.setVisibility(View.GONE);
                }
                if (holder.dto.getOffEnterExitImageSecond() != null) {

                    if(holder.dto.getOffEnterExitImageSecond().equalsIgnoreCase("OFF")){
                        holder.ivOffEnterExitSecond.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_red));
                    }else {
                        holder.ivOffEnterExitSecond.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_green));
                        holder.ivOffPlayerPositionSecond.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_substitue));
                    }

                } else {
                    holder.ivOffEnterExitSecond.setVisibility(View.GONE);
                }
            }else {
                holder.ivOffPlayerPositionSecond.setVisibility(View.GONE);
                holder.tvOffPlayerNameSecond.setVisibility(View.GONE);
                holder.ivOffCardTypeSecond.setVisibility(View.GONE);
                holder.ivOffEnterExitSecond.setVisibility(View.GONE);
            }*/

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

        private ImageView ivPlayerPositionSecond;
        private TextView tvPlayerNameSecond;
        private ImageView ivCardTypeSecond;
        private ImageView ivBallPassSecond;
        private ImageView ivEnterExitSecond;




        private ImageView ivOffPlayerPosition;
        private TextView tvOffPlayerName;
        private ImageView ivOffCardType;
        private ImageView ivOffEnterExit;

        private ImageView ivOffPlayerPositionSecond;
        private TextView tvOffPlayerNameSecond;
        private ImageView ivOffCardTypeSecond;

        private ImageView ivOffEnterExitSecond;

        private View prOffLeftPercentRelativeLayout;
        private View proffRightPercentRelativeLayout;


        public CompleteFootballLineUpDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
          ivPlayerPosition =(ImageView)view.findViewById(R.id.tv_player_number_or_replace);;
            tvPlayerName = (TextView)view.findViewById(R.id.tv_player_name);
            ivCardType =(ImageView)view.findViewById(R.id.iv_card_type);
            ivBallPass = (ImageView)view.findViewById(R.id.iv_ball_pass);
            ivEnterExit = (ImageView) view.findViewById(R.id.iv_enter_exit);
            ivPlayerPositionSecond =(ImageView)view.findViewById(R.id.tv_player_number_or_replace_second);;
            tvPlayerNameSecond = (TextView)view.findViewById(R.id.tv_player_name_second);
            ivCardTypeSecond =(ImageView)view.findViewById(R.id.iv_card_type_second);
            ivBallPassSecond = (ImageView)view.findViewById(R.id.iv_ball_pass_second);
            ivEnterExitSecond = (ImageView) view.findViewById(R.id.iv_enter_exit_second);
            //offplayer
            ivOffPlayerPosition =(ImageView)view.findViewById(R.id.tv_off_player_number_or_replace);;
            tvOffPlayerName = (TextView)view.findViewById(R.id.tv_off_player_name);
            ivOffCardType =(ImageView)view.findViewById(R.id.iv_off_card_type);

            ivOffEnterExit = (ImageView) view.findViewById(R.id.iv_off_enter_exit);
            ivOffPlayerPositionSecond =(ImageView)view.findViewById(R.id.tv_off_player_number_or_replace_second);;
            tvOffPlayerNameSecond = (TextView)view.findViewById(R.id.tv_off_player_name_second);
            ivOffCardTypeSecond =(ImageView)view.findViewById(R.id.iv_off_card_type_second);

            ivOffEnterExitSecond = (ImageView) view.findViewById(R.id.iv_off_enter_exit_second);
            prOffLeftPercentRelativeLayout = view.findViewById(R.id.ll_off_lineup_team_left);
            proffRightPercentRelativeLayout = view.findViewById(R.id.ll_lineup_team_right_off);




        }
    }
    private Drawable getDrwableResource(String event) {
        Resources.Theme theme = context.getTheme();
        int drwableId = 0;
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


    private Drawable getTextDrawable(String value,int textColor,int color) {

        if (value != null) {
            int radius = context.getResources().getDimensionPixelSize(R.dimen.recent_ball_radius);
            int border = context.getResources().getDimensionPixelSize(R.dimen.user_image_border);
            return TextDrawable.builder()
                    .beginConfig()
                    .textColor(textColor)
                    .withBorder(border)
                    .width(radius)
                    .height(radius).useFont(FontTypeface.getInstance(context).getRobotoCondensedRegular())
                    .endConfig()
                    .buildRound(value, color);
        }
        return  null;
    }


}
