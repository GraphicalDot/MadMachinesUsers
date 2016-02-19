package com.sports.unity.messages.controller.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.ToolbarActionsForChatScreen;
import com.sports.unity.messages.controller.viewhelper.OnSearchViewQueryListener;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by madmachines on 24/8/15.
 */
public class ContactsFragment extends Fragment implements OnSearchViewQueryListener, MainActivity.PermissionResultHandler {

    public static int USAGE_FOR_CONTACTS = 0;
    public static int USAGE_FOR_MEMBERS = 1;
    public static int USAGE_FOR_FORWARD = 2;

    private int frequentContactCount = 0;

    private int usageIn = 0;

    private ArrayList<Contacts> selectedMembersList = new ArrayList<>();

    private StickyListHeadersListView contacts;
    private ViewGroup titleLayout = null;
    private View v;

    private boolean copyContactCallInitiated = false;
    private boolean listeningCopyFinishPostCall = false;

    private AdapterView.OnItemClickListener contactItemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ContactListAdapter contactListAdapter = (ContactListAdapter) contacts.getAdapter();
            Contacts c = contactListAdapter.getItem(position);

            if (c.registered) {
                String number = c.jid;
                String name = c.name;
                long contactId = c.id;
                byte[] userPicture = c.image;

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
                CommonUtil.openSMSIntent(c, getContext());
//                Toast.makeText(getActivity().getApplicationContext(), "Invite to sports Unity!", Toast.LENGTH_SHORT).show();
            }
        }

    };


    private AdapterView.OnItemClickListener memberItemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//            EditText searchContacts = (EditText) view.findViewById(R.id.search_contacts_edittext);
//            searchContacts.getBackground().setColorFilter(getResources().getColor(R.color.app_theme_blue), PorterDuff.Mode.SRC_IN);

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
        contacts = (StickyListHeadersListView) v.findViewById(R.id.list_contacts);
//        contacts.setTextFilterEnabled(true);
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
        final SearchView searchView = (SearchView) v.findViewById(R.id.searchView);
        if (usageIn == USAGE_FOR_MEMBERS) {
            resource = R.layout.list_item_members;
            itemListener = memberItemListener;


            int searchSrcTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            EditText editText = (EditText) searchView.findViewById(searchSrcTextId);
            editText.setTextColor(Color.BLACK);
            editText.setHint("Type contact name...");
//            editText.getBackground().setColorFilter(getResources().getColor(R.color.app_theme_blue), PorterDuff.Mode.SRC_IN);

            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList(true);
//            searchView.getBackground().setColorFilter(getResources().getColor(R.color.app_theme_blue), PorterDuff.Mode.SRC_IN);
            searchView.onActionViewExpanded();
//            searchContacts.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    filterResults(searchContacts.getText().toString());
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });

            titleLayout = (ViewGroup) v.findViewById(R.id.title_layout_for_members_list);
            titleLayout.setVisibility(View.VISIBLE);

            multipleSelection = true;
        } else if (usageIn == USAGE_FOR_CONTACTS) {
            resource = R.layout.list_contact_msgs;
            itemListener = contactItemListener;

            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList_AvailableOnly(true);
            ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList(false);
            {
                /**
                 * Frequent Contacts + default contact list
                 */
                ArrayList<Contacts> frequentContacts = new ArrayList<>();
                ArrayList<Contacts> allContacts = new ArrayList<>();

                allContacts.addAll(contactList);

                frequentContactCount = 0;

                for (int i = 0; i < chatList.size(); i++) {
                    if (chatList.get(i).groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
                        if (frequentContactCount == 10) {
                            break;
                        } else {
                            frequentContacts.add(SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(chatList.get(i).contactId));
                            ++frequentContactCount;
                        }
                    } else {
                        // nothing
                    }
                }

                contactList.clear();
                contactList.addAll(frequentContacts);
                contactList.addAll(allContacts);
            }
            searchView.setVisibility(View.GONE);
        } else if (usageIn == USAGE_FOR_FORWARD) {
            resource = R.layout.list_item_members;
            itemListener = forwardToContactMemberListener;

            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList(true);

            multipleSelection = false;
            searchView.setVisibility(View.GONE);
        }

        ContactListAdapter adapter = new ContactListAdapter(getActivity(), resource, contactList, multipleSelection, frequentContactCount);
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
        ((ContactListAdapter) contacts.getAdapter()).getFilter().filter(filter);
        Log.d("max", "Filtering");
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
            // nothing
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

