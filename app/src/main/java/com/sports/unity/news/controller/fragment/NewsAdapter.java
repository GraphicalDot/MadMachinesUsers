package com.sports.unity.news.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.news.controller.activity.NewsDetailsActivity;
import com.sports.unity.news.model.NewsJsonCaller;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.FirebaseUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by madmachines on 21/8/15.
 */
public class NewsAdapter extends BaseNewsAdapter {

    private NewsJsonCaller newsJsonCaller = new NewsJsonCaller();

    public NewsAdapter(ArrayList<JSONObject> news, Activity activity) {
        super(news, activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView info;
        //  public TextView type;
        public TextView title;
        public TextView source;
        public TextView published;
        public TextView src;
        public TextView pub;
        private TextView type;
        public LinearLayout news_main;
        public ImageView imageView;
        public ImageView fabIcon;

        public ViewHolder(View v) {
            super(v);
            info = (TextView) v.findViewById(com.sports.unity.R.id.info_text);
            title = (TextView) v.findViewById(com.sports.unity.R.id.TitleText);
            published = (TextView) v.findViewById(com.sports.unity.R.id.published);
            type = (TextView) v.findViewById(R.id.type);
            imageView = (ImageView) v.findViewById(com.sports.unity.R.id.img_url);
            fabIcon = (ImageView) v.findViewById(R.id.fab_icon);
            news_main = (LinearLayout) v.findViewById(com.sports.unity.R.id.news);
            Context context = v.getContext();

            info.setTypeface(FontTypeface.getInstance(context).getRobotoRegular());
            title.setTypeface(FontTypeface.getInstance(context).getRobotoSlabRegular());
            published.setTypeface(FontTypeface.getInstance(context).getRobotoRegular());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(com.sports.unity.R.layout.news_cards, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;

        newsJsonCaller.setJsonObject(news.get(position));

        try {
            if (news.size() <= 0) {
                Toast.makeText(activity, "Some error occured, Please try later", Toast.LENGTH_LONG).show();
            } else {
                String text = newsJsonCaller.getSummary();

                if (text.length() > 135) {
                    text = text.substring(0, 135) + "...";
                }

                holder.info.setText(Html.fromHtml(text + " " + "<font color='#004d99'><u>Read More</u></font>"));
                holder.title.setText(newsJsonCaller.getTitle());
                holder.type.setText(newsJsonCaller.getType());
                DateTime dateTime = new DateTime(newsJsonCaller.getPublishEpoch() * 1000);
                DateTime dateTime1 = new DateTime(LocalDate.now(DateTimeZone.forID("Asia/Kolkata")).toDateTimeAtCurrentTime());
                int days = Days.daysBetween(dateTime, dateTime1).getDays();
                int hours = Hours.hoursBetween(dateTime, dateTime1).getHours();
                int minutes = Minutes.minutesBetween(dateTime, dateTime1).getMinutes();
                if (days > 0) {
                    holder.published.setText(String.valueOf(days) + " day" + (days == 1 ? "" : "s") + " ago");
                } else if (hours > 0) {
                    holder.published.setText(String.valueOf(hours) + " hour" + (hours == 1 ? "" : "s") + " ago");
                } else if (minutes >= 0) {
                    holder.published.setText(String.valueOf(minutes) + " minute" + (minutes == 1 ? "" : "s") + " ago");
                } else {
                    holder.published.setText("");
                }

                if (newsJsonCaller.getImage_link() != null && !newsJsonCaller.getImage_link().equals("null")) {
                    holder.imageView.setVisibility(View.VISIBLE);
                    String myUri = newsJsonCaller.getImage_link();
                    Glide.with(activity).load(myUri).placeholder(R.drawable.ic_blank_img).into(holder.imageView);

                } else {
                    holder.imageView.setVisibility(View.GONE);
                }

                if (newsJsonCaller.getFabIcon_link() != null && !newsJsonCaller.getFabIcon_link().equals("null")) {
                    holder.fabIcon.setVisibility(View.VISIBLE);
                    String myUri = newsJsonCaller.getFabIcon_link();
                    Glide.with(activity).load(myUri).into(holder.fabIcon);
                } else {
                    holder.imageView.setVisibility(View.GONE);
                }
            }

            holder.news_main.setTag(position);
            holder.news_main.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    newsJsonCaller.setJsonObject(news.get(position));

                    try {
                        String newsLink = newsJsonCaller.getNewsLink();
                        String title = newsJsonCaller.getTitle();
                        String type = CommonUtil.capitalize(newsJsonCaller.getType());
                        //FIREBASE INTEGRATION
                        {
                            FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(activity);
                            Bundle bundle = new Bundle();
                            String name = title;
                            String newsId = newsJsonCaller.getNewsId();
                            bundle.putString(FirebaseUtil.Param.NAME, FirebaseUtil.trimValue(name));
                            bundle.putString(FirebaseUtil.Param.ID, FirebaseUtil.trimValue(newsId));
                            bundle.putString(FirebaseUtil.Param.SPORTS_TYPE, type);
                            FirebaseUtil.logEvent(firebaseAnalytics, bundle, FirebaseUtil.Event.NEWS_DETAIL);
                        }
                        Intent intent = new Intent(activity, NewsDetailsActivity.class);
                        intent.putExtra(Constants.INTENT_KEY_ID, newsJsonCaller.getNewsId());
                        intent.putExtra(Constants.INTENT_KEY_URL, newsLink);
                        intent.putExtra(Constants.INTENT_KEY_TITLE, title);
                        intent.putExtra(Constants.INTENT_KEY_TYPE, type);
                        activity.startActivity(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
