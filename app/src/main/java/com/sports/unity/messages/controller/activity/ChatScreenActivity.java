package com.sports.unity.messages.controller.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.TextView;
import android.widget.Toast;

import com.sports.unity.Database.DBUtil;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPConnectionUtil;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.UserProfileActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.PermissionUtil;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.BlockUnblockUserHelper;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.messages.controller.model.PubSubMessaging;
import com.sports.unity.messages.controller.model.ShareableData;
import com.sports.unity.messages.controller.model.ToolbarActionsForChatScreen;
import com.sports.unity.messages.controller.viewhelper.AudioRecordingHelper;
import com.sports.unity.messages.controller.viewhelper.ChatKeyboardHelper;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;
import com.sports.unity.util.AlertDialogUtil;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.FileOnCloudHandler;
import com.sports.unity.util.GlobalEventHandler;
import com.sports.unity.util.ImageUtil;
import com.sports.unity.util.NotificationHandler;
import com.sports.unity.util.ThreadTask;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.chatstates.ChatState;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ChatScreenActivity extends CustomAppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, BlockUnblockUserHelper.BlockUnblockListener {

    public static final String INTENT_KEY_JID = "jid";
    public static final String INTENT_KEY_NAME = "name";
    public static final String INTENT_KEY_CHAT_ID = "chatId";
    public static final String INTENT_KEY_IMAGE = "image";
    public static final String INTENT_KEY_BLOCK_STATUS = "blockStatus";
    public static final String INTENT_KEY_NEARBY_CHAT = "nearbyChat";
    public static final String INTENT_KEY_CONTACT_AVAILABLE_STATUS = "contactAvailableStatus";
    public static final String INTENT_KEY_USER_STATUS = "chatStatus";
    public static final String INTENT_KEY_IS_GROUP_CHAT = "isGroupChat";

    public static Intent createChatScreenIntent(Context context, boolean isGroupChat, String jid, String name, int chatId, byte[] image, Boolean blockStatus, boolean othersChat, int contactAvailableStatus, String chatStatus) {
        Intent intent = new Intent(context, ChatScreenActivity.class);
        intent.putExtra(INTENT_KEY_JID, jid);
        intent.putExtra(INTENT_KEY_NAME, name);
        intent.putExtra(INTENT_KEY_CHAT_ID, chatId);
        intent.putExtra(INTENT_KEY_IMAGE, image);
        intent.putExtra(INTENT_KEY_BLOCK_STATUS, blockStatus);
        intent.putExtra(INTENT_KEY_NEARBY_CHAT, othersChat);
        intent.putExtra(INTENT_KEY_CONTACT_AVAILABLE_STATUS, contactAvailableStatus);
        intent.putExtra(INTENT_KEY_USER_STATUS, chatStatus);
        intent.putExtra(INTENT_KEY_IS_GROUP_CHAT, isGroupChat);
        return intent;
    }

    public static void viewProfile(Activity activity, boolean isGroupChat, int chatId, byte[] profilePicture, String name, String jid, String chatStatus, boolean otherChat, int contactAvailableStatus, boolean blockStatus) {
        if (!isGroupChat) {
            Intent intent = new Intent(activity, UserProfileActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("profilePicture", profilePicture);
            intent.putExtra("jid", jid);
            intent.putExtra("status", chatStatus);
            intent.putExtra("otherChat", otherChat);
            intent.putExtra(INTENT_KEY_CONTACT_AVAILABLE_STATUS, contactAvailableStatus);
            activity.startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_PROFILE);
        } else {
            Intent intent = new Intent(activity, GroupDetailActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("profilePicture", profilePicture);
            intent.putExtra("jid", jid);
            intent.putExtra("blockStatus", blockStatus);
            intent.putExtra("chatID", chatId);
            activity.startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_PROFILE);
        }
    }

    private boolean isGroupChat = false;
    private int chatID = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
    private byte[] userImageBytes;

    private String jabberId;
    private String jabberName;

    private ArrayList<Message> messageList;
    private ChatScreenAdapter chatScreenAdapter;
    private int availableStatus = Contacts.AVAILABLE_BY_MY_CONTACTS;
    private StickyListHeadersListView mChatView;
    private boolean otherChat = false;
    private boolean isLastTimeRequired = false;
    private ToolbarActionsForChatScreen toolbarActionsForChatScreen = null;

    private EditText messageText;
    private Chat chat;
    private TextView status;
    private ImageView back;
    private boolean blockStatus;

    private ViewGroup parentLayout;
    private CircleImageView userPic;
    private Button mSend;

    private Menu menu = null;

    private Handler removeViewHandler;
    private TextView friendRequestStatus;
    private LinearLayout addBlockLayout;

    private SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);
    private BlockUnblockUserHelper blockUnblockUserHelper = null;
    private ChatKeyboardHelper chatKeyboardHelper = null;

    private PersonalMessaging personalMessaging = PersonalMessaging.getInstance(this);
    private PubSubMessaging pubSubMessaging = PubSubMessaging.getInstance();

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
                                personalMessaging.getLastTime(jabberId);
                            } else {
                                status.setText("last seen " + object.toString());
                                isLastTimeRequired = false;
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
            } else if (id == ActivityActionHandler.EVENT_FRIEND_REQUEST_SENT
                    || id == ActivityActionHandler.EVENT_FRIEND_REQUEST_RECEIVED
                    || id == ActivityActionHandler.EVENT_FRIEND_REQUEST_ACCEPTED) {
                ChatScreenActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        friendRequestStatus.setVisibility(View.VISIBLE);
                        addBlockLayout.setVisibility(View.GONE);

                        friendRequestStatus.setText((CharSequence) object);
                        long milliseconds = 5000;
                        removeRequestStatusFromWindow(milliseconds);
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
            } else if (id == ActivityActionHandler.EVENT_ID_INCOMING_MEDIA) {
                //handle incoming media message
                if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE) && UserUtil.isMediaAutoDownloadEnabled(getApplicationContext(), UserUtil.IMAGE_MEDIA)) {
                    FileOnCloudHandler.getInstance(getBaseContext()).requestForDownload((String) messageContent, mimeType, (Integer) mediaContent, jabberId);
                } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO) && UserUtil.isMediaAutoDownloadEnabled(getApplicationContext(), UserUtil.AUDIO_MEDIA)) {
                    FileOnCloudHandler.getInstance(getBaseContext()).requestForDownload((String) messageContent, mimeType, (Integer) mediaContent, jabberId);
                } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO) && UserUtil.isMediaAutoDownloadEnabled(getApplicationContext(), UserUtil.VIDEO_MEDIA)) {
                    FileOnCloudHandler.getInstance(getBaseContext()).requestForDownload((String) messageContent, mimeType, (Integer) mediaContent, jabberId);
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

    private void removeRequestStatusFromWindow(long milliseconds) {
        if (removeViewHandler != null) {
            removeViewHandler.removeCallbacks(postDelayedRunnableToRemoveViewFromWindow);
            removeViewHandler.postDelayed(postDelayedRunnableToRemoveViewFromWindow, milliseconds);
        } else {
            removeViewHandler = new Handler();
            removeViewHandler.postDelayed(postDelayedRunnableToRemoveViewFromWindow, milliseconds);
        }
    }

    Runnable postDelayedRunnableToRemoveViewFromWindow = new Runnable() {
        @Override
        public void run() {
            friendRequestStatus.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onDestroy() {
        AudioRecordingHelper.cleanUp();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        blockUnblockUserHelper.removeBlockUnblockListener();
        AudioRecordingHelper.getInstance(this).stopAndReleaseMediaPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();

        ActivityActionHandler.getInstance().removeActionListener(ActivityActionHandler.CHAT_SCREEN_KEY, jabberId);
        GlobalEventHandler.getInstance().removeGlobalEventListener(ActivityActionHandler.CHAT_SCREEN_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        populateMessagesOnScreen();
        clearUnreadCount();

        checkAndUpdateBlockStatus();
        blockUnblockUserHelper.addBlockUnblockListener(ChatScreenActivity.this);
        ActivityActionHandler.getInstance().addActionListener(ActivityActionHandler.CHAT_SCREEN_KEY, jabberId, activityActionListener);
//        GlobalEventHandler.getInstance().addGlobalEventListener(ActivityActionHandler.CHAT_SCREEN_KEY, this);

//        XMPPConnectionUtil.getInstance().addConnectionListener(XMPP_CONNECTION_KEY, this);

        NotificationHandler.dismissNotification(getBaseContext());
        NotificationHandler.getInstance(getApplicationContext()).clearNotificationMessages(String.valueOf(chatID));

//        initAddBlockView();
    }

    private void checkAndUpdateBlockStatus() {
        Contacts contact = SportsUnityDBHelper.getInstance(getApplicationContext()).getContactByJid(jabberId);
        blockStatus = SportsUnityDBHelper.getInstance(getApplicationContext()).isChatBlocked(contact.id);
        if (blockStatus) {
            if (isGroupChat) {
                LinearLayout mediaButtonsLayout = (LinearLayout) findViewById(R.id.send_media_action_buttons);
                mediaButtonsLayout.setVisibility(View.GONE);
                mSend.setVisibility(View.GONE);
                messageText.setVisibility(View.GONE);
                TextView groupExitMessage = (TextView) findViewById(R.id.group_exit_text);
                groupExitMessage.setVisibility(View.VISIBLE);
            } else {
                status.setVisibility(View.GONE);
                LinearLayout messagecomposeLayout = (LinearLayout) findViewById(R.id.type_msg);
                messagecomposeLayout.setClickable(true);
                messagecomposeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayAlertToUnblockUser();
                    }
                });
            }
        } else {
            status.setVisibility(View.VISIBLE);
        }
        if (blockUnblockUserHelper == null) {
            blockUnblockUserHelper = new BlockUnblockUserHelper(blockStatus, this, status);
        } else {
            blockUnblockUserHelper.updateBlockStatus(blockStatus);
        }
        chatKeyboardHelper.disableOrEnableKeyboardAndMediaButtons(blockUnblockUserHelper.isBlockStatus(), this);
        initAddBlockView();
        invalidateOptionsMenu();
//        disableChatIfUserBlocked();

//        blockStatus = getIntent().getBooleanExtra("blockStatus", false);
//        if (isGroupChat) {
//            if (blockStatus) {
//                LinearLayout mediaButtonsLayout = (LinearLayout) findViewById(R.id.send_media_action_buttons);
//                mediaButtonsLayout.setVisibility(View.GONE);
//                mSend.setVisibility(View.GONE);
//                messageText.setVisibility(View.GONE);
//                TextView groupExitMessage = (TextView) findViewById(R.id.group_exit_text);
//                groupExitMessage.setVisibility(View.VISIBLE);
//            } else {
//                //do nothing
//            }
//            blockUnblockUserHelper = new BlockUnblockUserHelper(blockStatus, this, status);
//        } else {
//            if (blockStatus) {
//                status.setVisibility(View.GONE);
//            } else {
//                status.setVisibility(View.VISIBLE);
//            }
//            blockUnblockUserHelper = new BlockUnblockUserHelper(blockStatus, this, status);
//            disableChatIfUserBlocked();
    }


    @Override
    protected void onStart() {
        super.onStart();

        XMPPConnectionUtil.getInstance().requestConnection(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        if (toolbarActionsForChatScreen.getSelecteditems() != 0) {
            toolbarActionsForChatScreen.resetList(mChatView);
            invalidateOptionsMenu();
        } else if (toolbarActionsForChatScreen.getSearchFlag()) {
            toolbarActionsForChatScreen.setSearchFlag(false);
            LinearLayout sendContentLayout = (LinearLayout) findViewById(R.id.type_msg);
            sendContentLayout.setVisibility(View.VISIBLE);
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

        mSend = (Button) findViewById(R.id.send);
        mSend.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());

        friendRequestStatus = (TextView) findViewById(R.id.request_status);
        addBlockLayout = (LinearLayout) findViewById(R.id.add_block_layout);

        getIntentExtras();

        boolean isPending = SportsUnityDBHelper.getInstance(this).isRequestPending(jabberId);
        initToolbar();
//        hideStatusIfUserBlocked();
        final Handler mHandler = new Handler();

        populateMessagesOnScreen();

        setStatusConnecting();

        setEventListeners(mHandler);

        toolbarActionsForChatScreen.resetVariables();
    }

    private void initAddBlockView() {
        final TextView requestStatus = (TextView) findViewById(R.id.request_status);
        if ((availableStatus == Contacts.AVAILABLE_BY_OTHER_CONTACTS || availableStatus == Contacts.AVAILABLE_BY_PEOPLE_AROUND_ME) && !blockUnblockUserHelper.isBlockStatus()) {
            int requestId = sportsUnityDBHelper.checkJidForPendingRequest(jabberId);
            if (requestId == Contacts.WAITING_FOR_REQUEST_ACCEPTANCE) {
                addBlockLayout.setVisibility(View.GONE);
                requestStatus.setVisibility(View.VISIBLE);
                requestStatus.setText(R.string.request_sent);
            } else if (requestId == Contacts.PENDING_REQUESTS_TO_PROCESS) {
                addBlockLayout.setVisibility(View.GONE);
                requestStatus.setVisibility(View.VISIBLE);
                requestStatus.setText(R.string.request_pending);
            } else if (requestId == Contacts.REQUEST_ACCEPTED) {
                addBlockLayout.setVisibility(View.GONE);
                requestStatus.setVisibility(View.GONE);
            } else {
                addBlockLayout.setVisibility(View.VISIBLE);
                TextView addFriend = (TextView) findViewById(R.id.add_friend);
                addFriend.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
                addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                            boolean success = personalMessaging.sendFriendRequest(jabberId);
                            if (success) {
                                addBlockLayout.setVisibility(View.GONE);
                                requestStatus.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getApplicationContext(), "something went wrong ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.conn_not_authenticated, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                final TextView blockUser = (TextView) findViewById(R.id.block_user);
                blockUser.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
                blockUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        blockUnblockUserHelper.onMenuItemSelected(ChatScreenActivity.this, chatID, jabberId, menu);
                    }
                });
            }
        } else

        {
            addBlockLayout.setVisibility(View.GONE);
        }

    }

    private void hideStatusIfUserBlocked() {
        blockStatus = getIntent().getBooleanExtra("blockStatus", false);
        if (isGroupChat) {
            if (blockStatus) {
                LinearLayout mediaButtonsLayout = (LinearLayout) findViewById(R.id.send_media_action_buttons);
                mediaButtonsLayout.setVisibility(View.GONE);
                mSend.setVisibility(View.GONE);
                messageText.setVisibility(View.GONE);
                TextView groupExitMessage = (TextView) findViewById(R.id.group_exit_text);
                groupExitMessage.setVisibility(View.VISIBLE);
            } else {
                //do nothing
            }
            blockUnblockUserHelper = new BlockUnblockUserHelper(blockStatus, this, status);
        } else {
            if (blockStatus) {
                status.setVisibility(View.GONE);
            } else {
                status.setVisibility(View.VISIBLE);
            }
            blockUnblockUserHelper = new BlockUnblockUserHelper(blockStatus, this, status);
            disableChatIfUserBlocked();
        }

    }

    private void checkForwardMessageQueue() {
        ArrayList<ShareableData> messageList = getIntent().getParcelableArrayListExtra(Constants.INTENT_FORWARD_SELECTED_IDS);
        if (messageList != null) {
            ForwardMessages(messageList);
        }
    }

    final Runnable userStoppedTyping = new Runnable() {

        @Override
        public void run() {
            if (!isGroupChat && chat != null) {
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
                        //TODO sending user texting status,
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isGroupChat) {
                    handler.postDelayed(userStoppedTyping, 2000);
                } else {
                    //TODO sending user texting status,
                }
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
        if (isGroupChat == false) {
            ChatManager chatManager = ChatManager.getInstanceFor(XMPPClient.getConnection());
            chat = chatManager.getThreadChat(jabberId + "@mm.io");
            if (chat == null) {
                chat = chatManager.createChat(jabberId + "@mm.io");
            }
        } else {
            pubSubMessaging.initGroupChat(jabberId);
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
                int contactStatus = ChatScreenActivity.this.getIntent().getIntExtra(INTENT_KEY_CONTACT_AVAILABLE_STATUS, Contacts.AVAILABLE_NOT);
                String status = ChatScreenActivity.this.getIntent().getStringExtra(INTENT_KEY_USER_STATUS);
                viewProfile(ChatScreenActivity.this, isGroupChat, chatID, userImageBytes, jabberName, jabberId, status, otherChat, contactStatus, blockStatus);
            }
        });

        initUI(toolbar);
    }

    private void disableChatIfUserBlocked() {
//        if (blockUnblockUserHelper.isBlockStatus()) {
//        }
        chatKeyboardHelper.disableOrEnableKeyboardAndMediaButtons(blockUnblockUserHelper.isBlockStatus(), this);

//        LinearLayout messagecomposeLayout = (LinearLayout) findViewById(R.id.type_msg);
//        messagecomposeLayout.setClickable(true);
//        messagecomposeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("layoutclicked", "true");
//                if (blockUnblockUserHelper.isBlockStatus()) {
//                    displayAlertToUnblockUser();
//                } else {
//                    //nothing
//                }
//            }
//        });
    }

    /**
     * For blocked users show message "user blocked. Tap here to unblock" and do not show his/her last seen and status
     */

    private void displayAlertToUnblockUser() {
        AlertDialog.Builder build = new AlertDialog.Builder(
                ChatScreenActivity.this);
        build.setMessage(
                "Unblock " + jabberName + " To send a message? ");
        build.setPositiveButton("unblock",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        blockUnblockUserHelper.onMenuItemSelected(ChatScreenActivity.this, chatID, jabberId, menu);
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


    private void ForwardMessages(ArrayList<ShareableData> messageList) {

        for (ShareableData parcelableData : messageList) {
            if (parcelableData.mimeType.equals(sportsUnityDBHelper.MIME_TYPE_TEXT)) {
                sendMessage(parcelableData.textData);
            } else if (parcelableData.mimeType.equals(sportsUnityDBHelper.MIME_TYPE_STICKER)) {
                handleSendingMediaContent(parcelableData.mimeType, parcelableData.textData, null, null);
            } else if (parcelableData.mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
                if (parcelableData.pathOrFileNameForMedia != null) {
                    handleSendingMediaContent(parcelableData.mimeType, parcelableData.pathOrFileNameForMedia, null, null);
                }
            } else {
                if (parcelableData.pathOrFileNameForMedia != null) {
                    String thumbnailImage = null;
                    thumbnailImage = PersonalMessaging.createThumbnailImageAsBase64(getApplicationContext(),
                            parcelableData.mimeType, parcelableData.pathOrFileNameForMedia);
                    handleSendingMediaContent(parcelableData.mimeType, parcelableData.pathOrFileNameForMedia, null, thumbnailImage);
                }
            }
        }
        updateMessageList();

        int filesNotSent = getIntent().getIntExtra(ForwardSelectedItems.KEY_FILES_NOT_SENT, 0);
        if (filesNotSent > 0) {
            Toast.makeText(getApplicationContext(), getResources().getQuantityString(R.plurals.file_count, filesNotSent, filesNotSent), Toast.LENGTH_LONG).show();
        }
        messageList.clear();
        messageList = null;
    }

    private void populateMessagesOnScreen() {
        mChatView = (StickyListHeadersListView) findViewById(R.id.msgview);               // List for messages
        if (isGroupChat) {
            setGroupMembers();
        }

        messageList = sportsUnityDBHelper.getMessages(chatID);
        chatScreenAdapter = new ChatScreenAdapter(ChatScreenActivity.this, messageList, otherChat, isGroupChat, jabberId);
        mChatView.setAdapter(chatScreenAdapter);

//        loadAllMediaContent(messageList, null);

        sendReadStatus();
    }

    private void setGroupMembers() {
        String s = "";
        SportsUnityDBHelper.GroupParticipants participants = sportsUnityDBHelper.getGroupParticipants(chatID);
        ArrayList<Contacts> users = participants.usersInGroup;
        if (users != null && users.size() > 1) {
            Contacts contacts = users.get(0);
            s += contacts.getName();
            for (int index = 1; index < users.size(); index++) {
                s += ", ";
                s += users.get(index).getName();
            }
        }
        status.setText(s);
    }


    private void clearUnreadCount() {
        if (chatID != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            sportsUnityDBHelper.clearUnreadCount(chatID, jabberId);
        }
    }

    private void updateMessageList() {
        Message oldLastMessage = null;
        if (messageList.size() > 0) {
            oldLastMessage = messageList.get(messageList.size() - 1);
        }

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
        user.setText(jabberName);
        user.setTypeface(FontTypeface.getInstance(this).getRobotoRegular());

        userPic = (CircleImageView) toolbar.findViewById(R.id.user_picture);
        if (isGroupChat) {
            if (userImageBytes == null) {
                userPic.setImageResource(R.drawable.ic_group);
            } else {
                userPic.setImageBitmap(BitmapFactory.decodeByteArray(userImageBytes, 0, userImageBytes.length));
            }

        } else if (userImageBytes != null) {
            if (userImageBytes.length > 0) {
                userPic.setImageBitmap(BitmapFactory.decodeByteArray(userImageBytes, 0, userImageBytes.length));
            } else {
                userPic.setImageResource(R.drawable.ic_user);
            }
        } else {
            userPic.setImageResource(R.drawable.ic_user);
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
        isGroupChat = getIntent().getBooleanExtra(INTENT_KEY_IS_GROUP_CHAT, false);

        jabberId = getIntent().getStringExtra(INTENT_KEY_JID);
        jabberName = getIntent().getStringExtra(INTENT_KEY_NAME); //TODO ?

        chatID = getIntent().getIntExtra(INTENT_KEY_CHAT_ID, SportsUnityDBHelper.DEFAULT_ENTRY_ID);
        userImageBytes = getIntent().getByteArrayExtra(INTENT_KEY_IMAGE);
        otherChat = getIntent().getBooleanExtra(INTENT_KEY_NEARBY_CHAT, false);
        availableStatus = getIntent().getIntExtra(INTENT_KEY_CONTACT_AVAILABLE_STATUS, Contacts.AVAILABLE_BY_MY_CONTACTS);

        if (!isGroupChat) {
            if (XMPPClient.getInstance().isConnectionAuthenticated()) {
//                createRosterEntry();
            } else {
                //nothing
            }
        } else {
            //nothing
        }
    }

    private void getLastSeen() throws SmackException.NotConnectedException {

        if (XMPPClient.getConnection() != null) {
            Roster roster = Roster.getInstanceFor(XMPPClient.getConnection());
            Presence availability = roster.getPresence(jabberId + "@mm.io");
            int state = retrieveState_mode(availability.getStatus());
            if (state == 1) {
                status.post(new Runnable() {
                    @Override
                    public void run() {
                        status.setText("Online");
                    }
                });
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
            isLastTimeRequired = true;
            return userState;
        }
    }

    private void sendMessage(String message) {
        if (message.equals("") || message == null) {
            //Do nothing
        } else {
            if (isGroupChat) {
                pubSubMessaging.sendTextMessage(getApplicationContext(), message, chatID, jabberId);
            } else {
                personalMessaging.sendTextMessage(message, chat, jabberId, chatID, otherChat);
                personalMessaging.sendStatus(ChatState.paused, chat);
            }
            messageList = sportsUnityDBHelper.getMessages(chatID);
            chatScreenAdapter.notifydataset(messageList);
        }
        messageText.setText("");
    }

    private void handleSendingMediaContent(String mimeType, Object messageContent, Object mediaContent, String thumbnailImage) {
        if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            String mediaFileName = (String) messageContent;

            byte[] bytesOfThumbnail = null;
            if (thumbnailImage != null) {
                bytesOfThumbnail = Base64.decode(thumbnailImage, Base64.DEFAULT);
            }

            int messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, jabberId, true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload(mediaFileName, thumbnailImage, mimeType, chat, messageId, otherChat, isGroupChat, jabberId);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            String mediaFileName = (String) messageContent;

            byte[] bytesOfThumbnail = null;
            if (thumbnailImage != null) {
                bytesOfThumbnail = Base64.decode(thumbnailImage, Base64.DEFAULT);
            }

            int messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, jabberId, true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload(mediaFileName, thumbnailImage, mimeType, chat, messageId, otherChat, isGroupChat, jabberId);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            String stickerAssetPath = (String) messageContent;
            if (!this.isGroupChat) {
                personalMessaging.sendStickerMessage(stickerAssetPath, chat, jabberId, chatID, otherChat);
            } else {
                PubSubMessaging.getInstance().sendStickerMessage(getApplicationContext(), stickerAssetPath, chatID, jabberId);
            }
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
            String mediaFileName = (String) messageContent;

            int messageId = sportsUnityDBHelper.addMediaMessage("", mimeType, jabberId, true, String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch()),
                    null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS, mediaFileName, null);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID);

            FileOnCloudHandler.getInstance(getBaseContext()).requestForUpload(mediaFileName, null, mimeType, chat, messageId, otherChat, isGroupChat, jabberId);
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
            chatKeyboardHelper.tapOnTab(jabberName, view, this);
        } else {
            if (PermissionUtil.getInstance().requestPermission(ChatScreenActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.camera_permission_message), Constants.REQUEST_CODE_CAMERA_PERMISSION)) {

                chatKeyboardHelper.tapOnTab(jabberName, view, this);
            }
        }
    }

    public void emojipopup(View view) {
        resetToolbar();
        chatKeyboardHelper.tapOnTab(jabberName, view, this);
    }

    public void galleryPopup(View view) {
        resetToolbar();
        if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
            chatKeyboardHelper.tapOnTab(jabberName, view, this);
        } else {
            if (PermissionUtil.getInstance().requestPermission(ChatScreenActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.gallery_permission_message), Constants.REQUEST_CODE_GALLERY_STORAGE_PERMISSION)) {

                chatKeyboardHelper.tapOnTab(jabberName, view, this);
            }
        }
    }

    public void voicePopup(View view) {
        resetToolbar();
        if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
            chatKeyboardHelper.tapOnTab(jabberName, view, this);
        } else {
            if (PermissionUtil.getInstance().requestPermission(ChatScreenActivity.this, new ArrayList<String>(Arrays.asList(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)), getResources().getString(R.string.audio_permission_message), Constants.REQUEST_CODE_RECORD_AUDIO_PERMISSION)) {
                chatKeyboardHelper.tapOnTab(jabberName, view, this);
            }
        }
    }

    public void openKeyBoard(View view) {
        resetToolbar();
        chatKeyboardHelper.tapOnTab(jabberName, view, this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == 333) {
                //TODO handle forward
            } else if (requestCode == Constants.REQUEST_CODE_VIEW_PROFILE) {
                userImageBytes = data.getByteArrayExtra(ChatScreenActivity.INTENT_KEY_IMAGE);
                if (isGroupChat) {
                    if (userImageBytes == null) {
                        userPic.setImageResource(R.drawable.ic_group);
                    } else {
                        userPic.setImageBitmap(BitmapFactory.decodeByteArray(userImageBytes, 0, userImageBytes.length));
                    }

                } else if (userImageBytes != null) {
                    userPic.setImageBitmap(BitmapFactory.decodeByteArray(userImageBytes, 0, userImageBytes.length));
                } else {
                    userPic.setImageResource(R.drawable.ic_user);
                }
            } else if (requestCode == Constants.REQUEST_CODE_PICK_IMAGE) {
                handleResultForSendMedia(data);
            }
        }
    }

    private void handleResultForSendMedia(Intent data) {
        if (data.getClipData() == null) {
            handleSingleMediaFile(data);
        } else {
            handleMultipleMediaFiles(data);
        }

    }

    private void handleSingleMediaFile(Intent data) {
        int filesNotSent = 0;
        final Uri URI = data.getData();
        long filesize = ImageUtil.getFileSize(getApplicationContext(), URI, URI.getScheme());
        String path = null;
        String mimeType = ImageUtil.getMimeType(ChatScreenActivity.this, URI);
        if (mimeType.contains("image")) {
            if (filesize > 5120 && filesize <= 10485760) {
                path = ImageUtil.getPathforURI(getApplicationContext(), URI, MediaStore.Images.Media.DATA);
                sendMediaFile(URI, path);
            } else {
                filesNotSent++;
            }
        } else if (mimeType.contains("video")) {
            if (filesize > 51200 && filesize <= 10485760) {
                path = ImageUtil.getPathforURI(getApplicationContext(), URI, MediaStore.Video.Media.DATA);
                sendMediaFile(URI, path);
            } else {
                filesNotSent++;
            }
        }
        if (filesNotSent > 0) {
            Toast.makeText(ChatScreenActivity.this, "Sorry file not sent! File size was too large", Toast.LENGTH_LONG).show();
        }
    }

    private void handleMultipleMediaFiles(Intent data) {
        int filesNotSent = 0;
        if (data.getClipData() != null) {
            ClipData mClipData = data.getClipData();
            ArrayList<Uri> URIs = new ArrayList<Uri>();
            for (int i = 0; i < mClipData.getItemCount(); i++) {
                ClipData.Item item = mClipData.getItemAt(i);
                Uri uri = item.getUri();
                URIs.add(uri);
            }
            if (URIs != null) {
                if (URIs.size() > 10) {

                    Toast.makeText(getApplicationContext(), "Can't share more than 10 items", Toast.LENGTH_LONG).show();
                    this.finish();

                } else {
                    for (Uri URI : URIs) {

                        long filesize = ImageUtil.getFileSize(getApplicationContext(), URI, URI.getScheme());
                        String path = null;
                        String mimeType = ImageUtil.getMimeType(ChatScreenActivity.this, URI);
                        if (mimeType.contains("image")) {
                            if (filesize > 5120 && filesize <= 10485760) {
                                path = ImageUtil.getPathforURI(getApplicationContext(), URI, MediaStore.Images.Media.DATA);
                                sendMediaFile(URI, path);
                            } else {
                                filesNotSent++;
                            }
                        } else if (mimeType.contains("video")) {
                            if (filesize > 51200 && filesize <= 10485760) {
                                path = ImageUtil.getPathforURI(getApplicationContext(), URI, MediaStore.Video.Media.DATA);
                                sendMediaFile(URI, path);
                            } else {
                                filesNotSent++;
                            }
                        }
                    }

                    if (filesNotSent > 0) {
                        Toast.makeText(ChatScreenActivity.this, "Sorry " + filesNotSent + " were not sent ! File size was too large", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void sendMediaFile(final Uri URI, final String file) {
        try {
            new ThreadTask(null) {

                private String thumbnailImage = null;
                private boolean hasVideoContent = false;

                @Override
                public Object process() {
                    hasVideoContent = ImageUtil.getMimeType(ChatScreenActivity.this, URI).contains("video");

                    String fileName = null;
                    try {
                        if (!hasVideoContent) {
                            fileName = DBUtil.getUniqueFileName(SportsUnityDBHelper.MIME_TYPE_IMAGE, false);
                            this.object = ImageUtil.getScaledDownBytes(file, getResources().getDisplayMetrics());

                            DBUtil.writeContentToExternalFileStorage(ChatScreenActivity.this, fileName, (byte[]) this.object, SportsUnityDBHelper.MIME_TYPE_IMAGE);
                            thumbnailImage = PersonalMessaging.createThumbnailImageAsBase64(ChatScreenActivity.this, SportsUnityDBHelper.MIME_TYPE_IMAGE, fileName);
                        } else {
                            fileName = DBUtil.getUniqueFileName(SportsUnityDBHelper.MIME_TYPE_VIDEO, false);
                            this.object = fileName;
                            DBUtil.writeContentToExternalFileStorage(ChatScreenActivity.this, file, fileName, SportsUnityDBHelper.MIME_TYPE_VIDEO);
                            thumbnailImage = PersonalMessaging.createThumbnailImageAsBase64(ChatScreenActivity.this, SportsUnityDBHelper.MIME_TYPE_VIDEO, fileName);

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return fileName;
                }

                @Override
                public void postAction(Object object) {
                    String fileName = (String) object;
                    Object mediaContent = this.object;

                    if (!hasVideoContent) {
                        ActivityActionHandler.getInstance().dispatchSendMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_IMAGE, fileName, thumbnailImage, mediaContent);
                    } else {
                        ActivityActionHandler.getInstance().dispatchSendMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, SportsUnityDBHelper.MIME_TYPE_VIDEO, fileName, thumbnailImage, mediaContent);
                    }
                }

            }.start();
        } catch (Exception ex) {
            ex.printStackTrace();

            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.tool_bar_chat);
        toolbarActionsForChatScreen.getToolbarMenu(mtoolbar, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_screen, menu);
        this.menu = menu;

        if (isGroupChat) {
            MenuItem viewContactItem = menu.findItem(R.id.action_view_contact);
            viewContactItem.setTitle("View Group");

            MenuItem blockUserItem = menu.findItem(R.id.action_block_user);
            blockUserItem.setVisible(false);
        } else {
            //nothing
        }


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
            int contactStatus = ChatScreenActivity.this.getIntent().getIntExtra(INTENT_KEY_CONTACT_AVAILABLE_STATUS, Contacts.AVAILABLE_NOT);
            String status = ChatScreenActivity.this.getIntent().getStringExtra(INTENT_KEY_USER_STATUS);
            viewProfile(ChatScreenActivity.this, isGroupChat, chatID, userImageBytes, jabberName, jabberId, status, otherChat, contactStatus, blockStatus);
            return true;
        } else if (id == R.id.action_block_user) {
            blockUnblockUserHelper.onMenuItemSelected(this, chatID, jabberId, menu);
        } else if (id == R.id.action_clear_chat) {
            showAlertDialogToClearChat();
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
            LinearLayout sendContentLayout = (LinearLayout) findViewById(R.id.type_msg);
            sendContentLayout.setVisibility(View.GONE);
            invalidateOptionsMenu();

            View view = findViewById(R.id.btn_text);
            chatKeyboardHelper.openTextKeyBoard(view, this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialogToClearChat() {
        String positiveButtonTitle = "CLEAR";
        String negativeButtonTitle = "CANCEL";
        String dialogTitle = "Are you sure you want to clear all messages ?";

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearChat();
            }
        };

        new AlertDialogUtil(AlertDialogUtil.ACTION_CLEAR_ALL_MESSAGES, dialogTitle, positiveButtonTitle, negativeButtonTitle, ChatScreenActivity.this, clickListener).show();
    }

    private void clearChat() {
        sportsUnityDBHelper.clearChat(getApplicationContext(), chatID);

        AudioRecordingHelper.getInstance(ChatScreenActivity.this).stopAndReleaseMediaPlayer();
        AudioRecordingHelper.getInstance(ChatScreenActivity.this).clearProgressMap();

        messageList = sportsUnityDBHelper.getMessages(chatID);

        chatScreenAdapter.notifydataset(messageList);
    }

    private void sendReadStatus() {
        if (!isGroupChat) {
            for (Message message : messageList) {
                if (!(message.iAmSender || message.messagesRead)) {
                    personalMessaging.sendReadStatus(message.number, message.messageStanzaId);
                } else {
                    //nothing
                }
            }
        } else {
            ///TODO
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

    @Override
    public void onReconnecting(int seconds) {
        super.onReconnecting(seconds);
        setStatusConnecting();
    }


    private void setStatusConnecting() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setVisibility(View.VISIBLE);
                status.setText("Connecting...");
            }
        });
    }

    private void setStatusConnected() {
        try {
            if (!blockUnblockUserHelper.isBlockStatus()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setText("Connected");
                    }
                });
                getLastSeen();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setVisibility(View.GONE);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInternetStateChanged(boolean connected) {
    }

    @Override
    public void onXMPPServiceAuthenticated(boolean connected, XMPPConnection connection) {
        if (connected) {
            setStatusConnected();

            getChatThread();
            checkForwardMessageQueue();
            if (isGroupChat) {
                setGroupMembers();
            }
            if (!isGroupChat) {
                if (isLastTimeRequired)
                    personalMessaging.getLastTime(jabberId);
            } else {
                //TODO
            }
        }
    }


    @Override
    public void onBlock(boolean success, String phoneNumber) {
        if (success) {
//            findViewById(R.id.add_block_layout).setVisibility(View.GONE);
//            initAddBlockView();
            checkAndUpdateBlockStatus();
        }
    }

    @Override
    public void onUnblock(boolean success) {
        if (success) {
            checkAndUpdateBlockStatus();
        }
    }
}
