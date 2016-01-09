package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.football.CricketMatchJsonCaller;
import com.sports.unity.scores.model.football.FootballMatchJsonCaller;
import com.sports.unity.scores.model.football.MatchCommentaryJsonCaller;
import com.sports.unity.scores.model.football.MatchJsonCaller;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 6/1/16.
 */
public class BroadcastListAdapter extends RecyclerView.Adapter<BroadcastListAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<JSONObject> list;

    private String sportsType = null;
    private MatchCommentaryJsonCaller jsonCaller = new MatchCommentaryJsonCaller();

    public BroadcastListAdapter(String sportsType, ArrayList<JSONObject> list, Activity activity) {
        this.sportsType = sportsType;
        this.list = list;
        this.activity = activity;
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

        JSONObject jsonObject = list.get(position);

        jsonCaller.setJsonObject(jsonObject);

        try {
            holder.broadcast.setText(Html.fromHtml(jsonCaller.getComment()));

            if( sportsType.equals(ScoresJsonParser.CRICKET) ){
                holder.commentTime.setText(jsonCaller.getOvers());
            } else if( sportsType.equals(ScoresJsonParser.FOOTBALL) ){
                holder.commentTime.setText( Html.fromHtml(jsonCaller.getMinute() + "<sup>'</sup>"));
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
