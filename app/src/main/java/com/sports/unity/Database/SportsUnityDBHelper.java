package com.sports.unity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;

import static com.sports.unity.Database.SportsUnityContract.ContactsEntry;
import static com.sports.unity.Database.SportsUnityContract.MessagesEntry;
import static com.sports.unity.Database.SportsUnityContract.ChatEntry;
import static com.sports.unity.Database.SportsUnityContract.GroupUserEntry;

/**
 * Created by madmachines on 1/9/15.
 */
public class SportsUnityDBHelper extends SQLiteOpenHelper {

    private static long DUMMY_MESSAGE_ROW_ID = -1;
    public static final long DEFAULT_ENTRY_ID = -1;
    public static final boolean DEFAULT_READ_STATUS = false;

    public static final String DEFAULT_GROUP_SERVER_ID = "NOT_GROUP_CHAT";

    public static final String MIME_TYPE_TEXT = "t";
    public static final String MIME_TYPE_STICKER = "s";
    public static final String MIME_TYPE_IMAGE = "i";
    public static final String MIME_TYPE_VIDEO = "v";
    public static final String MIME_TYPE_AUDIO = "a";

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
            ContactsEntry.COLUMN_AVAILABLE + " boolean DEFAULT true " + COMMA_SEP +
            ContactsEntry.COLUMN_REGISTERED + " boolean" + COMMA_SEP +
            ContactsEntry.COLUMN_BLOCK_USER + " boolean DEFAULT false " +
            ");";

    private static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS " +
            MessagesEntry.TABLE_NAME + "( " +
            MessagesEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + COMMA_SEP +
            MessagesEntry.COLUMN_MESSAGE_ID + " " + "VARCHAR " + COMMA_SEP +
            MessagesEntry.COLUMN_DATA_TEXT + " " + "VARCHAR " + COMMA_SEP +
            MessagesEntry.COLUMN_DATA_MEDIA + " " + "BLOB " + COMMA_SEP +
            MessagesEntry.COLUMN_MIME_TYPE + " " + "VARCHAR " + COMMA_SEP +
            MessagesEntry.COLUMN_PHONENUMBER + " VARCHAR " + COMMA_SEP +
            MessagesEntry.COLUMN_CHAT_ID + " INTEGER " + COMMA_SEP +
            MessagesEntry.COLUMN_SERVER_RECEIPT + " " + "DATETIME " + COMMA_SEP +
            MessagesEntry.COLUMN_RECIPIENT_RECEIPT + " DATETIME " + COMMA_SEP +
            MessagesEntry.COLUMN_SEND_TIMESTAMP + "  DATETIME " + COMMA_SEP +
            MessagesEntry.COLUMN_NAME_I_AM_SENDER + " boolean " + COMMA_SEP +
            MessagesEntry.COLUMN_RECEIVE_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP " + COMMA_SEP +
            MessagesEntry.COLUMN_READ_STATUS + " boolean" + COMMA_SEP +
            MessagesEntry.COLUMN_MEDIA_FILE_NAME + " VARCHAR default NULL " +
            ");";

    private static final String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS " +
            ChatEntry.TABLE_NAME + "( " +
            ChatEntry.COLUMN_CHAT_ID + " " + "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " + COMMA_SEP +
            ChatEntry.COLUMN_NAME + " " + "VARCHAR " + COMMA_SEP +
            ChatEntry.COLUMN_IMAGE + " " + " BLOB " + COMMA_SEP +
            ChatEntry.COLUMN_GROUP_SERVER_ID + " " + " VARCHAR " + COMMA_SEP +
            ChatEntry.COLUMN_LAST_MESSAGE_ID + " INTEGER " + COMMA_SEP +
            ChatEntry.COLUMN_CONTACT_ID + " INTEGER " + COMMA_SEP +
            ChatEntry.COLUMN_MUTE_CONVERSATION + " boolean DEFAULT false " + COMMA_SEP +
            ChatEntry.COLUMN_UNREAD_COUNT + " boolean " + COMMA_SEP +
            ChatEntry.COLUMN_LAST_USED + " DATETIME DEFAULT CURRENT_TIMESTAMP " + COMMA_SEP +
            ChatEntry.COLUMN_PEOPLE_AROUND_ME + " boolean " +
            ");";

//    private static final String CREATE_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS " +
//            GroupEntry.TABLE_NAME + "( " +
//            GroupEntry.COLUMN_GROUP_ID + " " + "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " + COMMA_SEP +
//            GroupEntry.COLUMN_GROUP_NAME + " " + "VARCHAR " + COMMA_SEP +
//            GroupEntry.COLUMN_GROUP_IMAGE + " BLOB " + COMMA_SEP +
//            GroupEntry.COLUMN_ADMIN_CONTACT_ID + " INTEGER " + COMMA_SEP +
//            GroupEntry.COLUMN_LAST_MESSAGE_ID + " INTEGER " + COMMA_SEP +
//            GroupEntry.COLUMN_UNREAD_COUNT + " INTEGER);";

