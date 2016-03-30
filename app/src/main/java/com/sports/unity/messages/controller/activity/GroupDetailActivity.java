package com.sports.unity.messages.controller.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.fragment.ContactsFragment;
import com.sports.unity.messages.controller.fragment.GroupCreateFragment;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.PubSubMessaging;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ImageUtil;

import java.util.ArrayList;

public class GroupDetailActivity extends CustomAppCompatActivity {

    private String groupName = null;
    private String groupDescription = null;
    private byte[] groupImage;

    private boolean isGroupEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        String groupServerId = getIntent().getStringExtra("groupServerId");
        if( groupServerId != null ){
            isGroupEditing = true;
        } else {
            //nothing
        }

        if( isGroupEditing ){
            addGroupInfoFragment();
        } else {
            addGroupCreateFragment();
        }
    }

    private void addGroupCreateFragment() {
        GroupCreateFragment groupCreateFragment = new GroupCreateFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, groupCreateFragment).commit();
    }

    private void addGroupInfoFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("name", getIntent().getStringExtra("name"));
        bundle.putString("profilePicture", getIntent().getStringExtra("profilePicture"));
        bundle.putString("groupServerId", getIntent().getStringExtra("groupServerId"));
        bundle.putLong("chatID", getIntent().getLongExtra("chatID", SportsUnityDBHelper.DEFAULT_ENTRY_ID));

        GroupInfoFragment groupInfoFragment = new GroupInfoFragment();
        groupInfoFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, groupInfoFragment).commit();
    }

    public void moveToMembersListFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.INTENT_KEY_CONTACT_FRAGMENT_USAGE, ContactsFragment.USAGE_FOR_MEMBERS);
        bundle.putStringArrayList(Constants.INTENT_KEY_ADDED_MEMBERS, new ArrayList<String>());

        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment, "as_member");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        setToolBarForMembersList();
    }

    public void moveToMembersListFragment(SportsUnityDBHelper.GroupParticipants groupParticipants) {
        ArrayList<String> addedMembers = new ArrayList<>();
        for(Contacts contacts : groupParticipants.usersInGroup){
            addedMembers.add(contacts.jid);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.INTENT_KEY_CONTACT_FRAGMENT_USAGE, ContactsFragment.USAGE_FOR_MEMBERS);
        bundle.putStringArrayList(Constants.INTENT_KEY_ADDED_MEMBERS, addedMembers);

        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment, "as_member");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        setToolBarForMembersList();
    }

    public void setGroupDetails(String groupName, String groupDescription, byte[] groupImageArray) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.groupImage = groupImageArray;
    }

    private void setToolBarForMembersList() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

//        ((ImageView) toolbar.findViewById(R.id.backarrow)).setImageResource(R.drawable.ic_menu_back);
        toolbar.findViewById(R.id.backarrow).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView title = (TextView) toolbar.findViewById(R.id.title);
        title.setText(R.string.group_title_add_members);

        TextView actionView = (TextView) toolbar.findViewById(R.id.actionButton);
        if( isGroupEditing ) {
            actionView.setText(R.string.done);
        } else {
            actionView.setText(R.string.create_group);
        }
        actionView.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedBold());
        actionView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ContactsFragment fragment = (ContactsFragment) fragmentManager.findFragmentByTag("as_member");

                if( isGroupEditing ) {
                    ArrayList<Contacts> selectedMembersList = fragment.getSelectedMembersList();
                    if(selectedMembersList.size() > 0){
                        new AddNewMembers(fragment).execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Select at least one member, to add in group.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new createNewGroup(fragment).execute();
                }

            }

        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    class createNewGroup extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;
        private ContactsFragment contactsFragment;

        public createNewGroup(ContactsFragment fragment) {
            contactsFragment = fragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = new ProgressBar(GroupDetailActivity.this);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
            progressDialog = new ProgressDialog(GroupDetailActivity.this);
            progressDialog.setMessage("creating group...");
            progressDialog.setIndeterminateDrawable(progressBar.getIndeterminateDrawable());
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return createGroup(contactsFragment.getSelectedMembersList());
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressDialog.dismiss();
            if( success == true ){
                finish();
            } else {
                Toast.makeText(GroupDetailActivity.this, R.string.oops_try_again, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AddNewMembers extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;
        private ContactsFragment contactsFragment;

        public AddNewMembers(ContactsFragment fragment) {
            contactsFragment = fragment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProgressBar progressBar = new ProgressBar(GroupDetailActivity.this);
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);

            progressDialog = new ProgressDialog(GroupDetailActivity.this);
            progressDialog.setMessage("updating members in group...");
            progressDialog.setIndeterminateDrawable(progressBar.getIndeterminateDrawable());
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String groupJid = getIntent().getStringExtra("groupServerId");
            boolean success = addMembers( groupJid, contactsFragment.getSelectedMembersList());
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressDialog.dismiss();
            if( success ){
                onBackPressed();
            } else {
                Toast.makeText(GroupDetailActivity.this, R.string.oops_try_again, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean addMembers(String groupJid, ArrayList<Contacts> selectedMembers) {
        boolean success = false;
        ArrayList<String> membersJid = new ArrayList<>();
        for (Contacts contacts : selectedMembers) {
            membersJid.add(contacts.jid + "@mm.io");
        }

        PubSubMessaging pubSubMessaging = PubSubMessaging.getInstance();
        success = pubSubMessaging.addMembers(groupJid, membersJid, getApplicationContext());
        if (success) {
            PubSubMessaging.getInstance().sendIntimationAboutAffiliationListChanged(getApplicationContext(), groupJid);

            ArrayList<Long> members = new ArrayList<>();
            for (Contacts c : selectedMembers) {
                members.add(c.id);
            }

            long chatId = getIntent().getLongExtra("chatID", SportsUnityDBHelper.DEFAULT_ENTRY_ID);
            SportsUnityDBHelper.getInstance(getApplicationContext()).createGroupUserEntry(chatId, members);
        } else {
            //nothing
        }
        return success;
    }

    private boolean createGroup(ArrayList<Contacts> selectedMembers) {
        boolean success = false;
        String currentUserJID = TinyDB.getInstance(this).getString(TinyDB.KEY_USER_JID);

        String groupJID = currentUserJID + "" + System.currentTimeMillis();
        groupJID = groupJID + "%" + groupName + "%%";

        ArrayList<String> membersJid = new ArrayList<>();
        for (Contacts contacts : selectedMembers) {
            membersJid.add(contacts.jid + "@mm.io");
        }

        String groupImageAsBase64 = null;
        if( groupImage != null ){
            groupImageAsBase64 = Base64.encodeToString(groupImage, Base64.DEFAULT);
        }

        PubSubMessaging pubSubMessaging = PubSubMessaging.getInstance();
        success = pubSubMessaging.createNode(groupJID, groupName, groupImageAsBase64, membersJid, this);

        return success;
    }

}