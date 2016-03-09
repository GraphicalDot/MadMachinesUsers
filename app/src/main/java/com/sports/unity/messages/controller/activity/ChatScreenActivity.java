package com.sports.unity.messages.controller.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.UserProfileActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.BlockUnblockUserHelper;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.messages.controller.model.PubSubMessaging;
import com.sports.unity.messages.controller.model.ToolbarActionsForChatScreen;
import com.sports.unity.messages.controller.viewhelper.AudioRecordingHelper;
import com.sports.unity.messages.controller.viewhelper.ChatKeyboardHelper;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.FileOnCloudHandler;
import com.sports.unity.util.GlobalEventHandler;
import com.sports.unity.util.GlobalEventListener;
import com.sports.unity.util.NotificationHandler;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreenActivity extends CustomAppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String INTENT_KEY_JID = "jid";
    public static final String INTENT_KEY_NAME = "name";
    public static final String INTENT_KEY_CONTACT_ID = "contactId";
    public static final String INTENT_KEY_CHAT_ID = "chatId";
    public static final String INTENT_KEY_GROUP_SERVER_ID = "groupServerId";
    public static final String INTENT_KEY_IMAGE = "image";
    public static final String INTENT_KEY_BLOCK_STATUS = "blockStatus";
    public static final String INTENT_KEY_NEARBY_CHAT = "nearbyChat";

    private static String groupServerId = SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID;

    public static String getGroupServerId() {
        if (groupServerId != null) {
            return groupServerId;
        }
        return null;
    }

    public static Intent createChatScreenIntent(Context context, String jid, String name, long contactId, long chatId, String groupSeverId, byte[] userpicture, Boolean blockStatus, boolean othersChat) {
        Intent intent = new Intent(context, ChatScreenActivity.class);
        intent.putExtra(INTENT_KEY_JID, jid);
        intent.putExtra(INTENT_KEY_NAME, name);
        intent.putExtra(INTENT_KEY_CONTACT_ID, contactId);
        intent.putExtra(INTENT_KEY_CHAT_ID, chatId);
        intent.putExtra(INTENT_KEY_GROUP_SERVER_ID, groupSeverId);
        intent.putExtra(INTENT_KEY_IMAGE, userpicture);
        intent.putExtra(INTENT_KEY_BLOCK_STATUS, blockStatus);
        intent.putExtra(INTENT_KEY_NEARBY_CHAT, othersChat);
        return intent;
    }

    public static void viewProfile(Activity activity, long chatId, byte[] profilePicture, String name, String groupServerId, String jid, boolean otherChat) {
        if (groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
            Intent intent = new Intent(activity, UserProfileActivity.class);

            intent.putExtra("name", name);
            intent.putExtra("profilePicture", profilePicture);
            intent.putExtra("groupServerId", groupServerId);
            intent.putExtra("jid", jid);
            intent.putExtra("status", "available");
            intent.putExtra("otherChat", otherChat);
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity, GroupInfoActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("profilePicture", profilePicture);
            intent.putExtra("groupServerId", groupServerId);
            intent.putExtra("chatID", chatId);
            activity.startActivity(intent);
        }
    }

    private long contactID = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
    private long chatID = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
    private byte[] userImageBytes;

    private boolean isGroupChat = false;
    private String JABBERID;
    private String JABBERNAME;

    private ArrayList<Message> messageList;
    private ChatScreenAdapter chatScreenAdapter;

    private ListView mChatView;
    private boolean otherChat = false;
    private boolean isLastTimeRequired;
    private boolean isRoasterEntryRequired;
    private ToolbarActionsForChatScreen toolbarActionsForChatScreen = null;
    private String selfJid;

    private EditText messageText;
    private Chat chat;
    private TextView status;
    private ImageView back;

    private ViewGroup parentLayout;
    private CircleImageView userPic;
    private Button mSend;

    private SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);
    private PersonalMessaging personalMessaging = PersonalMessaging.getInstance(this);
    private PubSubMessaging pubSubMessaging = PubSubMessaging.getInstance(this);
    private BlockUnblockUserHelper blockUnblockUserHelper = null;

    private Menu menu = null;

    private ChatKeyboardHelper chatKeyboardHelper = null;


    private GlobalEventListener globalEventListener = new GlobalEventListener() {

        @Override
        public void onInternetStateChanged(boolean connected) {
            ChatScreenActivity.this.onInternetStateChanged(connected);
        }

        @Override
        public void onXMPPServiceAuthenticated(boolean connected) {
            ChatScreenActivity.this.onXMPPServiceAuthenticated(connected);
        }

    };

    private ActivityActionListener activityActionListener = new ActivityActionListener() {

        @Override
        public void handleAction(int id, final Object object) {
            if (id == ActivityActionHandler.EVENT_ID_CHAT_STATUS) {
                if (isGroupChat) {
                    //TODO
                } else {

                    ChatScreenActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (object.toString().equals("composing")) {
                                status.setText("typing...");
                            } else if (object.toString().equals("paused")) {
                                status.setText("Online");
                            } else if (object.toString().equals("available")) {
                                status.setText("Online");
                            } else if (object.toString().equals("unavailable")) {
                                if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                                    personalMessaging.getLastTime(JABBERID);
                                } else {
                                    isLastTimeRequired = true;
                                }
                            } else {
                                status.setText("last seen " + object.toString());
                            }
                        }
                    });
                }
            } else if (id == ActivityActionHandler.EVENT_ID_RECEIPT) {
                ChatScreenActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        int receiptKind = (Integer) object;
                        if (receiptKind == PersonalMessaging.RECEIPT_KIND_CLIENT) {

                        } else if (receiptKind == PersonalMessaging.RECEIPT_KIND_SERVER) {
                            playConversationTone();
                        } else if (receiptKind == PersonalMessaging.RECEIPT_KIND_READ) {

                        }

                        updateMessageList();
                    }

                });
            }
        }

        @Override
        public void handleAction(int id) {
            //id is 0, for media content uploaded.

            ChatScreenActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updateMessageList();
                    sendReadStatus();
                }

            });
        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, Object mediaContent) {
            if (id == ActivityActionHandler.EVENT_ID_SEND_MEDIA) {
                //handle send request for media content
                handleSendingMediaContent(mimeType, messageContent, mediaContent, null);
            } else if (id == ActivityActionHandler.EVENT_ID_DOWNLOAD_COMPLETED) {
                //download completed
//                mediaMap.put((String) messageContent, (byte[]) mediaContent);
            } else if (id == ActivityActionHandler.EVENT_ID_INCOMING_MEDIA) {
                //handle incoming media message
                if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE) && UserUtil.isMediaAutoDownloadEnabled(getApplicationContext(), UserUtil.IMAGE_MEDIA)) {
                    FileOnCloudHandler.getInstance(getBaseContext()).requestForDownload((String) messageContent, mimeType, (Long) mediaContent, JABBERID);
                } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO) && UserUtil.isMediaAutoDownloadEnabled(getApplicationContext(), UserUtil.AUDIO_MEDIA)) {
                    FileOnCloudHandler.getInstance(getBaseContext()).requestForDownload((String) messageContent, mimeType, (Long) mediaContent, JABBERID);
                } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO) && UserUtil.isMediaAutoDownloadEnabled(getApplicationContext(), UserUtil.VIDEO_MEDIA)) {
                    FileOnCloudHandler.getInstance(getBaseContext()).requestForDownload((String) messageContent, mimeType, (Long) mediaContent, JABBERID);
                }
            }

            ChatScreenActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updateMessageList();
                }

            });
        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, String thumbnailImage, Object mediaContent) {
            if (id == ActivityActionHandler.EVENT_ID_SEND_MEDIA) {
                //handle send request for media content
                handleSendingMediaContent(mimeType, messageContent, mediaContent, thumbnailImage);
            }

            ChatScreenActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updateMessageList();
                }

            });
        }

    };

    @Override
    protected void onDestroy() {
        ChatScreenApplication.activityDestroyed();
        AudioRecordingHelper.cleanUp();

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        ChatScreenApplication.activityPaused();
        AudioRecordingHelper.getInstance(this).stopAndReleaseMediaPlayer();

        super.onPause();
    }

    @Override
    public void onStop() {

        ChatScreenApplication.activityStopped();
        ActivityActionHandler.getInstance().removeActionListener(ActivityActionHandler.CHAT_SCREEN_KEY, JABBERID);
        GlobalEventHandler.getInstance().removeGlobalEventListener(ActivityActionHandler.CHAT_SCREEN_KEY);
        super.onStop();
        /*if (XMPPClient.getInstance().isConnectionAuthenticated()) {
            try {

                RosterEntry rosterSelf = Roster.getInstanceFor(XMPPClient.getConnection()).getEntry(selfJid + "@" + XMPPClient.SERVICE_NAME);
                RosterEntry rosterFriend = Roster.getInstanceFor(XMPPClient.getConnection()).getEntry(JABBERID + "@" + XMPPClient.SERVICE_NAME);
                Log.d("max", "selfjid is--" + selfJid + "@" + XMPPClient.SERVICE_NAME + "<<roseterself" + rosterSelf);
                if (rosterFriend != null) {
                    if (!rosterFriend.getType().equals(RosterPacket.ItemType.both)) {
                        Roster.getInstanceFor(XMPPClient.getConnection()).removeEntry(rosterFriend);
                        if (rosterSelf != null) {
                            Log.d("max", "SelfType__" + rosterSelf.getType());
                            Roster.getInstanceFor(XMPPClient.getConnection()).removeEntry(rosterSelf);

                        }
                    } else {
                        Presence presence1 = new Presence(Presence.Type.unsubscribe);
                        presence1.setTo(JABBERID + "@" + XMPPClient.SERVICE_NAME);
                        XMPPClient.getConnection().sendStanza(presence1);
                    }
                } else {
                    Presence presence1 = new Presence(Presence.Type.unsubscribe);
                    presence1.setTo(JABBERID + "@" + XMPPClient.SERVICE_NAME);
                    XMPPClient.getConnection().sendStanza(presence1);
                }
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            }
        } else {
            //nothing
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        ActivityActionHandler.getInstance().addActionListener(ActivityActionHandler.CHAT_SCREEN_KEY, JABBERID, activityActionListener);
        GlobalEventHandler.getInstance().addGlobalEventListener(ActivityActionHandler.CHAT_SCREEN_KEY, globalEventListener);
        ChatScreenApplication.activityResumed();

        NotificationHandler.dismissNotification(getBaseContext());
        NotificationHandler.getInstance(getApplicationContext()).clearNotificationMessages(String.valueOf(chatID));

        //TODO update message list
//        activityActionListener.handleAction(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (toolbarActionsForChatScreen.getSelecteditems() != 0) {
            toolbarActionsForChatScreen.resetList(mChatView);
            invalidateOptionsMenu();
        } else if (toolbarActionsForChatScreen.getSearchFlag()) {
            toolbarActionsForChatScreen.setSearchFlag(false);
            invalidateOptionsMenu();
            chatScreenAdapter.filterSearchQuery("");
        } else {
            super.onBackPressed();
        }
    }

    private void resetToolbar() {

        if (toolbarActionsForChatScreen.getSelecteditems() != 0) {
            toolbarActionsForChatScreen.resetList(mChatView);
            invalidateOptionsMenu();

        } else if (toolbarActionsForChatScreen.getSearchFlag()) {
            toolbarActionsForChatScreen.setSearchFlag(false);
            invalidateOptionsMenu();
            chatScreenAdapter.filterSearchQuery("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        parentLayout = (ViewGroup) findViewById(R.id.chat_layout_root_view);

        chatKeyboardHelper = ChatKeyboardHelper.getInstance(true);
        chatKeyboardHelper.createPopupWindowOnKeyBoard(parentLayout, this);
        chatKeyboardHelper.checkKeyboardHeight();
        toolbarActionsForChatScreen = ToolbarActionsForChatScreen.getInstance(this);

        selfJid = TinyDB.getInstance(ChatScreenActivity.this).getString(TinyDB.KEY_USER_JID);
        getIntentExtras();
        clearUnreadCount();
        initToolbar();
        hideStatusIfUserBlocked();

        final Handler mHandler = new Handler();

        mSend = (Button) findViewById(R.id.send);
        mSend.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());

        getChatThread();

        populateMessagesOnScreen();
        setEventListeners(mHandler);

        checkForwardMessageQueue();
    }

    private void hideStatusIfUserBlocked() {
        boolean blockStatus = getIntent().getBooleanExtra("blockStatus", false);
        if (blockStatus) {
            status.setVisibility(View.GONE);
        } else {
            status.setVisibility(View.VISIBLE);
        }
        blockUnblockUserHelper = new BlockUnblockUserHelper(blockStatus, this, status);
        disableChatIfUserBlocked();
    }

    private void checkForwardMessageQueue() {
        ArrayList<Integer> selectedMessageIds = getIntent().getIntegerArrayListExtra(Constants.INTENT_FORWARD_SELECTED_IDS);
        if (selectedMessageIds != null) {
            ForwardMessages(selectedMessageIds);
        }
    }

    final Runnable userStoppedTyping = new Runnable() {

        @Override
        public void run() {
            if (chat != null) {
                personalMessaging.sendStatus(ChatState.paused, chat);
            }
        }
    };

    private void setEventListeners(Handler mHandler) {

        final Handler handler = mHandler;

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarActionsForChatScreen.getSelecteditems() != 0) {
                    toolbarActionsForChatScreen.resetList(mChatView);
                    invalidateOptionsMenu();
                } else {
                    onBackPressed();
                }
            }
        });


        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resetToolbar();
                if (s.length() > 0) {
                    if (!isGroupChat) {
                        personalMessaging.sendStatus(ChatState.composing, chat);
                    } else {
//                        groupMessaging.sendStatus(ChatState.composing, multiUserChat);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.postDelayed(userStoppedTyping, 2000);
            }
        });

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(messageText.getText().toString());
            }
        });

        mChatView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean invalidate = toolbarActionsForChatScreen.onClickSelectView(view, position, messageList);
                if (invalidate) {
                    invalidateOptionsMenu();
                }
            }
        });
        mChatView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                boolean invalidate = toolbarActionsForChatScreen.onLongClickSelectView(view, messageList.get(position), position);
                if (invalidate) {
                    invalidateOptionsMenu();
                }
                chatScreenAdapter.filterSearchQuery("");
                return true;
            }
        });


    }

    private void getChatThread() {
        if (groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
            ChatManager chatManager = ChatManager.getInstanceFor(XMPPClient.getConnection());
            chat = chatManager.getThreadChat(JABBERID + "@mm.io");
            if (chat == null) {
                chat = chatManager.createChat(JABBERID + "@mm.io");
            }
        } else {
            isGroupChat = true;
            pubSubMessaging.updateLeadNode(groupServerId);
//            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(con);
//            multiUserChat = manager.getMultiUserChat(groupServerId + "@conference.mm.io");
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        back = (ImageView) toolbar.findViewById(R.id.backarrow);
        back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        messageText = (EditText) findViewById(R.id.msg);
        status = (TextView) toolbar.findViewById(R.id.status_active);
        LinearLayout profile_link = (LinearLayout) toolbar.findViewById(R.id.profile);

        profile_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewProfile(ChatScreenActivity.this, chatID, userImageBytes, JABBERNAME, groupServerId, JABBERID, otherChat);
            }
        });

        initUI(toolbar);
    }

    private void disableChatIfUserBlocked() {
        if (blockUnblockUserHelper.isBlockStatus()) {
            chatKeyboardHelper.disableOrEnableKeyboardAndMediaButtons(blockUnblockUserHelper.isBlockStatus(), this);
        }

        LinearLayout messagecomposeLayout = (LinearLayout) findViewById(R.id.type_msg);
        messagecomposeLayout.setClickable(true);
        messagecomposeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("layoutclicked", "true");
                if (blockUnblockUserHelper.isBlockStatus()) {
                    displayAlertToUnblockUser();
                } else {
                    //nothing
                }
            }
        });
    }

    /**
     * For blocked users show message "user blocked. Tap here to unblock" and do not show his/her last seen and status
     */

    private void displayAlertToUnblockUser() {
        AlertDialog.Builder build = new AlertDialog.Builder(
                ChatScreenActivity.this);
        build.setMessage(
                "Unblock " + JABBERNAME + " To send a message? ");
        build.setPositiveButton("unblock",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        blockUnblockUserHelper.onMenuItemSelected(ChatScreenActivity.this, contactID, JABBERID, menu);
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


    private void ForwardMessages(ArrayList<Integer> intArrayExtra) {
        ArrayList<Message> listOfMessages = new ArrayList<>();
        for (int id : intArrayExtra) {
            Message message = sportsUnityDBHelper.getMessage(id);
            listOfMessages.add(message);
        }

        for (Message message : listOfMessages) {
            if (message.mimeType.equals(sportsUnityDBHelper.MIME_TYPE_TEXT)) {
                sendMessage(message.textData);
            } else if (message.mimeType.equals(sportsUnityDBHelper.MIME_TYPE_STICKER)) {
                handleSendingMediaContent(message.mimeType, message.textData, null, null);
            } else {
                String thumbnailImage = null;
                if (message.media != null) {
                    thumbnailImage = Base64.encodeToString(message.media, Base64.DEFAULT);
                }

                handleSendingMediaContent(message.mimeType, message.mediaFileName, null, thumbnailImage);
            }
        }

    }

    private void populateMessagesOnScreen() {
        mChatView = (ListView) findViewById(R.id.msgview);               // List for messages
        if (isGroupChat) {
            setGroupMembers();
        }

        messageList = sportsUnityDBHelper.getMessages(chatID);
        chatScreenAdapter = new ChatScreenAdapter(ChatScreenActivity.this, messageList, otherChat, isGroupChat, groupServerId, JABBERID);
        mChatView.setAdapter(chatScreenAdapter);

//        loadAllMediaContent(messageList, null);

        sendReadStatus();
    }

    private void setGroupMembers() {
        String s = "";
        SportsUnityDBHelper.GroupParticipants participants = sportsUnityDBHelper.getGroupParticipants(chatID);
        ArrayList<Contacts> users = participants.usersInGroup;
        for (Contacts c : users) {
            s += c.name;
            s += ", ";
        }
        s += "You";
        status.setText(s);
    }


    private void clearUnreadCount() {
        if (chatID != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            sportsUnityDBHelper.clearUnreadCount(chatID, groupServerId);
        }
    }

    private void updateMessageList() {
        Message oldLastMessage = null;
        if (messageList.size() > 0) {
            oldLastMessage = messageList.get(messageList.size() - 1);
        }

//        if (isGroupChat) {
//            messageList = SportsUnityDBHelper.getInstance(getApplicationContext()).getMessages(chatID);
//            groupChatScreenAdapter.notifydataset(messageList);
//        } else {
//
//        }

        messageList = SportsUnityDBHelper.getInstance(getApplicationContext()).getMessages(chatID);
        chatScreenAdapter.notifydataset(messageList);

        if (messageList.size() > 0) {
            Message lastMessage = messageList.get(messageList.size() - 1);
            if (!lastMessage.iAmSender) {
                if ((oldLastMessage == null ? 0 : oldLastMessage.id) != lastMessage.id) {
                    playConversationTone();
                } else {
                    //nothing
                }
            }
        }
    }

    private void initUI(Toolbar toolbar) {
        TextView user = (TextView) toolbar.findViewById(R.id.chat_username);
        user.setText(JABBERNAME);
        user.setTypeface(FontTypeface.getInstance(this).getRobotoRegular());

        userPic = (CircleImageView) findViewById(R.id.user_picture);
        if (isGroupChat) {
            if (userImageBytes == null) {
                userPic.setImageResource(R.drawable.ic_group);
            } else {
                userPic.setImageBitmap(BitmapFactory.decodeByteArray(userImageBytes, 0, userImageBytes.length));
            }

        } else if (userImageBytes != null) {
            userPic.setImageBitmap(BitmapFactory.decodeByteArray(userImageBytes, 0, userImageBytes.length));
        }

        if (isGroupChat) {
            //TODO
        } else {
            try {
                getLastSeen();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getIntentExtras() {
        groupServerId = getIntent().getStringExtra(INTENT_KEY_GROUP_SERVER_ID);
        if (groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
            JABBERID = getIntent().getStringExtra(INTENT_KEY_JID);
            if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                createRosterEntry();
            } else {
                isRoasterEntryRequired = true;
            }
            JABBERNAME = getIntent().getStringExtra(INTENT_KEY_NAME);                                                 //jid of the user you are chatting with
            contactID = getIntent().getLongExtra(INTENT_KEY_CONTACT_ID, SportsUnityDBHelper.DEFAULT_ENTRY_ID);
            chatID = getIntent().getLongExtra(INTENT_KEY_CHAT_ID, SportsUnityDBHelper.DEFAULT_ENTRY_ID);
            userImageBytes = getIntent().getByteArrayExtra(INTENT_KEY_IMAGE);
            otherChat = getIntent().getBooleanExtra(INTENT_KEY_NEARBY_CHAT, false);
        } else {
            JABBERNAME = getIntent().getStringExtra(INTENT_KEY_NAME);
            contactID = getIntent().getLongExtra(INTENT_KEY_CONTACT_ID, SportsUnityDBHelper.DEFAULT_ENTRY_ID);

            chatID = getIntent().getLongExtra(INTENT_KEY_CHAT_ID, SportsUnityDBHelper.DEFAULT_ENTRY_ID);
            userImageBytes = getIntent().getByteArrayExtra(INTENT_KEY_IMAGE);
            otherChat = getIntent().getBooleanExtra(INTENT_KEY_NEARBY_CHAT, false);
        }
    }

    private void createRosterEntry() {
       /* try {
            Presence presence1 = new Presence(Presence.Type.subscribe);
            presence1.setTo(JABBERID + "@" + XMPPClient.SERVICE_NAME);
            XMPPClient.getConnection().sendStanza(presence1);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void createChatEntryifNotExists() {
        if (!isGroupChat) {
            if (chatID == SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
                chatID = sportsUnityDBHelper.getChatEntryID(contactID, groupServerId);
                if (chatID != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
                    //nothing
                } else {
                    chatID = sportsUnityDBHelper.createChatEntry(JABBERNAME, contactID, false);
                    Log.i("ChatEntry : ", "chat entry made " + chatID + " : " + contactID);
                }
            } else {
                //nothing
            }
        }
    }

    public void getLastSeen() throws SmackException.NotConnectedException {

        if (XMPPClient.getConnection() != null) {
            Roster roster = Roster.getInstanceFor(XMPPClient.getConnection());
            Presence availability = roster.getPresence(JABBERID + "@mm.io");
            Log.i("userPresence :", String.valueOf(availability.toXML()));
            int state = retrieveState_mode(availability.getStatus());
            if (state == 1) {
                status.setText("Online");
            } else {
                //do nothing
            }
        }

    }

    private int retrieveState_mode(String status) {
        int userState = 0;
        /** 0 for lastseen, 1 for online*/
        if ("Online".equals(status)) {
            userState = 1;
            return userState;
        } else {
            userState = 0;
            if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                personalMessaging.getLastTime(JABBERID);
            } else {
                isLastTimeRequired = true;
            }
            return userState;
        }
    }

    private void sendMessage(String message) {

        createChatEntryifNotExists();

        if (message.equals("") || message == null) {
            //Do nothing
        } else {
            Log.i("Message Entry", "adding message chat " + chatID);

            if (isGroupChat) {
//                groupMessaging.sendMessageToGroup(messageText.getText().toString(), multiUserChat, chatID, groupServerId, TinyDB.getInstance(this).getString(TinyDB.KEY_USERNAME));
                pubSubMessaging.publishMessage(message, chatID, groupServerId, this);
            } else {
                personalMessaging.sendTextMessage(message, chat, JABBERID, chatID, otherChat);
                personalMessaging.sendStatus(ChatState.paused, chat);
            }
            messageList = sportsUnityDBHelper.getMessages(chatID);
            chatScreenAdapter.notifydataset(messageList);
        }
        messageText.setText("");
    }

    private void handleSendingMediaContent(String mimeType, Object messageContent, Object mediaContent, String thumbnailImage) {
        createChatEntryifNotExists();

        if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            String mediaFileName = (String) messageContent;

//            mediaMap.put(mediaFileName, (byte[]) mediaContent);

            byte[] bytesOfThumbnail = null;
            if (thumbnailImage != null) {
                bytesOfThumbnail = Base64.decode(thumbnailImage, Base64.DEFAULT);
            }

            long messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, "", true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID, groupServerId);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload(mediaFileName, thumbnailImage, mimeType, chat, messageId, otherChat, isGroupChat, groupServerId, JABBERID);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            String mediaFileName = (String) messageContent;

            byte[] bytesOfThumbnail = null;
            if (thumbnailImage != null) {
                bytesOfThumbnail = Base64.decode(thumbnailImage, Base64.DEFAULT);
            }

            long messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, "", true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload(mediaFileName, thumbnailImage, mimeType, chat, messageId, otherChat, isGroupChat, groupServerId, JABBERID);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            String stickerAssetPath = (String) messageContent;
            if (!this.isGroupChat) {
                personalMessaging.sendStickerMessage(stickerAssetPath, chat, JABBERID, chatID, otherChat);
            } else {
                PubSubMessaging.getInstance(getApplicationContext()).sendStickerMessage(stickerAssetPath, chatID, groupServerId);
            }
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {

            String mediaFileName = (String) messageContent;

//            mediaMap.put(mediaFileName, (byte[]) mediaContent);

            long messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, "", true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, null);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload(mediaFileName, null, mimeType, chat, messageId, otherChat, isGroupChat, groupServerId, JABBERID);

        }
    }

    private void playConversationTone() {
        if (UserUtil.isConversationTones()) {
            Uri notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.conversation_tone);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } else {
            //nothing
        }
    }

    public void openCamera(View view) {
        resetToolbar();
        if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
            chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
        } else {
            if (PermissionUtil.getInstance().requestPermission(ChatScreenActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.camera_permission_message), Constants.REQUEST_CODE_CAMERA_PERMISSION)) {

                chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
            }
        }
    }

    public void emojipopup(View view) {
        resetToolbar();
        chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
    }

    public void galleryPopup(View view) {
        resetToolbar();
        if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
            chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
        } else {
            if (PermissionUtil.getInstance().requestPermission(ChatScreenActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.gallery_permission_message), Constants.REQUEST_CODE_GALLERY_STORAGE_PERMISSION)) {

                chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
            }
        }
    }

    public void voicePopup(View view) {
        resetToolbar();
        if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
            chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
        } else {
            if (PermissionUtil.getInstance().requestPermission(ChatScreenActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.audio_permission_message), Constants.REQUEST_CODE_RECORD_AUDIO_PERMISSION)) {
                chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
            }
        }
    }

    public void openKeyBoard(View view) {
        resetToolbar();
        chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 333) {
            //TODO handle forward
        } else {
            //nothing
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i("onprepareoptionsmeu", "true");
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.tool_bar_chat);
        toolbarActionsForChatScreen.getToolbarMenu(mtoolbar, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_chat_screen, menu);

        this.menu = menu;

        MenuItem deleteMessage = menu.findItem(R.id.delete);
        deleteMessage.setVisible(false);
        MenuItem copyMessage = menu.findItem(R.id.copy);
        copyMessage.setVisible(false);
        MenuItem forwardMessage = menu.findItem(R.id.forward);
        forwardMessage.setVisible(false);
        MenuItem upNavigation = menu.findItem(R.id.navigating_up);
        upNavigation.setVisible(false);
        MenuItem downNavigation = menu.findItem(R.id.navigating_down);
        downNavigation.setVisible(false);

        blockUnblockUserHelper.initViewBasedOnBlockStatus(menu);

        Toolbar mtoolbar = (Toolbar) findViewById(R.id.tool_bar_chat);
        final EditText searchText = (EditText) mtoolbar.findViewById(R.id.search_text);
        searchText.setText("");
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                chatScreenAdapter.filterSearchQuery(searchText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_contact) {
            viewProfile(ChatScreenActivity.this, chatID, userImageBytes, JABBERNAME, groupServerId, JABBERID, otherChat);
            return true;
        } else if (id == R.id.action_block_user) {
            blockUnblockUserHelper.onMenuItemSelected(this, contactID, JABBERID, menu);
        } else if (id == R.id.action_clear_chat) {

            sportsUnityDBHelper.clearChat(getApplicationContext(), chatID, groupServerId);

            AudioRecordingHelper.getInstance(this).stopAndReleaseMediaPlayer();
            AudioRecordingHelper.getInstance(this).clearProgressMap();

            messageList = sportsUnityDBHelper.getMessages(chatID);

            chatScreenAdapter.notifydataset(messageList);
        } else if (id == R.id.forward) {
            toolbarActionsForChatScreen.forwardSelectedMessages(messageList);
        } else if (id == R.id.delete) {
            toolbarActionsForChatScreen.deleteMessages(messageList, chatID, chatScreenAdapter);
            toolbarActionsForChatScreen.resetList(mChatView);
            invalidateOptionsMenu();
        } else if (id == R.id.copy) {
            toolbarActionsForChatScreen.copySelectedMessages(messageList);
            toolbarActionsForChatScreen.resetList(mChatView);
            invalidateOptionsMenu();
        } else if (id == R.id.navigating_up) {
            int pos = chatScreenAdapter.getPosition("up");
            if (pos >= 0) {
                mChatView.smoothScrollToPosition(pos);
            }
            return true;
        } else if (id == R.id.navigating_down) {
            int pos = chatScreenAdapter.getPosition("down");
            if (pos >= 0) {
                mChatView.smoothScrollToPosition(pos);
            }
            return true;
        } else if (id == R.id.searchChatScreen) {
            toolbarActionsForChatScreen.setSearchFlag(true);
            invalidateOptionsMenu();

            View view = findViewById(R.id.btn_text);
            chatKeyboardHelper.openTextKeyBoard(view, this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public HashMap<String, byte[]> getMediaMap() {
//        return mediaMap;
//    }

//    private void loadAllMediaContent(ArrayList<Message> list, final CustomTask laterTask) {
//        new ThreadTask(list) {
//
//            @Override
//            public Object process() {
//                ArrayList<Message> messageList = (ArrayList<Message>) object;
//
//                for (Message message : messageList) {
//                    if (
//                            message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE) ||
//                                    message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)
//                            ) {
//                        if (message.mediaFileName != null) {
//                            if (!mediaMap.containsKey(message.mediaFileName)) {
//                                byte[] content = DBUtil.loadContentFromExternalFileStorage(ChatScreenActivity.this.getBaseContext(), message.mediaFileName);
//                                mediaMap.put(message.mediaFileName, content);
//
//                                if ((message.textData.length() == 0 && message.iAmSender == true)) {
//                                    FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload(message.mediaFileName, null, message.mimeType, chat, message.id, otherChat);
//                                } else {
//                                    //nothing
//                                }
//                            } else {
//                                //nothing
//                            }
//                        } else {
////                            FileOnCloudHandler.getInstance(getBaseContext()).requestForDownload(message.textData, message.mimeType, message.id);
//                        }
//                    } else {
//                        //TODO
//                    }
//                }
//
//                if (laterTask != null) {
//                    ChatScreenActivity.this.runOnUiThread(laterTask);
//                }
//
//                return null;
//            }
//
//            @Override
//            public void postAction(Object object) {
//                ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_SCREEN_KEY);
//            }
//
//        }.start();
//    }

    private void sendReadStatus() {
        for (Message message : messageList) {
            if (!(message.iAmSender || message.messagesRead)) {
                personalMessaging.sendReadStatus(message.number, message.messageStanzaId);
            } else {
                //nothing
            }
        }
    }

    public void onInternetStateChanged(boolean connected) {

    }

    public void onXMPPServiceAuthenticated(boolean connected) {
        if (connected) {
            if (isLastTimeRequired) {
                personalMessaging.getLastTime(JABBERID);
                isLastTimeRequired = false;
            } else {
                //nothing
            }
            if (isRoasterEntryRequired) {
                createRosterEntry();
                isRoasterEntryRequired = false;
            } else {
                //nothing;
            }
        }
    }

    private abstract class CustomTask implements Runnable {

        private Object content;

        private CustomTask(Object content) {
            this.content = content;
        }

        public Object getContent() {
            return content;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_CAMERA_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                openCamera(findViewById(R.id.btn_camera));
            } else {
                PermissionUtil.getInstance().showSnackBar(this, getString(R.string.permission_denied));
            }
        } else if (requestCode == Constants.REQUEST_CODE_GALLERY_STORAGE_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                galleryPopup(findViewById(R.id.btn_gallery));
            } else {
                PermissionUtil.getInstance().showSnackBar(this, getString(R.string.permission_denied));
            }
        } else if (requestCode == Constants.REQUEST_CODE_RECORD_AUDIO_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                voicePopup(findViewById(R.id.btn_audiomsg));
            } else {
                PermissionUtil.getInstance().showSnackBar(this, getString(R.string.permission_denied));
            }
        }
    }
}
