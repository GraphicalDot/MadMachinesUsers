package com.sports.unity.scores.controller.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.scoredetails.CommentriesModel;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.football.MatchCommentaryJsonCaller;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by madmachines on 6/1/16.
 */
public class BroadcastListAdapter extends RecyclerView.Adapter<BroadcastListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommentriesModel> list;

    private String sportsType = null;
   // private MatchCommentaryJsonCaller jsonCaller = new MatchCommentaryJsonCaller();

    public BroadcastListAdapter(String sportsType, ArrayList<CommentriesModel> list, Context activity) {
        this.sportsType = sportsType;
        this.list = list;
        this.context = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView broadcast;
        private TextView commentTime;

        private View view;

        public ViewHolder(View v) {
            super(v);

            view = v;

            broadcast = (TextView) v.findViewById(R.id.broadcast);
            commentTime = (TextView) v.findViewById(R.id.comment_time);

            commentTime.setTypeface(FontTypeface.getInstance(view.getContext()).getRobotoCondensedBold());
        }
    }

    @Override
    public BroadcastListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_broadcast_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(BroadcastListAdapter.ViewHolder holder, int position) {
       //jsonCaller.setJsonObject(jsonObject);

        try {
            if(list != null ) {
                CommentriesModel jsonObject = list.get(position);
                if(jsonObject.getComment() != null) {
                    holder.broadcast.setText(Html.fromHtml(jsonObject.getComment()));
                }
                if (sportsType.equals(ScoresJsonParser.CRICKET)) {
                    holder.commentTime.setText(jsonObject.getOver());
                } else if (sportsType.equals(ScoresJsonParser.FOOTBALL)) {
                    holder.commentTime.setText(Html.fromHtml(jsonObject.getMinute()));
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