    private static final String CREATE_GROUP_USER_TABLE = "CREATE TABLE IF NOT EXISTS " +
            GroupUserEntry.TABLE_NAME + "( " +
            GroupUserEntry.COLUMN_CHAT_ID + " INTEGER " + COMMA_SEP +
            GroupUserEntry.COLUMN_ADMIN + " INTEGER " + COMMA_SEP +
            GroupUserEntry.COLUMN_CONTACT_ID + " " + " INTEGER );";

    private static final String DROP_CONTACTS_TABLE = "DROP TABLE IF EXISTS " + ContactsEntry.TABLE_NAME;
    private static final String DROP_MESSAGE_TABLE = "DROP TABLE IF EXISTS " + MessagesEntry.TABLE_NAME;
    private static final String DROP_CHAT_TABLE = "DROP TABLE IF EXISTS " + ChatEntry.TABLE_NAME;
    private static final String DROP_GROUP_USER_TABLE = "DROP TABLE IF EXISTS " + GroupUserEntry.TABLE_NAME;

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

    private ArrayList<Contacts> allContacts = null;

    private SportsUnityDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void createGroupUserEntry(long chatId, ArrayList<Long> selectedMembers) {

        SQLiteDatabase db = getWritableDatabase();

        for (Long id
                : selectedMembers) {

            ContentValues values = new ContentValues();
            values.put(GroupUserEntry.COLUMN_CHAT_ID, chatId);
            values.put(GroupUserEntry.COLUMN_CONTACT_ID, String.valueOf(id));
            values.put(GroupUserEntry.COLUMN_ADMIN, 0);

            db.insert(GroupUserEntry.TABLE_NAME, null, values);
        }

    }

