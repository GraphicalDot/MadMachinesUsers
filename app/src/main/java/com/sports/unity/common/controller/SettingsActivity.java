package com.sports.unity.common.controller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.NotificationHandler;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private static final boolean NOTIFICATION_AND_SOUNDS_DEFAULT_VALUE = true;
    private static final boolean NOTIFICATION_PREVIEWS_DEFAULT_VALUE = true;
    private static final boolean CONVERSATION_TONES_DEFAULT_VALUE = true;
    private static final boolean VIBRATE_DEFAULT_VALUE = true;
    private static final boolean LIGHT_DEFAULT_VALUE = true;
    private static final boolean NOTIFICATION_SOUND_DEFAULT_VALUE = true;

    private static final boolean SHOW_MY_LOCATION_DEFAULT_VALUE = true;
    private static final boolean FRIENDS_ONLY_DEFAULT_VALUE = true;
    private static final boolean SHOW_TO_ALL_DEFAULT_VALUE = true;

    private static final boolean PHOTOS_AND_MEDIA_DEFAULT_VALUE = true;
    private static final boolean SAVE_INCOMING_MEDIA_DEFAULT_VALUE = true;
    private static final boolean SAVE_INAPP_CAPTURE_DEFAULT_VALUE = true;


    private LinearLayout mNotificationPreviews;
    private LinearLayout mConversationTones;
    private LinearLayout mVibrate;
    private LinearLayout mLight;
    private LinearLayout mNotificationSound;

    private LinearLayout mLocationFriends;
    private LinearLayout mLocationAll;

    private LinearLayout mSaveIncMedia;
    private LinearLayout mSaveInAppMedia;

    private CheckBox mNotificationPreviewsCheckbox;
    private CheckBox mConversationTonesCheckbox;
    private CheckBox mVibrateCheckbox;
    private CheckBox mLightCheckbox;
    private CheckBox mNotificationSoundCheckbox;

    private CheckBox mFriendssOnlyCheckbox;
    private CheckBox mShowToAllCheckbox;

    private CheckBox mSaveIncomingMediaCheckbox;
    private CheckBox mSaveCapturedInAppMediaCheckbox;

    private Switch notificationSwitch;
    private Switch showLocationSwitch;
    private Switch photosAndMediaSwitch;

    private TinyDB tinyDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tinyDB = TinyDB.getInstance(getApplicationContext());
        initToolbar();
        setCustomFont();
        initLinearLayouts();
        initCheckBoxes();
        loadPreferences();
        setSwitchListeners();
        setCheckboxListeners();
    }

    private void initLinearLayouts() {
        mNotificationPreviews = (LinearLayout) findViewById(R.id.notify_prev);
        mConversationTones = (LinearLayout) findViewById(R.id.converse_tones);
        mVibrate = (LinearLayout) findViewById(R.id.notify_vibrate);
        mLight = (LinearLayout) findViewById(R.id.notify_light);
        mNotificationSound = (LinearLayout) findViewById(R.id.notify_sound);

        mLocationFriends = (LinearLayout) findViewById(R.id.friends_only_layout);
        mLocationAll = (LinearLayout) findViewById(R.id.show_all_layout);

        mSaveIncMedia = (LinearLayout) findViewById(R.id.save_incoming_media_layout);
        mSaveInAppMedia = (LinearLayout) findViewById(R.id.save_captured_media_layout);

        mNotificationPreviews.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        mConversationTones.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        mVibrate.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        mLight.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        mNotificationSound.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));

        mLocationFriends.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        mLocationAll.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));

        mSaveIncMedia.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        mSaveInAppMedia.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));

        Button clearAllChat = (Button) findViewById(R.id.clear_all_chat);
        clearAllChat.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));
        Button deleteAllChat = (Button) findViewById(R.id.delete_all_chat);
        deleteAllChat.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));


    }

    private void loadPreferences() {

        boolean notificationAndSoundOptions = tinyDB.getBoolean(TinyDB.NOTIFICATION_AND_SOUND_OPTIONS, NOTIFICATION_AND_SOUNDS_DEFAULT_VALUE);
        if (notificationAndSoundOptions) {
            notificationSwitch.setChecked(true);

            boolean notificationPreviews = tinyDB.getBoolean(TinyDB.NOTIFICATION_PREVIEW, NOTIFICATION_PREVIEWS_DEFAULT_VALUE);
            if (notificationPreviews) {
                mNotificationPreviewsCheckbox.setChecked(true);
            } else {
                mNotificationPreviewsCheckbox.setChecked(false);
            }


            boolean conversationTones = tinyDB.getBoolean(TinyDB.CONVERSATION_TONES, CONVERSATION_TONES_DEFAULT_VALUE);
            if (conversationTones) {
                mConversationTonesCheckbox.setChecked(true);
            } else {
                mConversationTonesCheckbox.setChecked(false);
            }

            boolean vibrate = tinyDB.getBoolean(TinyDB.VIBRATE, VIBRATE_DEFAULT_VALUE);
            if (vibrate) {
                mVibrateCheckbox.setChecked(true);
            } else {
                mVibrateCheckbox.setChecked(false);
            }


            boolean light = tinyDB.getBoolean(TinyDB.LIGHT, LIGHT_DEFAULT_VALUE);
            if (light) {
                mLightCheckbox.setChecked(true);
            } else {
                mLightCheckbox.setChecked(false);
            }


            boolean notificationSound = tinyDB.getBoolean(TinyDB.NOTIFICATION_SOUND, NOTIFICATION_SOUND_DEFAULT_VALUE);
            if (notificationSound) {
                mNotificationSoundCheckbox.setChecked(true);
            } else {
                mNotificationSoundCheckbox.setChecked(false);
            }


        } else {
            notificationSwitch.setChecked(false);
            disableNotificationOptions();
        }

        boolean locationOptions = tinyDB.getBoolean(TinyDB.LOCATION_OPTIONS, SHOW_MY_LOCATION_DEFAULT_VALUE);
        if (locationOptions) {
            showLocationSwitch.setChecked(true);

            boolean friendsOnly = tinyDB.getBoolean(TinyDB.FRIENDS_ONLY, FRIENDS_ONLY_DEFAULT_VALUE);
            if (friendsOnly) {
                mFriendssOnlyCheckbox.setChecked(true);
            } else {
                mFriendssOnlyCheckbox.setChecked(false);
            }


            boolean showToAll = tinyDB.getBoolean(TinyDB.SHOW_TO_ALL, SHOW_TO_ALL_DEFAULT_VALUE);
            if (showToAll) {
                mShowToAllCheckbox.setChecked(true);
            } else {
                mShowToAllCheckbox.setChecked(false);
            }


        } else {
            showLocationSwitch.setChecked(false);
            disableLocationOptions();
        }

        boolean saveMediaOptions = tinyDB.getBoolean(TinyDB.MEDIA_OPTIONS, PHOTOS_AND_MEDIA_DEFAULT_VALUE);
        if (saveMediaOptions) {
            photosAndMediaSwitch.setChecked(true);

            boolean saveIncomingMedia = tinyDB.getBoolean(TinyDB.SAVE_INCOMING_MEDIA, SAVE_INCOMING_MEDIA_DEFAULT_VALUE);
            if (saveIncomingMedia) {
                mSaveIncomingMediaCheckbox.setChecked(true);
            } else {
                mSaveIncomingMediaCheckbox.setChecked(false);
            }


            boolean saveInAppCaptureMedia = tinyDB.getBoolean(TinyDB.SAVE_IN_APP_MEDIA_CAPTURE, SAVE_INAPP_CAPTURE_DEFAULT_VALUE);
            if (saveInAppCaptureMedia) {
                mSaveCapturedInAppMediaCheckbox.setChecked(true);
            } else {
                mSaveCapturedInAppMediaCheckbox.setChecked(false);
            }


        } else {
            photosAndMediaSwitch.setChecked(false);
            disableMediaOptions();
        }

    }

    public void onViewClick(View view) {
        LinearLayout layout = (LinearLayout) findViewById(view.getId());
        CheckBox checkbox = (CheckBox) layout.getChildAt(1);
        if (checkbox.isChecked()) {
            checkbox.setChecked(false);
        } else {
            checkbox.setChecked(true);
        }

    }

    private void initCheckBoxes() {

        mNotificationPreviewsCheckbox = (CheckBox) findViewById(R.id.previews_checkbox);
        mConversationTonesCheckbox = (CheckBox) findViewById(R.id.tones_checkbox);
        mVibrateCheckbox = (CheckBox) findViewById(R.id.vibrate_checkbox);
        mLightCheckbox = (CheckBox) findViewById(R.id.light_checkbox);
        mNotificationSoundCheckbox = (CheckBox) findViewById(R.id.notification_sound_checkbox);

        mFriendssOnlyCheckbox = (CheckBox) findViewById(R.id.friends_only_checkbox);
        mShowToAllCheckbox = (CheckBox) findViewById(R.id.show_to_all_checkbox);
        mSaveIncomingMediaCheckbox = (CheckBox) findViewById(R.id.save_photo_checkbox);
        mSaveCapturedInAppMediaCheckbox = (CheckBox) findViewById(R.id.save_photo_inapp_checkbox);
    }

    private void setCheckboxListeners() {
        mNotificationPreviewsCheckbox.setOnCheckedChangeListener(checkedChangeListener);
        mConversationTonesCheckbox.setOnCheckedChangeListener(checkedChangeListener);
        mVibrateCheckbox.setOnCheckedChangeListener(checkedChangeListener);
        mLightCheckbox.setOnCheckedChangeListener(checkedChangeListener);
        mNotificationSoundCheckbox.setOnCheckedChangeListener(checkedChangeListener);

        mFriendssOnlyCheckbox.setOnCheckedChangeListener(checkedChangeListener);
        mShowToAllCheckbox.setOnCheckedChangeListener(checkedChangeListener);

        mSaveIncomingMediaCheckbox.setOnCheckedChangeListener(checkedChangeListener);
        mSaveCapturedInAppMediaCheckbox.setOnCheckedChangeListener(checkedChangeListener);
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            if (id == mNotificationPreviewsCheckbox.getId()) {
                tinyDB.putBoolean(TinyDB.NOTIFICATION_PREVIEW, mNotificationPreviewsCheckbox.isChecked());
            } else if (id == mConversationTonesCheckbox.getId()) {
                tinyDB.putBoolean(TinyDB.CONVERSATION_TONES, mConversationTonesCheckbox.isChecked());
            } else if (id == mVibrateCheckbox.getId()) {
                tinyDB.putBoolean(TinyDB.VIBRATE, mVibrateCheckbox.isChecked());
            } else if (id == mLightCheckbox.getId()) {
                tinyDB.putBoolean(TinyDB.LIGHT, mLightCheckbox.isChecked());
            } else if (id == mNotificationSoundCheckbox.getId()) {
                tinyDB.putBoolean(TinyDB.NOTIFICATION_SOUND, mNotificationSoundCheckbox.isChecked());
            } else if (id == mFriendssOnlyCheckbox.getId()) {
                tinyDB.putBoolean(TinyDB.FRIENDS_ONLY, mFriendssOnlyCheckbox.isChecked());
            } else if (id == mShowToAllCheckbox.getId()) {
                tinyDB.putBoolean(TinyDB.SHOW_TO_ALL, mShowToAllCheckbox.isChecked());
            } else if (id == mSaveIncomingMediaCheckbox.getId()) {
                tinyDB.putBoolean(TinyDB.SAVE_INCOMING_MEDIA, mSaveIncomingMediaCheckbox.isChecked());
            } else if (id == mSaveCapturedInAppMediaCheckbox.getId()) {
                tinyDB.putBoolean(TinyDB.SAVE_IN_APP_MEDIA_CAPTURE, mSaveCapturedInAppMediaCheckbox.isChecked());
            }
        }
    };

    private void setCustomFont() {

        /**
         * Headings
         */

        notificationSwitch = (Switch) findViewById(R.id.notification_and_sounds);
        notificationSwitch.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabBold());
        showLocationSwitch = (Switch) findViewById(R.id.show_my_location);
        showLocationSwitch.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabBold());
        photosAndMediaSwitch = (Switch) findViewById(R.id.photos_and_media);
        photosAndMediaSwitch.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabBold());
        TextView chatOptions = (TextView) findViewById(R.id.chat_options);
        chatOptions.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabBold());

        /**
         * Sub headings
         */

        TextView notificationPreviews = (TextView) findViewById(R.id.notification_previews);
        notificationPreviews.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        TextView conversationTones = (TextView) findViewById(R.id.conversation_tones);
        conversationTones.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        TextView vibrate = (TextView) findViewById(R.id.vibrate);
        vibrate.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        TextView light = (TextView) findViewById(R.id.light);
        light.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        TextView notificationSound = (TextView) findViewById(R.id.notification_sound);
        notificationSound.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        TextView friendsOnly = (TextView) findViewById(R.id.friends_only);
        friendsOnly.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        TextView showToAll = (TextView) findViewById(R.id.show_to_all);
        showToAll.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        TextView savePhoto = (TextView) findViewById(R.id.save_photo);
        savePhoto.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        TextView saveOnCapture = (TextView) findViewById(R.id.save_on_capture);
        saveOnCapture.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        Button clearAllChat = (Button) findViewById(R.id.clear_all_chat);
        clearAllChat.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        Button deleteAllChat = (Button) findViewById(R.id.delete_all_chat);
        deleteAllChat.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
        setButtonListeners(clearAllChat, deleteAllChat);

        /**
         * Sub - Sub Headings
         */

        TextView showNameAndMessage = (TextView) findViewById(R.id.show_name_and_message);
        showNameAndMessage.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView playSoundForMessages = (TextView) findViewById(R.id.play_sound_for_messages);
        playSoundForMessages.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView ping = (TextView) findViewById(R.id.ping);
        ping.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView friendsOnlySubText = (TextView) findViewById(R.id.show_location_friends_only);
        friendsOnlySubText.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView showToAllSubText = (TextView) findViewById(R.id.show_to_spu_users);
        showToAllSubText.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView saveIncomingMedia = (TextView) findViewById(R.id.save_incoming_media);
        saveIncomingMedia.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
        TextView savePhotoInApp = (TextView) findViewById(R.id.save_photo_inapp);
        savePhotoInApp.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
    }

    private void setButtonListeners(Button clearAllChat, Button deleteAllChat) {
        clearAllChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlert("Clear messages in all chat ?", 1);
            }
        });

        deleteAllChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlert("Delete all chats and their messages ?", 0);
            }
        });
    }

    private void displayAlert(String message, final int action) {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setMessage(message);
        build.setPositiveButton("ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (action == 1) {
                            clearAllChat();
                        } else {
                            deleteAllChat();
                        }
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

    private void setSwitchListeners() {
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableNotificationOptions();
                } else {
                    disableNotificationOptions();
                }
                tinyDB.putBoolean(TinyDB.NOTIFICATION_AND_SOUND_OPTIONS, isChecked);
            }
        });

        showLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableLocationOptions();
                } else {
                    disableLocationOptions();
                }
                tinyDB.putBoolean(TinyDB.LOCATION_OPTIONS, isChecked);
            }
        });

        photosAndMediaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableMediaOptions();
                } else {
                    disableMediaOptions();
                }
                tinyDB.putBoolean(TinyDB.MEDIA_OPTIONS, isChecked);
            }
        });
    }

    private void disableMediaOptions() {
        mSaveIncomingMediaCheckbox.setChecked(false);
        mSaveCapturedInAppMediaCheckbox.setChecked(false);

        mSaveIncMedia.setClickable(false);
        mSaveInAppMedia.setClickable(false);

        updateMediaPreferences();
    }

    private void enableMediaOptions() {

        mSaveIncomingMediaCheckbox.setChecked(true);
        mSaveCapturedInAppMediaCheckbox.setChecked(true);

        mSaveIncMedia.setClickable(true);
        mSaveInAppMedia.setClickable(true);

        updateMediaPreferences();
    }

    private void disableLocationOptions() {
        mFriendssOnlyCheckbox.setChecked(false);
        mShowToAllCheckbox.setChecked(false);

        mLocationFriends.setClickable(false);
        mLocationAll.setClickable(false);

        updateLocationPreferences();
    }

    private void enableLocationOptions() {

        mFriendssOnlyCheckbox.setChecked(true);
        mShowToAllCheckbox.setChecked(true);

        mLocationFriends.setClickable(true);
        mLocationAll.setClickable(true);

        updateLocationPreferences();

    }

    private void disableNotificationOptions() {

        mNotificationPreviewsCheckbox.setChecked(false);
        mConversationTonesCheckbox.setChecked(false);
        mVibrateCheckbox.setChecked(false);
        mLightCheckbox.setChecked(false);
        mNotificationSoundCheckbox.setChecked(false);

        mNotificationPreviews.setClickable(false);
        mConversationTones.setClickable(false);
        mVibrate.setClickable(false);
        mLight.setClickable(false);
        mNotificationSound.setClickable(false);

        updateNotificationPreferences();
    }

    private void enableNotificationOptions() {

        mNotificationPreviewsCheckbox.setChecked(true);
        mConversationTonesCheckbox.setChecked(true);
        mVibrateCheckbox.setChecked(true);
        mLightCheckbox.setChecked(true);
        mNotificationSoundCheckbox.setChecked(true);

        mNotificationPreviews.setClickable(true);
        mConversationTones.setClickable(true);
        mVibrate.setClickable(true);
        mLight.setClickable(true);
        mNotificationSound.setClickable(true);

        updateNotificationPreferences();

    }

    private void updateNotificationPreferences() {
        tinyDB.putBoolean(TinyDB.NOTIFICATION_PREVIEW, mNotificationPreviewsCheckbox.isChecked());
        tinyDB.putBoolean(TinyDB.CONVERSATION_TONES, mConversationTonesCheckbox.isChecked());
        tinyDB.putBoolean(TinyDB.VIBRATE, mVibrateCheckbox.isChecked());
        tinyDB.putBoolean(TinyDB.LIGHT, mLightCheckbox.isChecked());
        tinyDB.putBoolean(TinyDB.NOTIFICATION_SOUND, mNotificationSoundCheckbox.isChecked());
    }

    private void updateLocationPreferences() {
        tinyDB.putBoolean(TinyDB.FRIENDS_ONLY, mFriendssOnlyCheckbox.isChecked());
        tinyDB.putBoolean(TinyDB.SHOW_TO_ALL, mShowToAllCheckbox.isChecked());
    }

    private void updateMediaPreferences() {
        tinyDB.putBoolean(TinyDB.SAVE_INCOMING_MEDIA, mSaveIncomingMediaCheckbox.isChecked());
        tinyDB.putBoolean(TinyDB.SAVE_IN_APP_MEDIA_CAPTURE, mSaveCapturedInAppMediaCheckbox.isChecked());
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setContentInsetsAbsolute(0, 0);

        ImageView closeActivity = (ImageView) toolbar.findViewById(R.id.close_activity);
        closeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView settingTitle = (TextView) toolbar.findViewById(R.id.settings);
        settingTitle.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabBold());

        closeActivity.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));

    }

    private void clearAllChat() {
        ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getApplicationContext()).getChatList(false);
        for (Chats chatObject : chatList) {
            SportsUnityDBHelper.getInstance(getApplicationContext()).clearChat(getApplicationContext(), chatObject.chatid, chatObject.groupServerId);
        }
    }

    private void deleteAllChat() {
        ArrayList<Chats> chatList = SportsUnityDBHelper.getInstance(getApplicationContext()).getChatList(false);
        for (Chats chatObject : chatList) {
            SportsUnityDBHelper.getInstance(getApplicationContext()).clearChat(this, chatObject.chatid, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
            SportsUnityDBHelper.getInstance(this).clearChatEntry(chatObject.chatid);
            NotificationHandler.getInstance().clearNotificationMessages(chatObject.chatid);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
