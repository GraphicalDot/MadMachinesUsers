package com.sports.unity.messages.controller.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.controller.UserProfileActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.BlockUnblockUserHelper;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.GroupMessaging;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.messages.controller.model.PersonalMessaging;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.ActivityActionListener;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.util.ArrayList;

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

    private long contactID = SportsUnityDBHelper.DEFAULT_ENTRY_ID;
    private String groupServerId = null;
    private boolean isGroupChat = false;

    private EditText messageText;
    private Chat chat;
    private TextView status;
    private ImageButton back;

    private ViewGroup parentLayout;

    private View popUpView;
    private PopupWindow popupWindow;

    private int keyboardHeight;
    private boolean isKeyBoardVisible;
    private static XMPPTCPConnection con;


    private CircleImageView userPic;

    private SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);

    private PersonalMessaging personalMessaging = PersonalMessaging.getInstance(this);

    private GroupMessaging groupMessaging = GroupMessaging.getInstance(this);
    private MultiUserChat multiUserChat = null;

    private BlockUnblockUserHelper blockUnblockUserHelper = null;

    private Menu menu = null;

    private ActivityActionListener activityActionListener = new ActivityActionListener() {
        @Override
        public void handleAction(int id, final Object object) {
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
                        status.setText("Active " + object.toString() + " ago");
                    }
                }
            });
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

        parentLayout = (ViewGroup) findViewById(R.id.list_parent);
        checkKeyboardHeight(parentLayout);
        createPopupWindowOnKeyBoard();

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
        LinearLayout profile_link = (LinearLayout)  toolbar.findViewById(R.id.profile);

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

            boolean blockStatus = getIntent() .getBooleanExtra("blockStatus", false);
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
        if( blockUnblockUserHelper.isBlockStatus() ){
            blockUnblockUserHelper.showAlert_ToSendMessage_UnblockUser(this, contactID, JABBERID, menu);
            return;
        }

        createChatEntryifNotExists();

        if (messageText.getText().toString().equals("")) {
            //Do nothing
        } else {
            Log.i("Message Entry", "adding message chat " + chatID);

            if (isGroupChat) {
                groupMessaging.sendMessageToGroup(messageText.getText().toString(), multiUserChat, chatID, groupServerId, TinyDB.getInstance(this).getString(TinyDB.KEY_USERNAME));
                messageList = sportsUnityDBHelper.getMessages(chatID);
                groupChatScreenAdapter.notifydataset(messageList);
            } else {
                personalMessaging.sendMessageToPeer(messageText.getText().toString(), chat, JABBERID, chatID, JABBERNAME);
                personalMessaging.sendStatus(ChatState.paused, chat);
                messageList = sportsUnityDBHelper.getMessages(chatID);
                chatScreenAdapter.notifydataset(messageList);
            }
        }

        messageText.setText("");
    }

    /**
     * Overriding onKeyDown for dismissing keyboard on key down
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * Checking keyboard height and keyboard visibility
     */
    int previousHeightDiffrence = 0;

    private void checkKeyboardHeight(final View parentLayout) {

        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        parentLayout.getWindowVisibleDisplayFrame(r);

                        int screenHeight = parentLayout.getRootView()
                                .getHeight();
                        int heightDifference = screenHeight - (r.bottom);

                        if (previousHeightDiffrence - heightDifference > 50) {
                            popupWindow.dismiss();
                        }

                        previousHeightDiffrence = heightDifference;
                        if (heightDifference > 100) {

                            isKeyBoardVisible = true;
                            changeKeyboardHeight(heightDifference);

                        } else {

                            isKeyBoardVisible = false;

                        }

                    }
                });

    }

    /**
     * change height of emoticons keyboard according to height of actual
     * keyboard
     *
     * @param height minimum height by which we can make sure actual keyboard is
     *               open or not
     */
    private void changeKeyboardHeight(int height) {

        if (height > 100) {
            keyboardHeight = height;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight);
//            emoticonsCover.setLayoutParams(params);
        }

    }

    public void openCamera(View view) {
        if (isKeyBoardVisible) {
            enablePopupWindowOnKeyBoard();
            ViewGroup viewGroup = (ViewGroup) popupWindow.getContentView().findViewById(R.id.popup_window_camera);
            viewGroup.setVisibility(View.VISIBLE);
            Intent intent = new Intent(ChatScreenActivity.this, CameraActivity.class);
            startActivity(intent);

        }

    }


    public void emojipopup(View view) {
        if (isKeyBoardVisible) {
            enablePopupWindowOnKeyBoard();
            ViewGroup viewGroup = (ViewGroup) popupWindow.getContentView().findViewById(R.id.popup_window_emoji);
            viewGroup.setVisibility(View.VISIBLE);
        }
    }


    public void galleryPopup(View view) {
        if (isKeyBoardVisible) {
            enablePopupWindowOnKeyBoard();
            ViewGroup viewGroup = (ViewGroup) popupWindow.getContentView().findViewById(R.id.popup_window_gallery);
            viewGroup.setVisibility(View.VISIBLE);
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri chosenImageUri = data.getData();
            //File imageFile = new File(getRealPathFromURI(chosenImageUri));


            Bitmap mBitmap = null;
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);
                Log.i("getBitmap? :", "yes");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //      convertTobyteArray(mBitmap, imageFile);
        }
    }

    /*private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }*/

/*    private void convertTobyteArray(Bitmap mBitmap, File imageFile) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        getMD5checksum(byteArray, imageFile);
    }

    private void getMD5checksum(byte[] s, File imageFile) {

        byte[] hash = new byte[0];
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(s);
            hash = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Log.i("hash", String.valueOf(hash));
        HttpManager.getInstance().HttpPostImageRequest(hash, imageFile);
    }*/

    public void voicePopup(View view) {
        if (isKeyBoardVisible) {
            enablePopupWindowOnKeyBoard();
            ViewGroup viewGroup = (ViewGroup) popupWindow.getContentView().findViewById(R.id.popup_window_voice);
            viewGroup.setVisibility(View.VISIBLE);
        }
    }

    public void showKeyboard(View view) {

        if (popupWindow.isShowing())
            popupWindow.dismiss();

    }

    private void createPopupWindowOnKeyBoard() {

        popUpView = getLayoutInflater().inflate(R.layout.parent_layout_media_keyboard, null);
        popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, (int) keyboardHeight, false);

    }

    private void enablePopupWindowOnKeyBoard() {
        if (!popupWindow.isShowing()) {
            popupWindow.setHeight((int) (keyboardHeight));
            popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
        } else {
            //nothing
        }

        ViewGroup contentView = ((ViewGroup) popupWindow.getContentView());
        for (int loop = 0; loop < contentView.getChildCount(); loop++) {
            contentView.getChildAt(loop).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_screen, menu);

        this.menu = menu;

        blockUnblockUserHelper.initViewBasedOnBlockStatus(menu);
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
            blockUnblockUserHelper.onMenuItemSelected( this, contactID, JABBERID, menu);
        } else if (id == R.id.action_clear_chat) {
            sportsUnityDBHelper.clearChat(chatID, groupServerId);
            messageList = sportsUnityDBHelper.getMessages(chatID);
            chatScreenAdapter.notifydataset(messageList);
        } else if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static String getJABBERID() {
        return JABBERID;
    }

}
