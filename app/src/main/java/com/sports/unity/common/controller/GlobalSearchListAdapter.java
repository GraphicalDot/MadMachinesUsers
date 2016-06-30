package com.sports.unity.common.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.GlobalContentItemObject;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.news.controller.activity.NewsDetailsActivity;
import com.sports.unity.news.controller.activity.NewsSearchActivity;
import com.sports.unity.playerprofile.cricket.PlayerCricketBioDataActivity;
import com.sports.unity.playerprofile.football.PlayerProfileView;
import com.sports.unity.util.Constants;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anupam on 14/6/16.
 */
public class GlobalSearchListAdapter extends RecyclerView.Adapter {

    public static final int VIEW_TYPE_CONTACT = 0;
    public static final int VIEW_TYPE_MESSAGE = 1;
    public static final int VIEW_TYPE_NEWS = 2;
    public static final int VIEW_TYPE_PLAYER_PROFILE = 3;
    public static final int VIEW_TYPE_MATCH = 4;
    public static final int VIEW_TYPE_TEAM = 5;
    public static final int VIEW_TYPE_LEAGUE = 6;
    public static final int VIEW_TYPE_HEADER = 7;

    private ArrayList<GlobalContentItemObject> content = new ArrayList<>();
    private Context context;
    private String keyword = "";