    public void addToContacts(String name, String number, boolean registered, String defaultStatus, boolean available) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsEntry.COLUMN_NAME, name);
        contentValues.put(ContactsEntry.COLUMN_PHONENUMBER, number);
        contentValues.put(ContactsEntry.COLUMN_REGISTERED, registered);
        contentValues.put(ContactsEntry.COLUMN_STATUS, defaultStatus);
        contentValues.put(ContactsEntry.COLUMN_AVAILABLE, available);

        db.insert(ContactsEntry.TABLE_NAME, null, contentValues);
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
        return numbers;

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
        return 0;
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
    }

    public void updateUserName(String contact, String name) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactsEntry.COLUMN_NAME, name);
        values.put(ContactsEntry.COLUMN_AVAILABLE, true);

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {contact};

        int count = db.update(
                ContactsEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i("updated :", String.valueOf(count));
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
        return contacts;

    }

    public Contacts getContact(long contactId) {
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
        String[] selectionArgs = {String.valueOf(contactId)};

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
        return contacts;

    }

    public ArrayList<Contacts> getContactList_AvailableOnly(boolean forceLoad) {
        if (forceLoad == true || allContacts == null) {
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

            String selection = ContactsEntry.COLUMN_AVAILABLE + " LIKE ? ";
            String[] selectionArgs = {"1"};

            String sortOrder =
                    ContactsEntry.COLUMN_NAME + " ASC ";

            Cursor c = db.query(
                    ContactsEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
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

            allContacts = list;
        } else {
            //nothing
        }
        return allContacts;
    }

    public ArrayList<Contacts> getContactList(boolean registeredOnly) {

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

        String selection = ContactsEntry.COLUMN_REGISTERED + " LIKE ? and " + ContactsEntry.COLUMN_AVAILABLE + " LIKE ?";
        String[] selectionArgs = {"1", "1"};

        String sortOrder = ContactsEntry.COLUMN_NAME + " COLLATE NOCASE ASC ";

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
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
        return list;

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
        return list;
    }

    public void updateContacts(String number, byte[] userImage, String status, boolean available) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactsEntry.COLUMN_REGISTERED, true);
        values.put(ContactsEntry.COLUMN_USER_IMAGE, userImage);
        values.put(ContactsEntry.COLUMN_STATUS, status);
        values.put(ContactsEntry.COLUMN_AVAILABLE, status);

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {number};

        int count = db.update(
                ContactsEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i("updated :", String.valueOf(count));
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
    }

    public GroupParticipants getGroupParticipants(long chatId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + ContactsEntry.COLUMN_NAME + ", " + ContactsEntry.COLUMN_PHONENUMBER + ", " + ContactsEntry.COLUMN_REGISTERED + ", " +
                ContactsEntry.COLUMN_USER_IMAGE + ", A." + ContactsEntry.COLUMN_CONTACT_ID + ", " + ContactsEntry.COLUMN_STATUS +
                " FROM " + ContactsEntry.TABLE_NAME + " A INNER JOIN " + GroupUserEntry.TABLE_NAME + " B ON A." + ContactsEntry.COLUMN_CONTACT_ID + " = B." + GroupUserEntry.COLUMN_CONTACT_ID +
                " WHERE B." + GroupUserEntry.COLUMN_CHAT_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(chatId)};

        Cursor cursor = db.rawQuery(selectQuery, selectionArgs);

        ArrayList<Contacts> users = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                boolean value = cursor.getInt(2) > 0;
                users.add(new Contacts(cursor.getString(0), cursor.getString(1), value, cursor.getBlob(3), cursor.getInt(4), cursor.getString(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        GroupParticipants groupParticipants = new GroupParticipants(chatId, users);
        return groupParticipants;
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
        return null;
    }

    public void updateChatEntryName(int contactId, String name, String groupServerId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChatEntry.COLUMN_NAME, name);

        String selection = ChatEntry.COLUMN_CONTACT_ID + " = ? and " + ChatEntry.COLUMN_GROUP_SERVER_ID + " = ? ";
        String selectionArgs[] = {String.valueOf(contactId), groupServerId};

        int count = db.update(
                ChatEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }


    public ArrayList<Message> getMessages(long chatId) {
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
                MessagesEntry.COLUMN_SEND_TIMESTAMP,                            //8th column
                MessagesEntry.COLUMN_ID,                                        //9th column
                MessagesEntry.COLUMN_READ_STATUS,                               //10th column
                MessagesEntry.COLUMN_MEDIA_FILE_NAME,
                MessagesEntry.COLUMN_MESSAGE_ID
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
                int id = c.getInt(9);
                if (id != DUMMY_MESSAGE_ROW_ID) {
                    boolean read = c.getInt(10) > 0;
                    list.add(new Message(c.getInt(9), c.getString(0), c.getString(1), c.getBlob(2), c.getString(3), c.getString(4), c.getString(5), value, c.getString(7), c.getString(8), read, id, c.getString(11), c.getString(12)));
                } else {
                    //nothing
                }
            } while (c.moveToNext());
        }
        c.close();
        return list;

    }

    public long addMessage(String msg, String mimeType, String number, boolean iamsender, String sentTime, String messageId, String serverTime, String recipientTime,
                           long chatID, boolean read) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MessagesEntry.COLUMN_PHONENUMBER, number);
        values.put(MessagesEntry.COLUMN_DATA_TEXT, msg);
        values.put(MessagesEntry.COLUMN_MESSAGE_ID, messageId);
        values.put(MessagesEntry.COLUMN_MIME_TYPE, mimeType);
        values.put(MessagesEntry.COLUMN_NAME_I_AM_SENDER, iamsender);
        values.put(MessagesEntry.COLUMN_CHAT_ID, chatID);
        values.put(MessagesEntry.COLUMN_SEND_TIMESTAMP, sentTime);
        values.put(MessagesEntry.COLUMN_SERVER_RECEIPT, serverTime);
        values.put(MessagesEntry.COLUMN_RECIPIENT_RECEIPT, recipientTime);
        values.put(MessagesEntry.COLUMN_RECEIVE_TIMESTAMP, CommonUtil.getCurrentGMTTimeInEpoch());
        values.put(MessagesEntry.COLUMN_READ_STATUS, read);

        long lastMessageId = db.insert(MessagesEntry.TABLE_NAME, null, values);
        return lastMessageId;

    }

    public long addMediaMessage(String message, String mimeType, String number, boolean iamsender, String sentTime, String messageId, String serverTime, String recipientTime,
                                long chatID, boolean read, String mediaFileName, byte[] mediaThumbnail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MessagesEntry.COLUMN_PHONENUMBER, number);
        values.put(MessagesEntry.COLUMN_DATA_TEXT, message);
        values.put(MessagesEntry.COLUMN_DATA_MEDIA, mediaThumbnail);
        values.put(MessagesEntry.COLUMN_MESSAGE_ID, messageId);
        values.put(MessagesEntry.COLUMN_MIME_TYPE, mimeType);
        values.put(MessagesEntry.COLUMN_NAME_I_AM_SENDER, iamsender);
        values.put(MessagesEntry.COLUMN_CHAT_ID, chatID);
        values.put(MessagesEntry.COLUMN_SEND_TIMESTAMP, sentTime);
        values.put(MessagesEntry.COLUMN_SERVER_RECEIPT, serverTime);
        values.put(MessagesEntry.COLUMN_RECIPIENT_RECEIPT, recipientTime);
        values.put(MessagesEntry.COLUMN_RECEIVE_TIMESTAMP, CommonUtil.getCurrentGMTTimeInEpoch());
        values.put(MessagesEntry.COLUMN_READ_STATUS, read);
        values.put(MessagesEntry.COLUMN_MEDIA_FILE_NAME, mediaFileName);

        long lastMessageId = db.insert(MessagesEntry.TABLE_NAME, null, values);
        return lastMessageId;

    }

    public void updateReadStatus(String messageStanzaId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesEntry.COLUMN_READ_STATUS, "1");

        String selection = MessagesEntry.COLUMN_MESSAGE_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(messageStanzaId)};

        int count = db.update(
                MessagesEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public void updateMediaMessage_ContentUploaded(long messageId, String stanzaId, String checksum) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesEntry.COLUMN_MESSAGE_ID, stanzaId);
        values.put(MessagesEntry.COLUMN_DATA_TEXT, checksum);

        String selection = MessagesEntry.COLUMN_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(messageId)};

        int count = db.update(
                MessagesEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public void updateMediaMessage_ContentDownloaded(long messageId, String filename) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesEntry.COLUMN_MEDIA_FILE_NAME, filename);

        String selection = MessagesEntry.COLUMN_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(messageId)};

        int count = db.update(
                MessagesEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
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
    }

    public void updateUnreadCount(long chatId, String groupServerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int unread = getUnreadCount(chatId, groupServerId);

        ContentValues values = new ContentValues();
        values.put(ChatEntry.COLUMN_UNREAD_COUNT, ++unread);

        String selection = ChatEntry.COLUMN_CHAT_ID + " LIKE ? and " + ChatEntry.COLUMN_GROUP_SERVER_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(chatId), groupServerId};

        int count = db.update(
                ChatEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i(" unreadc :", String.valueOf(count));
    }

    public int getUnreadCount(long chatId, String groupServerId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ChatEntry.COLUMN_UNREAD_COUNT
        };

        String selection = ChatEntry.COLUMN_CHAT_ID + " LIKE ? and " + ChatEntry.COLUMN_GROUP_SERVER_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(chatId), groupServerId};

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
        return 0;
    }

    public void clearUnreadCount(long chatId, String groupServerId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChatEntry.COLUMN_UNREAD_COUNT, 0);

        String selection = ChatEntry.COLUMN_CHAT_ID + " LIKE ? and " + ChatEntry.COLUMN_GROUP_SERVER_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(chatId), groupServerId};

        int count = db.update(
                ChatEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i(" unreadc :", String.valueOf(count));
    }

