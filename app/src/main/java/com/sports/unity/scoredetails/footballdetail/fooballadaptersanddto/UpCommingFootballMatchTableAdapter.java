package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBattingCardDTO;

import java.util.List;

/**
 * Created by madmachines on 25/2/16.
 */
public class UpCommingFootballMatchTableAdapter   extends RecyclerView.Adapter<UpCommingFootballMatchTableAdapter.ViewHolder> {

    private final List<UpCommngFootbalMatchTableDTO> mValues;
    private Context context;

    public UpCommingFootballMatchTableAdapter(List<UpCommngFootbalMatchTableDTO> mValues,Context context) {
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_football_match_table_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dto = mValues.get(position);
        holder.tvSerialNumber.setText(holder.dto.getTvSerialNumber());
        Glide.with(context).load(holder.dto.getIvTeamProfileImage()).placeholder(R.drawable.ic_no_img).into(holder.ivTeamProfileImage);
        holder.tvTeamName.setText(holder.dto.getTvTeamName());
        holder.tvP.setText(holder.dto.getTvP());
        holder.tvW.setText(holder.dto.getTvW());
        holder.tvD.setText(holder.dto.getTvD());
        holder.tvL.setText(holder.dto.getTvL());
        holder.tvPts.setText(holder.dto.getTvPts());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;
        private TextView tvSerialNumber;
        private ImageView ivTeamProfileImage;
        private TextView tvTeamName;
        private TextView tvP;
        private TextView tvW;
        private TextView tvD;
        private TextView tvL;
        private TextView tvPts;

        public UpCommngFootbalMatchTableDTO dto;

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

        }
    }
}