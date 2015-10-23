package com.sports.unity.Database;

import android.provider.BaseColumns;

/**
 * Created by madmachines on 1/9/15.
 */
public final class SportsUnityContract {

    public SportsUnityContract() {
    }

    public static abstract class ContactsEntry implements BaseColumns {
        public static final String TABLE_NAME = "contactsTable";
        public static final String COLUMN_NAME = "display_name";
        public static final String COLUMN_PHONENUMBER = "jid";
        public static final String COLUMN_CHAT_ENABLED = "chat_label";
        public static final String COLUMN_REGISTERED = "is_sportsunity_user";
        public static final String COLUMN_UNIQUE_INDEX = "id";
        public static final String COLUMN_USER_IMAGE = "user_image";
        public static final String COLUMN_UNREAD_COUNT = "unread_messages";
        public static final String COLUMN_STATUS = "status";
    }

    public static abstract class MessagesEntry implements BaseColumns {
        public static final String TABLE_NAME = "messagesTable";
        public static final String COLUMN_PHONENUMBER = "_jid";
        public static final String COLUMN_RECIEVE_TIMESTAMP = "recieve_timestamp";
        public static final String COLUMN_SEND_TIMESTAMP = "send_timestamp";
        public static final String COLUMN_MESSAGE = "data";
        public static final String COLUMN_NAME_I_AM_SENDER = "i_am_sender";
    }

    public static abstract class NewsEntry implements BaseColumns {
        public static final String TABLE_NAME = "newsTable";

        public static final String COLUMN_NEWS_ID = "news_id";
        public static final String COLUMN_WEBSITE = "";
        public static final String COLUMN_IMAGE_URL = "";
        public static final String COLUMN_IMAGE_CONTENT = "";
        public static final String COLUMN_TITLE = "";
        public static final String COLUMN_SUMMARY = "";
        public static final String COLUMN_NEWS_LINK = "";
        public static final String COLUMN_CUSTOM_SUMMARY = "";
        public static final String COLUMN_PUBLISHED = "";
        public static final String COLUMN_TYPE = "";
        public static final String COLUMN_PUBLISH_EPOCH = "";

    }
}
