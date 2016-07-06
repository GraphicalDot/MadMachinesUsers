package com.sports.unity.common.controller;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class EnterPhoneActivity extends CustomAppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private ImageView getCountry;
    private Button continueButton;

    private TextView countryCode;
    private TextView countryName;
    private TextView countryCodetext;

    private EditText phoneNumberEditText;

    private RelativeLayout getCountryCode;

    private View.OnClickListener viewClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == R.id.getotp) {
                createUser();
            } else if (viewId == R.id.privacy_policy) {
                CommonUtil.openLinkOnBrowser(EnterPhoneActivity.this, getResources().getString(R.string.link_of_privacy_policy));
            } else if (viewId == R.id.getCountryCode || viewId == R.id.countryCodetext) {
                getCountryCodeWithCountryName();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enter_phone);
        initView();
    }

    private void initView() {
        getCountry = (ImageView) findViewById(R.id.getcountry);
        continueButton = (Button) findViewById(R.id.getotp);
        countryCode = (TextView) findViewById(R.id.countryCode);
        countryName = (TextView) findViewById(R.id.countryName);
        countryCodetext = (TextView) findViewById(R.id.countryCodetext);
        getCountryCode = (RelativeLayout) findViewById(R.id.getCountryCode);

        TextView privacy_policy = (TextView) findViewById(R.id.privacy_policy);

        privacy_policy.setOnClickListener(viewClickListener);
        continueButton.setOnClickListener(viewClickListener);
        countryCodetext.setOnClickListener(viewClickListener);
        getCountryCode.setOnClickListener(viewClickListener);


        ArrayList<String> countryDetails = CommonUtil.getCountryDetailsByCountryCode(EnterPhoneActivity.this, UserUtil.getCountryCode());

        countryName.setText(countryDetails.get(2));
        countryCode.setText("( +" + countryDetails.get(0) + ")");
        countryCodetext.setText("+" + countryDetails.get(0));


        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //nothing
            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = s.toString();
                if ((text.length() == 1)) {

                    countryName.setVisibility(View.GONE);
                    countryCode.setVisibility(View.GONE);
                    getCountry.setVisibility(View.GONE);
                    continueButton.setVisibility(View.VISIBLE);
                    countryCodetext.setVisibility(View.VISIBLE);

                } else if (text.length() == 0) {

                    countryName.setVisibility(View.VISIBLE);
                    countryCode.setVisibility(View.VISIBLE);
                    getCountry.setVisibility(View.VISIBLE);
                    continueButton.setVisibility(View.GONE);
                    countryCodetext.setVisibility(View.GONE);

                }


            }
        });
        if (PermissionUtil.getInstance().isRuntimePermissionRequired()) {
            PermissionUtil.getInstance().requestPermission(EnterPhoneActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.RECEIVE_SMS)), getResources().getString(R.string.read_sms_permission_message), Constants.REQUEST_CODE_PHONE_STATE_PERMISSION);
        }
        setUserPhoneNumber(phoneNumberEditText);
        /*
         * to set initial focus to edit text view and open keyboard.
         */
        phoneNumberEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void getCountryCodeWithCountryName() {

        Intent intent = new Intent(EnterPhoneActivity.this, GetCountryCode.class);
        intent.putExtra("CountryName", countryName.getText().toString());
        startActivityForResult(intent, 1111);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111) {
            if (resultCode == RESULT_OK) {
                String cName = data.getStringExtra("countryName");
                String cCode = data.getStringExtra("countryCode");

                countryName.setText(cName);
                countryCode.setText("(+" + cCode + " )");
                countryCodetext.setText("+" + cCode);

                UserUtil.setCountryCode(getApplicationContext(), cCode);
            }
        }
    }

    private void setUserPhoneNumber(EditText phoneNumberEditText) {
        String phone_Number = getIntent().getStringExtra(Constants.INTENT_KEY_PHONE_NUMBER);

        if (phone_Number != null) {
            phoneNumberEditText.setText(phone_Number);
            countryName.setVisibility(View.GONE);
            countryCode.setVisibility(View.GONE);
            getCountry.setVisibility(View.GONE);
            continueButton.setVisibility(View.VISIBLE);
            countryCodetext.setVisibility(View.VISIBLE);

        } else {
            //nothing
        }

    }

    private void createUser() {
        UserUtil.setOtpSent(EnterPhoneActivity.this, false);

        String phoneNumber = phoneNumberEditText.getText().toString();
        String countryCode = CommonUtil.getCountryDetailsByCountryCode(EnterPhoneActivity.this, UserUtil.getCountryCode()).get(0);
        TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_USERNAME, countryCode + phoneNumber);
        TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_USER_MOBILE_NUMBER, phoneNumber);
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
        if (requestCode == Constants.REQUEST_CODE_PHONE_STATE_PERMISSION) {
            if (!PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                PermissionUtil.getInstance().showSnackBar(this, getString(R.string.permission_denied));
            } else {
                //nothing
            }
        }
    }

}
