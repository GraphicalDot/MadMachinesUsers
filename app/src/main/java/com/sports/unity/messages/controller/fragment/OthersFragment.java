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

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.viewhelper.OnSearchViewQueryListener;

import java.util.ArrayList;

/**
 * Created by madmachines on 23/9/15.
 */
public class OthersFragment extends Fragment implements OnSearchViewQueryListener {

    private ListView otherChatListView;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_others, container, false);
        initContent(view);
        return view;
    }

    private void initContent(View view) {
        otherChatListView = (ListView) view.findViewById(R.id.people_around_me_chat);

        ChatListAdapter chatListAdapter = new ChatListAdapter(getActivity(), 0, new ArrayList<Chats>());
        otherChatListView.setAdapter(chatListAdapter);
        otherChatListView.setEmptyView(view.findViewById(R.id.empty_others));

        otherChatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moveToNextActivity(position, ChatScreenActivity.class);
            }

        });
    }

    private void moveToNextActivity(int position, Class<?> cls) {
        ArrayList<Chats> chatList = ((ChatListAdapter) otherChatListView.getAdapter()).getChatArrayList();
        Chats chatObject = chatList.get(position);
        moveToNextActivity(chatObject, cls);
    }

    private void moveToNextActivity(Chats chatObject, Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);

        String groupSeverId = chatObject.groupServerId;
        long contactId = chatObject.contactId;
        Contacts contact = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(contactId);

        String number = contact.jid;
        String name = chatObject.name;
        Boolean blockStatus = chatObject.block;
        long chatId = chatObject.chatid;
        byte[] userpicture = chatObject.userImage;

        intent.putExtra("number", number);
        intent.putExtra("name", name);
        intent.putExtra("contactId", contactId);
        intent.putExtra("chatId", chatId);
        intent.putExtra("groupServerId", groupSeverId);
        intent.putExtra("userpicture", userpicture);
        intent.putExtra("blockStatus", blockStatus);

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatecontent();
    }

    private void updatecontent() {
        ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList(true);
        if (chatList != null) {
            ChatListAdapter adapter = (ChatListAdapter) otherChatListView.getAdapter();
            adapter.updateList(chatList);
            otherChatListView.setAdapter(adapter);
        }
    }

    @Override
    public void onSearchQuery(String filterText) {

    }
}
