package com.sports.unity.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sports.unity.R;
import com.sports.unity.messages.controller.model.Chats;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.Message;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sports.unity.Database.SportsUnityContract.ContactChatEntry;
import static com.sports.unity.Database.SportsUnityContract.MessagesEntry;
import static com.sports.unity.Database.SportsUnityContract.GroupUserEntry;
import static com.sports.unity.Database.SportsUnityContract.FriendRequestEntry;

/**
 * Created by madmachines on 1/9/15.
 */
public class SportsUnityDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "spu.db";

    public static final int DEFAULT_ENTRY_ID = -1;
    public static final int DEFAULT_ENTRY_ID_FOR_GROUP = -2;

    public static final boolean DEFAULT_READ_STATUS = false;

    public static final String DUMMY_JID = "DUMMY_JID";
//    public static final String DEFAULT_GROUP_SERVER_ID = "NOT_GROUP_CHAT";

    public static final String MIME_TYPE_TEXT = "t";
    public static final String MIME_TYPE_STICKER = "s";
    public static final String MIME_TYPE_IMAGE = "i";
    public static final String MIME_TYPE_VIDEO = "v";
    public static final String MIME_TYPE_AUDIO = "a";

    public static final int DEFAULT_GET_ALL_CHAT_LIST = -1;

    private static final String COMMA_SEP = ",";

    private static int DUMMY_MESSAGE_ROW_ID = -1;
    private static int DUMMY_CONTACT_ROW_ID = -1;

    private static final String CREATE_CONTACT_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS " +
            ContactChatEntry.TABLE_NAME + "( " +
            ContactChatEntry.COLUMN_ID + " " + "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " + COMMA_SEP +
            ContactChatEntry.COLUMN_JID + " VARCHAR UNIQUE " + COMMA_SEP +
            ContactChatEntry.COLUMN_NAME + " " + "VARCHAR " + COMMA_SEP +
            ContactChatEntry.COLUMN_PHONE_NUMBER + " VARCHAR UNIQUE " + COMMA_SEP +
            ContactChatEntry.COLUMN_IMAGE + " BLOB " + COMMA_SEP +
            ContactChatEntry.COLUMN_STATUS + " VARCHAR " + COMMA_SEP +
            ContactChatEntry.COLUMN_AVAILABLE_STATUS + " INTEGER DEFAULT " + Contacts.AVAILABLE_NOT + " " + COMMA_SEP +
            ContactChatEntry.COLUMN_BLOCK_USER + " boolean DEFAULT 0 " + COMMA_SEP +
            ContactChatEntry.COLUMN_UPDATE_REQUIRED + " boolean DEFAULT 0 " + COMMA_SEP +

            ContactChatEntry.COLUMN_LAST_MESSAGE_ID + " INTEGER " + COMMA_SEP +
            ContactChatEntry.COLUMN_MUTE_CONVERSATION + " boolean DEFAULT 0 " + COMMA_SEP +
            ContactChatEntry.COLUMN_UNREAD_COUNT + " INTEGER " + COMMA_SEP +
            ContactChatEntry.COLUMN_LAST_USED + " DATETIME DEFAULT CURRENT_TIMESTAMP " + COMMA_SEP +
            ContactChatEntry.COLUMN_GROUP_CHAT + " boolean DEFAULT 0 " + COMMA_SEP +
            ContactChatEntry.COLUMN_ROSTER_ENTRY + " boolean DEFAULT 0 " + COMMA_SEP +
            ContactChatEntry.COLUMN_PENDING_FRIEND_REQUEST + " INTEGER DEFAULT " + Contacts.DEFAULT_PENDNG_REQUEST_ID +
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

    private static final String CREATE_FRIEND_REQUESTS_TABLE = " CREATE TABLE IF NOT EXISTS " +
            FriendRequestEntry.TABLE_NAME + "( " +
            FriendRequestEntry.COLUMN_CONTACT_ID + " INTEGER PRIMARY KEY " + COMMA_SEP +
            FriendRequestEntry.COLUMN_REQUEST_STANZA_ID + " VARCHAR " + COMMA_SEP +
            FriendRequestEntry.COLUMN_SERVER_RECEIPT_FOR_REQUEST_STANZA + " VARCHAR default NULL " +
            ");";


//    private static final String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS " +
//            ChatEntry.TABLE_NAME + "( " +
//            ChatEntry.COLUMN_CHAT_ID + " " + "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL " + COMMA_SEP +
//            ChatEntry.COLUMN_NAME + " " + "VARCHAR " + COMMA_SEP +
//            ChatEntry.COLUMN_IMAGE + " " + " BLOB " + COMMA_SEP +
//            ChatEntry.COLUMN_GROUP_SERVER_ID + " " + " VARCHAR " + COMMA_SEP +
//            ChatEntry.COLUMN_LAST_MESSAGE_ID + " INTEGER " + COMMA_SEP +
//            ChatEntry.COLUMN_CONTACT_ID + " INTEGER " + COMMA_SEP +
//            ChatEntry.COLUMN_MUTE_CONVERSATION + " boolean DEFAULT 0 " + COMMA_SEP +
//            ChatEntry.COLUMN_UNREAD_COUNT + " boolean " + COMMA_SEP +
//            ChatEntry.COLUMN_LAST_USED + " DATETIME DEFAULT CURRENT_TIMESTAMP " + COMMA_SEP +
//            ChatEntry.COLUMN_PEOPLE_AROUND_ME + " boolean DEFAULT 0 " +
//            ");";

    private static final String CREATE_GROUP_USER_TABLE = "CREATE TABLE IF NOT EXISTS " +
            GroupUserEntry.TABLE_NAME + "( " +
            GroupUserEntry.COLUMN_CHAT_ID + " INTEGER " + COMMA_SEP +
            GroupUserEntry.COLUMN_ADMIN + " INTEGER " + COMMA_SEP +
            GroupUserEntry.COLUMN_CONTACT_ID + " " + " INTEGER );";

    private static final String DROP_CONTACT_CHAT_TABLE = "DROP TABLE IF EXISTS " + ContactChatEntry.TABLE_NAME;
    private static final String DROP_MESSAGE_TABLE = "DROP TABLE IF EXISTS " + MessagesEntry.TABLE_NAME;
    private static final String DROP_GROUP_USER_TABLE = "DROP TABLE IF EXISTS " + GroupUserEntry.TABLE_NAME;

    private static SportsUnityDBHelper SPORTS_UNITY_DB_HELPER = null;

    synchronized public static SportsUnityDBHelper getInstance(Context context) {
        if (SPORTS_UNITY_DB_HELPER == null) {
            SPORTS_UNITY_DB_HELPER = new SportsUnityDBHelper(context);
        }
        return SPORTS_UNITY_DB_HELPER;
    }

    public static int getDummyMessageRowId() {
        return DUMMY_MESSAGE_ROW_ID;
    }

    public static int getDummyContactRowId() {
        return DUMMY_CONTACT_ROW_ID;
    }

    private ArrayList<Contacts> allContacts = null;

    private SportsUnityDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACT_CHAT_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
        db.execSQL(CREATE_GROUP_USER_TABLE);
        db.execSQL(CREATE_FRIEND_REQUESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1: {
                db.execSQL("ALTER TABLE " + ContactChatEntry.TABLE_NAME + " ADD COLUMN " + ContactChatEntry.COLUMN_ROSTER_ENTRY + " BOOLEAN DEFAULT 0");
            }
            case 2: {
                db.execSQL("ALTER TABLE " + ContactChatEntry.TABLE_NAME + " ADD COLUMN " + ContactChatEntry.COLUMN_PENDING_FRIEND_REQUEST + " INTEGER DEFAULT " + Contacts.DEFAULT_PENDNG_REQUEST_ID);
                db.execSQL(CREATE_FRIEND_REQUESTS_TABLE);
            }
            case 3: {

            }
        }

