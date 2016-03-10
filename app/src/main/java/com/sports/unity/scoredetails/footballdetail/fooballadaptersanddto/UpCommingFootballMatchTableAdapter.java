package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;

import java.util.List;

/**
 * Created by madmachines on 25/2/16.
 */
public class UpCommingFootballMatchTableAdapter   extends RecyclerView.Adapter<UpCommingFootballMatchTableAdapter.ViewHolder> {

    private final List<UpCommngFootbalMatchTableDTO> mValues;
    private Context context;
    private String team1;
    private String team2;


    public UpCommingFootballMatchTableAdapter(List<UpCommngFootbalMatchTableDTO> mValues,Context context,String team1,String team2) {
        this.mValues = mValues;
        this.context = context;
        this.team1 =team1;
        this.team2 = team2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_football_match_table_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try{


        holder.dto = mValues.get(position);

            if(team1.equalsIgnoreCase(holder.dto.getTvTeamName()) || team2.equalsIgnoreCase(holder.dto.getTvTeamName())){
                holder.tvSerialNumber.setText(String.valueOf(position+1));
                holder.tvTeamName.setText(holder.dto.getTvTeamName());
                holder.tvP.setText(holder.dto.getTvP());
                holder.tvW.setText(holder.dto.getTvW());
                holder.tvD.setText(holder.dto.getTvD());
                holder.tvL.setText(holder.dto.getTvL());
                holder.tvPts.setText(holder.dto.getTvPts());
                holder.tvSerialNumber.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
                holder.tvTeamName.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
                holder.tvP.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
                holder.tvW.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
                holder.tvD.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
                holder.tvL.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
                holder.tvPts.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
                holder.llLiveTeam.setVisibility(View.VISIBLE);
                Glide.with(context).load(holder.dto.getIvTeamProfileImage()).placeholder(R.drawable.ic_no_img).into(holder.ivTeamProfileImage);

            }else {
                holder.tvSerialNumber.setText(String.valueOf(position+1));
                holder.tvTeamName.setText(holder.dto.getTvTeamName());
                holder.tvP.setText(holder.dto.getTvP());
                holder.tvW.setText(holder.dto.getTvW());
                holder.tvD.setText(holder.dto.getTvD());
                holder.tvL.setText(holder.dto.getTvL());
                holder.tvPts.setText(holder.dto.getTvPts());
                holder.tvSerialNumber.setTextColor(context.getResources().getColor(R.color.news_static));
                holder.tvTeamName.setTextColor(context.getResources().getColor(R.color.news_static));
                holder.tvP.setTextColor(context.getResources().getColor(R.color.news_static));
                holder.tvW.setTextColor(context.getResources().getColor(R.color.news_static));
                holder.tvD.setTextColor(context.getResources().getColor(R.color.news_static));
                holder.tvL.setTextColor(context.getResources().getColor(R.color.news_static));
                holder.tvPts.setTextColor(context.getResources().getColor(R.color.news_static));
                holder.llLiveTeam.setVisibility(View.INVISIBLE);
                Glide.with(context).load(holder.dto.getIvTeamProfileImage()).placeholder(R.drawable.ic_no_img).into(holder.ivTeamProfileImage);
            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        public UpCommngFootbalMatchTableDTO dto;
        private TextView tvSerialNumber;
        private ImageView ivTeamProfileImage;
        private TextView tvTeamName;
        private TextView tvP;
        private TextView tvW;
        private TextView tvD;
        private TextView tvL;
        private TextView tvPts;
        private View llLiveTeam;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvSerialNumber = (TextView) view.findViewById(R.id.tv_serial_number);
            ivTeamProfileImage = (ImageView) view.findViewById(R.id.iv_team_profile_image);
            tvTeamName = (TextView) view.findViewById(R.id.tv_team_name);
            tvP = (TextView) view.findViewById(R.id.tv_p);
            tvW = (TextView) view.findViewById(R.id.tv_w);
            tvD = (TextView) view.findViewById(R.id.tv_d);
            tvL = (TextView) view.findViewById(R.id.tv_l);
            tvPts = (TextView) view.findViewById(R.id.tv_pts);
            llLiveTeam = view.findViewById(R.id.ll_live_team);

        }
    }
}
