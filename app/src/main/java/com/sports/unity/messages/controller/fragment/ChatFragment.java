package com.sports.unity.messages.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.Switch;
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

    //  MessageRecieved messageRecieved;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //  messageRecieved = new MessageRecieved();
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
                SportsUnityDBHelper.Chats chatObject = chatList.get(position);

                Intent chatScreen = new Intent(getActivity(), ChatScreenActivity.class);

                String groupSeverId = chatObject.groupServerId;
                if (groupSeverId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {

                    long contactId = chatObject.contactId;
                    SportsUnityDBHelper.Contacts contact = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(contactId);

                    String number = contact.jid;
                    String name = chatObject.name;
                    long chatId = chatObject.chatid;
                    byte[] userpicture = chatList.get(position).userImage;

                    chatScreen.putExtra("number", number);
                    chatScreen.putExtra("name", name);
                    chatScreen.putExtra("contactId", contactId);
                    chatScreen.putExtra("chatId", chatId);
                    chatScreen.putExtra("groupServerId", groupSeverId);
                    chatScreen.putExtra("userpicture", userpicture);
                } else {
                    long contactId = chatObject.contactId;

                    String number = groupSeverId;
                    String name = chatObject.name;
                    long chatId = chatObject.chatid;

                    chatScreen.putExtra("number", number);
                    chatScreen.putExtra("name", name);
                    chatScreen.putExtra("contactId", contactId);
                    chatScreen.putExtra("chatId", chatId);
                    chatScreen.putExtra("groupServerId", groupSeverId);
                }

                startActivity(chatScreen);
            }
        });

        chatListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int tag = (Integer) view.getTag();
                ArrayList<SportsUnityDBHelper.Chats> chatList = ((ChatListAdapter) chatListView.getAdapter()).getChatArrayList();
                SportsUnityDBHelper.Chats chatObject = chatList.get(position);
                showDialogWindow(position, tag, chatObject);
                return true;
            }
        });

    }

    private void showDialogWindow(final int position, final int tag, final SportsUnityDBHelper.Chats chatObject) {
        ArrayList<String> menuOptions = new ArrayList<>();
        if (tag == 0) {
            menuOptions.add("View Contact");
            menuOptions.add("Delete Chat");
            menuOptions.add("Mute Conversation");
        } else {
            menuOptions.add("View Group");
            menuOptions.add("Exit Group");
            menuOptions.add("Mute Conversation");
        }
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View popupListView = inflater.inflate(R.layout.singlechatpopupdialog, null);
        ListView popupList = (ListView) popupListView.findViewById(R.id.popup_list_menu);
        chatFragmentDialogListAdapter = new ChatFragmentDialogListAdapter(menuOptions, getActivity(), chatObject);
        popupList.setAdapter(chatFragmentDialogListAdapter);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(popupListView);
        final AlertDialog alert = builder.show();
        popupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        alert.dismiss();
                        break;
                    case 1:
                        deleteChat(position, tag, chatObject);
                        alert.dismiss();
                        break;
                    case 2:
                        Switch switchView = (Switch) view.findViewById(R.id.mute_switcher);
                        if (chatObject.mute) {
                            chatObject.mute = false;
                        } else {
                            chatObject.mute = true;
                        }
                        SportsUnityDBHelper.getInstance(getActivity()).muteConversation(chatObject.chatid, chatObject.mute);
                        switchView.setChecked(chatObject.mute);
                        break;
                }
            }
        });

    }

    private void deleteChat(int position, int tag, SportsUnityDBHelper.Chats chatObject) {
        Log.i("deletechat", "true");
        if (tag == 0) {
            SportsUnityDBHelper.getInstance(getActivity()).clearChat(chatObject.chatid, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
            SportsUnityDBHelper.getInstance(getActivity()).clearChatEntry(chatObject.chatid);
            updateContent();
        } else {

        }
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
                        ArrayList<SportsUnityDBHelper.Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList();
                        ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
                        adapter.updateList(chatList);
                        chatListView.setAdapter(adapter);
                    }
                }
            });
        }
    };


    private void updateContent() {
        ArrayList<SportsUnityDBHelper.Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList();
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

//    public static class MessageRecieved extends BroadcastReceiver {
//
//        public MessageRecieved() {
//        }
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            /*if (chatListView != null) {
//                ArrayList<SportsUnityDBHelper.Chats> chatList = SportsUnityDBHelper.getInstance(context).getChatScreenList();
//                ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
//                adapter.updateList(chatList);
//                chatListView.setAdapter(adapter);
//                Toast.makeText(context, "refreshing list", Toast.LENGTH_SHORT).show();
//
//            }*/
//        }
//    }

}
