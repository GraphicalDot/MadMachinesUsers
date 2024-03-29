package com.sports.unity.news.controller.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.R;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.common.viewhelper.VolleyCallComponentHelper;
import com.sports.unity.news.model.NewsJsonCaller;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.JsonObjectCaller;
import com.sports.unity.util.network.FirebaseUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class NewsDetailsActivity extends CustomVolleyCallerActivity {

    private static final String REQUEST_LISTENER_KEY = "news_detail_listener";
    private static final String NEWS_DETAIL_REQUEST_TAG = "news_detail_request_tag";

    private String id = null;
    private String title = null;
    private String shareContent = null;

    private JSONObject newsJsonObject = null;

    private ImageView refresh = null;
    private ImageView share = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news_details);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            decodeDataFromURL(data);
        }
        initView();
        onComponentCreate();
        requestNewsDetail();
    }

    private void decodeDataFromURL(String uri) {
        String jsonString = uri.substring(uri.lastIndexOf("/") + 1);
        String data = null;
        String title = null;
        String type = null;
        String id = null;
        boolean isCurated;
        try {
            data = URLDecoder.decode(jsonString, "UTF-8");
            JSONObject object = new JSONObject(data);
            title = object.getString(Constants.INTENT_KEY_TITLE);
            type = object.getString(Constants.INTENT_KEY_TYPE);
            id = object.getString(Constants.INTENT_KEY_ID);
            isCurated = object.getBoolean(Constants.INTENT_KEY_CURATED);
            if (isCurated) {
                Intent intent = new Intent(this, NewsDiscussActivity.class);
                intent.putExtra(Constants.INTENT_KEY_ID, id);
                intent.putExtra(Constants.INTENT_KEY_TITLE, title);
                intent.putExtra(Constants.INTENT_KEY_TYPE, type);
                startActivity(intent);
                finish();
            } else {
                getIntent().putExtra(Constants.INTENT_KEY_ID, id);
                getIntent().putExtra(Constants.INTENT_KEY_TITLE, title);
                getIntent().putExtra(Constants.INTENT_KEY_TYPE, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfDeepLinked(Intent intent) {
        boolean isDeepLinked = false;
        try {
            isDeepLinked = intent.getAction().equalsIgnoreCase(Intent.ACTION_VIEW);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDeepLinked;
    }

    @Override
    public VolleyCallComponentHelper getVolleyCallComponentHelper() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        ViewGroup errorLayout = (ViewGroup) findViewById(R.id.error);
        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper(REQUEST_LISTENER_KEY, new NewsDetailComponentListener(progressBar, errorLayout));
        return volleyCallComponentHelper;
    }

    private void initView() {
        id = getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        String title = getIntent().getStringExtra(Constants.INTENT_KEY_TITLE);

        setToolBar(title);
    }

    private void setToolBar(final String title) {
        Toolbar toolbar = (Toolbar) findViewById(com.sports.unity.R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView tv = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tv.setText(title);
        tv.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());

        ImageView img = (ImageView) toolbar.findViewById(R.id.img);
        img.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        img.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finish();
            }

        });

        refresh = (ImageView) toolbar.findViewById(R.id.refresh);
        refresh.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        refresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                requestNewsDetail();
            }

        });

        share = (ImageView) toolbar.findViewById(R.id.share);
        share.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        share.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                //FIREBASE INTEGRATION
                {
                    FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(NewsDetailsActivity.this);
                    Bundle bundle = new Bundle();
                    String name = title;
                    String type = getIntent().getStringExtra(Constants.INTENT_KEY_TYPE);
                    bundle.putString(FirebaseUtil.Param.NAME, FirebaseUtil.trimValue(name));
                    bundle.putString(FirebaseUtil.Param.ID, FirebaseUtil.trimValue(id));
                    bundle.putString(FirebaseUtil.Param.SPORTS_TYPE, type);
                    FirebaseUtil.logEvent(firebaseAnalytics, bundle, FirebaseUtil.Event.NEWS_SHARE);
                }

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }

        });
    }

    private boolean handleNewsDetailResponse(String response) {
        boolean success = false;
        try {
            JSONObject responseJson = new JSONObject(response);
            if (responseJson.getBoolean("success")) {
                newsJsonObject = responseJson.getJSONObject("result");

                success = true;
            } else {
                success = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private String getNewsUrl(NewsJsonCaller caller) {
        String url = getResources().getString(R.string.news_url);
        JSONObject object = new JSONObject();
        try {
            object.put(Constants.INTENT_KEY_ID, caller.getNewsId());
            object.put(Constants.INTENT_KEY_TITLE, caller.getTitle());
            object.put(Constants.INTENT_KEY_TYPE, CommonUtil.capitalize(caller.getType()));
            object.put(Constants.INTENT_KEY_CURATED, false);
            url = url + URLEncoder.encode(object.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private boolean renderResponse() {
        boolean success = false;
        if (newsJsonObject != null) {

            TextView titleText = (TextView) findViewById(R.id.TitleText);
            TextView sportType = (TextView) findViewById(R.id.type);

            TextView infoData = (TextView) findViewById(R.id.info_text);
            TextView published = (TextView) findViewById(R.id.published);

            ImageView image = (ImageView) findViewById(R.id.img_url);
            ImageView fabIcon = (ImageView) findViewById(R.id.fab_icon);

            infoData.setTypeface(FontTypeface.getInstance(this).getRobotoLight());
            titleText.setTypeface(FontTypeface.getInstance(this).getRobotoSlabRegular());
            published.setTypeface(FontTypeface.getInstance(this).getRobotoRegular());

            NewsJsonCaller newsJsonCaller = new NewsJsonCaller();
            newsJsonCaller.setJsonObject(newsJsonObject);

            try {
                title = newsJsonCaller.getTitle();
                shareContent = newsJsonCaller.getTitle() + "\n\n" + getNewsUrl(newsJsonCaller);

                infoData.setText(newsJsonCaller.getNews());

                titleText.setText(title);
                sportType.setText(newsJsonCaller.getType());

                {
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
                }

                if (newsJsonCaller.getImage_link() != null && !newsJsonCaller.getImage_link().equals("null")) {
                    image.setVisibility(View.VISIBLE);
                    String myUri = newsJsonCaller.getImage_link();
                    Glide.with(this).load(myUri).into(image);
                } else {
                    image.setVisibility(View.GONE);
                }

                if (newsJsonCaller.getFabIcon_link() != null && !newsJsonCaller.getFabIcon_link().equals("null")) {
                    fabIcon.setVisibility(View.VISIBLE);
                    String myUri = newsJsonCaller.getFabIcon_link();
                    Glide.with(this).load(myUri).into(fabIcon);
                } else {
                    fabIcon.setVisibility(View.GONE);
                }

                findViewById(R.id.news_layout).setVisibility(View.VISIBLE);
                success = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            //nothing
        }

        return success;
    }

    private void requestNewsDetail() {
        Log.i("News Detail", "Request news Details");

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(ScoresContentHandler.PARAM_NEWS_IMAGE_DPI, "hdpi");
        parameters.put(ScoresContentHandler.PARAM_NEWS_ID, id);
        requestContent(ScoresContentHandler.CALL_NAME_NEWS_DETAIL, parameters, NEWS_DETAIL_REQUEST_TAG);
    }

    private class NewsDetailComponentListener extends CustomComponentListener {

        public NewsDetailComponentListener(ProgressBar progressBar, ViewGroup errorLayout) {
            super(NEWS_DETAIL_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public void handleErrorContent(String tag) {
            //nothing
        }

        @Override
        protected void showErrorLayout() {
            super.showErrorLayout();

            refresh.setVisibility(View.VISIBLE);
            share.setVisibility(View.GONE);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            return NewsDetailsActivity.this.handleNewsDetailResponse(content);
        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderResponse();
            if (!success) {
                showErrorLayout();
            } else {
                share.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
            }
        }

    }

}
