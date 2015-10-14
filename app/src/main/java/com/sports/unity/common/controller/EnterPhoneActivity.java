package com.sports.unity.common.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sports.unity.common.model.TinyDB;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class EnterPhoneActivity extends AppCompatActivity {

    Button b;
    EditText phoneno;
    TelephonyManager telephonyManager;
    public final static String EXTRA_MESSAGE = "PHONE_NUMBER";
    static String url = "http://54.169.217.88/register?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(com.sports.unity.R.layout.activity_enter_phone);
        b = (Button) findViewById(com.sports.unity.R.id.getotp);
        phoneno = (EditText) findViewById(com.sports.unity.R.id.phoneNumber);
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
            Toast.makeText(getApplicationContext(), "Sim not found", Toast.LENGTH_SHORT).show();
        } else {
            String mPhoneNumber = telephonyManager.getLine1Number();
            phoneno.setText(mPhoneNumber);
        }
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneno.getText().toString().length() < 10) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid 10 digit number", Toast.LENGTH_SHORT).show();
                } else {
                    RequestParams requestParams = new RequestParams();
                    requestParams.add("phone_number", "91" + phoneno.getText().toString());
                    new AsyncHttpClient().get(url, requestParams, new JsonHttpResponseHandler() {
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Log.i("Success", "Sent Data");

                            try {
                                Log.i("Info  : ", response.getString("info"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Intent intent = new Intent(EnterPhoneActivity.this, EnterOtpActivity.class);
                    String message = phoneno.getText().toString();
                    TinyDB.getInstance(getApplicationContext()).putString("username", "91" + phoneno.getText().toString());
                    intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.sports.unity.R.menu.menu_enter_phone, menu);
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
