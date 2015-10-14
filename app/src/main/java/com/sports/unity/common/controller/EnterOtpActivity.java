package com.sports.unity.common.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EnterOtpActivity extends AppCompatActivity {

    TextView otpText;
    EditText Otp;
    Button sendOtp;
    Button resend;
    static String url = "http://54.169.217.88/register?";
    final static int DEFAULT_TIMEOUT = 20 * 1000;
    Button editNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(com.sports.unity.R.layout.activity_enter_otp);
        sendOtp = (Button) findViewById(com.sports.unity.R.id.sendOtpButton);
        sendOtp.setVisibility(View.INVISIBLE);
        editNumber = (Button) findViewById(R.id.editnumber);
        editNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        resend = (Button) findViewById(R.id.resend);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                requestParams.add("phone_number", "91" + getIntent().getStringExtra(EnterPhoneActivity.EXTRA_MESSAGE));
                new AsyncHttpClient().get(url, requestParams, new JsonHttpResponseHandler() {
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(getApplicationContext(), "Resending...", Toast.LENGTH_SHORT).show();
                        Log.i("Success", "Sent Data");

                        try {
                            Log.i("Info  : ", response.getString("info"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        otpText = (TextView) findViewById(com.sports.unity.R.id.enterotpText);
        otpText.setText("Enter the verification number we sent to +91 " + getIntent().getStringExtra(EnterPhoneActivity.EXTRA_MESSAGE));
        Otp = (EditText) findViewById(com.sports.unity.R.id.enterOtp);
        Otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4)
                    sendOtp.setVisibility(View.VISIBLE);
                else if (s.length() != 4)
                    sendOtp.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void createUser() {
        RequestParams requestParams = new RequestParams();
        requestParams.add("phone_number", "91" + getIntent().getStringExtra(EnterPhoneActivity.EXTRA_MESSAGE));
        requestParams.add("auth_code", Otp.getText().toString());
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);
        String url = "http://54.169.217.88/create?";
        client.get(url, requestParams, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String password;
                Log.i("Success", "Sent Data");

                try {
                    Log.i("Info  : ", response.getString("info"));
                    if (response.getString("status").equals("200")) {
                        password = response.getString("password");
                        Log.i("password", password);
                        TinyDB.getInstance(getApplicationContext()).putString("password", password);
                        TinyDB.getInstance(getApplicationContext()).putBoolean("registered", true);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(EnterOtpActivity.this, ProfileCreationActivity.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong or expired token", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                String resp = String.valueOf(statusCode);
                resp = resp + "   Failed";
                Log.i("Response : ", resp);

            }

            public void onFailure(int statusCode, Header[] headers, JSONObject response) {
                String resp = String.valueOf(statusCode);
                resp = resp + "   Failed";
                Log.i("Response ", resp);
            }

            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                String resp = String.valueOf(statusCode);
                resp = resp.concat("   Failed No response");
                Log.i("No Response Sent", resp);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.sports.unity.R.menu.menu_enter_otp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.sports.unity.R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}



