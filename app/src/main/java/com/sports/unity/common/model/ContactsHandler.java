package com.sports.unity.common.model;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.jivesoftware.smack.roster.Roster;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by madmachines on 2/9/15.
 */
public class ContactsHandler {

    public static final String defaultStatus = "Invite to Sports Unity";

    private static final String URL_REQUEST_CONTACT_JIDS = "http://" + XMPPClient.SERVER_HOST + "/get_contact_jids";

    private static final int CONTACT_PENDING_ACTION_NONE = 1;
    private static final int CONTACT_PENDING_ACTION_COPY_LOCALLY = 3;
    private static final int CONTACT_PENDING_ACTION_FETCH_JID = 5;
    private static final int CONTACT_PENDING_ACTION_ADD_NEW_CONTACTS = 7;
    private static final int CONTACT_PENDING_ACTION_UPDATE_USER_FAVORITES = 11;

    private static final int CONTACT_PENDING_ACTION_DEFAULT_VALUE = CONTACT_PENDING_ACTION_COPY_LOCALLY*CONTACT_PENDING_ACTION_FETCH_JID;

    private static ContactsHandler CONTACT_HANDLER = null;

    public static ContactsHandler getInstance() {
        if (CONTACT_HANDLER == null) {
            CONTACT_HANDLER = new ContactsHandler();
        }
        return CONTACT_HANDLER;
    }

    private Roster roster = null;
    private boolean inProcess = false;

    private ContactsHandler() {

    }

    synchronized public void addCallToSyncContacts(Context context){
        if( ! inProcess ){
            addContactActionsToProcess(context, true);
            process(context);
        } else {
            //nothing

            Log.d("ContactsHandler", "already processing");
        }
    }

