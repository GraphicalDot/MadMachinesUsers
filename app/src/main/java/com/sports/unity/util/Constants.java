package com.sports.unity.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by amandeep on 15/10/15.
 */
public class Constants {

    public static final String INTENT_KEY_PHONE_NUMBER = "PHONE_NUMBER";
    public static final String INTENT_KEY_ROOM_NAME = "ROOM_NAME";
    public static final String INTENT_KEY_CONTACT_FRAGMENT_USAGE = "CONTACT_FRAGMENT_USAGE";
    public static final String INTENT_KEY_SEARCH_ON = "SEARCH_ON";
    public static final String INTENT_FORWARD_SELECTED_IDS = "get_the _selected_ids";
    public static final String INTENT_KEY_URL = "URL";
    public static final String INTENT_KEY_TYPE = "TYPE";
    public static final String INTENT_KEY_TITLE = "TITLE";

    public static final String INTENT_KEY_FILENAME = "FILE_NAME";
    public static final String INTENT_KEY_MIMETYPE = "MIME_TYPE";

    public static final String URL_REGISTER = "http://54.169.217.88/register?";
    public static final String URL_CREATE = "http://54.169.217.88/create?";
    public static final String URL_NEWS_CONTENT = "http://52.76.74.188:8000/mixed?";

    public static final String REQUEST_PARAMETER_KEY_PHONE_NUMBER = "phone_number";
    public static final String REQUEST_PARAMETER_KEY_AUTH_CODE = "auth_code";
    public static final String REQUEST_PARAMETER_KEY_PASSWORD = "password";

    public static final String GAME_KEY_BASKETBALL = "basketball";
    public static final String GAME_KEY_CRICKET = "cricket";
    public static final String GAME_KEY_FOOTBALL = "football";
    public static final String GAME_KEY_TENNIS = "tennis";
    public static final String GAME_KEY_F1 = "f1";

    public static final String PARAM_TIME = "time";
    public static final String PARAM_MIME_TYPE = "mime_type";

    public static final String NEWS_REQUEST_TAG = "news_tag";

    public static final int CONNECTION_TIME_OUT = 20 * 1000;


    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 3;

    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp

    // SD card image directory
    public static final String PHOTO_ALBUM = "image";

    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg", "png");

    public static final String SPORTS_FILTER_TYPE = "advanced_filter_type";
    public static final String FILTER_TYPE_PLAYER = "players";
    public static final String FILTER_TYPE_LEAGUE = "leagues";
    public static final String FILTER_TYPE_TEAM = "teams";

    public static final String SPORTS_TYPE="sports_type";
    public static final String SPORTS_TYPE_CRICKET="cricket";
    public static final String SPORTS_TYPE_FOOTBALL="football";
    public static final String FILTER_SEARCH_EXTRA="filter_search";
}
