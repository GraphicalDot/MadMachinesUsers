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

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.news.controller.activity.NewsDetailsActivity;
import com.sports.unity.news.model.News;
import com.sports.unity.util.CommonUtil;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by madmachines on 23/9/15.
 */
public class NewsMinicardAdapter extends RecyclerView.Adapter<NewsMinicardAdapter.ViewHolder> {

    private ArrayList<News> news = null;
    private Activity activity;

    public NewsMinicardAdapter(ArrayList<News> news, Activity activity) {
        this.activity = activity;
        this.news = news;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView published;
        private TextView title;
        private TextView type;
        private ImageView imageView;
        private LinearLayout news_mini;

        public ViewHolder(View v) {
            super(v);

            published = (TextView) v.findViewById(com.sports.unity.R.id.published);
            type = (TextView) v.findViewById(R.id.type);
            title = (TextView) v.findViewById(com.sports.unity.R.id.TitleText);
            imageView = (ImageView) v.findViewById(com.sports.unity.R.id.img_url);
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
    public void onBindViewHolder(NewsMinicardAdapter.ViewHolder holder, final int position) {
        holder.title.setText(news.get(position).getTitle());
        holder.type.setText(CommonUtil.capitalize(news.get(position).getType()));

        DateTime dateTime = new DateTime(news.get(position).getPublishEpoch() * 1000);

        DateTime dateTime1 = new DateTime(LocalDate.now(DateTimeZone.forID("Asia/Kolkata")).toDateTimeAtCurrentTime());

        int days = Days.daysBetween(dateTime, dateTime1).getDays();
        int hours = Hours.hoursBetween(dateTime, dateTime1).getHours();
        int minutes = Minutes.minutesBetween(dateTime, dateTime1).getMinutes();
        if (days > 0) {
            holder.published.setText(String.valueOf(days) + " day" + ( days==1 ? "":"s" ) +" ago");
        } else if (hours > 0) {
            holder.published.setText(String.valueOf(hours) + " hour" + ( hours==1 ? "":"s" ) +" ago");
        } else if (minutes >= 0) {
            holder.published.setText(String.valueOf(minutes) + " minute" + ( minutes==1 ? "":"s" ) +" ago");
        } else {
            holder.published.setText("");
        }

        if (news.get(position).getImage_link() != null && !news.get(position).getImage_link().equals("null")) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setTag(news.get(position).getImage_link());
            String myUri = news.get(position).getImage_link();
            Picasso.with(activity).load(myUri).into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        holder.news_mini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(activity, NewsDetailsActivity.class);
                browserIntent.putExtra("Url", news.get(position).getNewsLink());
                browserIntent.putExtra("title", news.get(position).getTitle());
                activity.startActivity(browserIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return news.size();
    }

}
