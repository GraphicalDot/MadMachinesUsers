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
import com.sports.unity.XMPPManager.PubSubUtil;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.messages.controller.model.PubSubMessaging;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ThreadTask;
import com.sports.unity.util.UserCard;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Mad on 2/12/2016.
 * Helper class to handle the events related to user profile.
 */
public class UserProfileHandler {

    public static final String FB_REQUEST_TAG = "fb_request_tag";
    public static final String DOWNLOADING_FACEBOOK_IMAGE_TAG = "download_facebook_image_request_tag";
    public static final String SUBMIT_PROFILE_REQUEST_TAG = "submit_profile_tag";
    public static final String LOAD_PROFILE_REQUEST_TAG = "load_profile_tag";
    public static final String SUBMIT_GROUP_INFO_REQUEST_TAG = "submit_group_info_tag";

    public static final String SUBMIT_GROUP_LEAVING_REQUEST_TAG = "submit_group_leaving_tag";

    public static final String IMAGE_THUMNB = "S";
    public static final String IMAGE_LARGE = "L";

    public static int REQUEST_STATUS_QUEUED = 1;
    public static int REQUEST_STATUS_ALREADY_EXIST = 2;
    public static int REQUEST_STATUS_FAILED = 3;

    private static final String GET_USER_INFO_URL = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/get_user_info?";
    private static final String SET_USER_INFO_URL = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/set_user_info?";
    private static final String SET_USER_INTEREST = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/v1/set_user_interests?";

    private static final String SET_DISPLAY_PIC = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/set_dp?";
    private static final String GET_DISPLAY_PIC = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/get_dp?";

    private static final String SUBMIT_LEAVING_GROUP = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/exit_discussion?";

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

    public boolean requestInProgress() {
        boolean inProgress = false;
        if (requestInProcess_RequestTagAndListenerKey.size() > 0) {
            inProgress = true;
        }
        return inProgress;
    }

    public boolean isFacebookDetailFetchingInProgress() {
        boolean inProgress = false;
        if (requestInProcess_RequestTagAndListenerKey.containsKey(FB_REQUEST_TAG)) {
            inProgress = true;
        }
        return inProgress;
    }

