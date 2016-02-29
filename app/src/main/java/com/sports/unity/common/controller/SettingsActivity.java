package com.sports.unity.common.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.model.SettingsHelper;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.NotificationHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private HashMap<Integer, int[]> drillDownItemsMap = new HashMap<>();
    private int currentItemId = SettingsHelper.SETTINGS_MAIN_ID;

    private ItemEventListener itemEventListener = new ItemEventListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        SettingsHelper.initDrillDownMap(drillDownItemsMap);

        initToolbar();
        renderDrillDownItems(currentItemId);
    }

    @Override
    public void onBackPressed() {
        if (currentItemId == SettingsHelper.SETTINGS_MAIN_ID) {
            super.onBackPressed();
        } else {
            renderDrillDownItems(SettingsHelper.SETTINGS_MAIN_ID);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        ImageView back = (ImageView) toolbar.findViewById(R.id.backButton);
        back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }

        });

        TextView title = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        title.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabBold());

        TextView subtitle = (TextView) toolbar.findViewById(R.id.toolbarSubTitle);
        subtitle.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabBold());

        Switch switcher = (Switch) toolbar.findViewById(R.id.toolbarSwitcher);
        switcher.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabBold());

    }

    private void renderDrillDownItems(int itemId) {
        currentItemId = itemId;

        changeToolbar();

        LinearLayout itemsContainer = (LinearLayout) findViewById(R.id.items_container);
        itemsContainer.removeAllViews();

        int[] items = drillDownItemsMap.get(itemId);
        SettingsItem settingsItem = null;
        for (int index = 0; index < items.length; index++) {
            settingsItem = new SettingsItem(items[index]);
            itemsContainer.addView(settingsItem.getItemLayout());
        }
    }

    private void changeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        ImageView back = (ImageView) toolbar.findViewById(R.id.backButton);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        TextView subTitle = (TextView) toolbar.findViewById(R.id.toolbarSubTitle);
        Switch switcher = (Switch) toolbar.findViewById(R.id.toolbarSwitcher);

        if (currentItemId == SettingsHelper.SETTINGS_MAIN_ID) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.app_theme_blue));
            back.setImageResource(R.drawable.ic_menu_back);
            back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_BLUE, true));

            title.setText(SettingsHelper.getTitle(currentItemId, this));
            title.setVisibility(View.VISIBLE);
            title.setGravity(Gravity.CENTER);
            title.setTextColor(getResources().getColor(android.R.color.white));

            switcher.setVisibility(View.GONE);
            subTitle.setVisibility(View.GONE);
        } else if (currentItemId == SettingsHelper.NOTIFICATIONS_AND_SOUND_ITEM_ID || currentItemId == SettingsHelper.SHOW_MY_LOCATION_ITEM_ID) {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
            back.setImageResource(R.drawable.ic_menu_back_blk);
            back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, true));

            switcher.setText(SettingsHelper.getTitle(currentItemId, this));
            switcher.setVisibility(View.VISIBLE);
            switcher.setTag(currentItemId);
            switcher.setChecked(SettingsHelper.getCheckedValue(currentItemId));
            switcher.setOnCheckedChangeListener(itemEventListener);

            title.setVisibility(View.GONE);
            subTitle.setVisibility(View.GONE);
        } else {
            toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));
            back.setImageResource(R.drawable.ic_menu_back_blk);
            back.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, true));

            subTitle.setText(SettingsHelper.getTitle(currentItemId, this));
            subTitle.setVisibility(View.VISIBLE);

            switcher.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        }

    }

    private void changeAllRadioButtons(boolean checked) {
        LinearLayout settingItemParentLayout = (LinearLayout) findViewById(R.id.items_container);
        int childCount = settingItemParentLayout.getChildCount();

        ViewGroup item = null;
        CheckBox checkBox = null;
        for (int index = 0; index < childCount; index++) {
            item = (ViewGroup) settingItemParentLayout.getChildAt(index);
            checkBox = (CheckBox) item.findViewById(R.id.radio);
            if (checkBox.getVisibility() == View.VISIBLE) {
                checkBox.setChecked(checked);
            }
        }

    }

    private TextView getSubTitleView(int itemId) {
        TextView view = null;
        LinearLayout itemsContainer = (LinearLayout) findViewById(R.id.items_container);

        int[] items = drillDownItemsMap.get(currentItemId);
        int matchIndex = -1;
        for (int index = 0; index < items.length; index++) {
            if (items[index] == itemId) {
                matchIndex = index;
                break;
            }
        }

        if (matchIndex != -1) {
            ViewGroup settingsItemLayout = (ViewGroup) itemsContainer.getChildAt(matchIndex);
            if (settingsItemLayout != null) {
                view = (TextView) settingsItemLayout.findViewById(R.id.subTitle);
            }
        }

        return view;
    }

    private void updateViewBasedOnPopupSelection(int itemId) {
        if (itemId == SettingsHelper.NOTIFICATIONS_SOUND_ITEM_ID) {
            TextView view = getSubTitleView(itemId);
            view.setText(UserUtil.getNotificationSoundTitle());
        } else if (itemId == SettingsHelper.LAST_SEEN_ITEM_ID) {
            TextView view = getSubTitleView(itemId);
            view.setText(SettingsHelper.getSubTitle(itemId, SettingsActivity.this));
        } else if (itemId == SettingsHelper.PROFILE_PHOTO_ITEM_ID) {
            TextView view = getSubTitleView(itemId);
            view.setText(SettingsHelper.getSubTitle(itemId, SettingsActivity.this));
        } else if (itemId == SettingsHelper.STATUS_ITEM_ID) {
            TextView view = getSubTitleView(itemId);
            view.setText(SettingsHelper.getSubTitle(itemId, SettingsActivity.this));
        } else if (itemId == SettingsHelper.MEDIA_USING_MOBILE_DATA_ITEM_ID) {
            TextView view = getSubTitleView(itemId);
            view.setText(SettingsHelper.getMediaListingForMobileData());
        } else if (itemId == SettingsHelper.MEDIA_WHEN_CONNECTED_TO_WIFI_ITEM_ID) {
            TextView view = getSubTitleView(itemId);
            view.setText(SettingsHelper.getMediaListingForWifi());
        }
    }

    private ArrayList<ToneItem> listRingTones() {
        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_NOTIFICATION);

        ArrayList<ToneItem> toneItems = new ArrayList<>();
        toneItems.add(new ToneItem( "None", "/"));
        Cursor cursor = manager.getCursor();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
                String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);

                ToneItem toneItem = new ToneItem( title, uri + "/" + id);
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
            NotificationHandler.getInstance(getApplicationContext()).clearNotificationMessages(String.valueOf(chatObject.chatid));
        }
    }

    private void showNotificationToneDialog(final int itemId, String title) {
        final ArrayList<ToneItem> toneItems = listRingTones();

        String[] options = new String[toneItems.size()];
        for (int index = 0; index < toneItems.size(); index++) {
            options[index] = toneItems.get(index).title;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(title)

                .setItems(options, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        ToneItem selectedTone = toneItems.get(which);
                        itemEventListener.onSoundSelection(itemId, selectedTone.title, selectedTone.uri);
                    }

                });

        builder.create().show();
    }

    private class ToneItem {

        private String title = null;
        private String uri = null;

        private ToneItem(String title, String uri) {
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
            int itemId = (Integer) view.getTag();
            int itemType = (Integer) view.getTag(R.layout.settings_item);

            if (itemType == SettingsHelper.ITEM_TYPE_DRILL_DOWN) {
                renderDrillDownItems(itemId);
            } else if (itemType == SettingsHelper.ITEM_TYPE_RADIO) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.radio);
                checkBox.setChecked(!checkBox.isChecked());
            } else if (itemType == SettingsHelper.ITEM_TYPE_CLICK) {
                if (itemId == SettingsHelper.CLEAR_ALL_CHATS_ITEM_ID) {
                    ConfirmationAlertDialog confirmationAlertDialog = new ConfirmationAlertDialog(itemId, getResources().getString(R.string.clear_all_chats_confirm_message), getResources().getString(R.string.clear));
                    confirmationAlertDialog.show();
                } else if (itemId == SettingsHelper.DELETE_ALL_CHATS_ITEM_ID) {
                    ConfirmationAlertDialog confirmationAlertDialog = new ConfirmationAlertDialog(itemId, getResources().getString(R.string.delete_all_chats_confirm_message), getResources().getString(R.string.ok));
                    confirmationAlertDialog.show();
                } else if (itemId == SettingsHelper.BLOCKED_CONTACTS_ITEM_ID) {
                    //TODO
                }
            } else if (itemType == SettingsHelper.ITEM_TYPE_POPUP) {
                if (itemId == SettingsHelper.NOTIFICATIONS_SOUND_ITEM_ID) {
                    showNotificationToneDialog(itemId, SettingsHelper.getTitle(itemId, SettingsActivity.this));
                } else if (itemId == SettingsHelper.LAST_SEEN_ITEM_ID) {
                    SingleSelectionAlertDialog alertDialog = new SingleSelectionAlertDialog(itemId, SettingsHelper.getTitle(itemId, SettingsActivity.this),
                            new int[]{SettingsHelper.EVERY_ONE_ITEM_ID, SettingsHelper.ONLY_FRIENDS_ITEM_ID, SettingsHelper.NOBODY_ITEM_ID}, UserUtil.getPrivacyLastSeen());
                    alertDialog.show();
                } else if (itemId == SettingsHelper.PROFILE_PHOTO_ITEM_ID) {
                    SingleSelectionAlertDialog alertDialog = new SingleSelectionAlertDialog(itemId, SettingsHelper.getTitle(itemId, SettingsActivity.this),
                            new int[]{SettingsHelper.EVERY_ONE_ITEM_ID, SettingsHelper.ONLY_FRIENDS_ITEM_ID, SettingsHelper.NOBODY_ITEM_ID}, UserUtil.getPrivacyProfilePhoto());
                    alertDialog.show();
                } else if (itemId == SettingsHelper.STATUS_ITEM_ID) {
                    SingleSelectionAlertDialog alertDialog = new SingleSelectionAlertDialog(itemId, SettingsHelper.getTitle(itemId, SettingsActivity.this),
                            new int[]{SettingsHelper.EVERY_ONE_ITEM_ID, SettingsHelper.ONLY_FRIENDS_ITEM_ID, SettingsHelper.NOBODY_ITEM_ID}, UserUtil.getPrivacyStatus());
                    alertDialog.show();
                } else if (itemId == SettingsHelper.MEDIA_USING_MOBILE_DATA_ITEM_ID) {
                    boolean checked[] = new boolean[]{UserUtil.isMediaEnabledUsingMobileData(UserUtil.IMAGE_MEDIA), UserUtil.isMediaEnabledUsingMobileData(UserUtil.AUDIO_MEDIA), UserUtil.isMediaEnabledUsingMobileData(UserUtil.VIDEO_MEDIA)};
                    ListingAlertDialog listingAlertDialog = new ListingAlertDialog(itemId, SettingsHelper.getTitle(itemId, SettingsActivity.this), drillDownItemsMap.get(itemId), checked);
                    listingAlertDialog.show(SettingsActivity.this);
                } else if (itemId == SettingsHelper.MEDIA_WHEN_CONNECTED_TO_WIFI_ITEM_ID) {
                    boolean checked[] = new boolean[]{UserUtil.isMediaEnabledUsingWIFI(UserUtil.IMAGE_MEDIA), UserUtil.isMediaEnabledUsingWIFI(UserUtil.AUDIO_MEDIA), UserUtil.isMediaEnabledUsingWIFI(UserUtil.VIDEO_MEDIA)};
                    ListingAlertDialog listingAlertDialog = new ListingAlertDialog(itemId, SettingsHelper.getTitle(itemId, SettingsActivity.this), drillDownItemsMap.get(itemId), checked);
                    listingAlertDialog.show(SettingsActivity.this);
                }
            }
        }

        public void onSingleSelection(int itemId, int selection) {
            if (itemId == SettingsHelper.LAST_SEEN_ITEM_ID) {
                int value = SettingsHelper.getPopUpItemValue(selection);
                UserUtil.setPrivacyLastSeen(SettingsActivity.this, value);
                updateViewBasedOnPopupSelection(itemId);
            } else if (itemId == SettingsHelper.PROFILE_PHOTO_ITEM_ID) {
                int value = SettingsHelper.getPopUpItemValue(selection);
                UserUtil.setPrivacyProfilePhoto(SettingsActivity.this, value);
                updateViewBasedOnPopupSelection(itemId);
            } else if (itemId == SettingsHelper.STATUS_ITEM_ID) {
                int value = SettingsHelper.getPopUpItemValue(selection);
                UserUtil.setPrivacyStatus(SettingsActivity.this, value);
                updateViewBasedOnPopupSelection(itemId);
            }
        }

        public void onSoundSelection(int itemId, String soundTitle, String soundUri) {
            if (itemId == SettingsHelper.NOTIFICATIONS_SOUND_ITEM_ID) {
                UserUtil.setNotificationSoundTitle(SettingsActivity.this, soundTitle);
                UserUtil.setNotificationSoundURI(SettingsActivity.this, soundUri);

                updateViewBasedOnPopupSelection(itemId);
            } else {

            }
        }

        public void onMultipleSelection(int itemId, ArrayList<Integer> selectedOnes) {
            if (itemId == SettingsHelper.MEDIA_USING_MOBILE_DATA_ITEM_ID) {
                int value = SettingsHelper.getPopUpMediaItemValue(selectedOnes);
                UserUtil.setMediaUsingMobileData(SettingsActivity.this, value);
                updateViewBasedOnPopupSelection(itemId);
            } else if (itemId == SettingsHelper.MEDIA_WHEN_CONNECTED_TO_WIFI_ITEM_ID) {
                int value = SettingsHelper.getPopUpMediaItemValue(selectedOnes);
                UserUtil.setMediaUsingWIFI(SettingsActivity.this, value);
                updateViewBasedOnPopupSelection(itemId);
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int itemId = (Integer) buttonView.getTag();
            if (itemId == SettingsHelper.NOTIFICATIONS_AND_SOUND_ITEM_ID) {
                UserUtil.setNotificationAndSound(SettingsActivity.this, isChecked);
                changeAllRadioButtons(isChecked);
            } else if (itemId == SettingsHelper.NOTIFICATION_PREVIEW_ITEM_ID) {
                UserUtil.setNotificationPreviews(SettingsActivity.this, isChecked);
            } else if (itemId == SettingsHelper.NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID) {
                UserUtil.setConversationTones(SettingsActivity.this, isChecked);
            } else if (itemId == SettingsHelper.NOTIFICATIONS_VIBRATE_ITEM_ID) {
                UserUtil.setConversationVibrate(SettingsActivity.this, isChecked);
            } else if (itemId == SettingsHelper.NOTIFICATIONS_LIGHT_ITEM_ID) {
                UserUtil.setNotificationLight(SettingsActivity.this, isChecked);
            } else if (itemId == SettingsHelper.SHOW_MY_LOCATION_ITEM_ID) {
                UserUtil.setShowMyLocation(SettingsActivity.this, isChecked);
                changeAllRadioButtons(isChecked);
            } else if (itemId == SettingsHelper.FRIEND_ONLY_LOCATION_ITEM_ID) {
                UserUtil.setShowToFriendsLocation(SettingsActivity.this, isChecked);
            } else if (itemId == SettingsHelper.ALL_USER_LOCATION_ITEM_ID) {
                UserUtil.setShowToAllLocation(SettingsActivity.this, isChecked);
            } else if (itemId == SettingsHelper.SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID) {
                UserUtil.setSaveIncomingMediaToGallery(SettingsActivity.this, isChecked);
            } else if (itemId == SettingsHelper.SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID) {
                UserUtil.setSaveInAppCaptureMediaToGallery(SettingsActivity.this, isChecked);
            }
        }
    }

    private class SettingsItem {

        private int id = 0;
        private int iconResId = 0;

        private String title = null;
        private String subTitle = null;
        private int itemType = 0;

        private SettingsItem(int id) {
            this.id = id;

            this.title = SettingsHelper.getTitle(this.id, SettingsActivity.this);
            this.subTitle = SettingsHelper.getSubTitle(this.id, SettingsActivity.this);

            this.itemType = SettingsHelper.getItemType(this.id);
            this.iconResId = SettingsHelper.getItemIcon(this.id);
        }

        private ViewGroup getItemLayout() {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(SettingsActivity.this).inflate(R.layout.settings_item, null);

            ViewGroup clickableLayout = (ViewGroup) viewGroup.findViewById(R.id.clickableLayout);
            clickableLayout.setTag(id);
            clickableLayout.setTag(R.layout.settings_item, itemType);
            clickableLayout.setOnClickListener(itemEventListener);
            clickableLayout.setBackgroundResource(CommonUtil.getDrawable(Constants.COLOR_WHITE, false));

            if (iconResId != SettingsHelper.ITEM__WITH_NO_ICON) {
                ImageView icon = (ImageView) viewGroup.findViewById(R.id.leftIcon);
                icon.setImageResource(iconResId);
                icon.setVisibility(View.VISIBLE);
            } else {
                //nothing
            }

            TextView titleTextView = (TextView) clickableLayout.findViewById(R.id.title);
            titleTextView.setText(title);
            if (currentItemId == SettingsHelper.SETTINGS_MAIN_ID) {
                titleTextView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoSlabRegular());
            } else {
                titleTextView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoRegular());
            }

            TextView subtitleTextView = (TextView) clickableLayout.findViewById(R.id.subTitle);
            if (subTitle != null && !subTitle.isEmpty()) {
                subtitleTextView.setText(subTitle);
                subtitleTextView.setTypeface(FontTypeface.getInstance(getApplicationContext()).getRobotoCondensedRegular());
            } else {
                subtitleTextView.setVisibility(View.GONE);
            }

            if (itemType == SettingsHelper.ITEM_TYPE_DRILL_DOWN) {
                clickableLayout.findViewById(R.id.nextIcon).setVisibility(View.VISIBLE);
            } else if (itemType == SettingsHelper.ITEM_TYPE_CLICK) {

            } else if (itemType == SettingsHelper.ITEM_TYPE_RADIO) {
                CheckBox checkBox = (CheckBox) clickableLayout.findViewById(R.id.radio);
                checkBox.setChecked(SettingsHelper.getCheckedValue(id));
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setTag(id);
                checkBox.setOnCheckedChangeListener(itemEventListener);
            }

            return viewGroup;
        }

    }

    public class SingleSelectionAlertDialog {

        private int itemId = 0;
        private String dialogTitle = null;
        private int[] optionsItem = null;

        private int selectedItem = 0;

        public SingleSelectionAlertDialog(int itemId, String dialogTitle, int[] optionsItem, int selectedItem) {
            this.itemId = itemId;
            this.dialogTitle = dialogTitle;
            this.optionsItem = optionsItem;

            this.selectedItem = selectedItem;
        }

        private void show() {

            String[] options = new String[optionsItem.length];
            for (int index = 0; index < optionsItem.length; index++) {
                options[index] = SettingsHelper.getPopUpTitle(optionsItem[index], SettingsActivity.this);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle(dialogTitle)

                    .setItems(SettingsHelper.getPopUpOptions(itemId, SettingsActivity.this), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            int item = optionsItem[which];
                            itemEventListener.onSingleSelection(itemId, item);
                        }

                    });

            builder.create().show();
        }

    }

    public class ConfirmationAlertDialog {

        private int itemId = 0;
        private String dialogTitle = null;
        private String okButtonTitle = null;

        public ConfirmationAlertDialog(int itemId, String dialogTitle, String okButtonTitle) {
            this.itemId = itemId;
            this.dialogTitle = dialogTitle;
            this.okButtonTitle = okButtonTitle;
        }

        private void show() {
            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle(dialogTitle);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }

            });

            builder.setPositiveButton(okButtonTitle, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (itemId == SettingsHelper.CLEAR_ALL_CHATS_ITEM_ID) {
                        clearAllChat();
                    } else if (itemId == SettingsHelper.DELETE_ALL_CHATS_ITEM_ID) {
                        deleteAllChat();
                    }

                }

            });

            builder.create().show();
        }

    }

    public class ListingAlertDialog implements DialogInterface.OnMultiChoiceClickListener {

        private int itemId = 0;
        private String dialogTitle = null;
        private int[] optionsItem = null;

        private boolean[] checkedItems = null;

        public ListingAlertDialog(int itemId, String dialogTitle, int[] optionsItem, boolean[] checkedItems) {
            this.itemId = itemId;
            this.dialogTitle = dialogTitle;
            this.optionsItem = optionsItem;
            this.checkedItems = checkedItems;
        }

        public void show(Activity activity) {
            String[] options = new String[optionsItem.length];
            for (int index = 0; index < options.length; index++) {
                options[index] = SettingsHelper.getPopUpTitle(optionsItem[index], SettingsActivity.this);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(dialogTitle);
            builder.setMultiChoiceItems(options, checkedItems, this);

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }

            });

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (itemEventListener != null) {
                        ArrayList<Integer> selectedItems = new ArrayList<>();
                        for (int index = 0; index < checkedItems.length; index++) {
                            if (checkedItems[index] == true) {
                                selectedItems.add(optionsItem[index]);
                            }
                        }
                        itemEventListener.onMultipleSelection(itemId, selectedItems);
                    }
                }

            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }

        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            checkedItems[which] = isChecked;
        }
    }

}
