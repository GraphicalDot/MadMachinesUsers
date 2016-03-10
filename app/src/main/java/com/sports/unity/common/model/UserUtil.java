package com.sports.unity.common.model;

import android.content.Context;
import android.media.RingtoneManager;
import android.provider.Settings;
import android.util.Log;

import com.sports.unity.common.controller.SettingsActivity;
import com.sports.unity.util.CommonUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by madmachines on 21/10/15.
 */
public class UserUtil {

    public static final int NO_MEDIA = 1;
    public static final int IMAGE_MEDIA = 3;
    public static final int AUDIO_MEDIA = 5;
    public static final int VIDEO_MEDIA = 7;

    public static final int EVERY_ONE = 10;
    public static final int ONLY_FRIENDS = 11;
    public static final int NOBODY = 12;

    private static boolean USER_REGISTERED = false;
    private static boolean OTP_SENT = false;
    private static boolean PROFILE_CREATED = false;
    private static String COUNTRY_CODE = "";

    private static ArrayList<String> SPORTS_SELECTED = null;
    private static ArrayList<String> FILTER_SPORTS_SELECTED = null;
    private static boolean leagueSelected;
    private static boolean teamSelected;
    private static boolean playerSelected;
    private static boolean filterCompleted;
    private static String favFilterList = null;
    private static boolean isFavouriteVcardUpdated;


    /*
     * setting related preferences.
     */
    private static boolean NOTIFICATION_AND_SOUND = true;
    private static boolean NOTIFICATION_PREVIEWS = true;
    private static boolean CONVERSATION_TONES = true;
    private static boolean CONVERSATION_VIBRATE = true;
    private static boolean NOTIFICATION_LIGHT = true;

    private static String NOTIFICATION_SOUND_TITLE = null;
    private static String NOTIFICATION_SOUND_URI = null;

    private static boolean SHOW_MY_LOCATION = true;
    private static boolean SHOW_TO_FRIENDS_LOCATION = true;
    private static boolean SHOW_TO_ALL_LOCATION = true;

    private static boolean SAVE_INCOMING_MEDIA_TO_GALLERY = true;
    private static boolean SAVE_IN_APP_CAPTURE_MEDIA_TO_GALLERY = true;
    private static int MEDIA_USING_MOBILE_DATA = IMAGE_MEDIA * AUDIO_MEDIA * VIDEO_MEDIA;
    private static int MEDIA_USING_WIFI = IMAGE_MEDIA * AUDIO_MEDIA * VIDEO_MEDIA;

    private static int PRIVACY_LAST_SEEN = EVERY_ONE;
    private static int PRIVACY_PROFILE_PHOTO = EVERY_ONE;
    private static int PRIVACY_STATUS = EVERY_ONE;
    private static boolean READ_RECEIPTS = true;

    public static void init(Context context) {
        TinyDB tinyDB = TinyDB.getInstance(context);

        loadBasicPreferences(tinyDB, context);
        loadFavoritePreferences(tinyDB);
        loadSettingPreferences(tinyDB);
    }