//    public void updateStatus(long time, long chatId) {
//        SQLiteDatabase db = getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(ChatEntry.COLUMN_LAST_SEEN, time);
//
//        String selection = ChatEntry.COLUMN_CHAT_ID + " = ? ";
//        String[] selectionArgs = {String.valueOf(chatId)};
//
//        int count = db.update(
//                ChatEntry.TABLE_NAME,
//                values,
//                selection,
//                selectionArgs);
//    }

    public void updateChatEntry(long messageId, long chatId, String groupServerId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChatEntry.COLUMN_LAST_MESSAGE_ID, messageId);
        values.put(ChatEntry.COLUMN_LAST_USED, CommonUtil.getCurrentGMTTimeInEpoch());

        String selection = ChatEntry.COLUMN_CHAT_ID + " LIKE ? and " + ChatEntry.COLUMN_GROUP_SERVER_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(chatId), groupServerId};

        int count = db.update(
                ChatEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public long getChatEntryID(long contactID, String groupServerId) {

        SQLiteDatabase db = getReadableDatabase();

        String projection[] = {
                ChatEntry.COLUMN_CHAT_ID
        };

        String selection = ChatEntry.COLUMN_CONTACT_ID + " LIKE ? and " + ChatEntry.COLUMN_GROUP_SERVER_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(contactID), groupServerId};

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
        return DEFAULT_ENTRY_ID;
    }

    public long getChatEntryID(String groupServerId) {

        SQLiteDatabase db = getReadableDatabase();

        String projection[] = {
                ChatEntry.COLUMN_CHAT_ID
        };

        String selection = ChatEntry.COLUMN_GROUP_SERVER_ID + " LIKE ? ";
        String[] selectionArgs = {groupServerId};

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
        return DEFAULT_ENTRY_ID;
    }

    public long createChatEntry(String name, long contactId, boolean others) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ChatEntry.COLUMN_NAME, name);
        values.put(ChatEntry.COLUMN_CONTACT_ID, contactId);
        values.put(ChatEntry.COLUMN_UNREAD_COUNT, 0);
        values.put(ChatEntry.COLUMN_PEOPLE_AROUND_ME, others);
        values.put(ChatEntry.COLUMN_GROUP_SERVER_ID, DEFAULT_GROUP_SERVER_ID);

        long id = db.insert(ChatEntry.TABLE_NAME, null, values);
        return id;
    }

    public String getName(String number) {

        SQLiteDatabase db = this.getReadableDatabase();

        String projection[] = {
                ContactsEntry.COLUMN_NAME
        };

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(number)};

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        if (c.moveToFirst()) {
            return c.getString(0);
        }

        c.close();


        return null;
    }

    public long createGroupChatEntry(String subject, long contactId, byte[] image, String groupServerId) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ChatEntry.COLUMN_NAME, subject);
        values.put(ChatEntry.COLUMN_CONTACT_ID, contactId);
        values.put(ChatEntry.COLUMN_UNREAD_COUNT, 0);

        values.put(ChatEntry.COLUMN_GROUP_SERVER_ID, groupServerId);
        values.put(ChatEntry.COLUMN_IMAGE, image);

        long id = db.insert(ChatEntry.TABLE_NAME, null, values);
        return id;
    }

    public ArrayList<Chats> getChatList(boolean nearByChat) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Chats> list = new ArrayList<>();

