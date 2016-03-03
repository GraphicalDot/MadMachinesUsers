package com.sports.unity.messages.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.viewhelper.OnSearchViewQueryListener;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.NotificationHandler;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;

import java.util.ArrayList;

/**
 * Created by madmachines on 23/9/15.
 */
public class OthersFragment extends Fragment implements OnSearchViewQueryListener {

    private ListView otherChatListView;
    private View view;
    private ChatFragmentDialogListAdapter chatFragmentDialogListAdapter;


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

        otherChatListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int tag = (Integer) view.getTag();
                ArrayList<Chats> chatList = ((ChatListAdapter) otherChatListView.getAdapter()).getChatArrayList();
                Chats chatObject = chatList.get(position);
                showDialogWindow(position, tag, chatObject);
                return true;
            }
        });
    }

    private void showDialogWindow(int position, int tag, final Chats chatObject) {
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
                            ChatScreenActivity.viewProfile(getActivity(), chatObject.userImage, chatObject.name,
                                    chatObject.groupServerId, SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(chatObject.contactId).jid, true);
                        } else {
                            ChatScreenActivity.viewProfile(getActivity(), chatObject.chatImage, chatObject.name, chatObject.groupServerId,
                                    SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(chatObject.contactId).jid, true);
                        }
                        alert.dismiss();
                        break;
                    case 1:
                        if (chatObject.groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
                            deleteSingleChat(chatObject);
                        } else {
                            /*
                             * Exit Group
                             */
                            PubSubManager pubSubManager = new PubSubManager(XMPPClient.getConnection());
                            try {
                                LeafNode leafNode = pubSubManager.getNode(chatObject.groupServerId);
                                leafNode.unsubscribe(TinyDB.KEY_USERNAME);
                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }

                        }
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
                        updateContent();
                        break;
                }
            }
        });
    }

    private void deleteSingleChat(Chats chatObject) {
        int contactId = chatObject.contactId;
        SportsUnityDBHelper.getInstance(getActivity()).clearChat(getActivity(), chatObject.chatid, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
        SportsUnityDBHelper.getInstance(getActivity()).clearChatEntry(chatObject.chatid);

        NotificationHandler.getInstance(getActivity().getApplicationContext()).clearNotificationMessages(String.valueOf(chatObject.chatid));

        deleteContact(contactId);

        updateContent();
    }

    private void deleteContact(int contactId) {
        SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).deleteContactIfNotAvailable(contactId);
    }

    private void updateContent() {
        ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList(true);
        if (chatList != null) {
            ChatListAdapter adapter = (ChatListAdapter) otherChatListView.getAdapter();
            adapter.updateList(chatList);
            otherChatListView.setAdapter(adapter);
        }
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

        String jid = contact.jid;
        String name = chatObject.name;
        Boolean blockStatus = chatObject.block;
        long chatId = chatObject.chatid;
        byte[] userpicture = chatObject.userImage;

        intent.putExtra("jid", jid);
        intent.putExtra("name", name);
        intent.putExtra("contactId", contactId);
        intent.putExtra("chatId", chatId);
        intent.putExtra("groupServerId", groupSeverId);
        intent.putExtra("userpicture", userpicture);
        intent.putExtra("blockStatus", blockStatus);
        intent.putExtra("otherChat", true);

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateContent();

        ActivityActionHandler.getInstance().addActionListener(ActivityActionHandler.CHAT_OTHERS_LIST_KEY, activityActionListener);

        NotificationHandler.dismissNotification(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        ActivityActionHandler.getInstance().removeActionListener(ActivityActionHandler.CHAT_OTHERS_LIST_KEY);
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
                    if (otherChatListView != null) {
                        ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList(true);
                        ChatListAdapter adapter = (ChatListAdapter) otherChatListView.getAdapter();
                        adapter.updateList(chatList);
                        otherChatListView.setAdapter(adapter);
                    }
                }
            });
        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, Object mediaContent) {

        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, String thumbnailImage, Object mediaContent) {
            //nothing
        }

    };

    @Override
    public void onSearchQuery(String filterText) {

    }
}