    public static void setUserRegistered(Context context, boolean userRegistered) {
        USER_REGISTERED = userRegistered;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.KEY_REGISTERED, userRegistered);
    }

    public static void setProfileCreated(Context context, boolean profileCreated) {
        PROFILE_CREATED = profileCreated;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.KEY_PROFILE_CREATED, profileCreated);
    }

    public static void setSportsSelected(Context context, ArrayList<String> sportsSelected) {
        SPORTS_SELECTED = sportsSelected;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putListString(TinyDB.KEY_SPORTS_SELECTED, sportsSelected);
    }

    public static void setFilterSportsSelected(Context context, ArrayList<String> filterSportsSelected) {
        FILTER_SPORTS_SELECTED = filterSportsSelected;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putListString(TinyDB.KEY_FILTER_SPORTS_SELECTED, filterSportsSelected);
    }

    public static void setOtpSent(Context context, boolean otpSent) {
        OTP_SENT = otpSent;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.KEY_OTP_SENT, otpSent);
    }

    public static boolean isUserRegistered() {
        return USER_REGISTERED;
    }

    public static boolean isOtpSent() {
        return OTP_SENT;
    }

    public static boolean isProfileCreated() {
        return PROFILE_CREATED;
    }

    public static boolean isSportsSelected() {
        return !SPORTS_SELECTED.isEmpty();
    }

    public static ArrayList<String> getSportsSelected() {
        return SPORTS_SELECTED;
    }

    public static ArrayList<String> getFilterSportsSelected() {
        return FILTER_SPORTS_SELECTED;
    }

    public static void setLeagueSelected(Context context, boolean isLeagueSelected) {
        leagueSelected = isLeagueSelected;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.LEAGUE_SELECTION, isLeagueSelected);
    }

    public static boolean isLeagueSelected() {
        return leagueSelected;
    }

    public static void setTeamSelected(Context context, boolean isTeamSelected) {
        teamSelected = isTeamSelected;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.TEAM_SELECTION, isTeamSelected);
    }

    public static boolean isTeamSelected() {
        return teamSelected;
    }

    public static void setPlayerSelected(Context context, boolean isPlayerSelected) {
        playerSelected = isPlayerSelected;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.PLAYER_SELECTION, isPlayerSelected);
    }

    public static boolean isPlayerSelected() {
        return playerSelected;
    }

    public static void setFilterCompleted(Context context, boolean isFilterCompleted) {
        filterCompleted = isFilterCompleted;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.FILTER_COMPLETE, isFilterCompleted);
    }

    public static boolean isFilterCompleted() {
        return filterCompleted;
    }

    public static void setFavouriteFilters(Context context, String favItems) {

        TinyDB tinyDB = TinyDB.getInstance(context);
        if (tinyDB.contains(TinyDB.FAVOURITE_FILTERS)) {
            tinyDB.remove(TinyDB.FAVOURITE_FILTERS);
        }
        tinyDB.putString(TinyDB.FAVOURITE_FILTERS, favItems);
    }

    public static String getFavouriteFilters() {
        return favFilterList;
    }

    public static void setFavouriteVcardUpdated(Context context, boolean isFavouriteVcardUpdated) {
        UserUtil.isFavouriteVcardUpdated = isFavouriteVcardUpdated;
        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.VCARD_UPDATED, isFavouriteVcardUpdated);
    }

    public static boolean isIsFavouriteVcardUpdated() {
        return isFavouriteVcardUpdated;
    }

    public static boolean isNotificationAndSound() {
        return NOTIFICATION_AND_SOUND;
    }

    public static void setNotificationAndSound(Context context, boolean notificationAndSound) {
        NOTIFICATION_AND_SOUND = notificationAndSound;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.NOTIFICATION_AND_SOUND_OPTIONS, notificationAndSound);
    }

    public static boolean isNotificationPreviews() {
        return NOTIFICATION_PREVIEWS;
    }

    public static void setNotificationPreviews(Context context, boolean notificationPreviews) {
        NOTIFICATION_PREVIEWS = notificationPreviews;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.NOTIFICATION_PREVIEW, notificationPreviews);
    }

    public static boolean isConversationTones() {
        return CONVERSATION_TONES;
    }

    public static void setConversationTones(Context context, boolean conversationTones) {
        CONVERSATION_TONES = conversationTones;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.CONVERSATION_TONES, conversationTones);
    }

    public static boolean isConversationVibrate() {
        return CONVERSATION_VIBRATE;
    }

    public static void setConversationVibrate(Context context, boolean conversationVibrate) {
        CONVERSATION_VIBRATE = conversationVibrate;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.VIBRATE, conversationVibrate);
    }

    public static boolean isNotificationLight() {
        return NOTIFICATION_LIGHT;
    }

    public static void setNotificationLight(Context context, boolean notificationLight) {
        NOTIFICATION_LIGHT = notificationLight;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.LIGHT, notificationLight);
    }

    public static String getNotificationSoundTitle() {
        return NOTIFICATION_SOUND_TITLE;
    }

    public static void setNotificationSoundTitle(Context context, String notificationSoundTitle) {
        NOTIFICATION_SOUND_TITLE = notificationSoundTitle;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putString(TinyDB.NOTIFICATION_SOUND_TITLE, notificationSoundTitle);
    }

    public static String getNotificationSoundURI() {
        return NOTIFICATION_SOUND_URI;
    }

    public static void setNotificationSoundURI(Context context, String notificationSoundUri) {
        NOTIFICATION_SOUND_URI = notificationSoundUri;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putString(TinyDB.NOTIFICATION_SOUND_URI, notificationSoundUri);
    }


    public static boolean isShowMyLocation() {
        return SHOW_MY_LOCATION;
    }

    public static void setShowMyLocation(Context context, boolean showMyLocation) {
        SHOW_MY_LOCATION = showMyLocation;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.LOCATION_OPTIONS, showMyLocation);
    }

    public static boolean isShowToFriendsLocation() {
        return SHOW_TO_FRIENDS_LOCATION;
    }

    public static void setShowToFriendsLocation(Context context, boolean showToFriendsLocation) {
        SHOW_TO_FRIENDS_LOCATION = showToFriendsLocation;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.FRIENDS_ONLY, showToFriendsLocation);
    }

    public static boolean isShowToAllLocation() {
        return SHOW_TO_ALL_LOCATION;
    }

    public static void setShowToAllLocation(Context context, boolean showToAllLocation) {
        SHOW_TO_ALL_LOCATION = showToAllLocation;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.SHOW_TO_ALL, showToAllLocation);
    }

    public static boolean isSaveIncomingMediaToGallery() {
        return SAVE_INCOMING_MEDIA_TO_GALLERY;
    }

    public static void setSaveIncomingMediaToGallery(Context context, boolean saveIncomingMediaToGallery) {
        SAVE_INCOMING_MEDIA_TO_GALLERY = saveIncomingMediaToGallery;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.SAVE_INCOMING_MEDIA, saveIncomingMediaToGallery);
    }

    public static boolean isSaveInAppCaptureMediaToGallery() {
        return SAVE_IN_APP_CAPTURE_MEDIA_TO_GALLERY;
    }

    public static void setSaveInAppCaptureMediaToGallery(Context context, boolean saveInAppCaptureMediaToGallery) {
        SAVE_IN_APP_CAPTURE_MEDIA_TO_GALLERY = saveInAppCaptureMediaToGallery;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.SAVE_IN_APP_MEDIA_CAPTURE, saveInAppCaptureMediaToGallery);
    }

    public static void setMediaUsingMobileData(Context context, int value) {
        MEDIA_USING_MOBILE_DATA = value;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putInt(TinyDB.MEDIA_MOBILE_DATA, MEDIA_USING_MOBILE_DATA);
    }

    public static boolean isMediaEnabledUsingMobileData(int media) {
        boolean enabled = false;
        if (MEDIA_USING_MOBILE_DATA % media == 0) {
            enabled = true;
        }
        return enabled;
    }

    public static void setMediaUsingWIFI(Context context, int value) {
        MEDIA_USING_WIFI = value;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putInt(TinyDB.MEDIA_USING_WIFI, MEDIA_USING_WIFI);
    }

    public static boolean isMediaEnabledUsingWIFI(int media) {
        boolean enabled = false;
        if (MEDIA_USING_WIFI % media == 0) {
            enabled = true;
        }
        return enabled;
    }

    public static boolean isMediaAutoDownloadEnabled(Context context, int media) {
        boolean enabled = false;
        if (CommonUtil.isConnectedWifi(context)) {
            if (MEDIA_USING_WIFI % media == 0) {
                enabled = true;
            }
        } else {
            if (MEDIA_USING_MOBILE_DATA % media == 0) {
                enabled = true;
            }
        }
        return enabled;
    }

    public static int getPrivacyLastSeen() {
        return PRIVACY_LAST_SEEN;
    }

    public static void setPrivacyLastSeen(Context context, int privacyLastSeen) {
        PRIVACY_LAST_SEEN = privacyLastSeen;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putInt(TinyDB.PRIVACY_LAST_SEEN, PRIVACY_LAST_SEEN);
    }

    public static int getPrivacyProfilePhoto() {
        return PRIVACY_PROFILE_PHOTO;
    }

    public static void setPrivacyProfilePhoto(Context context, int privacyProfilePhoto) {
        PRIVACY_PROFILE_PHOTO = privacyProfilePhoto;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putInt(TinyDB.PRIVACY_PROFILE_PHOTO, PRIVACY_PROFILE_PHOTO);
    }

    public static int getPrivacyStatus() {
        return PRIVACY_STATUS;
    }

    public static void setPrivacyStatus(Context context, int privacyStatus) {
        PRIVACY_STATUS = privacyStatus;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putInt(TinyDB.PRIVACY_STATUS, PRIVACY_STATUS);
    }

    public static boolean isReadReceipts() {
        return READ_RECEIPTS;
    }

    public static void setReadReceipts(Context context, boolean readReceipts) {
        READ_RECEIPTS = readReceipts;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putBoolean(TinyDB.READ_RECEIPTS, readReceipts);
    }

    public static String getCountryCode() {
        return COUNTRY_CODE;
    }

    public static void setCountryCode(Context context, String countryCode) {
        COUNTRY_CODE = countryCode;

        TinyDB tinyDB = TinyDB.getInstance(context);
        tinyDB.putString(TinyDB.KEY_COUNTRY_CODE, countryCode);
    }

    private static void loadBasicPreferences(TinyDB tinyDB, Context context) {
        USER_REGISTERED = tinyDB.getBoolean(TinyDB.KEY_REGISTERED, false);
        PROFILE_CREATED = tinyDB.getBoolean(TinyDB.KEY_PROFILE_CREATED, false);
        OTP_SENT = tinyDB.getBoolean(TinyDB.KEY_OTP_SENT, false);
        COUNTRY_CODE = tinyDB.getString(TinyDB.KEY_COUNTRY_CODE);

        if (COUNTRY_CODE.isEmpty()) {
            COUNTRY_CODE = CommonUtil.getDefaultCountyCode(context);
        }
    }

    private static void loadFavoritePreferences(TinyDB tinyDB) {
        SPORTS_SELECTED = tinyDB.getListString(TinyDB.KEY_SPORTS_SELECTED);
        FILTER_SPORTS_SELECTED = tinyDB.getListString(TinyDB.KEY_FILTER_SPORTS_SELECTED);
        favFilterList = tinyDB.getString(TinyDB.FAVOURITE_FILTERS);

        leagueSelected = tinyDB.getBoolean(TinyDB.LEAGUE_SELECTION, false);
        teamSelected = tinyDB.getBoolean(TinyDB.TEAM_SELECTION, false);
        playerSelected = tinyDB.getBoolean(TinyDB.PLAYER_SELECTION, false);
        filterCompleted = tinyDB.getBoolean(TinyDB.FILTER_COMPLETE, false);
        isFavouriteVcardUpdated = tinyDB.getBoolean(TinyDB.VCARD_UPDATED, false);
    }

    private static void loadSettingPreferences(TinyDB tinyDB) {
        NOTIFICATION_AND_SOUND = tinyDB.getBoolean(TinyDB.NOTIFICATION_AND_SOUND_OPTIONS, NOTIFICATION_AND_SOUND);
        NOTIFICATION_PREVIEWS = tinyDB.getBoolean(TinyDB.NOTIFICATION_PREVIEW, NOTIFICATION_PREVIEWS);
        CONVERSATION_TONES = tinyDB.getBoolean(TinyDB.CONVERSATION_TONES, CONVERSATION_TONES);
        CONVERSATION_VIBRATE = tinyDB.getBoolean(TinyDB.VIBRATE, CONVERSATION_VIBRATE);
        NOTIFICATION_LIGHT = tinyDB.getBoolean(TinyDB.LIGHT, NOTIFICATION_LIGHT);

        NOTIFICATION_SOUND_TITLE = tinyDB.getString(TinyDB.NOTIFICATION_SOUND_TITLE);
        NOTIFICATION_SOUND_URI = tinyDB.getString(TinyDB.NOTIFICATION_SOUND_URI);
        if (NOTIFICATION_SOUND_URI.isEmpty()) {
            NOTIFICATION_SOUND_URI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
            NOTIFICATION_SOUND_TITLE = "Default";
        }

        SHOW_MY_LOCATION = tinyDB.getBoolean(TinyDB.LOCATION_OPTIONS, SHOW_MY_LOCATION);
        SHOW_TO_FRIENDS_LOCATION = tinyDB.getBoolean(TinyDB.FRIENDS_ONLY, SHOW_TO_FRIENDS_LOCATION);
        SHOW_TO_ALL_LOCATION = tinyDB.getBoolean(TinyDB.SHOW_TO_ALL, SHOW_TO_ALL_LOCATION);

        SAVE_INCOMING_MEDIA_TO_GALLERY = tinyDB.getBoolean(TinyDB.SAVE_INCOMING_MEDIA, SAVE_INCOMING_MEDIA_TO_GALLERY);
        SAVE_IN_APP_CAPTURE_MEDIA_TO_GALLERY = tinyDB.getBoolean(TinyDB.SAVE_IN_APP_MEDIA_CAPTURE, SAVE_IN_APP_CAPTURE_MEDIA_TO_GALLERY);
        MEDIA_USING_MOBILE_DATA = tinyDB.getInt(TinyDB.MEDIA_MOBILE_DATA, MEDIA_USING_MOBILE_DATA);
        MEDIA_USING_WIFI = tinyDB.getInt(TinyDB.MEDIA_USING_WIFI, MEDIA_USING_WIFI);

        PRIVACY_LAST_SEEN = tinyDB.getInt(TinyDB.PRIVACY_LAST_SEEN, PRIVACY_LAST_SEEN);
        PRIVACY_PROFILE_PHOTO = tinyDB.getInt(TinyDB.PRIVACY_PROFILE_PHOTO, PRIVACY_PROFILE_PHOTO);
        PRIVACY_STATUS = tinyDB.getInt(TinyDB.PRIVACY_STATUS, PRIVACY_STATUS);
        READ_RECEIPTS = tinyDB.getBoolean(TinyDB.READ_RECEIPTS, READ_RECEIPTS);
    }

}
