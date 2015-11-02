package com.sports.unity.messages.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.controller.RetainDataFragment;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.util.Constants;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by madmachines on 24/8/15.
 */
public class ContactsFragment extends Fragment {

    public static int USAGE_FOR_CONTACTS = 0;
    public static int USAGE_FOR_MEMBERS = 1;

    private int usageIn = 0;

    private ArrayList<SportsUnityDBHelper.Contacts> contactList = null;
    private ArrayList<SportsUnityDBHelper.Contacts> selectedMembersList = new ArrayList<>();

    private ListView contacts;
    private ViewGroup titleLayout = null;

    private AdapterView.OnItemClickListener contactItemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (contactList.get(position).registered) {
                String number = contactList.get(position).jid;
                String name = contactList.get(position).name;
                long contactId = contactList.get(position).id;
                long chatId = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
                byte[] userPicture = contactList.get(position).image;

                Intent chatScreen = new Intent(getActivity(), ChatScreenActivity.class);
                chatScreen.putExtra("number", number);
                chatScreen.putExtra("name", name);
                chatScreen.putExtra("contactId", contactId);
                chatScreen.putExtra("chatId", chatId);
                chatScreen.putExtra("userpicture", userPicture);
                startActivity(chatScreen);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Invite him to sports Unity!", Toast.LENGTH_SHORT).show();
            }
        }

    };

    private AdapterView.OnItemClickListener memberItemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            Boolean flag = (Boolean) checkBox.getTag();

            SportsUnityDBHelper.Contacts contacts = contactList.get(position);

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

    public ContactsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        usageIn = getArguments().getInt(Constants.INTENT_KEY_CONTACT_FRAGMENT_USAGE);

        View v = inflater.inflate(com.sports.unity.R.layout.fragment_contacts, container, false);
        contacts = (ListView) v.findViewById(R.id.list_contacts);

        int resource = 0;
        AdapterView.OnItemClickListener itemListener = null;
        if( usageIn == USAGE_FOR_MEMBERS ){
            resource = R.layout.list_item_members;
            itemListener = memberItemListener;

            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList(true);

            titleLayout = (ViewGroup)v.findViewById(R.id.title_layout_for_members_list);
            titleLayout.setVisibility(View.VISIBLE);
        } else if( usageIn == USAGE_FOR_CONTACTS ){
            resource = R.layout.list_contact_msgs;
            itemListener = contactItemListener;

            contactList = SportsUnityDBHelper.getInstance(getActivity()).getContactList();
        }

        ContactListAdapter adapter = new ContactListAdapter(getActivity(), resource, contactList);
        contacts.setAdapter(adapter);
        contacts.setOnItemClickListener(itemListener);

        return v;
    }

    public ArrayList<SportsUnityDBHelper.Contacts> getSelectedMembersList () {
        return selectedMembersList;
    }

}