    public GlobalSearchListAdapter(ArrayList<GlobalContentItemObject> content, Context context) {
        if (content.size() > 0) {
            this.content = content;
        }
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_NEWS) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_minicard, parent, false);
        } else if (viewType == VIEW_TYPE_MATCH) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_card, parent, false);
        } else if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.global_search_header, parent, false);
        } else {
            /**
             * same for contact, message, player profile, league, team
             */
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contact_msgs, parent, false);
        }
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        GlobalContentItemObject contentObject = content.get(position);
        ViewHolder holder1 = (ViewHolder) holder;

        switch (contentObject.getType()) {
            case VIEW_TYPE_CONTACT:
                displayContact(contentObject, holder1);
                break;
            case VIEW_TYPE_MESSAGE:
                displayMessages(contentObject, holder1);
                break;
            case VIEW_TYPE_HEADER:
                displayHeader(contentObject, holder1, position);
                break;
            case VIEW_TYPE_NEWS:
                displayNews(contentObject, holder1, position);
                break;
            case VIEW_TYPE_MATCH:
                displayMatch(contentObject, holder1, position);
                break;
            case VIEW_TYPE_PLAYER_PROFILE:
                displayPlayerProfile(contentObject, holder1, position);
                break;
            case VIEW_TYPE_TEAM:
                displayTeam(contentObject, holder1, position);
                break;
            case VIEW_TYPE_LEAGUE:
                displayLeague(contentObject, holder1, position);
                break;
        }
    }

    private void displayLeague(GlobalContentItemObject contentObject, ViewHolder holder1, int position) {
        JSONObject leagueObject = (JSONObject) contentObject.getObject();

        holder1.view.setTag(position);
        holder1.view.setOnClickListener(onClickListener);

        holder1.subtext.setVisibility(View.GONE);
        holder1.inviteButton.setVisibility(View.GONE);
        try {
            holder1.title.setText(leagueObject.getString("name") + " ," + leagueObject.getString("region"));
            if (leagueObject.get("image") == null || leagueObject.getString("image").equals("null")) {
                holder1.imageView.setVisibility(View.GONE);
            } else {
                String myUri = leagueObject.getString("image");
                Glide.with(context).load(myUri).placeholder(R.drawable.ic_blank_img).dontAnimate().into(holder1.imageView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayTeam(GlobalContentItemObject contentObject, ViewHolder holder1, int position) {
        JSONObject teamObject = (JSONObject) contentObject.getObject();

        holder1.view.setTag(position);
        holder1.view.setOnClickListener(onClickListener);

        holder1.subtext.setVisibility(View.GONE);
        holder1.inviteButton.setVisibility(View.GONE);
        try {
            holder1.title.setText(teamObject.getString("name"));
            if (teamObject.get("image") == null || teamObject.getString("image").equals("null")) {
                holder1.imageView.setVisibility(View.GONE);
            } else {
                String myUri = teamObject.getString("image");
                Glide.with(context).load(myUri).placeholder(R.drawable.ic_blank_img).dontAnimate().into(holder1.imageView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayPlayerProfile(GlobalContentItemObject contentObject, ViewHolder holder1, int position) {
        JSONObject playerProfileObject = (JSONObject) contentObject.getObject();

        holder1.subtext.setVisibility(View.GONE);
        holder1.inviteButton.setVisibility(View.GONE);

        holder1.view.setTag(position);
        holder1.view.setOnClickListener(onClickListener);

        try {
            holder1.title.setText(playerProfileObject.getString("name"));
            if (playerProfileObject.get("image") == null || playerProfileObject.getString("image").equals("null")) {
                holder1.imageView.setVisibility(View.GONE);
            } else {
                String myUri = playerProfileObject.getString("image");
                Glide.with(context).load(myUri).placeholder(R.drawable.ic_blank_img).dontAnimate().into(holder1.imageView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayMatch(GlobalContentItemObject contentObject, ViewHolder holder1, int position) {
        JSONObject matchObject = (JSONObject) contentObject.getObject();

//        holder1.view.setTag(position);
//        holder1.view.setOnClickListener(onClickListener);

        try {
            if (matchObject.getString("sport_type").equals("cricket")) {
                holder1.matchProgress.setVisibility(View.GONE);
                holder1.venue.setText(matchObject.getString("venue"));
                if (matchObject.getString("status").equals("N")) {

                    holder1.awayTeam.setText(matchObject.getString("away_team_short_name"));
                    holder1.homeTeam.setText(matchObject.getString("home_team_short_name"));
                    Glide.with(context).load(matchObject.getString("away_team_flag")).placeholder(R.drawable.ic_blank_img).dontAnimate().into(holder1.awayTeamFlag);
                    Glide.with(context).load(matchObject.getString("home_team_flag")).placeholder(R.drawable.ic_blank_img).dontAnimate().into(holder1.homeTeamFlag);
                    holder1.matchDay.setText("v/s");
                } else {
                    JSONObject matchWidget = matchObject.getJSONObject("match_widget");
                    JSONObject awayTeamObject = (JSONObject) matchWidget.getJSONArray("1").get(0);                 // 1 is for away team
                    String awayteamScore = matchObject.getString("away_team_short_name") + " " + awayTeamObject.getString("runs") + "/" + awayTeamObject.getString("overs");
                    holder1.awayTeam.setText(awayteamScore);
                    Glide.with(context).load(awayTeamObject.getString("team_image")).placeholder(R.drawable.ic_blank_img).dontAnimate().into(holder1.awayTeamFlag);
                    holder1.t2overs.setText(awayTeamObject.getString("overs"));

                    JSONObject homeTeamObject = (JSONObject) matchWidget.getJSONArray("2").get(0);                // 2 is for home team
                    String hometeamScore = matchObject.getString("home_team_short_name") + " " + homeTeamObject.getString("runs") + "/" + homeTeamObject.getString("overs");
                    holder1.homeTeam.setText(hometeamScore);
                    Glide.with(context).load(homeTeamObject.getString("team_image")).placeholder(R.drawable.ic_blank_img).dontAnimate().into(holder1.homeTeamFlag);
                    holder1.t1overs.setText(homeTeamObject.getString("overs"));
                    holder1.matchDay.setText("v/s");
                }
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayNews(GlobalContentItemObject contentObject, ViewHolder holder1, int position) {
        JSONObject newsObject = (JSONObject) contentObject.getObject();
        holder1.view.setTag(position);
        holder1.view.setOnClickListener(onClickListener);
        try {
            holder1.newsTitle.setText(newsObject.getString("title"));
            if (newsObject.get("image") == null || newsObject.getString("image").equals("null")) {
                holder1.newsImageView.setVisibility(View.GONE);
            } else {
                String myUri = newsObject.getString("image");
                Glide.with(context).load(myUri).placeholder(R.drawable.ic_blank_img).dontAnimate().into(holder1.newsImageView);
            }
            if (newsObject.get("favicon") == null || newsObject.getString("favicon").equals("null")) {
                holder1.newsFavIcon.setVisibility(View.GONE);
            } else {
                String myUri = newsObject.getString("favicon");
                Glide.with(context).load(myUri).placeholder(R.drawable.ic_blank_img).dontAnimate().into(holder1.newsFavIcon);
            }
            holder1.newsType.setText(newsObject.getString("sport_type").toUpperCase());
            holder1.newsPublished.setText(getPublishedTime(newsObject.getLong("publish_epoch")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getPublishedTime(long epoch) {
        String publishTime = "";
        DateTime dateTime = new DateTime(epoch * 1000);

        DateTime dateTime1 = new DateTime(LocalDate.now(DateTimeZone.forID("Asia/Kolkata")).toDateTimeAtCurrentTime());

        int days = Days.daysBetween(dateTime, dateTime1).getDays();
        int hours = Hours.hoursBetween(dateTime, dateTime1).getHours();
        int minutes = Minutes.minutesBetween(dateTime, dateTime1).getMinutes();
        if (days > 0) {
            publishTime = publishTime.concat(String.valueOf(days) + " day" + (days == 1 ? "" : "s") + " ago");
        } else if (hours > 0) {
            publishTime = publishTime.concat(String.valueOf(hours) + " hour" + (hours == 1 ? "" : "s") + " ago");
        } else if (minutes >= 0) {
            publishTime = publishTime.concat(String.valueOf(minutes) + " minute" + (minutes == 1 ? "" : "s") + " ago");
        } else {
            //do nothing
        }

        return publishTime;
    }

    private void displayHeader(GlobalContentItemObject contentObject, ViewHolder holder1, int position) {
        holder1.view.setTag(position);
        holder1.view.setOnClickListener(onClickListener);
        (holder1).headerCategory.setText((CharSequence) contentObject.getObject());
    }

    private void displayMessages(GlobalContentItemObject contentObject, ViewHolder holder1) {
        holder1.imageView.setVisibility(View.GONE);
        holder1.inviteButton.setVisibility(View.GONE);
        Message message = (Message) contentObject.getObject();
        (holder1).title.setText(SportsUnityDBHelper.getInstance(context).getContact(message.contactID).getName());
        String textData = message.textData;
        textData = textData.replaceAll(keyword, "<font color='#004d99'>" + keyword + "</font>");
        (holder1).subtext.setText(Html.fromHtml(textData));
    }

    private void displayContact(GlobalContentItemObject contentObject, ViewHolder holder1) {
        holder1.inviteButton.setVisibility(View.GONE);
        Contacts contact = (Contacts) contentObject.getObject();
        String name = contact.getName();
        name = name.replaceAll(keyword, "<font color='#004d99'>" + keyword + "</font>");
        (holder1).title.setText(Html.fromHtml(name));
        (holder1).subtext.setText(contact.status);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            GlobalContentItemObject globalContentItemObject = content.get((Integer) view.getTag());
            switch (globalContentItemObject.getType()) {
                case VIEW_TYPE_CONTACT:
                    Contacts contact = (Contacts) globalContentItemObject.getObject();
                    break;
                case VIEW_TYPE_MESSAGE:
                    //TODO
                    break;
                case VIEW_TYPE_NEWS:
                    JSONObject jsonObject = (JSONObject) globalContentItemObject.getObject();
                    try {
                        Intent intent = new Intent(context, NewsDetailsActivity.class);
                        intent.putExtra(Constants.INTENT_KEY_ID, jsonObject.getString("id"));
                        intent.putExtra(Constants.INTENT_KEY_URL, jsonObject.getString("news_link"));
                        intent.putExtra(Constants.INTENT_KEY_TITLE, jsonObject.getString("title"));
                        intent.putExtra(Constants.INTENT_KEY_TYPE, jsonObject.getString("sport_type"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case VIEW_TYPE_HEADER:
                    if (globalContentItemObject.getObject().equals("News")) {
                        Intent intent = new Intent(context, NewsSearchActivity.class);
                        intent.putExtra(GlobalSearchActivity.KEYWORD, keyword);
                        context.startActivity(intent);
                        ((GlobalSearchActivity) context).finish();
                    } else if (globalContentItemObject.getObject().equals("Player Profile")) {
                        ((GlobalSearchActivity) context).performSpecificSearch(keyword, GlobalSearchActivity.PLAYER_TYPE);
                    } else if (globalContentItemObject.getObject().equals("Matches")) {
                        ((GlobalSearchActivity) context).performSpecificSearch(keyword, GlobalSearchActivity.MATCH_TYPE);
                    } else if (globalContentItemObject.getObject().equals("Teams")) {
                        ((GlobalSearchActivity) context).performSpecificSearch(keyword, GlobalSearchActivity.TEAM_TYPE);
                    } else if (globalContentItemObject.getObject().equals("League")) {
                        ((GlobalSearchActivity) context).performSpecificSearch(keyword, GlobalSearchActivity.LEAGUE_TYPE);
                    }
                    break;
                case VIEW_TYPE_TEAM:
                    JSONObject teamObject = (JSONObject) globalContentItemObject.getObject();
                    FavouriteItem item = new FavouriteItem();
                    try {
                        item.setName(teamObject.getString("name"));
                        item.setId(teamObject.getString("id"));
                        item.setFlagImageUrl(teamObject.getString("image"));
                        item.setSportsType(teamObject.getString("sport_type"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    item.setFilterType(Constants.FILTER_TYPE_TEAM);
                    Intent teamIntent = new Intent(context, TeamLeagueDetails.class);
                    teamIntent.putExtra(Constants.INTENT_TEAM_LEAGUE_DETAIL_EXTRA, item.getJsonObject().toString());
                    teamIntent.putExtra(Constants.SPORTS_TYPE_STAFF, false);
                    context.startActivity(teamIntent);
                    break;
                case VIEW_TYPE_LEAGUE:
                    JSONObject leagueObject = (JSONObject) globalContentItemObject.getObject();
                    FavouriteItem leagueItem = new FavouriteItem();
                    try {
                        leagueItem.setName(leagueObject.getString("name"));
                        leagueItem.setId(leagueObject.getString("id"));
                        leagueItem.setFlagImageUrl(leagueObject.getString("image"));
                        leagueItem.setSportsType(leagueObject.getString("sport_type"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    leagueItem.setFilterType(Constants.FILTER_TYPE_TEAM);
                    Intent leagueIntent = new Intent(context, TeamLeagueDetails.class);
                    leagueIntent.putExtra(Constants.INTENT_TEAM_LEAGUE_DETAIL_EXTRA, leagueItem.getJsonObject().toString());
                    context.startActivity(leagueIntent);
                    break;
                case VIEW_TYPE_PLAYER_PROFILE:
                    JSONObject playerObject = (JSONObject) globalContentItemObject.getObject();
                    try {
                        if (playerObject.getString("sport_type").equals("cricket")) {
                            Intent intent = PlayerCricketBioDataActivity.createIntent(context, playerObject.getString("id"), playerObject.getString("name"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, PlayerProfileView.class);
                            intent.putExtra(Constants.INTENT_KEY_ID, playerObject.getString("id"));
                            intent.putExtra(Constants.INTENT_KEY_PLAYER_NAME, playerObject.getString("name"));
                            intent.putExtra(Constants.RESULT_REQUIRED, false);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    @Override
    public int getItemViewType(int position) {
        int viewType = 0;
        switch (content.get(position).getType()) {
            case VIEW_TYPE_CONTACT:
                viewType = VIEW_TYPE_CONTACT;
                break;
            case VIEW_TYPE_MESSAGE:
                viewType = VIEW_TYPE_MESSAGE;
                break;
            case VIEW_TYPE_HEADER:
                viewType = VIEW_TYPE_HEADER;
                break;
            case VIEW_TYPE_NEWS:
                viewType = VIEW_TYPE_NEWS;
                break;
            case VIEW_TYPE_MATCH:
                viewType = VIEW_TYPE_MATCH;
                break;
            case VIEW_TYPE_PLAYER_PROFILE:
                viewType = VIEW_TYPE_PLAYER_PROFILE;
                break;
            case VIEW_TYPE_TEAM:
                viewType = VIEW_TYPE_TEAM;
                break;
            case VIEW_TYPE_LEAGUE:
                viewType = VIEW_TYPE_LEAGUE;
                break;
        }
        return viewType;
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public ImageView imageView;
        public TextView title;
        public TextView subtext;
        public Button inviteButton;

        public TextView newsPublished;
        public TextView newsTitle;
        public TextView newsType;
        public ImageView newsImageView;
        public ImageView newsFavIcon;

        public TextView headerCategory;
        public TextView headerShowAll;

        public TextView homeTeam;
        public TextView awayTeam;
        public TextView matchName;
        public TextView venue;
        public TextView score;
        public TextView matchDay;
        public TextView date;
        public TextView t1overs;
        public TextView t2overs;
        public ImageView homeTeamFlag;
        public ImageView awayTeamFlag;
        public ImageView favoriateTag;
        public LinearLayout showOdds;
        public ImageView notification;
        public ProgressBar matchProgress;


        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            view = itemView;
            if (viewType == VIEW_TYPE_NEWS) {

                newsPublished = (TextView) itemView.findViewById(R.id.published);
                newsTitle = (TextView) itemView.findViewById(R.id.TitleText);
                newsType = (TextView) itemView.findViewById(R.id.type);

                newsImageView = (ImageView) itemView.findViewById(com.sports.unity.R.id.img_url);
                newsFavIcon = (ImageView) itemView.findViewById(R.id.fab_icon);

            } else if (viewType == VIEW_TYPE_MATCH) {
                homeTeam = (TextView) itemView.findViewById(R.id.team1);
                awayTeam = (TextView) itemView.findViewById(R.id.team2);
                score = (TextView) itemView.findViewById(R.id.team_score);
                venue = (TextView) itemView.findViewById(R.id.venue);
                matchName = (TextView) itemView.findViewById(R.id.match_name);
                matchDay = (TextView) itemView.findViewById(R.id.matchDay);
                date = (TextView) itemView.findViewById(R.id.date);
                t1overs = (TextView) itemView.findViewById(R.id.t1over);
                t2overs = (TextView) itemView.findViewById(R.id.t2over);

                homeTeamFlag = (ImageView) itemView.findViewById(R.id.t1flag);
                awayTeamFlag = (ImageView) itemView.findViewById(R.id.t2flag);
                favoriateTag = (ImageView) itemView.findViewById(R.id.star_fav);
                notification = (ImageView) itemView.findViewById(R.id.notification);

                showOdds = (LinearLayout) itemView.findViewById(R.id.show_odds);

                matchProgress = (ProgressBar) itemView.findViewById(R.id.match_progress);

                showOdds.setVisibility(View.GONE);
                notification.setVisibility(View.GONE);


            } else if (viewType == VIEW_TYPE_HEADER) {
                headerCategory = (TextView) itemView.findViewById(R.id.category);
                headerShowAll = (TextView) itemView.findViewById(R.id.show_all);
            } else {

                /**
                 * same for contact, message, player profile , league, team
                 */

                title = (TextView) itemView.findViewById(R.id.contact_name);
                subtext = (TextView) itemView.findViewById(R.id.status);

                imageView = (ImageView) itemView.findViewById(R.id.user_icon);

                inviteButton = (Button) itemView.findViewById(R.id.btn_invite);
            }
        }

    }

    public void updateData(ArrayList<GlobalContentItemObject> content, String keyword) {
        this.content.clear();
        this.content.addAll(content);
        this.notifyDataSetChanged();
        this.keyword = keyword;
    }
}
