package com.sports.unity.common.controller;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.messages.controller.BlockUnblockUserHelper;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.peoplearound.PeopleAroundActivity;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendRequestsActivity extends CustomAppCompatActivity implements BlockUnblockUserHelper.BlockUnblockListener {

    public static final String DUMMY_JABBER_ID = "dummy_friend_request_id";

    private ArrayList<Contacts> contactsWithPendingRequests = new ArrayList<>();
    private ListView listView;

    private ActivityActionListener activityActionListener = new ActivityActionListener() {

        @Override
        public void handleAction(int id, Object object) {
            updateAdapterList(id, object);
        }

        @Override
        public void handleAction(int id) {

        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, Object mediaContent) {

        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, String thumbnailImage, Object mediaContent) {

        }
    };

    private void updateAdapterList(int id, Object object) {
        if (id == ActivityActionHandler.EVENT_FRIEND_REQUEST_ACCEPTED) {
            ArrayList<Contacts> list = ((FriendRequestsActivityAdapter) listView.getAdapter()).getContactsArrayList();
            for (Contacts contact : list) {
                if (contact.jid.equals(object)) {
                    contact.requestStatus = Contacts.REQUEST_ACCEPTED;
                    break;
                }
            }
            FriendRequestsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((FriendRequestsActivityAdapter) listView.getAdapter()).notifyDataSetChanged();
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ActivityActionHandler.getInstance().addActionListener(ActivityActionHandler.REQEUSTS_SCREEN_KEY, DUMMY_JABBER_ID, activityActionListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ActivityActionHandler.getInstance().removeActionListener(ActivityActionHandler.REQEUSTS_SCREEN_KEY, DUMMY_JABBER_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        initToolbar();
        initView();
        initList();
    }

    private void initList() {
        listView = (ListView) findViewById(R.id.pending_requests_list);
        FriendRequestsActivityAdapter friendRequestsActivityAdapter = new FriendRequestsActivityAdapter(this, R.layout.list_contacts_pending_requests_item, contactsWithPendingRequests);
        listView.setAdapter(friendRequestsActivityAdapter);
        listView.setEmptyView(findViewById(R.id.error_layout));
        updateContent();
    }

    private void updateContent() {
        contactsWithPendingRequests = SportsUnityDBHelper.getInstance(getApplicationContext()).getPendingContacts();
        if (contactsWithPendingRequests.size() > 0) {
            if (listView != null) {
                ((FriendRequestsActivityAdapter) listView.getAdapter()).updateList(contactsWithPendingRequests);
            }
        }
    }

    private void initView() {
        Button findFriends = (Button) findViewById(R.id.find_friends);
        findFriends.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        findFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPeopleAroundMe();
            }
        });
    }

    private void openPeopleAroundMe() {
        if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {  // android below marshmallow so not runtime permissiosn required
            openActivity();
        } else {
            if (PermissionUtil.getInstance().requestPermission(FriendRequestsActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)), getResources().getString(R.string.location_permission_message), Constants.REQUEST_CODE_LOCATION_PERMISSION)) {
                openActivity();
            }
        }
    }

    private void openActivity() {
        Intent intent = new Intent(FriendRequestsActivity.this, PeopleAroundActivity.class);
        intent.putExtra("tabPosition", 2);
        startActivity(intent);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textView.setText("Friend Requests");
        ImageView imageView = (ImageView) toolbar.findViewById(R.id.backarrow);
        imageView.setImageResource(R.drawable.ic_menu_back);
        imageView.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, true));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBlock(boolean success, String jid) {
        ArrayList<Contacts> list = ((FriendRequestsActivityAdapter) listView.getAdapter()).getContactsArrayList();
        for (Contacts contact : list) {
            if (contact.jid.equals(jid)) {
                contact.requestStatus = Contacts.REQUEST_BLOCKED;
                break;
            }
        }
        FriendRequestsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((FriendRequestsActivityAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
        });
        SportsUnityDBHelper.getInstance(getApplicationContext()).updateContactFriendRequestStatus(jid, Contacts.REQUEST_BLOCKED);
    }

    @Override
    public void onUnblock(boolean success) {
        Toast.makeText(getApplicationContext(), "block operation was unsuccesful, check your internet connection and try again", Toast.LENGTH_SHORT).show();
    }
}
