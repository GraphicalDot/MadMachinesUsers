package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.scores.model.cricket.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 8/10/15.
 */
public class ScoresAdapter extends RecyclerView.Adapter<ScoresAdapter.ViewHolder> {

    private Context context;
    private List<Result> list;
    private Typeface robotoCondensedReg;
    private Typeface robotoMedium;
    private Typeface robotoCondensedBold;


    public ScoresAdapter(ArrayList<Result> list, Context applicationContext, Activity activity) {
        this.list = list;
        this.context = applicationContext;
        this.robotoCondensedReg = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");
        this.robotoMedium = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        this.robotoCondensedBold = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Bold.ttf");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView t1flag;
        public TextView team1;
        public TextView t1score;
        public ImageView t2flag;
        public TextView team2;
        public TextView t2score;
        public TextView odi;
        public TextView venue;
        public TextView date;
        LinearLayout footer;
        LinearLayout rootLayout;
        View seperator;


        public ViewHolder(View v) {
            super(v);

            t1flag = (ImageView) v.findViewById(R.id.t1flag);
            team1 = (TextView) v.findViewById(R.id.team1);
            t1score = (TextView) v.findViewById(R.id.t1score);
            t2flag = (ImageView) v.findViewById(R.id.t2flag);
            team2 = (TextView) v.findViewById(R.id.team2);
            t2score = (TextView) v.findViewById(R.id.t2score);
            odi = (TextView) v.findViewById(R.id.odi);
            venue = (TextView) v.findViewById(R.id.venue);
            date = (TextView) v.findViewById(R.id.date);
            footer = (LinearLayout) v.findViewById(R.id.footer);
            rootLayout = (LinearLayout) v.findViewById(R.id.rootLayout);
            seperator = v.findViewById(R.id.seperator);

        }
    }

    @Override
    public ScoresAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ScoresAdapter.ViewHolder holder, int position) {
        String[] teams = {};
        Result result = list.get(position);
        if ( result.getLive() != null) {
            teams = result.getMatchDesc().split("vs");
            if ("True".equals(result.getLive())) {
                liveMatchColoring(holder);
            } else {
                clearLiveMatchColoring(holder);
            }


        } else {
            String descrption = result.getMatchDesc();
            teams = descrption.substring(0, descrption.indexOf(",")).split("vs");

            clearLiveMatchColoring(holder);
        }
        holder.team1.setText(teams[0]);
        holder.team2.setText(teams[1]);
        holder.t1score.setText(result.getRuns() + "/" + result.getWkts() + "  " + "(" + result.getOvers() + ")");
        holder.odi.setText(result.getMchNum());
        holder.date.setText("Wednesday 7" + Html.fromHtml("<sup>th</sup>") + " August");
        holder.odi.setTypeface(robotoMedium);
        holder.venue.setTypeface(robotoCondensedBold);
        holder.date.setTypeface(robotoCondensedReg);
        holder.team1.setTypeface(robotoCondensedBold);
        holder.team2.setTypeface(robotoCondensedReg);
        holder.t1score.setTypeface(robotoCondensedReg);
    }

    private void liveMatchColoring(ScoresAdapter.ViewHolder holder){
        holder.footer.setBackgroundColor(Color.parseColor("#236aa3"));
        holder.rootLayout.setBackgroundColor(Color.parseColor("#2c84cc"));
        holder.team1.setTextColor(Color.parseColor("#ffffff"));
        holder.team2.setTextColor(Color.parseColor("#ffffff"));
        holder.t1score.setTextColor(Color.parseColor("#ffffff"));
        holder.t2score.setTextColor(Color.parseColor("#ffffff"));
        holder.date.setTextColor(Color.parseColor("#ffffff"));
        holder.venue.setTextColor(Color.parseColor("#ffffff"));
        holder.odi.setTextColor(Color.parseColor("#b7cde5"));
        holder.seperator.setBackgroundColor(Color.parseColor("#236aa3"));
    }

    private void clearLiveMatchColoring(ScoresAdapter.ViewHolder holder){
        holder.footer.setBackgroundColor(Color.parseColor("#e6e6e6"));
        holder.rootLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        holder.team1.setTextColor(Color.parseColor("#000000"));
        holder.team2.setTextColor(Color.parseColor("#000000"));
        holder.t1score.setTextColor(Color.parseColor("#000000"));
        holder.t2score.setTextColor(Color.parseColor("#000000"));
        holder.date.setTextColor(Color.parseColor("#666666"));
        holder.venue.setTextColor(Color.parseColor("#666666"));
        holder.odi.setTextColor(Color.parseColor("#000000"));
        holder.seperator.setBackgroundColor(Color.parseColor("#cbcbcb"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
