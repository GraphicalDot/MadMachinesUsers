package com.sports.unity.common.model;

import android.content.Context;

import com.sports.unity.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amandeep on 15/2/16.
 */
public class SettingsHelper {

    public static final int SETTINGS_MAIN_ID = 0;

    public static final int NOTIFICATIONS_AND_SOUND_ITEM_ID = 1;
    public static final int NOTIFICATION_PREVIEW_ITEM_ID = 2;
    public static final int NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID = 3;
    public static final int NOTIFICATIONS_VIBRATE_ITEM_ID = 4;
    public static final int NOTIFICATIONS_LIGHT_ITEM_ID = 5;
    public static final int NOTIFICATIONS_SOUND_ITEM_ID = 6;

    public static final int SHOW_MY_LOCATION_ITEM_ID = 10;
    public static final int FRIEND_ONLY_LOCATION_ITEM_ID = 11;
    public static final int ALL_USER_LOCATION_ITEM_ID = 12;

    public static final int PHOTO_AND_MEDIA_ITEM_ID = 20;
    public static final int SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID = 21;
    public static final int SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID = 22;
    public static final int MEDIA_USING_MOBILE_DATA_ITEM_ID = 23;
    public static final int MEDIA_WHEN_CONNECTED_TO_WIFI_ITEM_ID = 24;

    public static final int CHATS_ITEM_ID = 30;
    public static final int CLEAR_ALL_CHATS_ITEM_ID = 31;
    public static final int DELETE_ALL_CHATS_ITEM_ID = 32;

    public static final int PRIVACY_ITEM_ID = 40;
    public static final int LAST_SEEN_ITEM_ID = 41;
    public static final int PROFILE_PHOTO_ITEM_ID = 42;
    public static final int STATUS_ITEM_ID = 43;
    public static final int READ_RECEIPTS_ITEM_ID = 44;
    public static final int BLOCKED_CONTACTS_ITEM_ID = 45;

    public static final int IMAGE_ITEM_ID = 50;
    public static final int AUDIO_ITEM_ID = 51;
    public static final int VIDEO_ITEM_ID = 52;

    public static final int EVERY_ONE_ITEM_ID = 60;
    public static final int ONLY_FRIENDS_ITEM_ID = 61;
    public static final int NOBODY_ITEM_ID = 62;

    public static final int ITEM_TYPE_DRILL_DOWN = 1;
    public static final int ITEM_TYPE_RADIO = 2;
    public static final int ITEM_TYPE_CLICK = 3;
    public static final int ITEM_TYPE_POPUP = 4;

    public static final int ITEM__WITH_NO_ICON = -1;

    public static void initDrillDownMap(HashMap drillDownItemsMap){
        drillDownItemsMap.put(SETTINGS_MAIN_ID, new int[]{ NOTIFICATIONS_AND_SOUND_ITEM_ID, SHOW_MY_LOCATION_ITEM_ID, PHOTO_AND_MEDIA_ITEM_ID, CHATS_ITEM_ID, PRIVACY_ITEM_ID });
        drillDownItemsMap.put(NOTIFICATIONS_AND_SOUND_ITEM_ID, new int[]{NOTIFICATION_PREVIEW_ITEM_ID, NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID, NOTIFICATIONS_VIBRATE_ITEM_ID, NOTIFICATIONS_LIGHT_ITEM_ID, NOTIFICATIONS_SOUND_ITEM_ID});
        drillDownItemsMap.put(SHOW_MY_LOCATION_ITEM_ID, new int[]{FRIEND_ONLY_LOCATION_ITEM_ID, ALL_USER_LOCATION_ITEM_ID});
        drillDownItemsMap.put(PHOTO_AND_MEDIA_ITEM_ID, new int[]{SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID, SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID, MEDIA_USING_MOBILE_DATA_ITEM_ID, MEDIA_WHEN_CONNECTED_TO_WIFI_ITEM_ID });
        drillDownItemsMap.put(CHATS_ITEM_ID, new int[]{CLEAR_ALL_CHATS_ITEM_ID, DELETE_ALL_CHATS_ITEM_ID});
        drillDownItemsMap.put(PRIVACY_ITEM_ID, new int[]{LAST_SEEN_ITEM_ID, PROFILE_PHOTO_ITEM_ID, STATUS_ITEM_ID, READ_RECEIPTS_ITEM_ID, BLOCKED_CONTACTS_ITEM_ID});

        drillDownItemsMap.put(MEDIA_USING_MOBILE_DATA_ITEM_ID, new int[]{IMAGE_ITEM_ID, AUDIO_ITEM_ID, VIDEO_ITEM_ID});
        drillDownItemsMap.put(MEDIA_WHEN_CONNECTED_TO_WIFI_ITEM_ID, new int[]{IMAGE_ITEM_ID, AUDIO_ITEM_ID, VIDEO_ITEM_ID});
    }

