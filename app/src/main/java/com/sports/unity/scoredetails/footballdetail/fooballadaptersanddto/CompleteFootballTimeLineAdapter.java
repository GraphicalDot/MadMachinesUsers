package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
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
            // context.getResources().getDimensionPixelSize(R.dimen.);

            TextDrawable drawable = null;
            holder.dto = mValues.get(position);
            holder.setIsRecyclable(false);
            if(position==0){
                holder.cardView.setVisibility(View.GONE);
                holder.tvTimeInterval.setVisibility(View.VISIBLE);
                holder.upperDotView.setVisibility(View.VISIBLE);
                if("FT".equalsIgnoreCase(holder.dto.getMatchStatus())){
                    holder.tvTimeInterval.setText(R.string.full_time);
                }else if(holder.dto.getMatchStatus().equalsIgnoreCase("HT")){
                    holder.tvTimeInterval.setText(R.string.half_time);
                } else {
                    holder.tvTimeInterval.setText(R.string.on_going);
                }

            }else {
                holder.tvTimeInterval.setVisibility(View.GONE);
                holder.upperDotView.setVisibility(View.GONE);
                holder.upperDotView.setVisibility(View.INVISIBLE);
            }

            if(getItemCount()-1== position){
                holder.gameStartImage.setVisibility(View.VISIBLE);
                holder.gameStartImage.setImageResource(R.drawable.ic_match_start_circle);
            }else {
                holder.gameStartImage.setVisibility(View.GONE);
            }

            if(holder.dto.getTeamName().equalsIgnoreCase(context.getString(R.string.home_team_name))) {

                setLocalTeamTimeLine(holder);
            }else if(holder.dto.getTeamName().equalsIgnoreCase(context.getString(R.string.away_team_name)))
            {
                setVisitorTeamTimeLine(holder);
            }
            holder.centralCircularImage.setImageDrawable(holder.dto.getDrwDrawable());

        }catch (Exception e){e.printStackTrace();}
    }

    private void setVisitorTeamTimeLine(ViewHolder holder) {
        int padding = context.getResources().getDimensionPixelOffset(R.dimen.horizontal_padding_very_very_small);
        float density = context.getResources().getDisplayMetrics().density;
        int paddingDp = (int)(padding * density);
        holder.tvTeamSecondTime.setText(holder.dto.getTvTeamSecondTime());
        holder.tvTeamSecondOnPlayer.setText(holder.dto.getTvTeamSecondOnPlayer());
        if(holder.dto.getTvTeamSecondOffPlayer()!=null){
            holder.tvTeamSecondOffPlayer.setText(holder.dto.getTvTeamSecondOffPlayer());
            holder.tvTeamSecondOffPlayer.setVisibility(View.VISIBLE);
        }else{
            holder.tvTeamSecondOffPlayer.setVisibility(View.GONE);
            holder.tvTeamSecondOnPlayer.setGravity(Gravity.CENTER);
            holder.tvTeamSecondOnPlayer.setPadding(0,paddingDp,0,0);

            /*android:layout_gravity="center"
            android:paddingTop="@dimen/horizontal_padding_very_small"*/


        }

        holder.tvTeamSecondTime.setVisibility(View.VISIBLE);
        holder.tvTeamSecondOnPlayer.setVisibility(View.VISIBLE);
       /* holder.tvTeamSecondOffPlayer.setVisibility(View.VISIBLE);*/
        holder.teamSecondView.setVisibility(View.VISIBLE);
        holder.teamFirstView.setVisibility(View.INVISIBLE);
        holder.tvTeamFirstTime.setVisibility(View.INVISIBLE);

       /* holder.teamFirstView.setVisibility(View.INVISIBLE);
        holder.tvTeamSecondTime.setVisibility(View.VISIBLE);
        holder.teamSecondView.setVisibility(View.VISIBLE);


        holder.tvTeamFirstTime.setVisibility(View.INVISIBLE);
        holder.tvTeamFirstOnPlayer.setVisibility(View.INVISIBLE);*/

    }

    private void setLocalTeamTimeLine(ViewHolder holder) {
        int padding = context.getResources().getDimensionPixelOffset(R.dimen.horizontal_padding_very_very_small);
        float density = context.getResources().getDisplayMetrics().density;
        int paddingDp = (int)(padding * density);
        holder.tvTeamFirstTime.setText(holder.dto.getTvTeamFirstTime());
        holder.tvTeamFirstOnPlayer.setText(holder.dto.getTvTeamFirstOnPlayer());
        if(holder.dto.getTvTeamFirstOffPlayer()!=null){
            holder.tvTeamFirstOffPlayer.setText(holder.dto.getTvTeamFirstOffPlayer());
            holder.tvTeamFirstOffPlayer.setVisibility(View.VISIBLE);
        }else {
            holder.tvTeamFirstOffPlayer.setVisibility(View.GONE);
            holder.tvTeamFirstOnPlayer.setGravity(Gravity.CENTER);
            holder.tvTeamFirstOnPlayer.setGravity(Gravity.CENTER);
            holder.tvTeamFirstOnPlayer.setPadding(0, paddingDp, 0, 0);

        }
        holder.tvTeamFirstTime.setVisibility(View.VISIBLE);
        holder.tvTeamFirstOnPlayer.setVisibility(View.VISIBLE);
        /*holder.tvTeamFirstOffPlayer.setVisibility(View.VISIBLE);*/
        holder.teamFirstView.setVisibility(View.VISIBLE);
        holder.tvTeamSecondTime.setVisibility(View.INVISIBLE);
        holder.teamSecondView.setVisibility(View.INVISIBLE);

        /*holder.teamSecondView.setVisibility(View.INVISIBLE);
        holder.teamFirstView.setVisibility(View.VISIBLE);
        holder.tvTeamFirstTime.setVisibility(View.VISIBLE);

        holder.tvTeamSecondTime.setVisibility(View.INVISIBLE);
        holder.tvTeamSecondOnPlayer.setVisibility(View.INVISIBLE);*/


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;

        private TextView tvTeamFirstTime;
        private TextView tvTeamSecondTime;
        private TextView tvTeamFirstOnPlayer;
        private TextView tvTeamSecondOnPlayer;
        private TextView tvTeamFirstOffPlayer;
        private TextView tvTeamSecondOffPlayer;
        private ImageView keyImage;
        private View teamFirstView;
        private View teamSecondView;
        private View centralLineImage;
        private ImageView centralCircularImage;
        private ImageView gameStartImage;
        private TextView tvTimeInterval;
        private View upperDotView;
        public CompleteFootballTimeLineDTO dto;
        private View cardView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvTeamFirstTime = (TextView) view.findViewById(R.id.tv_team_first_time);
            tvTeamSecondTime = (TextView) view.findViewById(R.id.tv_team_second_time);
            tvTeamFirstOnPlayer = (TextView) view.findViewById(R.id.tv_team_first_on);
            tvTeamSecondOnPlayer = (TextView) view.findViewById(R.id.tv_team_second_on);
            tvTeamFirstOffPlayer = (TextView) view.findViewById(R.id.tv_team_first_off);
            tvTeamSecondOffPlayer = (TextView) view.findViewById(R.id.tv_team_second_off);
            keyImage = (ImageView) view.findViewById(R.id.iv_time_line_image);
            teamFirstView = view.findViewById(R.id.ll_first_player_info);
            teamSecondView = view.findViewById(R.id.ll_second_player_info);
            centralLineImage = view.findViewById(R.id.ll_imv_center);
            centralCircularImage = (ImageView) view.findViewById(R.id.iv_time_line_image);
            gameStartImage = (ImageView) view.findViewById(R.id.iv_centre_image);
            tvTimeInterval = (TextView) view.findViewById(R.id.tv_time_interval);
            upperDotView = view.findViewById(R.id.iv_dot_upper);
            cardView = view.findViewById(R.id.card_view);
        }
    }
}
