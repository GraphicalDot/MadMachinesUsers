package com.sports.unity.messages.controller.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.PubSubUtil;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.fragment.ChatFragmentDialogListAdapter;
import com.sports.unity.messages.controller.fragment.ContactsFragment;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.PubSubMessaging;
import com.sports.unity.util.AlertDialogUtil;
import com.sports.unity.util.NotificationHandler;

import org.jivesoftware.smack.chat.Chat;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mad on 2/22/2016.
 */
public class GroupInfoFragment extends Fragment {

    private ListView participantsList;

    private int chatID;
    private String groupJID;
    private String name;
    private byte[] byteArray;
    private boolean blockStatus;

    private SportsUnityDBHelper.GroupParticipants groupParticipants = null;
    private boolean isAdmin = false;

    private EventListener eventListener = new EventListener();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getIntentExtras(getArguments());

        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(getContext());
        groupParticipants = sportsUnityDBHelper.getGroupParticipants(chatID);
        String currentUserJid = TinyDB.getInstance(getContext()).getString(TinyDB.KEY_USER_JID);
        isAdmin = groupParticipants.adminJids.contains(currentUserJid);

        View view = inflater.inflate(R.layout.group_info_activity, container, false);
        initToolbar();
        initViews(view);

//        try {
////            PubSubMessaging.getInstance().getNodeConfig(groupJID);
//            PubSubUtil.getNodeConfig(groupJID);
//
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }

        return view;
    }

    private void getIntentExtras(Bundle bundle) {
        name = bundle.getString("name");
        byteArray = bundle.getByteArray("profilePicture");
        groupJID = bundle.getString("jid");
        chatID = bundle.getInt("chatID");
        blockStatus = bundle.getBoolean("blockStatus");
    }

    private void initViews(View view) {
        CircleImageView groupImage = (CircleImageView) view.findViewById(R.id.group_image);
        TextView groupName = (TextView) view.findViewById(R.id.group_name);
        TextView groupInfo = (TextView) view.findViewById(R.id.group_info);
        TextView groupCount = (TextView) view.findViewById(R.id.part_count);
        TextView delete = (TextView) view.findViewById(R.id.delete_group);

        if (blockStatus) {
            delete.setText("DELETE GROUP");
        } else {
            delete.setText("EXIT AND DELETE GROUP");
        }

        groupName.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoCondensedBold());
        groupInfo.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoRegular());
        delete.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoRegular());

        groupName.setText(name);
        if (byteArray != null) {
            groupImage.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
        } else {
            groupImage.setImageResource(R.drawable.ic_group);
        }

        String partCount = getResources().getString(R.string.participant_count);
        partCount = String.format(partCount, groupParticipants.usersInGroup.size());
        groupCount.setText(partCount);

        participantsList = (ListView) view.findViewById(R.id.participants_list);
        participantsList.setAdapter(new GroupParticipantsAdapter(getContext(), groupParticipants.usersInGroup, groupParticipants.adminJids, isAdmin));
        participantsList.setOnItemClickListener(eventListener);
        setListViewHeightBasedOnItems(participantsList);
        delete.setOnClickListener(onExitAndDeleteListener);
    }

    private void initToolbar() {
//        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_group_info);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView title = (TextView) toolbar.findViewById(R.id.title);
        title.setTextColor(getResources().getColor(android.R.color.black));
        title.setVisibility(View.GONE);

        ((ImageView) toolbar.findViewById(R.id.backarrow)).setImageResource(R.drawable.ic_menu_back_blk);

        toolbar.findViewById(R.id.backarrow).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        TextView toolbarEdit = (TextView) toolbar.findViewById(R.id.actionButton);
        toolbarEdit.setText("Edit");
        toolbarEdit.setTextColor(getResources().getColor(R.color.app_theme_blue));
        toolbarEdit.setTypeface(FontTypeface.getInstance(getActivity()).getRobotoRegular());
//        toolbarEdit.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        toolbarEdit.setVisibility(View.GONE);
    }

    private void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {

            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void showDialogWindow(final Contacts contacts) {
        String[] options = null;
        String currentUserJID = TinyDB.getInstance(getContext()).getString(TinyDB.KEY_USER_JID);

        if (contacts.jid.equals(currentUserJID)) {
            options = new String[]{"View Contact"};
        } else {
            if (isAdmin) {
                options = new String[]{"View Contact", "Send Message", "Remove From Group"};
            } else {
                options = new String[]{"View Contact", "Send Message"};
            }
        }

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setItems(options, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    viewUserProfile(contacts);
                } else if (which == 1) {
                    openChat(contacts);
                } else if (which == 2) {
                    RemoveMemberTask removeMemberTask = new RemoveMemberTask(contacts.jid, contacts.id);
                    removeMemberTask.execute();
                }
            }

        });

        builder.create().show();
    }

    private void showAddMemberFragment() {
        GroupDetailActivity groupDetailActivity = ((GroupDetailActivity) getActivity());
        groupDetailActivity.moveToMembersListFragment(groupParticipants);
    }

    private void viewUserProfile(Contacts contacts) {
        ChatScreenActivity.viewProfile(getActivity(), false, contacts.id, contacts.image, contacts.getName(), contacts.jid, contacts.status, false, contacts.availableStatus, blockStatus);
    }

    private void openChat(Contacts contacts) {
        String jid = contacts.jid;
        String name = contacts.getName();
        int contactId = contacts.id;
        byte[] userPicture = contacts.image;
        boolean blockStatus = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).isChatBlocked(contactId);

        Intent chatScreenIntent = ChatScreenActivity.createChatScreenIntent(getContext(), false, jid, name, contactId, userPicture, blockStatus, contacts.isOthers(), contacts.availableStatus, contacts.status);
        startActivity(chatScreenIntent);
    }

    View.OnClickListener onExitAndDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String positiveButtonTitle = "";
            String negativeButtonTitle = "CANCEL";
            String dialogTitle = "";
            if (blockStatus) {
                positiveButtonTitle = "DELETE";
                dialogTitle = "Are you Sure you want to delete this group?";
            } else {
                positiveButtonTitle = "EXIT AND DELETE";
                dialogTitle = "Are you Sure you want to exit and delete this group?";
            }
            DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (blockStatus) {
                        deleteGroup();
                    } else {
                        exitAndDeleteGroup();
                    }
                }
            };
            new AlertDialogUtil(AlertDialogUtil.ACTION_EXIT_AND_DELETE_GROUP, dialogTitle, positiveButtonTitle, negativeButtonTitle, getActivity(), clickListener).show();
        }
    };

    private void exitAndDeleteGroup() {
        boolean success = exitGroup();
        if (success) {
            deleteGroup();
        }
    }

    private void deleteGroup() {
        SportsUnityDBHelper.getInstance(getContext()).deleteGroup(chatID);
        NotificationHandler.getInstance(getActivity().getApplicationContext()).clearNotificationMessages(String.valueOf(chatID));
        Intent I = new Intent(getActivity(), MainActivity.class);
        I.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(I);
    }

    private boolean exitGroup() {
        String currentUserJID = TinyDB.getInstance(getContext()).getString(TinyDB.KEY_USER_JID);
        return PubSubMessaging.getInstance().exitGroup(currentUserJID + "@mm.io", groupJID);
    }

    class EventListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GroupParticipantsAdapter adapter = (GroupParticipantsAdapter) parent.getAdapter();
            ArrayList<Contacts> adapterList = adapter.getAllMembers();

            Contacts contacts = adapterList.get(position);
            if (contacts != null) {
                if (contacts.id == -1) {
                    showAddMemberFragment();
                } else {
                    showDialogWindow(contacts);
                }
            } else {
                //nothing
            }
        }

    }

    class RemoveMemberTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;
        private String jid;
        private int contactId;

        public RemoveMemberTask(String jid, int contactId) {
            this.jid = jid;
            this.contactId = contactId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = new ProgressBar(GroupInfoFragment.this.getActivity());
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
            progressDialog = new ProgressDialog(GroupInfoFragment.this.getActivity());
            progressDialog.setMessage("removing member...");
            progressDialog.setIndeterminateDrawable(progressBar.getIndeterminateDrawable());
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return removeUserFromGroup();
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progressDialog.dismiss();
            if (success == true) {
                SportsUnityDBHelper.getInstance(GroupInfoFragment.this.getActivity()).deleteGroupMember(chatID, contactId);
            } else {
                Toast.makeText(GroupInfoFragment.this.getActivity(), R.string.oops_try_again, Toast.LENGTH_SHORT).show();
            }
        }

        private boolean removeUserFromGroup() {
            boolean success = PubSubMessaging.getInstance().removeFromGroup(jid + "@mm.io", groupJID);
            if (success) {
                PubSubMessaging.getInstance().sendIntimationAboutMemberRemoved(getContext(), jid, groupJID);
            }
            return success;
        }

    }

}
