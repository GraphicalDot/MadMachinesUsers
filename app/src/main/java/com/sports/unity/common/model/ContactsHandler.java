package com.sports.unity.common.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.util.Constants;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by madmachines on 2/9/15.
 */
public class ContactsHandler {

    public static final String defaultStatus = "Invite to Sports Unity";

    private static ContactsHandler CONTACT_HANDLER = null;

    public static ContactsHandler getInstance() {
        if (CONTACT_HANDLER == null) {
            CONTACT_HANDLER = new ContactsHandler();
        }
        return CONTACT_HANDLER;
    }

    private static void addToContactList(String number) throws SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        Presence response = new Presence(Presence.Type.subscribe);
        response.setTo(number + "@mm.io");
        XMPPClient.getConnection().sendPacket(response);
    }

    private boolean contactCopyInProgress = false;

    private byte[] imgs;
    private String status;

    private ContactsHandler() {

    }

    public void copyAllContacts_OnThread(Context context){
        if( contactCopyInProgress == false ) {
            contactCopyInProgress = true;

            boolean isProcessedBefore = TinyDB.getInstance(context).getBoolean( TinyDB.KEY_CONTACTS_COPIED_SUCESSFULLY, false);

            if( ! isProcessedBefore ) {
                AddContactThread addContactThread = new AddContactThread(context);
                addContactThread.start();
            } else {
                //nothing
            }
        } else {
            //nothing
        }
    }

    public void updateRegisteredUsers(Context context) throws XMPPException {

        String currentUserPhoneNumber = TinyDB.getInstance(context).getString(TinyDB.KEY_USERNAME);
        SportsUnityDBHelper.getInstance(context).addToContacts(currentUserPhoneNumber, currentUserPhoneNumber, true, ContactsHandler.getInstance().defaultStatus, false);

        ArrayList<String> contactNumberList = SportsUnityDBHelper.getInstance(context).readContactNumbers();
        for (int i = 0; i < contactNumberList.size(); i++) {
            String number = contactNumberList.get(i);
            XMPPService.answerForm.setAnswer("user", number);
            if (checkIfUserExists(number, XMPPService.searchManager, XMPPService.answerForm)) {
                SportsUnityDBHelper.getInstance(context).updateContacts(number, imgs, status);
            }
        }

        imgs = null;
        status = null;
    }

    HashMap readLatestUpdatedContactsFromSystem(Context context) {
        HashMap<String, String> contacts = new HashMap<>();
        String[] PROJECTION = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
        };

        long timeToCheck = System.currentTimeMillis() - 10 * 60 * 1000; // before ten minutes.

        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1' and " + ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " > ?";
        String selectionArgs[] = { String.valueOf(timeToCheck) };

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, selection, selectionArgs, null);
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
                    phoneNumber = phoneNumber.replaceAll("\\p{P}", "");
                    if (phoneNumber.startsWith("91")) {
                        //Do nothing
                    } else {
                        phoneNumber = "91" + phoneNumber;
                    }
                    contacts.put(phoneNumber, name);
                }
            }
        }

        return contacts;
    }

    void matchAndUpdate(HashMap<String, String> androidContacts, Context context) {

        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        Iterator<String> keyIterator = androidContacts.keySet().iterator();
        while ( keyIterator.hasNext() ) {
            String phoneNumber = keyIterator.next();
            String name = androidContacts.get(phoneNumber);
            name = name.trim();
            if ( ! name.isEmpty() ) {
                sportsUnityDBHelper.updateUserName(phoneNumber, name);
                sportsUnityDBHelper.updateChatEntryName(sportsUnityDBHelper.getContactId(phoneNumber), name, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
            } else {
                sportsUnityDBHelper.setPhonenumberAsName(phoneNumber);
                sportsUnityDBHelper.updateChatEntryName(sportsUnityDBHelper.getContactId(phoneNumber), phoneNumber, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
            }
        }

    }

    private Boolean checkIfUserExists(String number, UserSearchManager search, Form answerForm) throws XMPPException {
        Boolean flag = false;
        try {

            ReportedData data = search.getSearchResults(answerForm, "vjud.mm.io");
            if (data.getRows() != null) {
                Log.i("Inside IF", "Now");
                for (ReportedData.Row row : data.getRows()) {
                    Log.i("Inside first ", "For loop");
                    for (String value : row.getValues("jid")) {
                        VCard card = new VCard();
                        card.load(XMPPClient.getConnection(), number + "@mm.io");
                        imgs = card.getAvatar();
                        status = card.getMiddleName();
                        String name = card.getNickName();
                        addToContactList(number);
                        Log.i("Iterator values......", " " + value);
                        flag = true;
                    }
                }
                if (flag) {
                    Log.i("Returning true Now", " ");
                    return true;
                }
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void addContactsToApplicationDB(Context context) {
        String[] PROJECTION = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
        };
        String SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
        ContentResolver sContentResolver = context.getContentResolver();
        Cursor cursor = sContentResolver.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, SELECTION, null, null);
        while (cursor.moveToNext()) {
            String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String phoneNumber = null;
            Cursor phoneCursor = sContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contact_id}, null);
            while (phoneCursor.moveToNext()) {
                phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (phoneNumber.length() < 10) {
                    //do nothing
                } else {
                    phoneNumber = phoneNumber.replaceAll("\\s+", "");
                    phoneNumber = phoneNumber.replaceAll("[-+.^:,]", "");
                    if (phoneNumber.startsWith("91")) {
                        SportsUnityDBHelper.getInstance(context).addToContacts(name, phoneNumber, false, defaultStatus, true);
                    } else {
                        phoneNumber = "91" + phoneNumber;
                        SportsUnityDBHelper.getInstance(context).addToContacts(name, phoneNumber, false, defaultStatus, true);
                    }
                }
            }
        }
        cursor.close();
    }

    private class AddContactThread extends Thread {
        private Context context;

        public AddContactThread(Context context){
            this.context = context;
        }

        @Override
        public void run() {
            Log.i("Copy All Contacts", "Started");
            ContactsHandler.getInstance().addContactsToApplicationDB(context);

            TinyDB.getInstance(context).putBoolean(TinyDB.KEY_CONTACTS_COPIED_SUCESSFULLY, true);
            contactCopyInProgress = false;
            Log.i("Copy All Contacts", "Ended");
        }

    }

}

