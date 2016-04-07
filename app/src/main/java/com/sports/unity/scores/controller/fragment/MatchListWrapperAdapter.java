package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.MatchDay;
import com.sports.unity.util.Constants;

import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by madmachines on 3/3/16.
 */
public class MatchListWrapperAdapter extends RecyclerView.Adapter<MatchListWrapperAdapter.ViewHolder> {

    private List<MatchListWrapperDTO> matchDay;
    private Activity activity;
    private Context context;
    private MatchListWrapperNotify matchListWrapperNotify;

    public void setIsIndividualFixture() {
        this.isIndividualFixture = true;
    }

    private boolean isIndividualFixture = false;

    public MatchListWrapperAdapter(List<MatchListWrapperDTO> matchDay, Activity activity, Context context,MatchListWrapperNotify matchListWrapperNotify) {
        this.matchDay = matchDay;
        this.activity = activity;
        this.context = context;
        this.matchListWrapperNotify = matchListWrapperNotify;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_score_wrapper, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            MatchListWrapperDTO previousDTO = null;
            if (position == 0) {
                previousDTO = matchDay.get(position);
            } else {
                previousDTO = matchDay.get(position - 1);
            }

            MatchListWrapperDTO dto = matchDay.get(position);
            List<JSONObject> list = holder.mAdapter.getList();
            list.clear();
            list.addAll(dto.getList());
            if (previousDTO.getDay().equalsIgnoreCase(dto.getDay()) && position != 0) {
                holder.tvDayName.setVisibility(View.GONE);
            } else {
                holder.tvDayName.setVisibility(View.VISIBLE);
                holder.tvDayName.setText(dto.getDay());
            }

            if (!isIndividualFixture) {
                holder.tvLeagueName.setText(dto.getLeagueName());
            } else {
                holder.leagueLayout.setVisibility(View.GONE);
                holder.sepTop.setVisibility(View.GONE);
                holder.sepBottom.setVisibility(View.GONE);
            }
            if (dto.getSportsType().equals(Constants.SPORTS_TYPE_CRICKET)) {
                holder.ivSportsIcon.setImageResource(R.drawable.ic_cricket_group);
            } else {
                holder.ivSportsIcon.setImageResource(R.drawable.ic_football_group);
            }
            holder.mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return matchDay.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView tvDayName;
        private TextView tvLeagueName;
        private RecyclerView rvChild;
        private MatchListAdapter mAdapter;
        private ImageView ivSportsIcon;
        private RelativeLayout leagueLayout;
        private View sepTop;
        private View sepBottom;
        public MatchDay dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvDayName = (TextView) view.findViewById(R.id.id_day_name);
            leagueLayout = (RelativeLayout) view.findViewById(R.id.league_layout);
            tvLeagueName = (TextView) view.findViewById(R.id.league_name);
            ivSportsIcon = (ImageView) view.findViewById(R.id.iv_league);
            rvChild = (RecyclerView) view.findViewById(R.id.child_rv);
            mAdapter = new MatchListAdapter(new ArrayList<JSONObject>(), activity,matchListWrapperNotify);
            rvChild.setLayoutManager(new LinearLayoutManager(context, VERTICAL, false));
            rvChild.setNestedScrollingEnabled(false);
            rvChild.setAdapter(mAdapter);
            sepTop = view.findViewById(R.id.sep_top);
            sepBottom = view.findViewById(R.id.sep_bottom);
        }
    }

}
