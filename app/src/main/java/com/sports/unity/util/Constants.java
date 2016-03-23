package com.sports.unity.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by amandeep on 15/10/15.
 */
public class Constants {

    public static final String GAME_KEY_CRICKET = "cricket";
    public static final String GAME_KEY_FOOTBALL = "football";
    public static final String GAME_KEY_TENNIS = "tennis";
    public static final String GAME_KEY_BASKETBALL = "basketball";
    public static final String GAME_KEY_F1 = "f1";

    public static final String INTENT_KEY_PHONE_NUMBER = "PHONE_NUMBER";
    public static final String INTENT_KEY_ROOM_NAME = "ROOM_NAME";
    public static final String INTENT_KEY_CONTACT_FRAGMENT_USAGE = "CONTACT_FRAGMENT_USAGE";
    public static final String INTENT_KEY_FRIENDS_FRAGMENT_USAGE = "FRIENDS_FRAGMENT_USAGE";
    public static final String INTENT_KEY_SEARCH_ON = "SEARCH_ON";
    public static final String INTENT_FORWARD_SELECTED_IDS = "get_the _selected_ids";
    public static final String INTENT_KEY_ADDED_MEMBERS = "added_members";
    public static final String INTENT_KEY_URL = "URL";
    public static final String INTENT_KEY_TYPE = "TYPE";
    public static final String INTENT_KEY_TITLE = "TITLE";
    public static final String INTENT_KEY_ID = "ID";
    public static final String INTENT_KEY_MATCH_STATUS = "MATCH_STATUS";

    public static final String INTENT_KEY_FILENAME = "FILE_NAME";
    public static final String INTENT_KEY_MIMETYPE = "MIME_TYPE";

    public static final String URL_NEWS_CONTENT = "http://52.76.74.188:8000/mixed?";

    public static final String REQUEST_PARAMETER_KEY_PHONE_NUMBER = "phone_number";
    public static final String REQUEST_PARAMETER_KEY_AUTH_CODE = "auth_code";
    public static final String REQUEST_PARAMETER_KEY_PASSWORD = "password";
    public static final String REQUEST_PARAMETER_KEY_USER_NAME = "username";
    public static final String REQUEST_PARAMETER_KEY_APK_VERSION = "apk_version";
    public static final String REQUEST_PARAMETER_KEY_UDID = "udid";

    public static final String PARAM_TIME = "time";
    public static final String PARAM_MIME_TYPE = "mime_type";
    public static final String PARAM_CHAT_TYPE_OTHERS = "chat_type_others";

    public static final int CONNECTION_TIME_OUT = 10 * 1000;
    public static final int CONNECTION_READ_TIME_OUT = 20 * 1000;

    // SD card image directory
    public static final String PHOTO_ALBUM = "image";

    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg", "png");

    public static final String SPORTS_FILTER_TYPE = "advanced_filter_type";
    public static final String FILTER_TYPE_PLAYER = "players";
    public static final String FILTER_TYPE_LEAGUE = "leagues";
    public static final String FILTER_TYPE_TEAM = "teams";

    public static final String SPORTS_TYPE = "sports_type";
    public static final String SPORTS_TYPE_CRICKET = "cricket";
    public static final String SPORTS_TYPE_FOOTBALL = "football";
    public static final String FILTER_SEARCH_EXTRA = "filter_search";

    public static final String RESULT_REQUIRED = "from_add_sports";

    public static final String COLOR_WHITE = "white";
    public static final String COLOR_BLUE = "blue";

    public static final String IS_OWN_PROFILE = "is_own_profile";
    public static final String INTENT_TEAM_LEAGUE_DETAIL_EXTRA = "team_league_detail_extra";
    public static final int REQUEST_CODE_SCORE = 1298;
    public static final int REQUEST_CODE_NEWS = 1397;
    public static final int REQUEST_CODE_EDIT_SPORT = 1496;
    public static final int REQUEST_CODE_ADD_SPORT = 1595;
    public static final int REQUEST_CODE_PROFILE = 1199;
    public static final int REQUEST_CODE_VIEW_PROFILE = 1299;
    public static final int REQUEST_CODE_CAMERA_EXTERNAL_STORAGE_PERMISSION = 101;
    public static final int REQUEST_CODE_CONTACT_PERMISSION = 102;
    public static final int REQUEST_CODE_PHONE_STATE_PERMISSION = 103;
    public static final int REQUEST_CODE_CAMERA_PERMISSION = 104;
    public static final int REQUEST_CODE_GALLERY_STORAGE_PERMISSION = 105;
    public static final int REQUEST_CODE_RECORD_AUDIO_PERMISSION = 106;
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 107;
    public static final String PLAYER_NAME = "player_name";
    public static final String INTENT_KEY_TOSS = "toss";
    public static final String INTENT_KEY_MATCH_NAME = "match_name";
    public static final String INTENT_KEY_DATE = "date";
    public static final String INTENT_KEY_MATCH_TIME = "match_time";
    public static final String INTENT_KEY_MATCH_LIVE = "isLive";
    public static final String INTENT_KEY_TEAM1_ID = "team1_id";
    public static final String INTENT_KEY_TEAM2_ID = "team2_id";
    public static final String INTENT_KEY_LEAGUE_ID = "league_id";
    public static final String INTENT_KEY_TEAM1_NAME = "team1";
    public static final String INTENT_KEY_TEAM2_NAME = "team2";
    public static final long TIMEINMILISECOND = 6000;
    public static final String ERRORRESPONSE ="{\"success\":false,\"error\":true}";
    public static final String LEAGUE_NAME = "leaague_name";
    public static final String ENABLE_LOCATION = "enable_location";
    public static final String CHECK_LOCATION = "location";
    public static final String SENT_TOKEN_TO_SERVER = "token_sent_to_server" ;
}