    public static String getTitle(int id, Context context){
        String title = null;
        if( id == SETTINGS_MAIN_ID ){
            title = context.getResources().getString(R.string.settings);
        } else if( id == NOTIFICATIONS_AND_SOUND_ITEM_ID ){
            title = context.getResources().getString(R.string.notification_and_sound_options);
        } else if( id == NOTIFICATION_PREVIEW_ITEM_ID ){
            title = context.getResources().getString(R.string.notification_previews);
        } else if( id == NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID ){
            title = context.getResources().getString(R.string.conversation_tones);
        } else if( id == NOTIFICATIONS_VIBRATE_ITEM_ID ){
            title = context.getResources().getString(R.string.notification_vibrate);
        } else if( id == NOTIFICATIONS_LIGHT_ITEM_ID ){
            title = context.getResources().getString(R.string.notification_light);
        } else if( id == NOTIFICATIONS_SOUND_ITEM_ID ){
            title = context.getResources().getString(R.string.notification_sound);
        } else if( id == SHOW_MY_LOCATION_ITEM_ID ){
            title = context.getResources().getString(R.string.show_my_location);
        } else if( id == FRIEND_ONLY_LOCATION_ITEM_ID ){
            title = context.getResources().getString(R.string.friends_only);
        } else if( id == ALL_USER_LOCATION_ITEM_ID ){
            title = context.getResources().getString(R.string.show_to_all);
        } else if( id == PHOTO_AND_MEDIA_ITEM_ID ){
            title = context.getResources().getString(R.string.photos_and_media);
        } else if( id == SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID ){
            title = context.getResources().getString(R.string.save_photo);
        } else if( id == SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID ){
            title = context.getResources().getString(R.string.save_on_capture);
        } else if( id == MEDIA_USING_MOBILE_DATA_ITEM_ID ){
            title = context.getResources().getString(R.string.media_using_mobile_data);
        } else if( id == MEDIA_WHEN_CONNECTED_TO_WIFI_ITEM_ID ){
            title = context.getResources().getString(R.string.media_using_wifi);
        } else if( id == CHATS_ITEM_ID ){
            title = context.getResources().getString(R.string.chats);
        } else if( id == CLEAR_ALL_CHATS_ITEM_ID ){
            title = context.getResources().getString(R.string.clear_all_chats);
        } else if( id ==  DELETE_ALL_CHATS_ITEM_ID){
            title = context.getResources().getString(R.string.delete_all_chats);
        } else if( id == PRIVACY_ITEM_ID ){
            title = context.getResources().getString(R.string.privacy);
        } else if( id == LAST_SEEN_ITEM_ID ){
            title = context.getResources().getString(R.string.last_seen);
        } else if( id == PROFILE_PHOTO_ITEM_ID ){
            title = context.getResources().getString(R.string.profile_photo);
        } else if( id == STATUS_ITEM_ID ){
            title = context.getResources().getString(R.string.status);
        } else if( id == READ_RECEIPTS_ITEM_ID ){
            title = context.getResources().getString(R.string.read_receipts);
        } else if( id == BLOCKED_CONTACTS_ITEM_ID ){
            title = context.getResources().getString(R.string.blocked_contacts);
        }
        return title;
    }

