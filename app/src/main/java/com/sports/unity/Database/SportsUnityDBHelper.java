package com.sports.unity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;

import static com.sports.unity.Database.SportsUnityContract.ContactsEntry;
import static com.sports.unity.Database.SportsUnityContract.MessagesEntry;
import static com.sports.unity.Database.SportsUnityContract.GroupEntry;
import static com.sports.unity.Database.SportsUnityContract.ChatEntry;

/**
 * Created by madmachines on 1/9/15.
 */
public class SportsUnityDBHelper extends SQLiteOpenHelper {

    private static long DUMMY_MESSAGE_ROW_ID = -1;
    public static final long DEFAULT_ENTRY_ID = -1;

    private static final String MIME_TYPE_TEXT = "text";
    private static final String MIME_TYPE_VIDEO = "video";
    private static final String MIME_TYPE_AUDIO = "audio";
    private static final String MIME_TYPE_VOICE = "voice";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "spu.db";

    private static final String COMMA_SEP = ",";


    private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            ContactsEntry.TABLE_NAME + "( " +
            ContactsEntry.COLUMN_CONTACT_ID + " " + "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " + COMMA_SEP +
            ContactsEntry.COLUMN_NAME + " " + "VARCHAR " + COMMA_SEP +
            ContactsEntry.COLUMN_PHONENUMBER + " VARCHAR UNIQUE " + COMMA_SEP +
            ContactsEntry.COLUMN_USER_IMAGE + " BLOB " + COMMA_SEP +
            ContactsEntry.COLUMN_STATUS + " VARCHAR " + COMMA_SEP +
            ContactsEntry.COLUMN_REGISTERED + " boolean);";

    private static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS " +
            MessagesEntry.TABLE_NAME + "( " +
            MessagesEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
            MessagesEntry.COLUMN_MESSAGE_ID + " " + "VARCHAR " + COMMA_SEP +
            MessagesEntry.COLUMN_DATA_TEXT + " " + "VARCHAR " + COMMA_SEP +
            MessagesEntry.COLUMN_DATA_MEDIA + " " + "BLOB " + COMMA_SEP +
            MessagesEntry.COLUMN_MIME_TYPE + " " + "VARCHAR " + COMMA_SEP +
            MessagesEntry.COLUMN_PHONENUMBER + " VARCHAR " + COMMA_SEP +
            MessagesEntry.COLUMN_CHAT_ID + " INTEGER " + COMMA_SEP +
            MessagesEntry.COLUMN_GROUP_ID + " INTEGER " + COMMA_SEP +
            MessagesEntry.COLUMN_CHAT_IS_GROUP + " boolean " + COMMA_SEP +
            MessagesEntry.COLUMN_SERVER_RECEIPT + " " + "DATETIME " + COMMA_SEP +
            MessagesEntry.COLUMN_RECIPIENT_RECEIPT + " DATETIME " + COMMA_SEP +
            MessagesEntry.COLUMN_SEND_TIMESTAMP + "  DATETIME " + COMMA_SEP +
            MessagesEntry.COLUMN_NAME_I_AM_SENDER + " boolean " + COMMA_SEP +
            MessagesEntry.COLUMN_RECEIVE_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    private static final String CREATE_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS " +
            GroupEntry.TABLE_NAME + "( " +
            GroupEntry.COLUMN_GROUP_ID + " " + "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " + COMMA_SEP +
            GroupEntry.COLUMN_GROUP_NAME + " " + "VARCHAR " + COMMA_SEP +
            GroupEntry.COLUMN_GROUP_IMAGE + " BLOB " + COMMA_SEP +
            GroupEntry.COLUMN_LAST_MESSAGE_ID + " INTEGER " + COMMA_SEP +
            GroupEntry.COLUMN_UNREAD_COUNT + " INTEGER);";

    private static final String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS " +
            ChatEntry.TABLE_NAME + "( " +
            ChatEntry.COLUMN_CHAT_ID + " " + "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " + COMMA_SEP +
            ChatEntry.COLUMN_NAME + " " + "VARCHAR " + COMMA_SEP +
            ChatEntry.COLUMN_LAST_MESSAGE_ID + " INTEGER " + COMMA_SEP +
            ChatEntry.COLUMN_CONTACT_ID + " INTEGER UNIQUE " + COMMA_SEP +
            ChatEntry.COLUMN_LAST_SEEN + " VARCHAR " + COMMA_SEP +
            ChatEntry.COLUMN_UNREAD_COUNT + " boolean);";

