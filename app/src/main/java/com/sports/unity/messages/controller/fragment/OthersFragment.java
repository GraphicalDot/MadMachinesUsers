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
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.messages.controller.activity.ForwardSelectedItems;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.ShareableData;
import com.sports.unity.messages.controller.viewhelper.OnSearchViewQueryListener;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.Constants;
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

    public static int USAGE_FOR_CHAT = 0;
    public static int USAGE_FOR_SHARE_OR_FORWARD = 1;

    private int usageFor = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_others, container, false);
        if (getArguments() != null) {
            usageFor = getArguments().getInt(Constants.INTENT_KEY_FRIENDS_FRAGMENT_USAGE);
        }
        initContent(view);
        return view;
    }

    private AdapterView.OnItemLongClickListener openContactOptions = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ArrayList<Chats> chatList = ((ChatListAdapter) otherChatListView.getAdapter()).getChatArrayList();
            Chats chatObject = chatList.get(position);

            int tag = 0;
            if (!chatObject.isGroupChat) {
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

            ArrayList<Chats> chatList = ((ChatListAdapter) otherChatListView.getAdapter()).getChatArrayList();
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
            TextView emptyText = (TextView) view.findViewById(R.id.empty_others);
            emptyText.setText("No recents chats from people around me");
        }

        otherChatListView = (ListView) view.findViewById(R.id.people_around_me_chat);

        ChatListAdapter chatListAdapter = new ChatListAdapter(getActivity(), 0, new ArrayList<Chats>());
        otherChatListView.setAdapter(chatListAdapter);
        otherChatListView.setEmptyView(view.findViewById(R.id.empty_others));

        otherChatListView.setOnItemClickListener(itemClickListener);

        otherChatListView.setOnItemLongClickListener(itemLongClickListener);
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
                        Contacts contacts = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(chatObject.id);
                        if (!chatObject.isGroupChat) {
                            ChatScreenActivity.viewProfile(getActivity(), chatObject.isGroupChat, chatObject.id, chatObject.image, chatObject.name,
                                    chatObject.jid, contacts.status, true, contacts.availableStatus, chatObject.block);
                        } else {
                            ChatScreenActivity.viewProfile(getActivity(), chatObject.isGroupChat, chatObject.id, chatObject.image, chatObject.name,
                                    chatObject.jid, contacts.status, true, contacts.availableStatus, chatObject.block);
                        }
                        alert.dismiss();
                        break;
                    case 1:
                        if (!chatObject.isGroupChat) {
                            deleteSingleChat(chatObject);
                        } else {
                            /*
                             * Exit Group
                             */
                            PubSubManager pubSubManager = new PubSubManager(XMPPClient.getConnection());
                            try {
                                LeafNode leafNode = pubSubManager.getNode(chatObject.jid);
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
                        SportsUnityDBHelper.getInstance(getActivity()).muteConversation(chatObject.id, chatObject.mute);
                        switchView.setChecked(chatObject.mute);
                        updateContent();
                        break;
                }
            }
        });
    }

    private void deleteSingleChat(Chats chatObject) {
        int contactId = chatObject.id;
        SportsUnityDBHelper.getInstance(getActivity()).clearChat(getActivity(), chatObject.id);
        SportsUnityDBHelper.getInstance(getActivity()).clearChatEntry(chatObject.id, chatObject.isGroupChat);

        NotificationHandler.getInstance(getActivity().getApplicationContext()).clearNotificationMessages(String.valueOf(chatObject.id));

        deleteContact(contactId);

        updateContent();
    }

    private void deleteContact(int contactId) {
        SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).deleteContactIfNotVisible(contactId);
    }

    private void updateContent() {
        ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList(Contacts.AVAILABLE_BY_OTHER_CONTACTS);
        if (chatList != null) {
            ChatListAdapter adapter = (ChatListAdapter) otherChatListView.getAdapter();
            adapter.updateList(chatList);
            otherChatListView.setAdapter(adapter);
        }
    }

    private void moveToNextActivity(int position, ArrayList<ShareableData> dataList) {
        ArrayList<Chats> chatList = ((ChatListAdapter) otherChatListView.getAdapter()).getChatArrayList();
        Chats chatObject = chatList.get(position);
        moveToNextActivity(chatObject, dataList);
    }

    private void moveToNextActivity(Chats chatObject, ArrayList<ShareableData> dataList) {
        Contacts contact = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getContact(chatObject.id);

        String jid = contact.jid;
        String name = chatObject.name;
        Boolean blockStatus = chatObject.block;
        int chatId = chatObject.id;
        byte[] userPicture = chatObject.image;

        Intent intent = ChatScreenActivity.createChatScreenIntent(getActivity(), chatObject.isGroupChat, jid, name, chatId, userPicture, blockStatus, true, contact.availableStatus, contact.status);
        intent.putExtra(Constants.INTENT_KEY_USER_AVAILABLE_STATUS, contact.availableStatus);
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
                        ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getActivity().getApplicationContext()).getChatList(Contacts.AVAILABLE_BY_OTHER_CONTACTS);
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