//        {
//            String query = "SELECT " + ChatEntry.COLUMN_CHAT_ID + " , " + ChatEntry.COLUMN_NAME + " , " + ChatEntry.COLUMN_LAST_MESSAGE_ID + " , " + ChatEntry.COLUMN_CONTACT_ID + " , " + ChatEntry.COLUMN_GROUP_SERVER_ID +
//                    " FROM " + ChatEntry.TABLE_NAME;
//            Cursor cursor = db.rawQuery(query, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    Log.i("Chat entry", "Item " + cursor.getInt(0) + " : " + cursor.getString(1) + " : " + cursor.getInt(2) + " : " + cursor.getInt(3) + " : " + cursor.getString(4));
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
//        {
//            String query = "SELECT " + GroupUserEntry.COLUMN_CONTACT_ID + " , " + GroupUserEntry.COLUMN_CHAT_ID +
//                    " FROM " + GroupUserEntry.TABLE_NAME;
//            Cursor cursor = db.rawQuery(query, null);
//            if (cursor.moveToFirst()) {
//                do {
//                    Log.i("Group user entry", "Item " + cursor.getInt(0) + " : " + cursor.getInt(1));
//                } while (cursor.moveToNext());
//            }
//
//        }
        {
            String subQuery = "";
            if (nearByChat) {
                subQuery = "( SELECT " + ChatEntry.COLUMN_UNREAD_COUNT + " ," + ChatEntry.COLUMN_NAME + " ," + ChatEntry.COLUMN_CONTACT_ID + " ," +
                        ChatEntry.COLUMN_LAST_MESSAGE_ID + " ," + MessagesEntry.COLUMN_DATA_TEXT + " ," + MessagesEntry.COLUMN_DATA_MEDIA + " ," +
                        MessagesEntry.COLUMN_MIME_TYPE + " ," + MessagesEntry.COLUMN_SEND_TIMESTAMP + " ," + MessagesEntry.COLUMN_RECEIVE_TIMESTAMP + " , A." +
                        ChatEntry.COLUMN_CHAT_ID + " ," + ChatEntry.COLUMN_GROUP_SERVER_ID + " ," + ChatEntry.COLUMN_IMAGE + "," + ChatEntry.COLUMN_MUTE_CONVERSATION +
                        "," + ChatEntry.COLUMN_LAST_USED +
                        " FROM " + ChatEntry.TABLE_NAME + " A INNER JOIN " + MessagesEntry.TABLE_NAME + " B ON " + ChatEntry.COLUMN_LAST_MESSAGE_ID + " = " + MessagesEntry.COLUMN_ID +
                        " WHERE " + ChatEntry.COLUMN_PEOPLE_AROUND_ME + " = 1 " + " ) ";
            } else {
                subQuery = "( SELECT " + ChatEntry.COLUMN_UNREAD_COUNT + " ," + ChatEntry.COLUMN_NAME + " ," + ChatEntry.COLUMN_CONTACT_ID + " ," +
                        ChatEntry.COLUMN_LAST_MESSAGE_ID + " ," + MessagesEntry.COLUMN_DATA_TEXT + " ," + MessagesEntry.COLUMN_DATA_MEDIA + " ," +
                        MessagesEntry.COLUMN_MIME_TYPE + " ," + MessagesEntry.COLUMN_SEND_TIMESTAMP + " ," + MessagesEntry.COLUMN_RECEIVE_TIMESTAMP + " , A." +
                        ChatEntry.COLUMN_CHAT_ID + " ," + ChatEntry.COLUMN_GROUP_SERVER_ID + " ," + ChatEntry.COLUMN_IMAGE + "," + ChatEntry.COLUMN_MUTE_CONVERSATION +
                        "," + ChatEntry.COLUMN_LAST_USED +
                        " FROM " + ChatEntry.TABLE_NAME + " A INNER JOIN " + MessagesEntry.TABLE_NAME + " B ON " + ChatEntry.COLUMN_LAST_MESSAGE_ID + " = " + MessagesEntry.COLUMN_ID +
                        " WHERE " + ChatEntry.COLUMN_PEOPLE_AROUND_ME + " = 0 " + " ) ";
            }


            String selectQuery = " SELECT B.* , A." + ContactsEntry.COLUMN_USER_IMAGE + "," + ContactsEntry.COLUMN_BLOCK_USER + " FROM " +
                    ContactsEntry.TABLE_NAME + " A INNER JOIN " + subQuery + " B  ON A." + ContactsEntry.COLUMN_CONTACT_ID + " = B." + ChatEntry.COLUMN_CONTACT_ID
                    + " order by " + ChatEntry.COLUMN_LAST_USED + " DESC";

            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    boolean value_mute = cursor.getInt(12) == 1;
                    boolean value_block = cursor.getInt(15) == 1;
                    list.add(new Chats(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                            cursor.getInt(3), cursor.getString(4), cursor.getBlob(5),
                            cursor.getString(6), cursor.getString(7), cursor.getString(8),
                            cursor.getInt(9), cursor.getString(10), cursor.getBlob(11), value_mute, cursor.getBlob(14), value_block));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return list;

    }

    public ArrayList<String> getUserBlockedList() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + ContactsEntry.COLUMN_PHONENUMBER + " FROM " + ContactsEntry.TABLE_NAME + " where " + ContactsEntry.COLUMN_BLOCK_USER + " = ? ";

        ArrayList<String> userBlockedList = new ArrayList<>();

        String[] args = {String.valueOf(1)};
        Cursor cursor = db.rawQuery(selectQuery, args);

        if (cursor.moveToFirst()) {
            do {
                userBlockedList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return userBlockedList;
    }

    public ArrayList<String> clearChat(Context context, long chatId, String groupServerId) {
        ArrayList<String> mediaFileNames = getMediaFileNamesForParticularChat(chatId);

        SQLiteDatabase db = this.getWritableDatabase();

        String selection = MessagesEntry.COLUMN_CHAT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

        if (chatId != DEFAULT_ENTRY_ID) {
            db.delete(MessagesEntry.TABLE_NAME, selection, selectionArgs);
            updateChatEntry(getDummyMessageRowId(), chatId, groupServerId);

            DBUtil.deleteContentFromExternalFileStorage(context, mediaFileNames);
        }

        return mediaFileNames;
    }

    public ArrayList<String> getMediaFileNamesForParticularChat(long chatId) {
        ArrayList<String> mediaFileNames = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + MessagesEntry.COLUMN_MEDIA_FILE_NAME + " FROM " + MessagesEntry.TABLE_NAME + " where " + MessagesEntry.COLUMN_CHAT_ID + " = ? ";

        String[] args = {String.valueOf(chatId)};
        Cursor cursor = db.rawQuery(selectQuery, args);

        if (cursor.moveToFirst()) {
            do {
                String filename = cursor.getString(0);
                if (filename != null) {
                    mediaFileNames.add(filename);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return mediaFileNames;
    }

    public void clearChatEntry(long chatId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = ChatEntry.COLUMN_CHAT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

        if (chatId != DEFAULT_ENTRY_ID) {
            db.delete(ChatEntry.TABLE_NAME, selection, selectionArgs);
        }
    }

    public void updateUserBlockStatus(long contactId, boolean blockStatus) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactsEntry.COLUMN_BLOCK_USER, blockStatus);

        String selection = ContactsEntry.COLUMN_CONTACT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(contactId)};

        int count = db.update(
                ContactsEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }

    public boolean isChatBlocked(long contactId) {

        boolean block = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String projection[] = {
                ContactsEntry.COLUMN_BLOCK_USER
        };

        String selection = ContactsEntry.COLUMN_CONTACT_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(contactId)};

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        if (c.moveToFirst()) {
            boolean value = c.getInt(0) == 1;
            return value;
        }

        c.close();
        return block;
    }

    public void muteConversation(long chatId, boolean mute) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ChatEntry.COLUMN_MUTE_CONVERSATION, mute);

        String selection = ChatEntry.COLUMN_CHAT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(chatId)};

        int count = db.update(
                ChatEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }

    public boolean isMute(long chatId) {

        boolean mute = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String projection[] = {
                ChatEntry.COLUMN_MUTE_CONVERSATION
        };

        String selection = ChatEntry.COLUMN_CHAT_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

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
            boolean value = c.getInt(0) == 1;
            return value;
        }

        c.close();
        return mute;
    }

    public String getGroupSubject(String groupServerId) {

        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ChatEntry.COLUMN_NAME,
        };

        String selection = ChatEntry.COLUMN_GROUP_SERVER_ID + " LIKE ? ";
        String[] selectionArgs = {groupServerId};


        Cursor c = db.query(
                ChatEntry.TABLE_NAME,                 // The table to query
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
        return null;
    }

    public void updateAdmin(String user, String groupname) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GroupUserEntry.COLUMN_ADMIN, 1);

        String selection = GroupUserEntry.COLUMN_CONTACT_ID + " = ? and " + GroupUserEntry.COLUMN_CHAT_ID + " LIKE ? ";
        String[] selectionArgs = {user, String.valueOf(getChatEntryID(groupname))};

        int update = db.update(GroupUserEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }

    public void deleteMessageFromTable(int id) {

        SQLiteDatabase db = getWritableDatabase();

        String selection = MessagesEntry.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(MessagesEntry.TABLE_NAME, selection, selectionArgs);

    }

    public Message getMessage(int messageId) {

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                MessagesEntry.COLUMN_PHONENUMBER,                               //0th column
                MessagesEntry.COLUMN_DATA_TEXT,                                 //1th column
                MessagesEntry.COLUMN_DATA_MEDIA,                                //2th column
                MessagesEntry.COLUMN_MIME_TYPE,                                 //3th column
                MessagesEntry.COLUMN_SERVER_RECEIPT,                            //4th column
                MessagesEntry.COLUMN_RECIPIENT_RECEIPT,                         //5th column
                MessagesEntry.COLUMN_NAME_I_AM_SENDER,                          //6th column
                MessagesEntry.COLUMN_RECEIVE_TIMESTAMP,                         //7th column
                MessagesEntry.COLUMN_SEND_TIMESTAMP,                            //8th column
                MessagesEntry.COLUMN_ID,                                        //9th column
                MessagesEntry.COLUMN_READ_STATUS,                               //10th column
                MessagesEntry.COLUMN_MEDIA_FILE_NAME,
                MessagesEntry.COLUMN_MESSAGE_ID
        };

        String selection = MessagesEntry.COLUMN_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(messageId)};

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
            boolean value = c.getInt(6) > 0;
            int id = c.getInt(9);
            boolean read = c.getInt(10) > 0;
            return new Message(c.getInt(9), c.getString(0), c.getString(1), c.getBlob(2), c.getString(3), c.getString(4), c.getString(5), value, c.getString(7), c.getString(8), read, id, c.getString(11), c.getString(12));
        }
        c.close();
        return null;
    }

    public void deleteContact(int contactId) {

        if (isContactAvailable(contactId)) {
            //nothing
        } else {
            SQLiteDatabase db = getWritableDatabase();

            String table = ContactsEntry.TABLE_NAME;
            String whereClause = ContactsEntry.COLUMN_CONTACT_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(contactId)};
            db.delete(ContactsEntry.TABLE_NAME, whereClause, whereArgs);
        }

    }

    private boolean isContactAvailable(int contactId) {
        SQLiteDatabase db = getReadableDatabase();

        boolean value = false;
        String[] projection = {
                ContactsEntry.COLUMN_AVAILABLE
        };

        String selection = ContactsEntry.COLUMN_CONTACT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(contactId)};


        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (c.moveToFirst()) {
            value = c.getInt(0) > 0;
        }
        return value;
    }

    public static class GroupParticipants {

        public long chatId;
        public ArrayList<Contacts> usersInGroup;

        public GroupParticipants(long chatId, ArrayList<Contacts> usersInGroup) {
            this.chatId = chatId;
            this.usersInGroup = usersInGroup;
        }

    }

    public void addDummyMessageIfNotExist() {
        if (DUMMY_MESSAGE_ROW_ID == DEFAULT_ENTRY_ID) {
            SQLiteDatabase db = getReadableDatabase();
            String[] projection = {
                    MessagesEntry.COLUMN_ID
            };

            String selection = MessagesEntry.COLUMN_CHAT_ID + " = ? ";
            String[] selectionArgs = {String.valueOf(DEFAULT_ENTRY_ID)};

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

            if (!exist) {
                DUMMY_MESSAGE_ROW_ID = addMessage("", MIME_TYPE_TEXT, "", false, "", "", "", "", DEFAULT_ENTRY_ID, DEFAULT_READ_STATUS);
            } else {
                //nothing
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_UNIQUE_INDEX);
        db.execSQL(CREATE_CHAT_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
//        db.execSQL(CREATE_GROUP_TABLE);
        db.execSQL(CREATE_GROUP_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_CONTACTS_TABLE);
        db.execSQL(DROP_MESSAGE_TABLE);
        db.execSQL(DROP_CHAT_TABLE);
        db.execSQL(DROP_GROUP_USER_TABLE);

        onCreate(db);
    }

}
