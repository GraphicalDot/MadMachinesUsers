package com.sports.unity.news.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.news.controller.activity.NewsDetailsActivity;
import com.sports.unity.news.controller.activity.NewsDiscussActivity;
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
 * Created by madmachines on 23/9/15.
 */
public class NewsMinicardAdapter extends RecyclerView.Adapter<NewsMinicardAdapter.ViewHolder> {

    private NewsJsonCaller newsJsonCaller = new NewsJsonCaller();
    private final int VIEW_TYPE_BANNER = 0;
    private final int VIEW_TYPE_NEWS = 1;
    private final int VIEW_TYPE_CURATED_NEWS = 2;
    private final String CURATED_NEWS = "curated";
    private Activity activity;
    private ArrayList<JSONObject> newsList;
    private ArrayList<JSONObject> curatedNewsList;
    CuratedPagerAdapter curatedPagerAdapter;

    public NewsMinicardAdapter(ArrayList<JSONObject> newsList, ArrayList<JSONObject> curatedNewsList, Activity activity) {
        this.activity = activity;
        this.newsList = newsList;
        this.curatedNewsList = curatedNewsList;
        curatedPagerAdapter = new CuratedPagerAdapter(activity, curatedNewsList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView published;
        private TextView title;
        private TextView type;
        private ImageView imageView;
        private ImageView fabIcon;
        private LinearLayout news_mini;
        private ViewPager curatedBannerPager;
        private SlidingTabLayout slidingTabLayout;

        public ViewHolder(View v, boolean isBanner) {
            super(v);
            if (isBanner) {
                curatedBannerPager = (ViewPager) v.findViewById(R.id.curated_pager);
                slidingTabLayout = (SlidingTabLayout) v.findViewById(R.id.tabs);
            } else {
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
    }

    private void initCuratedNews(ViewHolder holder) {
        holder.curatedBannerPager.setAdapter(curatedPagerAdapter);
        holder.slidingTabLayout.setDistributeEvenly(true);
        holder.slidingTabLayout.setCustomTabView(R.layout.curated_tab, R.id.tab_text);
        holder.slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return activity.getResources().getColor(R.color.ColorPrimary);
            }
        });
        holder.slidingTabLayout.setSelectedIndicatorColors(activity.getResources().getColor(R.color.ColorPrimary));
        holder.slidingTabLayout.setViewPager(holder.curatedBannerPager);
    }


    @Override
    public NewsMinicardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_BANNER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.curated_news_pager, parent, false);
            return new ViewHolder(view, true);
        } else if (viewType == VIEW_TYPE_NEWS) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_minicard, parent, false);
            return new ViewHolder(view, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_curated_card, parent, false);
            return new ViewHolder(view, false);

        }
    }

    @Override
    public void onBindViewHolder(NewsMinicardAdapter.ViewHolder holder, final int position) {
        if (getItemViewType(position) == VIEW_TYPE_BANNER) {
            initCuratedNews(holder);
        } else {
            int pos = 0;
            if (curatedNewsList.isEmpty()) {
                pos = position;
            } else {
                pos = position - 1;
            }
            newsJsonCaller.setJsonObject(newsList.get(pos));
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

                holder.news_mini.setTag(pos);
                holder.news_mini.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        int position = (Integer) view.getTag();
                        newsJsonCaller.setJsonObject(newsList.get(position));

                        try {
                            String newsLink = newsJsonCaller.getNewsLink();
                            String title = newsJsonCaller.getTitle();
                            String type = CommonUtil.capitalize(newsJsonCaller.getType());

                            Intent intent;
                            String newsType = newsJsonCaller.getCuratedType();
                            if (!TextUtils.isEmpty(newsType) && newsType.equalsIgnoreCase(CURATED_NEWS)) {
                                intent = new Intent(activity, NewsDiscussActivity.class);
                                //FIREBASE INTEGRATION
                                {
                                    FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(activity);
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseUtil.Param.ARTICLE_ID, newsJsonCaller.getNewsId());
                                    FirebaseUtil.logEvent(firebaseAnalytics, bundle, FirebaseUtil.Event.CURATED_NEWS_CLICK);
                                }
                            } else {
                                intent = new Intent(activity, NewsDetailsActivity.class);
                            }
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

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        if (position == 0 && !curatedNewsList.isEmpty()) {
            return VIEW_TYPE_BANNER;
        } else {
            int pos = 0;
            if (curatedNewsList.isEmpty()) {
                pos = position;
            } else {
                pos = position - 1;
            }
            newsJsonCaller.setJsonObject(newsList.get(pos));
            String newsType = newsJsonCaller.getCuratedType();
            if (!TextUtils.isEmpty(newsType) && newsType.equalsIgnoreCase(CURATED_NEWS)) {
                return VIEW_TYPE_CURATED_NEWS;
            } else {
                return VIEW_TYPE_NEWS;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (curatedNewsList.isEmpty()) {
            return newsList.size();
        } else {
            return newsList.size() + 1;
        }
    }

    public ArrayList<JSONObject> getNews() {
        return newsList;
    }

    public void updateAdapter() {
        this.notifyDataSetChanged();
        this.curatedPagerAdapter.notifyDataSetChanged();
    }
}

