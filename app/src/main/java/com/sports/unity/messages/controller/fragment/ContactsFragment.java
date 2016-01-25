package com.sports.unity.messages.controller.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.ToolbarActionsForChatScreen;
import com.sports.unity.messages.controller.viewhelper.OnSearchViewQueryListener;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;

/**
 * Created by madmachines on 24/8/15.
 */
public class ContactsFragment extends Fragment implements OnSearchViewQueryListener, MainActivity.PermissionResultHandler {

    public static int USAGE_FOR_CONTACTS = 0;
    public static int USAGE_FOR_MEMBERS = 1;
    public static int USAGE_FOR_FORWARD = 2;

    private int usageIn = 0;

    private ArrayList<Contacts> selectedMembersList = new ArrayList<>();

    private ListView contacts;
    private ViewGroup titleLayout = null;
    private View v;

    private boolean copyContactCallInitiated = false;
    private boolean listeningCopyFinishPostCall = false;

    private AdapterView.OnItemClickListener contactItemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ContactListAdapter contactListAdapter = (ContactListAdapter) contacts.getAdapter();
            ArrayList<Contacts> contactList = contactListAdapter.getInUseContactListForAdapter();

            if (contactList.get(position).registered) {
                String number = contactList.get(position).jid;
                String name = contactList.get(position).name;
                long contactId = contactList.get(position).id;
                byte[] userPicture = contactList.get(position).image;

                String groupServerId = SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID;
                long chatId = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatEntryID(contactId, groupServerId);
                boolean blockStatus = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).isChatBlocked(contactId);

