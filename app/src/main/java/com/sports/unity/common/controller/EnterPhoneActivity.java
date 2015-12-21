package com.sports.unity.common.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
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

        TextView entr_ph_no=(TextView) findViewById(R.id.entr_ph_no);
        entr_ph_no.setTypeface(FontTypeface.getInstance(this).getRobotoLight());

        TextView txt_details=(TextView) findViewById(R.id.txt_details);
        txt_details.setTypeface(FontTypeface.getInstance(this).getRobotoLight());

        TextView take_a_minut=(TextView) findViewById(R.id.take_a_minut);
        take_a_minut.setTypeface(FontTypeface.getInstance(this).getRobotoLight());

        TextView privacy_policy=(TextView) findViewById(R.id.privacy_policy);
        privacy_policy.setTypeface(FontTypeface.getInstance(this).getRobotoLight());

        final Button continueButton = (Button) findViewById(R.id.getOtp);
        continueButton.setVisibility(View.INVISIBLE);
        continueButton.setOnClickListener(sendButtonClickListener);

        final EditText phoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);
        setUserPhoneNumber(phoneNumberEditText, continueButton);
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

        /*
         * to set initial focus to edit text view and open keyboard.
         */
        phoneNumberEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void setUserPhoneNumber(EditText phoneNumberEditText, Button continueButton) {
        String phone_Number = getIntent().getStringExtra(Constants.INTENT_KEY_PHONE_NUMBER);

        if (phone_Number != null) {
            phoneNumberEditText.setText(phone_Number);
            continueButton.setVisibility(View.VISIBLE);
        } else {
            String phoneNumber = CommonUtil.getUserSimNumber(this);

            if (phoneNumber == null) {
                Toast.makeText(getApplicationContext(), R.string.sim_not_found, Toast.LENGTH_SHORT).show();
            } else {
                phoneNumberEditText.setText(phoneNumber);
                continueButton.setVisibility(View.VISIBLE);
            }
        }

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
                try {
                    String info = response.getString("info");

                    if( info.equalsIgnoreCase("Success")){
//                        UserUtil.setOtpSent(EnterPhoneActivity.this, true);
                        Toast.makeText(EnterPhoneActivity.this, R.string.otp_message_otp_sent, Toast.LENGTH_SHORT).show();
                    } else {
                        onFailure_OnSendingOTP();
                    }

                    Log.i("Enter Phone", "otp sent response info : " + info);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                onFailure_OnSendingOTP();
            }

        });

        TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_USERNAME, "91" + phoneNumber);
        UserUtil.setOtpSent(EnterPhoneActivity.this, true);
        moveToNextActivity();

    }

    private void onFailure_OnSendingOTP() {
        Toast.makeText(EnterPhoneActivity.this, R.string.otp_message_resending_failed, Toast.LENGTH_SHORT).show();
        UserUtil.setOtpSent(EnterPhoneActivity.this, false);
    }

    private void moveToNextActivity() {
        Intent intent = new Intent(EnterPhoneActivity.this, EnterOtpActivity.class);
        startActivity(intent);

        finish();
    }

}
