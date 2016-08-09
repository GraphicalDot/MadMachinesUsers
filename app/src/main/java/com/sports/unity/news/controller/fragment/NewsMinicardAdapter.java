package com.sports.unity.news.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.news.controller.activity.NewsDetailsActivity;
import com.sports.unity.news.controller.activity.NewsDiscussActivity;
import com.sports.unity.news.model.NewsJsonCaller;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by madmachines on 23/9/15.
 */
public class NewsMinicardAdapter extends BaseNewsAdapter {

    private NewsJsonCaller newsJsonCaller = new NewsJsonCaller();

    public NewsMinicardAdapter(ArrayList<JSONObject> news, Activity activity) {
        super(news, activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView published;
        private TextView title;
        private TextView type;
        private ImageView imageView;
        private ImageView fabIcon;
        private LinearLayout news_mini;

        public ViewHolder(View v) {
            super(v);

            published = (TextView) v.findViewById(com.sports.unity.R.id.published);
            type = (TextView) v.findViewById(R.id.type);
            title = (TextView) v.findViewById(com.sports.unity.R.id.TitleText);
            imageView = (ImageView) v.findViewById(com.sports.unity.R.id.img_url);
            fabIcon = (ImageView) v.findViewById(R.id.fab_icon);
            news_mini = (LinearLayout) v.findViewById(com.sports.unity.R.id.news_mini);

            published.setTypeface(FontTypeface.getInstance(v.getContext()).getRobotoRegular());
            title.setTypeface(FontTypeface.getInstance(v.getContext()).getRobotoSlabRegular());

        }
    }

    @Override
    public NewsMinicardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_minicard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        NewsMinicardAdapter.ViewHolder holder = (NewsMinicardAdapter.ViewHolder) viewHolder;

        newsJsonCaller.setJsonObject(news.get(position));

        try {
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

            holder.news_mini.setTag(position);
            holder.news_mini.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int position = (Integer) view.getTag();
                    newsJsonCaller.setJsonObject(news.get(position));

                    try {
                        String newsLink = newsJsonCaller.getNewsLink();
                        String title = newsJsonCaller.getTitle();
                        String type = CommonUtil.capitalize(newsJsonCaller.getType());

                        Intent intent = new Intent(activity, NewsDetailsActivity.class);
                        intent.putExtra(Constants.INTENT_KEY_ID, newsJsonCaller.getNewsId());
                        intent.putExtra(Constants.INTENT_KEY_URL, newsLink);
                        intent.putExtra(Constants.INTENT_KEY_TITLE, title);
                        intent.putExtra(Constants.INTENT_KEY_TYPE, type);
                        activity.startActivity(intent);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
//                    Intent intent = new Intent(activity, NewsDiscussActivity.class);
//                    activity.startActivity(intent);
                }

            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
