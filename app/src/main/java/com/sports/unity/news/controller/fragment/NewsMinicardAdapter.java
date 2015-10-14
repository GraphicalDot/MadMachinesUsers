package com.sports.unity.news.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sports.unity.R;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;

import java.util.ArrayList;

/**
 * Created by madmachines on 23/9/15.
 */
public class NewsMinicardAdapter extends RecyclerView.Adapter<NewsMinicardAdapter.ViewHolder> {

    private ArrayList<Long> published = null;
    private ArrayList<String> titleText = null;
    private ArrayList<String> imgurl = null;
    private ArrayList<String> mDataset = null;
    private ArrayList<String> newsLink = null;
    private static Typeface robotoSlabRegular;
    private static Typeface robotoRegular;

    static Context context;
    Activity activity;


    public NewsMinicardAdapter(ArrayList<String> mDataset, ArrayList<String> Title, ArrayList<String> image_url, ArrayList<Long> published, ArrayList<String> newsLink, Context applicationContext, Activity activity) {

        this.mDataset = mDataset;
        this.published = published;
        this.titleText = Title;
        this.imgurl = image_url;
        this.context = applicationContext;
        this.activity = activity;
        this.newsLink = newsLink;
        robotoSlabRegular = Typeface.createFromAsset(context.getAssets(), "RobotoSlab-Regular.ttf");
        robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView published;
        public TextView title;
        public ImageView imageView;
        public LinearLayout news_mini;

        public ViewHolder(View v) {
            super(v);


            published = (TextView) v.findViewById(com.sports.unity.R.id.published);
            title = (TextView) v.findViewById(com.sports.unity.R.id.TitleText);
            imageView = (ImageView) v.findViewById(com.sports.unity.R.id.img_url);
            news_mini = (LinearLayout) v.findViewById(com.sports.unity.R.id.news_mini);
            published.setTypeface(robotoRegular);
            title.setTypeface(robotoSlabRegular);

        }
    }


    @Override
    public NewsMinicardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_minicard, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(NewsMinicardAdapter.ViewHolder holder, final int position) {


        holder.title.setText(titleText.get(position));
        DateTime dateTime = new DateTime(published.get(position) * 1000);
        DateTime dateTime1 = new DateTime(LocalDate.now(DateTimeZone.forID("Asia/Kolkata")).toDateTimeAtCurrentTime());
        int days = Days.daysBetween(dateTime, dateTime1).getDays();
        int hours = Hours.hoursBetween(dateTime, dateTime1).getHours();
        int minutes = Minutes.minutesBetween(dateTime, dateTime1).getMinutes();
        if (days > 0)
            holder.published.setText(String.valueOf(days) + " days ago");
        else if (hours > 0)
            holder.published.setText(String.valueOf(hours) + " hours ago");
        else if (minutes >= 0)
            holder.published.setText(String.valueOf(minutes) + " minutes ago");
        if (imgurl.get(position) != null && !imgurl.get(position).equals("null")) {
            holder.imageView.setTag(imgurl.get(position));
            String myUri = imgurl.get(position);
            Picasso.with(context).load(myUri).into(holder.imageView);
        } else
            holder.imageView.setVisibility(View.GONE);
        holder.news_mini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                myWebLink.setData(Uri.parse(newsLink.get(position)));
                activity.startActivity(myWebLink);
            }
        });
    }


    @Override
    public int getItemCount() {

        return mDataset.size();
    }

}
