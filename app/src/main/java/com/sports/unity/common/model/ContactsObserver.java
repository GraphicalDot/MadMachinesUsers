package com.sports.unity.common.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import com.sports.unity.Database.SportsUnityDBHelper;

import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by madmachines on 13/10/15.
 */
public class ContactsObserver extends ContentObserver {
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    private Context context;
    private static ContactsObserver CONTACTS_OBSERVER = null;
    private boolean contactSync = false;

    /**
     * this list contins numbers which are in the sports unity app database
     */
    private ArrayList<String> spuContacts;

    private HashMap<String, String> androidContacts;

    synchronized public static ContactsObserver getInstance(Handler handler, Context context) {
        if (CONTACTS_OBSERVER == null) {
            CONTACTS_OBSERVER = new ContactsObserver(handler, context);
        }
        return CONTACTS_OBSERVER;
    }

    private ContactsObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.i("callingonchange:", "yes");

        if (contactSync == false) {
            contactSync = true;
            ContactsHandler.getInstance().getAllContacts(this.context);

            spuContacts = SportsUnityDBHelper.getInstance(context).getAllContactsNumbersOnly();
            for (String c : spuContacts)
                Log.i("spucontacts :", c);
            androidContacts = readContactsDatabase();

            matchAndUpdate(spuContacts, androidContacts, context);
        }
    }

    public HashMap readContactsDatabase() {
        HashMap<String, String> contacts = new HashMap<>();
        String[] PROJECTION = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
        };
        String SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, null, null);
        while (cursor.moveToNext()) {
            String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contact_id}, null);
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (phoneNumber.length() < 10) {
                    //do nothing
                } else {
                    phoneNumber = phoneNumber.replaceAll("\\s+", "");
                    phoneNumber = phoneNumber.replaceAll("[-+.^:,]", "");
                    if (phoneNumber.startsWith("91")) {
                        //Do nothing
                    } else {
                        phoneNumber = "91" + phoneNumber;
                    }
                    contacts.put(phoneNumber, name);
                    Log.i("numberis :", phoneNumber);
                }
            }
        }

        return contacts;

    }

    private void matchAndUpdate(ArrayList<String> spuContacts, HashMap<String, String> androidContacts, Context context) {

        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        for (String contact : spuContacts) {
            if (androidContacts.containsKey(contact)) {
                sportsUnityDBHelper.updateUserName(contact, androidContacts.get(contact));
                sportsUnityDBHelper.updateChatEntryName(sportsUnityDBHelper.getContactId(contact), androidContacts.get(contact));
            } else {
                sportsUnityDBHelper.setPhonenumberAsName(contact);
                sportsUnityDBHelper.updateChatEntryName(sportsUnityDBHelper.getContactId(contact), contact);

            }
        }
        contactSync = false;
    }
}