package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.scores.model.football.FootballMatchJsonCaller;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by madmachines on 8/10/15.
 */
public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.ViewHolder> {

    private Activity activity;
    private List<JSONObject> list;

    private FootballMatchJsonCaller footballMatchJsonCaller = new FootballMatchJsonCaller();

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            handleItemClick(view);
        }

    };

    public MatchListAdapter(ArrayList<JSONObject> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView t1flag;
        private TextView team1;
        private TextView t1score;
        private ImageView t2flag;
        private TextView team2;
        private TextView t2score;
        private TextView matchDay;
        private TextView venue;
        private TextView date;

        private View view;

        public ViewHolder(View v) {
            super(v);

            view = v;

            t1flag = (ImageView) v.findViewById(R.id.t1flag);
            team1 = (TextView) v.findViewById(R.id.team1);
            t1score = (TextView) v.findViewById(R.id.t1score);
            t2flag = (ImageView) v.findViewById(R.id.t2flag);
            team2 = (TextView) v.findViewById(R.id.team2);
            t2score = (TextView) v.findViewById(R.id.t2score);
            matchDay = (TextView) v.findViewById(R.id.matchDay);
            venue = (TextView) v.findViewById(R.id.venue);
            date = (TextView) v.findViewById(R.id.date);

        }
    }

    @Override
    public MatchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MatchListAdapter.ViewHolder holder, int position) {
        JSONObject matchJsonObject = list.get(position);

        try {
            footballMatchJsonCaller.setJsonObject(matchJsonObject);

            Date date = new Date(new java.text.SimpleDateFormat("MM/dd/yyyy").format(new java.util.Date(Long.valueOf(footballMatchJsonCaller.getMatchDateEpoch()) * 1000)));
            String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);
            String day = (String) android.text.format.DateFormat.format("dd", date);
            String month = getMonth((String) android.text.format.DateFormat.format("MMM", date));
            String isttime = null;
            try {
                isttime = getLocalTime(footballMatchJsonCaller.getMatchTime()).substring(0, 5);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.team1.setText(footballMatchJsonCaller.getHomeTeam());
            holder.team2.setText(footballMatchJsonCaller.getAwayTeam());

            holder.venue.setText(footballMatchJsonCaller.getStadium());
            holder.date.setText(dayOfTheWeek + ", " + month + " " + day + ", " + isttime + " (IST) ");

            Glide.with(activity).load(footballMatchJsonCaller.getHomeTeamFlag()).into(holder.t1flag);
            Glide.with(activity).load(footballMatchJsonCaller.getAwayTeamFlag()).into(holder.t2flag);

            if ("?".equals(footballMatchJsonCaller.getAwayTeamScore())) {
//                holder.matchDay.setText(isttime);
            } else {
                holder.t1score.setText(footballMatchJsonCaller.getHomeTeamScore());
                holder.t2score.setText(footballMatchJsonCaller.getAwayTeamScore());
//                holder.matchDay.setText(footballMatchJsonCaller.getMatchStatus());
            }

            if( footballMatchJsonCaller.isLive() ){

            } else {

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        holder.matchDay.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
        holder.venue.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
        holder.date.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
        holder.team1.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
        holder.team2.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
        holder.t1score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
        holder.t2score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());

        holder.view.setTag(position);
        holder.view.setOnClickListener(listener);
    }

    private void handleItemClick(View view){
        int position = (Integer)view.getTag();
        JSONObject matchJsonObject = list.get(position);

        try {
            footballMatchJsonCaller.setJsonObject(matchJsonObject);

            Intent intent = new Intent( activity, ScoreDetailActivity.class);
            activity.startActivity(intent);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private String getLocalTime(String matchTime) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        java.sql.Time timeValue = new java.sql.Time(formatter.parse(matchTime).getTime());
        String time = timeValue.toString();

        return time;
    }

    /*private void liveMatchColoring(MatchListAdapter.ViewHolder holder) {
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

    private void clearLiveMatchColoring(MatchListAdapter.ViewHolder holder) {
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
    }*/

    public String getMonth(String mon) {
        switch (mon) {
            case "Jan":
                return "January";
            case "Feb":
                return "February";
            case "Mar":
                return "March";
            case "Apr":
                return "April";
            case "May":
                return "May";
            case "Jun":
                return "June";
            case "Jul":
                return "July";
            case "Aug":
                return "August";
            case "Sep":
                return "September";
            case "Oct":
                return "October";
            case "Nov":
                return "November";
            case "Dec":
                return "December";
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}
