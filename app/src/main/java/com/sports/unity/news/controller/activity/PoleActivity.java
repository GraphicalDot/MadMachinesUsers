package com.sports.unity.news.controller.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.BuildConfig;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ThreadTask;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PoleActivity extends AppCompatActivity {


    private static final String SUBMIT_POLL_ANSWER = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/submit_poll_answer?";
    private String pollAnswer = null;
    int article_id = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pole);
        article_id = getIntent().getIntExtra(Constants.INTENT_KEY_ID, 0);
        removePollIfAlreadyPolled();
    }

    private void removePollIfAlreadyPolled() {

        LinearLayout agreeLayout = (LinearLayout) findViewById(R.id.agree_layout);
        LinearLayout disagreeLayout = (LinearLayout) findViewById(R.id.disagree_layout);

        TextView agree = (TextView) findViewById(R.id.agree);
        TextView disagree = (TextView) findViewById(R.id.disagree);

        View view = findViewById(R.id.seperator);

        if (getIntent().getBooleanExtra(Constants.INTENT_POLL_PARRY, false)) {
            view.setVisibility(View.GONE);
            boolean poll = getIntent().getBooleanExtra(Constants.INTENT_POLL_STATUS, false);
            if (poll) {
                disagreeLayout.setVisibility(View.GONE);
                agree.setClickable(false);
                agree.setText("Agreed");
            } else {
                agreeLayout.setVisibility(View.GONE);
                disagree.setClickable(false);
                disagree.setText("Disagreed");
            }
        }
    }

    public void onAgree(View view) {
        //TODO
        pollAnswer = "y";
        Log.d("max", "Sending agree");
        showProgress();
        submitPollAnswer();
    }

    public void onDisagree(View view) {
        //TODO
        Log.d("max", "Sending disagree");
        pollAnswer = "n";
        showProgress();
        submitPollAnswer();
    }


    private void submitPollAnswer() {
        ThreadTask pollSubmitTask = new ThreadTask(null) {
            @Override
            public Object process() {
                boolean success = sendPollAnswer();
                return success;
            }

            @Override
            public void postAction(Object object) {
                final boolean success = (boolean) object;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
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

    private boolean sendPollAnswer() {
        boolean success = false;
        String jsonContent = null;
        try {

            String password = TinyDB.getInstance(PoleActivity.this).getString(TinyDB.KEY_PASSWORD);
            String userJID = TinyDB.getInstance(PoleActivity.this).getString(TinyDB.KEY_USER_JID);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userJID);
            jsonObject.put("password", password);
            jsonObject.put("apk_version", CommonUtil.getBuildConfig());
            jsonObject.put("udid", CommonUtil.getDeviceId(PoleActivity.this));
            jsonObject.put("poll_answer", pollAnswer);
            jsonObject.put("article_id", article_id);

            jsonContent = jsonObject.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (jsonContent != null) {
            HttpURLConnection httpURLConnection = null;
            ByteArrayInputStream byteArrayInputStream = null;
            try {
                URL sendInterests = new URL(SUBMIT_POLL_ANSWER);
                Log.d("max", "PollUrl>>" + sendInterests);
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

                Log.d("max", "response code is" + httpURLConnection.getResponseCode() + "<<JsonContent>>" + jsonContent);
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    success = true;
                    SportsUnityDBHelper.getInstance(getApplicationContext()).insertPollinDatabase("articleName", 165, true);
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

    private void showProgress() {
        ProgressBar progressBar = new ProgressBar(PoleActivity.this);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        progressDialog = new ProgressDialog(PoleActivity.this);
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