    public static String getSubTitle(int id, Context context){
        String subTitle = null;
        if( id == SETTINGS_MAIN_ID ){
            subTitle = context.getResources().getString(R.string.settings);
        } else if( id == NOTIFICATIONS_AND_SOUND_ITEM_ID ){
            subTitle = UserUtil.isNotificationAndSound() ? "ON" : "OFF";
        } else if( id == NOTIFICATION_PREVIEW_ITEM_ID ){
            subTitle = context.getResources().getString(R.string.show_name_message);
        } else if( id == NOTIFICATIONS_CONVERSATION_TONE_ITEM_ID ){
            subTitle = context.getResources().getString(R.string.play_sound_for_messages);
        } else if( id == NOTIFICATIONS_VIBRATE_ITEM_ID ){
            subTitle = "";
        } else if( id == NOTIFICATIONS_LIGHT_ITEM_ID ){
            subTitle = "";
        } else if( id == NOTIFICATIONS_SOUND_ITEM_ID ){
            subTitle = context.getResources().getString(R.string.notification_ping);
        } else if( id == SHOW_MY_LOCATION_ITEM_ID ){
            subTitle = UserUtil.isShowMyLocation() ? "ON" : "OFF";
        } else if( id == FRIEND_ONLY_LOCATION_ITEM_ID ){
            subTitle = context.getResources().getString(R.string.show_location_to_friends);
        } else if( id == ALL_USER_LOCATION_ITEM_ID ){
            subTitle = context.getResources().getString(R.string.show_location_to_spu_users);
        } else if( id == PHOTO_AND_MEDIA_ITEM_ID ){
            subTitle = "";
        } else if( id == SAVE_INCOMING_PHOTO_TO_GALLERY_ITEM_ID ){
            subTitle = context.getResources().getString(R.string.save_photos_to_gallery);
        } else if( id == SAVE_CAPTURED_PHOTO_TO_GALLERY_ITEM_ID ){
            subTitle = context.getResources().getString(R.string.save_new_photo_captured_in_gallery);
        } else if( id == MEDIA_USING_MOBILE_DATA_ITEM_ID ){
            subTitle = getMediaListingForMobileData();
        } else if( id == MEDIA_WHEN_CONNECTED_TO_WIFI_ITEM_ID ){
            subTitle = getMediaListingForWifi();
        } else if( id == CHATS_ITEM_ID ){
            subTitle = "";
        } else if( id == CLEAR_ALL_CHATS_ITEM_ID ){
            subTitle = "";
        } else if( id ==  DELETE_ALL_CHATS_ITEM_ID){
            subTitle = "";
        } else if( id == PRIVACY_ITEM_ID ){
            subTitle = "";
        } else if( id == LAST_SEEN_ITEM_ID ){
            subTitle = getPrivacyTitle(context, UserUtil.getPrivacyLastSeen());
        } else if( id == PROFILE_PHOTO_ITEM_ID ){
            subTitle = getPrivacyTitle(context, UserUtil.getPrivacyProfilePhoto());
        } else if( id == STATUS_ITEM_ID ){
            subTitle = getPrivacyTitle(context, UserUtil.getPrivacyStatus());
        } else if( id == READ_RECEIPTS_ITEM_ID ){
            subTitle = "";
        } else if( id == BLOCKED_CONTACTS_ITEM_ID ){
            subTitle = context.getResources().getString(R.string.list_of_blocked_contacts);
        }
        return subTitle;
    }