//        db.execSQL(DROP_CONTACT_CHAT_TABLE);
//        db.execSQL(DROP_MESSAGE_TABLE);
//        db.execSQL(DROP_GROUP_USER_TABLE);
//
//        onCreate(db);
    }

    public void createGroupUserEntry(int chatId, ArrayList<Integer> selectedMembers) {
        SQLiteDatabase db = getWritableDatabase();

        for (Integer id : selectedMembers) {
            ContentValues values = new ContentValues();
            values.put(GroupUserEntry.COLUMN_CHAT_ID, chatId);
            values.put(GroupUserEntry.COLUMN_CONTACT_ID, id);
            values.put(GroupUserEntry.COLUMN_ADMIN, 0);

            int rows = updateParticipantAsMember(id, chatId);
            if (rows == 0) {
                db.insert(GroupUserEntry.TABLE_NAME, null, values);
            } else {
                //nothing
            }
        }
    }

    public int addToContacts(String name, String number, String jid, String defaultStatus, byte[] image, int availableStatus, boolean infoUpdateRequired) {
        int rowId = -1;

        // temporary fix to remove /Smack from jid if it exists
        if (jid != null) {
            if (jid.contains("/Smack")) {
                jid = jid.replace("/Smack", "");
            }
        }

        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(ContactChatEntry.COLUMN_NAME, name);
            contentValues.put(ContactChatEntry.COLUMN_PHONE_NUMBER, number);
            contentValues.put(ContactChatEntry.COLUMN_JID, jid);
            contentValues.put(ContactChatEntry.COLUMN_STATUS, defaultStatus);
            contentValues.put(ContactChatEntry.COLUMN_IMAGE, image);
            contentValues.put(ContactChatEntry.COLUMN_AVAILABLE_STATUS, availableStatus);
            contentValues.put(ContactChatEntry.COLUMN_UPDATE_REQUIRED, infoUpdateRequired);

            rowId = (int) db.insert(ContactChatEntry.TABLE_NAME, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowId;
    }

    public int addToContacts(String name, String number, String jid, String defaultStatus, byte[] image, int availableStatus) {
        return addToContacts(name, number, jid, defaultStatus, image, availableStatus, true);
    }

    public int updateRosterEntryinDatabase(String jid, boolean entryCreated) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_ROSTER_ENTRY, entryCreated);

        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {jid};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public ArrayList<String> getPendingRosterEntries() {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {ContactChatEntry.COLUMN_JID};

        String selection = ContactChatEntry.COLUMN_ROSTER_ENTRY + " LIKE ? " +
                "AND " + ContactChatEntry.COLUMN_JID + " IS NOT NULL " +
                "AND " + ContactChatEntry.COLUMN_AVAILABLE_STATUS + " = ? " +
                "AND " + ContactChatEntry.COLUMN_GROUP_CHAT + " = ? ";
        String[] selectionArgs = {String.valueOf("0"), String.valueOf(Contacts.AVAILABLE_BY_MY_CONTACTS), String.valueOf("0")};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        ArrayList<String> jids = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                jids.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        return jids;
    }

    public ArrayList getAllPhoneNumbers() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> numbers = new ArrayList<>();
        String[] projection = {ContactChatEntry.COLUMN_PHONE_NUMBER};

        String selection = ContactChatEntry.COLUMN_AVAILABLE_STATUS + " == " + Contacts.AVAILABLE_BY_MY_CONTACTS + " AND " + ContactChatEntry.COLUMN_PHONE_NUMBER + " is not NULL ";
        String[] selectionArgs = null;

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            do {
                numbers.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();

        return numbers;
    }

    public int getContactIdFromPhoneNumber(String phoneNumber) {
        int contactId = DEFAULT_ENTRY_ID;

        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {ContactChatEntry.COLUMN_ID};

        String selection = ContactChatEntry.COLUMN_PHONE_NUMBER + " LIKE ?";
        String[] selectionArgs = {phoneNumber};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            contactId = c.getInt(0);
        }
        c.close();

        return contactId;
    }

    public int getContactIdFromJID(String jid) {
        int contactId = DEFAULT_ENTRY_ID;

        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {ContactChatEntry.COLUMN_ID};

        String selection = ContactChatEntry.COLUMN_JID + " LIKE ?";
        String[] selectionArgs = {jid};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            contactId = c.getInt(0);
        }
        c.close();

        return contactId;
    }

    public int setPhoneNumberAsName(String phoneNumber) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_NAME, phoneNumber);

        String selection = ContactChatEntry.COLUMN_PHONE_NUMBER + " LIKE ? ";
        String[] selectionArgs = {phoneNumber};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateUserContactFromPhoneContactDetails(String phoneNumber, String name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_NAME, name);
