package com.sports.unity.messages.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.UserProfileActivity;
import com.sports.unity.common.model.FontTypeface;
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
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreenActivity extends CustomAppCompatActivity {

    private static ArrayList<Message> messageList;
    private static ChatScreenAdapter chatScreenAdapter;
    private static GroupChatScreenAdapter groupChatScreenAdapter;

    private static String JABBERID;
    private static String JABBERNAME;
    private static byte[] userImageBytes;
    private ArrayList<Integer> selectedItemsList = new ArrayList<>();

    boolean selectedFlag = false;
    boolean mediaSelected = false;
    int mediaSelectedItems = 0;
    int selecteditems = 0;
    private ListView mChatView;

    private ToolbarActionsForChatScreen toolbarActionsForChatScreen = null;
    private ArrayList<Integer> positions = new ArrayList<>();

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
    private ImageButton back;

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

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ActivityActionHandler.getInstance().addActionListener(ActivityActionHandler.CHAT_SCREEN_KEY, activityActionListener);
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
        } else {
            super.onBackPressed();
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

        back = (ImageButton) toolbar.findViewById(R.id.backarrow);
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
            blockUnblockUserHelper = new BlockUnblockUserHelper(blockStatus);
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
            chatScreenAdapter = new ChatScreenAdapter(ChatScreenActivity.this, messageList);
            mChatView.setAdapter(chatScreenAdapter);
        }

        loadAllMediaContent(messageList, null);
        mChatView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean invalidate = toolbarActionsForChatScreen.onClickSelectView(view, position, messageList);
//                if (selectedFlag == true) {
//                    if (selectedItemsList.contains(position)) {
//                        view.setBackgroundColor(Color.TRANSPARENT);
//                        selecteditems--;
//                        selectedItemsList.remove(Integer.valueOf(position));
//                        if (!messageList.get(position).mimeType.equals(sportsUnityDBHelper.MIME_TYPE_TEXT)) {
//                            mediaSelectedItems--;
//                            if (mediaSelectedItems == 0) {
//                                mediaSelected = false;
//                            }
//                        }
//                        Log.i("view", "selected");
//                    } else {
//                        view.setBackgroundColor(getResources().getColor(R.color.list_selector));
//                        selecteditems++;
//                        selectedItemsList.add(position);
//                        if (!messageList.get(position).mimeType.equals(sportsUnityDBHelper.MIME_TYPE_TEXT)) {
//                            mediaSelected = true;
//                            mediaSelectedItems++;
//                        }
//                        Log.i("view", "notselected");
//                    }
//                } else {
//                    //do nothing
//                }
                if (invalidate) {
                    invalidateOptionsMenu();
                }
//                checkSelectedItems();
            }
        });
        mChatView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                boolean invalidate = toolbarActionsForChatScreen.onLongClickSelectView(view, messageList.get(position), position);
//                if (selectedFlag == false) {
//                    view.setBackgroundColor(getResources().getColor(R.color.list_selector));
//                    selectedFlag = true;
//                    selecteditems++;
//                    selectedItemsList.add(position);
//                    if (!messageList.get(position).mimeType.equals(sportsUnityDBHelper.MIME_TYPE_TEXT)) {
//                        mediaSelected = true;
//                        mediaSelectedItems++;
//                    }
//                } else {
//                    //do nothing
//                }
                if (invalidate) {
                    invalidateOptionsMenu();
                }
//                checkSelectedItems();
                return true;
            }
        });
        sendReadStatus();
    }

