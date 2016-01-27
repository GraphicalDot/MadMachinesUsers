package com.sports.unity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.sports.unity.common.controller.SelectSportsActivity;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ImageUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileCreationActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private CallbackManager callbackManager;
    private EditText nameText;
    private String profilePicUrl;
    private byte[] byteArray;

    private static final int LOAD_IMAGE_GALLERY_CAMERA = 1;
    private boolean paused = false;
    private boolean vCardSaved = false;
    private boolean moved = false;

    private View.OnClickListener continueButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!nameText.getText().toString().isEmpty()) {
                beforeAsyncCall();
                TinyDB.getInstance(ProfileCreationActivity.this).putString(TinyDB.KEY_PROFILE_NAME, nameText.getText().toString());
                new LoginAndPushVCardThread().start();
            } else {
                Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
            }
        }

    };

    private View.OnClickListener profilePictureonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                handleAddPhotoClick();
            } else {
                if (PermissionUtil.getInstance().requestPermission(ProfileCreationActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.camera_and_external_storage_permission_message), Constants.REQUEST_CODE_CAMERA_EXTERNAL_STORAGE_PERMISSION)) {
                    handleAddPhotoClick();
                }
            }
        }

    };

    private void handleAddPhotoClick() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");


        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
        chooser.putExtra(Intent.CATEGORY_OPENABLE, intent);

        Intent[] intentArray = {cameraIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooser, LOAD_IMAGE_GALLERY_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_IMAGE_GALLERY_CAMERA && resultCode == Activity.RESULT_OK) {
            CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);

            byteArray = ImageUtil.handleImageAndSetToView(data, circleImageView, ImageUtil.SMALL_THUMB_IMAGE_SIZE, ImageUtil.SMALL_THUMB_IMAGE_SIZE);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XMPPService.startService(this);
        initFacebookLogin();

        setContentView(R.layout.activity_profile_creation);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.progress_light), android.graphics.PorterDuff.Mode.MULTIPLY);


        addFacebookCallback();
        addListenerToContinueButton();
        addListnerToProfilePicture();


        /*
         * to set initial focus to edit text view and open keyboard.
         */
//        nameText.requestFocus();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        paused = false;
        if (vCardSaved) {
            moveOn();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        paused = true;
    }


    private void addListnerToProfilePicture() {
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        circleImageView.setOnClickListener(profilePictureonOnClickListener);
    }

    private void addListenerToContinueButton() {
        Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(continueButtonOnClickListener);
    }

    private void initFacebookLogin() {
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

    private void addFacebookCallback() {
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
                                    TinyDB.getInstance(getApplicationContext()).putString((String) object.get("name"), TinyDB.KEY_PROFILE_NAME);
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
                Toast.makeText(getApplicationContext(), R.string.profile_facebook_login_cancelled, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(), R.string.profile_facebook_login_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setProfileImage(Bitmap image) {
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        circleImageView.setImageBitmap(image);
        byteArray = ImageUtil.getCompressedBytes(image);
    }

    private void onSuccessfulLogin() {
        vCardSaved = true;
//        TinyDB.getInstance(ProfileCreationActivity.this).putBoolean(TinyDB.KEY_REGISTERED, true);

        if (!paused) {
            moveOn();
        } else {
            //nothing
        }
    }

    private void onUnSuccessfulLogin() {
        Toast.makeText(ProfileCreationActivity.this, R.string.message_login_failed, Toast.LENGTH_SHORT).show();
    }

    private void onUnSuccessfulVCardSubmit() {
        Toast.makeText(ProfileCreationActivity.this, R.string.message_submit_vcard_failed, Toast.LENGTH_SHORT).show();
    }

    private void beforeAsyncCall() {
        Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setClickable(false);
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        circleImageView.setClickable(false);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setVisibility(View.GONE);


        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void afterAsyncCall() {
        Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setClickable(true);
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        circleImageView.setClickable(true);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setVisibility(View.VISIBLE);

        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    private void moveOn() {
        if (!moved) {
            moved = true;

            UserUtil.setProfileCreated(this, true);
//            XMPPService.startService(ProfileCreationActivity.this);

            if (UserUtil.isSportsSelected()) {
                Intent intent = new Intent(ProfileCreationActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ProfileCreationActivity.this, SelectSportsActivity.class);
                startActivity(intent);
            }

            finish();
        }
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
            boolean success = XMPPClient.getInstance().reconnectConnection();

            if (success) {
                success = XMPPClient.getInstance().authenticateConnection(ProfileCreationActivity.this);
            }

            if (success == true) {
                new SubmitVCardAsyncTask().execute();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        afterAsyncCall();
                        onUnSuccessfulLogin();
                    }
                });
            }
        }

    }


    private class SubmitVCardAsyncTask extends AsyncTask<Void, Void, Void> {
        private boolean success = false;

        @Override
        protected Void doInBackground(Void... params) {
            try {

                VCardManager manager = VCardManager.getInstanceFor(XMPPClient.getConnection());
                VCard vCard = new VCard();
                vCard.setNickName(TinyDB.getInstance(ProfileCreationActivity.this).getString(TinyDB.KEY_PROFILE_NAME));
                vCard.setAvatar(byteArray);
                vCard.setMiddleName(getResources().getString(R.string.default_status));
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
            afterAsyncCall();
            if (success) {
                onSuccessfulLogin();
            } else {
                onUnSuccessfulVCardSubmit();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_CAMERA_EXTERNAL_STORAGE_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                handleAddPhotoClick();
            } else {
                PermissionUtil.getInstance().showSnackBar(this, getString(R.string.permission_denied));
            }
        }
    }
}

