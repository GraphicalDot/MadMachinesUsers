package com.sports.unity.messages.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.sports.unity.common.model.FontTypeface;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.joda.time.DateTime;

import java.util.ArrayList;

public class ChatScreenActivity extends AppCompatActivity {

    private static ArrayList<SportsUnityDBHelper.Message> messageList;
    private static ChatScreenAdapter chatScreenAdapter;
    private static String JABBERID;
    private static String JABBERNAME;

    private EditText mMsg;
    private Chat chat;
    private MessageRecieved messageRecieved;

    private ViewGroup parentLayout;

    private View popUpView;
    private PopupWindow popupWindow;

    private int keyboardHeight;
    private boolean isKeyBoardVisible;

    private SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);

        parentLayout = (ViewGroup) findViewById(R.id.list_parent);
        checkKeyboardHeight(parentLayout);
        createPopupWindowOnKeyBoard();

        /**
         * Initialising toolbar for chat screen activity
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        JABBERID = getIntent().getStringExtra("jid");                       //name of the user you are messaging with
        JABBERNAME = getIntent().getStringExtra("jbname");                  //phone number or jid of the user you are chatting with


        ListView mChatView = (ListView) findViewById(R.id.msgview);                // List for messages

        messageRecieved = new MessageRecieved();

        sportsUnityDBHelper.clearUnreadCount(JABBERID);
        TextView user = (TextView) toolbar.findViewById(R.id.chat_username);
        user.setText(JABBERNAME);
        user.setTypeface(FontTypeface.getInstance(this).getRobotoRegular());
        ImageButton back = (ImageButton) toolbar.findViewById(R.id.backarrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mMsg = (EditText) findViewById(R.id.msg);
        Button mSend = (Button) findViewById(R.id.send);
        mSend.setTypeface(FontTypeface.getInstance(this).getRobotoCondensedRegular());
        //MessageListener messageListener = new MessageListenerImpl();
        ChatManager chatManager = ChatManager.getInstanceFor(XMPPClient.getConnection());
        if (mMsg.getText().toString() != null) {
            chat = chatManager.getThreadChat(JABBERID);
            if (chat == null)
                chat = chatManager.createChat(JABBERID + "@mm.io");

            messageList = sportsUnityDBHelper.getMessages(JABBERID);
            chatScreenAdapter = new ChatScreenAdapter(ChatScreenActivity.this, messageList);
            mChatView.setAdapter(chatScreenAdapter);
            mSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessage();
                }
            });
        }
    }


    private void sendMessage() {

        if (mMsg.getText().toString().equals("")) {
            //Do nothing
        } else {
            try {
                Message message = new Message();
                message.setBody(mMsg.getText().toString());
                DateTime dateTime = DateTime.now();
                JivePropertiesManager.addProperty(message, "time", dateTime.getMillis());

                chat.sendMessage(message);


                sportsUnityDBHelper.addMessage(mMsg.getText().toString(), JABBERID, true, null);
                sportsUnityDBHelper.updateChatLabel(JABBERID);
                messageList = sportsUnityDBHelper.getMessages(JABBERID);
                chatScreenAdapter.notifydataset(messageList);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        mMsg.setText("");
    }

    /*public class MessageListenerImpl implements MessageListener, ChatStateListener {
        @Override
        public void stateChanged(Chat chat, ChatState state) {
            if (ChatState.composing.equals(state)) {
                Log.d("Chat State", chat.getParticipant() + " is typing..");
                userActiveStatus.setText("typing...");
            } else if (ChatState.gone.equals(state)) {
                Log.d("Chat State", chat.getParticipant() + " has left the conversation.");
                userActiveStatus.setText("Online");
            } else {
                Log.d("Chat State", chat.getParticipant() + ": " + state.name());
            }
        }

        @Override
        public void processMessage(Chat chat, Message message) {

        }

        @Override
        public void processMessage(Message message) {

        }
    }*/

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
        }
    }

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

        // Creating a pop window for emoticons keyboard
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
    protected void onResume() {
        super.onResume();
        registerReceiver(messageRecieved, new IntentFilter("com.madmachine.SINGLE_MESSAGE_RECEIVED"));
        Log.i("reciever :", "registered");
        ChatScreenApplication.activityResumed();

    }

    @Override
    public void onBackPressed() {
//        Intent intent = new Intent();
//        intent.setAction("com.madmachine.SINGLE_MESSAGE_RECEIVED");
//        sendBroadcast(intent);

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_clear_chat) {
            sportsUnityDBHelper.clearChat(JABBERID);
            messageList = sportsUnityDBHelper.getMessages(JABBERID);
            chatScreenAdapter.notifydataset(messageList);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        ChatScreenApplication.activityDestroyed();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        ChatScreenApplication.activityPaused();
        super.onPause();
        unregisterReceiver(messageRecieved);
        Log.i("receiver :", "unregistered");

    }

    @Override
    public void onStop() {
        ChatScreenApplication.activityStopped();
        super.onStop();

    }

    public static String getJABBERID() {
        return JABBERID;
    }

    public static class MessageRecieved extends BroadcastReceiver {

        public MessageRecieved() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            messageList = SportsUnityDBHelper.getInstance(context).getMessages(JABBERID);
            chatScreenAdapter.notifydataset(messageList);
            Log.i("yoyo :", "yoyo");

        }
    }
}
