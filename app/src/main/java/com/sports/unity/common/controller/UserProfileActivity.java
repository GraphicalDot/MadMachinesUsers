package com.sports.unity.common.controller;

import android.Manifest;
import android.app.Activity;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ImageUtil;

import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends CustomAppCompatActivity {

    private static final String INFO_EDIT = "EDIT PROFILE";
    private static final String INFO_SAVE = "SAVE PROFILE";
    private static final String ADD_FRIEND = "ADD FRIEND";

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
    private ProgressBar progressBar;
    private TextView currentStatus;
    private boolean ownProfile;

    private LayoutInflater mInflater;

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
            toolbarActionButton.setText(ADD_FRIEND);
            toolbarActionButton.setBackground(getResources().getDrawable(R.drawable.round_edge_blue_box));
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

    private void onClickSaveButton() {
        if (!TextUtils.isEmpty(name.getText().toString()) && !TextUtils.isEmpty(status.getText().toString())) {
            new SubmitVCardAsyncTask().execute();
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
        progressBar.setVisibility(View.GONE);
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

        byte[] imageArray = getIntent().getByteArrayExtra("profilePicture");
        if (imageArray != null) {
            profileImage.setImageBitmap(BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length));
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
        new FetchVcardTask(getIntent().getStringExtra("number")).execute();
    }

    private void addFacebookCallback() {
        callbackManager = CallbackManager.Factory.create();
        name = (EditText) findViewById(R.id.name);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button_facebook);
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
                                    name.setText(object.getString("name"));
                                    TinyDB.getInstance(UserProfileActivity.this).putString(TinyDB.KEY_PROFILE_NAME, name.getText().toString());
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

    private void setProfileImage(Bitmap image) {
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.user_picture);
        circleImageView.setImageBitmap(image);
        byteArray = ImageUtil.getCompressedBytes(image);
    }

    private class FetchVcardTask extends AsyncTask<Void, Void, String> {

        private boolean success = false;

        String number = null;

        public FetchVcardTask(String number) {
            this.number = number;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String favorite = null;
            try {
                VCard card = new VCard();
                card.load(XMPPClient.getConnection(), number + "@mm.io");
                favorite = card.getField("fav_list");
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return favorite;
        }

        @Override
        protected void onPostExecute(String favorite) {
            progressBar.setVisibility(View.GONE);
            if (success) {
                successfulVCardLoad(favorite);
            } else {
                onUnSuccessfulVCardLoad();
            }
        }
    }

    private void successfulVCardLoad(String favorite) {
        ArrayList<FavouriteItem> savedList = null;
        if (favorite != null) {
            savedList = FavouriteItemWrapper.getInstance(this).getFavListOfOthers(favorite);
            setFavouriteProfile(savedList);
        }
    }

    private void onUnSuccessfulVCardLoad() {
        Toast.makeText(UserProfileActivity.this, R.string.message_submit_vcard_failed, Toast.LENGTH_SHORT).show();
    }

    private class SubmitVCardAsyncTask extends AsyncTask<Void, Void, Void> {
        private boolean success = false;
        String nickname = name.getText().toString();
        String status = currentStatus.getText().toString();

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TinyDB.getInstance(UserProfileActivity.this).putString(TinyDB.KEY_PROFILE_NAME, nickname);
                TinyDB.getInstance(UserProfileActivity.this).putString(TinyDB.KEY_PROFILE_STATUS, status);
                VCardManager manager = VCardManager.getInstanceFor(XMPPClient.getConnection());
                VCard vCard = new VCard();
                vCard.setNickName(nickname);
                vCard.setAvatar(byteArray);
                vCard.setMiddleName(status);
                vCard.setJabberId(XMPPClient.getConnection().getUser());
                manager.saveVCard(vCard);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
            progressBar.setVisibility(View.GONE);
            if (success) {
                successfulVCardSubmit();
            } else {
                onUnSuccessfulVCardSubmit();
            }
        }
    }

    private void onUnSuccessfulVCardSubmit() {
        Toast.makeText(UserProfileActivity.this, R.string.message_submit_vcard_failed, Toast.LENGTH_SHORT).show();
    }

    private void successfulVCardSubmit() {
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

        for (int i = 0; i < leagues.size(); i++) {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
            textView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
            textView.setText(leagues.get(i).getName());
            textView.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
            leagueList.addView(linearLayout);

        }

        for (int i = 0; i < teams.size(); i++) {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
            textView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
            textView.setText(teams.get(i).getName());
            textView.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
            teamList.addView(linearLayout);
        }

        for (int i = 0; i < players.size(); i++) {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
            textView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
            textView.setText(players.get(i).getName());
            textView.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
            playerList.addView(linearLayout);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void onBack() {
        if (toolbarActionButton.getText().equals(INFO_SAVE) && progressBar.getVisibility() == View.INVISIBLE) {
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

        setInitDataOwn();
    }
}
