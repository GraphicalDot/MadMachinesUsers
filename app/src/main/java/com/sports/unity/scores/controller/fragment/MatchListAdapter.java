package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.gcm.TokenRegistrationHandler;
import com.sports.unity.scores.ScoreDetailActivity;
import com.sports.unity.scores.model.ScoresJsonParser;
import com.sports.unity.scores.model.football.CricketMatchJsonCaller;
import com.sports.unity.scores.model.football.FootballMatchJsonCaller;
import com.sports.unity.scores.model.football.MatchJsonCaller;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.commons.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by madmachines on 8/10/15.
 */
public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.ViewHolder> implements TokenRegistrationHandler.TokenRegistrationContentListener {

    private Activity activity;
    private List<JSONObject> list;

    private MatchJsonCaller matchJsonCaller = new MatchJsonCaller();
    private FootballMatchJsonCaller footballMatchJsonCaller = new FootballMatchJsonCaller();
    private CricketMatchJsonCaller cricketMatchJsonCaller = new CricketMatchJsonCaller();

    private OddsClickListener oddsClickListener = new OddsClickListener();

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            handleItemClick(view);
        }

    };

    /*private View.OnClickListener matchAlertListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            handleMatchAlert(view);
        }

    };*/
    private TokenRegistrationHandler tokenRegistrationHandler;
    private SharedPreferences preferences;
    private String seriesId;
    private String matchId;
    private MatchListWrapperNotify matchListWrapperNotify;

    public MatchListAdapter(List<JSONObject> list, Activity activity, MatchListWrapperNotify matchListWrapperNotify) {
        this.list = list;
        this.activity = activity;
        this.matchListWrapperNotify = matchListWrapperNotify;


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView t1flag;
        private TextView team1;
        private TextView team1Overs;
        private ImageView t2flag;
        private TextView team2;
        private TextView team2Overs;
        private TextView matchDay;
        private TextView liveText;
        private TextView venue;
        private TextView date;
        private TextView odds;
        private TextView team_score;
        private TextView matchMinutes;
        private ImageView notification;

        private View view;

        public ViewHolder(View v) {
            super(v);

            view = v;

            t1flag = (ImageView) v.findViewById(R.id.t1flag);
            team1 = (TextView) v.findViewById(R.id.team1);
            team_score = (TextView) v.findViewById(R.id.team_score);
            t2flag = (ImageView) v.findViewById(R.id.t2flag);
            team2 = (TextView) v.findViewById(R.id.team2);
            matchDay = (TextView) v.findViewById(R.id.matchDay);
            liveText = (TextView) v.findViewById(R.id.liveText);
            venue = (TextView) v.findViewById(R.id.venue);
            date = (TextView) v.findViewById(R.id.date);
            odds = (TextView) v.findViewById(R.id.show_odds);
            team1Overs = (TextView) v.findViewById(R.id.t1over);
            team2Overs = (TextView) v.findViewById(R.id.t2over);
            matchMinutes = (TextView) v.findViewById(R.id.minutes);
            notification = (ImageView) v.findViewById(R.id.notification);

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
            matchJsonCaller.setJsonObject(matchJsonObject);


//            holder.liveText.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
//            holder.liveText.setTextColor(Color.BLACK);
            if (matchJsonCaller.getType().equals(ScoresJsonParser.CRICKET)) {
                cricketMatchJsonCaller.setJsonObject(matchJsonObject);
                cricketMatchJsonCaller.setMatchWidgetAwayTeam(null);
                cricketMatchJsonCaller.setMatchWidgetHomeTeam(null);
                footballMatchJsonCaller.setJsonObject(null);

                /*if(cricketMatchJsonCaller.getTeams1Odds()== null && cricketMatchJsonCaller.getTeams1Odds()==null){
                    holder.odds.setVisibility(View.INVISIBLE);
                }else{
                    holder.odds.setVisibility(View.VISIBLE);
                }*/

                JSONObject widgetTeamsObject = cricketMatchJsonCaller.getTeamsWiget();
                JSONArray widgetTeamsFirst = null;
                JSONArray widgetTeamSecond = null;
                if (!widgetTeamsObject.isNull("1")) {
                    widgetTeamsFirst = widgetTeamsObject.getJSONArray("1");
                } else {
                    widgetTeamsFirst = new JSONArray();
                }

                if (!widgetTeamsObject.isNull("2")) {
                    widgetTeamSecond = widgetTeamsObject.getJSONArray("2");
                } else {
                    widgetTeamSecond = new JSONArray();
                }


                String homeTeam = cricketMatchJsonCaller.getTeam1();
                String awayTeam = cricketMatchJsonCaller.getTeam2();
                Glide.with(activity).load(cricketMatchJsonCaller.getTeam1Flag()).placeholder(R.drawable.ic_no_img).into(holder.t1flag);
                Glide.with(activity).load(cricketMatchJsonCaller.getTeam2Flag()).placeholder(R.drawable.ic_no_img).into(holder.t2flag);


                if (widgetTeamsFirst != null) {
                    for (int i = 0; i < widgetTeamsFirst.length(); i++) {
                        JSONObject teamData = widgetTeamsFirst.getJSONObject(i);
                        if (awayTeam.equalsIgnoreCase(teamData.getString("team_name"))) {
                            cricketMatchJsonCaller.setMatchWidgetAwayTeam(teamData);
                        }
                        if (homeTeam.equalsIgnoreCase(teamData.getString("team_name"))) {
                            cricketMatchJsonCaller.setMatchWidgetHomeTeam(teamData);

                        }

                    }
                }
                if (widgetTeamSecond != null) {

                    for (int i = 0; i < widgetTeamSecond.length(); i++) {
                        JSONObject teamData = widgetTeamSecond.getJSONObject(i);
                        if (homeTeam.equalsIgnoreCase(teamData.getString("team_name"))) {
                            cricketMatchJsonCaller.setMatchWidgetHomeTeam(teamData);

                        }


                        if (awayTeam.equalsIgnoreCase(teamData.getString("team_name"))) {
                            cricketMatchJsonCaller.setMatchWidgetAwayTeam(teamData);
                        }
                    }
                }
                setCommonDetails(holder, homeTeam, awayTeam);
                // f completed
                if (cricketMatchJsonCaller.getStatus().equalsIgnoreCase("F")) {
                    SetCompletedMatchScoreCard(holder);
                    // N means Match Not started
                } else if (cricketMatchJsonCaller.getStatus().equalsIgnoreCase("N") || TextUtils.isEmpty(cricketMatchJsonCaller.getStatus())) {
                    setUpcommingMatchScoreCard(holder);
                    // L means Match Live
                } else if (cricketMatchJsonCaller.getStatus().equalsIgnoreCase("L")) {
                    boolean isLive = false;
                    SetLiveMatchScoreCard(holder, isLive);
                }


                if (!matchJsonCaller.getTeams1Odds().equals("") && !matchJsonCaller.getTeams2Odds().equals("")) {
                    holder.odds.setVisibility(View.VISIBLE);
                    ((ViewGroup) holder.odds.getParent()).setTag(position);
                    ((ViewGroup) holder.odds.getParent()).setClickable(true);
                    ((ViewGroup) holder.odds.getParent()).setOnClickListener(oddsClickListener);
                } else {
                    holder.odds.setVisibility(View.GONE);

                    ((ViewGroup) holder.odds.getParent()).setClickable(false);
                }
                if ("L".equalsIgnoreCase(cricketMatchJsonCaller.getStatus()) || "N".equalsIgnoreCase(cricketMatchJsonCaller.getStatus())) {
                    preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    final String key = cricketMatchJsonCaller.getMatchId() + "|" + cricketMatchJsonCaller.getSeriesId();
                    String subsMatch = preferences.getString(key, "");
                    if (key.equalsIgnoreCase(subsMatch) && !subsMatch.equals("")) {
                        holder.notification.setImageResource(R.drawable.ic_notification_enable);
                        holder.notification.setVisibility(View.VISIBLE);
                        holder.notification.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    seriesId = cricketMatchJsonCaller.getSeriesId();
                                    matchId = cricketMatchJsonCaller.getMatchId();
                                    tokenRegistrationHandler = TokenRegistrationHandler.getInstance(activity);
                                    tokenRegistrationHandler.addListener(MatchListAdapter.this);
                                    tokenRegistrationHandler.removeMatchUser(key);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        holder.notification.setImageResource(R.drawable.ic_notification_disabled);
                        holder.notification.setVisibility(View.VISIBLE);
                        holder.notification.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    seriesId = cricketMatchJsonCaller.getSeriesId();
                                    matchId = cricketMatchJsonCaller.getMatchId();
                                    tokenRegistrationHandler = TokenRegistrationHandler.getInstance(activity);
                                    tokenRegistrationHandler.addListener(MatchListAdapter.this);
                                    tokenRegistrationHandler.registrerMatchUser(key, CommonUtil.getToken(activity));


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    }

                } else {
                    holder.notification.setVisibility(View.GONE);
                }


            } else if (matchJsonCaller.getType().equals(ScoresJsonParser.FOOTBALL)) {


                footballMatchJsonCaller.setJsonObject(matchJsonObject);
                cricketMatchJsonCaller.setJsonObject(null);

                String isttime = null;
                try {
                    isttime = getLocalTime(footballMatchJsonCaller.getMatchTime()).substring(0, 5);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                holder.team1.setText(footballMatchJsonCaller.getHomeTeam());
                holder.team2.setText(footballMatchJsonCaller.getAwayTeam());

                holder.team1Overs.setVisibility(View.GONE);
                holder.team2Overs.setVisibility(View.GONE);

                holder.venue.setText(footballMatchJsonCaller.getStadium());
                holder.date.setVisibility(View.GONE);

                Glide.with(activity).load(footballMatchJsonCaller.getHomeTeamFlag()).placeholder(R.drawable.ic_no_img).into(holder.t1flag);
                Glide.with(activity).load(footballMatchJsonCaller.getAwayTeamFlag()).placeholder(R.drawable.ic_no_img).into(holder.t2flag);

                if ("?".equals(footballMatchJsonCaller.getAwayTeamScore())) {
//                    holder.t1score.setVisibility(View.GONE);
//                    holder.t2score.setVisibility(View.GONE);


                    if("Postp.".equalsIgnoreCase(footballMatchJsonCaller.getMatchStatus())){
                        holder.matchDay.setText("PostPond");
                        holder.liveText.setVisibility(View.GONE);
                        holder.team_score.setVisibility(View.GONE);
                        holder.date.setVisibility(View.VISIBLE);
                        holder.date.setText(isttime);
                    }else{
                        // holder.matchDay.setText("Upcoming");
                        holder.matchDay.setVisibility(View.GONE);
                        holder.liveText.setVisibility(View.GONE);
                        holder.team_score.setVisibility(View.GONE);
                        holder.date.setVisibility(View.VISIBLE);
                        holder.date.setText(isttime);
                    }

                } else {
                    if( footballMatchJsonCaller.isLive() ){
                        holder.matchMinutes.setVisibility(View.VISIBLE);
                        holder.matchMinutes.setText(footballMatchJsonCaller.getMatchStatus());
                        holder.liveText.setVisibility(View.GONE);
                        holder.date.setVisibility(View.GONE);
                        holder.team_score.setVisibility(View.VISIBLE);
                        holder.team_score.setBackgroundResource(R.drawable.score_background_blue);
                        holder.team_score.setTextColor(holder.team_score.getResources().getColor(R.color.ColorPrimary));
                        StringBuilder stringBuilder = new StringBuilder("");
                        stringBuilder.append(footballMatchJsonCaller.getHomeTeamScore());
                        stringBuilder.append("-");
                        stringBuilder.append(footballMatchJsonCaller.getAwayTeamScore());
                        holder.team_score.setText(stringBuilder.toString());

                    } else {
                        // holder.matchDay.setText("Completed");
                        holder.date.setVisibility(View.GONE);
                        holder.matchDay.setVisibility(View.GONE);
                        holder.liveText.setVisibility(View.GONE);
                        holder.matchMinutes.setVisibility(View.GONE);
                        holder.team_score.setVisibility(View.VISIBLE);
                        holder.team_score.setBackgroundResource(R.drawable.score_background_green);
                        holder.team_score.setTextColor(holder.team_score.getResources().getColor(R.color.ColorPrimary));
                        StringBuilder stringBuilder = new StringBuilder("");
                        stringBuilder.append(footballMatchJsonCaller.getHomeTeamScore());
                        stringBuilder.append(" : ");
                        stringBuilder.append(footballMatchJsonCaller.getAwayTeamScore());
                        holder.team_score.setText(stringBuilder.toString());
                    }
                }

                if (footballMatchJsonCaller.getTeams1Odds() != null && footballMatchJsonCaller.getTeams2Odds() != null) {
                    holder.odds.setVisibility(View.VISIBLE);

                    ((ViewGroup) holder.odds.getParent()).setTag(position);
                    ((ViewGroup) holder.odds.getParent()).setClickable(true);
                    ((ViewGroup) holder.odds.getParent()).setOnClickListener(oddsClickListener);
                } else {
                    holder.odds.setVisibility(View.GONE);

                    ((ViewGroup) holder.odds.getParent()).setClickable(false);
                }

                if (footballMatchJsonCaller.getMatchStatus().equals("FT")) {
                    Log.i("FOOTBALMATCHSTATUS: ", footballMatchJsonCaller.getMatchStatus());
                    holder.notification.setVisibility(View.GONE);
                } else {

                    preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                    final String key = footballMatchJsonCaller.getMatchId() + "|" + footballMatchJsonCaller.getLeagueId();
                    String subsMatch = preferences.getString(key, "");
                    if (key.equalsIgnoreCase(subsMatch) && !subsMatch.equals("")) {
                        holder.notification.setImageResource(R.drawable.ic_notification_enable);
                        holder.notification.setVisibility(View.VISIBLE);
                        holder.notification.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    seriesId = footballMatchJsonCaller.getLeagueId();
                                    matchId = footballMatchJsonCaller.getMatchId().toString();
                                    tokenRegistrationHandler = TokenRegistrationHandler.getInstance(activity);
                                    tokenRegistrationHandler.addListener(MatchListAdapter.this);
                                    tokenRegistrationHandler.removeMatchUser(key);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        holder.notification.setImageResource(R.drawable.ic_notification_disabled);
                        holder.notification.setVisibility(View.VISIBLE);
                        holder.notification.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    seriesId = footballMatchJsonCaller.getLeagueId();
                                    matchId = footballMatchJsonCaller.getMatchId().toString();
                                    tokenRegistrationHandler = TokenRegistrationHandler.getInstance(activity);
                                    tokenRegistrationHandler.addListener(MatchListAdapter.this);
                                    tokenRegistrationHandler.registrerMatchUser(key, CommonUtil.getToken(activity));


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    }

                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        holder.view.setTag(position);
        holder.view.setOnClickListener(listener);

        holder.liveText.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());

        holder.matchDay.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
        holder.venue.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
        holder.date.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());

    }

    private void setCommonDetails(ViewHolder holder, String homeTeam, String awayTeam) throws JSONException {

        holder.team_score.setVisibility(View.GONE);
        holder.matchMinutes.setVisibility(View.GONE);
        holder.team1Overs.setVisibility(View.GONE);
        holder.team2Overs.setVisibility(View.GONE);
        holder.venue.setText(cricketMatchJsonCaller.getVenue());
        holder.date.setVisibility(View.GONE);
        Glide.with(activity).load(cricketMatchJsonCaller.getTeam1Flag()).placeholder(R.drawable.ic_no_img).into(holder.t1flag);
        Glide.with(activity).load(cricketMatchJsonCaller.getTeam2Flag()).placeholder(R.drawable.ic_no_img).into(holder.t2flag);
    }

    private void SetLiveMatchScoreCard(ViewHolder holder, boolean isLive) throws JSONException {

        isLive = true;
        holder.liveText.setVisibility(View.VISIBLE);
        //  holder.matchDay.setText(cricketMatchJsonCaller.getMatchNumber());
        holder.matchDay.setVisibility(View.GONE);

        String score = cricketMatchJsonCaller.getTeam1Score();

        holder.team1Overs.setVisibility(View.VISIBLE);
        holder.team2Overs.setVisibility(View.VISIBLE);
        holder.team1Overs.setText(cricketMatchJsonCaller.getOversTeam1() + " OVS");

        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append(cricketMatchJsonCaller.getTeam1Short());
        stringBuilder.append(" ");
        stringBuilder.append(score);
        stringBuilder.append("/");
        stringBuilder.append(cricketMatchJsonCaller.getWicketsTeam1());
        holder.team1.setText(stringBuilder.toString());
        //   holder.team1Overs.setText(cricketMatchJsonCaller.getOvers(score)+"ovs");
        score = cricketMatchJsonCaller.getTeam2Score();

        holder.team2Overs.setText(cricketMatchJsonCaller.getOversTeam2() + " OVS");

        stringBuilder = new StringBuilder("");
        stringBuilder.append(cricketMatchJsonCaller.getTeam2Short());
        stringBuilder.append(" ");
        stringBuilder.append(score);
        stringBuilder.append("/");
        stringBuilder.append(cricketMatchJsonCaller.getWicketsTeam2());
        holder.team2.setText(stringBuilder.toString());
    }

    private void setUpcommingMatchScoreCard(ViewHolder holder) throws JSONException {

        String isttime = null;

        try {
            isttime = getLocalTime(cricketMatchJsonCaller.getMatchTime()).substring(0, 5);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.matchDay.setVisibility(View.GONE);
        holder.liveText.setVisibility(View.GONE);
        holder.team1Overs.setVisibility(View.GONE);
        holder.team2Overs.setVisibility(View.GONE);
        holder.date.setVisibility(View.VISIBLE);
        holder.date.setText(isttime);

        holder.team1.setText(cricketMatchJsonCaller.getTeam1Short());
        holder.team2.setText(cricketMatchJsonCaller.getTeam2Short());
    }

    private void SetCompletedMatchScoreCard(ViewHolder holder) throws JSONException {

        holder.matchDay.setVisibility(View.VISIBLE);
        holder.matchDay.setText("V/S");
        holder.liveText.setVisibility(View.GONE);
        holder.date.setVisibility(View.GONE);
        holder.team1Overs.setVisibility(View.VISIBLE);
        holder.team2Overs.setVisibility(View.VISIBLE);


        String score = cricketMatchJsonCaller.getTeam1Score();

        holder.team1Overs.setText(cricketMatchJsonCaller.getOversTeam1() + " OVS");
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append(cricketMatchJsonCaller.getTeam1Short());
        stringBuilder.append(" ");
        stringBuilder.append(score);
        stringBuilder.append("/");
        stringBuilder.append(cricketMatchJsonCaller.getWicketsTeam1());

        holder.team1.setText(stringBuilder.toString());

        score = cricketMatchJsonCaller.getTeam2Score();

        holder.team2Overs.setText(cricketMatchJsonCaller.getOversTeam2() + " OVS");

        stringBuilder = new StringBuilder("");
        stringBuilder.append(cricketMatchJsonCaller.getTeam2Short());
        stringBuilder.append(" ");
        stringBuilder.append(score);
        stringBuilder.append("/");
        stringBuilder.append(cricketMatchJsonCaller.getWicketsTeam2());

        holder.team2.setText(stringBuilder.toString());
    }

    private void handleItemClick(View view) {
        int position = (Integer) view.getTag();
        JSONObject matchJsonObject = list.get(position);

        try {
            matchJsonCaller.setJsonObject(matchJsonObject);

            String type = matchJsonCaller.getType();
            String matchId = null;
            String matchStatus = "";
            String toss = "";
            String matchName = "";
            String date = "";
            String matchTime = "";
            String seriesId = "";
            String leagueName = "";
            Intent intent = new Intent(activity, ScoreDetailActivity.class);
            if (type.equalsIgnoreCase(ScoresJsonParser.CRICKET)) {
                cricketMatchJsonCaller.setJsonObject(matchJsonObject);
                matchId = cricketMatchJsonCaller.getMatchId();
                matchStatus = cricketMatchJsonCaller.getStatus();
                toss = cricketMatchJsonCaller.getToss();
                matchName = cricketMatchJsonCaller.getMatchNumber() + ", " + cricketMatchJsonCaller.getTeam1() + " v " + cricketMatchJsonCaller.getTeam2();
                date = DateUtil.getDateFromEpochTime(cricketMatchJsonCaller.getMatchDateTimeEpoch() * 1000);
                seriesId = cricketMatchJsonCaller.getSeriesId();
                leagueName = cricketMatchJsonCaller.getSeriesName();
            } else if (type.equalsIgnoreCase(ScoresJsonParser.FOOTBALL)) {
                footballMatchJsonCaller.setJsonObject(matchJsonObject);
                matchId = String.valueOf(footballMatchJsonCaller.getMatchId());
                matchStatus = footballMatchJsonCaller.getMatchStatus();
                matchTime = footballMatchJsonCaller.getMatchTime();
                leagueName = footballMatchJsonCaller.getLeagueName();
                intent.putExtra(Constants.INTENT_KEY_MATCH_TIME, matchTime);
                intent.putExtra(Constants.INTENT_KEY_MATCH_LIVE, footballMatchJsonCaller.isLive());
                intent.putExtra(Constants.INTENT_KEY_TEAM1_ID, footballMatchJsonCaller.getTeam1Id());
                intent.putExtra(Constants.INTENT_KEY_TEAM2_ID, footballMatchJsonCaller.getTeam2Id());
                intent.putExtra(Constants.INTENT_KEY_LEAGUE_ID, footballMatchJsonCaller.getLeagueId());
                intent.putExtra(Constants.INTENT_KEY_TEAM1_NAME, footballMatchJsonCaller.getHomeTeam());
                intent.putExtra(Constants.INTENT_KEY_TEAM2_NAME, footballMatchJsonCaller.getAwayTeam());

            }
            intent.putExtra(Constants.INTENT_KEY_SERIES, seriesId);
            intent.putExtra(Constants.INTENT_KEY_TYPE, type);
            intent.putExtra(Constants.INTENT_KEY_ID, matchId);
            intent.putExtra(Constants.INTENT_KEY_MATCH_STATUS, matchStatus);
            intent.putExtra(Constants.INTENT_KEY_TOSS, toss);
            intent.putExtra(Constants.INTENT_KEY_MATCH_NAME, matchName);
            intent.putExtra(Constants.INTENT_KEY_DATE, date);
            intent.putExtra(Constants.LEAGUE_NAME, leagueName);


            activity.startActivity(intent);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getLocalTime(String matchTime) throws ParseException {
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

    public static String getMonth(String mon) {
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


    class OddsClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();

            JSONObject jsonObject = list.get(position);
            matchJsonCaller.setJsonObject(jsonObject);


            LayoutInflater inflater = activity.getLayoutInflater();
            final View popupOdds = inflater.inflate(R.layout.betfair_dialog_layout, null);

            final AlertDialog.Builder oddsBuilder = new AlertDialog.Builder(activity);
            oddsBuilder.setView(popupOdds);

            final AlertDialog oddsDialog = oddsBuilder.create();
            oddsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            oddsDialog.show();

            ImageView flag1 = (ImageView) popupOdds.findViewById(R.id.flag1);
            ImageView flag2 = (ImageView) popupOdds.findViewById(R.id.flag2);
            ImageView close = (ImageView) popupOdds.findViewById(R.id.close);
            close.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
            TextView team1 = (TextView) popupOdds.findViewById(R.id.team1_name);
            TextView team2 = (TextView) popupOdds.findViewById(R.id.team2_name);
            TextView bet1 = (TextView) popupOdds.findViewById(R.id.bet1);
            TextView bet2 = (TextView) popupOdds.findViewById(R.id.bet2);
            TextView title = (TextView) popupOdds.findViewById(R.id.title);

            team1.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
            team2.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
            bet1.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
            bet2.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
            title.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());

            try {
                if (matchJsonCaller.getType().equals(ScoresJsonParser.CRICKET)) {
                    cricketMatchJsonCaller.setJsonObject(jsonObject);


                    Glide.with(activity).load(cricketMatchJsonCaller.getTeam1Flag()).placeholder(R.drawable.ic_no_img).into(flag1);
                    Glide.with(activity).load(cricketMatchJsonCaller.getTeam2Flag()).placeholder(R.drawable.ic_no_img).into(flag2);

                    team1.setText(cricketMatchJsonCaller.getTeam1());
                    team2.setText(cricketMatchJsonCaller.getTeam2());
                    bet1.setText(matchJsonCaller.getTeams1Odds());
                    bet2.setText(matchJsonCaller.getTeams2Odds());
                } else if (matchJsonCaller.getType().equals(ScoresJsonParser.FOOTBALL)) {
                    footballMatchJsonCaller.setJsonObject(jsonObject);

                    Glide.with(activity).load(footballMatchJsonCaller.getHomeTeamFlag()).placeholder(R.drawable.ic_no_img).into(flag1);
                    Glide.with(activity).load(footballMatchJsonCaller.getAwayTeamFlag()).placeholder(R.drawable.ic_no_img).into(flag2);

                    team1.setText(footballMatchJsonCaller.getHomeTeam());
                    team2.setText(footballMatchJsonCaller.getAwayTeam());
                    bet1.setText(footballMatchJsonCaller.getTeams1Odds());
                    bet2.setText(footballMatchJsonCaller.getTeams2Odds());
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    oddsDialog.dismiss();
                }
            });


        }
    }

    public void updateChild(ArrayList<JSONObject> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public List<JSONObject> getList() {
        return list;
    }

    public void setList(List<JSONObject> list) {
        this.list = list;
    }


    /*private void handleMatchAlert(View view) {
        Integer position = (Integer) view.getTag();
        JSONObject matchJsonObject = list.get(position);
        cricketMatchJsonCaller.setJsonObject(matchJsonObject);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        try {
            String seriesId = cricketMatchJsonCaller.getSeriesId();
            String matchId = cricketMatchJsonCaller.getMatchId();
            tokenRegistrationHandler = TokenRegistrationHandler.getInstance(activity);
            tokenRegistrationHandler.addListener(this);
            tokenRegistrationHandler.registrerMatchUser(seriesId + "|" + matchId, CommonUtil.getToken(activity));
            Toast.makeText(activity,matchId+" "+seriesId,Toast.LENGTH_SHORT).show();



        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/

    @Override
    public void handleContent(String content) {
        try {

            JSONObject object = new JSONObject(content);
            if (object != null && !object.isNull("status") && 200 == object.getInt("status")) {
                if ("success".equalsIgnoreCase(object.getString("info"))) {
                    String key = matchId + "|" + seriesId;
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
                    SharedPreferences.Editor editor = prefs.edit();
                    String storedKey = prefs.getString(key, "");
                    if (key.equalsIgnoreCase(storedKey)) {
                        editor.remove(key);
                    } else {
                        editor.putString(key, key);
                    }
                    editor.apply();
                    matchListWrapperNotify.notifyParent();

                }
            } else {
                Toast.makeText(activity, R.string.match_not_exist, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
