package com.sports.unity.peoplearound.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.messages.controller.model.Person;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 8/4/16.
 */
public class PeopleAroundMeAdapter extends RecyclerView.Adapter<PeopleAroundMeAdapter.ViewHolder> {

    private ArrayList<Person> people;
    private Context context;

    public PeopleAroundMeAdapter(ArrayList<Person> people,Context context) {
        this.people = people;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_people_aroundme_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.dto = people.get(position);



    }

    @Override
    public int getItemCount() {
        return people.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView tvfriendname;
        public TextView tvfrienddistance;
        public ImageView ivfriendimg;

        public final View mView;

        public Person dto;

        public ViewHolder(View view) {
            super(view);
            tvfriendname=(TextView) view.findViewById(R.id.tv_friend_name);
            tvfrienddistance=(TextView) view.findViewById(R.id.tv_friend_distance);
            ivfriendimg=(ImageView) view.findViewById(R.id.iv_friend_img);
            mView = view;

        }
    }
}
