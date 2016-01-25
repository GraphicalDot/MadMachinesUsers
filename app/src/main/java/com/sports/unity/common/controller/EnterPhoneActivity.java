package com.sports.unity.common.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.network.AsyncHttpClient;
import com.sports.unity.util.network.ResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EnterPhoneActivity extends CustomVolleyCallerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private View.OnClickListener sendButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createUser();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enter_phone);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        onComponentResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        onComponentPause();
    }

    private void init() {
        TextView entr_ph_no = (TextView) findViewById(R.id.entr_ph_no);
        entr_ph_no.setTypeface(FontTypeface.getInstance(this).getRobotoLight());

        TextView txt_details = (TextView) findViewById(R.id.txt_details);
        txt_details.setTypeface(FontTypeface.getInstance(this).getRobotoLight());

        TextView take_a_minut = (TextView) findViewById(R.id.take_a_minut);
        take_a_minut.setTypeface(FontTypeface.getInstance(this).getRobotoLight());

        TextView privacy_policy = (TextView) findViewById(R.id.privacy_policy);
        privacy_policy.setTypeface(FontTypeface.getInstance(this).getRobotoLight());

        final Button continueButton = (Button) findViewById(R.id.getOtp);
        continueButton.setVisibility(View.INVISIBLE);
        continueButton.setOnClickListener(sendButtonClickListener);

        final EditText phoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);
        if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {

            setUserPhoneNumber(phoneNumberEditText, continueButton);
        } else {
            if (PermissionUtil.getInstance().requestPermission(EnterPhoneActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.READ_PHONE_STATE)), getResources().getString(R.string.read_phone_permission_message), Constants.REQUEST_CODE_PHONE_STATE_PERMISSION)) {

                setUserPhoneNumber(phoneNumberEditText, continueButton);
            }
        }
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
        UserUtil.setOtpSent(EnterPhoneActivity.this, false);

        EditText phoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);
        String phoneNumber = phoneNumberEditText.getText().toString();
        TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_USERNAME, "91" + phoneNumber);

        moveToNextActivity();
    }

    private void moveToNextActivity() {
        Intent intent = new Intent(EnterPhoneActivity.this, EnterOtpActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_CONTACT_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                setUserPhoneNumber((EditText) findViewById(R.id.phoneNumber), (Button) findViewById(R.id.getOtp));
            } else {
                PermissionUtil.getInstance().showSnackBar(this, getString(R.string.permission_denied));
            }
        }
    }

}
