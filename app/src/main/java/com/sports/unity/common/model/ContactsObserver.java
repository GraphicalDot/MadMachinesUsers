package com.sports.unity.common.model;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.sports.unity.Database.SportsUnityDBHelper;

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

    private static ContactsObserver CONTACTS_OBSERVER = null;

    synchronized public static ContactsObserver getInstance(Handler handler, Context context) {
        if (CONTACTS_OBSERVER == null) {
            CONTACTS_OBSERVER = new ContactsObserver(handler, context);
        }
        return CONTACTS_OBSERVER;
    }

    private Context context;
    private boolean contactSyncInProgress = false;

    private ContactsObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {

        super.onChange(selfChange);

        if (contactSyncInProgress == false) {
            contactSyncInProgress = true;

            SyncContactThread syncContactThread = new SyncContactThread();
            syncContactThread.start();
        }
    }

    class SyncContactThread extends Thread {

        @Override
        public void run() {
            Log.i("Contact Sync:", "Started");

            ContactsHandler contactsHandler = ContactsHandler.getInstance();
//            contactsHandler.addContactsToApplicationDB(ContactsObserver.this.context);

//            ArrayList<String> spuContacts = SportsUnityDBHelper.getInstance(context).getAllContactsNumbersOnly();
            HashMap<String, String> androidContacts = contactsHandler.readLatestUpdatedContactsFromSystem(ContactsObserver.this.context);

            contactsHandler.matchAndUpdate(androidContacts, context);

            contactSyncInProgress = false;
            Log.i("Contact Sync:", "Ended");
        }

    }

}