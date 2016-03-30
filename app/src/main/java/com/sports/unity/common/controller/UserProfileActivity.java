package com.sports.unity.common.controller;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserProfileHandler;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.activity.PeopleAroundMeMap;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.playerprofile.cricket.PlayerCricketBioDataActivity;
import com.sports.unity.playerprofile.football.PlayerProfileView;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ImageUtil;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends CustomAppCompatActivity implements UserProfileHandler.ContentListener {

    private static final String INFO_EDIT = "EDIT PROFILE";
    private static final String INFO_SAVE = "SAVE PROFILE";
    private static final String ADD_FRIEND = "ADD FRIEND";

    private static final String LISTENER_KEY = "profile_listener_key";

    private static final int LOAD_IMAGE_GALLERY_CAMERA = 1;
    private String uName;
    private String uStatus;

    private CallbackManager callbackManager;
    private String profilePicUrl = null;

    private TextView toolbarActionButton;
    private EditText name;
    private EditText status;
    private CircleImageView profileImage;
    private TextView editFavourite, statusTitle;
    private LinearLayout statusView;
    private LinearLayout statusList;
    private LinearLayout favDetails;
    private FrameLayout fbButton;
    private byte[] byteArray;
    private byte[] imageArray;
    private ProgressBar progressBar;
    private TextView currentStatus;
    private boolean ownProfile;

    private LayoutInflater mInflater;

    private ProgressBar progessBar;
    private ProgressDialog dialog;

    private int statusValue[] = {R.string.available, R.string.busy, R.string.movie, R.string.work};

    private Drawable oldBackgroundForNameEditView = null;
    private Drawable oldBackgroundForStatusEditView = null;

    private TextView.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (toolbarActionButton.getText().equals(ADD_FRIEND)) {
                onClickAddFriend();
            } else {
                if (toolbarActionButton.getText().equals(INFO_SAVE)) {
                    onClickSaveButton();
                } else {
                    onClickEditButton();
                }
            }
        }

    };

    private View.OnClickListener editFavoritesClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onClickEditFavorites();
        }

    };

    private View.OnClickListener statusClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            onClickStatus(view);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFacebookLogin();
        setContentView(R.layout.activity_user_profile);

        mInflater = LayoutInflater.from(this);
        ownProfile = getIntent().getBooleanExtra(Constants.IS_OWN_PROFILE, false);

        setToolbar(ownProfile);
        initView(ownProfile);
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

    private void setToolbar(boolean ownProfile) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        LinearLayout clickAction = (LinearLayout) toolbar.findViewById(R.id.click_action);
        clickAction.setOnClickListener(onClickListener);

        toolbarActionButton = (TextView) toolbar.findViewById(R.id.toolbar_action_button);
        toolbarActionButton.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());

        if (ownProfile) {
            toolbarActionButton.setText(INFO_EDIT);
        } else {
            if (getIntent().getBooleanExtra("otherChat", false)) {
                toolbarActionButton.setText(ADD_FRIEND);
                toolbarActionButton.setBackground(getResources().getDrawable(R.drawable.round_edge_blue_box));
            } else {
                toolbarActionButton.setVisibility(View.GONE);
            }
        }

        ImageView backButton = (ImageView) toolbar.findViewById(R.id.backarrow);
        backButton.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, true));
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBack();
            }

        });
    }

    private void onClickAddFriend() {
        //TODO
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

    private void onClickSaveButton() {
        showInDeterminateProgress("updating details on vcard...");
        if (!TextUtils.isEmpty(name.getText().toString()) && !TextUtils.isEmpty(status.getText().toString())) {
            String nickname = name.getText().toString();
            String status = this.status.getText().toString();
            String phoneNumber = TinyDB.getInstance(this).getString(TinyDB.KEY_USERNAME);
            String jid = TinyDB.getInstance(UserProfileActivity.this).getString(TinyDB.KEY_USER_JID);

            Contacts contacts = new Contacts(nickname, jid, phoneNumber, byteArray, -1, status, Contacts.AVAILABLE_NOT);
            int requestStatus = UserProfileHandler.getInstance().submitUserProfile(UserProfileActivity.this, contacts, LISTENER_KEY);
            if (requestStatus == UserProfileHandler.REQUEST_STATUS_FAILED) {
                onUnSuccessfulVCardSubmit();
            }
        } else {
            if (TextUtils.isEmpty(name.getText().toString())) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(status.getText().toString())) {
                Toast.makeText(this, "Please enter your status", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onClickEditButton() {
        favDetails.setVisibility(View.GONE);
        statusView.setVisibility(View.VISIBLE);

        fbButton.setVisibility(View.VISIBLE);

        toolbarActionButton.setText(INFO_SAVE);

        name.setEnabled(true);
        name.setBackground(oldBackgroundForNameEditView);
        name.getBackground().setColorFilter(getResources().getColor(R.color.app_theme_blue), PorterDuff.Mode.SRC_IN);

        int pL = status.getPaddingLeft();
        int pT = status.getPaddingTop();
        int pR = status.getPaddingRight();
        int pB = status.getPaddingBottom();

        status.setEnabled(true);
        status.setBackground(oldBackgroundForStatusEditView);
        status.setPadding(pL, pT, pR, pB);
        status.getBackground().setColorFilter(getResources().getColor(R.color.app_theme_blue), PorterDuff.Mode.SRC_IN);

        profileImage.setBorderColor(getResources().getColor(R.color.app_theme_blue));
        profileImage.setEnabled(true);
        profileImage.setBorderWidth(2);
        addListnerToProfilePicture();
    }

    private void addStatusList() {

        statusList = (LinearLayout) findViewById(R.id.list);
        statusList.removeAllViews();
        for (int i = 0; i < statusValue.length; i++) {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
            ImageView flag = (ImageView) linearLayout.findViewById(R.id.flag);
            flag.setVisibility(View.GONE);

            TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
            textView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
            textView.setText(statusValue[i]);
            textView.setTag(statusValue[i]);
            textView.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
            textView.setOnClickListener(statusClickListener);
            statusList.addView(linearLayout);
        }
    }

    private void onClickStatus(View view) {
        Integer status = (Integer) view.getTag();
        if (status != null) {
            this.status.setText(getResources().getString(status));
        } else {
            //nothing
        }
    }

    private void addListnerToProfilePicture() {
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.user_picture);
        circleImageView.setOnClickListener(profilePictureonOnClickListener);
    }

    private void initView(boolean ownProfile) {

        favDetails = (LinearLayout) findViewById(R.id.favDetails);
        fbButton = (FrameLayout) findViewById(R.id.faceBook_btn);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.GONE);
        name = (EditText) findViewById(R.id.name);
        oldBackgroundForNameEditView = name.getBackground();

        name.setText(getIntent().getStringExtra("name"));
        name.setBackground(getResources().getDrawable(R.drawable.round_edge_black_box));
        name.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
        name.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());
        name.setEnabled(false);
        favDetails = (LinearLayout) findViewById(R.id.favDetails);
        status = (EditText) findViewById(R.id.your_status);
        oldBackgroundForStatusEditView = status.getBackground();

        status.setText(getIntent().getStringExtra("status"));
        status.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
        status.setBackground(new ColorDrawable(Color.TRANSPARENT));
        status.setEnabled(false);

        progessBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        currentStatus = (TextView) findViewById(R.id.current_status);
        profileImage = (CircleImageView) findViewById(R.id.user_picture);
        profileImage.setEnabled(false);

        editFavourite = (TextView) findViewById(R.id.edit_fav);

        editFavourite.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        editFavourite.setVisibility(View.VISIBLE);

        statusView = (LinearLayout) findViewById(R.id.status_list);
        statusView.setVisibility(View.GONE);

        setcustomFont();

        if (ownProfile) {
            setInitDataOwn();
        } else {
            setInitDataOthers();
        }
    }

    private void setcustomFont() {
        status.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView currentStatus = (TextView) findViewById(R.id.current_status);
        currentStatus.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView favTeam = (TextView) findViewById(R.id.fav_team);
        favTeam.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView favLeague = (TextView) findViewById(R.id.fav_league);
        favLeague.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView favPlayer = (TextView) findViewById(R.id.fav_player);
        favPlayer.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView fav = (TextView) findViewById(R.id.fav);
        fav.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedBold());
        statusTitle = (TextView) findViewById(R.id.select_status);
        statusTitle.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
    }

    private void setInitDataOwn() {

        addFacebookCallback();

        addStatusList();


        profileImage = (CircleImageView) findViewById(R.id.user_picture);

        byteArray = getIntent().getByteArrayExtra("profilePicture");
        if (byteArray != null) {
            profileImage.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        } else {
            profileImage.setImageResource(R.drawable.ic_user);
        }

        ArrayList<FavouriteItem> savedList = FavouriteItemWrapper.getInstance(this).getFavList();
        setFavouriteProfile(savedList);

        editFavourite.setOnClickListener(editFavoritesClickListener);
    }

    private void onClickEditFavorites() {
        moveToSelectSports();
    }

    private void moveToSelectSports() {
        Intent intent = new Intent(this, SelectSportsActivity.class);
        intent.putExtra(Constants.RESULT_REQUIRED, true);
        startActivityForResult(intent, Constants.REQUEST_CODE_PROFILE);
    }

    private void setInitDataOthers() {
        editFavourite = (TextView) findViewById(R.id.edit_fav);

        editFavourite.setVisibility(View.GONE);
        statusView.setVisibility(View.GONE);


        profileImage = (CircleImageView) findViewById(R.id.user_picture);
        byte[] imageArray = getIntent().getByteArrayExtra("profilePicture");
        if (imageArray != null) {
            profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length));
        } else {
            if (getIntent().getStringExtra("groupServerId").equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
                profileImage.setImageResource(R.drawable.ic_user);
            } else {
                //TODO
            }
        }
        String jid = getIntent().getStringExtra("jid");
        progessBar.setVisibility(View.VISIBLE);
        int requestStatus = UserProfileHandler.getInstance().loadProfile(getApplicationContext(), jid, LISTENER_KEY);
        if (requestStatus == UserProfileHandler.REQUEST_STATUS_FAILED) {
            onUnSuccessfulVCardLoad();
        }
    }

    private void showInDeterminateProgress(String message) {
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);

        dialog = ProgressDialog.show(UserProfileActivity.this, "", message, true);
        dialog.setIndeterminateDrawable(progressBar.getIndeterminateDrawable());
    }

    private void dismissInDeterminateProgress() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void addFacebookCallback() {

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        callbackManager = CallbackManager.Factory.create();
        UserProfileHandler.getInstance().setFacebookDetails(this, loginButton, LISTENER_KEY, callbackManager);
    }

    @Override
    public void handleContent(String requestTag, final Object content) {
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
        } else if (requestTag.equals(UserProfileHandler.LOAD_PROFILE_REQUEST_TAG) && content != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    VCard card = (VCard) content;
                    if (card != null) {
                        successfulVCardLoad(card);
                    } else {
                        onUnSuccessfulVCardLoad();
                    }
                }
            });

        } else if (requestTag.equals(UserProfileHandler.SUBMIT_PROFILE_REQUEST_TAG) && content != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final boolean success = (boolean) content;
                    progressBar.setVisibility(View.GONE);
                    if (success) {
                        successfulVCardSubmit();
                    } else {
                        onUnSuccessfulVCardSubmit();
                    }
                }
            });

        }
    }

    private void setProfileDetail(UserProfileHandler.ProfileDetail profileDetail) {
        name = (EditText) findViewById(R.id.name);
        name.setText(profileDetail.getName());

        if (profileDetail.getBitmap() != null) {
            setProfileImage(profileDetail.getBitmap());
        } else {
            //nothing
        }
    }

    private void updateUserDetail(VCard card) {
        //TODO

        try {
            int contactAvailableStatus = getIntent().getIntExtra(ChatScreenActivity.INTENT_KEY_CONTACT_AVAILABLE_STATUS, Contacts.AVAILABLE_BY_MY_CONTACTS);

            String userStatus = card.getMiddleName();
            imageArray = card.getAvatar();
            String nickname = card.getNickName();

            if (imageArray != null) {
                profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length));
            } else {
                //nothing
            }

            if( contactAvailableStatus <= Contacts.AVAILABLE_BY_OTHER_CONTACTS ) {
                name.setText(nickname);
            } else {
                //nothing
            }

            status.setText(userStatus);

            {
                String favorite = card.getField("fav_list");
                ArrayList<FavouriteItem> savedList = FavouriteItemWrapper.getInstance(this).getFavListOfOthers(favorite);
                setFavouriteProfile(savedList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onUnSuccessfulVCardLoad();
        }

    }

    private void setProfileImage(Bitmap image) {
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.user_picture);
        circleImageView.setImageBitmap(image);
        byteArray = ImageUtil.getCompressedBytes(image);
    }

    private void successfulVCardLoad(VCard vCard) {
        progressBar.setVisibility(View.GONE);
        updateUserDetail(vCard);
    }

    private void onUnSuccessfulVCardLoad() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(UserProfileActivity.this, R.string.message_load_vcard_failed, Toast.LENGTH_SHORT).show();
    }

    private void onUnSuccessfulVCardSubmit() {
        dismissInDeterminateProgress();
        Toast.makeText(UserProfileActivity.this, R.string.message_submit_vcard_failed, Toast.LENGTH_SHORT).show();
    }

    private void successfulVCardSubmit() {
        dismissInDeterminateProgress();
        initViewUI();
        statusView.setVisibility(View.GONE);
        Toast.makeText(UserProfileActivity.this, R.string.message_submit_vcard_sucess, Toast.LENGTH_SHORT).show();
    }

    private void initViewUI() {
        favDetails.setVisibility(View.VISIBLE);
        profileImage.setBorderWidth(0);
        profileImage.setEnabled(false);
        fbButton.setVisibility(View.GONE);
        toolbarActionButton.setText(INFO_EDIT);
        name.setEnabled(false);
        name.setBackground(getResources().getDrawable(R.drawable.round_edge_black_box));
        status.setEnabled(false);
        status.setBackground(new ColorDrawable(Color.TRANSPARENT));
        name.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
        status.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
    }

    private View.OnClickListener profilePictureonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                handleAddPhotoClick();
            } else {
                if (PermissionUtil.getInstance().requestPermission(UserProfileActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.camera_and_external_storage_permission_message), Constants.REQUEST_CODE_CAMERA_EXTERNAL_STORAGE_PERMISSION)) {
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
            CircleImageView circleImageView = (CircleImageView) findViewById(R.id.user_picture);
            byteArray = ImageUtil.handleImageAndSetToView(data, circleImageView, ImageUtil.SMALL_THUMB_IMAGE_SIZE, ImageUtil.SMALL_THUMB_IMAGE_SIZE);
        } else if (requestCode == Constants.REQUEST_CODE_PROFILE && resultCode == Activity.RESULT_OK) {
            setFavouriteProfile(FavouriteItemWrapper.getInstance(this).getFavList());
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void setFavouriteProfile(ArrayList<FavouriteItem> savedList) {

        List<FavouriteItem> teams = new ArrayList<>();
        List<FavouriteItem> leagues = new ArrayList<FavouriteItem>();
        List<FavouriteItem> players = new ArrayList<>();

        for (FavouriteItem f : savedList) {
            if (f.getFilterType().equals(Constants.FILTER_TYPE_LEAGUE)) {
                leagues.add(f);
            } else if (f.getFilterType().equals(Constants.FILTER_TYPE_TEAM)) {
                teams.add(f);
            } else if (f.getFilterType().equals(Constants.FILTER_TYPE_PLAYER)) {
                players.add(f);
            }
        }
        Collections.sort(teams);
        Collections.sort(leagues);
        Collections.sort(players);


        LinearLayout teamList = (LinearLayout) findViewById(R.id.teamlist);
        LinearLayout leagueList = (LinearLayout) findViewById(R.id.leaguelist);
        LinearLayout playerList = (LinearLayout) findViewById(R.id.playerlist);
        teamList.removeAllViews();
        leagueList.removeAllViews();
        playerList.removeAllViews();

        //TextView textView = (TextView) getLayoutInflater().inflate(R.layout.textview_user_profile_activity, null);

        if (leagues.size() > 0) {
            for (int i = 0; i < leagues.size(); i++) {
                LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
                TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
                textView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
                textView.setText(leagues.get(i).getName());
                ImageView iv = (ImageView) linearLayout.findViewById(R.id.flag);
                String uri = null;
                try {
                    uri = leagues.get(i).getFlagImageUrl();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (uri != null) {
                    Glide.with(this).load(Uri.parse(uri)).placeholder(R.drawable.ic_no_img).into(iv);
                } else {
                    iv.setVisibility(View.VISIBLE);
                    iv.setImageResource(R.drawable.ic_no_img);
                }
                linearLayout.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
                leagueList.addView(linearLayout);
                final FavouriteItem favouriteItem = leagues.get(i);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        teamAndLeagueDetails(favouriteItem.getId(), favouriteItem.getName(), favouriteItem.getFilterType());
                    }
                });

            }
        } else {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
            textView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
            if (ownProfile) {
                textView.setText("Add favourite leagues");
            } else {
                textView.setText("No favourite leagues");
            }
            textView.setTextColor(getResources().getColor(R.color.gray1));
            leagueList.addView(linearLayout);
        }

        if (teams.size() > 0) {
            for (int i = 0; i < teams.size(); i++) {
                LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
                TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
                textView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
                textView.setText(teams.get(i).getName());
                ImageView iv = (ImageView) linearLayout.findViewById(R.id.flag);
                String uri = null;
                try {
                    uri = teams.get(i).getFlagImageUrl();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (uri != null) {
                    Glide.with(this).load(Uri.parse(uri)).placeholder(R.drawable.ic_no_img).into(iv);
                } else {
                    iv.setVisibility(View.VISIBLE);
                    iv.setImageResource(R.drawable.ic_no_img);
                }
                linearLayout.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
                teamList.addView(linearLayout);
                final FavouriteItem favouriteItem = teams.get(i);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        teamAndLeagueDetails(favouriteItem.getId(), favouriteItem.getName(), favouriteItem.getFilterType());
                    }
                });

            }
        } else {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
            textView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
            if (ownProfile) {
                textView.setText("Add favourite teams");
            } else {
                textView.setText("No favourite teams");
            }
            textView.setTextColor(getResources().getColor(R.color.gray1));
            teamList.addView(linearLayout);
        }

        if (players.size() > 0) {
            for (int i = 0; i < players.size(); i++) {
                LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
                TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
                textView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());

                final FavouriteItem favouriteItem = players.get(i);
                textView.setText(favouriteItem.getName());
                ImageView iv = (ImageView) linearLayout.findViewById(R.id.flag);
                String uri = null;
                try {
                    uri = players.get(i).getFlagImageUrl();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (uri != null) {
                    Glide.with(this).load(Uri.parse(uri)).placeholder(R.drawable.ic_no_img).into(iv);
                } else {
                    iv.setVisibility(View.VISIBLE);
                    iv.setImageResource(R.drawable.ic_no_img);
                }
                linearLayout.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
                playerList.addView(linearLayout);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playerProfile(favouriteItem.getName(), favouriteItem.getId(), favouriteItem.getSportsType());
                    }
                });

            }
        } else {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
            textView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
            if (ownProfile) {
                textView.setText("Add favourite players");
            } else {
                textView.setText("No favourite players");
            }
            textView.setTextColor(getResources().getColor(R.color.gray1));
            playerList.addView(linearLayout);

        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack() {
        if (toolbarActionButton.getText().equals(INFO_SAVE) && progressBar.getVisibility() == View.GONE) {
            AlertDialog.Builder build = new AlertDialog.Builder(UserProfileActivity.this);
            build.setTitle("Discard Edits ? ");
            build.setMessage("If you cancel now, your edits will be discarded.");
            build.setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    discardChanges();
                }

            });
            build.setNegativeButton("KEEP", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    //nothing

                }

            });

            AlertDialog dialog = build.create();
            dialog.show();
        } else {
            Intent i = new Intent();
            i.putExtra(ChatScreenActivity.INTENT_KEY_IMAGE, imageArray);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    private void discardChanges() {
        favDetails.setVisibility(View.VISIBLE);
        statusView.setVisibility(View.GONE);
        fbButton.setVisibility(View.GONE);

        profileImage.setBorderWidth(0);
        toolbarActionButton.setText(INFO_EDIT);

        name.setEnabled(false);
        name.setText(getIntent().getStringExtra("name"));
        name.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));
        name.setBackground(getResources().getDrawable(R.drawable.round_edge_black_box));

        status.setEnabled(false);
        status.setText(getIntent().getStringExtra("status"));
        status.setBackground(new ColorDrawable(Color.TRANSPARENT));
        status.setTextColor(getResources().getColor(R.color.ColorPrimaryDark));

        profileImage.setEnabled(false);
        profileImage.setBackground(new ColorDrawable(Color.TRANSPARENT));
        byteArray = getIntent().getByteArrayExtra("profilePicture");
        if (byteArray != null) {
            profileImage.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        } else {
            profileImage.setImageResource(R.drawable.ic_user);
        }
        setInitDataOwn();
    }

    private void playerProfile(String playerName, String playerId, String sportsType) {

        if (Constants.SPORTS_TYPE_FOOTBALL.equals(sportsType)) {
            Intent intent = new Intent(UserProfileActivity.this, PlayerProfileView.class);
            intent.putExtra(Constants.INTENT_KEY_ID, playerId);
            intent.putExtra(Constants.INTENT_KEY_PLAYER_NAME,playerName);
            startActivity(intent);
        } else {
            Intent intent = new Intent(UserProfileActivity.this, PlayerCricketBioDataActivity.class);
            intent.putExtra(Constants.INTENT_KEY_ID, playerId);
            intent.putExtra(Constants.INTENT_KEY_PLAYER_NAME,playerName);
            startActivity(intent);
        }
    }

    private void teamAndLeagueDetails(String id, String name, String type) {
        Intent intent = new Intent(this, TeamLeagueDetails.class);
        intent.putExtra("Id", id);
        intent.putExtra("Name", name);
        intent.putExtra("Type", type);
        startActivity(intent);
    }

}
