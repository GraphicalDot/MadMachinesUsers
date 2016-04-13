package com.sports.unity.scoredetails.footballdetail.fooballadaptersanddto;

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
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by madmachines on 25/2/16.
 */
public class UpCommingFootballMatchTableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String team1;
    private String team2;

    private ArrayList<String> groupsListInOrder = new ArrayList<>();
    private HashMap<String, ArrayList<UpCommngFootbalMatchTableDTO>> groupStandingsMap;
    private int size = 0;

    public UpCommingFootballMatchTableAdapter(Context context, String team1, String team2, ArrayList<String> groupsListInOrder, HashMap<String, ArrayList<UpCommngFootbalMatchTableDTO>> groupStandingsMap) {
        this.context = context;
        this.team1 = team1;
        this.team2 = team2;

        this.groupsListInOrder = groupsListInOrder;
        this.groupStandingsMap = groupStandingsMap;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == 1) {
            view = inflater.inflate(R.layout.football_league_table_item, parent, false);
        } else {
            view = inflater.inflate(R.layout.upcoming_football_match_table_card, parent, false);
        }
//        View singeTableView = inflater.inflate(R.layout.upcoming_football_match_table_card, parent, false);
        RecyclerView.ViewHolder viewHolder = new SingleTableViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SingleTableViewHolder vh2 = (SingleTableViewHolder) holder;
        displaySingleTableData(vh2, position);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void initContent() {
        int groupCount = groupsListInOrder.size();
        if (groupCount == 1) {
            groupCount = 0;
        } else {

        }

        int totalItemCount = 0;
        for (String key : groupsListInOrder) {
            totalItemCount += groupStandingsMap.get(key).size();
        }
        size = totalItemCount;

        this.notifyDataSetChanged();
    }

    private UpCommngFootbalMatchTableDTO getItemFromHashMap(int position) {
        UpCommngFootbalMatchTableDTO dto = null;

        for (String key : groupsListInOrder) {
            ArrayList<UpCommngFootbalMatchTableDTO> arrayList = groupStandingsMap.get(key);
            if (position <= arrayList.size() - 1) {
                dto = arrayList.get(position);
                break;
            } else {
                position -= arrayList.size();
            }
        }

        return dto;
    }

    @Override
    public int getItemViewType(int position) {
        return getItemFromHashMap(position).getViewType();
    }

    private void displaySingleTableData(SingleTableViewHolder holder, int position) {
        UpCommngFootbalMatchTableDTO dto = getItemFromHashMap(position);


        if (team1.equalsIgnoreCase(dto.getTvTeamName()) || team2.equalsIgnoreCase(dto.getTvTeamName())) {
            holder.tvSerialNumber.setText(dto.getRank());
            holder.tvTeamName.setText(dto.getTvTeamName());
            holder.tvP.setText(dto.getTvP());
            holder.tvW.setText(dto.getTvW());
            holder.tvD.setText(dto.getTvD());
            holder.tvL.setText(dto.getTvL());
            holder.tvPts.setText(dto.getTvPts());
            holder.tvSerialNumber.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
            holder.tvTeamName.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
            holder.tvP.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
            holder.tvW.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
            holder.tvD.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
            holder.tvL.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
            holder.tvPts.setTextColor(context.getResources().getColor(R.color.app_theme_blue));
            holder.llLiveTeam.setVisibility(View.VISIBLE);
            Glide.with(context).load(dto.getIvTeamProfileImage()).placeholder(R.drawable.ic_no_img).into(holder.ivTeamProfileImage);

        } else {
            holder.tvSerialNumber.setText(dto.getRank());
            holder.tvTeamName.setText(dto.getTvTeamName());
            holder.tvP.setText(dto.getTvP());
            holder.tvW.setText(dto.getTvW());
            holder.tvD.setText(dto.getTvD());
            holder.tvL.setText(dto.getTvL());
            holder.tvPts.setText(dto.getTvPts());
            holder.tvSerialNumber.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvTeamName.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvP.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvW.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvD.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvL.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.tvPts.setTextColor(context.getResources().getColor(R.color.news_static));
            holder.llLiveTeam.setVisibility(View.INVISIBLE);
            Glide.with(context).load(dto.getIvTeamProfileImage()).placeholder(R.drawable.ic_no_img).into(holder.ivTeamProfileImage);

        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FavouriteItem f = new FavouriteItem();
//                f.setName(dto.getTvTeamName());
//                f.setId(dto.getTeamId());
//                f.setSportsType(Constants.SPORTS_TYPE_FOOTBALL);
//                f.setFilterType(Constants.FILTER_TYPE_TEAM);
//                Intent intent = new Intent(context, TeamLeagueDetails.class);
//                intent.putExtra(Constants.INTENT_TEAM_LEAGUE_DETAIL_EXTRA, f.getJsonObject().toString());
//                context.startActivity(intent);
            }
        });
    }

    public class SingleTableViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        private TextView tvSerialNumber;
        private ImageView ivTeamProfileImage;
        private TextView tvTeamName;
        private TextView tvP;
        private TextView tvW;
        private TextView tvD;
        private TextView tvL;
        private TextView tvPts;
        private View llLiveTeam;

        public SingleTableViewHolder(View view) {
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

