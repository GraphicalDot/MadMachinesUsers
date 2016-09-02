package com.sports.unity.Database;

import android.provider.BaseColumns;

/**
 * Created by madmachines on 1/9/15.
 */
public final class SportsUnityContract {

    public SportsUnityContract() {
    }

    public static abstract class ContactChatEntry implements BaseColumns {
        public static final String TABLE_NAME = "contactChatTable";

        public static final String COLUMN_ID = "incremental_id";
        public static final String COLUMN_JID = "jid";

        public static final String COLUMN_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME = "display_name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_STATUS = "status";

        public static final String COLUMN_AVAILABLE_STATUS = "available_status";
        public static final String COLUMN_BLOCK_USER = "block_user";
        public static final String COLUMN_UPDATE_REQUIRED = "update_required";

        public static final String COLUMN_LAST_MESSAGE_ID = "last_message_id";
        public static final String COLUMN_LAST_USED = "last_used";

        public static final String COLUMN_UNREAD_COUNT = "unread_count";
        public static final String COLUMN_MUTE_CONVERSATION = "mute_conversation";
        public static final String COLUMN_GROUP_CHAT = "group_chat";
        public static final String COLUMN_ROSTER_ENTRY = "roster_entry";
        public static final String COLUMN_PENDING_FRIEND_REQUEST = "pending_friend_request";

//        public static final String COLUMN_CHAT_ID = "chat_id";
//        public static final String COLUMN_GROUP_SERVER_ID = "group_server_id";
//        public static final String COLUMN_PEOPLE_AROUND_ME = "people_around_me";
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
        public static final String COLUMN_READ_STATUS = "message_read";
        public static final String COLUMN_MEDIA_FILE_NAME = "media_file_name";

    }

    public static abstract class GroupUserEntry implements BaseColumns {
        public static final String TABLE_NAME = "groupUserTable";

        public static final String COLUMN_CHAT_ID = "chat_id";
        public static final String COLUMN_CONTACT_ID = "contact_id";
        public static final String COLUMN_ADMIN = "group_admin";

    }

    public static abstract class FriendRequestEntry implements BaseColumns {
        public static final String TABLE_NAME = "friendRequeststable";

        public static final String COLUMN_CONTACT_ID = "contact_id";
        public static final String COLUMN_REQUEST_STANZA_ID = "request_stanza_id";
        public static final String COLUMN_SERVER_RECEIPT_FOR_REQUEST_STANZA = "server_receipt";

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
        public static final String COLUMN_FABICON_URL = "fabicon_url";

    }

    public static abstract class NewsDiscussDetailsEntry implements BaseColumns {
        public static final String TABLE_NAME = "newsDiscuss";

        public static final String COLUMN_ID = "news_discuss_id";       // auto incremented
        public static final String COLUMN_ARTICLE_ID = "articleId";
        public static final String COLUMN_GROUP_JID = "groupJID";
        public static final String COLUMN_ARTICLE_NAME = "articleName";
        public static final String COLUMN_POLL_STATUS = "poll";
        
    }

}
