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
import android.os.Looper;
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
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.controller.SelectSportsActivity;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserProfileHandler;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ImageUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileCreationActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, UserProfileHandler.ContentListener {

    private static final int LOAD_IMAGE_GALLERY_CAMERA = 1;
    private static final String LISTENER_KEY = "profile_creation_key";

    private CallbackManager callbackManager;
    private EditText nameText;
    private byte[] byteArray;

    private View.OnClickListener continueButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!nameText.getText().toString().isEmpty()) {
                beforeAsyncCall();

                String name = nameText.getText().toString();
                TinyDB.getInstance(ProfileCreationActivity.this).putString(TinyDB.KEY_PROFILE_NAME, name);

                int requestStatus = UserProfileHandler.getInstance().connectToXmppServer(ProfileCreationActivity.this, LISTENER_KEY);
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

    public void handleAddPhotoClick() {
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

        UserProfileHandler.getInstance().addContentListener(LISTENER_KEY, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        UserProfileHandler.getInstance().removeContentListener(LISTENER_KEY);
    }


    private void addListnerToProfilePicture() {
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        circleImageView.setOnClickListener(profilePictureonOnClickListener);
    }

    private void addListenerToContinueButton() {
        Button continueButton = (Button) findViewById(R.id.continue_button);
        nameText = (EditText) findViewById(R.id.nameView);
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
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        UserProfileHandler.getInstance().setFacebookDetails(this, loginButton, LISTENER_KEY, callbackManager);
    }

    private void setProfileImage(Bitmap image) {
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        circleImageView.setImageBitmap(image);
        byteArray = ImageUtil.getCompressedBytes(image);
    }

    private void onSuccessfulLogin() {
        moveOn();
    }

    private void onUnSuccessfulLogin() {
        if( CommonUtil.isInternetConnectionAvailable(ProfileCreationActivity.this) ){
            Toast.makeText(ProfileCreationActivity.this, R.string.common_message_internet_not_available, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ProfileCreationActivity.this, R.string.message_login_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void onUnSuccessfulVCardSubmit() {
        if( CommonUtil.isInternetConnectionAvailable(ProfileCreationActivity.this) ){
            Toast.makeText(ProfileCreationActivity.this, R.string.common_message_internet_not_available, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ProfileCreationActivity.this, R.string.message_submit_vcard_failed, Toast.LENGTH_SHORT).show();
        }
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
        UserUtil.setProfileCreated(this, true);

        if (UserUtil.isSportsSelected()) {
            Intent intent = new Intent(ProfileCreationActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(ProfileCreationActivity.this, SelectSportsActivity.class);
            startActivity(intent);
        }

        finish();
    }

    @Override
    public void handleContent(String requestTag, Object content) {

        if( requestTag.equals(UserProfileHandler.CONNECT_XMPP_SERVER_TAG) ){
            Boolean success = (Boolean)content;

            if( success == true ) {
                String phoneNumber = TinyDB.getInstance(ProfileCreationActivity.this).getString(TinyDB.KEY_USERNAME);
                String name = TinyDB.getInstance(ProfileCreationActivity.this).getString(TinyDB.KEY_PROFILE_NAME);
                String jid = TinyDB.getInstance(ProfileCreationActivity.this).getString(TinyDB.KEY_USER_JID);

                Contacts contacts = new Contacts(name, jid, phoneNumber, byteArray, -1, getResources().getString(R.string.default_status));
                UserProfileHandler.getInstance().submitUserProfile( ProfileCreationActivity.this, contacts, LISTENER_KEY);
            } else {
                ProfileCreationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onUnSuccessfulLogin();
                        afterAsyncCall();
                    }
                });
            }
        } else if ( requestTag.equals(UserProfileHandler.FB_REQUEST_TAG) && content != null) {
            final UserProfileHandler.ProfileDetail profileDetail = (UserProfileHandler.ProfileDetail) content;

            if (Looper.myLooper() == Looper.getMainLooper()) {
                setProfileDetail(profileDetail);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProfileDetail(profileDetail);
                    }
                });
            }
        } else if ( requestTag.equals(UserProfileHandler.SUBMIT_PROFILE_REQUEST_TAG) && content != null) {

            final boolean success = (boolean) content;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (success) {
                        onSuccessfulLogin();
                    } else {
                        onUnSuccessfulVCardSubmit();
                    }
                    afterAsyncCall();
                }
            });

        }
    }

    private void setProfileDetail(UserProfileHandler.ProfileDetail profileDetail) {
        nameText = (EditText) findViewById(R.id.nameView);
        nameText.setText(profileDetail.getName());

        if (profileDetail.getBitmap() != null) {
            setProfileImage(profileDetail.getBitmap());
        } else {
            //nothing
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

