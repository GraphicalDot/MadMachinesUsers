package com.sports.unity.common.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.BuildConfig;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.CommonUtil;

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

    public static final String BASE_URL_GET_PROMO_CODE = "http://" + BuildConfig.XMPP_SERVER_BASE_URL + "/get_referral_code";
    public static final String BASE_URL_AVAIL_PROMO_CODE = "http://" + BuildConfig.XMPP_SERVER_BASE_URL + "/redeem_code";
//    public static final String BASE_INVITE_USERS_URL = "https://play.google.com/store/apps/details?id=co.sports.unity&referrer=utm_source%3D";
//    public static final String END_INVITE_USERS_URL = "%26utm_medium%3Dsp-andr";


    public static final String REFERRAL_CODE = "referral_code";

    private ImageView promotionResultImage;
    private TextView promotionResultText;
    private LinearLayout promotionResultLayout;
    private TextView addPromo;
    private EditText promoCodeText;
    private TextView ownPromoCode;
    private TextView noPromoCodeText;
    private LinearLayout sharePromoCodeLayout;
    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        initToolbar();
        initView();
        setPromoCode();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        ImageView backNavigation = (ImageView) toolbar.findViewById(R.id.backarrow);

        toolbarTitle.setText("Promotions");
        backNavigation.setOnClickListener(onClickListener);

    }

    private void initView() {
        TextView detail = (TextView) findViewById(R.id.details);
        Button inviteFriends = (Button) findViewById(R.id.invite_frnds);

        promoCodeText = (EditText) findViewById(R.id.promo_text);
        addPromo = (TextView) findViewById(R.id.promo_code);
        promotionResultImage = (ImageView) findViewById(R.id.promotion_result);
        promotionResultText = (TextView) findViewById(R.id.promotion_text);
        promotionResultLayout = (LinearLayout) findViewById(R.id.promotion_result_layout);
        noPromoCodeText = (TextView) findViewById(R.id.no_promo_code_text);
        sharePromoCodeLayout = (LinearLayout) findViewById(R.id.share_your_promo_code);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_coordinator_layout);

        addPromo.setOnClickListener(onClickListener);
        inviteFriends.setOnClickListener(onClickListener);

        promoCodeText.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        setEditTextListeners();

        SpannableString link = makeLinkSpan("Details", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail();
            }
        });

        String detailText = "Share your code with your friends and family to avail new offers T&C Apply. ";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(detailText);
        spannableStringBuilder.append(link);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_blue)), detailText.length(),
                spannableStringBuilder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        detail.setText(spannableStringBuilder);
        makeLinksFocusable(detail);
        promoCodeText.clearFocus();
    }

    private SpannableString makeLinkSpan(CharSequence text, View.OnClickListener listener) {
        SpannableString link = new SpannableString(text);
        link.setSpan(new ClickableString(listener), 0, text.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        return link;
    }

    private void makeLinksFocusable(TextView tv) {
        MovementMethod m = tv.getMovementMethod();
        if ((m == null) || !(m instanceof LinkMovementMethod)) {
            if (tv.getLinksClickable()) {
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    private static class ClickableString extends ClickableSpan {
        private View.OnClickListener mListener;

        public ClickableString(View.OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }

    private void setEditTextListeners() {
        promoCodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                addPromo.setVisibility(View.VISIBLE);
                promotionResultLayout.setVisibility(View.GONE);
            }
        });

        promoCodeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    promoCodeText.setCursorVisible(true);
                } else {
                    promoCodeText.setCursorVisible(false);
                }
            }
        });
    }

    private void setPromoCode() {
        ownPromoCode = (TextView) findViewById(R.id.own_promocode);
        ownPromoCode.setVisibility(View.VISIBLE);
        String promoCode = TinyDB.getInstance(getApplicationContext()).getString(TinyDB.PROMO_CODE);
        if (promoCode.equals("") || promoCode == null) {
            getOwnPromoCode();
        } else {
            ownPromoCode.setText(promoCode);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.promo_code:
                    availPromoCode();
                    break;
                case R.id.invite_frnds:
                    inviteFriends();
                    break;
                case R.id.backarrow:
                    PromoActivity.this.finish();
            }
        }
    };

    private void inviteFriends() {
        String invitationURL = TinyDB.getInstance(getApplicationContext()).getString(TinyDB.INVITE_URL);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, invitationURL);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void showDetail() {
        String preText = "Please read the ";
        String terms = "Terms and Conditions";
        String postText = " that apply while sharing the code.";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(preText);

        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray2)), 0,
                spannableStringBuilder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString link = makeLinkSpan(terms, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.openLinkOnBrowser(PromoActivity.this, getResources().getString(R.string.link_of_terms_of_use));
            }
        });

        spannableStringBuilder.append(link);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_blue)), preText.length(),
                spannableStringBuilder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableStringBuilder.append(postText);

        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.gray2)), (preText.length() + terms.length()),
                spannableStringBuilder.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(spannableStringBuilder);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView msgTxt = (TextView) dialog.findViewById(android.R.id.message);
        makeLinksFocusable(msgTxt);
        Button negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getResources().getColor(R.color.app_theme_blue));

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

    private void availPromoCode() {
        if (TextUtils.isEmpty(promoCodeText.getText().toString())) {
            Toast.makeText(getApplicationContext(), "please enter a refferal code", Toast.LENGTH_SHORT).show();
        } else {
            promoCodeText.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);


            String promoDataAsJSON = getPromoDataAsJSON();
            new AvailPromoCode().execute(promoDataAsJSON);
        }
    }

    private String getPromoDataAsJSON() {
        JSONObject data = new JSONObject();
        try {
            data.put(SettingsActivity.USERNAME_KEY, TinyDB.getInstance(getApplicationContext()).getString(KEY_USER_JID));
            data.put(SettingsActivity.PASSWORD_KEY, TinyDB.getInstance(getApplicationContext()).getString(KEY_PASSWORD));
            data.put(SettingsActivity.APK_VERSION, "1.0");
            data.put(SettingsActivity.UDID, getDeviceId(getApplicationContext()));
            data.put(REFERRAL_CODE, promoCodeText.getText().toString());
            Log.i("data", data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data.toString();
    }

    class AvailPromoCode extends AsyncTask<String, Void, Void> {

        int success = -1;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = new ProgressBar(PromoActivity.this);
            progressBar.getIndeterminateDrawable().setColorFilter(PromoActivity.this.getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);

            pDialog = new ProgressDialog(PromoActivity.this);
            pDialog.setMessage("Redeeming...");
            pDialog.show();
            pDialog.setIndeterminateDrawable(progressBar.getIndeterminateDrawable());
        }

        @Override
        protected Void doInBackground(String... data) {
            HttpURLConnection httpURLConnection;
            ByteArrayInputStream byteArrayInputStream;
            ByteArrayOutputStream byteArrayOutputStream;
            URL postPromoCode;
            try {
                postPromoCode = new URL(BASE_URL_AVAIL_PROMO_CODE);
                Log.i("url", BASE_URL_AVAIL_PROMO_CODE);
                httpURLConnection = (HttpURLConnection) postPromoCode.openConnection();
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
                        success = 1;
                    } else {
                        success = 0;
                    }
                } else {
                    success = 0;
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

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            promoCodeText.clearFocus();
            if (success == 1) {
                onSuccesfulAvail();
            } else if (success == -1) {
                Toast.makeText(getApplicationContext(), "Check your internet connection and try again", Toast.LENGTH_SHORT).show();
            } else {
                onUnSuccesfulAvail();
            }
        }
    }

    private void onSuccesfulAvail() {
        promotionResultLayout.setVisibility(View.VISIBLE);
        addPromo.setVisibility(View.GONE);
        promotionResultImage.setImageResource(R.drawable.ic_valid);
        promotionResultText.setTextColor(getResources().getColor(R.color.green));
        promotionResultText.setText("Congratulations");


    }

    private void onUnSuccesfulAvail() {
        promotionResultLayout.setVisibility(View.VISIBLE);
        addPromo.setVisibility(View.GONE);
        promotionResultImage.setImageResource(R.drawable.ic_invalid);
        promotionResultText.setTextColor(getResources().getColor(R.color.brick_red));
        promotionResultText.setText("Invalid or Expired Code");
    }


    class GetPromoCode extends AsyncTask<String, Void, String> {

        private boolean success = false;
        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = (ProgressBar) findViewById(R.id.ownpromo_progress);
            progressBar.setVisibility(View.VISIBLE);
            ownPromoCode.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... data) {
            HttpURLConnection httpURLConnection;
            ByteArrayInputStream byteArrayInputStream;
            ByteArrayOutputStream byteArrayOutputStream;
            String ownPromoCode = null;
            URL postPrivacyData;
            try {
                postPrivacyData = new URL(BASE_URL_GET_PROMO_CODE);
                Log.i("url", BASE_URL_GET_PROMO_CODE);
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
                        success = true;
                        if (responseJsonContent.has("referral_code")) {
                            ownPromoCode = responseJsonContent.getString("referral_code");
                            String shortenerPromoUrl = responseJsonContent.getString("referral_url");
                            TinyDB.getInstance(getApplicationContext()).putString(TinyDB.INVITE_URL, shortenerPromoUrl);
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
            progressBar.setVisibility(View.GONE);
            if (success) {
                onSuccesfulFetchPromoCode(s);
            } else {
                onUnSuccesfulFetchPromoCode();
            }
        }
    }

    private void onSuccesfulFetchPromoCode(String s) {
        noPromoCodeText.setVisibility(View.GONE);
        sharePromoCodeLayout.setVisibility(View.VISIBLE);
        TinyDB.getInstance(getApplicationContext()).putString(TinyDB.PROMO_CODE, s);
        setPromoCode();
    }

    private void onUnSuccesfulFetchPromoCode() {
        noPromoCodeText.setVisibility(View.VISIBLE);
        sharePromoCodeLayout.setVisibility(View.GONE);
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "No Internet connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getOwnPromoCode();
                    }
                });

        snackbar.setActionTextColor(getResources().getColor(R.color.app_theme_blue));
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

}
