package com.sports.unity.common.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.sports.unity.common.model.TinyDB.KEY_PASSWORD;
import static com.sports.unity.common.model.TinyDB.KEY_USER_JID;
import static com.sports.unity.util.CommonUtil.getDeviceId;

public class PromoActivity extends CustomAppCompatActivity {

    public static final String BASE_URL = "http://54.169.217.88/get_referral_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        initToolbar();
        initView();
        setPromoCode();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.detail:
                    showDetail();
                    break;
                case R.id.promo_code:
                    availPromoCode();
                    break;
                case R.id.invite_frnds:
                    inviteFriends();
                    break;
            }
        }
    };

    private void availPromoCode() {
        //TODO
    }

    private void inviteFriends() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void showDetail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Heading");
        builder.setMessage("This is PhotoShop's version of Lorem Ipsum");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initView() {
        TextView detail = (TextView) findViewById(R.id.detail);
        TextView addPromo = (TextView) findViewById(R.id.promo_code);
        Button inviteFriends = (Button) findViewById(R.id.invite_frnds);

        detail.setOnClickListener(onClickListener);
        addPromo.setOnClickListener(onClickListener);
        inviteFriends.setOnClickListener(onClickListener);
    }

    private void setPromoCode() {
        TextView ownPromoCode = (TextView) findViewById(R.id.own_promocode);
        String promoCode = TinyDB.getInstance(getApplicationContext()).getString(TinyDB.PROMO_CODE);
        if (promoCode.equals("") || promoCode == null) {
            getOwnPromoCode();
        } else {
            ownPromoCode.setText(promoCode);
        }
    }

    private void getOwnPromoCode() {
        String dataAsJson = getAppDataAsJSON();
        new GetPromoCode().execute(dataAsJson);
    }

    private String getAppDataAsJSON() {
        JSONObject data = new JSONObject();
        try {
            data.put(SettingsActivity.USERNAME_KEY, TinyDB.getInstance(getApplicationContext()).getString(KEY_USER_JID));
            data.put(SettingsActivity.PASSWORD_KEY, TinyDB.getInstance(getApplicationContext()).getString(KEY_PASSWORD));
            data.put(SettingsActivity.APK_VERSION, "1.0");
            data.put(SettingsActivity.UDID, getDeviceId(getApplicationContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data.toString();
    }

    private void initToolbar() {

    }


    class GetPromoCode extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... data) {
            HttpURLConnection httpURLConnection;
            ByteArrayInputStream byteArrayInputStream;
            ByteArrayOutputStream byteArrayOutputStream;
            String ownPromoCode = null;
            URL postPrivacyData;
            try {
                postPrivacyData = new URL(BASE_URL);
                Log.i("url", BASE_URL);
                httpURLConnection = (HttpURLConnection) postPrivacyData.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("POST");

                byteArrayInputStream = new ByteArrayInputStream(data[0].getBytes());
                OutputStream outputStream = httpURLConnection.getOutputStream();

                byte chunk[] = new byte[4096];
                int read = 0;
                while ((read = byteArrayInputStream.read(chunk)) != -1) {
                    outputStream.write(chunk, 0, read);
                }

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    byteArrayOutputStream = new ByteArrayOutputStream();
                    InputStream inputStream = httpURLConnection.getInputStream();

                    byte chunk2[] = new byte[4096];
                    int read2 = 0;
                    while ((read2 = inputStream.read(chunk2)) != -1) {
                        byteArrayOutputStream.write(chunk2, 0, read2);
                    }

                    String content = String.valueOf(byteArrayOutputStream.toString());
                    JSONObject responseJsonContent = new JSONObject(content);
                    if (responseJsonContent.getInt("status") == 200) {
                        if (responseJsonContent.has("referral_code")) {
                            ownPromoCode = responseJsonContent.getString("referral_code");
                        } else {
                            ownPromoCode = null;
                        }
                    }
                } else {
                    //nothing
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ownPromoCode;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TinyDB.getInstance(getApplicationContext()).putString(TinyDB.PROMO_CODE, s);
            setPromoCode();
        }
    }

}
