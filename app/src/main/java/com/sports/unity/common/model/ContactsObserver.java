package com.sports.unity.common.model;

import android.Manifest;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.sports.unity.Database.SportsUnityDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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

    private Context context = null;

    private ContactsObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        if( context != null ) {
            if (!PermissionUtil.getInstance().isRuntimePermissionRequired()) {
                ContactsHandler.getInstance().addCallToSyncLatestContacts(context, true);
            } else if (PermissionUtil.getInstance().isPermissionGranted(context, Manifest.permission.READ_CONTACTS)) {
                ContactsHandler.getInstance().addCallToSyncLatestContacts(context, true);
            }
        }

    }

}