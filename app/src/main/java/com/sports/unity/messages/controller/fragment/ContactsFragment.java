package com.sports.unity.messages.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.sports.unity.R;
import com.sports.unity.common.controller.RetainDataFragment;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.Database.SportsUnityDBHelper;

import java.util.ArrayList;

/**
 * Created by madmachines on 24/8/15.
 */
public class ContactsFragment extends Fragment {

    ListView contacts;
    String[] names;
    //ArrayList<SportsUnityDBHelper.Contacts> contactList

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(com.sports.unity.R.layout.fragment_contacts, container, false);
        contacts = (ListView) v.findViewById(R.id.list_contacts);

        //final ArrayList<SportsUnityDBHelper.Contacts> contactList = new SportsUnityDBHelper(getActivity().getApplication()).getContactList();
        RetainDataFragment retainDataFragment = (RetainDataFragment) getActivity().getSupportFragmentManager().findFragmentByTag("data");
        final ArrayList<SportsUnityDBHelper.Contacts> contactList = retainDataFragment.getContactList();

        ContactListAdapter adapter = new ContactListAdapter(getActivity(), 0, contactList);

        contacts.setAdapter(adapter);

        contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });
        return v;
    }
}
