package com.sports.unity.common.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sports.unity.BuildConfig;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ThreadTask;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Mad on 2/12/2016.
 * Helper class to handle the events related to user profile.
 */
public class UserProfileHandler {

    public static final String FB_REQUEST_TAG = "fb_request_tag";
    public static final String DOWNLOAD_IMAGE_REQUEST_TAG = "download_image_request_tag";
    public static final String CONNECT_XMPP_SERVER_TAG = "connect_xmpp_server_request_tag";
    public static final String SUBMIT_PROFILE_REQUEST_TAG = "submit_profile_tag";
    public static final String LOAD_PROFILE_REQUEST_TAG = "load_profile_tag";

    public static int REQUEST_STATUS_QUEUED = 1;
    public static int REQUEST_STATUS_ALREADY_EXIST = 2;
    public static int REQUEST_STATUS_FAILED = 3;

    private String SET_USER_INFO_URL = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/set_user_info?";
    private static final String SET_USER_INTEREST = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/set_user_interests?";

    private static UserProfileHandler USER_PROFILE_HANDLER;

    public static UserProfileHandler getInstance() {
        if (USER_PROFILE_HANDLER == null) {
            USER_PROFILE_HANDLER = new UserProfileHandler();
        }
        return USER_PROFILE_HANDLER;
    }

    private HashMap<String, ContentListener> contentListenerHashMap = new HashMap<>();
    private HashMap<String, String> requestInProcess_RequestTagAndListenerKey = new HashMap<>();

    private UserProfileHandler() {

    }

    public void addContentListener(String key, ContentListener contentListener) {
        contentListenerHashMap.put(key, contentListener);
    }

    public void removeContentListener(String key) {
        contentListenerHashMap.remove(key);
    }

    public int connectToXmppServer(Context context, String listenerKey) {
        int requestStatus = REQUEST_STATUS_FAILED;
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(CONNECT_XMPP_SERVER_TAG)) {

            requestInProcess_RequestTagAndListenerKey.put(CONNECT_XMPP_SERVER_TAG, listenerKey);
            requestStatus = REQUEST_STATUS_QUEUED;

            UserThreadTask userThreadTask = new UserThreadTask(context, CONNECT_XMPP_SERVER_TAG, null) {

                @Override
                public Object process() {
                    Boolean success = XMPPClient.getInstance().reconnectConnection();
                    if (success) {
                        success = XMPPClient.getInstance().authenticateConnection(this.context);
                    } else {
                        //nothing
                    }
                    return success;
                }

            };
            userThreadTask.start();

        } else {
            requestStatus = REQUEST_STATUS_ALREADY_EXIST;
        }

