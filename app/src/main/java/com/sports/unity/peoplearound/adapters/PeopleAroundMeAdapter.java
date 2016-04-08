package com.sports.unity.peoplearound.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.peoplearound.dto.PeopleAroundMeDTO;
import com.sports.unity.playerprofile.cricket.PlayerCricketBioDataActivity;
import com.sports.unity.scoredetails.cricketdetail.completedmatchscorecardadapters.LiveAndCompletedCricketBattingCardDTO;
import com.sports.unity.util.Constants;

import java.util.List;

/**
 * Created by madmachines on 8/4/16.
 */
public class PeopleAroundMeAdapter extends RecyclerView.Adapter<PeopleAroundMeAdapter.ViewHolder> {

    private final List<PeopleAroundMeDTO> mValues;
    private Context context;

    public PeopleAroundMeAdapter(List<PeopleAroundMeDTO> mValues,Context context) {
        this.mValues = mValues;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_people_aroundme_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dto = mValues.get(position);



    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mView;

        public PeopleAroundMeDTO dto;

        public ViewHolder(View view) {
            super(view);
            mView = view;

        }
    }
}
