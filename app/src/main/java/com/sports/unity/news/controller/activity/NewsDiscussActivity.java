package com.sports.unity.news.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.common.viewhelper.VolleyCallComponentHelper;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.news.model.NewsJsonCaller;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
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
import java.util.HashMap;

public class NewsDiscussActivity extends CustomVolleyCallerActivity {

    private static final String REQUEST_LISTENER_KEY = "news_discuss_detail_listener";
    private static final String NEWS_DETAIL_REQUEST_TAG = "news_discuss_detail_request_tag";

    private String id = null;
    private JSONObject newsJsonObject = null;
    private RelativeLayout newsLayout = null;
    private ImageView newsAvatar = null;

    private String shareContent = null;
    private ImageView share = null;

    private String title = null;
    private String pollQuestion = null;
    private String groupName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_discuss);

        initToolbar();
        initViews();

        onComponentCreate();
        requestNewsDetail();

    }

    @Override
    public VolleyCallComponentHelper getVolleyCallComponentHelper() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        ViewGroup errorLayout = (ViewGroup) findViewById(R.id.error);
        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper(REQUEST_LISTENER_KEY, new NewsDetailComponentListener(progressBar, errorLayout));
        return volleyCallComponentHelper;
    }

    private void initViews() {
        newsAvatar = (ImageView) findViewById(R.id.slant_view);
        id = getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        newsLayout = (RelativeLayout) findViewById(R.id.root);
        boolean isNotification = getIntent().getBooleanExtra(Constants.INTENT_KEY_NOTIFICATION, false);
        if (isNotification) {
            logFirebaseEvent(FirebaseUtil.Event.NEWS_NOTIFICATION_CLICK);
        }
    }

    public void onPole(View view) {

        Intent intent = new Intent(getApplicationContext(), PollActivity.class);
        intent.putExtra(Constants.INTENT_KEY_ID, id);
        intent.putExtra(Constants.INTENT_POLL_QUESTION, pollQuestion);
        intent.putExtra(Constants.INTENT_GROUP_NAME, groupName);
        boolean articleExists = SportsUnityDBHelper.getInstance(getApplicationContext()).articleIdExistsOrNot(id);
        if (articleExists) {
            String groupJID = SportsUnityDBHelper.getInstance(getApplicationContext()).groupJIDExistsOrNot(id);
            if (null == groupJID) {
                boolean poll = SportsUnityDBHelper.getInstance(getApplicationContext()).getPoll(id);
                intent.putExtra(Constants.INTENT_POLL_PARRY, true);
                intent.putExtra(Constants.INTENT_POLL_STATUS, poll);
                startActivity(intent);
            } else {
                SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(getApplicationContext());
                Contacts contact = sportsUnityDBHelper.getContactByJid(groupJID);
                Intent openChatScreen = ChatScreenActivity.createChatScreenIntent(getApplicationContext(), true, groupJID, contact.getName(), sportsUnityDBHelper.getChatEntryID(groupJID), contact.image, false, false, Contacts.AVAILABLE_BY_MY_CONTACTS, "");
                startActivity(openChatScreen);

            }
        } else {
            logFirebaseEvent(FirebaseUtil.Event.DISCUSS_CLICK);
            startActivity(intent);
        }
    }

    private void logFirebaseEvent(String eventName) {
        //FIREBASE INTEGRATION
        FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(getApplicationContext());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseUtil.Param.ARTICLE_ID, id);
        FirebaseUtil.logEvent(firebaseAnalytics, bundle, eventName);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        share = (ImageView) toolbar.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
        share.setVisibility(View.GONE);

        ImageView backButton = (ImageView) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsDiscussActivity.this.finish();
            }
        });
    }

    private void requestNewsDetail() {
        Log.i("News Detail", "Request news Details");
        newsLayout.setVisibility(View.GONE);
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(ScoresContentHandler.PARAM_NEWS_IMAGE_DPI, "hdpi");
        parameters.put(ScoresContentHandler.PARAM_NEWS_ID, id);
        requestContent(ScoresContentHandler.CALL_NAME_NEWS_DETAIL, parameters, NEWS_DETAIL_REQUEST_TAG);
    }

    private String getNewsUrl(NewsJsonCaller caller) {
        String url = getResources().getString(R.string.news_url);
        JSONObject object = new JSONObject();
        try {
            object.put(Constants.INTENT_KEY_ID, caller.getNewsId());
            object.put(Constants.INTENT_KEY_TITLE, caller.getTitle());
            object.put(Constants.INTENT_KEY_TYPE, CommonUtil.capitalize(caller.getType()));
            object.put(Constants.INTENT_KEY_CURATED, true);
            url = url + URLEncoder.encode(object.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private boolean renderResponse() {
        boolean success = false;
        newsLayout.setVisibility(View.VISIBLE);
        if (newsJsonObject != null) {

            TextView newsTitle = (TextView) findViewById(R.id.news_title);
            TextView sportsType = (TextView) findViewById(R.id.sports_type);
            TextView time = (TextView) findViewById(R.id.time);
            TextView content = (TextView) findViewById(R.id.content);

            NewsJsonCaller newsJsonCaller = new NewsJsonCaller();
            newsJsonCaller.setJsonObject(newsJsonObject);

            try {

                title = newsJsonCaller.getTitle();

                if (newsJsonCaller.getImage_link() != null && !newsJsonCaller.getImage_link().equals("null")) {
                    String myUri = newsJsonCaller.getImage_link();
                    Glide.with(this).load(myUri).into(newsAvatar);
                } else {
                    //do nothing
                }

                newsTitle.setText(newsJsonCaller.getTitle());
                sportsType.setText(newsJsonCaller.getType());
                time.setText(getTime(newsJsonCaller.getPublishEpoch()));
                shareContent = newsJsonCaller.getTitle() + "\n\n" + getNewsUrl(newsJsonCaller);
                pollQuestion = newsJsonCaller.getPollQuestion();
                groupName = newsJsonCaller.getDiscussGroupName();
                content.setText(newsJsonCaller.getNews());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            success = true;
        } else {
            //nothing
        }

        if (!success) {
            newsLayout.setVisibility(View.GONE);
        }

        return success;
    }

    private String getTime(Long publishEpoch) {
        String published = null;
        DateTime dateTime = new DateTime(publishEpoch * 1000);
        DateTime dateTime1 = new DateTime(LocalDate.now(DateTimeZone.forID("Asia/Kolkata")).toDateTimeAtCurrentTime());
        int days = Days.daysBetween(dateTime, dateTime1).getDays();
        int hours = Hours.hoursBetween(dateTime, dateTime1).getHours();
        int minutes = Minutes.minutesBetween(dateTime, dateTime1).getMinutes();
        if (days > 0) {
            published = String.valueOf(days) + " day" + (days == 1 ? "" : "s") + " ago";
        } else if (hours > 0) {
            published = String.valueOf(hours) + " hour" + (hours == 1 ? "" : "s") + " ago";
        } else if (minutes >= 0) {
            published = String.valueOf(minutes) + " minute" + (minutes == 1 ? "" : "s") + " ago";
        } else {
            published = "";
        }
        return published;
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

    private class NewsDetailComponentListener extends CustomComponentListener {

        public NewsDetailComponentListener(ProgressBar progressBar, ViewGroup errorLayout) {
            super(NEWS_DETAIL_REQUEST_TAG, progressBar, errorLayout);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            return NewsDiscussActivity.this.handleNewsDetailResponse(content);
        }

        @Override
        public void handleErrorContent(String tag) {

        }

        @Override
        public void changeUI(String tag) {
            boolean success = renderResponse();
            if (!success) {
                showErrorLayout();
            } else {
                share.setVisibility(View.VISIBLE);
            }
        }

    }

}

