package com.sports.unity.util;

import android.content.Context;
import android.util.Base64;

import com.sports.unity.BuildConfig;
import com.sports.unity.common.model.TinyDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by amandeep on 17/6/16.
 */
public class UserCard {

    private static final String GET_USER_INFO_URL = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/get_user_info?";
    private static final String SET_USER_INFO_URL = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/set_user_info?";
    private static final String SET_USER_INTERESTS_URL = "http://" + BuildConfig.XMPP_SERVER_API_BASE_URL + "/v1/set_user_interests?";

    private static final String KEY_NAME = "N";
    private static final String KEY_STATUS = "S";
    private static final String KEY_INTEREST = "I";
    private static final String KEY_THUMBNAIL = "T";
    private static final String KEY_PIC = "P";

    private HashMap<String, Object> valuesMap = new HashMap<>();

    public UserCard(){

    }

    public boolean loadInterests(Context context, String userJid){
        boolean success = false;
        ArrayList<String> list = new ArrayList<>();
        list.add("interests");

        String jsonContent = createRequestJsonContentToLoadCard( context, userJid, list);
        success = loadUserInfo(jsonContent);
        return success;
    }

    public boolean loadCard(Context context, String userJid, boolean loadName, boolean loadStatus, boolean loadInterests, boolean loadThumbnail, boolean loadPic){
        boolean success = false;
        ArrayList<String> list = new ArrayList<>();
        if( loadName ) {
            list.add("name");
        }
        if( loadStatus ) {
            list.add("status");
        }
        if( loadInterests ) {
            list.add("interests");
        }
        if( loadThumbnail ) {
            list.add("s_photo");
        }
        if( loadPic ) {
            list.add("l_photo");
        }

        String jsonContent = createRequestJsonContentToLoadCard( context, userJid, list);
        success = loadUserInfo(jsonContent);
        return success;
    }

    public boolean saveCard(Context context, boolean saveName, boolean saveStatus, boolean saveInterest, boolean saveThumbnail, boolean savePic){
        boolean success = false;
        HashSet<String> keysToUpdate = new HashSet<>();
        if( saveName ){
            keysToUpdate.add(KEY_NAME);
        }
        if( saveStatus ){
            keysToUpdate.add(KEY_STATUS);
        }
        if( saveInterest ){
            keysToUpdate.add(KEY_INTEREST);
        }
        if( saveThumbnail ){
            keysToUpdate.add(KEY_THUMBNAIL);
        }
        if( savePic ){
            keysToUpdate.add(KEY_PIC);
        }

        String jsonContent = createRequestJsonContentToSubmitCard( context, keysToUpdate);
        success = submitUserInfo(jsonContent);
        return success;
    }

    public boolean saveInterests(Context context){
        boolean success = false;
        HashSet<String> keysToUpdate = new HashSet<>();
        keysToUpdate.add(KEY_INTEREST);

        String jsonContent = createRequestJsonContentToSubmitCard( context, keysToUpdate);
        success = submitUserInterests(jsonContent);
        return success;
    }

    public void setName(String name){
        valuesMap.put(KEY_NAME, name);
    }

    public String getName(){
        return (String)valuesMap.get(KEY_NAME);
    }

    public void setStatus(String status){
        valuesMap.put(KEY_STATUS, status);
    }

    public String getStatus(){
        return (String)valuesMap.get(KEY_STATUS);
    }

    public void setInterest(JSONArray interest){
        valuesMap.put(KEY_INTEREST, interest);
    }

    public JSONArray getInterest(){
        return (JSONArray)valuesMap.get(KEY_INTEREST);
    }

//    public void setThumbnail(byte[] thumbnail){
//        valuesMap.put(KEY_THUMBNAIL, thumbnail);
//    }

    public byte[] getThumbnail(){
        return (byte[])valuesMap.get(KEY_THUMBNAIL);
    }

    public void setPic(byte[] pic){
        valuesMap.put(KEY_PIC, pic);
    }

    public byte[] getPic(){
        return (byte[])valuesMap.get(KEY_PIC);
    }