    private static final String DROP_CONTACTS_TABLE = "DROP TABLE IF EXISTS " + ContactsEntry.TABLE_NAME;

    private static final String CREATE_UNIQUE_INDEX = " CREATE UNIQUE INDEX " +
            ContactsEntry.COLUMN_UNIQUE_INDEX + " ON " +
            ContactsEntry.TABLE_NAME + "(" +
            ContactsEntry.COLUMN_PHONENUMBER + ")";

    private static SportsUnityDBHelper SPORTS_UNITY_DB_HELPER = null;

    synchronized public static SportsUnityDBHelper getInstance(Context context) {
        if (SPORTS_UNITY_DB_HELPER == null) {
            SPORTS_UNITY_DB_HELPER = new SportsUnityDBHelper(context);
        }
        return SPORTS_UNITY_DB_HELPER;
    }

    public static long getDummyMessageRowId() {
        return DUMMY_MESSAGE_ROW_ID;
    }

    private SportsUnityDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void addToContacts(String name, String number, boolean registered, String defaultStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsEntry.COLUMN_NAME, name);
        contentValues.put(ContactsEntry.COLUMN_PHONENUMBER, number);
        contentValues.put(ContactsEntry.COLUMN_REGISTERED, registered);
        contentValues.put(ContactsEntry.COLUMN_STATUS, defaultStatus);

        db.insert(ContactsEntry.TABLE_NAME, null, contentValues);
        Log.i("Added", " bitch");
//        db.close();

    }

    public ArrayList getAllContactsNumbersOnly() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> numbers = new ArrayList<>();
        String[] projection = {
                ContactsEntry.COLUMN_PHONENUMBER
        };

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,                 // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if (c.moveToFirst()) {
            do {
                numbers.add(c.getString(0));
            } while (c.moveToNext());
        }

        c.close();
