package com.sports.unity.common.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.model.FavouriteItem;
import com.sports.unity.common.model.FavouriteItemWrapper;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.common.view.SlidingTabLayout;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ImageUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends CustomAppCompatActivity {

    private String userOrGroupName;
    private byte[] userOrGroupImage = null;
    private String groupServerId;
    private String phoneNumber;

    private TextView addFriend;
    private ImageView editStatus;
    private EditText name;
    private EditText status;
    private FrameLayout facebook_button;
    private CircleImageView profileimage;
    private ImageView edit_name;

    private byte[] byteArray;
    private CallbackManager callbackManager;
    private String profilePicUrl;
    private boolean paused = false;
    private boolean vCardSaved = false;
    ArrayList<FavouriteItem> savedList;
    private static final int LOAD_IMAGE_GALLERY_CAMERA = 1;
    private LayoutInflater mInflater;
    private boolean ownProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XMPPService.startService(this);


        setContentView(R.layout.activity_user_profile);

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle bundle = getIntent().getExtras();

        try {
            ownProfile = bundle.getBoolean(Constants.IS_OWN_PROFILE, false);
        } catch (NullPointerException booleanNull) {

        }

        initView();
        setToolbar();

        if (ownProfile) {
            setInitDataOwn();
        } else {
            setInitDataOthers();
        }

    }

    private void getIntentExtrasForOthers() {
        userOrGroupName = getIntent().getStringExtra("name");
        userOrGroupImage = getIntent().getByteArrayExtra("profilePicture");
        groupServerId = getIntent().getStringExtra("groupServerId");
        phoneNumber = getIntent().getStringExtra("number");

    }

    private void getIntentExtrasForOwn() {
        userOrGroupName = getIntent().getStringExtra("name");
        userOrGroupImage = getIntent().getByteArrayExtra("profilePicture");
//        groupServerId = getIntent().getStringExtra("groupServerId");
        phoneNumber = getIntent().getStringExtra("phoneNumber");

    }

    private void setToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        addFriend = (TextView) toolbar.findViewById(R.id.add_friend);

        ImageView back = (ImageView) toolbar.findViewById(R.id.backarrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        profileimage = (CircleImageView) findViewById(R.id.user_picture);
        //editStatus = (ImageView) findViewById(R.id.edit);
        // edit_name = (ImageView) findViewById(R.id.edit_name);
        facebook_button = (FrameLayout) findViewById(R.id.faceBook_btn);
        name = (EditText) findViewById(R.id.name);
        status = (EditText) findViewById(R.id.your_status);
        name.setFocusable(false);
        status.setFocusable(false);
    }

    private void setInitDataOwn() {

        addFriend.setVisibility(View.GONE);
        facebook_button.setVisibility(View.VISIBLE);
        // editStatus.setVisibility(View.VISIBLE);
        // edit_name.setVisibility(View.VISIBLE);

//        edit_name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(UserProfileActivity.this, EditNameAndStatus.class);
//                intent.putExtra("Data", name.getText().toString());
//                intent.putExtra("Title", "Enter your name ");
//                startActivity(intent);
//            }
//        });
//
//        editStatus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(UserProfileActivity.this, EditNameAndStatus.class);
//                intent.putExtra("Data", status.getText().toString());
//                intent.putExtra("Title", "Change your status");
//                startActivity(intent);
//            }
//        });

        getIntentExtrasForOwn();

        name.setText(userOrGroupName);

        if (userOrGroupImage == null) {
            profileimage.setImageResource(R.drawable.ic_user);
        } else {
            profileimage.setImageBitmap(BitmapFactory.decodeByteArray(userOrGroupImage, 0, userOrGroupImage.length));
        }

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfileImage();
            }
        });

        facebook_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ArrayList<FavouriteItem> savedList = FavouriteItemWrapper.getInstance().getFavList(this);

        setFavouriteProfile(savedList);

    }

    private void setInitDataOthers() {

        addFriend.setVisibility(View.VISIBLE);
        facebook_button.setVisibility(View.GONE);
//        editStatus.setVisibility(View.GONE);
//        edit_name.setVisibility(View.GONE);

        getIntentExtrasForOthers();

        name.setText(userOrGroupName);

        if (userOrGroupImage == null) {
            if (groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
                profileimage.setImageResource(R.drawable.ic_user);
            } else {
                profileimage.setImageResource(R.drawable.ic_group);
            }
        } else {
            profileimage.setImageBitmap(BitmapFactory.decodeByteArray(userOrGroupImage, 0, userOrGroupImage.length));
        }

        JSONObject jsonObject = null;
        JSONArray jsonArray = null;

        try {
            VCard vCard = new VCard();
            vCard.load(XMPPClient.getConnection(), phoneNumber + "@mm.io");

            Log.i("Fav String", "" + phoneNumber);
            String favourite = vCard.getField("fav_list");
            Log.i("Fav String", "" + favourite);
            savedList = FavouriteItemWrapper.getInstance().getFavListOfOthers(favourite);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setFavouriteProfile(savedList);
    }

    private void changeProfileImage() {

        if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
            handleAddPhotoClick();
        } else {
            if (PermissionUtil.getInstance().requestPermission(UserProfileActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.camera_and_external_storage_permission_message), Constants.REQUEST_CODE_CAMERA_EXTERNAL_STORAGE_PERMISSION)) {
                handleAddPhotoClick();
            }
        }
    }

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

        } else {
            //nothing
        }
    }

    private void setFavouriteProfile(ArrayList<FavouriteItem> savedList) {

        List<FavouriteItem> teams = new ArrayList<>();
        List<FavouriteItem> leagues = new ArrayList<FavouriteItem>();
        List<FavouriteItem> players = new ArrayList<>();

        /*for (String name : savedList) {
            if (name.contains(Constants.NAV_COMP)) {
                name = name.replace(Constants.NAV_COMP, "");
                leagues.add(name);
            } else if (name.contains(Constants.NAV_TEAM)) {
                name = name.replace(Constants.NAV_TEAM, "");
                teams.add(name);
            } else if (name.contains(Constants.NAV_PLAYER)) {
                name = name.replace(Constants.NAV_PLAYER, "");
                players.add(name);
            }
        }*/
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

        //TextView textView = (TextView) getLayoutInflater().inflate(R.layout.textview_user_profile_activity, null);

        for (int i = 0; i < leagues.size(); i++) {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
            textView.setText(leagues.get(i).getName());
            leagueList.addView(linearLayout);

        }

        for (int i = 0; i < teams.size(); i++) {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
            textView.setText(teams.get(i).getName());
            teamList.addView(linearLayout);
        }

        for (int i = 0; i < players.size(); i++) {
            LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.textview_user_profile_activity, null);
            TextView textView = (TextView) linearLayout.findViewById(R.id.list_item);
            textView.setText(players.get(i).getName());
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
}
