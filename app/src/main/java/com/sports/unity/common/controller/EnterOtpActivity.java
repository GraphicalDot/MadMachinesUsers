package com.sports.unity.common.controller;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.gsm.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EnterOtpActivity extends CustomVolleyCallerActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String REQUEST_LISTENER = "ENTER_OTP_SCREEN_LISTENER";

    private static final String CREATE_USER_REQUEST_TAG = "CreateUserTag";
    private static final String RESEND_OTP_REQUEST_TAG = "ResendOtpTag";

    private boolean moved = false;

    private AlertDialog otpWaitingDialog;
    private EditText otpEditText;

    private SMSReceiverBroadcast smsReceiverBroadcast = null;

    private View.OnClickListener sendButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createUser();
        }
    };

    private View.OnClickListener resendOtpButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resendOtp();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.sports.unity.R.layout.activity_enter_otp);

        initViews();

        {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            CreateUserComponentListener createUserComponentListener = new CreateUserComponentListener(progressBar);
            ResendOtpComponentListener resendOtpComponentListener = new ResendOtpComponentListener(progressBar);

            ArrayList<CustomComponentListener> listeners = new ArrayList<>();
            listeners.add(createUserComponentListener);
            listeners.add(resendOtpComponentListener);

            onComponentCreate(listeners, REQUEST_LISTENER);
        }

        {
            if (!UserUtil.isOtpSent()) {
                UserUtil.setOtpSent(EnterOtpActivity.this, true);
                resendOtp();
            } else {
                //nothing
            }
        }

        {
            if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                copyContacts();
            } else {
                if (PermissionUtil.getInstance().requestPermission(EnterOtpActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)))) {
                    copyContacts();
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UserUtil.isUserRegistered()) {
            moveToNextActivity();
        } else {
            onComponentResume();
        }

    }

    private void copyContacts() {
        ContactsHandler.getInstance().addCallToSyncContacts(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();

        onComponentPause();
    }

    @Override
    public void onBackPressed() {
        moveBack();

        super.onBackPressed();
    }

    private class SMSReceiverBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    otpWaitingDialog.cancel();

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

                        try {
                            if (senderNum.contains("SPOUNI")) {
                                String str = message.replaceAll("\\D+", "");
                                otpEditText.setText(str);
                                createUser();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initViews() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.gray1), android.graphics.PorterDuff.Mode.MULTIPLY);

        LinearLayout editNumberLayout = (LinearLayout) findViewById(R.id.editNumberLayout);
        editNumberLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });

        editNumberLayout.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));

        TextView editNumberTextView = (TextView) findViewById(R.id.editNumber);
        editNumberTextView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        final Button sendOtpButton = (Button) findViewById(com.sports.unity.R.id.sendOtpButton);
        sendOtpButton.setVisibility(View.INVISIBLE);
        sendOtpButton.setOnClickListener(sendButtonClickListener);

        Button resendButton = (Button) findViewById(R.id.resend);
        resendButton.setOnClickListener(resendOtpButtonClickListener);
        resendButton.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        resendButton.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, false));

        TextView otpText = (TextView) findViewById(com.sports.unity.R.id.enterotpText);
        otpText.setText(getString(R.string.otp_message_verification) + getPhoneNumber());
        otpText.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        otpEditText = (EditText) findViewById(com.sports.unity.R.id.enterOtp);
        otpEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    sendOtpButton.setVisibility(View.VISIBLE);
                } else {
                    sendOtpButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        /*
         * to set initial focus to edit text view and open keyboard.
         */
        otpEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private String getPhoneNumber() {
        String phoneNumber = TinyDB.getInstance(this).getString(TinyDB.KEY_USERNAME);
        phoneNumber = phoneNumber.substring(2);
        return phoneNumber;
    }

    private void createUser() {
        unRegisterSmsBroadcastReceiver();

        EditText otpEditText = (EditText) findViewById(com.sports.unity.R.id.enterOtp);
        String otp = otpEditText.getText().toString();
        String phoneNumber = "91" + getPhoneNumber();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER, phoneNumber);
        parameters.put(Constants.REQUEST_PARAMETER_KEY_AUTH_CODE, otp);
        parameters.put(Constants.REQUEST_PARAMETER_KEY_APK_VERSION, CommonUtil.getBuildConfig());
        parameters.put(Constants.REQUEST_PARAMETER_KEY_UDID, CommonUtil.getDeviceId(this));
        requestContent(ScoresContentHandler.CALL_NAME_CREATE_USER, parameters, CREATE_USER_REQUEST_TAG);
    }

    private void resendOtp() {
        Toast.makeText(EnterOtpActivity.this, R.string.otp_message_sending, Toast.LENGTH_SHORT).show();

        String phoneNumber = "91" + getPhoneNumber();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER, phoneNumber);
        parameters.put(Constants.REQUEST_PARAMETER_KEY_APK_VERSION, CommonUtil.getBuildConfig());
        parameters.put(Constants.REQUEST_PARAMETER_KEY_UDID, CommonUtil.getDeviceId(this));
        requestContent(ScoresContentHandler.CALL_NAME_ASK_OTP, parameters, RESEND_OTP_REQUEST_TAG);
    }

    private void moveToNextActivity() {
        if (!moved) {
            moved = true;

            Intent intent = new Intent(this, ProfileCreationActivity.class);
            startActivity(intent);

            finish();
        }
    }

    private void moveBack() {
        String phoneNumber = getPhoneNumber();

        Intent intent = new Intent(this, EnterPhoneActivity.class);
        intent.putExtra(Constants.INTENT_KEY_PHONE_NUMBER, phoneNumber);
        startActivity(intent);
    }

    private class CreateUserComponentListener extends CustomComponentListener {

        private boolean success;

        public CreateUserComponentListener(ProgressBar progressBar) {
            super(CREATE_USER_REQUEST_TAG, progressBar, null);
        }

        @Override
        protected void showErrorLayout() {
            super.showErrorLayout();
            Toast.makeText(getApplicationContext(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = false;
            try {
                JSONObject response = new JSONObject(content);
                if (response.getString("status").equals("200")) {
                    String password = response.getString(Constants.REQUEST_PARAMETER_KEY_PASSWORD);
                    String userJid = response.getString(Constants.REQUEST_PARAMETER_KEY_USER_NAME);
                    TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_USER_JID, userJid);

                    Log.d("max", "Jid is-" + userJid);
                    TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_PASSWORD, password);
                    UserUtil.setOtpSent(EnterOtpActivity.this, false);
                    UserUtil.setUserRegistered(EnterOtpActivity.this, true);

                    this.success = true;
                } else {
                    this.success = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            success = true;
            return success;
        }

        @Override
        public void handleErrorContent(String tag) {
            //nothing
        }

        @Override
        public void changeUI() {
            if (success) {
                moveToNextActivity();
            } else {
                Toast.makeText(getApplicationContext(), R.string.otp_message_wrong_expired_token, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class ResendOtpComponentListener extends CustomComponentListener {

        private boolean resendSuccessful;

        public ResendOtpComponentListener(ProgressBar progressBar) {
            super(RESEND_OTP_REQUEST_TAG, progressBar, null);
        }

        @Override
        protected void showErrorLayout() {
            super.showErrorLayout();
            Toast.makeText(getApplicationContext(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void handleErrorContent(String tag) {
            UserUtil.setOtpSent(EnterOtpActivity.this, false);
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = false;
            try {
                JSONObject response = new JSONObject(content);
                String info = response.getString("info");

                if (info.equalsIgnoreCase("Success")) {
                    UserUtil.setOtpSent(EnterOtpActivity.this, true);
                    this.resendSuccessful = true;
                } else {
                    UserUtil.setOtpSent(EnterOtpActivity.this, false);
                    this.resendSuccessful = false;
                }
                Log.i("Enter Otp", "resend response info : " + info);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            success = true;
            return success;
        }

        @Override
        public void changeUI() {
            if (resendSuccessful) {
                Toast.makeText(EnterOtpActivity.this, R.string.otp_message_otp_sent, Toast.LENGTH_SHORT).show();
                if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                    displayOtpWaitingDialog();
                    registerSmsBroadcastReceiver();
                } else if (PermissionUtil.getInstance().requestPermission(EnterOtpActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.RECEIVE_SMS)))) {
                    displayOtpWaitingDialog();
                    registerSmsBroadcastReceiver();
                }
            } else {
                Toast.makeText(EnterOtpActivity.this, R.string.otp_message_resending_failed, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void registerSmsBroadcastReceiver() {
        smsReceiverBroadcast = new SMSReceiverBroadcast();
        registerReceiver(smsReceiverBroadcast, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    private void unRegisterSmsBroadcastReceiver() {
        if (smsReceiverBroadcast != null) {
            unregisterReceiver(smsReceiverBroadcast);
            smsReceiverBroadcast = null;
        }
    }

    private void displayOtpWaitingDialog() {

        AlertDialog.Builder build = new AlertDialog.Builder(EnterOtpActivity.this);
        build.setMessage("Waiting to automatically detect an SMS.");
        build.setNegativeButton("Enter Manually", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                unRegisterSmsBroadcastReceiver();

            }
        });

        otpWaitingDialog = build.create();
        otpWaitingDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_CONTACT_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                copyContacts();
            } else {
                PermissionUtil.getInstance().showSnackBar(this, getString(R.string.permission_denied));
            }
        }
    }

}