//        db.close();

        return numbers;

    }

    public long addTextMessage(String msg, String number, boolean iamsender, String sentTime, String messageId, String serverTime, String recipientTime,
                               long chatID, long groupID, boolean isGroupChat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        values.put(MessagesEntry.COLUMN_PHONENUMBER, number);
        values.put(MessagesEntry.COLUMN_DATA_TEXT, msg);
        values.put(MessagesEntry.COLUMN_MESSAGE_ID, messageId);
        values.put(MessagesEntry.COLUMN_MIME_TYPE, MIME_TYPE_TEXT);
        values.put(MessagesEntry.COLUMN_NAME_I_AM_SENDER, iamsender);
        values.put(MessagesEntry.COLUMN_CHAT_ID, chatID);
        values.put(MessagesEntry.COLUMN_GROUP_ID, groupID);
        values.put(MessagesEntry.COLUMN_CHAT_IS_GROUP, isGroupChat);
        values.put(MessagesEntry.COLUMN_SEND_TIMESTAMP, sentTime);
        values.put(MessagesEntry.COLUMN_SERVER_RECEIPT, serverTime);
        values.put(MessagesEntry.COLUMN_RECIPIENT_RECEIPT, recipientTime);
        values.put(MessagesEntry.COLUMN_RECEIVE_TIMESTAMP, String.valueOf(today.format("%k:%M")));

        long lastMessageId = db.insert(MessagesEntry.TABLE_NAME, null, values);
        //Log.i("Time : ", today.format("%k:%M"));
        Log.i("Added", " Message");
//        db.close();
        return lastMessageId;

    }

    public void updateUnreadCount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int unread = getUnreadCount(id);

        ContentValues values = new ContentValues();
        values.put(ChatEntry.COLUMN_UNREAD_COUNT, ++unread);

        String selection = ChatEntry.COLUMN_CONTACT_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(id)};

        int count = db.update(
                ChatEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i(" unreadc :", String.valueOf(count));
//        db.close();
    }

    public int getUnreadCount(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ChatEntry.COLUMN_UNREAD_COUNT
        };

        String selection = ChatEntry.COLUMN_CONTACT_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor c = db.query(
                ChatEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if (c.moveToFirst()) {
            int count = c.getInt(0);
            return count;
        }

        c.close();
//        db.close();

        return 0;
    }

    public void clearUnreadCount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChatEntry.COLUMN_UNREAD_COUNT, 0);

        String selection = ChatEntry.COLUMN_CONTACT_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(id)};

        int count = db.update(
                ChatEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i(" unreadc :", String.valueOf(count));
//        db.close();
    }

    public ArrayList<Message> getMessages(long chatId, long groupId, boolean isGroupChat) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Message> list = new ArrayList<>();

        String[] projection = {
                MessagesEntry.COLUMN_PHONENUMBER,                               //0th column
                MessagesEntry.COLUMN_DATA_TEXT,                                 //1th column
                MessagesEntry.COLUMN_DATA_MEDIA,                                //2th column
                MessagesEntry.COLUMN_MIME_TYPE,                                 //3th column
                MessagesEntry.COLUMN_SERVER_RECEIPT,                            //4th column
                MessagesEntry.COLUMN_RECIPIENT_RECEIPT,                         //5th column
                MessagesEntry.COLUMN_NAME_I_AM_SENDER,                          //6th column
                MessagesEntry.COLUMN_RECEIVE_TIMESTAMP,                         //7th column
                MessagesEntry.COLUMN_SEND_TIMESTAMP                             //8th column
        };

        String selection = MessagesEntry.COLUMN_CHAT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(chatId)};

        Cursor c = db.query(
                MessagesEntry.TABLE_NAME,                 // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if (c.moveToFirst()) {
            do {
                boolean value = c.getInt(6) > 0;
                list.add(new Message(c.getString(0), c.getString(1), c.getBlob(2), c.getString(3), c.getString(4), c.getString(5), value, c.getString(7), c.getString(8)));
            } while (c.moveToNext());
        }
        c.close();
//        db.close();
        return list;

    }

    public int getContactId(String number) {

        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ContactsEntry.COLUMN_CONTACT_ID,                               //0th column
        };

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ?";
        String[] selectionArgs = {number};

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,                 // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if (c.moveToFirst()) {
            int id = c.getInt(0);
            return id;
        }
        c.close();
