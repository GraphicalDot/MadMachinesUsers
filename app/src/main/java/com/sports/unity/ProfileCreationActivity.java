package com.sports.unity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.TinyDB;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileCreationActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private EditText nameText;
    private String profilePicUrl;
    private byte[] byteArray;


    private TinyDB tinyDB = TinyDB.getInstance(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFacebookLogin();

        setContentView(R.layout.activity_profile_creation);

        addFacebookCallback();
        addListenerToContinueButton();
    }

    private void addListenerToContinueButton(){
        Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinyDB.putString("name", nameText.getText().toString());
                new LoginAndPushVCardThread().start();
            }
        });
    }

    private void initFacebookLogin(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.sports.unity", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    private void addFacebookCallback(){
        callbackManager = CallbackManager.Factory.create();
        nameText = (EditText) findViewById(R.id.nameView);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), loginResult.getAccessToken().getUserId().toString(), Toast.LENGTH_LONG).show();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    nameText.setText(object.getString("name"));
                                    JSONObject data = response.getJSONObject();
                                    if (data.has("picture")) {
                                        profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                        new DownloadImageTask().execute(profilePicUrl);
                                        Log.i("PICURL : ", profilePicUrl);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setProfileImage(Bitmap image){
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        circleImageView.setImageBitmap(image);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
    }

    private void onSuccessfulLogin(){
        tinyDB.putBoolean("Registered", true);
        Intent serviceIntent = new Intent(ProfileCreationActivity.this, XMPPService.class);
        startService(serviceIntent);
        Intent intent = new Intent(ProfileCreationActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void onUnSuccessfulLogin(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText( ProfileCreationActivity.this, R.string.message_login_failed, Toast.LENGTH_SHORT).show();
            }
        });
        //TODO
    }

    private void onUnSuccessfulVCardSubmit(){
        //TODO
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap image) {
            setProfileImage(image);
        }
    }

    private class LoginAndPushVCardThread extends Thread {

        @Override
        public void run() {
            boolean success = false;
            try {
                XMPPClient.getConnection().login(tinyDB.getString("username"), tinyDB.getString("password"));
                success = true;
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if( success ) {
                new Vcard().execute();
            } else {
                onUnSuccessfulLogin();
            }
        }

    }

    private class Vcard extends AsyncTask<Void, Void, Void> {
        private boolean success = false;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                VCardManager manager = VCardManager.getInstanceFor(XMPPClient.getConnection());
                VCard vCard = new VCard();
                vCard.setNickName(tinyDB.getString("name"));
                vCard.setAvatar(byteArray);
                vCard.setMiddleName( getResources().getString(R.string.default_status));
                vCard.setJabberId(XMPPClient.getConnection().getUser());
                manager.saveVCard(vCard);
                success = true;
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            if( success ){
                onSuccessfulLogin();
            } else {
                onUnSuccessfulVCardSubmit();
            }
        }
    }

    public static XMPPTCPConnection returnConnection() {
        return XMPPClient.getConnection();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.sports.unity.R.menu.menu_profile_creation, menu);
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
