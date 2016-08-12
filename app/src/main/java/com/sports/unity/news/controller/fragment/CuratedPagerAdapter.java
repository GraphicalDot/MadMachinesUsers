package com.sports.unity.news.controller.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sports.unity.R;
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
 * Created by Mad on 09-Aug-16.
 */
public class CuratedPagerAdapter extends PagerAdapter {

    private Context context;
    ArrayList<JSONObject> curatedNewsList;
    private NewsJsonCaller newsJsonCaller = new NewsJsonCaller();

    public CuratedPagerAdapter(Context context, ArrayList<JSONObject> curatedNewsList) {
        this.context = context;
        this.curatedNewsList = curatedNewsList;
    }

    @Override
    public int getCount() {
        return curatedNewsList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        newsJsonCaller.setJsonObject(curatedNewsList.get(position));
        final FrameLayout curatedView = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.curated_content, null);
        final ImageView curatedImage = (ImageView) curatedView.findViewById(R.id.curated_image);
        TextView title = (TextView) curatedView.findViewById(R.id.title);
        TextView type = (TextView) curatedView.findViewById(R.id.sports_type);
        TextView published = (TextView) curatedView.findViewById(R.id.published);
        try {
            Glide.with(context).load(Uri.parse(newsJsonCaller.getImage_link())).dontAnimate().placeholder(R.drawable.img_curated_news_plchldr).into(curatedImage);
            title.setText(newsJsonCaller.getTitle());
            type.setText(newsJsonCaller.getType());
            DateTime dateTime = new DateTime(newsJsonCaller.getPublishEpoch() * 1000);
            DateTime dateTime1 = new DateTime(LocalDate.now(DateTimeZone.forID("Asia/Kolkata")).toDateTimeAtCurrentTime());
            int days = Days.daysBetween(dateTime, dateTime1).getDays();
            int hours = Hours.hoursBetween(dateTime, dateTime1).getHours();
            int minutes = Minutes.minutesBetween(dateTime, dateTime1).getMinutes();
            if (days > 0) {
                published.setText(String.valueOf(days) + " day" + (days == 1 ? "" : "s") + " ago");
            } else if (hours > 0) {
                published.setText(String.valueOf(hours) + " hour" + (hours == 1 ? "" : "s") + " ago");
            } else if (minutes >= 0) {
                published.setText(String.valueOf(minutes) + " minute" + (minutes == 1 ? "" : "s") + " ago");
            } else {
                published.setText("");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        curatedView.setTag(position);
        curatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int position = (Integer) v.getTag();
                    newsJsonCaller.setJsonObject(curatedNewsList.get(position));
                    String newsLink = newsJsonCaller.getNewsLink();
                    String title = newsJsonCaller.getTitle();
                    String type = CommonUtil.capitalize(newsJsonCaller.getType());

                    Intent intent = new Intent(context, NewsDiscussActivity.class);
                    intent.putExtra(Constants.INTENT_KEY_ID, newsJsonCaller.getNewsId());
                    intent.putExtra(Constants.INTENT_KEY_URL, newsLink);
                    intent.putExtra(Constants.INTENT_KEY_TITLE, title);
                    intent.putExtra(Constants.INTENT_KEY_TYPE, type);
                    context.startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        container.addView(curatedView);
        return curatedView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        super.getPageTitle(position);
        return "page";
    }
}
