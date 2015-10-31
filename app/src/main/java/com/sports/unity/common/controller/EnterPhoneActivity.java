package com.sports.unity.common.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class EnterPhoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone);
        init();
    }

    private View.OnClickListener sendButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // beforeAsyncCall();
            createUser();
        }
    };

    private void init() {

        final Button continueButton = (Button) findViewById(R.id.getOtp);
        final EditText phoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);
        continueButton.setVisibility(View.INVISIBLE);
        continueButton.setOnClickListener(sendButtonClickListener);
        setUserPhoneNumber(phoneNumberEditText);


        phoneNumberEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    continueButton.setVisibility(View.VISIBLE);
                } else {
                    continueButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    private void createUser() {
        EditText phoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);
        String phoneNumber = phoneNumberEditText.getText().toString();

        RequestParams requestParams = new RequestParams();
            requestParams.add(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER, "91" + phoneNumber);

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            asyncHttpClient.setTimeout(Constants.CONNECTION_TIME_OUT);
            asyncHttpClient.get(Constants.URL_REGISTER, requestParams, new JsonHttpResponseHandler() {

                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i("Success", "Sent Data");

                    try {
                        Log.i("Info  : ", response.getString("info"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    Toast.makeText(EnterPhoneActivity.this, R.string.otp_message_resending_failed, Toast.LENGTH_SHORT).show();
                }

            });

            TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_USERNAME, "91" + phoneNumber);

            moveToNextActivity(phoneNumber);

    }


    private void moveToNextActivity(String phoneNumber) {
        Intent intent = new Intent(EnterPhoneActivity.this, EnterOtpActivity.class);
        intent.putExtra(Constants.INTENT_KEY_PHONE_NUMBER, phoneNumber);
        startActivity(intent);

        finish();
    }

    private void setUserPhoneNumber(EditText phoneNumberEditText) {
        String phoneNumber = CommonUtil.getUserSimNumber(this);

        if (phoneNumber == null) {
            Toast.makeText(getApplicationContext(), R.string.sim_not_found, Toast.LENGTH_SHORT).show();
        } else {
            phoneNumberEditText.setText(phoneNumber);
        }
    }

}