//        values.put(ContactChatEntry.COLUMN_AVAILABLE_STATUS, Contacts.AVAILABLE_BY_MY_CONTACTS);

        String selection = ContactChatEntry.COLUMN_PHONE_NUMBER + " LIKE ? ";
        String[] selectionArgs = {phoneNumber};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public Contacts getContactByPhoneNumber(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactChatEntry.COLUMN_NAME,
                ContactChatEntry.COLUMN_JID,
                ContactChatEntry.COLUMN_PHONE_NUMBER,
                ContactChatEntry.COLUMN_IMAGE,
                ContactChatEntry.COLUMN_ID,
                ContactChatEntry.COLUMN_STATUS,
                ContactChatEntry.COLUMN_AVAILABLE_STATUS
        };
        String selection = ContactChatEntry.COLUMN_PHONE_NUMBER + " LIKE ?";
        String[] selectionArgs = {phoneNumber};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        Contacts contacts = null;
        if (c.moveToFirst()) {
            contacts = new Contacts(c.getString(0), c.getString(1), c.getString(2), c.getBlob(3), c.getInt(4), c.getString(5), c.getInt(6));
        }
        c.close();

        return contacts;
    }

    public Contacts getContactByJid(String jid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactChatEntry.COLUMN_NAME,
                ContactChatEntry.COLUMN_JID,
                ContactChatEntry.COLUMN_PHONE_NUMBER,
                ContactChatEntry.COLUMN_IMAGE,
                ContactChatEntry.COLUMN_ID,
                ContactChatEntry.COLUMN_STATUS,
                ContactChatEntry.COLUMN_AVAILABLE_STATUS
        };
        String selection = ContactChatEntry.COLUMN_JID + " LIKE ?";
        String[] selectionArgs = {jid};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        Contacts contacts = null;
        if (c.moveToFirst()) {
            contacts = new Contacts(c.getString(0), c.getString(1), c.getString(2), c.getBlob(3), c.getInt(4), c.getString(5), c.getInt(6));
        }
        c.close();

        return contacts;
    }

    public Contacts getContact(int contactId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactChatEntry.COLUMN_NAME,
                ContactChatEntry.COLUMN_JID,
                ContactChatEntry.COLUMN_PHONE_NUMBER,
                ContactChatEntry.COLUMN_IMAGE,
                ContactChatEntry.COLUMN_ID,
                ContactChatEntry.COLUMN_STATUS,
                ContactChatEntry.COLUMN_AVAILABLE_STATUS
        };
        String selection = ContactChatEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(contactId)};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        Contacts contacts = null;
        if (c.moveToFirst()) {
            contacts = new Contacts(c.getString(0), c.getString(1), c.getString(2), c.getBlob(3), c.getInt(4), c.getString(5), c.getInt(6));
        }
        c.close();

        return contacts;

    }

    public byte[] getUserProfileImage(String jid) {
        SQLiteDatabase db = this.getReadableDatabase();

        int contactId = getContactIdFromJID(jid);
        String[] projection = {ContactChatEntry.COLUMN_IMAGE};

        String selection = ContactChatEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(contactId)};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        byte[] image = null;
        if (c.moveToFirst()) {
            image = c.getBlob(0);
        }
        c.close();

        return image;
    }

    public ArrayList<Contacts> getPendingContacts() {
        ArrayList<Contacts> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactChatEntry.COLUMN_NAME,
                ContactChatEntry.COLUMN_JID,
                ContactChatEntry.COLUMN_PHONE_NUMBER,
                ContactChatEntry.COLUMN_IMAGE,
                ContactChatEntry.COLUMN_ID,
                ContactChatEntry.COLUMN_STATUS,
                ContactChatEntry.COLUMN_AVAILABLE_STATUS,
                ContactChatEntry.COLUMN_PENDING_FRIEND_REQUEST,
                ContactChatEntry.COLUMN_BLOCK_USER
        };

        String selection = ContactChatEntry.COLUMN_PENDING_FRIEND_REQUEST + " = " + Contacts.PENDING_REQUESTS_TO_PROCESS
                + " AND " + ContactChatEntry.COLUMN_GROUP_CHAT + " = ? "
                + " AND " + ContactChatEntry.COLUMN_BLOCK_USER + " !=  ? ";        // we dont want to accept requests of blocked contacts from people around me
        String[] selectionArgs = new String[]{"0", "1"};
        String sortOrder = ContactChatEntry.COLUMN_NAME + " COLLATE NOCASE ASC ";

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()) {
            do {
                boolean blockstatus = c.getInt(8) > 0;
                list.add(new Contacts(c.getString(0), c.getString(1), c.getString(2), c.getBlob(3), c.getInt(4), c.getString(5), c.getInt(6), c.getInt(7), blockstatus));
            } while (c.moveToNext());
        }
        c.close();

        return list;
    }

    public boolean isRequestPending(String jid) {

        boolean isRequestPending = false;
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactChatEntry.COLUMN_NAME,
                ContactChatEntry.COLUMN_JID,
                ContactChatEntry.COLUMN_PHONE_NUMBER,
                ContactChatEntry.COLUMN_IMAGE,
                ContactChatEntry.COLUMN_ID,
                ContactChatEntry.COLUMN_STATUS,
                ContactChatEntry.COLUMN_AVAILABLE_STATUS
        };

        String selection = ContactChatEntry.COLUMN_PENDING_FRIEND_REQUEST + " = " + Contacts.WAITING_FOR_REQUEST_ACCEPTANCE + " AND " + ContactChatEntry.COLUMN_GROUP_CHAT + " = 0 " + " AND " + ContactChatEntry.COLUMN_JID + " LIKE ?";
        String[] selectionArgs = new String[]{jid};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            isRequestPending = true;
        }
        c.close();

        return isRequestPending;
    }

    public ArrayList<Contacts> getContactList_AvailableOnly(boolean forceLoad) {
        if (forceLoad == true || allContacts == null) {
            ArrayList<Contacts> list = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();

            String[] projection = {
                    ContactChatEntry.COLUMN_NAME,
                    ContactChatEntry.COLUMN_JID,
                    ContactChatEntry.COLUMN_PHONE_NUMBER,
                    ContactChatEntry.COLUMN_IMAGE,
                    ContactChatEntry.COLUMN_ID,
                    ContactChatEntry.COLUMN_STATUS,
                    ContactChatEntry.COLUMN_AVAILABLE_STATUS
            };

            String selection = ContactChatEntry.COLUMN_AVAILABLE_STATUS + " = " + Contacts.AVAILABLE_BY_MY_CONTACTS + " AND " + ContactChatEntry.COLUMN_GROUP_CHAT + " == 0 ";
            String[] selectionArgs = null;
            String sortOrder = ContactChatEntry.COLUMN_NAME + " COLLATE NOCASE ASC ";

            Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            if (c.moveToFirst()) {
                do {
                    list.add(new Contacts(c.getString(0), c.getString(1), c.getString(2), c.getBlob(3), c.getInt(4), c.getString(5), c.getInt(6)));
                } while (c.moveToNext());
            }
            c.close();

            allContacts = list;
        } else {
            //nothing
        }

        return allContacts;
    }

    public ArrayList<Contacts> getBlockedContactList() {
        ArrayList<Contacts> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactChatEntry.COLUMN_NAME,
                ContactChatEntry.COLUMN_JID,
                ContactChatEntry.COLUMN_PHONE_NUMBER,
                ContactChatEntry.COLUMN_IMAGE,
                ContactChatEntry.COLUMN_ID,
                ContactChatEntry.COLUMN_STATUS,
                ContactChatEntry.COLUMN_BLOCK_USER,
                ContactChatEntry.COLUMN_AVAILABLE_STATUS
        };

        String selection = ContactChatEntry.COLUMN_BLOCK_USER + " != 0 " + " AND " + ContactChatEntry.COLUMN_GROUP_CHAT + " == 0 ";
        String[] selectionArgs = null;
        String sortOrder = ContactChatEntry.COLUMN_NAME + " COLLATE NOCASE ASC ";

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()) {
            do {
                list.add(new Contacts(c.getString(0), c.getString(1), c.getString(2), c.getBlob(3), c.getInt(4), c.getString(5), c.getInt(6)));
            } while (c.moveToNext());
        }
        c.close();

        return list;
    }

    public ArrayList<Contacts> getListOfJIDRequireUpdate(boolean forcedSync) {
        ArrayList<Contacts> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactChatEntry.COLUMN_NAME,
                ContactChatEntry.COLUMN_JID,
                ContactChatEntry.COLUMN_PHONE_NUMBER,
                ContactChatEntry.COLUMN_IMAGE,
                ContactChatEntry.COLUMN_ID,
                ContactChatEntry.COLUMN_STATUS,
                ContactChatEntry.COLUMN_AVAILABLE_STATUS
        };

        String selection =  (forcedSync ? " " : "ContactChatEntry.COLUMN_UPDATE_REQUIRED == 1 AND ") + ContactChatEntry.COLUMN_JID + " is not null ";
        String[] selectionArgs = null;

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            do {
                list.add(new Contacts(c.getString(0), c.getString(1), c.getString(2), c.getBlob(3), c.getInt(4), c.getString(5), c.getInt(6)));
            } while (c.moveToNext());
        }
        c.close();

        return list;
    }

    public ArrayList<Contacts> getContactList(boolean registeredOnly) {
        ArrayList<Contacts> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactChatEntry.COLUMN_NAME,
                ContactChatEntry.COLUMN_JID,
                ContactChatEntry.COLUMN_PHONE_NUMBER,
                ContactChatEntry.COLUMN_IMAGE,
                ContactChatEntry.COLUMN_ID,
                ContactChatEntry.COLUMN_STATUS,
                ContactChatEntry.COLUMN_AVAILABLE_STATUS
        };

        String registerCondition = null;
        if (registeredOnly) {
            registerCondition = " is not NULL ";
        } else {
            registerCondition = " is NULL ";
        }

        String selection = ContactChatEntry.COLUMN_JID + registerCondition + " AND " + ContactChatEntry.COLUMN_AVAILABLE_STATUS + " = " + Contacts.AVAILABLE_BY_MY_CONTACTS
                + " AND " + ContactChatEntry.COLUMN_GROUP_CHAT + " == 0 ";
        String[] selectionArgs = null;
        String sortOrder = ContactChatEntry.COLUMN_NAME + " COLLATE NOCASE ASC ";

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        if (c.moveToFirst()) {
            do {
                list.add(new Contacts(c.getString(0), c.getString(1), c.getString(2), c.getBlob(3), c.getInt(4), c.getString(5), c.getInt(6)));
            } while (c.moveToNext());
        }
        c.close();

        return list;
    }

    public int updateContacts(Context context, String number, String jid) {
        return updateContacts(number, jid, context.getResources().getString(R.string.others_default_status));
    }

    public int updateContacts(String number, String jid, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        // temporary fix to remove /Smack from jid if it exists
        if (jid != null) {
            if (jid.contains("/Smack")) {
                jid = jid.replace("/Smack", "");
            }
        }

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_JID, jid);
        values.put(ContactChatEntry.COLUMN_STATUS, status);

        String selection = ContactChatEntry.COLUMN_PHONE_NUMBER + " LIKE ? ";
        String[] selectionArgs = {number};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateContactsPhoneNumberAndName(String jid, String phoneNumber, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_PHONE_NUMBER, phoneNumber);
        if (name != null && name.length() > 0) {
            values.put(ContactChatEntry.COLUMN_NAME, name);
        }

        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {jid};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateContacts(String phoneNumber, String jid, String name, byte[] userImage, String status, int availableStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_NAME, name);
        values.put(ContactChatEntry.COLUMN_JID, jid);
        values.put(ContactChatEntry.COLUMN_IMAGE, userImage);
        values.put(ContactChatEntry.COLUMN_STATUS, status);
        values.put(ContactChatEntry.COLUMN_AVAILABLE_STATUS, availableStatus);

        String selection = ContactChatEntry.COLUMN_PHONE_NUMBER + " LIKE ? ";
        String[] selectionArgs = {phoneNumber};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateContacts(String jid, byte[] userImage, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_IMAGE, userImage);
        values.put(ContactChatEntry.COLUMN_STATUS, status);
        values.put(ContactChatEntry.COLUMN_UPDATE_REQUIRED, false);

        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {jid};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateContacts(String jid, String name, byte[] userImage, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_NAME, name);
        values.put(ContactChatEntry.COLUMN_IMAGE, userImage);
        values.put(ContactChatEntry.COLUMN_STATUS, status);
        values.put(ContactChatEntry.COLUMN_UPDATE_REQUIRED, false);

        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {jid};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateGroupInfo(String jid, String groupName, byte[] groupImage) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_NAME, groupName);
        if( groupImage != null ) {
            values.put(ContactChatEntry.COLUMN_IMAGE, groupImage);
        }
        values.put(ContactChatEntry.COLUMN_UPDATE_REQUIRED, false);

        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {jid};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public GroupParticipants getGroupParticipants(int chatId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + ContactChatEntry.COLUMN_NAME + ", " + ContactChatEntry.COLUMN_JID + ", " + ContactChatEntry.COLUMN_PHONE_NUMBER + ", " +
                ContactChatEntry.COLUMN_IMAGE + ", A." + ContactChatEntry.COLUMN_ID + ", " + ContactChatEntry.COLUMN_STATUS + ", " + ContactChatEntry.COLUMN_AVAILABLE_STATUS + ", " + GroupUserEntry.COLUMN_ADMIN +
                " FROM " + ContactChatEntry.TABLE_NAME + " A INNER JOIN " + GroupUserEntry.TABLE_NAME + " B ON A." + ContactChatEntry.COLUMN_ID + " = B." + GroupUserEntry.COLUMN_CONTACT_ID +
                " WHERE B." + GroupUserEntry.COLUMN_CHAT_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(chatId)};

        ArrayList<Contacts> users = new ArrayList<>();
        ArrayList<String> admins = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, selectionArgs);
        if (cursor.moveToFirst()) {
            do {
                int isAdmin = cursor.getInt(7);
                if (isAdmin == 1) {
                    admins.add(cursor.getString(1));
                }
                users.add(new Contacts(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getBlob(3), cursor.getInt(4), cursor.getString(5), cursor.getInt(6)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        GroupParticipants groupParticipants = new GroupParticipants(chatId, users, admins);
        return groupParticipants;
    }

    public String getUserNameByJid(String jid) {
        String name = null;
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {ContactChatEntry.COLUMN_NAME};

        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {jid};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            name = c.getString(0);
        }
        c.close();

        return name;
    }

    public int updateContactName(int contactId, String name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_NAME, name);

        String selection = ContactChatEntry.COLUMN_ID + " = ? ";
        String selectionArgs[] = {String.valueOf(contactId)};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public ArrayList<Message> getMessages(int chatId) {
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

        Cursor c = db.query(MessagesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
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

    public ArrayList<Chats> getChatsBasedOnSearchedMessage(String searchKeyword, boolean nearByChat) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Chats> list = new ArrayList<>();
        if (searchKeyword != null && searchKeyword.length() > 0) {
            StringBuilder searchedMessagesSubQuery = new StringBuilder("");
            {
                searchedMessagesSubQuery.append("( SELECT ");
                searchedMessagesSubQuery.append(MessagesEntry.COLUMN_CHAT_ID + " ,");
                searchedMessagesSubQuery.append(MessagesEntry.COLUMN_DATA_TEXT + " ,");
//                searchedMessagesSubQuery.append(MessagesEntry.COLUMN_DATA_MEDIA + " ,");
                searchedMessagesSubQuery.append(MessagesEntry.COLUMN_MIME_TYPE + " ,");
                searchedMessagesSubQuery.append(MessagesEntry.COLUMN_SEND_TIMESTAMP + " ,");
                searchedMessagesSubQuery.append(MessagesEntry.COLUMN_RECEIVE_TIMESTAMP);

                searchedMessagesSubQuery.append(" FROM " + MessagesEntry.TABLE_NAME);
                searchedMessagesSubQuery.append(" WHERE " + MessagesEntry.COLUMN_MIME_TYPE + " = '" + MIME_TYPE_TEXT + "' AND " + MessagesEntry.COLUMN_DATA_TEXT + " LIKE '%" + searchKeyword + "%' ) ");
            }

            StringBuilder selectQuery = new StringBuilder("");

            selectQuery.append("SELECT ");
            selectQuery.append(ContactChatEntry.COLUMN_UNREAD_COUNT + " ,");
            selectQuery.append(ContactChatEntry.COLUMN_NAME + " ,");
            selectQuery.append(ContactChatEntry.COLUMN_ID + " ,");
            selectQuery.append(ContactChatEntry.COLUMN_LAST_MESSAGE_ID + " ,");

            selectQuery.append(" B.*, ");

//            selectQuery.append(" A." + ContactChatEntry.COLUMN_ID + " ,");
            selectQuery.append(ContactChatEntry.COLUMN_JID + " ,");
            selectQuery.append(ContactChatEntry.COLUMN_IMAGE + ",");
            selectQuery.append(ContactChatEntry.COLUMN_MUTE_CONVERSATION + ",");
            selectQuery.append(ContactChatEntry.COLUMN_BLOCK_USER + ",");
            selectQuery.append(ContactChatEntry.COLUMN_GROUP_CHAT + ",");
            selectQuery.append(ContactChatEntry.COLUMN_LAST_USED);

            selectQuery.append(" FROM " + ContactChatEntry.TABLE_NAME + " A INNER JOIN " + searchedMessagesSubQuery.toString() + " B ");
            selectQuery.append("ON A." + ContactChatEntry.COLUMN_ID + " = B." + MessagesEntry.COLUMN_CHAT_ID);
            if (nearByChat) {
                selectQuery.append(" WHERE " + ContactChatEntry.COLUMN_AVAILABLE_STATUS + " = " + Contacts.AVAILABLE_BY_PEOPLE_AROUND_ME + " ");
            } else {
                selectQuery.append(" WHERE " + ContactChatEntry.COLUMN_AVAILABLE_STATUS + " = " + Contacts.AVAILABLE_BY_MY_CONTACTS + " ");
            }
            selectQuery.append(" order by " + ContactChatEntry.COLUMN_LAST_USED + " DESC");
//            subQuery.append(" ) ");

//            StringBuilder selectQuery = new StringBuilder();
//            selectQuery.append(" SELECT B.* , A.");
//            selectQuery.append(ContactChatEntry.COLUMN_IMAGE + ",");
//            selectQuery.append(ContactChatEntry.COLUMN_BLOCK_USER + " FROM ");
//            selectQuery.append(ContactChatEntry.TABLE_NAME);
//            selectQuery.append(" A INNER JOIN ");
//            selectQuery.append(subQuery.toString());
//            selectQuery.append(" B  ON ");
//            selectQuery.append("A." + ContactChatEntry.COLUMN_ID + " = B." + ChatEntry.COLUMN_CONTACT_ID);
//            selectQuery.append(" order by " + ChatEntry.COLUMN_LAST_USED + " DESC");

            Cursor cursor = db.rawQuery(selectQuery.toString(), null);
            if (cursor.moveToFirst()) {
                do {
                    boolean value_mute = cursor.getInt(11) == 1;
                    boolean value_block = cursor.getInt(12) == 1;
                    boolean isGroupChat = cursor.getInt(13) == 1;
                    list.add(new Chats(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                            cursor.getInt(3), cursor.getString(5),
                            cursor.getString(6), cursor.getString(7), cursor.getString(8),
                            cursor.getString(9), cursor.getBlob(10), value_mute, value_block, isGroupChat));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        return list;
    }

    public int addMessage(String msg, String mimeType, String number, boolean iamsender, String sentTime, String messageId, String serverTime, String recipientTime,
                          int chatId, boolean read) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MessagesEntry.COLUMN_PHONENUMBER, number);
        values.put(MessagesEntry.COLUMN_DATA_TEXT, msg);
        values.put(MessagesEntry.COLUMN_MESSAGE_ID, messageId);
        values.put(MessagesEntry.COLUMN_MIME_TYPE, mimeType);
        values.put(MessagesEntry.COLUMN_NAME_I_AM_SENDER, iamsender);
        values.put(MessagesEntry.COLUMN_CHAT_ID, chatId);
        values.put(MessagesEntry.COLUMN_SEND_TIMESTAMP, sentTime);
        values.put(MessagesEntry.COLUMN_SERVER_RECEIPT, serverTime);
        values.put(MessagesEntry.COLUMN_RECIPIENT_RECEIPT, recipientTime);
        values.put(MessagesEntry.COLUMN_RECEIVE_TIMESTAMP, CommonUtil.getCurrentGMTTimeInEpoch());
        values.put(MessagesEntry.COLUMN_READ_STATUS, read);

        return (int) db.insert(MessagesEntry.TABLE_NAME, null, values);
    }

    public int addMediaMessage(String message, String mimeType, String number, boolean iamsender, String sentTime, String messageId, String serverTime, String recipientTime,
                               int chatId, boolean read, String mediaFileName, byte[] mediaThumbnail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MessagesEntry.COLUMN_PHONENUMBER, number);
        values.put(MessagesEntry.COLUMN_DATA_TEXT, message);
        values.put(MessagesEntry.COLUMN_DATA_MEDIA, mediaThumbnail);
        values.put(MessagesEntry.COLUMN_MESSAGE_ID, messageId);
        values.put(MessagesEntry.COLUMN_MIME_TYPE, mimeType);
        values.put(MessagesEntry.COLUMN_NAME_I_AM_SENDER, iamsender);
        values.put(MessagesEntry.COLUMN_CHAT_ID, chatId);
        values.put(MessagesEntry.COLUMN_SEND_TIMESTAMP, sentTime);
        values.put(MessagesEntry.COLUMN_SERVER_RECEIPT, serverTime);
        values.put(MessagesEntry.COLUMN_RECIPIENT_RECEIPT, recipientTime);
        values.put(MessagesEntry.COLUMN_RECEIVE_TIMESTAMP, CommonUtil.getCurrentGMTTimeInEpoch());
        values.put(MessagesEntry.COLUMN_READ_STATUS, read);
        values.put(MessagesEntry.COLUMN_MEDIA_FILE_NAME, mediaFileName);

        return (int) db.insert(MessagesEntry.TABLE_NAME, null, values);
    }

    public int updateReadStatus(String messageStanzaId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesEntry.COLUMN_READ_STATUS, "1");

        String selection = MessagesEntry.COLUMN_MESSAGE_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(messageStanzaId)};

        return db.update(MessagesEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateMediaMessage_ContentUploaded(int messageId, String stanzaId, String checksum) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesEntry.COLUMN_MESSAGE_ID, stanzaId);
        values.put(MessagesEntry.COLUMN_DATA_TEXT, checksum);

        String selection = MessagesEntry.COLUMN_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(messageId)};

        return db.update(MessagesEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateMediaMessage_ContentDownloaded(int messageId, String filename) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesEntry.COLUMN_MEDIA_FILE_NAME, filename);

        String selection = MessagesEntry.COLUMN_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(messageId)};

        return db.update(MessagesEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateServerReceived(String receiptId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesEntry.COLUMN_SERVER_RECEIPT, receiptId);

        String selection = MessagesEntry.COLUMN_MESSAGE_ID + " LIKE ? ";
        String[] selectionArgs = {receiptId};

        return db.update(MessagesEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateClientReceived(String receiptId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MessagesEntry.COLUMN_RECIPIENT_RECEIPT, receiptId);

        String selection = MessagesEntry.COLUMN_MESSAGE_ID + " LIKE ? ";
        String[] selectionArgs = {receiptId};

        return db.update(MessagesEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateUnreadCount(int id, String jid) {
        SQLiteDatabase db = this.getWritableDatabase();

        int unread = getUnreadCount(id, jid);

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_UNREAD_COUNT, ++unread);

        String selection = ContactChatEntry.COLUMN_ID + " LIKE ? and " + ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(id), jid};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int getUnreadCount(int id, String jid) {
        int unreadCount = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {ContactChatEntry.COLUMN_UNREAD_COUNT};
        String selection = ContactChatEntry.COLUMN_ID + " LIKE ? and " + ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(id), jid};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            unreadCount = c.getInt(0);
        }
        c.close();

        return unreadCount;
    }

    public int clearUnreadCount(int id, String groupJID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_UNREAD_COUNT, 0);

        String selection = ContactChatEntry.COLUMN_ID + " LIKE ? and " + ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(id), groupJID};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
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

    public int updateChatEntry(int messageId, int chatId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_LAST_MESSAGE_ID, messageId);
        values.put(ContactChatEntry.COLUMN_LAST_USED, CommonUtil.getCurrentGMTTimeInEpoch());

        String selection = ContactChatEntry.COLUMN_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateChatEntry(int messageId, String jid) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_LAST_MESSAGE_ID, messageId);
        values.put(ContactChatEntry.COLUMN_LAST_USED, CommonUtil.getCurrentGMTTimeInEpoch());

        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {jid};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

