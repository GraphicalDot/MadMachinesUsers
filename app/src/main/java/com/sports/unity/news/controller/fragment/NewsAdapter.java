package com.sports.unity.news.controller.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

/**
 * Created by madmachines on 21/8/15.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<News> news = null;
    private Activity activity;

    public NewsAdapter(ArrayList<News> news, Activity activity) {
        this.news = news;
        this.activity = activity;
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
        public LinearLayout news_main;
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
            news_main = (LinearLayout) v.findViewById(com.sports.unity.R.id.news);
          //  type = (TextView) v.findViewById(R.id.type);
            Context context = v.getContext();

            info.setTypeface(FontTypeface.getInstance(context).getRobotoLight());
            title.setTypeface(FontTypeface.getInstance(context).getRobotoSlabRegular());
            source.setTypeface(FontTypeface.getInstance(context).getRobotoRegular());
            published.setTypeface(FontTypeface.getInstance(context).getRobotoRegular());
            src.setTypeface(FontTypeface.getInstance(context).getRobotoRegular());
            pub.setTypeface(FontTypeface.getInstance(context).getRobotoRegular());
        }
    }

    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(com.sports.unity.R.layout.news_cards, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, final int position) {
        if ( news.size() <= 0 ) {
            Toast.makeText(activity, "Some error occured Please try later", Toast.LENGTH_LONG).show();
        } else {
            String text = news.get(position).getSummary();

            if (text.length() > 135) {
                text = text.substring(0, 135) + "...";
            }
            //result.setText(Html.fromHtml(text+"<font color='red'> <u>View More</u></font>"));
            holder.info.setText(Html.fromHtml(text + " " + "<font color='#2c84cc'><u>Read More</u></font>"));
            holder.title.setText(news.get(position).getTitle());
         //   holder.type.setText(CommonUtil.capitalize(news.get(position).getType()));
            holder.source.setText(news.get(position).getWebsite());
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
            }
            if (news.get(position).getHdpi() != null && !news.get(position).getHdpi().equals("null")) {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageView.setTag(news.get(position).getHdpi());
                String myUri = news.get(position).getHdpi();
                Picasso.with(activity).load(myUri).into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.GONE);
            }
        }
        holder.news_main.setOnClickListener(new View.OnClickListener() {
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
