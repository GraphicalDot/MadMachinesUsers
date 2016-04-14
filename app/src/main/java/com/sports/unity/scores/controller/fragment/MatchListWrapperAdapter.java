package com.sports.unity.scores.controller.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.controller.fragment.StaffPagerAdapter;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by madmachines on 3/3/16.
 */
public class MatchListWrapperAdapter extends RecyclerView.Adapter<MatchListWrapperAdapter.ViewHolder> implements TokenRegistrationHandler.TokenRegistrationContentListener {

    private List<MatchListWrapperItem> matchDay;
    private Activity activity;
    private MatchListWrapperNotify matchListWrapperNotify;
    private ArrayList<FavouriteItem> flagFavItem;

    private boolean isIndividualFixture = false;
    private long dummyBannerEpochTime = -99;

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
    private String tempKey;
    private boolean shouldShowHeader = false;

    public MatchListWrapperAdapter(List<MatchListWrapperItem> matchDay, Activity activity, MatchListWrapperNotify matchListWrapperNotify, boolean shouldShowHeader) {
        this.matchDay = matchDay;
        this.activity = activity;
        this.matchListWrapperNotify = matchListWrapperNotify;
        this.shouldShowHeader = shouldShowHeader;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staff_banner_layout, parent, false);
            return new ViewHolder(view, true);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_score_wrapper, parent, false);
            return new ViewHolder(view, false);

        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            if (getItemViewType(position) == 0) {
                handleStaffFavContent(holder);
            } else {
                {
//                    if (shouldShowHeader && position > pos) {
//                        position = position - 1;
//                    }
                    MatchListWrapperItem previousDTO = null;
                    if (position == 0) {
                        previousDTO = matchDay.get(position);
                    } else {
                        previousDTO = matchDay.get(position - 1);
                    }

                    MatchListWrapperItem dto = matchDay.get(position);
                    if (previousDTO.getDay().equalsIgnoreCase(dto.getDay()) && (position != 0)) {
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


                    JSONObject matchJsonObject = dto.getJsonObject();
                    try {
                        matchJsonCaller.setJsonObject(matchJsonObject);


//            holder.liveText.setTypeface(FontTypeface.getInstance(activity).getRobotoRegular());
//            holder.liveText.setTextColor(Color.BLACK);
                        if (matchJsonCaller.getType().equals(ScoresJsonParser.CRICKET)) {
                            cricketMatchJsonCaller.setJsonObject(matchJsonObject);
                            cricketMatchJsonCaller.setMatchWidgetAwayTeam(null);
                            cricketMatchJsonCaller.setMatchWidgetHomeTeam(null);

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
                                SetLiveMatchScoreCard(holder);
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
                                                tempKey = key;
                                                tokenRegistrationHandler = TokenRegistrationHandler.getInstance(activity);
                                                tokenRegistrationHandler.addListener(MatchListWrapperAdapter.this);
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
                                                tempKey = key;
                                                tokenRegistrationHandler = TokenRegistrationHandler.getInstance(activity);
                                                tokenRegistrationHandler.addListener(MatchListWrapperAdapter.this);
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
                            holder.team1Overs.setVisibility(View.GONE);
                            holder.team2Overs.setVisibility(View.GONE);

                            footballMatchJsonCaller.setJsonObject(matchJsonObject);
                            final String key = footballMatchJsonCaller.getMatchId().toString() + "|" + footballMatchJsonCaller.getLeagueId();
                            Log.i("FOOTBALMATCH: ", key);
                            if ("FT".equalsIgnoreCase(footballMatchJsonCaller.getMatchStatus())) {
                                Log.i("FOOTBALMATCHSTATUS: ", footballMatchJsonCaller.getMatchStatus());
                                holder.notification.setVisibility(View.GONE);
                            } else {

                                preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                                String subsMatch = preferences.getString(key, "");
                                if (key.equalsIgnoreCase(subsMatch) && !subsMatch.equals("") ) {
                                    holder.notification.setImageResource(R.drawable.ic_notification_enable);
                                    holder.notification.setVisibility(View.VISIBLE);
                                    holder.notification.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {

                                                tempKey = key;
                                                tokenRegistrationHandler = TokenRegistrationHandler.getInstance(activity);
                                                tokenRegistrationHandler.addListener(MatchListWrapperAdapter.this);

                                                // Toast.makeText(context,"Key :- "+ tempKey,Toast.LENGTH_LONG).show();


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

                                                tempKey = key;
                                                tokenRegistrationHandler = TokenRegistrationHandler.getInstance(activity);
                                                tokenRegistrationHandler.addListener(MatchListWrapperAdapter.this);

                                                // Toast.makeText(context,"Key :- "+ tempKey,Toast.LENGTH_LONG).show();

                                                tokenRegistrationHandler.registrerMatchUser(key, CommonUtil.getToken(activity));


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });


                                }

                            }


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

                            Glide.with(activity).load(footballMatchJsonCaller.getHomeTeamFlag()).placeholder(R.drawable.ic_no_img).into(holder.t1flag);
                            Glide.with(activity).load(footballMatchJsonCaller.getAwayTeamFlag()).placeholder(R.drawable.ic_no_img).into(holder.t2flag);

                            if ("?".equals(footballMatchJsonCaller.getAwayTeamScore())) {
                                if ("Postp.".equalsIgnoreCase(footballMatchJsonCaller.getMatchStatus())) {
                                    holder.matchDay.setText(R.string.post_pond);
                                    holder.liveText.setVisibility(View.GONE);
                                } else {
                                    holder.matchDay.setText("Upcoming");
                                    holder.liveText.setVisibility(View.GONE);
                                }


                                holder.t1score.setText("");
                                holder.t2score.setText("");
                            } else {
                                if (footballMatchJsonCaller.isLive()) {
                                    //holder.matchMinutes.setText(footballMatchJsonCaller.getMatchStatus());
                                    holder.liveText.setVisibility(View.VISIBLE);
                                    holder.matchDay.setVisibility(View.GONE);
                                    holder.matchMinutes.setVisibility(View.GONE);
                                    holder.liveText.setText(footballMatchJsonCaller.getMatchStatus());

                                } else {
                                    holder.matchDay.setText("Completed");
                                    holder.liveText.setVisibility(View.GONE);
                                }
                                holder.t1score.setVisibility(View.VISIBLE);
                                holder.t2score.setVisibility(View.VISIBLE);
                                holder.t1score.setText(footballMatchJsonCaller.getHomeTeamScore());
                                holder.t2score.setText(footballMatchJsonCaller.getAwayTeamScore());
                            }

                            if (!footballMatchJsonCaller.getTeams1Odds().equals("") && !footballMatchJsonCaller.getTeams2Odds().equals("")) {
                                holder.odds.setVisibility(View.VISIBLE);
                                ((ViewGroup) holder.odds.getParent()).setTag(position);
                                ((ViewGroup) holder.odds.getParent()).setClickable(true);
                                ((ViewGroup) holder.odds.getParent()).setOnClickListener(oddsClickListener);
                            } else {
                                holder.odds.setVisibility(View.GONE);

                                ((ViewGroup) holder.odds.getParent()).setClickable(false);
                            }

                            holder.team1.setTextColor(activity.getResources().getColor(R.color.ColorPrimaryDark));
                            holder.t1score.setTextColor(activity.getResources().getColor(R.color.ColorPrimaryDark));
                            holder.team2.setTextColor(activity.getResources().getColor(R.color.ColorPrimaryDark));
                            holder.t2score.setTextColor(activity.getResources().getColor(R.color.ColorPrimaryDark));
                            holder.team1.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
                            holder.team2.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
                            holder.t1score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
                            holder.t2score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());

                            String result = matchJsonCaller.getResult();
                            if (holder.matchDay.getText().equals("Completed")) {
                                if (result != null || result != "") {
                                    if (result.equals("home_team")) {
                                        holder.team1.setTextColor(activity.getResources().getColor(R.color.app_theme_blue));
                                        holder.t1score.setTextColor(activity.getResources().getColor(R.color.app_theme_blue));
                                        holder.team1.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
                                        holder.t1score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
                                    } else if (result.equals("away_team")) {
                                        holder.team2.setTextColor(activity.getResources().getColor(R.color.app_theme_blue));
                                        holder.t2score.setTextColor(activity.getResources().getColor(R.color.app_theme_blue));
                                        holder.team2.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
                                        holder.t2score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
                                    }
                                }
                            } else {
                            }


                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    holder.liveText.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());

                    holder.matchDay.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
                    holder.venue.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
                    holder.date.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
//        holder.team1.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
//        holder.team2.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
//        holder.t1score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
//        holder.t2score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());

                    holder.rvChild.setTag(position);
                    holder.rvChild.setOnClickListener(listener);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemViewType(int position) {
        if ( matchDay.get(position).getEpochTime() == dummyBannerEpochTime) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        int count = matchDay.size();
        return count;
    }

    @Override
    public void handleContent(String content) {
        try {

            JSONObject object = new JSONObject(content);
            if (object != null && !object.isNull("status") && 200 == object.getInt("status")) {
                if ("success".equalsIgnoreCase(object.getString("info"))) {

                    // Toast.makeText(context,"Key :- "+tempKey,Toast.LENGTH_LONG).show();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
                    SharedPreferences.Editor editor = prefs.edit();
                    String storedKey = prefs.getString(tempKey, "");
                    if (tempKey.equalsIgnoreCase(storedKey)) {
                        editor.remove(tempKey);
                    } else {
                        editor.putString(tempKey, tempKey);
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

    public void setIsIndividualFixture() {
        this.isIndividualFixture = true;
    }

    private void setCommonDetails(ViewHolder holder, String homeTeam, String awayTeam) throws JSONException {
        holder.team1.setText(homeTeam);
        holder.team2.setText(awayTeam);
        holder.venue.setText(cricketMatchJsonCaller.getVenue());
        holder.t1score.setVisibility(View.VISIBLE);
        holder.t2score.setVisibility(View.VISIBLE);
        holder.team1Overs.setVisibility(View.VISIBLE);
        holder.team2Overs.setVisibility(View.VISIBLE);
        holder.date.setText(DateUtil.getDateFromEpochTime(Long.valueOf(cricketMatchJsonCaller.getMatchDateTimeEpoch()) * 1000));
        holder.team1.setTextColor(activity.getResources().getColor(R.color.ColorPrimaryDark));
        holder.t1score.setTextColor(activity.getResources().getColor(R.color.ColorPrimaryDark));
        holder.team2.setTextColor(activity.getResources().getColor(R.color.ColorPrimaryDark));
        holder.t2score.setTextColor(activity.getResources().getColor(R.color.ColorPrimaryDark));
        holder.team1.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
        holder.team2.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
        holder.t1score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
        holder.t2score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedRegular());
    }

    private void SetLiveMatchScoreCard(ViewHolder holder) throws JSONException {
        holder.team1Overs.setVisibility(View.VISIBLE);
        holder.team2Overs.setVisibility(View.VISIBLE);
        holder.t1score.setVisibility(View.VISIBLE);
        holder.t2score.setVisibility(View.VISIBLE);
        holder.liveText.setText(R.string.live);
        holder.liveText.setVisibility(View.VISIBLE);
        holder.matchDay.setText(cricketMatchJsonCaller.getMatchName());
        StringBuilder stringBuilder = new StringBuilder("");
        stringBuilder.append(cricketMatchJsonCaller.getTeam1Score() );
        stringBuilder.append("/");
        stringBuilder.append(cricketMatchJsonCaller.getWicketsTeam1());
        holder.t1score.setText(stringBuilder.toString() + "(" + cricketMatchJsonCaller.getOversTeam1()+")");
        //holder.team1Overs.setText(cricketMatchJsonCaller.getOversTeam1()  + " OVS");
        stringBuilder = new StringBuilder("");
        stringBuilder.append(cricketMatchJsonCaller.getTeam2Score() );
        stringBuilder.append("/");
        stringBuilder.append(cricketMatchJsonCaller.getWicketsTeam2());
        holder.t2score.setText(stringBuilder.toString()+"("+cricketMatchJsonCaller.getOversTeam2()+")");
        //holder.team2Overs.setText(cricketMatchJsonCaller.getOversTeam2() + " OVS");
    }

    private void setUpcommingMatchScoreCard(ViewHolder holder) throws JSONException {
        //holder.matchDay.setText(cricketMatchJsonCaller.getMatchNumber());
        holder.matchDay.setText(cricketMatchJsonCaller.getMatchName());
        holder.liveText.setText("Upcoming");
        holder.t1score.setVisibility(View.GONE);
        holder.t2score.setVisibility(View.GONE);
        holder.team1Overs.setVisibility(View.GONE);
        holder.team2Overs.setVisibility(View.GONE);
        holder.liveText.setVisibility(View.VISIBLE);
    }

    private void SetCompletedMatchScoreCard(ViewHolder holder) throws JSONException {
        holder.matchDay.setText(cricketMatchJsonCaller.getMatchName());
        holder.liveText.setText("Completed");
        holder.liveText.setVisibility(View.VISIBLE);
        holder.t1score.setVisibility(View.VISIBLE);
        holder.t2score.setVisibility(View.VISIBLE);
        holder.team1Overs.setVisibility(View.GONE);
        holder.team2Overs.setVisibility(View.GONE);
        holder.t1score.setText(cricketMatchJsonCaller.getTeam1Score() + "/" + cricketMatchJsonCaller.getWicketsTeam1()+"(" + cricketMatchJsonCaller.getOversTeam1()+")");
        holder.t2score.setText(cricketMatchJsonCaller.getTeam2Score() + "/" +cricketMatchJsonCaller.getWicketsTeam1()+"("+cricketMatchJsonCaller.getOversTeam2()+")");
        //holder.matchDay.setText("Completed");

        String result = matchJsonCaller.getWinerTeam();
        if (result != null || result != "") {
            if (result.equals("home_team")) {
                holder.team1.setTextColor(activity.getResources().getColor(R.color.app_theme_blue));
                holder.t1score.setTextColor(activity.getResources().getColor(R.color.app_theme_blue));
                holder.team1.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
                holder.t1score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
            } else if (result.equals("away_team")) {
                holder.team2.setTextColor(activity.getResources().getColor(R.color.app_theme_blue));
                holder.t2score.setTextColor(activity.getResources().getColor(R.color.app_theme_blue));
                holder.team2.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
                holder.t2score.setTypeface(FontTypeface.getInstance(activity).getRobotoCondensedBold());
            }
        }
    }

    private void handleItemClick(View view) {
        int position = (Integer) view.getTag();
        JSONObject matchJsonObject = matchDay.get(position).getJsonObject();

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
                date = cricketMatchJsonCaller.getMatchDate();
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


    class OddsClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();

            JSONObject jsonObject = matchDay.get(position).getJsonObject();
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

            } catch (Exception ex) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView = null;
        private TextView tvDayName;
        private TextView tvLeagueName;
        private LinearLayout rvChild;
        private ImageView ivSportsIcon;
        private RelativeLayout leagueLayout;
        private View sepTop;
        private View sepBottom;


        private ImageView t1flag;
        private TextView team1;
        private TextView t1score;
        private ImageView t2flag;
        private TextView team2;
        private TextView t2score;
        private TextView matchDay;
        private TextView liveText;
        private TextView venue;
        private TextView date;
        private TextView odds;
        private TextView team1Overs;
        private TextView team2Overs;
        private TextView matchMinutes;
        private ImageView notification;
        private RadioGroup radioGroup;
        private ViewPager pager;

        public ViewHolder(View view, boolean isHeader) {
            super(view);
            if (isHeader) {
                radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
                pager = (ViewPager) view.findViewById(R.id.pager);
            } else {
                mView = view;
                tvDayName = (TextView) view.findViewById(R.id.id_day_name);
                leagueLayout = (RelativeLayout) view.findViewById(R.id.league_layout);
                tvLeagueName = (TextView) view.findViewById(R.id.league_name);
                ivSportsIcon = (ImageView) view.findViewById(R.id.iv_league);
                rvChild = (LinearLayout) view.findViewById(R.id.child_rv);
                sepTop = view.findViewById(R.id.sep_top);
                sepBottom = view.findViewById(R.id.sep_bottom);

                t1flag = (ImageView) rvChild.findViewById(R.id.t1flag);
                team1 = (TextView) rvChild.findViewById(R.id.team1);
                t1score = (TextView) rvChild.findViewById(R.id.t1score);
                t2flag = (ImageView) rvChild.findViewById(R.id.t2flag);
                team2 = (TextView) rvChild.findViewById(R.id.team2);
                t2score = (TextView) rvChild.findViewById(R.id.t2score);
                matchDay = (TextView) rvChild.findViewById(R.id.matchDay);
                liveText = (TextView) rvChild.findViewById(R.id.liveText);
                venue = (TextView) rvChild.findViewById(R.id.venue);
                date = (TextView) rvChild.findViewById(R.id.date);
                odds = (TextView) rvChild.findViewById(R.id.show_odds);
                team1Overs = (TextView) rvChild.findViewById(R.id.t1over);
                team2Overs = (TextView) rvChild.findViewById(R.id.t2over);
                matchMinutes = (TextView) rvChild.findViewById(R.id.minutes);
                notification = (ImageView) rvChild.findViewById(R.id.notification);
            }

        }
    }

    private void handleStaffFavContent(final MatchListWrapperAdapter.ViewHolder holder) {
        String staffFavString = UserUtil.getStaffSelectedData(activity);
        final ArrayList<FavouriteItem> favouriteItems = new ArrayList<FavouriteItem>();
        if (null != staffFavString && !TextUtils.isEmpty(staffFavString)) {
            flagFavItem = FavouriteItemWrapper.getInstance(activity).getFavListOfOthers(staffFavString);
            if (flagFavItem != null && flagFavItem.size() > 0) {
                for (FavouriteItem f : flagFavItem) {
                    final String id = f.getId();
                    if (!TinyDB.getInstance(activity).getBoolean(id, false)) {
                        favouriteItems.add(f);
                    }
                }
                if (favouriteItems.size() > 0) {
                    holder.radioGroup.removeAllViews();
                    StaffPagerAdapter adapter = new StaffPagerAdapter(activity, favouriteItems, holder.radioGroup, this);
                    holder.pager.setAdapter(adapter);
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    if (favouriteItems.size() > 1) {
                        for (int i = 0; i < favouriteItems.size(); i++) {
                            RadioButton radioButton = new RadioButton(activity);
                            radioButton.setClickable(false);
                            radioButton.setButtonDrawable(R.drawable.tour_icon);
                            radioButton.setId(i);
                            radioButton.setPadding(5, 0, 5, 0);
                            if (i == 0) {
                                radioButton.setChecked(true);
                            }
                            holder.radioGroup.addView(radioButton, params);
                        }
                    }
                    holder.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            if (favouriteItems.size() > 1) {
                                holder.radioGroup.check(position);
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }
            }
        }
    }

    public void removeStaffBanner() {
        shouldShowHeader = false;
        for (int i = 0; i < matchDay.size(); i++) {
            MatchListWrapperItem item = matchDay.get(i);
            if( item.getEpochTime() == dummyBannerEpochTime ){
                matchDay.remove(i);
            }
        }
        this.notifyDataSetChanged();
    }

    public int notifyAdapter() {
        int pos = 0;
        if (matchDay.size() > 0 ) {
            try {
                for (int i = 0; i < matchDay.size(); i++) {
                    MatchListWrapperItem item = matchDay.get(i);
                    if( item.getEpochTime() == dummyBannerEpochTime ){
                        matchDay.remove(i);
                    }
                }

                for (int i = 0; i < matchDay.size(); i++) {
                    if (matchDay.get(i).getDay().equalsIgnoreCase("Today")) {
                        pos = i;
                        if( shouldShowHeader ) {
                            MatchListWrapperItem bannerDummyItem = new MatchListWrapperItem();
                            bannerDummyItem.setDay("Yesterday");
                            bannerDummyItem.setEpochTime(Long.valueOf(dummyBannerEpochTime));
                            matchDay.add(i, bannerDummyItem);
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.notifyDataSetChanged();
        return pos;
    }

}
