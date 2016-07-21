package com.sports.unity.util.network;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sports.unity.BuildConfig;
import com.sports.unity.util.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * A utility class to handle the
 * Firebase analytics event logging.
 * Created by Mad on 13-Jul-16.
 */
public class FirebaseUtil {


    /**
     * Used for FireBase Analytics param names.
     * Param names can be up to 24 characters long,
     * may only contain alphanumeric characters and underscores ("_"),
     * and must start with an alphabetic character.
     * Param values can be up to 36 characters long.
     * The "firebase_" prefix is reserved and should not be used.
     */
    public static class Param {
        public static final String TIME_STAMP = "time_stamp";

        public static final String PROFILE_CREATION = "profile_creation";

        public static final String CRICKET = "cricket";
        public static final String FOOTBALL = "football";


        public static final String SPORTS_TYPE = "sports_type";

        public static final String NAME = "name";
        public static final String ID = "id";


        public static final String FILTER_TYPE = "filter_type";

        public static final String TEAM = "team";
        public static final String LEAGUE = "league";
        public static final String PLAYER = "player";


        public static final String SCORE_TEAM_1 = "score_team_1";
        public static final String SCORE_TEAM_2 = "score_team_2";
        public static final String SERIES_NAME = "series_name";

        public static final String SELECTED = "selected";
        public static final String DESELECTED = "deselected";
        public static final String ENABLED = "enabled";
        public static final String MATCH_ID = "match_id";

        public static final String GLOBAL_SEARCH_TEXT = "global_search_text";
        public static final String GLOBAL_SEARCH_TYPE = "global_search_type";


    }


    /**
     * Used for FireBase Analytics event names.
     * Event names can be up to 32 characters long,
     * may only contain alphanumeric characters and underscores ("_"),
     * and must start with an alphabetic character.
     * The "firebase_" prefix is reserved and should not be used.
     */
    public static class Event {
        public static final String ENTER_PHONE_NUMBER = "enter_phone_number";
        public static final String SUBMIT_PHONE_NUMBER = "submit_phone_number";

        public static final String AUTO_OTP = "auto_otp";
        public static final String MANUAL_OTP = "manual_otp";

        public static final String PROFILE_IMAGE = "profile_image";
        public static final String PROFILE_NAME = "profile_name";
        public static final String FB_LOGIN = "fb_login";

        public static final String SPORTS_SELECTION = "sports_selection";
        public static final String SPORTS_COMPLETE = "sports_complete";

        public static final String FAV_SELECTION = "fav_selection";
        public static final String NEXT_CLICK_EVENT = "next_click";
        public static final String SKIP_CLICK_EVENT = "skip_click";

        public static final String NEWS_SCREEN = "news_screen";
        public static final String SCORE_SCREEN = "score_screen";
        public static final String CHAT_SCREEN = "chat_screen";

        public static final String NEWS_DETAIL = "news_detail";
        public static final String NEWS_SHARE = "news_share";
        public static final String NEWS_FILTER = "news_filter";
        public static final String NEWS_SEARCH = "news_search";
        public static final String NEWS_CARD = "news_card";

        public static final String FILTER_FAV_DETAIL = "filter_fav_detail";
        public static final String FILTER_EDIT_CLICK = "filter_edit_click";

        public static final String SCORE_DETAIL = "score_detail";
        public static final String SCORE_SHARE = "score_share";
        public static final String SCORE_FILTER = "score_filter";
        public static final String SCORE_SEARCH = "score_search";
        public static final String SCORE_ODS = "score_ods";
        public static final String SCORE_NOTIFICATION = "score_notification";

        public static final String SCORE_SUMMARY = "score_summary";
        public static final String SCORE_COMMENTARY = "score_commentary";
        public static final String SCORE_CARD = "score_card";
        public static final String SCORE_MATCH_STATS = "score_match_stats";
        public static final String SCORE_TIMELINE = "score_timeline";
        public static final String SCORE_LINEUP = "score_lineup";

        public static final String GLOBAL_SCORE_FILTER = "global_score_filter";
        public static final String GLOBAL_SEARCH = "global_search";

