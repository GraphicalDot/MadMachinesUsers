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

/**
 * Created by madmachines on 1/9/15.
 */
public class SportsUnityDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "spu.db";

    private static final String COMMA_SEP = ",";

    private static final String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            ContactsEntry.TABLE_NAME + "( " +
            ContactsEntry.COLUMN_NAME + " " + "VARCHAR " + COMMA_SEP +
            ContactsEntry.COLUMN_PHONENUMBER + " VARCHAR UNIQUE " + COMMA_SEP +
            ContactsEntry.COLUMN_CHAT_ENABLED + " boolean " + COMMA_SEP +
            ContactsEntry.COLUMN_USER_IMAGE + " BLOB " + COMMA_SEP +
            ContactsEntry.COLUMN_UNREAD_COUNT + " INTEGER " + COMMA_SEP +
            ContactsEntry.COLUMN_STATUS + " VARCHAR " + COMMA_SEP +
            ContactsEntry.COLUMN_REGISTERED + " boolean);";

    private static final String CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS " +
            MessagesEntry.TABLE_NAME + "( " +
            MessagesEntry.COLUMN_MESSAGE + " " + "VARCHAR " + COMMA_SEP +
            MessagesEntry.COLUMN_PHONENUMBER + " VARCHAR " + COMMA_SEP +
            MessagesEntry.COLUMN_SEND_TIMESTAMP + "  DATETIME " + COMMA_SEP +
            MessagesEntry.COLUMN_NAME_I_AM_SENDER + " boolean " + COMMA_SEP +
            MessagesEntry.COLUMN_RECIEVE_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    private static final String DELETE_CONTACTS_TABLE = "DROP TABLE IF EXISTS " + ContactsEntry.TABLE_NAME;

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

    private SportsUnityDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void addToContacts(String name, String number, boolean registered, boolean chatLabel, String defaultStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsEntry.COLUMN_NAME, name);
        contentValues.put(ContactsEntry.COLUMN_PHONENUMBER, number);
        contentValues.put(ContactsEntry.COLUMN_CHAT_ENABLED, registered);
        contentValues.put(ContactsEntry.COLUMN_REGISTERED, chatLabel);
        contentValues.put(ContactsEntry.COLUMN_UNREAD_COUNT, 0);
        contentValues.put(ContactsEntry.COLUMN_STATUS, defaultStatus);

        db.insert(ContactsEntry.TABLE_NAME, null, contentValues);
        Log.i("Added", " bitch");
        db.close();

    }

    public ArrayList getAllContactsNumbersOnly() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<String> numbers = new ArrayList<>();
        String[] projection = {
                ContactsEntry.COLUMN_PHONENUMBER
        };

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if (c.moveToFirst()) {
            do {
                numbers.add(c.getString(0));
            } while (c.moveToNext());
        }

        return numbers;

    }

    public void addMessage(String msg, String number, boolean iamsender, String sentTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        values.put(MessagesEntry.COLUMN_PHONENUMBER, number);
        values.put(MessagesEntry.COLUMN_MESSAGE, msg);
        values.put(MessagesEntry.COLUMN_NAME_I_AM_SENDER, iamsender);
        values.put(MessagesEntry.COLUMN_SEND_TIMESTAMP, sentTime);
        values.put(MessagesEntry.COLUMN_RECIEVE_TIMESTAMP, String.valueOf(today.format("%k:%M")));

        db.insert(MessagesEntry.TABLE_NAME, null, values);
        //Log.i("Time : ", today.format("%k:%M"));
        Log.i("Added", " Message");
        db.close();

    }

    public void updateUnreadCount(String jid) {
        SQLiteDatabase db = this.getWritableDatabase();

        int unread = getUnreadCount(jid);

        ContentValues values = new ContentValues();
        values.put(ContactsEntry.COLUMN_UNREAD_COUNT, ++unread);

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {jid};

        int count = db.update(
                ContactsEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i(" unreadc :", String.valueOf(count));
        db.close();
    }

    public int getUnreadCount(String jid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactsEntry.COLUMN_UNREAD_COUNT
        };

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ?";
        String[] selectionArgs = {jid};

        Cursor c = db.query(
                ContactsEntry.TABLE_NAME,  // The table to query
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

        return 0;
    }

    public void clearUnreadCount(String jid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactsEntry.COLUMN_UNREAD_COUNT, 0);

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {jid};

        int count = db.update(
                ContactsEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i(" unreadc :", String.valueOf(count));
        db.close();
    }

    public ArrayList<Message> getMessages(String jid) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Message> list = new ArrayList<>();
        String[] projection = {
                MessagesEntry.COLUMN_PHONENUMBER,
                MessagesEntry.COLUMN_MESSAGE,
                MessagesEntry.COLUMN_NAME_I_AM_SENDER,
                MessagesEntry.COLUMN_RECIEVE_TIMESTAMP,
                MessagesEntry.COLUMN_SEND_TIMESTAMP
        };

        String selection = MessagesEntry.COLUMN_PHONENUMBER + " LIKE ?";
        String[] selectionArgs = {jid};

        Cursor c = db.query(
                MessagesEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        if (c.moveToFirst()) {
            do {
                boolean value = c.getInt(2) > 0;
                list.add(new Message(c.getString(0), c.getString(1), value, c.getString(3), c.getString(4)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;

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
        db.close();

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
        db.close();
    }

    public class Message {
        public String jid;
        public String msgTxt;
        public boolean iAmSender;
        public String send_Time;
        public String recieve_Time;

        Message(String phoneNumber, String msgtxt, Boolean iamsender, String recieve_time, String send_time) {
            this.jid = phoneNumber;
            this.msgTxt = msgtxt;
            this.iAmSender = iamsender;
            this.recieve_Time = recieve_time;
            this.send_Time = send_time;
        }
    }

    public ArrayList<Contacts> getContactList() {

        ArrayList<Contacts> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ContactsEntry.COLUMN_NAME,
                ContactsEntry.COLUMN_PHONENUMBER,
                ContactsEntry.COLUMN_REGISTERED,
                ContactsEntry.COLUMN_USER_IMAGE,
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
                list.add(new Contacts(c.getString(0), c.getString(1), value, c.getBlob(3), c.getString(4)));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;

    }

    public class Contacts {
        public String name;
        public String jid;
        public boolean registered;
        public byte[] image;
        public String status;

        Contacts(String name, String phoneNumber, Boolean registered, byte[] userimage, String status) {
            this.name = name;
            this.jid = phoneNumber;
            this.registered = registered;
            this.image = userimage;
            this.status = status;
        }

    }

    public ArrayList<Chats> getChatScreenList() {

        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Chats> list = new ArrayList<>();

        String selectQuery = " SELECT " + ContactsEntry.COLUMN_NAME + " ," + ContactsEntry.COLUMN_PHONENUMBER + " ," + ContactsEntry.COLUMN_UNREAD_COUNT + ","
                + ContactsEntry.COLUMN_USER_IMAGE + " ," + MessagesEntry.COLUMN_MESSAGE + " ," + MessagesEntry.COLUMN_SEND_TIMESTAMP + " ,"
                + MessagesEntry.COLUMN_RECIEVE_TIMESTAMP + " FROM " + ContactsEntry.TABLE_NAME + " INNER JOIN " + MessagesEntry.TABLE_NAME + " ON "
                + ContactsEntry.COLUMN_PHONENUMBER + " = " + MessagesEntry.COLUMN_PHONENUMBER + " GROUP BY " + ContactsEntry.COLUMN_PHONENUMBER + " ORDER BY "
                + MessagesEntry.COLUMN_MESSAGE + " DESC ";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Chats(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getBlob(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
            } while (cursor.moveToNext());
        }

        return list;

    }


    public class Chats {
        public String name;
        public String jid;
        public int unreadCount;
        public String sentTime;
        public String msg;
        public String recieveTime;
        public byte[] image;

        Chats(String name, String phoneNumber, int uc, byte[] userimage, String text, String senttime, String rectime) {
            this.name = name;
            this.jid = phoneNumber;
            this.unreadCount = uc;
            this.image = userimage;
            this.msg = text;
            this.sentTime = senttime;
            this.recieveTime = rectime;

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
        db.close();
        return list;

    }

    public void updateChatLabel(String jid) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContactsEntry.COLUMN_CHAT_ENABLED, true);

        String selection = ContactsEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {jid};

        int count = db.update(
                ContactsEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i("updated :", String.valueOf(count));
        db.close();
    }

    public void clearChat(String number) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = MessagesEntry.COLUMN_PHONENUMBER + " LIKE ? ";
        String[] selectionArgs = {number};

        db.delete(MessagesEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
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
        db.close();
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
        db.close();
        return null;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
        db.execSQL(CREATE_UNIQUE_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_CONTACTS_TABLE);
        onCreate(db);
    }
}
