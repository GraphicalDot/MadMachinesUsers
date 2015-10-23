package com.sports.unity.common.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class EnterOtpActivity extends AppCompatActivity {

    private boolean paused = false;
    private boolean moved = false;

    private View.OnClickListener sendButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            beforeAsyncCall();
            createUser();
        }
    };

    private View.OnClickListener resendOtpButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            beforeAsyncCall();
            resendOtp();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.sports.unity.R.layout.activity_enter_otp);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        paused = false;
        if( UserUtil.isUserRegistered() ){
            moveToNextActivity();
        } else {
            //nothing
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        paused = true;
    }

    @Override
    public void onBackPressed() {
        moveBack();

        super.onBackPressed();
    }

    private void initViews(){
        Button editNumberButton = (Button) findViewById(R.id.editnumber);
        editNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Button sendOtpButton = (Button) findViewById(com.sports.unity.R.id.sendOtpButton);
        sendOtpButton.setVisibility(View.INVISIBLE);
        sendOtpButton.setOnClickListener(sendButtonClickListener);

        Button resendButton = (Button) findViewById(R.id.resend);
        resendButton.setOnClickListener(resendOtpButtonClickListener);

        TextView otpText = (TextView) findViewById(com.sports.unity.R.id.enterotpText);
        otpText.setText(getString(R.string.otp_message_verification) + getIntent().getStringExtra(Constants.INTENT_KEY_PHONE_NUMBER));

        EditText otpEditText = (EditText) findViewById(com.sports.unity.R.id.enterOtp);
        otpEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    sendOtpButton.setVisibility(View.VISIBLE);
                } else if (s.length() != 4) {
                    sendOtpButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    private void createUser() {
        EditText otpEditText = (EditText) findViewById(com.sports.unity.R.id.enterOtp);
        String otp = otpEditText.getText().toString();
        String phoneNumber = getIntent().getStringExtra(Constants.INTENT_KEY_PHONE_NUMBER);

        RequestParams requestParams = new RequestParams();
        requestParams.add(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER, "91" + phoneNumber);
        requestParams.add(Constants.REQUEST_PARAMETER_KEY_AUTH_CODE, otp);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(Constants.CONNECTION_TIME_OUT);
        asyncHttpClient.get(Constants.URL_CREATE, requestParams, new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("Success", "Sent Data");

                try {
                    afterAsyncCall();

                    Log.i("Info  : ", response.getString("info"));
                    if (response.getString("status").equals("200")) {
                        String password = response.getString(Constants.REQUEST_PARAMETER_KEY_PASSWORD);
                        TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_PASSWORD, password);
                        UserUtil.setUserRegistered( EnterOtpActivity.this, true);

                        if (!paused) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    moveToNextActivity();
                                }
                            });
                        } else {
                            //nothing
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.otp_message_wrong_expired_token, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                afterAsyncCall();

                String resp = String.valueOf(statusCode);
                resp = resp + "   Failed";
                Log.i("Response ", resp);

                Toast.makeText(getApplicationContext(), R.string.otp_message_wrong_expired_token, Toast.LENGTH_LONG).show();
            }

        });
    }

    private void resendOtp(){
        String phoneNumber = getIntent().getStringExtra(Constants.INTENT_KEY_PHONE_NUMBER);

        RequestParams requestParams = new RequestParams();
        requestParams.add(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER, "91" + phoneNumber);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(Constants.CONNECTION_TIME_OUT);
        asyncHttpClient.get(Constants.URL_REGISTER, requestParams, new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                afterAsyncCall();
                Toast.makeText(EnterOtpActivity.this, R.string.otp_message_resending, Toast.LENGTH_SHORT).show();

                try {
                    Log.i("Success", "Sent Data");
                    Log.i("Info  : ", response.getString("info"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

//                afterAsyncCall();
                Toast.makeText(EnterOtpActivity.this, R.string.otp_message_resending_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void beforeAsyncCall(){
        Button sendOtpButton = (Button) findViewById(R.id.sendOtpButton);
        sendOtpButton.setOnClickListener(null);

//        Button resendOtpButton = (Button) findViewById(R.id.resend);
//        resendOtpButton.setOnClickListener(null);

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void afterAsyncCall(){
        Button sendOtpButton = (Button) findViewById(R.id.sendOtpButton);
        sendOtpButton.setOnClickListener(sendButtonClickListener);

//        Button resendOtpButton = (Button) findViewById(R.id.resend);
//        resendOtpButton.setOnClickListener(resendOtpButtonClickListener);

        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    private void moveToNextActivity(){
        if( ! moved ) {
            moved = true;

            Intent intent = new Intent(this, ProfileCreationActivity.class);
            startActivity(intent);

            finish();
        }
    }

    private void moveBack(){
        Intent intent = new Intent(this, EnterPhoneActivity.class);
        startActivity(intent);
    }

}



