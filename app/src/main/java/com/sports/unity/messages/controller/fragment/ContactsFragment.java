package com.sports.unity.messages.controller.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.activity.ForwardSelectedItems;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.ShareableData;
import com.sports.unity.messages.controller.model.ToolbarActionsForChatScreen;
import com.sports.unity.messages.controller.viewhelper.OnSearchViewQueryListener;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by madmachines on 24/8/15.
 */
public class ContactsFragment extends Fragment implements OnSearchViewQueryListener, MainActivity.PermissionResultHandler, MainActivity.ContactSyncListener {

    public static int USAGE_FOR_CONTACTS = 0;
    public static int USAGE_FOR_MEMBERS = 1;
    public static int USAGE_FOR_FORWARD = 2;

    private int registeredContactCount = 0;

    private int usageIn = 0;

    private ArrayList<Contacts> selectedMembersList = new ArrayList<>();

    private StickyListHeadersListView contacts;
    private ViewGroup titleLayout = null;
    private View v;

//    private boolean copyContactCallInitiated = false;
//    private boolean listeningCopyFinishPostCall = false;

    private AdapterView.OnItemClickListener contactItemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ContactListAdapter contactListAdapter = (ContactListAdapter) contacts.getAdapter();
            Contacts c = contactListAdapter.getUsedContact().get(position);

            if (c.isRegistered()) {
                String jid = c.jid;
                String name = c.name;
                long contactId = c.id;
                byte[] userPicture = c.image;

                String groupServerId = SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID;
                long chatId = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatEntryID(contactId, groupServerId);
                boolean blockStatus = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).isChatBlocked(contactId);

                Intent chatScreenIntent = ChatScreenActivity.createChatScreenIntent(getContext(), jid, name, contactId, chatId, groupServerId, userPicture, blockStatus, c.isOthers(), c.availableStatus, c.status);
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

            Boolean isClickableFlag = (Boolean)view.getTag();
            if( isClickableFlag == null || isClickableFlag == true ) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                boolean flag = checkBox.isChecked();

                ContactListAdapter contactListAdapter = (ContactListAdapter) contacts.getAdapter();
                Contacts contacts = contactListAdapter.getUsedContact().get(position);

                if (flag == false) {
                    checkBox.setChecked(true);

                    selectedMembersList.add(contacts);
                } else {
                    checkBox.setChecked(false);

                    selectedMembersList.remove(contacts);
                }

                int count = contactListAdapter.getPreviouslySelectedMembersList().size() + selectedMembersList.size();

                TextView textView = (TextView) titleLayout.findViewById(R.id.members_count);
                textView.setText( count + "/50");

                contactListAdapter.refreshSelectedMembers(selectedMembersList);
            } else {
                //nothing
            }
        }

    };

    private AdapterView.OnItemClickListener forwardToContactMemberListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ContactListAdapter contactListAdapter = (ContactListAdapter) contacts.getAdapter();
            final Contacts contact = contactListAdapter.getUsedContact().get(position);

            AlertDialog.Builder build = new AlertDialog.Builder(
                    getActivity());
            build.setMessage(
                    "Send to " + contact.name + " ?");
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
        if (contact.isRegistered()) {
            ToolbarActionsForChatScreen.getInstance(getActivity().getApplicationContext()).resetVariables();
            String jid = contact.jid;
            String name = contact.name;
            long contactId = contact.id;
            byte[] userPicture = contact.image;

            String groupServerId = SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID;
            long chatId = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatEntryID(contactId, groupServerId);
            boolean blockStatus = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).isChatBlocked(contactId);

            if (blockStatus) {
                Toast.makeText(getActivity().getApplicationContext(), "This user is blocked Please select another user", Toast.LENGTH_SHORT).show();
            } else {
                Intent chatScreenIntent = ChatScreenActivity.createChatScreenIntent(getContext(), jid, name, contactId, chatId, groupServerId, userPicture, blockStatus, contact.isOthers(), contact.availableStatus, contact.status);

                ArrayList<ShareableData> dataArrayList = getArguments().getParcelableArrayList(Constants.INTENT_FORWARD_SELECTED_IDS);
                chatScreenIntent.putParcelableArrayListExtra(Constants.INTENT_FORWARD_SELECTED_IDS, dataArrayList);
                chatScreenIntent.putExtra(ForwardSelectedItems.KEY_FILES_NOT_SENT, getArguments().getInt(ForwardSelectedItems.KEY_FILES_NOT_SENT));

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
//        addListenerToHandleContactCopyPostCall();
//        ContactsHandler.getInstance().copyAllContacts_OnThread(getActivity().getApplicationContext(), new Runnable() {
//            @Override
//            public void run() {
//                if (listeningCopyFinishPostCall) {
//                    removeListenerToHandleContactCopyPostCall();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                            copyContactCallInitiated = false;
                setContactList(v);
            }
        });