//        db.close();

        return 0;
    }

    public void updateStatus(long time, long chatId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChatEntry.COLUMN_LAST_SEEN, time);

        String selection = ChatEntry.COLUMN_CHAT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

        int count = db.update(
                ChatEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public void updateChatEntry(long messageId, long chatId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChatEntry.COLUMN_LAST_MESSAGE_ID, messageId);

        String selection = ChatEntry.COLUMN_CHAT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

        int count = db.update(
                ChatEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public long getChatEntryID(long contactID) {

        SQLiteDatabase db = getReadableDatabase();

        String projection[] = {
                ChatEntry.COLUMN_CHAT_ID
        };

        String selection = ChatEntry.COLUMN_CONTACT_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(contactID)};

        Cursor c = db.query(
                ChatEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        if (c.moveToFirst()) {
            return c.getInt(0);
        }

        c.close();
//        db.close();

        return DEFAULT_ENTRY_ID;
    }

    public class Message {
        public String number;
        public String textData;
        public byte[] media;
        public String mimeType;
        public String serverR;
        public String recipientR;
        public boolean iAmSender;
        public String sendTime;
        public String recieveTime;

        Message(String number, String textData, byte[] blob, String mimeType, String serverReceipt, String recipientReceipt, Boolean iamsender, String recievetime, String sendtime) {
            this.number = number;
            this.textData = textData;
            this.media = blob;
            this.mimeType = mimeType;
            this.serverR = serverReceipt;
            this.recipientR = recipientReceipt;
            this.iAmSender = iamsender;
            this.sendTime = sendtime;
            this.recieveTime = recievetime;

        }
    }

    public void updateServerReceived(String receiptId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesEntry.COLUMN_SERVER_RECEIPT, receiptId);

        String selection = MessagesEntry.COLUMN_MESSAGE_ID + " LIKE ? ";
        String[] selectionArgs = {receiptId};

        int count = db.update(
                MessagesEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i("RecipientReceipt :", String.valueOf(count));
//        db.close();


    }

    public void updateClientReceived(String receiptId) {
        SQLiteDatabase db = getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(MessagesEntry.COLUMN_RECIPIENT_RECEIPT, receiptId);

        String selection = MessagesEntry.COLUMN_MESSAGE_ID + " LIKE ? ";
        String[] selectionArgs = {receiptId};

        int count = db.update(
                MessagesEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i("RecipientReceipt :", String.valueOf(count));
//        db.close();

    }

    public void updateUserName(String contact, String name) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactsEntry.COLUMN_NAME, name);

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {contact};

        int count = db.update(
                ContactsEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i("updated :", String.valueOf(count));
//        db.close();

    }

    public void setPhonenumberAsName(String contact) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactsEntry.COLUMN_NAME, contact);

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {contact};

        int count = db.update(
                ContactsEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i("updated :", String.valueOf(count));
//        db.close();
    }

    public long createChatEntry(String name, long contactId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ChatEntry.COLUMN_NAME, name);
        values.put(ChatEntry.COLUMN_CONTACT_ID, contactId);
        values.put(ChatEntry.COLUMN_UNREAD_COUNT, 0);

        long id = db.insert(ChatEntry.TABLE_NAME, null, values);
//        db.close();
        return id;
    }

    public Contacts getContact(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactsEntry.COLUMN_NAME,
                ContactsEntry.COLUMN_PHONENUMBER,
                ContactsEntry.COLUMN_REGISTERED,
                ContactsEntry.COLUMN_USER_IMAGE,
                ContactsEntry.COLUMN_CONTACT_ID,
                ContactsEntry.COLUMN_STATUS
        };
        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ?";
        String[] selectionArgs = {phoneNumber};

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        Contacts contacts = null;
        if (c.moveToFirst()) {
            boolean value = c.getInt(2) > 0;
            contacts = new Contacts(c.getString(0), c.getString(1), value, c.getBlob(3), c.getInt(4), c.getString(5));
        }

        c.close();
//        db.close();

        return contacts;

    }

    public Contacts getContact(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactsEntry.COLUMN_NAME,
                ContactsEntry.COLUMN_PHONENUMBER,
                ContactsEntry.COLUMN_REGISTERED,
                ContactsEntry.COLUMN_USER_IMAGE,
                ContactsEntry.COLUMN_CONTACT_ID,
                ContactsEntry.COLUMN_STATUS
        };
        String selection = ContactsEntry.COLUMN_CONTACT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        Contacts contacts = null;
        if (c.moveToFirst()) {
            boolean value = c.getInt(2) > 0;
            contacts = new Contacts(c.getString(0), c.getString(1), value, c.getBlob(3), c.getInt(4), c.getString(5));
        }

        c.close();
//        db.close();

        return contacts;

    }

    public ArrayList<Contacts> getContactList() {

        ArrayList<Contacts> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactsEntry.COLUMN_NAME,
                ContactsEntry.COLUMN_PHONENUMBER,
                ContactsEntry.COLUMN_REGISTERED,
                ContactsEntry.COLUMN_USER_IMAGE,
                ContactsEntry.COLUMN_CONTACT_ID,
                ContactsEntry.COLUMN_STATUS
        };
        String sortOrder =
                ContactsEntry.COLUMN_NAME + " ASC ";

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        if (c.moveToFirst()) {
            do {
                boolean value = c.getInt(2) > 0;
                list.add(new Contacts(c.getString(0), c.getString(1), value, c.getBlob(3), c.getInt(4), c.getString(5)));
            } while (c.moveToNext());
        }
        c.close();
//        db.close();
        return list;

    }

    public class Contacts {
        public String name;
        public String jid;
        public boolean registered;
        public byte[] image;
        public String status;
        public long id;

        Contacts(String name, String phoneNumber, Boolean registered, byte[] userimage, long cId, String status) {
            this.name = name;
            this.jid = phoneNumber;
            this.registered = registered;
            this.image = userimage;
            this.status = status;
            this.id = cId;
        }

    }

    public ArrayList<Chats> getChatScreenList() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Chats> list = new ArrayList<>();

