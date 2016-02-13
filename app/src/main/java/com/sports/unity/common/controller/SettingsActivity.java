package com.sports.unity.common.controller;

import android.content.DialogInterface;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.NotificationHandler;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private static final int SETTINGS_MAIN_ID = 0;

    private static final int NOTIFICATIONS_AND_SOUND_ITEM_ID = 1;
    private static final int NOTIFICATION_PREVIEW_ITEM_ID = 2;
    private static final int NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID = 3;
    private static final int NOTIFICATIONS_VIBRATE_ITEM_ID = 4;
    private static final int NOTIFICATIONS_LIGHT_ITEM_ID = 5;
    private static final int NOTIFICATIONS_SOUND_ITEM_ID = 6;

    private static final int SHOW_MY_LOCATION_ITEM_ID = 10;
    private static final int FRIEND_ONLY_LOCATION_ITEM_ID = 11;
    private static final int ALL_USER_LOCATION_ITEM_ID = 12;

    private static final int PHOTO_AND_MEDIA_ITEM_ID = 20;
    private static final int SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID = 21;
    private static final int SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID = 22;

    private static final int CHATS_ITEM_ID = 30;
    private static final int CLEAR_ALL_CHATS_ITEM_ID = 31;
    private static final int DELETE_ALL_CHATS_ITEM_ID = 32;

    private static final int PRIVACY_ITEM_ID = 40;
    private static final int LAST_SEEN_ITEM_ID = 41;
    private static final int PROFILE_PHOTO_ITEM_ID = 42;
    private static final int STATUS_ITEM_ID = 43;
    private static final int READ_RECEIPTS_ITEM_ID = 44;
    private static final int BLOCKED_CONTACTS_ITEM_ID = 45;

    private HashMap<Integer, int[]> drillDownItemsMap = new HashMap<>();
    private int currentItemId = SETTINGS_MAIN_ID;

    private ItemEventListener itemEventListener = new ItemEventListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        initDrillDownMap();

        initToolbar();
        renderDrillDownItems(currentItemId);
    }

    @Override
    public void onBackPressed() {
        if ( currentItemId == SETTINGS_MAIN_ID ) {
            super.onBackPressed();
        } else {
            renderDrillDownItems(SETTINGS_MAIN_ID);
        }
    }

    private void initToolbar( ) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        ImageView back = (ImageView) toolbar.findViewById(R.id.backButton);
        back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });

        TextView settingTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        settingTitle.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabBold());

        Switch switcher = (Switch)toolbar.findViewById(R.id.toolbarSwitcher);
        switcher.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabBold());

    }

    private void initDrillDownMap(){
        drillDownItemsMap.put(SETTINGS_MAIN_ID, new int[]{ NOTIFICATIONS_AND_SOUND_ITEM_ID, SHOW_MY_LOCATION_ITEM_ID, PHOTO_AND_MEDIA_ITEM_ID, CHATS_ITEM_ID, PRIVACY_ITEM_ID });
        drillDownItemsMap.put(NOTIFICATIONS_AND_SOUND_ITEM_ID, new int[]{NOTIFICATION_PREVIEW_ITEM_ID, NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID, NOTIFICATIONS_VIBRATE_ITEM_ID, NOTIFICATIONS_LIGHT_ITEM_ID, NOTIFICATIONS_SOUND_ITEM_ID});
        drillDownItemsMap.put(SHOW_MY_LOCATION_ITEM_ID, new int[]{FRIEND_ONLY_LOCATION_ITEM_ID, ALL_USER_LOCATION_ITEM_ID});
        drillDownItemsMap.put(PHOTO_AND_MEDIA_ITEM_ID, new int[]{SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID, SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID});
        drillDownItemsMap.put(CHATS_ITEM_ID, new int[]{CLEAR_ALL_CHATS_ITEM_ID, DELETE_ALL_CHATS_ITEM_ID});
        drillDownItemsMap.put(PRIVACY_ITEM_ID, new int[]{LAST_SEEN_ITEM_ID, PROFILE_PHOTO_ITEM_ID, STATUS_ITEM_ID, READ_RECEIPTS_ITEM_ID, BLOCKED_CONTACTS_ITEM_ID});
    }

    private void renderDrillDownItems(int itemId){
        currentItemId = itemId;

        changeToolbar();

        LinearLayout itemsContainer = (LinearLayout)findViewById(R.id.items_container);
        itemsContainer.removeAllViews();

        int[] items = drillDownItemsMap.get(itemId);
        SettingsItem settingsItem = null;
        for( int index = 0; index < items.length ; index ++ ){
            settingsItem = new SettingsItem(items[index]);
            itemsContainer.addView( settingsItem.getItemLayout());
        }
    }

    private void changeToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        ImageView back = (ImageView) toolbar.findViewById(R.id.backButton);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        TextView subTitle = (TextView) toolbar.findViewById(R.id.toolbarSubTitle);
        Switch switcher = (Switch)toolbar.findViewById(R.id.toolbarSwitcher);

        if( currentItemId == SETTINGS_MAIN_ID ){
            toolbar.setBackgroundColor(getResources().getColor(R.color.app_theme_blue));
            back.setImageResource(R.drawable.ic_menu_back);

            SettingsItem settingsItem = new SettingsItem(currentItemId);
            title.setText(settingsItem.getTitle());
            title.setVisibility(View.VISIBLE);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(getResources().getColor(android.R.color.white));

            switcher.setVisibility(View.GONE);
            subTitle.setVisibility(View.GONE);
        } else if( currentItemId == NOTIFICATIONS_AND_SOUND_ITEM_ID || currentItemId == SHOW_MY_LOCATION_ITEM_ID ){
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
            back.setImageResource(R.drawable.ic_menu_back_blk);

            SettingsItem settingsItem = new SettingsItem(currentItemId);
            switcher.setText(settingsItem.getTitle());
            switcher.setVisibility(View.VISIBLE);
            switcher.setTag(currentItemId);
            switcher.setChecked(settingsItem.getCheckedValue());
            switcher.setOnCheckedChangeListener(itemEventListener);

            title.setVisibility(View.GONE);
            subTitle.setVisibility(View.GONE);
        } else {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
            back.setImageResource(R.drawable.ic_menu_back_blk);

            SettingsItem settingsItem = new SettingsItem(currentItemId);
            subTitle.setText(settingsItem.getTitle());
            subTitle.setVisibility(View.VISIBLE);

            switcher.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        }

    }

    private void changeAllRadioButtons(boolean checked){
        LinearLayout settingItemParentLayout = (LinearLayout)findViewById(R.id.items_container);
        int childCount = settingItemParentLayout.getChildCount();

        ViewGroup item = null;
        CheckBox checkBox = null;
        for( int index=0; index < childCount ; index++ ){
            item = (ViewGroup)settingItemParentLayout.getChildAt(index);
            checkBox = (CheckBox)item.findViewById(R.id.radio);
            if( checkBox.getVisibility() == View.VISIBLE ){
                checkBox.setChecked(checked);
            }
        }

    }

    private ArrayList<ToneItem> listRingTones() {
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);

        ArrayList<ToneItem> toneItems = new ArrayList<>();
        Cursor cursor = manager.getCursor();
        if( cursor != null ) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);

                ToneItem toneItem = new ToneItem( title, uri);
                toneItems.add(toneItem);
            }
        } else {
            //nothing
        }

        return toneItems;
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

    private class ToneItem {

        private String title = null;
        private String uri = null;

        private ToneItem(String title, String uri){
            this.title = title;
            this.uri = uri;
        }

        public String getTitle() {
            return title;
        }

        public String getUri() {
            return uri;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

    }

    private class ItemEventListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        @Override
        public void onClick(View view) {
            int itemId = (Integer)view.getTag();
            int itemType = (Integer)view.getTag(R.layout.settings_item);

            if( itemType == SettingsItem.ITEM_TYPE_DRILL_DOWN && drillDownItemsMap.containsKey(itemId) ){
                renderDrillDownItems(itemId);
            } else if( itemType == SettingsItem.ITEM_TYPE_RADIO ) {
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.radio);
                checkBox.setChecked(!checkBox.isChecked());
            } else if( itemType == SettingsItem.ITEM_TYPE_CLICK ) {
                if( itemId == CLEAR_ALL_CHATS_ITEM_ID ){
                    clearAllChat();
                } else if( itemId == DELETE_ALL_CHATS_ITEM_ID ){
                    deleteAllChat();
                } else if( itemId == BLOCKED_CONTACTS_ITEM_ID ){
                    //TODO
                }
            } else if( itemType == SettingsItem.ITEM_TYPE_POPUP ) {
                if( itemId == NOTIFICATIONS_SOUND_ITEM_ID ){
                    //TODO
                } else if( itemId == LAST_SEEN_ITEM_ID ){
                    //TODO
                } else if( itemId == PROFILE_PHOTO_ITEM_ID ){
                    //TODO
                } else if( itemId == STATUS_ITEM_ID ){
                    //TODO
                }
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int itemId = (Integer)buttonView.getTag();
            if ( itemId == NOTIFICATIONS_AND_SOUND_ITEM_ID ) {
                UserUtil.setNotificationAndSound(SettingsActivity.this, isChecked);
                changeAllRadioButtons(isChecked);
            } else if ( itemId == NOTIFICATION_PREVIEW_ITEM_ID ) {
                UserUtil.setNotificationPreviews(SettingsActivity.this, isChecked);
            } else if ( itemId == NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID ) {
                UserUtil.setConversationTones(SettingsActivity.this, isChecked);
            } else if ( itemId == NOTIFICATIONS_VIBRATE_ITEM_ID ) {
                UserUtil.setConversationVibrate(SettingsActivity.this, isChecked);
            } else if ( itemId == NOTIFICATIONS_LIGHT_ITEM_ID ) {
                UserUtil.setNotificationLight(SettingsActivity.this, isChecked);
            } else if ( itemId == NOTIFICATIONS_SOUND_ITEM_ID ) {
                UserUtil.setNotificationSound(SettingsActivity.this, isChecked);
            } else if ( itemId == SHOW_MY_LOCATION_ITEM_ID ) {
                UserUtil.setShowMyLocation(SettingsActivity.this, isChecked);
                changeAllRadioButtons(isChecked);
            } else if ( itemId == FRIEND_ONLY_LOCATION_ITEM_ID ) {
                UserUtil.setShowToFriendsLocation(SettingsActivity.this, isChecked);
            } else if ( itemId == ALL_USER_LOCATION_ITEM_ID ) {
                UserUtil.setShowToAllLocation(SettingsActivity.this, isChecked);
            } else if ( itemId == SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID ) {
                UserUtil.setSaveIncomingMediaToGallery(SettingsActivity.this, isChecked);
            } else if ( itemId == SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID) {
                UserUtil.setSaveInAppCaptureMediaToGallery(SettingsActivity.this, isChecked);
            }
        }
    }

    private class SettingsItem {

        private static final int ITEM_TYPE_DRILL_DOWN = 1;
        private static final int ITEM_TYPE_RADIO = 2;
        private static final int ITEM_TYPE_CLICK = 3;
        private static final int ITEM_TYPE_POPUP = 4;

        private static final int ITEM__WITH_NO_ICON = -1;

        private int id = 0;
        private int iconResId = 0;

        private String title = null;
        private String subTitle = null;
        private int itemType = 0;

        private SettingsItem(int id){
            this.id = id;

            this.title = getTitle();
            this.subTitle = getSubTitle();

            this.itemType = getItemType();
            this.iconResId = getItemIcon();
        }

        private ViewGroup getItemLayout(){
            ViewGroup viewGroup = (ViewGroup)LayoutInflater.from(SettingsActivity.this).inflate(R.layout.settings_item, null);

            ViewGroup clickableLayout = (ViewGroup)viewGroup.findViewById(R.id.clickableLayout);
            clickableLayout.setTag(id);
            clickableLayout.setTag(R.layout.settings_item, itemType);
            clickableLayout.setOnClickListener(itemEventListener);

            if( iconResId != ITEM__WITH_NO_ICON ){
                ImageView icon = (ImageView)viewGroup.findViewById(R.id.leftIcon);
                icon.setImageResource(iconResId);
                icon.setVisibility(View.VISIBLE);
            } else {
                //nothing
            }

            TextView titleTextView = (TextView)clickableLayout.findViewById(R.id.title);
            titleTextView.setText(title);
            titleTextView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());

            TextView subtitleTextView = (TextView)clickableLayout.findViewById(R.id.subTitle);
            if( subTitle != null && !subTitle.isEmpty() ) {
                subtitleTextView.setText(subTitle);
                subtitleTextView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
            } else {
                subtitleTextView.setVisibility(View.GONE);
            }

            if( itemType == ITEM_TYPE_DRILL_DOWN ){
                clickableLayout.findViewById(R.id.nextIcon).setVisibility(View.VISIBLE);
            } else if( itemType == ITEM_TYPE_CLICK ){

            } else if( itemType == ITEM_TYPE_RADIO ){
                CheckBox checkBox = (CheckBox)clickableLayout.findViewById(R.id.radio);
                checkBox.setChecked(getCheckedValue());
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setTag(id);
                checkBox.setOnCheckedChangeListener(itemEventListener);
            }

            return viewGroup;
        }

        private String getTitle(){
            String title = null;
            if( id == SETTINGS_MAIN_ID ){
                title = getResources().getString(R.string.settings);
            } else if( id == NOTIFICATIONS_AND_SOUND_ITEM_ID ){
                title = getResources().getString(R.string.notification_and_sound_options);
            } else if( id == NOTIFICATION_PREVIEW_ITEM_ID ){
                title = getResources().getString(R.string.notification_previews);
            } else if( id == NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID ){
                title = getResources().getString(R.string.conversation_tones);
            } else if( id == NOTIFICATIONS_VIBRATE_ITEM_ID ){
                title = getResources().getString(R.string.notification_vibrate);
            } else if( id == NOTIFICATIONS_LIGHT_ITEM_ID ){
                title = getResources().getString(R.string.notification_light);
            } else if( id == NOTIFICATIONS_SOUND_ITEM_ID ){
                title = getResources().getString(R.string.notification_sound);
            } else if( id == SHOW_MY_LOCATION_ITEM_ID ){
                title = getResources().getString(R.string.show_my_location);
            } else if( id == FRIEND_ONLY_LOCATION_ITEM_ID ){
                title = getResources().getString(R.string.friends_only);
            } else if( id == ALL_USER_LOCATION_ITEM_ID ){
                title = getResources().getString(R.string.show_to_all);
            } else if( id == PHOTO_AND_MEDIA_ITEM_ID ){
                title = getResources().getString(R.string.photos_and_media);
            } else if( id == SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID ){
                title = getResources().getString(R.string.save_photo);
            } else if( id == SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID ){
                title = getResources().getString(R.string.save_on_capture);
            } else if( id == CHATS_ITEM_ID ){
                title = getResources().getString(R.string.chats);
            } else if( id == CLEAR_ALL_CHATS_ITEM_ID ){
                title = getResources().getString(R.string.clear_all_chats);
            } else if( id ==  DELETE_ALL_CHATS_ITEM_ID){
                title = getResources().getString(R.string.delete_all_chats);
            } else if( id == PRIVACY_ITEM_ID ){
                title = getResources().getString(R.string.privacy);
            } else if( id == LAST_SEEN_ITEM_ID ){
                title = getResources().getString(R.string.last_seen);
            } else if( id == PROFILE_PHOTO_ITEM_ID ){
                title = getResources().getString(R.string.profile_photo);
            } else if( id == STATUS_ITEM_ID ){
                title = getResources().getString(R.string.status);
            } else if( id == READ_RECEIPTS_ITEM_ID ){
                title = getResources().getString(R.string.read_receipts);
            } else if( id == BLOCKED_CONTACTS_ITEM_ID ){
                title = getResources().getString(R.string.blocked_contacts);
            }
            return title;
        }

        private String getSubTitle(){
            String subTitle = null;
            if( id == SETTINGS_MAIN_ID ){
                subTitle = getResources().getString(R.string.settings);
            } else if( id == NOTIFICATIONS_AND_SOUND_ITEM_ID ){
                subTitle = UserUtil.isNotificationAndSound() ? "ON" : "OFF";
            } else if( id == NOTIFICATION_PREVIEW_ITEM_ID ){
                subTitle = getResources().getString(R.string.show_name_message);
            } else if( id == NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID ){
                subTitle = getResources().getString(R.string.play_sound_for_messages);
            } else if( id == NOTIFICATIONS_VIBRATE_ITEM_ID ){
                subTitle = "";
            } else if( id == NOTIFICATIONS_LIGHT_ITEM_ID ){
                subTitle = "";
            } else if( id == NOTIFICATIONS_SOUND_ITEM_ID ){
                subTitle = getResources().getString(R.string.notification_ping);
            } else if( id == SHOW_MY_LOCATION_ITEM_ID ){
                subTitle = UserUtil.isShowMyLocation() ? "ON" : "OFF";
            } else if( id == FRIEND_ONLY_LOCATION_ITEM_ID ){
                subTitle = getResources().getString(R.string.show_location_to_friends);
            } else if( id == ALL_USER_LOCATION_ITEM_ID ){
                subTitle = getResources().getString(R.string.show_location_to_spu_users);
            } else if( id == PHOTO_AND_MEDIA_ITEM_ID ){
                subTitle = "";
            } else if( id == SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID ){
                subTitle = getResources().getString(R.string.save_photos_to_gallery);
            } else if( id == SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID ){
                subTitle = getResources().getString(R.string.save_new_photo_captured_in_gallery);
            } else if( id == CHATS_ITEM_ID ){
                subTitle = "";
            } else if( id == CLEAR_ALL_CHATS_ITEM_ID ){
                subTitle = "";
            } else if( id ==  DELETE_ALL_CHATS_ITEM_ID){
                subTitle = "";
            } else if( id == PRIVACY_ITEM_ID ){
                subTitle = "";
            } else if( id == LAST_SEEN_ITEM_ID ){
                subTitle = getResources().getString(R.string.my_contacts);
            } else if( id == PROFILE_PHOTO_ITEM_ID ){
                subTitle = getResources().getString(R.string.my_contacts);
            } else if( id == STATUS_ITEM_ID ){
                subTitle = getResources().getString(R.string.my_contacts);
            } else if( id == READ_RECEIPTS_ITEM_ID ){
                subTitle = "";
            } else if( id == BLOCKED_CONTACTS_ITEM_ID ){
                subTitle = getResources().getString(R.string.list_of_blocked_contacts);
            }
            return subTitle;
        }

        private int getItemType(){
            int itemType = 0;
            if( id == SETTINGS_MAIN_ID ){
                itemType = ITEM_TYPE_DRILL_DOWN;
            } else if( id == NOTIFICATIONS_AND_SOUND_ITEM_ID ){
                itemType = ITEM_TYPE_DRILL_DOWN;
            } else if( id == NOTIFICATION_PREVIEW_ITEM_ID ){
                itemType = ITEM_TYPE_RADIO;
            } else if( id == NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID ){
                itemType = ITEM_TYPE_RADIO;
            } else if( id == NOTIFICATIONS_VIBRATE_ITEM_ID ){
                itemType = ITEM_TYPE_RADIO;
            } else if( id == NOTIFICATIONS_LIGHT_ITEM_ID ){
                itemType = ITEM_TYPE_RADIO;
            } else if( id == NOTIFICATIONS_SOUND_ITEM_ID ){
                itemType = ITEM_TYPE_POPUP;
            } else if( id == SHOW_MY_LOCATION_ITEM_ID ){
                itemType = ITEM_TYPE_DRILL_DOWN;
            } else if( id == FRIEND_ONLY_LOCATION_ITEM_ID ){
                itemType = ITEM_TYPE_RADIO;
            } else if( id == ALL_USER_LOCATION_ITEM_ID ){
                itemType = ITEM_TYPE_RADIO;
            } else if( id == PHOTO_AND_MEDIA_ITEM_ID ){
                itemType = ITEM_TYPE_DRILL_DOWN;
            } else if( id == SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID ){
                itemType = ITEM_TYPE_RADIO;
            } else if( id == SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID ){
                itemType = ITEM_TYPE_RADIO;
            } else if( id == CHATS_ITEM_ID ){
                itemType = ITEM_TYPE_DRILL_DOWN;
            } else if( id == CLEAR_ALL_CHATS_ITEM_ID ){
                itemType = ITEM_TYPE_CLICK;
            } else if( id ==  DELETE_ALL_CHATS_ITEM_ID){
                itemType = ITEM_TYPE_CLICK;
            } else if( id == PRIVACY_ITEM_ID ){
                itemType = ITEM_TYPE_DRILL_DOWN;
            } else if( id == LAST_SEEN_ITEM_ID ){
                itemType = ITEM_TYPE_POPUP;
            } else if( id == PROFILE_PHOTO_ITEM_ID ){
                itemType = ITEM_TYPE_POPUP;
            } else if( id == STATUS_ITEM_ID ){
                itemType = ITEM_TYPE_POPUP;
            } else if( id == READ_RECEIPTS_ITEM_ID ){
                itemType = ITEM_TYPE_RADIO;
            } else if( id == BLOCKED_CONTACTS_ITEM_ID ){
                itemType = ITEM_TYPE_CLICK;
            }
            return itemType;
        }

        private int getItemIcon(){
            int icon = ITEM__WITH_NO_ICON;

            if( id == NOTIFICATIONS_AND_SOUND_ITEM_ID ){
                icon = R.drawable.ic_settings_notification;
            } else if( id == SHOW_MY_LOCATION_ITEM_ID ){
                icon = R.drawable.ic_show_my_location;
            } else if( id == PHOTO_AND_MEDIA_ITEM_ID ){
                icon = R.drawable.ic_media;
            } else if( id == CHATS_ITEM_ID ){
                icon = R.drawable.ic_chats;
            } else if( id == PRIVACY_ITEM_ID ){
                icon = R.drawable.privacy;
            }

            return icon;
        }

        private boolean getCheckedValue(){
            boolean checked = false;
            if( id == NOTIFICATIONS_AND_SOUND_ITEM_ID ){
                checked = UserUtil.isNotificationAndSound();
            } else if( id == NOTIFICATION_PREVIEW_ITEM_ID ){
                checked = UserUtil.isNotificationPreviews();
            } else if( id == NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID ){
                checked = UserUtil.isConversationTones();
            } else if( id == NOTIFICATIONS_VIBRATE_ITEM_ID ){
                checked = UserUtil.isConversationVibrate();
            } else if( id == NOTIFICATIONS_LIGHT_ITEM_ID ){
                checked = UserUtil.isNotificationLight();
            } else if( id == SHOW_MY_LOCATION_ITEM_ID ){
                checked = UserUtil.isShowMyLocation();
            } else if( id == FRIEND_ONLY_LOCATION_ITEM_ID ){
                checked = UserUtil.isShowToFriendsLocation();
            } else if( id == ALL_USER_LOCATION_ITEM_ID ){
                checked = UserUtil.isShowToAllLocation();
            } else if( id == SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID ){
                checked = UserUtil.isSaveIncomingMediaToGallery();
            } else if( id == SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID ){
                checked = UserUtil.isSaveInAppCaptureMediaToGallery();
            } else if( id == READ_RECEIPTS_ITEM_ID ){
                checked = UserUtil.isReadReceipts();
            }
            return checked;
        }

    }

}