        public static final String VIEW_PROFILE = "view_profile";
        public static final String NAV_FAV_DETAIL = "nav_fav_detail";
        public static final String EDIT_SPORTS = "edit_sports";
        public static final String STAFF_PICK_DETAIL = "staff_pick_detail";
        public static final String SETTINGS = "settings";
        public static final String FRIEND_REQUEST = "friend_request";

        public static final String PROMO_INVITE = "promo_invite";

        public static final String EDIT_PROFILE = "edit_profile";
        public static final String EDIT_FAVOURITE = "edit_favourite";
        public static final String PROFILE_FAV_DETAIL = "profile_fav_detail";

        public static final String CHAT_FRAG = "chat_frag";
        public static final String CONTACT_FRAG = "contact_frag";
        public static final String OTHER_FRAG = "other_frag";

        public static final String OPEN_FAB_MENU = "open_fab_menu";
        public static final String PEOPLE_AROUND_ME = "people_around_me";
        public static final String CREATE_GROUP = "create_group";


        public static final String CONTACT_SU = "contact_su";
        public static final String CONTACT_INVITE = "contact_invite";

        public static final String OPEN_SPECIFIC_CHAT = "open_specific_chat";
        public static final String OPEN_GROUP_CHAT = "open_group_chat";

        public static final String CHAT_EMOJI = "chat_emoji";
        public static final String CHAT_CAMERA = "chat_camera";
        public static final String CHAT_VOICE = "chat_voice";
        public static final String CHAT_GALLERY = "chat_gallery";

        public static final String CHAT_VIEW_PROFILE = "chat_view_profile";
        public static final String GROUP_VIEW_PROFILE = "group_view_profile";

        public static final String BLOCK_USER = "block_user";

        public static final String GROUP_IMAGE = "group_image";
        public static final String GROUP_NAME = "group_name";
        public static final String GROUP_NEXT = "group_next";
        public static final String GROUP_ADD_MEMBER = "group_add_member";
        public static final String GROUP_SEARCH_MEMBER = "group_search_member";
        public static final String GROUP_FINALIZE_CREATE = "group_finalize_create";

        public static final String PAM_FRIENDS_TAB = "pam_friends_tab";
        public static final String PAM_SU_TAB = "pam_su_tab";
        public static final String PAM_SIMILAR_TAB = "pam_similar_tab";
        public static final String PAM_SLIDER = "pam_slider";

        public static final String PAM_FRIEND_CHAT = "pam_friend_chat";
        public static final String PAM_SU_CHAT = "pam_su_chat";
        public static final String PAM_SIMILAR_CHAT = "pam_similar_chat";

        public static final String DATA_ERROR = "data_error";
    }

    /**
     * Instantiate the firebase analytics to log the events.
     *
     * @param context context of origin activity
     * @return instance of FirebaseAnalytics.
     */

    public static FirebaseAnalytics getInstance(Context context) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        return firebaseAnalytics;
    }

    /**
     * Send events to firebase console.
     *
     * @param firebaseAnalytics Instance of FirebaseAnalytics
     * @param bundle            Bundle containing name value pair of event params.
     * @param eventName         Name of the event to be loged.
     */
    public static void logEvent(FirebaseAnalytics firebaseAnalytics, Bundle bundle, String eventName) {
        bundle.putString(FirebaseUtil.Param.TIME_STAMP, FirebaseUtil.getLogTime());
        if (!BuildConfig.DEBUG) {
            Log.i("Firebase", "EventName= " + eventName);
            firebaseAnalytics.logEvent(eventName, bundle);
        }
    }

    /**
     * Get current system time in "dd/MM HH:mm" format in IST (GMT 05:30).
     *
     * @return current time of system in IST.
     */
    public static String getLogTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String time = String.valueOf(simpleDateFormat.format(CommonUtil.getCurrentGMTTimeInEpoch() * 1000));
        return time;
    }

    /**
     * Param values can be up to 36 characters long.
     * So if the value contains more than 30 characters then
     * substring it and use only first 30 characters.
     *
     * @param paramValue Parameter value.
     * @return first 30 characters of Parameter value.
     */
    public static String trimValue(String paramValue) {
        if (paramValue.length() > 30) {
            String name = paramValue.substring(0, 30);
            return name;
        } else {
            return paramValue;
        }
    }
}
