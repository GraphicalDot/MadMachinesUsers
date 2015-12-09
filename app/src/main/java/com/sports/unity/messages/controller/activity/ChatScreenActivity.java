package com.sports.unity.messages.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.sports.unity.messages.controller.BlockUnblockUserHelper;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.GroupMessaging;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.messages.controller.model.PubSubMessaging;
import com.sports.unity.messages.controller.viewhelper.ChatKeyboardHelper;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.FileOnCloudHandler;
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
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreenActivity extends CustomAppCompatActivity {

    private static ArrayList<Message> messageList;
    private static ChatScreenAdapter chatScreenAdapter;
    private static GroupChatScreenAdapter groupChatScreenAdapter;

    private static String JABBERID;
    private static String JABBERNAME;
    private static byte[] userImageBytes;

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
                handleSendingMediaContent(mimeType, messageContent, mediaContent);
            } else if (id == 2) {
                mediaMap.put((String) messageContent, (byte[]) mediaContent);
            } else if (id == 3) {
                FileOnCloudHandler.getInstance(getBaseContext()).requestForDownload((String) messageContent, mimeType, (Long) mediaContent);
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
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        ChatScreenApplication.activityPaused();
        super.onPause();
    }

    @Override
    public void onStop() {
        ChatScreenApplication.activityStopped();
        super.onStop();
        ActivityActionHandler.getInstance().removeActionListener(ActivityActionHandler.CHAT_SCREEN_KEY);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityActionHandler.getInstance().addActionListener(ActivityActionHandler.CHAT_SCREEN_KEY, activityActionListener);
        ChatScreenApplication.activityResumed();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

        /**
         * Adding custom font to views
         */
        mSend.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                sendMessage();
            }
        });

        CommonUtil.dismissNotification(getBaseContext());
    }


    private void populateMessagesOnScreen() {
        ListView mChatView = (ListView) findViewById(R.id.msgview);               // List for messages
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

        loadAllMediaContent();
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

    private void sendMessage() {
        if (blockUnblockUserHelper.isBlockStatus()) {
            blockUnblockUserHelper.showAlert_ToSendMessage_UnblockUser(this, contactID, JABBERID, menu);
            return;
        }

        createChatEntryifNotExists();

        if (messageText.getText().toString().equals("")) {
            //Do nothing
        } else {
            Log.i("Message Entry", "adding message chat " + chatID);

            if (isGroupChat) {
//                groupMessaging.sendMessageToGroup(messageText.getText().toString(), multiUserChat, chatID, groupServerId, TinyDB.getInstance(this).getString(TinyDB.KEY_USERNAME));
                PubSubMessaging pubSubMessaging = PubSubMessaging.getInstance(this);
                boolean success = pubSubMessaging.publishMessage(messageText.getText().toString(), chatID, groupServerId, this);
                if (success) {
                    messageList = sportsUnityDBHelper.getMessages(chatID);
                    groupChatScreenAdapter.notifydataset(messageList);
                }
            } else {
                personalMessaging.sendTextMessage(messageText.getText().toString(), chat, JABBERID, chatID);
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
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            String stickerAssetPath = (String) messageContent;
            personalMessaging.sendStickerMessage(stickerAssetPath, chat, JABBERID, chatID);

        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {

            String mediaFileName = (String) messageContent;

            long messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, "", true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, null);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload((byte[]) mediaContent, mimeType, chat, messageId);

        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            //TODO
        }
    }

    public void openCamera(View view) {
        chatKeyboardHelper.tapOnTab(view, this);
    }

    public void emojipopup(View view) {
        chatKeyboardHelper.tapOnTab(view, this);
    }

    public void galleryPopup(View view) {
        chatKeyboardHelper.tapOnTab(view, this);
    }

    public void voicePopup(View view) {
        chatKeyboardHelper.tapOnTab(view, this);
    }

    public void openKeyBoard(View view) {
        chatKeyboardHelper.tapOnTab(view, this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            Uri chosenImageUri = data.getData();
//            //File imageFile = new File(getRealPathFromURI(chosenImageUri));
//
//
//            Bitmap mBitmap = null;
//            try {
//                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);
//                Log.i("getBitmap? :", "yes");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            //      convertTobyteArray(mBitmap, imageFile);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_screen, menu);

        this.menu = menu;

        blockUnblockUserHelper.initViewBasedOnBlockStatus(menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_menu_search_blk);
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
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
            sportsUnityDBHelper.clearChat(chatID, groupServerId);
            messageList = sportsUnityDBHelper.getMessages(chatID);
            chatScreenAdapter.notifydataset(messageList);
        } else if (id == R.id.action_search) {
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

    private void loadAllMediaContent() {
        new ThreadTask(messageList) {

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

                return null;
            }

            @Override
            public void postAction(Object object) {
                sendActionToCorrespondingActivityListener();
            }

        }.start();
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

}