    synchronized public void addCallToSyncLatestContacts(Context context, boolean wait){
        if( ! inProcess ){
            addContactActionsToProcess(context, false);
            process(context);
        } else {
            if( wait ) {
                addPendingActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_ADD_NEW_CONTACTS);
            } else {
                //nothing
            }

            Log.d("ContactsHandler", "already processing");
        }
    }

    synchronized public void addCallToUpdateUserFavorites(Context context){
        addPendingActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_UPDATE_USER_FAVORITES);

        if( ! inProcess ){
            process(context);
        } else {
            //nothing
        }
    }

    synchronized public void addCallToProcessPendingActions(Context context){
        Log.d("ContactsHandler", "process pending actions");

        if( ! inProcess ) {
            process(context);
        } else {
            //nothing
        }

    }

    private void addContactActionsToProcess(Context context, boolean allContacts){
        if( allContacts ) {
            addPendingActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_COPY_LOCALLY);
            addPendingActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_FETCH_JID);
        } else {
            addPendingActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_ADD_NEW_CONTACTS);
        }

    }

    private void process(Context context){
        ContactsThread contactsThread = new ContactsThread(context);
        contactsThread.start();
    }

    private void onCompleteActionAndUpdatePendingActions(Context context, int completedAction){
        int pendingActions = TinyDB.getInstance(context).getInt(TinyDB.KEY_ALL_CONTACTS_SYNC_STATUS, CONTACT_PENDING_ACTION_DEFAULT_VALUE);
        pendingActions = removePendingAction(pendingActions, completedAction);
        TinyDB.getInstance(context).putInt(TinyDB.KEY_ALL_CONTACTS_SYNC_STATUS, pendingActions);
    }

    private void addPendingActionAndUpdatePendingActions(Context context, int addAction){
        int pendingActions = TinyDB.getInstance(context).getInt(TinyDB.KEY_ALL_CONTACTS_SYNC_STATUS, CONTACT_PENDING_ACTION_DEFAULT_VALUE);
        pendingActions = addPendingAction(pendingActions, addAction);
        TinyDB.getInstance(context).putInt(TinyDB.KEY_ALL_CONTACTS_SYNC_STATUS, pendingActions);
    }

    private int removePendingAction(int pendingActions, int completedAction){
        int value = pendingActions;
        if( isPendingAction(pendingActions, completedAction) ){
            value = pendingActions / completedAction;
        } else {
            //nothing
        }
        return value;
    }

    private int addPendingAction(int pendingActions, int addAction){
        int value = pendingActions;
        if( ! isPendingAction(pendingActions, addAction) ){
            value = pendingActions * addAction;
        } else {
            //nothing
        }
        return value;
    }

    private boolean isPendingAction(int pendingActions, int actionToCheck){
        boolean pending = false;
        if( pendingActions % actionToCheck == 0 ){
            pending = true;
        } else {
            //nothing
        }
        return pending;
    }

    private ArrayList<String> addContactsToApplicationDB(Context context, boolean allContacts) {
        ArrayList<String> addedContacts = new ArrayList<>();

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
        };

        ContentResolver sContentResolver = context.getContentResolver();

        long timeToCheck = System.currentTimeMillis() - 10 * 60 * 1000; // before ten minutes.

        String[] selectionArgs = null;
        StringBuilder selectionBuilder = new StringBuilder();
        selectionBuilder.append(ContactsContract.Contacts.HAS_PHONE_NUMBER + "='1' ");

        if( ! allContacts ) {
            selectionBuilder.append(" and ");
            selectionBuilder.append(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP);
            selectionBuilder.append(" > ?");

            selectionArgs = new String[]{ String.valueOf(timeToCheck) };
        } else {
            //nothing
        }

        Cursor cursor = sContentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, selectionBuilder.toString(), selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                String phoneNumber = null;
                Cursor phoneCursor = sContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contact_id}, null);
                if ( phoneCursor != null ) {
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        phoneNumber = removeExtraCharacters(phoneNumber);
                        phoneNumber = checkAndUpdateCountryCode(phoneNumber);

                        if ( isValidPhoneNumber(phoneNumber) ) {
                            SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
                            long rowId = SportsUnityDBHelper.getInstance(context).addToContacts(name, phoneNumber, null, defaultStatus, null, SportsUnityDBHelper.AVAILABLE_BY_MY_CONTACTS);
                            if( rowId == -1 ){
                                if ( ! name.isEmpty() ) {
                                    sportsUnityDBHelper.updateUserContactFromPhoneContactDetails(phoneNumber, name);
                                    sportsUnityDBHelper.updateChatEntryName(sportsUnityDBHelper.getContactIdFromPhoneNumber(phoneNumber), name, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
                                } else {
                                    sportsUnityDBHelper.setPhoneNumberAsName(phoneNumber);
                                    sportsUnityDBHelper.getInstance(context).updateChatEntryName(sportsUnityDBHelper.getContactIdFromPhoneNumber(phoneNumber), phoneNumber, SportsUnityDBHelper.DEFAULT_GROUP_SERVER_ID);
                                }
                            } else {
                                //nothing
                            }

                            addedContacts.add(phoneNumber);
                        } else {
                            //nothing
                        }
                    }
                    phoneCursor.close();
                }
            }
            cursor.close();
        } else {
            //nothing
        }

        return addedContacts;
    }

    private boolean isValidPhoneNumber(String phoneNumber){
        //TODO
        return true;
    }

    private String checkAndUpdateCountryCode(String phoneNumber){
        String countryCode = UserUtil.getCountryCode();
        if( phoneNumber.startsWith("+" + countryCode) || phoneNumber.startsWith(countryCode) ){

        } else {
            phoneNumber = countryCode + phoneNumber;
        }
        return phoneNumber;
    }

    private String removeExtraCharacters(String phoneNumber){
        phoneNumber = phoneNumber.replaceAll("\\s+", "");
        phoneNumber = phoneNumber.replaceAll("[-+.^:,()]", "");
        return phoneNumber;
    }

    private boolean getAndUpdateContactJIDs(Context context){
        ArrayList<String> phoneNumbers = SportsUnityDBHelper.getInstance(context).getAllPhoneNumbers(false);
        return getAndUpdateContactJIDs(context, phoneNumbers);
    }

    private boolean getAndUpdateContactJIDs(Context context, ArrayList<String> phoneNumbers){
        String username = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);
        String password = TinyDB.getInstance(context).getString(TinyDB.KEY_PASSWORD);
        String apk_version = CommonUtil.getBuildConfig();
        String udid = CommonUtil.getDeviceId(context);

        String request = getRequestJsonContentForGetContactList(username, password, apk_version, udid, phoneNumbers);
        return makeHttpCallToFetchJID(context, request);
    }

    private boolean handleResponseOfGetJIDs(Context context, String content, int responseCode){
        boolean success = false;
        if( responseCode == HttpURLConnection.HTTP_OK ){
            try{
                JSONObject jsonObject = new JSONObject(content);

                int info = jsonObject.getInt("status");
                if( info == 200 ){
                    JSONArray list = jsonObject.getJSONArray("jids");

                    String phoneNumber = null;
                    String jid = null;
                    for( int index = 0 ; index < list.length() ; index++ ){
                        JSONObject map = list.getJSONObject(index);
                        phoneNumber = map.getString("phone_number");
                        jid = map.getString("username");

                        SportsUnityDBHelper.getInstance(context).updateContacts(phoneNumber, jid);
                    }

                    for( int index = 0 ; index < list.length() ; index++ ){
                        JSONObject map = list.getJSONObject(index);
                        jid = map.getString("username");

                        UserProfileHandler.getInstance().loadVCardAndUpdateDB(context, jid);
                    }

                    success = true;
                } else {

                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        } else {
            //nothing
        }
        return success;
    }

    private boolean makeHttpCallToFetchJID(Context context, String requestBody){
        Log.i("Contacts Handler", "http call start");

        String response = null;
        int responseCode = 0;

        HttpURLConnection httpURLConnection = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            URL url = new URL(URL_REQUEST_CONTACT_JIDS);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
            httpURLConnection.setReadTimeout(Constants.CONNECTION_READ_TIME_OUT);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setChunkedStreamingMode(4096);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            byteArrayInputStream = new ByteArrayInputStream(requestBody.getBytes());

            byte chunk[] = new byte[4096];
            int read = 0;
            while ((read = byteArrayInputStream.read(chunk)) != -1) {
                outputStream.write(chunk, 0, read);
            }

            responseCode = httpURLConnection.getResponseCode();
            if ( responseCode == HttpURLConnection.HTTP_OK) {

                InputStream is = httpURLConnection.getInputStream();
                byteArrayOutputStream = new ByteArrayOutputStream();
                read = 0;
                while ((read = is.read(chunk)) != -1) {
                    byteArrayOutputStream.write(chunk, 0, read);
                }

                response = new String(byteArrayOutputStream.toByteArray());
            } else {
                //nothing
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayInputStream.close();
            } catch (Exception ex) {
            }
            try {
                httpURLConnection.disconnect();
            } catch (Exception ex) {
            }
        }
        Log.i("Contacts Handler", "http call ends");

        return handleResponseOfGetJIDs(context, response, responseCode);
    }

    private String getRequestJsonContentForGetContactList(String username, String password, String apkVersion, String udid, ArrayList<String> contacts){
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("apk_version", apkVersion);
            jsonObject.put("udid", udid);

            JSONArray jsonArray = new JSONArray(contacts);
            jsonObject.put("contacts", jsonArray);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return jsonObject.toString();
    }

    private boolean isContactAccessGranted(Context context) {
        boolean granted = false;
        if ( ! PermissionUtil.getInstance().isRuntimePermissionRequired() ) {
            granted = true;
        } else if( PermissionUtil.getInstance().isPermissionGranted(context, Manifest.permission.READ_CONTACTS) &&
                PermissionUtil.getInstance().isPermissionGranted(context, Manifest.permission.WRITE_CONTACTS) ) {
            granted = true;
        }
        return granted;
    }

    private class ContactsThread extends Thread {

        private Context context = null;
        private int failedActions = CONTACT_PENDING_ACTION_NONE;

        public ContactsThread(Context context){
            this.context = context;
        }

        @Override
        public void run() {
            processPendingActions(context);

            TinyDB.getInstance(context).putInt(TinyDB.KEY_ALL_CONTACTS_SYNC_STATUS, failedActions);
            Log.d("ContactsHandler", "process completed with pending actions " + failedActions);
        }

        private void processPendingActions(Context context){
            {
                int pendingActions = TinyDB.getInstance(context).getInt(TinyDB.KEY_ALL_CONTACTS_SYNC_STATUS, CONTACT_PENDING_ACTION_DEFAULT_VALUE);
                if (isPendingAction(pendingActions, CONTACT_PENDING_ACTION_COPY_LOCALLY)) {
                    Log.d("ContactsHandler", "copy contacts");

                    if ( isContactAccessGranted(context) ) {
                        addContactsToApplicationDB(context, true);
                    } else {
                        //nothing
                    }

                    onCompleteActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_COPY_LOCALLY);
                } else if (isPendingAction(pendingActions, CONTACT_PENDING_ACTION_FETCH_JID)) {
                    Log.d("ContactsHandler", "fetch jid");

                    boolean success = getAndUpdateContactJIDs(context);
                    if (success) {
                        //nothing
                    } else {
                        failedActions = addPendingAction(failedActions, CONTACT_PENDING_ACTION_FETCH_JID);
                    }

                    onCompleteActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_FETCH_JID);
                } else if (isPendingAction(pendingActions, CONTACT_PENDING_ACTION_ADD_NEW_CONTACTS)) {
                    Log.d("ContactsHandler", "copy latest contacts");

                    if ( isContactAccessGranted(context) ) {

                        ArrayList<String> phoneNumbers = addContactsToApplicationDB(context, false);
                        addPendingActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_FETCH_JID);
                        onCompleteActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_ADD_NEW_CONTACTS);

                        Log.d("ContactsHandler", "fetch latest jid");
                        boolean success = getAndUpdateContactJIDs(context, phoneNumbers);
                        if (success) {
                            //nothing
                        } else {
                            failedActions = addPendingAction(failedActions, CONTACT_PENDING_ACTION_FETCH_JID);
                        }

                        onCompleteActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_FETCH_JID);
                    } else {
                        //nothing
                        onCompleteActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_ADD_NEW_CONTACTS);
                    }

                } else if (isPendingAction(pendingActions, CONTACT_PENDING_ACTION_UPDATE_USER_FAVORITES)) {
                    Log.d("ContactsHandler", "update login user favorites on server");

                    boolean success = UserProfileHandler.getInstance().submitUserFavorites(context);
                    if( success ) {
                        //nothing
                    } else {
                        failedActions = addPendingAction(failedActions, CONTACT_PENDING_ACTION_UPDATE_USER_FAVORITES);
                    }

                    onCompleteActionAndUpdatePendingActions(context, CONTACT_PENDING_ACTION_UPDATE_USER_FAVORITES);
                }
            }

            {
                int pendingActions = TinyDB.getInstance(context).getInt(TinyDB.KEY_ALL_CONTACTS_SYNC_STATUS, CONTACT_PENDING_ACTION_DEFAULT_VALUE);
                if (pendingActions == CONTACT_PENDING_ACTION_NONE) {
                    //nothing
                } else {
                    processPendingActions(context);
                }
            }
        }

    }

}

