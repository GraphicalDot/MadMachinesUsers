package com.sports.unity.messages.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.activity.ChatScreenAdapter;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;

import java.util.ArrayList;

/**
 * Created by madmachines on 24/8/15.
 */
public class ChatFragment extends Fragment {

    static ListView chatListView;

    private View popupView;
    private PopupWindow popupWindow;

    private ListView popupScreenOptions;
    private View view;

    private ChatFragmentDialogListAdapter chatFragmentDialogListAdapter;

    MessageRecieved messageRecieved;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        messageRecieved = new MessageRecieved();
        view = inflater.inflate(com.sports.unity.R.layout.fragment_chats, container, false);
        initContent(view);

        return view;
    }

    private void initContent(View view) {
        chatListView = (ListView) view.findViewById(R.id.chats);

        ChatListAdapter chatListAdapter = new ChatListAdapter(getActivity(), 0, new ArrayList<SportsUnityDBHelper.Chats>());
        chatListView.setAdapter(chatListAdapter);
        chatListView.setEmptyView(view.findViewById(R.id.empty));

        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<SportsUnityDBHelper.Chats> chatList = ((ChatListAdapter) chatListView.getAdapter()).getChatArrayList();

                long contactId = chatList.get(position).contactId;
                SportsUnityDBHelper.Contacts contacts = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(contactId);
                String number = contacts.jid;
                String name = chatList.get(position).userName;
                long chatId = chatList.get(position).chatid;
                byte[] userpicture = chatList.get(position).userImage;

                Intent chatScreen = new Intent(getActivity(), ChatScreenActivity.class);
                chatScreen.putExtra("number", number);
                chatScreen.putExtra("name", name);
                chatScreen.putExtra("contactId", contactId);
                chatScreen.putExtra("chatId", chatId);
                chatScreen.putExtra("userpicture", userpicture);

                startActivity(chatScreen);
            }
        });

        chatListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogWindow(position);
                return true;
            }
        });

    }

    private void showDialogWindow(final int position) {
        ArrayList<String> menuOptions = new ArrayList<>();
        menuOptions.add("View Contact");
        menuOptions.add("Delete Chat");
        menuOptions.add("Block Contact");
        menuOptions.add("Mute Conversation");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View popupListView = inflater.inflate(R.layout.singlechatpopupdialog, null);
        ListView popupList = (ListView) popupListView.findViewById(R.id.popup_list_menu);
        chatFragmentDialogListAdapter = new ChatFragmentDialogListAdapter(menuOptions, getActivity());
        popupList.setAdapter(chatFragmentDialogListAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(popupListView);
        builder.create();
        builder.show();
    }

    private void deleteChat(int position) {
        /*switch (which) {
            case 1:
                break;
            case 2:
                deleteChat(position);
                break;
            case 3:
                break;
        }*/
    }

    private ActivityActionListener activityActionListener = new ActivityActionListener() {
        @Override
        public void handleAction(int id, Object object) {

        }

        @Override
        public void handleAction(int id) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (chatListView != null) {
                        ArrayList<SportsUnityDBHelper.Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatScreenList();
                        ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
                        adapter.updateList(chatList);
                        chatListView.setAdapter(adapter);
                    }
                }
            });
        }
    };


    private void updateContent() {
        ArrayList<SportsUnityDBHelper.Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatScreenList();
        if (chatList != null) {
            ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
            adapter.updateList(chatList);
            chatListView.setAdapter(adapter);
        }
    }

    private void clearStaticContent() {
        chatListView = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateContent();
        ActivityActionHandler.getInstance().addActionListener(ActivityActionHandler.CHAT_LIST_KEY, activityActionListener);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        ActivityActionHandler.getInstance().removeActionListener(ActivityActionHandler.CHAT_LIST_KEY);
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
            /*if (chatListView != null) {
                ArrayList<SportsUnityDBHelper.Chats> chatList = SportsUnityDBHelper.getInstance(context).getChatScreenList();
                ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
                adapter.updateList(chatList);
                chatListView.setAdapter(adapter);
                Toast.makeText(context, "refreshing list", Toast.LENGTH_SHORT).show();

            }*/
        }
    }

}
