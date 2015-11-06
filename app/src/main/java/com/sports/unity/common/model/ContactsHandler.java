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

/**
 * Created by madmachines on 2/9/15.
 */
public class ContactsHandler {

    public static final String defaultStatus = "Invite to Sports Unity";

    private static ContactsHandler CONTACT_HANDLER = null;

    public static ContactsHandler getInstance(){
        if( CONTACT_HANDLER == null ){
            CONTACT_HANDLER = new ContactsHandler();
        }
        return CONTACT_HANDLER;
    }

    private byte[] imgs;
    private String status;

    private ContactsHandler() {

    }

    public void getAllContacts(Context context) {
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

    public void updateRegisteredUsers(Context context) throws XMPPException {

        Log.i("Updating  : ", "contacsupdating");
        String currentUserPhoneNumber = TinyDB.getInstance(context).getString(TinyDB.KEY_USERNAME);
        SportsUnityDBHelper.getInstance(context).addToContacts(currentUserPhoneNumber, currentUserPhoneNumber, true, ContactsHandler.getInstance().defaultStatus, false);

        ArrayList<String> contactNumberList = SportsUnityDBHelper.getInstance(context).readContactNumbers();
        for (int i = 0; i < contactNumberList.size(); i++) {
            String number = contactNumberList.get(i);
            XMPPService.answerForm.setAnswer("user", number);
            if (checkIfUserExists(number, XMPPService.searchForm, XMPPService.searchManager, XMPPService.answerForm)) {
                SportsUnityDBHelper.getInstance(context).updateContacts(number, imgs, status);
            }
        }

        imgs = null;
        status = null;
    }

    private Boolean checkIfUserExists(String number, Form searchForm, UserSearchManager search, Form answerForm) throws XMPPException {
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

    private static void addToContactList(String number) throws SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        Presence response = new Presence(Presence.Type.subscribe);
        response.setTo(number + "@mm.io");
        XMPPClient.getConnection().sendPacket(response);
    }


}