    private String createRequestJsonContentToLoadCard(Context context, String requestingUserJid, ArrayList<String> requestList){
        String jsonString = null;
        try {
            TinyDB tinyDB = TinyDB.getInstance(context);
            String password = tinyDB.getString(TinyDB.KEY_PASSWORD);
            String userJid = tinyDB.getString(TinyDB.KEY_USER_JID);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userJid);
            jsonObject.put("password", password);
            jsonObject.put("apk_version", CommonUtil.getBuildConfig());
            jsonObject.put("udid", CommonUtil.getDeviceId(context));

            jsonObject.put("r_jid", requestingUserJid);
            JSONArray requestingItems = new JSONArray();
            for(String key : requestList){
                requestingItems.put(key);
            }
            jsonObject.put("r_info", requestingItems);

            jsonString = jsonObject.toString();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return jsonString;
    }

    private String createRequestJsonContentToSubmitCard(Context context, HashSet<String> keysToUpdate){
        String jsonString = null;
        try {
            TinyDB tinyDB = TinyDB.getInstance(context);
            String password = tinyDB.getString(TinyDB.KEY_PASSWORD);
            String userJid = tinyDB.getString(TinyDB.KEY_USER_JID);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userJid);
            jsonObject.put("password", password);
            jsonObject.put("apk_version", CommonUtil.getBuildConfig());
            jsonObject.put("udid", CommonUtil.getDeviceId(context));

            if( keysToUpdate.contains(KEY_NAME) ){
                jsonObject.put("name", valuesMap.get(KEY_NAME));
            }
            if( keysToUpdate.contains(KEY_STATUS) ){
                jsonObject.put("status", valuesMap.get(KEY_STATUS));
            }
            if( keysToUpdate.contains(KEY_INTEREST) ){
                jsonObject.put("interests", valuesMap.get(KEY_INTEREST));
            }
//            if( keysToUpdate.contains(KEY_THUMBNAIL) ){
//                byte[] content = getThumbnail();
//                if( content != null ) {
//                    jsonObject.put("photo", Base64.encodeToString( content, Base64.DEFAULT));
//                }
//            }
            if( keysToUpdate.contains(KEY_PIC) ){
                byte[] content = getPic();
                if( content != null ) {
                    jsonObject.put("photo", Base64.encodeToString( content, Base64.DEFAULT));
                }
            }

            jsonString = jsonObject.toString();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return jsonString;
    }

    private void handleJsonResponse(String response) throws Exception {
        JSONObject jsonObject = new JSONObject(response);
        if( jsonObject.getInt("status") == 200 ){
            jsonObject = (JSONObject) jsonObject.get("user_info");
            if( jsonObject.has("name") ){
                valuesMap.put(KEY_NAME, jsonObject.getString("name"));
            }
            if( jsonObject.has("status") ){
                valuesMap.put(KEY_STATUS, jsonObject.getString("status"));
            }
            if( jsonObject.has("interests") ){
                valuesMap.put(KEY_INTEREST, jsonObject.getJSONArray("interests"));
            }
            if( jsonObject.has("s_photo") ){
                String content = jsonObject.getString("s_photo");
                if( content != null && content.length() > 0 ) {
                    valuesMap.put(KEY_THUMBNAIL, Base64.decode( content, Base64.DEFAULT));
                }
            }
            if( jsonObject.has("l_photo") ){
                valuesMap.put(KEY_PIC, jsonObject.getString("l_photo"));
            }
        } else {

        }
    }

    private boolean submitUserInfo(String jsonContent){
        return makeSubmitCall(jsonContent, SET_USER_INFO_URL);
    }

    private boolean submitUserInterests(String jsonContent){
        return makeSubmitCall(jsonContent, SET_USER_INTERESTS_URL);
    }

    private boolean makeSubmitCall(String jsonContent, String url){
        boolean success = false;
        HttpURLConnection httpURLConnection = null;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            URL sendInterests = new URL(url);
            httpURLConnection = (HttpURLConnection) sendInterests.openConnection();
            httpURLConnection.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
            httpURLConnection.setDoInput(false);
            httpURLConnection.setDoOutput(true);
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

    private boolean loadUserInfo(String jsonContent){
        boolean success = false;
        HttpURLConnection httpURLConnection = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            URL sendInterests = new URL(GET_USER_INFO_URL);
            httpURLConnection = (HttpURLConnection) sendInterests.openConnection();
            httpURLConnection.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
            httpURLConnection.setReadTimeout(Constants.CONNECTION_READ_TIME_OUT);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
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

                InputStream is = httpURLConnection.getInputStream();
                byteArrayOutputStream = new ByteArrayOutputStream();
                read = 0;
                while ((read = is.read(chunk)) != -1) {
                    byteArrayOutputStream.write(chunk, 0, read);
                }

                String response = new String(byteArrayOutputStream.toByteArray());
                handleJsonResponse(response);
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

}
