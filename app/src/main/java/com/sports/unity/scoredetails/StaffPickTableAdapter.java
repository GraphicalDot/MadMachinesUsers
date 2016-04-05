package com.sports.unity.scoredetails;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.controller.TeamLeagueDetails;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommingFootballMatchTableAdapter;
import com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto.UpCommngFootbalMatchTableDTO;
import com.sports.unity.util.Constants;

import java.util.List;

/**
 * Created by madmachines on 25/2/16.
 */
public class StaffPickTableAdapter extends RecyclerView.Adapter<StaffPickTableAdapter.ViewHolder> {

    private final List<StaffPickTableDTO> mValues;
    private Context context;


    public StaffPickTableAdapter(Context context, List<StaffPickTableDTO> mValues) {
        this.mValues = mValues;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_pick_table_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            holder.dto = mValues.get(position);
            holder.tvSerialNumber.setText(String.valueOf(position + 1));
            holder.tvTeamName.setText(holder.dto.getTvTeamName());
            holder.tvP.setText(holder.dto.getTvP());
            holder.tvW.setText(holder.dto.getTvW());
            holder.tvD.setText(holder.dto.getTvD());
            holder.tvL.setText(holder.dto.getTvL());
            holder.tvPts.setText(holder.dto.getTvPts());
            holder.tvNrr.setText(holder.dto.getTvNRR());
            holder.tvSerialNumber.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvTeamName.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvP.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvW.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvD.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvL.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvPts.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvNrr.setTextColor(context.getResources().getColor(R.color.news_static));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FavouriteItem f = new FavouriteItem();
                    f.setName(holder.dto.getTvTeamName());
                    f.setId(holder.dto.getTeamId());
                    f.setSportsType(Constants.SPORTS_TYPE_CRICKET);
                    f.setFilterType(Constants.FILTER_TYPE_TEAM);
                    Intent intent = new Intent(context, TeamLeagueDetails.class);
                    intent.putExtra(Constants.INTENT_TEAM_LEAGUE_DETAIL_EXTRA, f.getJsonObject().toString());
                    context.startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public StaffPickTableDTO dto;
        private TextView tvSerialNumber;
        private TextView tvTeamName;
        private TextView tvP;
        private TextView tvW;
        private TextView tvD;
        private TextView tvL;
        private TextView tvPts;
        private TextView tvNrr;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvSerialNumber = (TextView) view.findViewById(R.id.tv_serial_number);
            tvTeamName = (TextView) view.findViewById(R.id.tv_team_name);
            tvP = (TextView) view.findViewById(R.id.tv_p);
            tvW = (TextView) view.findViewById(R.id.tv_w);
            tvD = (TextView) view.findViewById(R.id.tv_d);
            tvL = (TextView) view.findViewById(R.id.tv_l);
            tvPts = (TextView) view.findViewById(R.id.tv_pts);
            tvNrr = (TextView) view.findViewById(R.id.tv_nrr);


        }
    }
}
