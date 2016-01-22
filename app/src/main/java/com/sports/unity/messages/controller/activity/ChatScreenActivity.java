package com.sports.unity.messages.controller.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.UserProfileActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.messages.controller.BlockUnblockUserHelper;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.GroupMessaging;
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
import com.sports.unity.util.ThreadTask;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreenActivity extends CustomAppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static ArrayList<Message> messageList;
    private static ChatScreenAdapter chatScreenAdapter;
    private static GroupChatScreenAdapter groupChatScreenAdapter;

    private static String JABBERID;
    private static String JABBERNAME;
    private static byte[] userImageBytes;
    private ArrayList<Integer> selectedItemsList = new ArrayList<>();

    private ListView mChatView;
    private boolean otherChat = false;


    private ToolbarActionsForChatScreen toolbarActionsForChatScreen = null;
//    private ArrayList<Integer> positions = new ArrayList<>();

    private static long chatID = SportsUnityDBHelper.DEFAULT_ENTRY_ID;

    public static void viewProfile(Activity activity, byte[] profilePicture, String name, String groupServerId) {

        Intent intent = new Intent(activity, UserProfileActivity.class);

        intent.putExtra("name", name);
        intent.putExtra("profilePicture", profilePicture);
        intent.putExtra("groupServerId", groupServerId);
        activity.startActivity(intent);
    }

    private static XMPPTCPConnection con;

    public static String getJABBERID() {
        return JABBERID;
    }

    private long contactID = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
    private static String groupServerId = null;
    private boolean isGroupChat = false;

    private EditText messageText;
    private Chat chat;
    private TextView status;
    private ImageView back;

    private ViewGroup parentLayout;
    private CircleImageView userPic;

    private SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);

    private PersonalMessaging personalMessaging = PersonalMessaging.getInstance(this);
    private GroupMessaging groupMessaging = GroupMessaging.getInstance(this);

    private MultiUserChat multiUserChat = null;

    private BlockUnblockUserHelper blockUnblockUserHelper = null;

    private Menu menu = null;

    private ChatKeyboardHelper chatKeyboardHelper = null;

    private HashMap<String, byte[]> mediaMap = new HashMap<>();

    private GlobalEventListener globalEventListener = new GlobalEventListener() {

        @Override
        public void onInternetStateChanged(boolean connected) {
            ChatScreenActivity.this.onInternetStateChanged(connected);
        }

        @Override
        public void onXMPPServerConnected(boolean connected) {
            ChatScreenActivity.this.onXMPPServerConnected(connected);
        }

    };

    private ActivityActionListener activityActionListener = new ActivityActionListener() {
        @Override
        public void handleAction(int id, final Object object) {
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
                            status.setText("");
                            personalMessaging.getLastTime(JABBERID);
                        } else {
                            status.setText("last seen at " + object.toString());
                        }
                    }
                });
            }

        }

        @Override
        public void handleAction(int id) {
            //id is 0, for media content uploaded.

            sendReadStatus();

            ChatScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isGroupChat) {
                        messageList = SportsUnityDBHelper.getInstance(getApplicationContext()).getMessages(chatID);
                        groupChatScreenAdapter.notifydataset(messageList);
                    } else {
                        messageList = SportsUnityDBHelper.getInstance(getApplicationContext()).getMessages(chatID);
                        chatScreenAdapter.notifydataset(messageList);
                    }
                }
            });
        }

        @Override
        public void handleMediaContent(int id, String mimeType, Object messageContent, Object mediaContent) {
            if (id == 1) {
                //handle send request for media content
                handleSendingMediaContent(mimeType, messageContent, mediaContent);
            } else if (id == 2) {
                //download completed
                mediaMap.put((String) messageContent, (byte[]) mediaContent);
            } else if (id == 3) {
                //handle incoming media message
                if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE) || mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
                    FileOnCloudHandler.getInstance(getBaseContext()).requestForDownload((String) messageContent, mimeType, (Long) mediaContent);
                } else {
                    //nothing
                }
            }

            ChatScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isGroupChat) {
                        messageList = SportsUnityDBHelper.getInstance(getApplicationContext()).getMessages(chatID);
                        groupChatScreenAdapter.notifydataset(messageList);
                    } else {
                        messageList = SportsUnityDBHelper.getInstance(getApplicationContext()).getMessages(chatID);
                        chatScreenAdapter.notifydataset(messageList);
                    }
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
        ActivityActionHandler.getInstance().removeActionListener(ActivityActionHandler.CHAT_SCREEN_KEY);
        GlobalEventHandler.getInstance().removeGlobalEventListener(ActivityActionHandler.CHAT_SCREEN_KEY);

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ActivityActionHandler.getInstance().addActionListener(ActivityActionHandler.CHAT_SCREEN_KEY, activityActionListener);
        GlobalEventHandler.getInstance().addGlobalEventListener(ActivityActionHandler.CHAT_SCREEN_KEY, globalEventListener);
        ChatScreenApplication.activityResumed();

        NotificationHandler.dismissNotification(getBaseContext());
        NotificationHandler.getInstance().clearNotificationMessages(chatID);

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

        con = XMPPClient.getConnection();

        /**
         * Initialising toolbar for chat screen activity
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Handler mHandler = new Handler();

        /**
         * Declarations ofr all the textviews and other ui elements
         */

        back = (ImageView) toolbar.findViewById(R.id.backarrow);
        messageText = (EditText) findViewById(R.id.msg);
        status = (TextView) toolbar.findViewById(R.id.status_active);
        LinearLayout profile_link = (LinearLayout) toolbar.findViewById(R.id.profile);

        profile_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewProfile(ChatScreenActivity.this, userImageBytes, JABBERNAME, groupServerId);
            }
        });
        Button mSend = (Button) findViewById(R.id.send);

        {
            /**
             * getting all the extras in the intent neccesary for communicating in chat and layout
             */
            getIntentExtras();

            boolean blockStatus = getIntent().getBooleanExtra("blockStatus", false);
            blockUnblockUserHelper = new BlockUnblockUserHelper(blockStatus, this);
            disableChatIfUserBlocked();
        }

        if (groupServerId.equals(SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID)) {
            ChatManager chatManager = ChatManager.getInstanceFor(con);
            chat = chatManager.getThreadChat(JABBERID);
            if (chat == null) {
                chat = chatManager.createChat(JABBERID + "@mm.io");
            }
        } else {
            isGroupChat = true;
            MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(con);
            multiUserChat = manager.getMultiUserChat(groupServerId + "@conference.mm.io");

        }

        otherChat = getIntent().getBooleanExtra("otherChat", false);

        initUI(toolbar);

        clearUnreadCount();

        toolbarActionsForChatScreen = ToolbarActionsForChatScreen.getInstance(this);

        /**
         * Adding custom font to views
         */
        mSend.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());

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

        final Runnable userStoppedTyping = new Runnable() {

            @Override
            public void run() {
                if (chat != null) {
                    personalMessaging.sendStatus(ChatState.paused, chat);
                }
            }
        };

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
                        groupMessaging.sendStatus(ChatState.composing, multiUserChat);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                mHandler.postDelayed(userStoppedTyping, 2000);
            }
        });

        populateMessagesOnScreen();
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(messageText.getText().toString());
            }
        });

        ArrayList<Integer> selectedMessageIds = getIntent().getIntegerArrayListExtra(Constants.INTENT_FORWARD_SELECTED_IDS);
        if (selectedMessageIds != null) {
            ForwardMessages(selectedMessageIds);
        }
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

        loadAllMediaContent(listOfMessages, new CustomTask(listOfMessages) {

            @Override
            public void run() {
                ArrayList<Message> listOfMessages = (ArrayList<Message>) getContent();
                for (Message message : listOfMessages) {
                    if (message.mimeType.equals(sportsUnityDBHelper.MIME_TYPE_TEXT)) {
                        sendMessage(message.textData);
                    } else if (message.mimeType.equals(sportsUnityDBHelper.MIME_TYPE_STICKER)) {
                        handleSendingMediaContent(message.mimeType, message.textData, null);
                    } else {
                        handleSendingMediaContent(message.mimeType, message.mediaFileName, mediaMap.get(message.mediaFileName));
                    }
                }
            }

        });


    }

    private void populateMessagesOnScreen() {
        mChatView = (ListView) findViewById(R.id.msgview);               // List for messages
        if (isGroupChat) {
            //TODO
            String s = "";
            SportsUnityDBHelper.GroupParticipants participants = sportsUnityDBHelper.getGroupParticipants(chatID);
            ArrayList<Contacts> users = participants.usersInGroup;
            for (Contacts c : users) {
                s += c.name;
                s += ", ";
            }
            s += "You";
            status.setText(s);
            messageList = sportsUnityDBHelper.getMessages(chatID);
            groupChatScreenAdapter = new GroupChatScreenAdapter(ChatScreenActivity.this, messageList);
            mChatView.setAdapter(groupChatScreenAdapter);
        } else {
            messageList = sportsUnityDBHelper.getMessages(chatID);
            chatScreenAdapter = new ChatScreenAdapter(ChatScreenActivity.this, messageList, otherChat);
            mChatView.setAdapter(chatScreenAdapter);
        }

        loadAllMediaContent(messageList, null);
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
        sendReadStatus();
    }


    private void clearUnreadCount() {
        if (chatID != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            sportsUnityDBHelper.clearUnreadCount(chatID, groupServerId);
        }
    }

    private void initUI(Toolbar toolbar) {
        TextView user = (TextView) toolbar.findViewById(R.id.chat_username);
        user.setText(JABBERNAME);
        user.setTypeface(FontTypeface.getInstance(this).getRobotoRegular());

        userPic = (CircleImageView) toolbar.findViewById(R.id.user_picture);
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
            status.setText("");
            //get group participants
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
        JABBERID = getIntent().getStringExtra("number");                                                 //name of the user you are messaging with
        JABBERNAME = getIntent().getStringExtra("name");                                                 //phone number or jid of the user you are chatting with
        contactID = getIntent().getLongExtra("contactId", SportsUnityDBHelper.DEFAULT_ENTRY_ID);
        chatID = getIntent().getLongExtra("chatId", SportsUnityDBHelper.DEFAULT_ENTRY_ID);
        userImageBytes = getIntent().getByteArrayExtra("userpicture");

        groupServerId = getIntent().getStringExtra("groupServerId");

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

        Roster roster = Roster.getInstanceFor(con);
        Presence availability = roster.getPresence(JABBERID + "@mm.io");
        Log.i("availability :", String.valueOf(availability));
        int state = retrieveState_mode(availability.getStatus());
        if (state == 1) {
            status.setText("Online");
        } else {
            status.setText("");
        }
        //Log.i("State", String.valueOf(state));
    }

    private int retrieveState_mode(String userMode) {
        int userState = 0;
        /** 0 for lastseen, 1 for online*/
        if ("Online".equals(userMode)) {
            userState = 1;
            return userState;
        } else {
            userState = 0;
            personalMessaging.getLastTime(JABBERID);
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
                PubSubMessaging pubSubMessaging = PubSubMessaging.getInstance(this);
                boolean success = pubSubMessaging.publishMessage(message, chatID, groupServerId, this);
                if (success) {
                    messageList = sportsUnityDBHelper.getMessages(chatID);
                    groupChatScreenAdapter.notifydataset(messageList);
                }
            } else {
                personalMessaging.sendTextMessage(message, chat, JABBERID, chatID, otherChat);
                personalMessaging.sendStatus(ChatState.paused, chat);
                messageList = sportsUnityDBHelper.getMessages(chatID);
                chatScreenAdapter.notifydataset(messageList);
            }
        }
        messageText.setText("");
    }

    private void handleSendingMediaContent(String mimeType, Object messageContent, Object mediaContent) {
        createChatEntryifNotExists();

        if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            String mediaFileName = (String) messageContent;

            mediaMap.put(mediaFileName, (byte[]) mediaContent);

            long messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, "", true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, null);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload((byte[]) mediaContent, mimeType, chat, messageId, otherChat);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            String mediaFileName = (String) messageContent;

            long messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, "", true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, null);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload(mediaFileName, mimeType, chat, messageId, otherChat);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            String stickerAssetPath = (String) messageContent;
            personalMessaging.sendStickerMessage(stickerAssetPath, chat, JABBERID, chatID, otherChat);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {

            String mediaFileName = (String) messageContent;

            mediaMap.put(mediaFileName, (byte[]) mediaContent);

            long messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, "", true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, null);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload((byte[]) mediaContent, mimeType, chat, messageId, otherChat);

        }
    }

    public void openCamera(View view) {
        resetToolbar();
        if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
            chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
        } else {
            if (PermissionUtil.getInstance().requestPermission(ChatScreenActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.CAMERA)), getResources().getString(R.string.camera_permission_message), Constants.REQUEST_CODE_CAMERA_PERMISSION)) {

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
            if (PermissionUtil.getInstance().requestPermission(ChatScreenActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.RECORD_AUDIO)), getResources().getString(R.string.audio_permission_message), Constants.REQUEST_CODE_RECORD_AUDIO_PERMISSION)) {
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
            viewProfile(ChatScreenActivity.this, userImageBytes, JABBERNAME, groupServerId);
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

    public static String getGroupServerId() {
        if (groupServerId != null) {
            return groupServerId;
        }
        return null;
    }

    public HashMap<String, byte[]> getMediaMap() {
        return mediaMap;
    }

    private void loadAllMediaContent(ArrayList<Message> list, final CustomTask laterTask) {
        new ThreadTask(list) {

            @Override
            public Object process() {
                ArrayList<Message> messageList = (ArrayList<Message>) object;

                for (Message message : messageList) {
                    if (
                            message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE) ||
                                    message.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)
                            ) {
                        if (message.mediaFileName != null) {
                            if (!mediaMap.containsKey(message.mediaFileName)) {
                                byte[] content = DBUtil.loadContentFromExternalFileStorage(ChatScreenActivity.this.getBaseContext(), message.mediaFileName);
                                mediaMap.put(message.mediaFileName, content);

                                if ((message.textData.length() == 0 && message.iAmSender == true) || (message.mediaFileName == null && message.iAmSender == false)) {
                                    FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload(content, message.mimeType, chat, message.id, otherChat);
                                } else {
                                    //nothing
                                }
                            } else {
                                //nothing
                            }
                        } else {
                            String checksum = message.textData;
                            FileOnCloudHandler.getInstance(getBaseContext()).requestForDownload(checksum, message.mimeType, message.id);
                        }
                    } else {
                        //TODO
                    }
                }

                if (laterTask != null) {
                    ChatScreenActivity.this.runOnUiThread(laterTask);
                }

                return null;
            }

            @Override
            public void postAction(Object object) {
                sendActionToCorrespondingActivityListener();
            }

        }.start();
    }

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

    public void onXMPPServerConnected(boolean connected) {

    }

    private boolean sendActionToCorrespondingActivityListener() {
        boolean success = false;

        ActivityActionHandler activityActionHandler = ActivityActionHandler.getInstance();
        ActivityActionListener actionListener = activityActionHandler.getActionListener(ActivityActionHandler.CHAT_SCREEN_KEY);

        if (actionListener != null) {
            actionListener.handleAction(0);
            success = true;
        }
        return success;
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
        }else if (requestCode == Constants.REQUEST_CODE_GALLERY_STORAGE_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                galleryPopup(findViewById(R.id.btn_gallery));
            } else {
                PermissionUtil.getInstance().showSnackBar(this, getString(R.string.permission_denied));
            }
        }else if (requestCode == Constants.REQUEST_CODE_RECORD_AUDIO_PERMISSION) {
            if (PermissionUtil.getInstance().verifyPermissions(grantResults)) {
                voicePopup(findViewById(R.id.btn_audiomsg));
            } else {
                PermissionUtil.getInstance().showSnackBar(this, getString(R.string.permission_denied));
            }
        }
    }
}
