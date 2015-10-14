package com.sports.unity.news.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;

import java.util.ArrayList;

/**
 * Created by madmachines on 21/8/15.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<String> mDataset = null;
    private ArrayList<String> titleText = null;
    private ArrayList<String> imgurl = null;
    private ArrayList<String> newsLink = null;
    private ArrayList<Long> published = null;
    private ArrayList<String> website = null;
    static Context context;
    Activity activity;
    private static Typeface robotoSlabRegular;
    private static Typeface robotoRegular;
    private static Typeface robotoLight;

    public NewsAdapter(ArrayList<String> myDataset, ArrayList<String> Title,
                       ArrayList<String> image_url, ArrayList<Long> published,
                       ArrayList<String> website, ArrayList<String> newsLink, Context applicationContext, Activity activity) {

        this.mDataset = myDataset;
        this.titleText = Title;
        this.imgurl = image_url;
        this.published = published;
        this.context = applicationContext;
        this.activity = activity;
        this.newsLink = newsLink;
        this.website = website;
        robotoSlabRegular = Typeface.createFromAsset(context.getAssets(), "RobotoSlab-Regular.ttf");
        robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        robotoLight = Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView info;
        public TextView title;
        public TextView source;
        public TextView published;
        public TextView src;
        public TextView pub;
        public LinearLayout news;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            info = (TextView) v.findViewById(com.sports.unity.R.id.info_text);
            title = (TextView) v.findViewById(com.sports.unity.R.id.TitleText);
            source = (TextView) v.findViewById(com.sports.unity.R.id.source);
            published = (TextView) v.findViewById(com.sports.unity.R.id.published);
            src = (TextView) v.findViewById(com.sports.unity.R.id.src);
            pub = (TextView) v.findViewById(com.sports.unity.R.id.pub);
            imageView = (ImageView) v.findViewById(com.sports.unity.R.id.img_url);
            news = (LinearLayout) v.findViewById(com.sports.unity.R.id.news);
            info.setTypeface(robotoLight);
            title.setTypeface(robotoSlabRegular);
            source.setTypeface(robotoRegular);
            published.setTypeface(robotoRegular);
            src.setTypeface(robotoRegular);
            pub.setTypeface(robotoRegular);
        }
    }


    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(com.sports.unity.R.layout.news_cards, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, final int position) {
        if (mDataset.size() <= 0) {
            Toast.makeText(activity, "Some error occured Please try later", Toast.LENGTH_LONG).show();

        } else {

            String text = mDataset.get(position);
            if (text.length() > 135)

                text = text.substring(0, 135) + "...";
            //result.setText(Html.fromHtml(text+"<font color='red'> <u>View More</u></font>"));
            holder.info.setText(Html.fromHtml(text + " " + "<font color='#2c84cc'><u>Read More</u></font>"));
            holder.title.setText(titleText.get(position));
            holder.source.setText(newsLink.get(position));

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

        }

        holder.news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                myWebLink.setData(Uri.parse(website.get(position)));
                activity.startActivity(myWebLink);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
