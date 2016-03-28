package com.sports.unity.messages.controller.fragment;

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
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.activity.ForwardSelectedItems;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.PubSubMessaging;
import com.sports.unity.messages.controller.model.ShareableData;
import com.sports.unity.messages.controller.viewhelper.OnSearchViewQueryListener;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.AlertDialogUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.NotificationHandler;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by madmachines on 24/8/15.
 */
public class ChatFragment extends Fragment implements OnSearchViewQueryListener {

    private StickyListHeadersListView chatListView;
    private View view;

    public static int USAGE_FOR_CHAT = 0;
    public static int USAGE_FOR_SHARE_OR_FORWARD = 1;

    private int usageFor = 0;

    private ChatFragmentDialogListAdapter chatFragmentDialogListAdapter;
    private boolean isSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(com.sports.unity.R.layout.fragment_chats, container, false);
        if (getArguments() != null) {
            usageFor = getArguments().getInt(Constants.INTENT_KEY_FRIENDS_FRAGMENT_USAGE);
        }
        initContent(view);

        return view;
    }


    private AdapterView.OnItemLongClickListener openContactOptions = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ArrayList<Chats> chatList = ((ChatListAdapter) chatListView.getAdapter()).getChatArrayList();
            Chats chatObject = chatList.get(position);

            int tag = 0;
            if (chatObject.groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
                tag = 0;
            } else {
                tag = 1;
            }

            showDialogWindow(position, tag, chatObject);
            return true;
        }

    };

    private AdapterView.OnItemClickListener openchat = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            moveToNextActivity(position, null);
        }

    };

    private AdapterView.OnItemClickListener openChatAndShareContent = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

            ArrayList<Chats> chatList = ((ChatListAdapter) chatListView.getAdapter()).getChatArrayList();
            Chats chatObject = chatList.get(position);

            AlertDialog.Builder build = new AlertDialog.Builder(
                    getActivity());
            build.setMessage(
                    "Send to " + chatObject.name + " ?");
            build.setPositiveButton("ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ShareContent(position);
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

    private void ShareContent(int position) {
        if (getArguments() != null) {
            ArrayList<ShareableData> messageList = getArguments().getParcelableArrayList(Constants.INTENT_FORWARD_SELECTED_IDS);
            moveToNextActivity(position, messageList);
        }
    }


    private void initContent(View view) {

        AdapterView.OnItemClickListener itemClickListener = null;
        AdapterView.OnItemLongClickListener itemLongClickListener = null;
        if (usageFor == USAGE_FOR_CHAT) {
            itemClickListener = openchat;
            itemLongClickListener = openContactOptions;
        } else {
            itemClickListener = openChatAndShareContent;
            itemLongClickListener = null;
            TextView emptyText = (TextView) view.findViewById(R.id.empty);
            emptyText.setText("No recents chats from friends");
        }

        chatListView = (StickyListHeadersListView) view.findViewById(R.id.chats);

        ChatListAdapter chatListAdapter = new ChatListAdapter(getActivity(), 0, new ArrayList<Chats>());
        chatListView.setAdapter(chatListAdapter);
        chatListView.setEmptyView(view.findViewById(R.id.empty));

        chatListView.setOnItemClickListener(itemClickListener);

        chatListView.setOnItemLongClickListener(itemLongClickListener);
    }

    private void showDialogWindow(final int position, final int tag, final Chats chatObject) {
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
                        if (chatObject.groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
                            ChatScreenActivity.viewProfile(getActivity(), chatObject.chatid, chatObject.userImage, chatObject.name, chatObject.groupServerId,
                                    SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(chatObject.contactId).jid, false);
                        } else {
                            ChatScreenActivity.viewProfile(getActivity(), chatObject.chatid, chatObject.chatImage, chatObject.name, chatObject.groupServerId,
                                    SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(chatObject.contactId).jid, false);
                        }
                        alert.dismiss();
                        break;
                    case 1:
                        alert.dismiss();
                        showAlertDialogToDeleteChatOrExitGroup(chatObject);
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
                        updateContent();
                        break;
                }
            }
        });
    }

    private void showAlertDialogToDeleteChatOrExitGroup(final Chats chat) {

        String positiveButtonTitle = "";
        String negativeButtonTitle = "CANCEL";
        String dialogTitle = "";
        if (chat.groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
            positiveButtonTitle = "DELETE";
            dialogTitle = "Are you sure you want to delete chat with " + chat.name + " ?";
        } else {
            positiveButtonTitle = "EXIT";
            dialogTitle = "Are you sure you want to exit group " + chat.name + " ?";
        }

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (chat.groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
                    deleteSingleChat(chat);
                } else {
                    String currentUserJID = TinyDB.getInstance(getContext()).getString(TinyDB.KEY_USER_JID);
                    PubSubMessaging.getInstance().exitGroup( currentUserJID+"@mm.io", chat.groupServerId);
                }
            }
        };

        new AlertDialogUtil(AlertDialogUtil.ACTION_DELETE_CHAT, dialogTitle, positiveButtonTitle, negativeButtonTitle, getActivity(), clickListener).show();
    }

    public void filterResults(String filter) {
        ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
        ArrayList<Chats> chatList = adapter.getChatArrayList();
        if (chatList != null) {
            adapter.filter(filter);
            chatListView.setAdapter(adapter);
        }
    }

    private void moveToNextActivity(int position, ArrayList<ShareableData> dataList) {
        ArrayList<Chats> chatList = ((ChatListAdapter) chatListView.getAdapter()).getChatArrayList();
        Chats chatObject = chatList.get(position);
        moveToNextActivity(chatObject, dataList);
    }

    private void moveToNextActivity(Chats chatObject, ArrayList<ShareableData> dataList) {
        Intent intent;

        boolean nearByChat = false;
        String groupServerId = chatObject.groupServerId;
        if (groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
            long contactId = chatObject.contactId;
            Contacts contact = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(contactId);

            String jid = contact.jid;
            String name = chatObject.name;
            Boolean blockStatus = chatObject.block;
            long chatId = chatObject.chatid;
            byte[] userpicture = chatObject.userImage;

            intent = ChatScreenActivity.createChatScreenIntent(getContext(), jid, name, contactId, chatId, groupServerId, userpicture, blockStatus, nearByChat);
        } else {
            long contactId = chatObject.contactId;
            String jid = groupServerId;
            String name = chatObject.name;
            long chatId = chatObject.chatid;
            byte[] groupImage = chatObject.chatImage;

            intent = ChatScreenActivity.createChatScreenIntent(getContext(), jid, name, contactId, chatId, groupServerId, groupImage, false, nearByChat);

        }

        if (dataList != null) {
            ArrayList<ShareableData> dataArrayList = getArguments().getParcelableArrayList(Constants.INTENT_FORWARD_SELECTED_IDS);
            intent.putParcelableArrayListExtra(Constants.INTENT_FORWARD_SELECTED_IDS, dataArrayList);
            intent.putExtra(ForwardSelectedItems.KEY_FILES_NOT_SENT, getArguments().getInt(ForwardSelectedItems.KEY_FILES_NOT_SENT));

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        startActivity(intent);
        if (usageFor == USAGE_FOR_SHARE_OR_FORWARD) {
            getActivity().finish();
        }
    }

    private void deleteSingleChat(Chats chatObject) {
        SportsUnityDBHelper.getInstance(getActivity()).clearChat(getActivity(), chatObject.chatid, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
        SportsUnityDBHelper.getInstance(getActivity()).clearChatEntry(chatObject.chatid, chatObject.groupServerId);

        NotificationHandler.getInstance(getActivity().getApplicationContext()).clearNotificationMessages(String.valueOf(chatObject.chatid));

        updateContent();
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
                        ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList(Contacts.AVAILABLE_BY_MY_CONTACTS);
                        ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
                        adapter.updateList(chatList);
                        chatListView.setAdapter(adapter);
                    }
                }
            });
        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, Object mediaContent) {
            //nothing
        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, String thumbnailImage, Object mediaContent) {
            //nothing
        }

    };


    private void updateContent() {
        ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList(Contacts.AVAILABLE_BY_MY_CONTACTS);
        if (chatList != null) {
            ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
            adapter.updateList(chatList);
            chatListView.setAdapter(adapter);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isSearch) {
            updateContent();
        } else {
            isSearch = false;
        }
        ActivityActionHandler.getInstance().addActionListener(ActivityActionHandler.CHAT_LIST_KEY, activityActionListener);

        NotificationHandler.dismissNotification(getActivity());
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
    }

    @Override
    public void onSearchQuery(String filterText) {

        Log.d("Chat Fragment", "search query " + filterText);

        ArrayList<Chats> chatArrayList = null;
        ChatListAdapter adapter = (ChatListAdapter) chatListView.getAdapter();
        if (filterText.length() > 0) {
            chatArrayList = SportsUnityDBHelper.getInstance(getActivity()).getChatList(filterText, Contacts.AVAILABLE_BY_MY_CONTACTS);

            ArrayList<Chats> searchedBasedOnMessage = SportsUnityDBHelper.getInstance(getActivity()).getChatsBasedOnSearchedMessage(filterText, false);
            adapter.updateSearch(chatArrayList, searchedBasedOnMessage);
        } else {
            chatArrayList = SportsUnityDBHelper.getInstance(getActivity()).getChatList(Contacts.AVAILABLE_BY_MY_CONTACTS);
            adapter.updateList(chatArrayList);
        }


/*        ArrayList<Chats> chatsList = adapter.getChatArrayList();

       if( chatArrayList.size() > 0 )
        {
            chatsList.clear();
            chatsList.addAll(chatArrayList);
            adapter.notifyDataSetChanged();
        }*/
        isSearch = true;
//        filterResults(filterText);
    }

}
