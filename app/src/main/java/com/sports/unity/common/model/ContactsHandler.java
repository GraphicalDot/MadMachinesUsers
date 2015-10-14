package com.sports.unity.common.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;

/**
 * Created by madmachines on 2/9/15.
 */
public class ContactsHandler {

    static Context sContext;
    static byte[] imgs;
    static String status;

    public ContactsHandler(Context c) {
        this.sContext = c;
    }

    static ContentResolver sContentResolver;

    static final String defaultStatus = "Invite to Sports Unity";

    public static void getaAllContacts() {
        String[] PROJECTION = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
        };
        String SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1'";
        sContentResolver = sContext.getContentResolver();
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
                        SportsUnityDBHelper.getInstance(sContext).addToContacts(name, phoneNumber, false, false, defaultStatus);
                    } else {
                        phoneNumber = "91" + phoneNumber;
                        SportsUnityDBHelper.getInstance(sContext).addToContacts(name, phoneNumber, false, false, defaultStatus);
                    }
                }
            }
        }
        cursor.close();
    }

    public static void updateRegisteredUsers() throws XMPPException {

        Log.i("Updating  : ", "contacsupdating");
        ArrayList<String> contactNumberList = SportsUnityDBHelper.getInstance(sContext).readContactNumbers();
        for (int i = 0; i < contactNumberList.size(); i++) {
            String number = contactNumberList.get(i);
            MainActivity.answerForm.setAnswer("user", number);
            if (checkIfUserExists(number, MainActivity.searchForm, MainActivity.searchManager, MainActivity.answerForm)) {
                SportsUnityDBHelper.getInstance(sContext).updateContacts(number, imgs, status);
            }
        }

    }

    public static Boolean checkIfUserExists(String number, Form searchForm, UserSearchManager search, Form answerForm) throws XMPPException {
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
        }


        return false;
    }


}