//    public int getChatEntryID(long contactID, String groupServerId) {
//        int entryId = DEFAULT_ENTRY_ID;
//        SQLiteDatabase db = getReadableDatabase();
//
//        String projection[] = { ContactChatEntry.COLUMN_CHAT_ID };
//        String selection = ContactChatEntry.COLUMN_CONTACT_ID + " LIKE ? and " + ContactChatEntry.COLUMN_JID + " LIKE ? ";
//        String[] selectionArgs = {String.valueOf(contactID), groupServerId};
//
//        Cursor c = db.query( ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null );
//        if (c.moveToFirst()) {
//            entryId = c.getInt(0);
//        }
//        c.close();
//
//        return entryId;
//    }

    public int getChatEntryID(String jid) {
        int entryId = DEFAULT_ENTRY_ID;
        SQLiteDatabase db = getReadableDatabase();

        String projection[] = {ContactChatEntry.COLUMN_ID};
        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {jid};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            entryId = c.getInt(0);
        }
        c.close();

        return entryId;
    }

//    public int getContactAssociatedToChat(long chatId) {
//        int entryId = DEFAULT_ENTRY_ID;
//        SQLiteDatabase db = getReadableDatabase();
//
//        String projection[] = { ContactChatEntry.COLUMN_CONTACT_ID };
//        String selection = ContactChatEntry.COLUMN_CHAT_ID + " = ? ";
//        String[] selectionArgs = {String.valueOf(chatId)};
//
//        Cursor c = db.query( ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null );
//        if (c.moveToFirst()) {
//            entryId = c.getInt(0);
//        }
//        c.close();
//
//        return entryId;
//    }

