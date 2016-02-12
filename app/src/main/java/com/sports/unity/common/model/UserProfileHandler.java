package com.sports.unity.common.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.messages.controller.model.Contacts;
import com.sports.unity.util.ImageUtil;
import com.sports.unity.util.ThreadTask;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mad on 2/12/2016.
 * Helper class to handle the events related to user profile.
 */
public class UserProfileHandler {
    private static UserProfileHandler USER_PROFILE_HANDLER;

    public static UserProfileHandler getInstance() {
        if (USER_PROFILE_HANDLER == null) {
            USER_PROFILE_HANDLER = new UserProfileHandler();
        }
        return USER_PROFILE_HANDLER;
    }

    private HashMap<String,ContentListener> contentListenerHashMap = new HashMap<>();

    private UserProfileHandler() {
        USER_PROFILE_HANDLER = new UserProfileHandler();
    }

    public void addContentListener(String key, ContentListener contentListener){
        contentListenerHashMap.put(key, contentListener);
    }

    public void removeContentListener(String key){
        contentListenerHashMap.remove(key);
    }

    public boolean loadMyProfile(String listenerKey){
        String jid = null; //TODO
        return loadProfile(jid, listenerKey);
    }

    public boolean loadProfile(String jid, String listenerKey){
        boolean loading = false;
        if( XMPPClient.getInstance().isConnectionAuthenticated() ){
            loading = true;

            UserThreadTask userThreadTask = new UserThreadTask( listenerKey, jid) {

                @Override
                public Object process() {
                    VCard card = new VCard();
                    try {
                        card.load(XMPPClient.getConnection(), (String)object);
                    } catch (Exception e) {
                        e.printStackTrace();
                        card = null;
                    }
                    return card;
                }

            };
            userThreadTask.start();
        } else {
            //nothing
        }

        return loading;
    }

    public boolean submitUserProfile(Contacts contacts, String listenerKey){
        boolean submitting = false;
        if( XMPPClient.getInstance().isConnectionAuthenticated() ){
            submitting = true;

            UserThreadTask userThreadTask = new UserThreadTask( listenerKey, contacts) {

                @Override
                public Object process() {
                    Boolean success = false;
                    try {
                        Contacts userContact = (Contacts)object;

                        VCardManager manager = VCardManager.getInstanceFor(XMPPClient.getConnection());
                        VCard vCard = new VCard();
                        vCard.setNickName(userContact.name);
                        vCard.setAvatar(userContact.image);
                        vCard.setMiddleName(userContact.status);
                        vCard.setJabberId(XMPPClient.getConnection().getUser());
                        manager.saveVCard(vCard);

                        success = true;
                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    return success;
                }

            };
            userThreadTask.start();
        } else {
            //nothing
        }

        return submitting;
    }

    private void fetchMyProfileFromDB(String listenerKey){
        UserThreadTask userThreadTask = new UserThreadTask( listenerKey, null) {

            @Override
            public Object process() {
                //TODO fetch content from db.
                return null;
            }

        };
        userThreadTask.start();
    }

    private void saveMyProfileInDB(String listenerKey, Contacts contacts){
        UserThreadTask userThreadTask = new UserThreadTask( listenerKey, contacts) {

            @Override
            public Object process() {
                Contacts userContact = (Contacts)object;
                //TODO save user profile in db.
                return null;
            }

        };
        userThreadTask.start();
    }

    public void initFacebookLogin(Context context) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo("com.sports.unity", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void setFacebookDetails(final Context context, LoginButton loginButton, final String listenerKey) {
        CallbackManager callbackManager = CallbackManager.Factory.create();
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
                                    contentListener.handleContent(object);
                                } else {
                                    //nothing
                                }
//                                try {
//                                    userNameTextView.setText(object.getString("name"));
//                                    TinyDB.getInstance(context.getApplicationContext()).putString((String) object.get("name"), TinyDB.KEY_PROFILE_NAME);
//                                    JSONObject data = response.getJSONObject();
//                                    if (data.has("picture")) {
//                                        final String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
//                                        downloadUserImageFromUri(profileImageView, profilePicUrl);
//                                        Log.i("PICURL : ", profilePicUrl);
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
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

    public void downloadImageFromUri(String imageUri, String listenerKey) {
        UserThreadTask userThreadTask = new UserThreadTask(listenerKey, imageUri){

            @Override
            public Object process() {
                String urlDisplay = (String)object;
                Bitmap bitmap = null;
                InputStream in = null;
                try {
                    in = new java.net.URL(urlDisplay).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try{
                        in.close();
                    }catch (Exception ex){}
                }
                return bitmap;
            }

        };
        userThreadTask.start();
    }

    private abstract class UserThreadTask extends ThreadTask {

        private String listenerKey = null;

        public UserThreadTask(String listenerKey, Object helperObject){
            super(helperObject);
            this.listenerKey = listenerKey;
        }

        @Override
        abstract public Object process();

        @Override
        public void postAction(Object content) {
            ContentListener contentListener = contentListenerHashMap.get(listenerKey);
            if( contentListener != null ){
                contentListener.handleContent( content);
            } else {
                //nothing
            }
        }
    }

    public interface ContentListener {

        public void handleContent(Object content);

    }

}