package com.sports.unity.messages.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
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

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;

import java.util.ArrayList;

/**
 * Created by madmachines on 24/8/15.
 */
public class ChatFragment extends Fragment {

    static ListView chatListView;

    MessageRecieved messageRecieved;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        messageRecieved = new MessageRecieved();

        View view = inflater.inflate(com.sports.unity.R.layout.fragment_chats, container, false);
        initContent(view);

        return view;
    }

    private void initContent(View view){
        chatListView = (ListView) view.findViewById(R.id.chats);

        ChatListAdapter chatListAdapter = new ChatListAdapter(getActivity(), 0, new ArrayList<SportsUnityDBHelper.Chats>());
        chatListView.setAdapter(chatListAdapter);
        chatListView.setEmptyView(view.findViewById(R.id.empty));

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<SportsUnityDBHelper.Chats> chatList = ((ChatListAdapter) chatListView.getAdapter()).getChatArrayList();
                String jid = chatList.get(position).jid;
                String jbname = chatList.get(position).name;
                Intent chatScreen = new Intent(getActivity(), ChatScreenActivity.class);
                chatScreen.putExtra("jid", jid);
                chatScreen.putExtra("jbname", jbname);
                chatScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(chatScreen);
            }
        });
    }

    private void updateContent(){
        ArrayList<SportsUnityDBHelper.Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatScreenList();
        ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
        adapter.updateList(chatList);
        chatListView.setAdapter( adapter);
    }

    private void clearStaticContent(){
        chatListView = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        updateContent();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearStaticContent();
    }

    public static class MessageRecieved extends BroadcastReceiver {

        public MessageRecieved() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if ( chatListView != null ) {
                ArrayList<SportsUnityDBHelper.Chats> chatList = SportsUnityDBHelper.getInstance(context).getChatScreenList();
                ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
                adapter.updateList(chatList);
                chatListView.setAdapter(adapter);
                Toast.makeText(context, "refreshing list", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
