package com.sports.unity.messages.controller.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.ToolbarActionsForChatScreen;
import com.sports.unity.messages.controller.viewhelper.OnSearchViewQueryListener;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

/**
 * Created by madmachines on 24/8/15.
 */
public class ContactsFragment extends Fragment implements OnSearchViewQueryListener {

    public static int USAGE_FOR_CONTACTS = 0;
    public static int USAGE_FOR_MEMBERS = 1;
    public static int USAGE_FOR_FORWARD = 2;

    private int usageIn = 0;

    private ArrayList<Contacts> selectedMembersList = new ArrayList<>();

    private ListView contacts;
    private ViewGroup titleLayout = null;

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

        View v = inflater.inflate(com.sports.unity.R.layout.fragment_contacts, container, false);
        contacts = (ListView) v.findViewById(R.id.list_contacts);
        contacts.setTextFilterEnabled(true);

        boolean multipleSelection = false;
        int resource = 0;
        AdapterView.OnItemClickListener itemListener = null;
        ArrayList<Contacts> contactList = null;
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

        return v;
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

}