                Intent chatScreenIntent = new Intent(getActivity(), ChatScreenActivity.class);
                chatScreenIntent.putExtra("number", number);
                chatScreenIntent.putExtra("name", name);
                chatScreenIntent.putExtra("contactId", contactId);
                chatScreenIntent.putExtra("chatId", chatId);
                chatScreenIntent.putExtra("groupServerId", groupServerId);
                chatScreenIntent.putExtra("userpicture", userPicture);
                chatScreenIntent.putExtra("blockStatus", blockStatus);
                startActivity(chatScreenIntent);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Invite to sports Unity!", Toast.LENGTH_SHORT).show();
            }
        }

    };


    private AdapterView.OnItemClickListener memberItemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            Boolean flag = (Boolean) checkBox.getTag();

            ContactListAdapter contactListAdapter = (ContactListAdapter) contacts.getAdapter();
            Contacts contacts = contactListAdapter.getInUseContactListForAdapter().get(position);

            if (flag == null || flag == false) {
                checkBox.setTag(true);
                checkBox.setChecked(true);

                selectedMembersList.add(contacts);
            } else {
                checkBox.setTag(false);
                checkBox.setChecked(false);

                selectedMembersList.remove(contacts);
            }

            TextView textView = (TextView) titleLayout.findViewById(R.id.members_count);
            textView.setText(selectedMembersList.size() + "/100");
        }

    };

    private AdapterView.OnItemClickListener forwardToContactMemberListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ContactListAdapter contactListAdapter = (ContactListAdapter) contacts.getAdapter();
            final Contacts contact = contactListAdapter.getInUseContactListForAdapter().get(position);

            AlertDialog.Builder build = new AlertDialog.Builder(
                    getActivity());
            build.setMessage(
                    "Forward to " + contact.name + " ?");
            build.setPositiveButton("ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            forward(contact);
                        }
                    });
            build.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Do nothing
                        }
                    });
            AlertDialog dialog = build.create();
            dialog.show();

        }

    };

    private void forward(Contacts contact) {
        if (contact.registered) {
            ToolbarActionsForChatScreen.getInstance(getActivity().getApplicationContext()).resetVariables();
            String number = contact.jid;
            String name = contact.name;
            long contactId = contact.id;
            byte[] userPicture = contact.image;

            String groupServerId = SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID;
            long chatId = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatEntryID(contactId, groupServerId);
            boolean blockStatus = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).isChatBlocked(contactId);

            Intent chatScreenIntent = new Intent(getActivity(), ChatScreenActivity.class);
            chatScreenIntent.putExtra("number", number);
            chatScreenIntent.putExtra("name", name);
            chatScreenIntent.putExtra("contactId", contactId);
            chatScreenIntent.putExtra("chatId", chatId);
            chatScreenIntent.putExtra("groupServerId", groupServerId);
            chatScreenIntent.putExtra("userpicture", userPicture);
            chatScreenIntent.putExtra("blockStatus", blockStatus);

            if (blockStatus) {
                Toast.makeText(getActivity().getApplicationContext(), "This user is blocked Please select another user", Toast.LENGTH_SHORT).show();
            } else {

                ArrayList<Integer> selectedIds = getArguments().getIntegerArrayList(Constants.INTENT_FORWARD_SELECTED_IDS);
                chatScreenIntent.putIntegerArrayListExtra(Constants.INTENT_FORWARD_SELECTED_IDS, selectedIds);

                chatScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(chatScreenIntent);
                getActivity().finish();
            }
        }
    }

    public ContactsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        usageIn = getArguments().getInt(Constants.INTENT_KEY_CONTACT_FRAGMENT_USAGE);

        v = inflater.inflate(com.sports.unity.R.layout.fragment_contacts, container, false);
        contacts = (ListView) v.findViewById(R.id.list_contacts);
        contacts.setTextFilterEnabled(true);
        if (PermissionUtil.getInstance().isRuntimePermissionRequired()) {

            if (PermissionUtil.getInstance().requestPermission(getActivity(), new ArrayList<String>(Arrays.asList(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)), getResources().getString(R.string.read_contact_permission_message), Constants.REQUEST_CODE_CONTACT_PERMISSION)) {
                handleContacts();
            }
        }

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup searchLayout = (ViewGroup) view.findViewById(R.id.search_layout);
        searchLayout.setVisibility(View.GONE);
    }

    private void handleContacts() {
        addListenerToHandleContactCopyPostCall();
        ContactsHandler.getInstance().copyAllContacts_OnThread(getActivity().getApplicationContext(), new Runnable() {
            @Override
            public void run() {
                if (listeningCopyFinishPostCall) {
                    removeListenerToHandleContactCopyPostCall();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            copyContactCallInitiated = false;
                            setContactList(v);
                        }
                    });
                }
            }
        });
    }

    private void setContactList(View v) {
        boolean multipleSelection = false;
        int resource = 0;
        ArrayList<Contacts> contactList = null;
        AdapterView.OnItemClickListener itemListener = null;
        if (usageIn == USAGE_FOR_MEMBERS) {
            resource = R.layout.list_item_members;
            itemListener = memberItemListener;

            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList(true);

            titleLayout = (ViewGroup) v.findViewById(R.id.title_layout_for_members_list);
            titleLayout.setVisibility(View.VISIBLE);

            ViewGroup searchLayout = (ViewGroup) v.findViewById(R.id.search_layout);
            searchLayout.setVisibility(View.VISIBLE);

            multipleSelection = true;
        } else if (usageIn == USAGE_FOR_CONTACTS) {
            resource = R.layout.list_contact_msgs;
            itemListener = contactItemListener;

            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList_AvailableOnly(false);

            {
                /*
                 * Sorting and grouping by registered and unregistered contacts
                 */
                ArrayList<Contacts> contactList_registered = new ArrayList<>();
                ArrayList<Contacts> contactList_unregistered = new ArrayList<>();

                for (int i = 0; i < contactList.size(); i++) {
                    if (contactList.get(i).registered) {
                        contactList_registered.add(contactList.get(i));
                    } else {
                        contactList_unregistered.add(contactList.get(i));
                    }
                }

                contactList.clear();
                contactList.addAll(contactList_registered);
                contactList.addAll(contactList_unregistered);
            }

            ViewGroup searchLayout = (ViewGroup) v.findViewById(R.id.search_layout);
            searchLayout.setVisibility(View.GONE);
        } else if (usageIn == USAGE_FOR_FORWARD) {
            resource = R.layout.list_item_members;
            itemListener = forwardToContactMemberListener;

            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList(true);

            ViewGroup searchLayout = (ViewGroup) v.findViewById(R.id.search_layout);
            searchLayout.setVisibility(View.GONE);

            multipleSelection = false;
        }

        ContactListAdapter adapter = new ContactListAdapter(getActivity(), resource, contactList, multipleSelection);
        contacts.setAdapter(adapter);
        contacts.setOnItemClickListener(itemListener);
    }

    private void addListenerToHandleContactCopyPostCall() {
        listeningCopyFinishPostCall = true;
        copyContactCallInitiated = true;
    }

    private void removeListenerToHandleContactCopyPostCall() {
        listeningCopyFinishPostCall = false;
    }

    public void filterResults(String filter) {
        ContactListAdapter adapter = (ContactListAdapter) contacts.getAdapter();
        adapter.getFilter().filter(filter);
    }

    public ArrayList<Contacts> getSelectedMembersList() {
        return selectedMembersList;
    }

    @Override
    public void onSearchQuery(String filterText) {
        filterResults(filterText);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PermissionUtil.getInstance().isRuntimePermissionRequired()) {
            ((MainActivity) getActivity()).addContactResultListener(this);
            if (copyContactCallInitiated) {
                if (ContactsHandler.getInstance().isContactCopyInProgress()) {
                    addListenerToHandleContactCopyPostCall();
                } else {
                    setContactList(getView());
                }
            } else {
                //nothing
            }
        } else {
            setContactList(getView());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        removeListenerToHandleContactCopyPostCall();
    }

    @Override
    public void onPermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == Constants.REQUEST_CODE_CONTACT_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                handleContacts();
            } else {
                PermissionUtil.getInstance().showSnackBar(getActivity(), getString(R.string.permission_denied));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            ((MainActivity) getActivity()).removeContactResultListener();
        } catch (Exception e) {
            //nothing
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