        return requestStatus;
    }

    public int loadMyProfile(Context context, String listenerKey) {
        String jid = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);
        return loadProfile(context, jid, listenerKey);
    }

    public int loadProfile(Context context, String jid, String listenerKey) {
        int requestStatus = REQUEST_STATUS_FAILED;
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(LOAD_PROFILE_REQUEST_TAG)) {

            if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                requestInProcess_RequestTagAndListenerKey.put(LOAD_PROFILE_REQUEST_TAG, listenerKey);
                requestStatus = REQUEST_STATUS_QUEUED;

                UserThreadTask userThreadTask = new UserThreadTask(context, LOAD_PROFILE_REQUEST_TAG, jid) {

                    @Override
                    public Object process() {
                        return loadVCardAndUpdateDB( this.context, (String)object);
                    }

                };
                userThreadTask.start();
            } else {
                //nothing
            }
        } else {
            requestStatus = REQUEST_STATUS_ALREADY_EXIST;
        }

        return requestStatus;
    }

    VCard loadVCardAndUpdateDB(Context context, String jid){
        return loadVCardAndUpdateDB(context, jid, true);
    }

    VCard loadVCardAndUpdateDB(Context context, String jid, boolean isAvailableInMyContacts){
        VCard card = new VCard();
        try {
            card.load(XMPPClient.getConnection(), jid + "@" + XMPPClient.SERVICE_NAME);

            String status = card.getMiddleName();
            byte[] image = card.getAvatar();
            String nickname = card.getNickName();

            if( isAvailableInMyContacts ) {
                SportsUnityDBHelper.getInstance(context).updateContacts(jid, image, status);
            } else {
                SportsUnityDBHelper.getInstance(context).updateContacts(jid, nickname, image, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            card = null;
        }

        return card;
    }

    public int submitUserProfile(Context context, Contacts contacts, String listenerKey) {
        int requestStatus = REQUEST_STATUS_FAILED;
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(SUBMIT_PROFILE_REQUEST_TAG)) {

            if (XMPPClient.getInstance().isConnectionAuthenticated()) {

                requestInProcess_RequestTagAndListenerKey.put(SUBMIT_PROFILE_REQUEST_TAG, listenerKey);
                requestStatus = REQUEST_STATUS_QUEUED;

                UserThreadTask userThreadTask = new UserThreadTask(context, SUBMIT_PROFILE_REQUEST_TAG, contacts) {

                    @Override
                    public Object process() {
                        Contacts userContact = (Contacts) object;

                        Boolean success = submitUserInfo(context, userContact);
                        if( success ) {
                            success = submitUserVCard(context, userContact);
                        }
                        return success;
                    }

                };
                userThreadTask.start();
            }
        } else {
            requestStatus = REQUEST_STATUS_ALREADY_EXIST;
        }
        return requestStatus;
    }

    public boolean submitUserFavorites(Context context){
        boolean success = false;
        try {
            success = sendInterests(context);

            if( success ) {
                success = false;

                if (UserUtil.isFilterCompleted() && XMPPClient.getConnection() != null) {
                    VCardManager manager = VCardManager.getInstanceFor(XMPPClient.getConnection());
                    VCard vCard = new VCard();
                    vCard.load(XMPPClient.getConnection());
                    vCard.setField("fav_list", TinyDB.getInstance(context).getString(TinyDB.FAVOURITE_FILTERS));
                    manager.saveVCard(vCard);

                    success = true;
                    UserUtil.setFavouriteVcardUpdated(context, true);
                } else {
                    success = false;
                    UserUtil.setFavouriteVcardUpdated(context, false);
                }
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return success;
    }

    public boolean submitUserCompleteProfile(Context context){
        String jid = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);
        Contacts userContact = SportsUnityDBHelper.getInstance(context).getContactByJid(jid);

        boolean success = submitUserInfo(context, userContact);
        if( success ) {
            success = submitUserVCard(context, userContact);
        }
        return success;
    }

    public Contacts getLoginUserDetail(Context context){
        String jid = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);
        return SportsUnityDBHelper.getInstance(context).getContactByJid(jid);
    }

    private boolean submitUserInfo(Context context, Contacts userContact){
        String jsonString = getUserInfoAsJSON(context, userContact);
        boolean success = false;
        HttpURLConnection httpURLConnection = null;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            URL sendInterests = new URL(SET_USER_INFO_URL);
            httpURLConnection = (HttpURLConnection) sendInterests.openConnection();
            httpURLConnection.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
            httpURLConnection.setDoInput(false);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");

            byteArrayInputStream = new ByteArrayInputStream(jsonString.getBytes());
            OutputStream outputStream = httpURLConnection.getOutputStream();

            byte chunk[] = new byte[4096];
            int read = 0;
            while ((read = byteArrayInputStream.read(chunk) ) != -1) {
                outputStream.write(chunk, 0, read);
            }

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                success = true;
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpURLConnection.disconnect();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    private boolean submitUserVCard(Context context, Contacts userContact){
        boolean success = false;
        try {
            VCardManager manager = VCardManager.getInstanceFor(XMPPClient.getConnection());
            VCard vCard = new VCard();
            vCard.setNickName(userContact.getName());
            vCard.setAvatar(userContact.image);
            vCard.setMiddleName(userContact.status);
            vCard.setJabberId(XMPPClient.getConnection().getUser());
            manager.saveVCard(vCard);

            success = true;
            saveLoginUserDetail(context, userContact);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return success;
    }

    private String getUserInfoAsJSON(Context context, Contacts userContact){
        String jsonString = null;
        try {
            String password = TinyDB.getInstance(context).getString(TinyDB.KEY_PASSWORD);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userContact.jid);
            jsonObject.put("password", password);
            jsonObject.put("name", userContact.getName());
            jsonObject.put("apk_version", CommonUtil.getBuildConfig());
            jsonObject.put("udid", CommonUtil.getDeviceId(context));

            jsonString = jsonObject.toString();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return jsonString;
    }

    private void saveLoginUserDetail(Context context, Contacts loginUserDetail){
        int count = SportsUnityDBHelper.getInstance(context).updateContacts(loginUserDetail.phoneNumber, loginUserDetail.jid, loginUserDetail.getName(), loginUserDetail.image, loginUserDetail.status, Contacts.AVAILABLE_NOT);
        if( count == 0 ) {
            SportsUnityDBHelper.getInstance(context).addToContacts(loginUserDetail.getName(), loginUserDetail.phoneNumber, loginUserDetail.jid, loginUserDetail.status, loginUserDetail.image, Contacts.AVAILABLE_NOT);
        }
    }

    private void fetchMyProfileFromDB(String listenerKey) {
        UserThreadTask userThreadTask = new UserThreadTask(null, listenerKey, null) {

            @Override
            public Object process() {
                //TODO fetch content from db.
                return null;
            }

        };
        userThreadTask.start();
    }

    private void saveMyProfileInDB(String listenerKey, Contacts contacts) {
        UserThreadTask userThreadTask = new UserThreadTask(null, listenerKey, contacts) {

            @Override
            public Object process() {
                Contacts userContact = (Contacts) object;
                //TODO save user profile in db.
                return null;
            }

        };
        userThreadTask.start();
    }

    public void initFacebookLogin(Context context) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("co.sports.unity", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void setFacebookDetails(final Context context, LoginButton loginButton, final String listenerKey, CallbackManager callback) {
        CallbackManager callbackManager = callback;
        loginButton.setReadPermissions(Arrays.asList("public_profile, email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                Toast.makeText(context.getApplicationContext(), loginResult.getAccessToken().getUserId().toString(), Toast.LENGTH_LONG).show();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                ContentListener contentListener = contentListenerHashMap.get(listenerKey);
                                if (contentListener != null) {
                                    try {
                                        ProfileDetail profileDetail = new ProfileDetail();
                                        profileDetail.setName(object.getString("name"));

                                        JSONObject data = response.getJSONObject();
                                        if (data.has("picture")) {
                                            String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                            profileDetail.setProfilePicUri(profilePicUrl);

                                            UserProfileHandler.getInstance().downloadImageFromUri(profileDetail, listenerKey, FB_REQUEST_TAG);
                                        } else {
                                            contentListener.handleContent(listenerKey, profileDetail);
                                        }

                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    //nothing
                                }
                            }

                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, R.string.profile_facebook_login_cancelled, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(context, R.string.profile_facebook_login_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void downloadProfileImage(ProfileDetail profileDetail, String listenerKey) {
        downloadImageFromUri(profileDetail, listenerKey, DOWNLOAD_IMAGE_REQUEST_TAG);
    }

    private void downloadProfileImage(ProfileDetail profileDetail, String listenerKey, String requestTag) {
        downloadImageFromUri(profileDetail, listenerKey, requestTag);
    }

    private int downloadImageFromUri(ProfileDetail profileDetail, String listenerKey, String requestTag) {
        int requestStatus = REQUEST_STATUS_FAILED;

        if (!requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {

            requestInProcess_RequestTagAndListenerKey.put(requestTag, listenerKey);
            requestStatus = REQUEST_STATUS_QUEUED;

            UserThreadTask userThreadTask = new UserThreadTask(null, requestTag, profileDetail) {

                @Override
                public Object process() {
                    ProfileDetail profileDetail = (ProfileDetail) object;
                    InputStream in = null;
                    try {
                        in = new java.net.URL(profileDetail.getProfilePicUri()).openStream();
                        profileDetail.bitmap = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (Exception ex) {
                        }
                    }
                    return profileDetail;
                }

            };
            userThreadTask.start();
        } else {
            requestStatus = REQUEST_STATUS_ALREADY_EXIST;
        }

        return requestStatus;
    }

    private static boolean sendInterests(Context context) {
        boolean  success = false;
        String jsonContent = null;
        try {
            JSONArray interests = FavouriteItemWrapper.getInstance(context).getAllInterestsAsJsonArray();

            String password = TinyDB.getInstance(context).getString(TinyDB.KEY_PASSWORD);
            String userJID = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userJID);
            jsonObject.put("password", password);
            jsonObject.put("interests", interests);
            jsonObject.put("apk_version", CommonUtil.getBuildConfig());
            jsonObject.put("udid", CommonUtil.getDeviceId(context));

            jsonContent = jsonObject.toString();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        if( jsonContent != null ) {
            HttpURLConnection httpURLConnection = null;
            ByteArrayInputStream byteArrayInputStream = null;
            try {
                URL sendInterests = new URL(SET_USER_INTEREST);
                httpURLConnection = (HttpURLConnection) sendInterests.openConnection();
                httpURLConnection.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
                httpURLConnection.setDoInput(false);
                httpURLConnection.setRequestMethod("POST");

                byteArrayInputStream = new ByteArrayInputStream(jsonContent.getBytes());
                OutputStream outputStream = httpURLConnection.getOutputStream();

                byte chunk[] = new byte[4096];
                int read = 0;
                while ((read = byteArrayInputStream.read(chunk) ) != -1) {
                    outputStream.write(chunk, 0, read);
                }

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    success = true;
                } else {
                    //nothing
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    httpURLConnection.disconnect();
                } catch (Exception ex) {
                }
            }
        } else {
            //nothing
        }

        return success;
    }

    public static class ProfileDetail {
        private String name = null;
        private String profilePicUri = null;
        private Bitmap bitmap = null;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setProfilePicUri(String profilePicUri) {
            this.profilePicUri = profilePicUri;
        }

        public String getProfilePicUri() {
            return profilePicUri;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }

    private abstract class UserThreadTask extends ThreadTask {

        public Context context = null;
        private String requestTag = null;

        public UserThreadTask(Context context, String requestTag, Object helperObject) {
            super(helperObject);
            this.context = context;
            this.requestTag = requestTag;
        }

        @Override
        abstract public Object process();

        @Override
        public void postAction(Object content) {
            if (requestInProcess_RequestTagAndListenerKey.containsKey(requestTag)) {
                ContentListener contentListener = contentListenerHashMap.get(requestInProcess_RequestTagAndListenerKey.get(requestTag));
                requestInProcess_RequestTagAndListenerKey.remove(requestTag);
                if (contentListener != null) {
                    contentListener.handleContent(requestTag, content);
                } else {
                    //nothing
                }
            } else {

            }
        }
    }

    public interface ContentListener {

        public void handleContent(String listenerKey, Object content);

    }

}