//    public long createChatEntry(String name, long contactId, boolean others) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(ContactChatEntry.COLUMN_NAME, name);
//        values.put(ContactChatEntry.COLUMN_CONTACT_ID, contactId);
//        values.put(ContactChatEntry.COLUMN_UNREAD_COUNT, 0);
//        values.put(ContactChatEntry.COLUMN_PEOPLE_AROUND_ME, others);
//        values.put(ContactChatEntry.COLUMN_JID, DEFAULT_GROUP_SERVER_ID);
//
//        return db.insert(ChatEntry.TABLE_NAME, null, values);
//    }

    public String getNameByJID(String jid) {
        String name = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String projection[] = {ContactChatEntry.COLUMN_NAME};
        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(jid)};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            name = c.getString(0);
        }
        c.close();

        return name;
    }

    public int createGroupChatEntry(String subject, byte[] image, String groupJID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ContactChatEntry.COLUMN_NAME, subject);
        values.put(ContactChatEntry.COLUMN_UNREAD_COUNT, 0);

        values.put(ContactChatEntry.COLUMN_JID, groupJID);
        values.put(ContactChatEntry.COLUMN_IMAGE, image);
        values.put(ContactChatEntry.COLUMN_GROUP_CHAT, true);
        values.put(ContactChatEntry.COLUMN_AVAILABLE_STATUS, Contacts.AVAILABLE_BY_MY_CONTACTS);
        values.put(ContactChatEntry.COLUMN_UPDATE_REQUIRED, true);

        return (int) db.insert(ContactChatEntry.TABLE_NAME, null, values);
    }

    public ArrayList<Chats> getChatList(int availability) {
        return getChatList(null, availability);
    }

    public ArrayList<Chats> getChatList(String searchKeyword, int availability) {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Chats> list = new ArrayList<>();
        {

            StringBuilder selectQuery = new StringBuilder("");

//            selectQuery.append("( ");
            selectQuery.append("SELECT ");
            selectQuery.append(ContactChatEntry.COLUMN_UNREAD_COUNT + " ,");
            selectQuery.append(ContactChatEntry.COLUMN_NAME + " ,");
            selectQuery.append(ContactChatEntry.COLUMN_ID + " ,");
            selectQuery.append(ContactChatEntry.COLUMN_LAST_MESSAGE_ID + " ,");

            selectQuery.append(MessagesEntry.COLUMN_DATA_TEXT + " ,");
//            selectQuery.append(MessagesEntry.COLUMN_DATA_MEDIA + " ,");
            selectQuery.append(MessagesEntry.COLUMN_MIME_TYPE + " ,");
            selectQuery.append(MessagesEntry.COLUMN_SEND_TIMESTAMP + " ,");
            selectQuery.append(MessagesEntry.COLUMN_RECEIVE_TIMESTAMP + " ,");

//            subQuery.append(" A." + ChatEntry.COLUMN_CHAT_ID + " ,");
            selectQuery.append(ContactChatEntry.COLUMN_JID + " ,");
            selectQuery.append(ContactChatEntry.COLUMN_IMAGE + ",");
            selectQuery.append(ContactChatEntry.COLUMN_MUTE_CONVERSATION + ",");
            selectQuery.append(ContactChatEntry.COLUMN_BLOCK_USER + ",");
            selectQuery.append(ContactChatEntry.COLUMN_GROUP_CHAT + ",");
            selectQuery.append(ContactChatEntry.COLUMN_LAST_USED);

            selectQuery.append(" FROM " + ContactChatEntry.TABLE_NAME + " A INNER JOIN " + MessagesEntry.TABLE_NAME + " B ");
            selectQuery.append(" ON " + ContactChatEntry.COLUMN_LAST_MESSAGE_ID + " = " + MessagesEntry.COLUMN_ID);

            selectQuery.append(" WHERE ");

            if (searchKeyword != null && searchKeyword.length() > 0) {
                selectQuery.append(ContactChatEntry.COLUMN_NAME + " LIKE '%" + searchKeyword + "%'");
                selectQuery.append(" and ");
            }

//            selectQuery.append(" ) ");

//            StringBuilder selectQuery = new StringBuilder();
//            selectQuery.append(" SELECT B.* , A.");
//            selectQuery.append(ContactChatEntry.COLUMN_IMAGE + ",");
//            selectQuery.append(ContactChatEntry.COLUMN_BLOCK_USER + " FROM ");
//            selectQuery.append(ContactChatEntry.TABLE_NAME);
//            selectQuery.append(" A INNER JOIN ");
//            selectQuery.append(subQuery.toString());
//            selectQuery.append(" B  ON ");
//            selectQuery.append("A." + ContactChatEntry.COLUMN_ID + " = B." + ChatEntry.COLUMN_CONTACT_ID);

            String[] selectionArg = null;
            if (availability == DEFAULT_GET_ALL_CHAT_LIST) {
                selectionArg = new String[]{SportsUnityDBHelper.DUMMY_JID, String.valueOf(Contacts.AVAILABLE_NOT)};
                selectQuery.append(" ( ");
                selectQuery.append(ContactChatEntry.COLUMN_JID + " = ? OR " + ContactChatEntry.COLUMN_AVAILABLE_STATUS + " NOT LIKE ?");
                selectQuery.append(" ) ");
            } else if (availability == Contacts.AVAILABLE_BY_MY_CONTACTS) {
                selectionArg = new String[]{SportsUnityDBHelper.DUMMY_JID, String.valueOf(availability)};
                selectQuery.append(" ( ");
                selectQuery.append(ContactChatEntry.COLUMN_JID + " = ? OR " + ContactChatEntry.COLUMN_AVAILABLE_STATUS + " LIKE ? ");
                selectQuery.append(" ) ");
            } else {
                selectionArg = new String[]{String.valueOf(Contacts.AVAILABLE_BY_OTHER_CONTACTS), String.valueOf(Contacts.AVAILABLE_BY_PEOPLE_AROUND_ME)};
                selectQuery.append(" ( ");
                selectQuery.append(ContactChatEntry.COLUMN_AVAILABLE_STATUS + " LIKE ? OR " + ContactChatEntry.COLUMN_AVAILABLE_STATUS + " LIKE ? ");
                selectQuery.append(" ) ");
            }

            selectQuery.append(" order by " + ContactChatEntry.COLUMN_LAST_USED + " DESC");

            Log.d("Chat Fragment", selectQuery.toString());

            Cursor cursor = db.rawQuery(selectQuery.toString(), selectionArg);
            if (cursor.moveToFirst()) {
                do {
                    boolean value_mute = cursor.getInt(10) == 1;
                    boolean value_block = cursor.getInt(11) == 1;
                    boolean isGroupChat = cursor.getInt(12) == 1;
                    list.add(new Chats(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                            cursor.getInt(3), cursor.getString(4),
                            cursor.getString(5), cursor.getString(6), cursor.getString(7),
                            cursor.getString(8), cursor.getBlob(9), value_mute, value_block, isGroupChat));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

//        {
//            String query = "SELECT unread_count ,chat_name ,contact_id ,last_message ,text_data ,media_data ,data_mime_type ,send_timestamp ,recieve_timestamp , A.chat_id ,group_server_id ,chat_image,mute_conversation,last_used FROM chatEntryTable A INNER JOIN messagesTable B ON last_message = incremental_messages_id WHERE people_around_me LIKE 'false' ";
////            String query = "SELECT B.* , A.user_image,block_user FROM contactsTable A INNER JOIN " +
////                    "( SELECT unread_count ,chat_name ,contact_id ,last_message ,text_data ,media_data ,data_mime_type ,send_timestamp ,recieve_timestamp , A.chat_id ,group_server_id ,chat_image,mute_conversation,last_used FROM chatEntryTable A INNER JOIN messagesTable B ON last_message = incremental_messages_id WHERE people_around_me = 0  )  B " +
////                    " ON A.contact_id = B.contact_id order by last_used DESC";
//
//            Cursor cursor = db.rawQuery(query.toString(), null);
//            if (cursor.moveToFirst()) {
//                Log.i("entry", "" + cursor.getString(1));
//            }
//
//        }
//
//        printChatEntryTable();
//        printContactsTable();
//        printMessagesTable();
        return list;

    }

    private void printChatEntryTable() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + ContactChatEntry.COLUMN_ID + " , " + ContactChatEntry.COLUMN_NAME + " , " + ContactChatEntry.COLUMN_LAST_MESSAGE_ID + " , " + ContactChatEntry.COLUMN_JID +
                " FROM " + ContactChatEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Log.i("Chat entry", "Item " + cursor.getInt(0) + " : " + cursor.getString(1) + " : " + cursor.getInt(2) + " : " + cursor.getString(3));
            } while (cursor.moveToNext());
        }
    }

    private void printContactsTable() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + ContactChatEntry.COLUMN_ID + " , " + ContactChatEntry.COLUMN_PHONE_NUMBER + " , " + ContactChatEntry.COLUMN_NAME + " , " + ContactChatEntry.COLUMN_AVAILABLE_STATUS +
                " FROM " + ContactChatEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Log.i("Contact entry", "Item " + cursor.getInt(0) + " : " + cursor.getString(1) + " : " + cursor.getString(2) + " : " + cursor.getInt(3));
            } while (cursor.moveToNext());
        }
    }

    private void printMessagesTable() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + MessagesEntry.COLUMN_ID + " , " + MessagesEntry.COLUMN_PHONENUMBER + " , " + MessagesEntry.COLUMN_DATA_TEXT + " , " + MessagesEntry.COLUMN_CHAT_ID +
                " FROM " + MessagesEntry.TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Log.i("Message entry", "Item " + cursor.getInt(0) + " : " + cursor.getString(1) + " : " + cursor.getString(2) + " : " + cursor.getInt(3));
            } while (cursor.moveToNext());
        }
    }

    public boolean isGroupEntry(int chatId) {
        boolean groupEntry = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String projection[] = {ContactChatEntry.COLUMN_GROUP_CHAT};
        String selection = ContactChatEntry.COLUMN_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            groupEntry = c.getInt(0) == 1;
        }
        c.close();

        return groupEntry;
    }

    public ArrayList<String> getUserBlockedList() {
        ArrayList<String> userBlockedList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " + ContactChatEntry.COLUMN_JID + " FROM " + ContactChatEntry.TABLE_NAME +
                " WHERE " + ContactChatEntry.COLUMN_BLOCK_USER + " = ? AND " + ContactChatEntry.COLUMN_GROUP_CHAT + " == 0 ";
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

    public HashMap<String, ArrayList<String>> clearChat(Context context, int chatId) {
        HashMap<String, ArrayList<String>> mapOnType = getMediaFileNamesForParticularChat(chatId);
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = MessagesEntry.COLUMN_CHAT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

        if (chatId != DEFAULT_ENTRY_ID) {
            db.delete(MessagesEntry.TABLE_NAME, selection, selectionArgs);
            updateChatEntry(getDummyMessageRowId(), chatId);

            DBUtil.deleteContentFromExternalFileStorage(context, mapOnType);
        }

        return mapOnType;
    }

    public HashMap<String, ArrayList<String>> getMediaFileNamesForParticularChat(int chatId) {
        HashMap<String, ArrayList<String>> mapOnType = new HashMap<>();
        mapOnType.put(MIME_TYPE_IMAGE, new ArrayList<String>());
        mapOnType.put(MIME_TYPE_VIDEO, new ArrayList<String>());
        mapOnType.put(MIME_TYPE_AUDIO, new ArrayList<String>());

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + MessagesEntry.COLUMN_MEDIA_FILE_NAME + " , " + MessagesEntry.COLUMN_MIME_TYPE + " FROM " + MessagesEntry.TABLE_NAME + " WHERE " + MessagesEntry.COLUMN_CHAT_ID + " = ? ";
        String[] args = {String.valueOf(chatId)};

        Cursor cursor = db.rawQuery(selectQuery, args);
        if (cursor.moveToFirst()) {
            do {
                String filename = cursor.getString(0);
                String mimeType = cursor.getString(1);

                if (filename != null) {
                    ArrayList<String> list = mapOnType.get(mimeType);
                    list.add(filename);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return mapOnType;
    }

    public void clearChatEntry(int chatId, boolean isGroupChat) {
        if (!isGroupChat) {
            updateChatEntry(DEFAULT_ENTRY_ID, chatId);
        } else {
            //TODO
        }
    }

    public int updateChatUpdateRequired(int chatId, boolean updateRequired) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_UPDATE_REQUIRED, updateRequired);

        String selection = ContactChatEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(chatId)};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateUserBlockStatus(int contactId, boolean blockStatus) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_BLOCK_USER, blockStatus);

        String selection = ContactChatEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(contactId)};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void updateChatBlockStatus(int chatId, boolean blockStatus) {
        //TODO

//        SQLiteDatabase db = getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(ChatEntry.COLUMN_BLOCK_USER, blockStatus);
//
//        String selection = ChatEntry.COLUMN_CHAT_ID + " = ?";
//        String[] selectionArgs = {String.valueOf(chatId)};
//
//        int count = db.update(
//                ContactsEntry.TABLE_NAME,
//                values,
//                selection,
//                selectionArgs);

    }

    public boolean isChatBlocked(int chatId) {
        boolean block = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String projection[] = {ContactChatEntry.COLUMN_BLOCK_USER};
        String selection = ContactChatEntry.COLUMN_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            block = c.getInt(0) == 1;
        }
        c.close();

        return block;
    }

    public int muteConversation(int id, boolean mute) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_MUTE_CONVERSATION, mute);

        String selection = ContactChatEntry.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public boolean isMute(int id) {
        boolean mute = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String projection[] = {ContactChatEntry.COLUMN_MUTE_CONVERSATION};

        String selection = ContactChatEntry.COLUMN_ID + " LIKE ? ";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            mute = (c.getInt(0) == 1);
        }
        c.close();

        return mute;
    }

    public String getGroupSubject(String groupJID) {
        String name = null;
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {ContactChatEntry.COLUMN_NAME};
        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {groupJID};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            name = c.getString(0);
        }
        c.close();

        return name;
    }

    public void updateParticipantAsAdmin(ArrayList<Integer> selectedMembers, int chatId) {
        for (Integer id : selectedMembers) {
            updateParticipantAsAdmin(id, chatId);
        }
    }

    public int updateParticipantAsAdmin(int contactId, int chatId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GroupUserEntry.COLUMN_ADMIN, 1);

        String selection = GroupUserEntry.COLUMN_CONTACT_ID + " = ? and " + GroupUserEntry.COLUMN_CHAT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(contactId), String.valueOf(chatId)};

        return db.update(GroupUserEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateParticipantAsMember(int contactId, int chatId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GroupUserEntry.COLUMN_ADMIN, 0);

        String selection = GroupUserEntry.COLUMN_CONTACT_ID + " = ? and " + GroupUserEntry.COLUMN_CHAT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(contactId), String.valueOf(chatId)};

        return db.update(GroupUserEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int deleteGroupMember(int chatId, int contactId) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = GroupUserEntry.COLUMN_CONTACT_ID + " = ? And " + GroupUserEntry.COLUMN_CHAT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(contactId), String.valueOf(chatId)};

        return db.delete(GroupUserEntry.TABLE_NAME, selection, selectionArgs);
    }

    public int deleteGroupMembers(int chatId) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = GroupUserEntry.COLUMN_CHAT_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(chatId)};

        return db.delete(GroupUserEntry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteGroup(int chatId) {
        deleteContact(chatId);
        deleteGroupMembers(chatId);
    }

    public int deleteMessageFromTable(int messageId) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = MessagesEntry.COLUMN_ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(messageId)};

        return db.delete(MessagesEntry.TABLE_NAME, selection, selectionArgs);
    }

    public Message getMessage(int messageId) {
        Message message = null;
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

        Cursor c = db.query(MessagesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            boolean value = c.getInt(6) > 0;
            int id = c.getInt(9);
            boolean read = c.getInt(10) > 0;

            message = new Message(c.getInt(9), c.getString(0), c.getString(1), c.getBlob(2), c.getString(3), c.getString(4), c.getString(5), value, c.getString(7), c.getString(8), read, id, c.getString(11), c.getString(12));
        }
        c.close();

        return message;
    }

    public void deleteContactIfNotVisible(int contactId) {
        if (isContactAvailable(contactId)) {
            //nothing
        } else {
            SQLiteDatabase db = getWritableDatabase();

            String whereClause = ContactChatEntry.COLUMN_ID + " = ? ";
            String[] whereArgs = new String[]{String.valueOf(contactId)};

            db.delete(ContactChatEntry.TABLE_NAME, whereClause, whereArgs);
        }
    }

    public void deleteContact(int contactId) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = ContactChatEntry.COLUMN_ID + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(contactId)};

        db.delete(ContactChatEntry.TABLE_NAME, whereClause, whereArgs);
    }

    public void deleteContact(String phoneNumber) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = ContactChatEntry.COLUMN_PHONE_NUMBER + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(phoneNumber)};

        db.delete(ContactChatEntry.TABLE_NAME, whereClause, whereArgs);
    }

    private boolean isContactAvailable(int contactId) {
        boolean value = false;
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {ContactChatEntry.COLUMN_AVAILABLE_STATUS};
        String selection = ContactChatEntry.COLUMN_ID + " = ? ";
        String[] selectionArgs = {String.valueOf(contactId)};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            value = c.getInt(0) >= Contacts.AVAILABLE_BY_OTHER_CONTACTS;
        }
        c.close();

        return value;
    }

    public int updateContactAvailability(String jid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_AVAILABLE_STATUS, Contacts.AVAILABLE_BY_MY_CONTACTS);

        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {jid};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateFriendRequestStatus(String stanzaId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FriendRequestEntry.COLUMN_SERVER_RECEIPT_FOR_REQUEST_STANZA, stanzaId);

        String selection = FriendRequestEntry.COLUMN_REQUEST_STANZA_ID + " LIKE ? ";
        String[] selectionArgs = {stanzaId};

        return db.update(FriendRequestEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int updateContactFriendRequestStatus(String jid, int requestStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactChatEntry.COLUMN_PENDING_FRIEND_REQUEST, requestStatus);

        String selection = ContactChatEntry.COLUMN_JID + " LIKE ? ";
        String[] selectionArgs = {jid};

        return db.update(ContactChatEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public int createRequestStatusEntry(int contactId, String stanzaId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FriendRequestEntry.COLUMN_CONTACT_ID, contactId);
        values.put(FriendRequestEntry.COLUMN_REQUEST_STANZA_ID, stanzaId);

        return (int) db.insert(FriendRequestEntry.TABLE_NAME, null, values);
    }

    public int getContactIdByReceipt(String receiptId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = {FriendRequestEntry.COLUMN_CONTACT_ID};
        String selection = FriendRequestEntry.COLUMN_SERVER_RECEIPT_FOR_REQUEST_STANZA + " = ? ";
        String[] selectionArgs = {receiptId};

        Cursor c = db.query(FriendRequestEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            return c.getInt(0);
        }
        c.close();
        return -1;
    }

    public int getPendingFriendRequestCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectionArgs[] = new String[]{String.valueOf(Contacts.PENDING_REQUESTS_TO_PROCESS)};

        int numRows = (int) DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " +
                ContactChatEntry.TABLE_NAME + " WHERE " + ContactChatEntry.COLUMN_PENDING_FRIEND_REQUEST + " = ? ", selectionArgs);

        return numRows;
    }

    public int checkJidForPendingRequest(String jabberId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String[] projection = {ContactChatEntry.COLUMN_PENDING_FRIEND_REQUEST};
        String selection = ContactChatEntry.COLUMN_JID + " = ? ";
        String[] selectionArgs = {jabberId};

        Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            return c.getInt(0);
        }
        c.close();
        return -1;
    }

    public static class GroupParticipants {

        public int chatId;
        public ArrayList<Contacts> usersInGroup;
        public ArrayList<String> adminJids;

        public GroupParticipants(int chatId, ArrayList<Contacts> usersInGroup, ArrayList<String> adminJids) {
            this.chatId = chatId;
            this.usersInGroup = usersInGroup;
            this.adminJids = adminJids;
        }
    }

    public void addDummyMessageIfNotExist() {
        if (DUMMY_MESSAGE_ROW_ID == DEFAULT_ENTRY_ID) {
            boolean exist = false;
            SQLiteDatabase db = getReadableDatabase();

            String[] projection = {MessagesEntry.COLUMN_ID};
            String selection = MessagesEntry.COLUMN_CHAT_ID + " = ? ";
            String[] selectionArgs = {String.valueOf(DEFAULT_ENTRY_ID)};

            Cursor c = db.query(MessagesEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
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

    public void addDummyContactIfNotExist() {
        if (DUMMY_CONTACT_ROW_ID == DEFAULT_ENTRY_ID) {
            boolean exist = false;
            SQLiteDatabase db = getReadableDatabase();

            String[] projection = {ContactChatEntry.COLUMN_ID};
            String selection = ContactChatEntry.COLUMN_JID + " = ? ";
            String[] selectionArgs = {DUMMY_JID};

            Cursor c = db.query(ContactChatEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if (c.moveToFirst()) {
                exist = true;
                DUMMY_CONTACT_ROW_ID = c.getInt(0);
            } else {
                //nothing
            }
            c.close();

            if (!exist) {
                DUMMY_CONTACT_ROW_ID = addToContacts("", "", DUMMY_JID, "", null, Contacts.AVAILABLE_NOT);
            } else {
                //nothing
            }
        }
    }

}
