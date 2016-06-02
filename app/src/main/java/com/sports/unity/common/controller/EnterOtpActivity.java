package com.sports.unity.common.controller;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.telephony.gsm.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserProfileHandler;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.CustomVolleyCallerActivity;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.common.viewhelper.VolleyCallComponentHelper;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EnterOtpActivity extends CustomVolleyCallerActivity {

    private static final String REQUEST_LISTENER = "ENTER_OTP_SCREEN_LISTENER";

    private static final String CREATE_USER_REQUEST_TAG = "CreateUserTag";
    private static final String RESEND_OTP_REQUEST_TAG = "ResendOtpTag";

    private boolean moved = false;

    private AlertDialog otpWaitingDialog;
    private EditText otpEditText;

    private SMSReceiverBroadcast smsReceiverBroadcast = null;
    private EnterOtpCustomComponentListener enterOtpCustomComponentListener = null;

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
            onComponentCreate();

            if (!UserUtil.isOtpSent()) {
                UserUtil.setOtpSent(EnterOtpActivity.this, true);
                resendOtp();
            } else {
                //nothing
            }
        }
    }

    @Override
    public VolleyCallComponentHelper getVolleyCallComponentHelper() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        enterOtpCustomComponentListener = new EnterOtpCustomComponentListener(progressBar);

        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper(REQUEST_LISTENER, enterOtpCustomComponentListener);
        return volleyCallComponentHelper;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UserUtil.isUserRegistered()) {
            moveToNextActivity(ProfileCreationActivity.class);
        } else {

        }

    }

    @Override
    public void onBackPressed() {
        if (UserUtil.isFilterCompleted()) {
            //nothing
        } else {
            moveBack();
        }

        super.onBackPressed();
    }

    private void initViews() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.gray1), android.graphics.PorterDuff.Mode.MULTIPLY);

        LinearLayout editNumberLayout = (LinearLayout) findViewById(R.id.editNumberLayout);
        editNumberLayout.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        if (UserUtil.isFilterCompleted()) {
            // when user re-verify its account. Don't allow to change phone number.
            editNumberLayout.setVisibility(View.GONE);
        } else {

        }
        editNumberLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });

        TextView editNumberTextView = (TextView) findViewById(R.id.editNumber);
        editNumberTextView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

        final Button sendOtpButton = (Button) findViewById(com.sports.unity.R.id.sendOtpButton);
        sendOtpButton.setVisibility(View.INVISIBLE);
        sendOtpButton.setOnClickListener(sendButtonClickListener);

        Button resendButton = (Button) findViewById(R.id.resend);
        resendButton.setOnClickListener(resendOtpButtonClickListener);
        resendButton.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        resendButton.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, false));

        ArrayList<String> countryDetails = CommonUtil.getCountryDetailsByCountryCode(EnterOtpActivity.this, UserUtil.getCountryCode());
        String countryCode = countryDetails.get(0);

        TextView otpText = (TextView) findViewById(com.sports.unity.R.id.enterotpText);
        otpText.setText(getString(R.string.otp_message_verification) + "+" + countryCode + " " + getPhoneNumberWithoutCountryCode());
        otpText.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoLight());

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

    private String getPhoneNumberWithCountryCode() {
        String phoneNumber = TinyDB.getInstance(this).getString(TinyDB.KEY_USERNAME);
        return phoneNumber;
    }

    private String getPhoneNumberWithoutCountryCode() {
        String phoneNumber = TinyDB.getInstance(this).getString(TinyDB.KEY_USER_MOBILE_NUMBER);
        return phoneNumber;
    }

    private void createUser() {
        unRegisterSmsBroadcastReceiver();

        enterOtpCustomComponentListener.setCurrentTag(CREATE_USER_REQUEST_TAG);

        EditText otpEditText = (EditText) findViewById(com.sports.unity.R.id.enterOtp);
        String otp = otpEditText.getText().toString();
        String phoneNumber = getPhoneNumberWithCountryCode();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER, phoneNumber);
        parameters.put(Constants.REQUEST_PARAMETER_KEY_AUTH_CODE, otp);
        parameters.put(Constants.REQUEST_PARAMETER_KEY_APK_VERSION, CommonUtil.getBuildConfig());
        parameters.put(Constants.REQUEST_PARAMETER_KEY_UDID, CommonUtil.getDeviceId(this));
        requestContent(ScoresContentHandler.CALL_NAME_CREATE_USER, parameters, CREATE_USER_REQUEST_TAG);
    }

    private void resendOtp() {
        Toast.makeText(EnterOtpActivity.this, R.string.otp_message_sending, Toast.LENGTH_SHORT).show();

        enterOtpCustomComponentListener.setCurrentTag(RESEND_OTP_REQUEST_TAG);

        String phoneNumber = getPhoneNumberWithCountryCode();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(Constants.REQUEST_PARAMETER_KEY_PHONE_NUMBER, phoneNumber);
        parameters.put(Constants.REQUEST_PARAMETER_KEY_APK_VERSION, CommonUtil.getBuildConfig());
        parameters.put(Constants.REQUEST_PARAMETER_KEY_UDID, CommonUtil.getDeviceId(this));
        requestContent(ScoresContentHandler.CALL_NAME_ASK_OTP, parameters, RESEND_OTP_REQUEST_TAG);
    }

    private void moveToNextActivity(Class activityClass) {
        if (!moved) {
            moved = true;

            Intent intent = new Intent(this, activityClass);
            startActivity(intent);

            finish();
        }
    }

    private void moveBack() {
        String phoneNumber = getPhoneNumberWithoutCountryCode();

        Intent intent = new Intent(this, EnterPhoneActivity.class);
        intent.putExtra(Constants.INTENT_KEY_PHONE_NUMBER, phoneNumber);
        startActivity(intent);
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

        final int lennghtInMillis = 60 * 1000;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.otp_dialog, (ViewGroup) findViewById(R.id.your_dialog_root_element));

        ArrayList<String> countryDetails = CommonUtil.getCountryDetailsByCountryCode(EnterOtpActivity.this, UserUtil.getCountryCode());
        String countryCode = countryDetails.get(0);
        final TextView seekMessage = (TextView) layout.findViewById(R.id.seek_msg);
        TextView phNo = (TextView) layout.findViewById(R.id.ph_no);
        phNo.setText("+" + countryCode + " " + getPhoneNumberWithoutCountryCode());


        final ProgressBar mProgressBar = (ProgressBar) layout.findViewById(R.id.progressbar);
        mProgressBar.setMax(lennghtInMillis / 1000);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(layout);
        otpWaitingDialog = builder.create();
        otpWaitingDialog.show();
        otpWaitingDialog.setCancelable(false);
        otpWaitingDialog.setCanceledOnTouchOutside(false);


        CountDownTimer mCountDownTimer = new CountDownTimer(lennghtInMillis, 1000) {
            private boolean warned = false;

            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) ((lennghtInMillis - millisUntilFinished) / 1000);
                mProgressBar.setProgress(progress);
                seekMessage.setText("Your SMS should arrive within " + millisUntilFinished / 1000 + " second.");
            }

            @Override
            public void onFinish() {
                otpWaitingDialog.dismiss();
                unRegisterSmsBroadcastReceiver();
            }
        }.start();
    }

    private class SMSReceiverBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();

                        try {
                            if (senderNum.contains("SPORTU") || message.toLowerCase().contains("Sports Unity".toLowerCase())) {
                                otpWaitingDialog.cancel();
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

    private class EnterOtpCustomComponentListener extends CustomComponentListener {

        private boolean success;
        private String currentTag = RESEND_OTP_REQUEST_TAG;

        private String toastMessage = "";

        public EnterOtpCustomComponentListener(ProgressBar progressBar) {
            super(CREATE_USER_REQUEST_TAG, progressBar, null);
        }

        @Override
        protected void showErrorLayout() {
            super.showErrorLayout();

            if (CommonUtil.isInternetConnectionAvailable(EnterOtpActivity.this)) {
                Toast.makeText(getApplicationContext(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.common_message_internet_not_available, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void handleErrorContent(String tag) {
            if (tag.equals(RESEND_OTP_REQUEST_TAG)) {
                UserUtil.setOtpSent(EnterOtpActivity.this, false);
            } else if (tag.equals(CREATE_USER_REQUEST_TAG)) {
                //nothing
            }
        }

        @Override
        public boolean handleContent(String tag, String content) {
            boolean success = false;
            if (tag.equals(RESEND_OTP_REQUEST_TAG)) {
                success = handleContentForOtpRequest(content);
            } else if (tag.equals(CREATE_USER_REQUEST_TAG)) {
                success = handleContentForCreateUserRequest(content);
            }
            return success;
        }

        @Override
        public void changeUI(String tag) {
            if (tag.equals(RESEND_OTP_REQUEST_TAG)) {
                Toast.makeText(EnterOtpActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                if (success) {
                    if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                        displayOtpWaitingDialog();
                        registerSmsBroadcastReceiver();
                    } else if (PermissionUtil.getInstance().requestPermission(EnterOtpActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.RECEIVE_SMS)))) {
                        displayOtpWaitingDialog();
                        registerSmsBroadcastReceiver();
                    }
                } else {
                    //nothing
                }
            } else if (tag.equals(CREATE_USER_REQUEST_TAG)) {
                if (success) {
                    if (!UserUtil.isFilterCompleted()) {
                        moveToNextActivity(ProfileCreationActivity.class);
                    } else {
                        ContactsHandler.getInstance().addCallToUpdateCompleteUserProfile(getApplicationContext());
                        ContactsHandler.getInstance().addCallToUpdateUserFavorites(getApplicationContext());
                        moveToNextActivity(MainActivity.class);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.otp_message_wrong_expired_token, Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void setCurrentTag(String currentTag) {
            this.currentTag = currentTag;
            this.success = false;
            this.toastMessage = "";
        }

        private boolean handleContentForOtpRequest(String content) {
            boolean success = false;
            try {
                JSONObject response = new JSONObject(content);
                int responseCode = response.getInt("status");
                String info = response.getString("info");

                if (responseCode == 200) {
                    UserUtil.setOtpSent(EnterOtpActivity.this, true);
                    this.success = true;
                    this.toastMessage = getResources().getString(R.string.otp_message_otp_sent);
                } else if (responseCode == 500) {
                    UserUtil.setOtpSent(EnterOtpActivity.this, false);
                    this.success = false;
                    this.toastMessage = getResources().getString(R.string.otp_message_invalid_number);
                } else {
                    UserUtil.setOtpSent(EnterOtpActivity.this, false);
                    this.success = false;
                    this.toastMessage = getResources().getString(R.string.otp_message_resending_failed);
                }
                Log.i("Enter Otp", "resend response info : " + info);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            success = true;
            return success;
        }

        private boolean handleContentForCreateUserRequest(String content) {
            boolean success = false;
            try {
                JSONObject response = new JSONObject(content);
                if (response.getString("status").equals("200")) {
                    String password = response.getString(Constants.REQUEST_PARAMETER_KEY_PASSWORD);
                    String userJid = response.getString(Constants.REQUEST_PARAMETER_KEY_USER_NAME);
                    TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_USER_JID, userJid);

                    TinyDB.getInstance(getApplicationContext()).putString(TinyDB.KEY_PASSWORD, password);
                    UserUtil.setOtpSent(EnterOtpActivity.this, false);
                    UserUtil.setUserRegistered(EnterOtpActivity.this, true);
                    if (!response.isNull("interests")) {
                        String favString = response.getString("interests");
                        if (!TextUtils.isEmpty(favString)) {

                            JSONArray array = new JSONArray(favString);

                            JSONArray favArray = new JSONArray();

                            if (array.length() > 0) {

                                for (int index = 0; index < array.length(); index++) {

                                    JSONObject object = array.getJSONObject(index);
                                    if (!object.isNull("properties")) {
                                        JSONObject favObject = object.getJSONObject("properties");
                                        favArray.put(favObject);
                                    }
                                }

                                if (favArray.length() > 0) {
                                    UserUtil.setFavouriteFilters(getApplicationContext(), favArray.toString());
                                    FavouriteItemWrapper.getInstance(getApplicationContext());
                                }
                            }

                        }
                    }
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

    }

}