    public static int getItemType(int id){
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
        } else if( id == MEDIA_USING_MOBILE_DATA_ITEM_ID ){
            itemType = ITEM_TYPE_POPUP;
        } else if( id == MEDIA_WHEN_CONNECTED_TO_WIFI_ITEM_ID ){
            itemType = ITEM_TYPE_POPUP;
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

    public static int getItemIcon(int id){
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

    public static boolean getCheckedValue(int id){
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

    public static int getPopUpMediaItemValue(ArrayList<Integer> selectedOnes){
        int value = UserUtil.NO_MEDIA;
        for( int index=0 ; index < selectedOnes.size() ; index++ ){
            value *= getPopUpItemValue(selectedOnes.get(index));
        }
        return value;
    }

    public static int getPopUpItemValue(int itemId){
        int value = UserUtil.NO_MEDIA;
        if( itemId == IMAGE_ITEM_ID ){
            value = UserUtil.IMAGE_MEDIA;
        } else if( itemId == AUDIO_ITEM_ID ){
            value = UserUtil.AUDIO_MEDIA;
        } else if( itemId == VIDEO_ITEM_ID ){
            value = UserUtil.VIDEO_MEDIA;
        } else if( itemId == EVERY_ONE_ITEM_ID ){
            value = UserUtil.EVERY_ONE;
        } else if( itemId == ONLY_FRIENDS_ITEM_ID ){
            value = UserUtil.ONLY_FRIENDS;
        } else if( itemId == NOBODY_ITEM_ID ){
            value = UserUtil.NOBODY;
        }
        return value;
    }

    public static String getPrivacyTitle(Context context, int value){
        String title = null;
        if( value == UserUtil.EVERY_ONE ){
            title = context.getResources().getString(R.string.everyone);
        } else if( value == UserUtil.ONLY_FRIENDS ){
            title = context.getResources().getString(R.string.my_friends);
        } else if( value == UserUtil.NOBODY ){
            title = context.getResources().getString(R.string.nobody);
        }
        return title;
    }

    public static String getMediaListingForMobileData(){
        StringBuilder mediaListing = new StringBuilder();
        if( UserUtil.isMediaEnabledUsingMobileData(UserUtil.IMAGE_MEDIA) ){
            mediaListing.append("Image");
        }
        if( UserUtil.isMediaEnabledUsingMobileData(UserUtil.AUDIO_MEDIA) ){
            if( mediaListing.length() != 0 ){
                mediaListing.append(", ");
            }
            mediaListing.append("Audio");
        }
        if( UserUtil.isMediaEnabledUsingMobileData(UserUtil.VIDEO_MEDIA) ){
            if( mediaListing.length() != 0 ){
                mediaListing.append(", ");
            }
            mediaListing.append("Video");
        }
        return mediaListing.toString();
    }

    public static String getMediaListingForWifi(){
        StringBuilder mediaListing = new StringBuilder();
        if( UserUtil.isMediaEnabledUsingWIFI(UserUtil.IMAGE_MEDIA) ){
            mediaListing.append("Image");
        }
        if( UserUtil.isMediaEnabledUsingWIFI(UserUtil.AUDIO_MEDIA) ){
            if( mediaListing.length() != 0 ){
                mediaListing.append(", ");
            }
            mediaListing.append("Audio");
        }
        if( UserUtil.isMediaEnabledUsingWIFI(UserUtil.VIDEO_MEDIA) ){
            if( mediaListing.length() != 0 ){
                mediaListing.append(", ");
            }
            mediaListing.append("Video");
        }
        return mediaListing.toString();
    }

    public static String[] getPopUpOptions(int itemId, Context context){
        String[] options = null;
        if( itemId == MEDIA_USING_MOBILE_DATA_ITEM_ID || itemId == MEDIA_WHEN_CONNECTED_TO_WIFI_ITEM_ID ){
            options = new String[]{ context.getResources().getString(R.string.image), context.getResources().getString(R.string.audio), context.getResources().getString(R.string.video) };
        } else if( itemId == LAST_SEEN_ITEM_ID || itemId == PROFILE_PHOTO_ITEM_ID || itemId == STATUS_ITEM_ID ){
            options = new String[]{ context.getResources().getString(R.string.everyone), context.getResources().getString(R.string.my_friends), context.getResources().getString(R.string.nobody) };
        } else {

        }
        return options;
    }

    public static String getPopUpTitle(int itemId, Context context){
        String title = null;
        if( itemId == EVERY_ONE_ITEM_ID ){
            title = context.getResources().getString(R.string.everyone);
        } else if( itemId == ONLY_FRIENDS_ITEM_ID ){
            title = context.getResources().getString(R.string.my_friends);
        } else if( itemId == NOBODY_ITEM_ID ){
            title = context.getResources().getString(R.string.nobody);
        } else if( itemId == IMAGE_ITEM_ID ){
            title = context.getResources().getString(R.string.image);
        } else if( itemId == AUDIO_ITEM_ID ){
            title = context.getResources().getString(R.string.audio);
        } else if( itemId == VIDEO_ITEM_ID ){
            title = context.getResources().getString(R.string.video);
        }
        return title;
    }

}