//        {
//            String query = "SELECT " + ChatEntry.COLUMN_CHAT_ID + " , " + ChatEntry.COLUMN_NAME + " , " + ChatEntry.COLUMN_LAST_MESSAGE_ID + " , " + ChatEntry.COLUMN_CONTACT_ID +
//                    " FROM " + ChatEntry.TABLE_NAME;
//            Cursor cursor = db.rawQuery(query, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    Log.i("Chat entry", "Item " + cursor.getInt(0) + " : " + cursor.getString(1) + " : " + cursor.getInt(2) + " : " + cursor.getInt(3));
//                } while (cursor.moveToNext());
//            }
//
//        }
//        {
//            String query = "SELECT " + MessagesEntry.COLUMN_ID + " , " + MessagesEntry.COLUMN_PHONENUMBER + " , " + MessagesEntry.COLUMN_DATA_TEXT + " , " + MessagesEntry.COLUMN_CHAT_ID +
//                    " FROM " + MessagesEntry.TABLE_NAME;
//            Cursor cursor = db.rawQuery(query, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    Log.i("Message entry", "Item " + cursor.getInt(0) + " : " + cursor.getString(1) + " : " + cursor.getString(2) + " : " + cursor.getInt(3));
//                } while (cursor.moveToNext());
//            }
//
//        }
//        {
//            String query = "SELECT " + ContactsEntry.COLUMN_CONTACT_ID + " , " + ContactsEntry.COLUMN_PHONENUMBER + " , " + ContactsEntry.COLUMN_NAME +
//                    " FROM " + ContactsEntry.TABLE_NAME;
//            Cursor cursor = db.rawQuery(query, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    Log.i("Contact entry", "Item " + cursor.getInt(0) + " : " + cursor.getString(1) + " : " + cursor.getString(2));
//                } while (cursor.moveToNext());
//            }
//
//        }

        String subQuery = "( SELECT " + ChatEntry.COLUMN_UNREAD_COUNT + " ," + ChatEntry.COLUMN_NAME + " ," + ChatEntry.COLUMN_CONTACT_ID + " ," +
                ChatEntry.COLUMN_LAST_MESSAGE_ID + " ," + MessagesEntry.COLUMN_DATA_TEXT + " ," + MessagesEntry.COLUMN_DATA_MEDIA + " ," +
                MessagesEntry.COLUMN_MIME_TYPE + " ," + MessagesEntry.COLUMN_SEND_TIMESTAMP + " ," + MessagesEntry.COLUMN_RECEIVE_TIMESTAMP + " , A." + ChatEntry.COLUMN_CHAT_ID +
                " FROM " + ChatEntry.TABLE_NAME + " A INNER JOIN " + MessagesEntry.TABLE_NAME + " B ON " + ChatEntry.COLUMN_LAST_MESSAGE_ID + " = " + MessagesEntry.COLUMN_ID + " ) ";

        String selectQuery = " SELECT B.* , A." + ContactsEntry.COLUMN_USER_IMAGE + " FROM " +
                ContactsEntry.TABLE_NAME + " A INNER JOIN " + subQuery + " B  ON A." + ContactsEntry.COLUMN_CONTACT_ID + " = B." + ChatEntry.COLUMN_CONTACT_ID;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Chats(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4),
                        cursor.getBlob(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9), cursor.getBlob(10)));
            } while (cursor.moveToNext());
        }

        cursor.close();