//                }
//            }
//        });
    }

    private void setContactList(View v) {
        boolean multipleSelection = false;
        int resource = 0;
        ArrayList<Contacts> contactList = null;
        AdapterView.OnItemClickListener itemListener = null;
        final SearchView searchView = (SearchView) v.findViewById(R.id.searchView);
        int searchSrcTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText editText = (EditText) searchView.findViewById(searchSrcTextId);
        editText.setTextColor(Color.BLACK);
        editText.setHint("Type contact name...");
        if (usageIn == USAGE_FOR_MEMBERS) {
            searchView.setVisibility(View.VISIBLE);
            resource = R.layout.list_item_members;
            itemListener = memberItemListener;


//            editText.getBackground().setColorFilter(getResources().getColor(R.color.app_theme_blue), PorterDuff.Mode.SRC_IN);

            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList(true);
//            searchView.getBackground().setColorFilter(getResources().getColor(R.color.app_theme_blue), PorterDuff.Mode.SRC_IN);
            searchView.onActionViewExpanded();

            searchView.clearFocus();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterResults(newText);
                    return true;
                }
            });

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

            registeredContactCount = contactList.size();
            multipleSelection = true;
        } else if (usageIn == USAGE_FOR_CONTACTS) {
            searchView.setVisibility(View.GONE);
            resource = R.layout.list_contact_msgs;
            itemListener = contactItemListener;

//            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList_AvailableOnly(true);
//            ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList(Contacts.AVAILABLE_BY_MY_CONTACTS);
            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList(true);
            ArrayList<Contacts> unregisteredContacts = SportsUnityDBHelper.getInstance(getActivity()).getContactList(false);
            {
                /**
                 * Frequent Contacts + default contact list
                 */
                ArrayList<Contacts> registeredContacts = new ArrayList<>();
                ArrayList<Contacts> allContacts = new ArrayList<>();

                allContacts.addAll(contactList);

                registeredContactCount = contactList.size();

//                for (int i = 0; i < chatList.size(); i++) {
//                    if (chatList.get(i).groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
//                        if (registeredContactCount == 10) {
//                            break;
//                        } else {
//                            frequentContacts.add(SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(chatList.get(i).contactId));
//                            ++registeredContactCount;
//                        }
//                    } else {
//                        // nothing
//                    }
//                }

//                contactList.clear();
//                contactList.addAll(contactList);
                contactList.addAll(unregisteredContacts);
            }
        } else if (usageIn == USAGE_FOR_FORWARD) {
            resource = R.layout.list_item_members;
            itemListener = forwardToContactMemberListener;

            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList(true);

            multipleSelection = false;
            searchView.setVisibility(View.VISIBLE);
            searchView.onActionViewExpanded();

            searchView.clearFocus();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterResults(newText);
                    return true;
                }
            });
            searchView.setVisibility(View.GONE);

            registeredContactCount = contactList.size();
        }

        ContactListAdapter adapter = new ContactListAdapter(getActivity(), resource, contactList, multipleSelection, registeredContactCount, selectedMembersList);
        if (usageIn == USAGE_FOR_MEMBERS) {
            ArrayList<String> previouslySelectedMembersList = getArguments().getStringArrayList(Constants.INTENT_KEY_ADDED_MEMBERS);
            adapter.setPreviouslySelectedMembersList(previouslySelectedMembersList);

            TextView textView = (TextView) titleLayout.findViewById(R.id.members_count);
            textView.setText(previouslySelectedMembersList.size()+ "/50");
        } else {
            //nothing
        }
        contacts.setAdapter(adapter);
        contacts.setEmptyView(v.findViewById(R.id.contact_empty));

        contacts.setOnItemClickListener(itemListener);
    }

//    private void addListenerToHandleContactCopyPostCall() {
//        listeningCopyFinishPostCall = true;
//        copyContactCallInitiated = true;
//    }
//
//    private void removeListenerToHandleContactCopyPostCall() {
//        listeningCopyFinishPostCall = false;
//    }

    public void filterResults(String filter) {
        ((ContactListAdapter) contacts.getAdapter()).getFilter().filter(filter);
    }

    public ArrayList<Contacts> getSelectedMembersList() {
        return selectedMembersList;
    }

    @Override
    public void onSearchQuery(String filterText) {
        if (filterText.length() > 0) {
            if (contacts != null && contacts.getAdapter() != null) {
                filterResults(filterText);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Contact permission is denied by you.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (contacts != null && contacts.getAdapter() != null) {
                ((ContactListAdapter) contacts.getAdapter()).refreshContacts();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).addContactSyncListener(this);
        }
        if (PermissionUtil.getInstance().isRuntimePermissionRequired()) {

            //TODO need to handle it cleanly.
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).addContactResultListener(this);
            } else {
                //TODO to handle permission on forward activity.
            }
//            if (copyContactCallInitiated) {
//                if (ContactsHandler.getInstance().isContactCopyInProgress()) {
//                    addListenerToHandleContactCopyPostCall();
//                } else {
//                    setContactList(getView());
//                }
//            } else {
//                //nothing
//            }
        } else {
            setContactList(getView());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Activity act = getActivity();
        if (act != null && act instanceof MainActivity) {
            ((MainActivity) act).removeContactSyncListener();
        }
//        removeListenerToHandleContactCopyPostCall();
    }

    @Override
    public void onPermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == Constants.REQUEST_CODE_CONTACT_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                List<Fragment> list = getActivity().getSupportFragmentManager().getFragments();
                for (Fragment f : list) {
                    if (f != null && f instanceof MessagesFragment) {
                        ((MessagesFragment) f).showSyncProgress();
                    }
                }

                ContactsHandler.getInstance().addCallToSyncContacts(getContext());
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

    @Override
    public void onSyncComplete() {
        if (PermissionUtil.getInstance().requestPermission(getActivity(), new ArrayList<String>(Arrays.asList(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)))) {
            handleContacts();
        }
    }
}

