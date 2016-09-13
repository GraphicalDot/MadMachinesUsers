package com.sports.unity.news.controller.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.BuildConfig;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ThreadTask;
import com.sports.unity.util.network.FirebaseUtil;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PollActivity extends AppCompatActivity {

    public static final String POLL_AGREE = "y";
    public static final String POLL_DISAGREE = "n";

    private static final String SUBMIT_POLL_ANSWER = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/submit_poll_answer?";
    private String pollAnswer = null;
    private String article_id = null;
    private ProgressDialog progressDialog;
    private LinearLayout agreeLayout;
    private LinearLayout disagreeLayout;
    private ImageView backButton;

    private String groupName = "SportsGroup";
    private String pollQuestion = "What is your opinion?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        getIntentExtras();
        initView();
    }

    private void getIntentExtras() {
        article_id = getIntent().getStringExtra(Constants.INTENT_KEY_ID);
        pollQuestion = getIntent().getStringExtra(Constants.INTENT_POLL_QUESTION);
        groupName = getIntent().getStringExtra(Constants.INTENT_GROUP_NAME);
    }

    private void initView() {
        backButton = (ImageView) findViewById(R.id.back_button);
        agreeLayout = (LinearLayout) findViewById(R.id.agree_layout);
        disagreeLayout = (LinearLayout) findViewById(R.id.disagree_layout);

        backButton.setOnClickListener(onClickListener);
        agreeLayout.setOnClickListener(onClickListener);
        disagreeLayout.setOnClickListener(onClickListener);
        TextView pollView = (TextView) findViewById(R.id.poll_question);
        pollView.setText(pollQuestion);
        boolean pollStatus = getIntent().getBooleanExtra(Constants.INTENT_POLL_STATUS, false);
        if (getIntent().getBooleanExtra(Constants.INTENT_POLL_PARRY, false)) {
            AlreadyPolled(pollStatus);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.agree_layout:
                    onAgree(v);
                    break;
                case R.id.disagree_layout:
                    onDisagree(v);
                    break;
                case R.id.back_button:
                    PollActivity.this.finish();
                    break;
            }
        }
    };


    private void AlreadyPolled(boolean pollStatus) {

        TextView agree = (TextView) findViewById(R.id.agree);
        TextView disagree = (TextView) findViewById(R.id.disagree);

        View view = findViewById(R.id.seperator);

        view.setVisibility(View.GONE);

        if (pollStatus) {
            pollAnswer = POLL_AGREE;
            disagreeLayout.setVisibility(View.GONE);
            agree.setText("Agreed");
        } else {
            pollAnswer = POLL_DISAGREE;
            agreeLayout.setVisibility(View.GONE);
            disagree.setText("Disagreed");
        }
    }

    private void changeUI(String pollAnswer) {
        boolean pollStatus = pollAnswer.equals(POLL_AGREE) ? true : false;
        AlreadyPolled(pollStatus);
    }

    public void onAgree(View view) {
        if (pollAnswer == null) {
            showProgress();
            submitPollAnswer(POLL_AGREE);
        }
    }

    public void onDisagree(View view) {
        if (pollAnswer == null) {
            showProgress();
            submitPollAnswer(POLL_DISAGREE);
        }
    }


    private void submitPollAnswer(final String poll) {
        ThreadTask pollSubmitTask = new ThreadTask(null) {
            @Override
            public Object process() {
                boolean success = sendPollAnswer(poll);
                return success;
            }

            @Override
            public void postAction(Object object) {
                final boolean success = (boolean) object;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            pollAnswer = poll;
                            changeUI(pollAnswer);
                            Toast.makeText(getApplicationContext(), "Sucesss", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "FAILED try again", Toast.LENGTH_SHORT).show();
                        }
                        hideProgress();
                    }
                });
            }
        };
        pollSubmitTask.start();
    }

    private boolean sendPollAnswer(String pollAnswer) {
        boolean success = false;
        String jsonContent = null;
        try {

            String password = TinyDB.getInstance(PollActivity.this).getString(TinyDB.KEY_PASSWORD);
            String userJID = TinyDB.getInstance(PollActivity.this).getString(TinyDB.KEY_USER_JID);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userJID);
            jsonObject.put("password", password);
            jsonObject.put("apk_version", CommonUtil.getBuildConfig());
            jsonObject.put("udid", CommonUtil.getDeviceId(PollActivity.this));
            jsonObject.put("poll_answer", pollAnswer);
            jsonObject.put("article_id", article_id);
            jsonObject.put("group_name", groupName);

            jsonContent = jsonObject.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (jsonContent != null) {
            HttpURLConnection httpURLConnection = null;
            ByteArrayInputStream byteArrayInputStream = null;
            try {

                URL sendInterests = new URL(SUBMIT_POLL_ANSWER);
                httpURLConnection = (HttpURLConnection) sendInterests.openConnection();
                httpURLConnection.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
                httpURLConnection.setDoInput(false);
                httpURLConnection.setRequestMethod("POST");

                byteArrayInputStream = new ByteArrayInputStream(jsonContent.getBytes());
                OutputStream outputStream = httpURLConnection.getOutputStream();

                byte chunk[] = new byte[4096];
                int read = 0;
                while ((read = byteArrayInputStream.read(chunk)) != -1) {
                    outputStream.write(chunk, 0, read);
                }
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    success = true;
                    boolean pollStatus = pollAnswer.equals(POLL_AGREE) ? true : false;
                    logFireBaseEvent(pollStatus);
                    SportsUnityDBHelper.getInstance(getApplicationContext()).insertPollinDatabase(groupName, article_id, pollStatus);
                } else {
                    //nothing
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    httpURLConnection.disconnect();
                } catch (Exception ex) {
                }
            }
        } else {
            //nothing
        }

        return success;
    }

    private void logFireBaseEvent(boolean pollStatus) {
        //FIREBASE INTEGRATION
        {
            String firebaseEvent = pollStatus ? FirebaseUtil.Event.POLL_AGREE : FirebaseUtil.Event.POLL_DISAGREE;
            FirebaseAnalytics firebaseAnalytics = FirebaseUtil.getInstance(getApplicationContext());
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseUtil.Param.ARTICLE_ID, article_id);
            FirebaseUtil.logEvent(firebaseAnalytics, bundle, firebaseEvent);
        }
    }

    private void showProgress() {
        ProgressBar progressBar = new ProgressBar(PollActivity.this);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        progressDialog = new ProgressDialog(PollActivity.this);
        progressDialog.setMessage("creating group...");
        progressDialog.setIndeterminateDrawable(progressBar.getIndeterminateDrawable());
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