//        db.close();

        return list;

    }

    public class Chats {

        public int unreadCount;
        public String userName;
        public int contactId;
        public int lastMessage;
        public String data;
        public byte[] media;
        public String mimeType;
        public String sent;
        public String recieved;
        public byte[] userImage;
        public int chatid;


        public Chats(int unread, String name, int contactId, int lastMessageId, String dataText, byte[] dataMedia, String dataType, String sentTime, String recieveTime, int chatId, byte[] userImage) {

            this.unreadCount = unread;
            this.userName = name;
            this.contactId = contactId;
            this.lastMessage = lastMessageId;
            this.data = dataText;
            this.media = dataMedia;
            this.mimeType = dataType;
            this.sent = sentTime;
            this.recieved = recieveTime;
            this.userImage = userImage;
            this.chatid = chatId;
        }
    }


    public ArrayList readContactNumbers() {

        ArrayList<String> list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactsEntry.COLUMN_PHONENUMBER,
        };

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,                // The table to query
                projection,                              // The columns to return
                null,                                    // The columns for the WHERE clause
                null,                                    // The values for the WHERE clause
                null,                                    // don't group the rows
                null,                                    // don't filter by row groups
                null                                     // The sort order
        );
        if (c.moveToFirst()) {
            do {
                list.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
//        db.close();
        return list;

    }


    public void clearChat(long chatId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = MessagesEntry.COLUMN_CHAT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

        if (chatId != DEFAULT_ENTRY_ID) {
            db.delete(MessagesEntry.TABLE_NAME, selection, selectionArgs);
            updateChatEntry(getDummyMessageRowId(), chatId);
//            sportsUnityDBHelper.updateChatEntry(SportsUnityDBHelper.getDummyMessageRowId(), chatID);
        }
//        db.close();
    }

    public void updateContacts(String number, byte[] userImage, String status) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactsEntry.COLUMN_REGISTERED, true);
        values.put(ContactsEntry.COLUMN_USER_IMAGE, userImage);
        values.put(ContactsEntry.COLUMN_STATUS, status);

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {number};

        int count = db.update(
                ContactsEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i("updated :", String.valueOf(count));
//        db.close();
    }


    public ArrayList<GroupParticipants> readContactNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GroupParticipants> list = new ArrayList<>();
        //HashMap hm = new HashMap();
        String[] projection = {
                ContactsEntry.COLUMN_NAME,
                ContactsEntry.COLUMN_PHONENUMBER
        };

        String selection = ContactsEntry.COLUMN_REGISTERED + " LIKE ? ";
        String[] selectionArgs = {"1"};

        String sortOrder =
                ContactsEntry.COLUMN_NAME + " ASC ";
        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,                 // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        if (c.moveToFirst()) {
            do {
                list.add(new GroupParticipants(c.getString(0), c.getString(1)));
            } while (c.moveToNext());
        }

        c.close();
//        db.close();

        return list;

    }

    public static class GroupParticipants {
        public String name;
        public String number;

        public GroupParticipants(String name, String num) {

            this.name = name;
            this.number = num;
        }
    }

    public String getJabberName(String jid) {

        Log.i("gettingjb :", "getting jabber name");
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ContactsEntry.COLUMN_NAME,
        };

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {jid};


        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,                 // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if (c.moveToFirst()) {
            String name = c.getString(0);
            return name;
        }
        c.close();
//        db.close();
        return null;

    }

    public void addDummyMessageIfNotExist() {
        if (DUMMY_MESSAGE_ROW_ID == DEFAULT_ENTRY_ID) {
            SQLiteDatabase db = getReadableDatabase();
            String[] projection = {
                    MessagesEntry.COLUMN_ID
            };

            String selection = MessagesEntry.COLUMN_CHAT_ID + " = ? and " + MessagesEntry.COLUMN_GROUP_ID + " = ? ";
            String[] selectionArgs = {String.valueOf(DEFAULT_ENTRY_ID), String.valueOf(DEFAULT_ENTRY_ID)};

            Cursor c = db.query(
                    MessagesEntry.TABLE_NAME,                 // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                      // The sort order
            );

            boolean exist = false;
            if (c.moveToFirst()) {
                exist = true;
                DUMMY_MESSAGE_ROW_ID = c.getInt(0);
            } else {
                //nothing
            }
            c.close();
//        db.close();

            if (!exist) {
                DUMMY_MESSAGE_ROW_ID = addTextMessage("", "", false, "", "", "", "", DEFAULT_ENTRY_ID, DEFAULT_ENTRY_ID, false);
            } else {
                //nothing
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_UNIQUE_INDEX);
        db.execSQL(CREATE_GROUP_TABLE);
        db.execSQL(CREATE_CHAT_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_CONTACTS_TABLE);
        onCreate(db);
    }
}
