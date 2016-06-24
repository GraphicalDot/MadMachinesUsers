package com.sports.unity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.controller.SelectSportsActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserProfileHandler;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ImageUtil;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileCreationActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, UserProfileHandler.ContentListener {

    private static final int LOAD_IMAGE_GALLERY_CAMERA = 1;
    private static final String LISTENER_KEY = "profile_creation_key";

    private CallbackManager callbackManager;
    private EditText nameText;
    private byte[] byteArray;
    private String userName;
    public static final String CROP_FRAGMENT_TAG = "crop_fragment_tag";
    private View.OnClickListener continueButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!nameText.getText().toString().trim().isEmpty()) {
                beforeAsyncCall();

                userName = nameText.getText().toString();

                String phoneNumber = TinyDB.getInstance(ProfileCreationActivity.this).getString(TinyDB.KEY_USERNAME);
                String jid = TinyDB.getInstance(ProfileCreationActivity.this).getString(TinyDB.KEY_USER_JID);

                Contacts contacts = new Contacts(userName, jid, phoneNumber, byteArray, -1, getResources().getString(R.string.default_status), Contacts.AVAILABLE_NOT);
                UserProfileHandler.getInstance().submitUserProfile(ProfileCreationActivity.this, contacts, LISTENER_KEY);
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
            ImageView ImageView = (ImageView) findViewById(R.id.profile_image);

            byteArray = ImageUtil.handleImageAndSetToView(data, ImageView, ImageUtil.FULL_IMAGE_SIZE, ImageUtil.FULL_IMAGE_SIZE);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            initiateCrop(bitmap);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initiateCrop(Bitmap bitmap) {
        CropImageFragment cropImageFragment = new CropImageFragment();
        cropImageFragment.setProfileImage(bitmap);
        getSupportFragmentManager().beginTransaction().add(R.id.crop_container, cropImageFragment, CROP_FRAGMENT_TAG).addToBackStack(CROP_FRAGMENT_TAG).commit();
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
        handleAvatar();

        {
            if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                ContactsHandler.getInstance().addCallToSyncContacts(getApplicationContext());
            } else {
                if (PermissionUtil.getInstance().requestPermission(this, new ArrayList<String>(Arrays.asList(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)), getResources().getString(R.string.read_contact_permission_message), Constants.REQUEST_CODE_CONTACT_PERMISSION)) {
                    ContactsHandler.getInstance().addCallToSyncContacts(getApplicationContext());
                }
            }
        }

        /*
         * to set initial focus to edit text view and open keyboard.
         */
//        nameText.requestFocus();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void handleAvatar() {
        String base64Image = TinyDB.getInstance(this).getString(TinyDB.KEY_PHOTO);
        if (!TextUtils.isEmpty(base64Image)) {
            ImageView ImageView = (ImageView) findViewById(R.id.profile_image);
            byteArray = ImageUtil.handleAvatarAndSetToView(base64Image, ImageView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserProfileHandler.getInstance().addContentListener(LISTENER_KEY, this);

        if (UserProfileHandler.getInstance().requestInProgress()) {
            //nothing
        } else {
            afterAsyncCall();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        UserProfileHandler.getInstance().removeContentListener(LISTENER_KEY);
    }

    private void addListnerToProfilePicture() {
        ImageView ImageView = (ImageView) findViewById(R.id.profile_image);
        ImageView.setOnClickListener(profilePictureonOnClickListener);
    }

    private void addListenerToContinueButton() {
        Button continueButton = (Button) findViewById(R.id.continue_button);
        nameText = (EditText) findViewById(R.id.nameView);
        continueButton.setOnClickListener(continueButtonOnClickListener);
    }

    private void initFacebookLogin() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    private void addFacebookCallback() {
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        UserProfileHandler.getInstance().setFacebookDetails(this, loginButton, LISTENER_KEY, callbackManager);
    }

    public void setProfileImage(Bitmap image) {
        ImageView ImageView = (ImageView) findViewById(R.id.profile_image);
        ImageView.setImageBitmap(ImageUtil.getRoundedCornerBitmap(image, ImageView));
        byteArray = ImageUtil.getCompressedBytes(image);
    }

    private void onSuccessfulLogin() {
        moveOn();
    }

    private void onUnSuccessfulLogin() {
        if (CommonUtil.isInternetConnectionAvailable(ProfileCreationActivity.this)) {
            Toast.makeText(ProfileCreationActivity.this, R.string.message_login_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ProfileCreationActivity.this, R.string.common_message_internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void onUnSuccessfulVCardSubmit() {
        if (CommonUtil.isInternetConnectionAvailable(ProfileCreationActivity.this)) {
            Toast.makeText(ProfileCreationActivity.this, R.string.message_submit_failed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ProfileCreationActivity.this, R.string.common_message_internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void beforeAsyncCall() {
        Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setClickable(false);
        ImageView ImageView = (ImageView) findViewById(R.id.profile_image);
        ImageView.setClickable(false);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setVisibility(View.GONE);

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
    }

    private void afterAsyncCall() {
        Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setClickable(true);
        ImageView ImageView = (ImageView) findViewById(R.id.profile_image);
        ImageView.setClickable(true);

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setVisibility(View.VISIBLE);

        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    private void moveOn() {
        UserUtil.setProfileCreated(this, true);
        ContactsHandler.getInstance().addCallToProcessPendingActions(this);

        {
            ChatScreenApplication application = (ChatScreenApplication) getApplication();
            application.userLoginTrack();
        }

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
        if (requestTag.equals(UserProfileHandler.FB_REQUEST_TAG) && content != null) {
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
        } else if (requestTag.equals(UserProfileHandler.SUBMIT_PROFILE_REQUEST_TAG) && content != null) {

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

        } else if (requestTag.equals(UserProfileHandler.DOWNLOADING_FACEBOOK_IMAGE_TAG)) {
            beforeAsyncCall();
        }
    }

    private void setProfileDetail(UserProfileHandler.ProfileDetail profileDetail) {
        afterAsyncCall();

        nameText = (EditText) findViewById(R.id.nameView);
        nameText.setText(profileDetail.getName());

        if (profileDetail.getBitmap() != null) {
            initiateCrop(profileDetail.getBitmap());
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
        } else if (requestCode == Constants.REQUEST_CODE_CONTACT_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                ContactsHandler.getInstance().addCallToSyncContacts(getApplicationContext());
            } else {
                PermissionUtil.getInstance().showSnackBar(this, getString(R.string.permission_denied));
            }
        }
    }

}