//    private void checkSelectedItems() {
//        if (selecteditems == 0) {
//            selectedFlag = false;
//            invalidateOptionsMenu();
//        } else {
//            if (selecteditems > 0) {
//                invalidateOptionsMenu();
//            } else {
//                //do nothing
//            }
//        }
//    }

    private void clearUnreadCount() {
        if (chatID != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            sportsUnityDBHelper.clearUnreadCount(chatID, groupServerId);
        }
    }

    private void initUI(Toolbar toolbar) {
        TextView user = (TextView) toolbar.findViewById(R.id.chat_username);
        user.setText(JABBERNAME);
        user.setTypeface(FontTypeface.getInstance(this).getRobotoRegular());
        getWindow().setBackgroundDrawableResource(R.drawable.img_chat);
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
                    chatID = sportsUnityDBHelper.createChatEntry(JABBERNAME, contactID);
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
        if (blockUnblockUserHelper.isBlockStatus()) {
            blockUnblockUserHelper.showAlert_ToSendMessage_UnblockUser(this, contactID, JABBERID, menu);
            return;
        }

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
                personalMessaging.sendTextMessage(message, chat, JABBERID, chatID);
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

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload((byte[]) mediaContent, mimeType, chat, messageId);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            String mediaFileName = (String) messageContent;

            long messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, "", true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, null);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload(mediaFileName, mimeType, chat, messageId);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            String stickerAssetPath = (String) messageContent;
            personalMessaging.sendStickerMessage(stickerAssetPath, chat, JABBERID, chatID);

        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {

            String mediaFileName = (String) messageContent;

            mediaMap.put(mediaFileName, (byte[]) mediaContent);

            long messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, "", true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, null);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload((byte[]) mediaContent, mimeType, chat, messageId);

        }
    }

    public void openCamera(View view) {
        chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
    }

    public void emojipopup(View view) {
        chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
    }

    public void galleryPopup(View view) {
        chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
    }

    public void voicePopup(View view) {
        chatKeyboardHelper.tapOnTab(JABBERNAME, view, this);
    }

    public void openKeyBoard(View view) {
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
        if (toolbarActionsForChatScreen.getSelectionflag()) {
            mtoolbar.findViewById(R.id.profile).setVisibility(View.GONE);
            MenuItem deleteMessage = menu.findItem(R.id.delete);
            deleteMessage.setVisible(true);
            MenuItem copyMessage = menu.findItem(R.id.copy);
            copyMessage.setVisible(true);
            MenuItem forwardMessage = menu.findItem(R.id.forward);
            forwardMessage.setVisible(true);
            MenuItem searchMessages = menu.findItem(R.id.action_search);
            searchMessages.setVisible(false);
            MenuItem blockUser = menu.findItem(R.id.action_block_user);
            blockUser.setVisible(false);
            MenuItem viewContact = menu.findItem(R.id.action_view_contact);
            viewContact.setVisible(false);
            MenuItem clearChat = menu.findItem(R.id.action_clear_chat);
            clearChat.setVisible(false);
            TextView noOfSelectedItems = (TextView) mtoolbar.findViewById(R.id.selectedItems);
            noOfSelectedItems.setVisibility(View.VISIBLE);
            noOfSelectedItems.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
            noOfSelectedItems.setText(String.valueOf(toolbarActionsForChatScreen.getSelecteditems()));
            if (toolbarActionsForChatScreen.getMediaSelectionflag()) {
                copyMessage.setVisible(false);
            }

        } else {
            mtoolbar.findViewById(R.id.selectedItems).setVisibility(View.GONE);
            MenuItem deleteMessage = menu.findItem(R.id.delete);
            deleteMessage.setVisible(false);
            MenuItem copyMessage = menu.findItem(R.id.copy);
            copyMessage.setVisible(false);
            MenuItem forwardMessage = menu.findItem(R.id.forward);
            forwardMessage.setVisible(false);
            mtoolbar.findViewById(R.id.profile).setVisibility(View.VISIBLE);
        }

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

        blockUnblockUserHelper.initViewBasedOnBlockStatus(menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        setCustomSearchViewbutton(searchView);
        setSubmitAreaNull(searchView);
        addCustomButtonsToSearchView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                positions.clear();
                positions = chatScreenAdapter.filterSearchQuery(newText);
                return false;
            }
        });
        return true;
    }

    private void addCustomButtonsToSearchView(SearchView searchView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageButton up = new ImageButton(getApplicationContext());
        up.setImageResource(R.drawable.ic_up_arrow);
        up.setLayoutParams(params);
        up.setBackgroundColor(Color.TRANSPARENT);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "up", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton down = new ImageButton(getApplicationContext());
        down.setImageResource(R.drawable.ic_down_arrow);
        down.setLayoutParams(params);
        down.setBackgroundColor(Color.TRANSPARENT);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "down", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout linearLayoutOfSearchView = (LinearLayout) searchView.findViewById(R.id.submit_area);
        linearLayoutOfSearchView.addView(up);
        linearLayoutOfSearchView.addView(down);

    }

    private void setSubmitAreaNull(SearchView searchView) {
//        int submit_areaId = searchView.getContext().getResources().getIdentifier("android:id/submit_area", null, null);
        ImageView mCloseButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        mCloseButton.setEnabled(false);
        mCloseButton.setImageAlpha(00);
        ImageView submitImage = (ImageView) searchView.findViewById(R.id.search_go_btn);
        searchView.setSubmitButtonEnabled(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
        submitImage.setLayoutParams(layoutParams);
    }

    private void setCustomSearchViewbutton(SearchView searchView) {
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_menu_search);
        searchView.setQueryHint("Search...");
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
            sportsUnityDBHelper.clearChat(chatID, groupServerId);

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
        } else if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void resetList() {
//        selectedFlag = false;
//        selecteditems = 0;
//        selectedItemsList.clear();
//
//
//        for (int i = 0; i < mChatView.getChildCount();
//             i++) {
//            View v = mChatView.getChildAt(i);
//            v.setBackgroundColor(Color.TRANSPARENT);
//        }
//        invalidateOptionsMenu();
//    }

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

}
