package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.scores.model.football.MatchCommentaryJsonCaller;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 6/1/16.
 */
public class BroadcastListAdapter extends RecyclerView.Adapter<BroadcastListAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<JSONObject> list;

    private MatchCommentaryJsonCaller jsonCaller = new MatchCommentaryJsonCaller();

    public BroadcastListAdapter(ArrayList<JSONObject> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView broadcast;

        private View view;

        public ViewHolder(View v) {
            super(v);

            view = v;

            broadcast = (TextView) v.findViewById(R.id.broadcast);
        }
    }

    @Override
    public BroadcastListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_broadcast_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BroadcastListAdapter.ViewHolder holder, int position) {

        jsonCaller.setJsonObject(list.get(position));

        try {
            holder.broadcast.setText(jsonCaller.getComment());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return list.size();
    }



}