    public int loadMyProfile(Context context, String listenerKey) {
        String jid = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);
        return loadProfile(context, jid, listenerKey);
    }

    public int loadProfile(Context context, String jid, String listenerKey) {
        int requestStatus = REQUEST_STATUS_FAILED;
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(LOAD_PROFILE_REQUEST_TAG)) {
            requestInProcess_RequestTagAndListenerKey.put(LOAD_PROFILE_REQUEST_TAG, listenerKey);
            requestStatus = REQUEST_STATUS_QUEUED;

            UserThreadTask userThreadTask = new UserThreadTask(context, LOAD_PROFILE_REQUEST_TAG, jid) {

                @Override
                public Object process() {
                    Contacts contacts = SportsUnityDBHelper.getInstance(context).getContactByJid((String) object);
                    return loadVCardAndUpdateDB(this.context, (String) object, true, contacts.availableStatus == Contacts.AVAILABLE_BY_MY_CONTACTS);
                }

            };
            userThreadTask.start();
        } else {
            requestStatus = REQUEST_STATUS_ALREADY_EXIST;
        }

        return requestStatus;
    }

    UserCard loadVCardAndUpdateDB(Context context, String jid, boolean loadInterests) {
        return loadVCardAndUpdateDB(context, jid, loadInterests, true);
    }

    UserCard loadVCardAndUpdateDBWithNoUpdateRequired(Context context, String jid, boolean loadInterests, boolean isAvailableInMyContacts) {
        UserCard card = new UserCard();
        try {
            card.loadCard(context, jid, true, true, loadInterests, true, false);

            String status = card.getStatus();
            byte[] image = card.getThumbnail();
            String nickname = card.getName();

            if (isAvailableInMyContacts) {
                SportsUnityDBHelper.getInstance(context).updateContacts(jid, image, status, false);
            } else {
                SportsUnityDBHelper.getInstance(context).updateContacts(jid, nickname, image, status, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            card = null;
        }

        return card;
    }

    UserCard loadVCardAndUpdateDB(Context context, String jid, boolean loadInterests, boolean isAvailableInMyContacts) {
        UserCard card = new UserCard();
        try {
            boolean success = card.loadCard(context, jid, true, true, loadInterests, true, false);
            if (success) {
                String status = card.getStatus();
                byte[] image = card.getThumbnail();
                String nickname = card.getName();

                if (isAvailableInMyContacts) {
                    SportsUnityDBHelper.getInstance(context).updateContacts(jid, image, status);
                } else {
                    SportsUnityDBHelper.getInstance(context).updateContacts(jid, nickname, image, status);
                }
            } else {
                card = null;
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
            requestInProcess_RequestTagAndListenerKey.put(SUBMIT_PROFILE_REQUEST_TAG, listenerKey);
            requestStatus = REQUEST_STATUS_QUEUED;

            UserThreadTask userThreadTask = new UserThreadTask(context, SUBMIT_PROFILE_REQUEST_TAG, contacts) {

                @Override
                public Object process() {
                    Contacts userContact = (Contacts) object;

                    Boolean success = submitUserVCard(context, userContact);
                    return success;
                }

            };
            userThreadTask.start();
        } else {
            requestStatus = REQUEST_STATUS_ALREADY_EXIST;
        }
        return requestStatus;
    }

    public int submitGroupInfo(Context context, String groupJid, String groupTitle, String groupImage, String listenerKey) {
        int requestStatus = REQUEST_STATUS_FAILED;
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(SUBMIT_GROUP_INFO_REQUEST_TAG)) {

            requestInProcess_RequestTagAndListenerKey.put(SUBMIT_GROUP_INFO_REQUEST_TAG, listenerKey);
            requestStatus = REQUEST_STATUS_QUEUED;

            ArrayList content = new ArrayList();
            content.add(groupJid);
            content.add(groupTitle);
            content.add(groupImage);

            UserThreadTask userThreadTask = new UserThreadTask(context, SUBMIT_GROUP_INFO_REQUEST_TAG, content) {

                @Override
                public Object process() {
                    ArrayList content = (ArrayList) object;

                    String groupJid = (String) content.get(0);
                    String groupTitle = (String) content.get(1);
                    String groupImage = (String) content.get(2);

                    boolean success = PubSubMessaging.getInstance().updateGroupInfo(groupJid, groupTitle, groupImage, context);
                    return success;
                }

            };
            userThreadTask.start();
        } else {
            requestStatus = REQUEST_STATUS_ALREADY_EXIST;
        }
        return requestStatus;
    }

    public int submitGroupExit(Context context, String groupJid, String listenerKey) {
        int requestStatus = REQUEST_STATUS_FAILED;
        if (!requestInProcess_RequestTagAndListenerKey.containsKey(SUBMIT_GROUP_LEAVING_REQUEST_TAG)) {

            requestInProcess_RequestTagAndListenerKey.put(SUBMIT_GROUP_LEAVING_REQUEST_TAG, listenerKey);
            requestStatus = REQUEST_STATUS_QUEUED;

            UserThreadTask userThreadTask = new UserThreadTask(context, SUBMIT_GROUP_LEAVING_REQUEST_TAG, groupJid) {

                @Override
                public Object process() {
                    String groupJid = (String) object;
                    boolean success = sendLeavingGroup(context, groupJid);
                    return success;
                }

            };
            userThreadTask.start();
        } else {
            requestStatus = REQUEST_STATUS_ALREADY_EXIST;
        }
        return requestStatus;
    }

    private boolean sendLeavingGroup(Context context, String groupJid) {
        boolean success = false;
        String jsonContent = null;
        try {
            String password = TinyDB.getInstance(context).getString(TinyDB.KEY_PASSWORD);
            String userJID = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);
            String articleId = SportsUnityDBHelper.getInstance(context).getArticleIDThroughJid(groupJid);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userJID);
            jsonObject.put("password", password);
            jsonObject.put("apk_version", CommonUtil.getBuildConfig());
            jsonObject.put("udid", CommonUtil.getDeviceId(context));
            jsonObject.put("discussion_id", groupJid);
            jsonObject.put("article_id", articleId);

            jsonContent = jsonObject.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (jsonContent != null) {
            HttpURLConnection httpURLConnection = null;
            ByteArrayInputStream byteArrayInputStream = null;
            try {
                URL sendInterests = new URL(SUBMIT_LEAVING_GROUP);
                Log.d("max", "PollUrl>>" + sendInterests);
                httpURLConnection = (HttpURLConnection) sendInterests.openConnection();
                httpURLConnection.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
                httpURLConnection.setDoInput(false);
                httpURLConnection.setRequestMethod("POST");

                byteArrayInputStream = new ByteArrayInputStream(jsonContent.getBytes());
                OutputStream outputStream = httpURLConnection.getOutputStream();

                byte chunk[] = new byte[4096];
                int read = 0;
                while ((read = byteArrayInputStream.read(chunk)) != -1) {
                    outputStream.write(chunk, 0, read);
                }

                Log.d("max", "response code is" + httpURLConnection.getResponseCode() + "<<JsonContent>>" + jsonContent);
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

    public boolean submitUserFavorites(Context context) {
        boolean success = false;
        try {
            JSONArray interests = FavouriteItemWrapper.getInstance(context).getAllInterestsAsJsonArray();

            UserCard card = new UserCard();
            card.setInterest(interests);
            success = card.saveInterests(context);

            if (success) {
                UserUtil.setFavouriteVcardUpdated(context, true);
            } else {
                UserUtil.setFavouriteVcardUpdated(context, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return success;
    }

    public boolean submitUserCompleteProfile(Context context) {
        String jid = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);
        Contacts userContact = SportsUnityDBHelper.getInstance(context).getContactByJid(jid);

        boolean success = submitUserVCard(context, userContact);
        return success;
    }

    public Contacts getLoginUserDetail(Context context) {
        String jid = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);
        return SportsUnityDBHelper.getInstance(context).getContactByJid(jid);
    }

    private boolean submitUserVCard(Context context, Contacts userContact) {
        boolean success = false;
        try {
            UserCard card = new UserCard();
            card.setName(userContact.getName());
            card.setPic(userContact.image);
            card.setStatus(userContact.status);
            card.saveCard(context, true, true, false, false, true);

            success = true;
            saveLoginUserDetail(context, userContact);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    private String getUserInfoAsJSON(Context context, Contacts userContact) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jsonString;
    }

    private void saveLoginUserDetail(Context context, Contacts loginUserDetail) {
        int count = SportsUnityDBHelper.getInstance(context).updateContacts(loginUserDetail.phoneNumber, loginUserDetail.jid, loginUserDetail.getName(), loginUserDetail.image, loginUserDetail.status, Contacts.AVAILABLE_NOT);
        if (count == 0) {
            SportsUnityDBHelper.getInstance(context).addToContacts(loginUserDetail.getName(), loginUserDetail.phoneNumber, loginUserDetail.jid, loginUserDetail.status, loginUserDetail.image, Contacts.AVAILABLE_NOT, false);
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

                                            contentListener.handleContent(DOWNLOADING_FACEBOOK_IMAGE_TAG, null);

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
                parameters.putString("fields", "id,name,link,picture.width(720).height(720)");
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
        boolean success = false;
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (jsonContent != null) {
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
                while ((read = byteArrayInputStream.read(chunk)) != -1) {
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

    public static boolean uploadDisplayPic(Context context, String jid, String imageContent) {
        boolean success = false;
        if (imageContent == null) {
            success = true;
        } else {
            String jsonContent = null;
            try {
                String password = TinyDB.getInstance(context).getString(TinyDB.KEY_PASSWORD);
                String userJID = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", userJID);
                jsonObject.put("password", password);
                jsonObject.put("content", imageContent);
                jsonObject.put("jid", jid);
                jsonObject.put("apk_version", CommonUtil.getBuildConfig());
                jsonObject.put("udid", CommonUtil.getDeviceId(context));

                jsonContent = jsonObject.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (jsonContent != null) {
                HttpURLConnection httpURLConnection = null;
                ByteArrayInputStream byteArrayInputStream = null;
                try {
                    URL sendInterests = new URL(SET_DISPLAY_PIC);
                    httpURLConnection = (HttpURLConnection) sendInterests.openConnection();
                    httpURLConnection.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
                    httpURLConnection.setDoInput(false);
                    httpURLConnection.setRequestMethod("POST");

                    byteArrayInputStream = new ByteArrayInputStream(jsonContent.getBytes());
                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    byte chunk[] = new byte[4096];
                    int read = 0;
                    while ((read = byteArrayInputStream.read(chunk)) != -1) {
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
        }

        return success;
    }

    /*
     * return value
     * NULL means some issue with downloading,
     * EMPTY STRING means no image uploaded for this.
     */
    public static String downloadDisplayPic(Context context, String jid, String imageVersion) {
        String imageContent = null;
        String requestJsonContent = null;
        try {
            String password = TinyDB.getInstance(context).getString(TinyDB.KEY_PASSWORD);
            String userJID = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userJID);
            jsonObject.put("password", password);
            jsonObject.put("jid", jid);
            jsonObject.put("apk_version", CommonUtil.getBuildConfig());
            jsonObject.put("udid", CommonUtil.getDeviceId(context));
            jsonObject.put("version", imageVersion);

            requestJsonContent = jsonObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (requestJsonContent != null) {
            HttpURLConnection httpURLConnection = null;
            ByteArrayOutputStream byteArrayOutputStream = null;
            ByteArrayInputStream byteArrayInputStream = null;
            try {
                URL sendInterests = new URL(GET_DISPLAY_PIC);
                httpURLConnection = (HttpURLConnection) sendInterests.openConnection();
                httpURLConnection.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
                httpURLConnection.setReadTimeout(60000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                {
                    byteArrayInputStream = new ByteArrayInputStream(requestJsonContent.getBytes());
                    OutputStream outputStream = httpURLConnection.getOutputStream();

                    byte chunk[] = new byte[4096];
                    int read = 0;
                    while ((read = byteArrayInputStream.read(chunk)) != -1) {
                        outputStream.write(chunk, 0, read);
                    }
                }

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        InputStream inputStream = httpURLConnection.getInputStream();

                        byte chunk[] = new byte[4096];
                        int read = 0;
                        while ((read = inputStream.read(chunk)) != -1) {
                            byteArrayOutputStream.write(chunk, 0, read);
                        }
                    }

                    String content = String.valueOf(byteArrayOutputStream.toString());
                    JSONObject responseJsonContent = new JSONObject(content);
                    if (responseJsonContent.getInt("status") == 200) {
                        if (responseJsonContent.has("content")) {
                            imageContent = responseJsonContent.getString("content");
                        } else {
                            imageContent = "";
                        }
                    }
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
        return imageContent;
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

        public void handleContent(String requestTag, Object content);

    }

}