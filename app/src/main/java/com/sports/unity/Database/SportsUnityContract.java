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

        public static final String COLUMN_CONTACT_ID = "contact_id";
        public static final String COLUMN_NAME = "display_name";
        public static final String COLUMN_PHONENUMBER = "jid";
        public static final String COLUMN_REGISTERED = "is_sportsunity_user";
        public static final String COLUMN_UNIQUE_INDEX = "id";
        public static final String COLUMN_USER_IMAGE = "user_image";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_AVAILABLE = "available";

    }

    public static abstract class MessagesEntry implements BaseColumns {
        public static final String TABLE_NAME = "messagesTable";

        public static final String COLUMN_ID = "incremental_messages_id";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        public static final String COLUMN_CHAT_ID = "chat_id";
        public static final String COLUMN_PHONENUMBER = "_jid";
        public static final String COLUMN_RECEIVE_TIMESTAMP = "recieve_timestamp";
        public static final String COLUMN_SEND_TIMESTAMP = "send_timestamp";
        public static final String COLUMN_DATA_TEXT = "text_data";
        public static final String COLUMN_DATA_MEDIA = "media_data";
        public static final String COLUMN_MIME_TYPE = "data_mime_type";
        public static final String COLUMN_SERVER_RECEIPT = "receive_server_timestamp";
        public static final String COLUMN_RECIPIENT_RECEIPT = "receive_recipient_timestamp";
        public static final String COLUMN_NAME_I_AM_SENDER = "i_am_sender";

    }

    public static abstract class ChatEntry implements BaseColumns {
        public static final String TABLE_NAME = "chatEntryTable";

        public static final String COLUMN_CHAT_ID = "chat_id";
        public static final String COLUMN_NAME = "chat_name";
        public static final String COLUMN_GROUP_SERVER_ID = "group_server_id";
        public static final String COLUMN_IMAGE = "chat_image";
        public static final String COLUMN_CONTACT_ID = "contact_id";
        public static final String COLUMN_LAST_MESSAGE_ID = "last_message";
        public static final String COLUMN_UNREAD_COUNT = "unread_count";

    }

//    public static abstract class GroupEntry implements BaseColumns {
//        public static final String TABLE_NAME = "groupTable";
//
//        public static final String COLUMN_GROUP_ID = "group_id";
//        public static final String COLUMN_GROUP_NAME = "group_name";
//        public static final String COLUMN_GROUP_IMAGE = "group_image";
//        public static final String COLUMN_ADMIN_CONTACT_ID = "admin_contact_id";
//        public static final String COLUMN_LAST_MESSAGE_ID = "last_message";
//        public static final String COLUMN_UNREAD_COUNT = "unread_count";
//
//    }

    public static abstract class GroupUserEntry implements BaseColumns {
        public static final String TABLE_NAME = "groupUserTable";

        public static final String COLUMN_CHAT_ID = "chat_id";
        public static final String COLUMN_CONTACT_ID = "contact_id";

    }

    public static abstract class NewsEntry implements BaseColumns {
        public static final String TABLE_NAME = "newsTable";

        public static final String COLUMN_NEWS_ID = "newsId";
        public static final String COLUMN_WEBSITE = "website";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_IMAGE_CONTENT = "image_content";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SUMMARY = "summary";
        public static final String COLUMN_NEWS_LINK = "newsLink";
        public static final String COLUMN_CUSTOM_SUMMARY = "customSummary";
        public static final String COLUMN_PUBLISHED = "published";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_PUBLISH_EPOCH = "publishEpoch";

    }

}
