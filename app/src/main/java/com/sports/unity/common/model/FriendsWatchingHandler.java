package com.sports.unity.common.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sports.unity.BuildConfig;
import com.sports.unity.scores.model.ScoresContentHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.ThreadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A utility class to
 * get the number of friends who are
 * watching a particular live match.
 * Created by Mad on 26-May-16.
 */
public class FriendsWatchingHandler {

    public static Context sContext;

    private Boolean mSuccess = false;
    private ArrayList<String> mMatchId = new ArrayList<String>();

    public static FriendsWatchingHandler FRIENDS_WATCHING = null;

    private HashMap<String, JSONArray> mMatchMap = new HashMap<>();

    private HashMap<String, String> mRequestQue = new HashMap<>();

    private HashMap<String, FriendsContentListener> mListenerQue = new HashMap<>();

    public static final String GET_FRIENDS_WATCHING = "http://" + BuildConfig.XMPP_SERVER_BASE_URL + "/friends_watching?";

    public static FriendsWatchingHandler getInstance(Context context) {
        if (FRIENDS_WATCHING == null) {
            FRIENDS_WATCHING = new FriendsWatchingHandler(context);
        }
        FRIENDS_WATCHING.sContext = context;
        return FRIENDS_WATCHING;
    }

    private FriendsWatchingHandler(Context context) {

    }

    public void addFriendsContentListener(FriendsContentListener contentListener, String listenerKey) {
        mListenerQue.put(listenerKey, contentListener);
    }

    public void removeFriendsContentListener(String listenerKey) {
        mListenerQue.remove(listenerKey);
    }

    /**
     * Get ID's of the live matches.
     *
     * @return {@link ArrayList} of ID's.
     */
    public ArrayList getMatchIds() {
        return mMatchId;
    }

    /**
     * Set the ID's of live matches.
     *
     * @param matchId {@link ArrayList} of ID's.
     */
    public void setMatchId(ArrayList<String> matchId) {
        mMatchId.clear();
        mMatchId.addAll(matchId);
    }

    public void addMatch(String matchId) {
        if (!mMatchId.contains(matchId)) {
            mMatchId.add(matchId);
        }
    }

    public void requestContent(final String listenerKey, final String requestTag) {
        if (!mRequestQue.containsKey(requestTag) && CommonUtil.isInternetConnectionAvailable(sContext)) {
            mRequestQue.put(requestTag, listenerKey);
            ThreadTask getFriendsWatchingTask = new ThreadTask(null) {
                @Override
                public Object process() {
                    boolean success = false;
                    success = getFriendsWatching(sContext);
                    return success;
                }

                @Override
                public void postAction(Object object) {
                    mSuccess = (boolean) object;
                    mRequestQue.remove(requestTag);

                    if (mSuccess && mListenerQue.containsKey(listenerKey)) {
                        FriendsContentListener contentListener = mListenerQue.get(listenerKey);
                        contentListener.handleFriendsContent();
                    }
                }
            };
            getFriendsWatchingTask.start();
        }
    }

    private boolean getFriendsWatching(Context context) {
        boolean success = false;
        String jsonContent = null;
        String response = "";
        try {
            JSONArray interests = getMatchIdAsJson();

            String password = TinyDB.getInstance(context).getString(TinyDB.KEY_PASSWORD);
            String userJID = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", userJID);
            jsonObject.put("password", password);
            jsonObject.put("apk_version", CommonUtil.getBuildConfig());
            jsonObject.put("udid", CommonUtil.getDeviceId(context));
            jsonObject.put("matches", interests);

            jsonContent = jsonObject.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (jsonContent != null) {
            HttpURLConnection httpURLConnection = null;
            ByteArrayInputStream byteArrayInputStream = null;
            try {
                URL sendInterests = new URL(GET_FRIENDS_WATCHING);
                httpURLConnection = (HttpURLConnection) sendInterests.openConnection();
                httpURLConnection.setConnectTimeout(Constants.CONNECTION_TIME_OUT);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");

                byteArrayInputStream = new ByteArrayInputStream(jsonContent.getBytes());
                OutputStream outputStream = httpURLConnection.getOutputStream();

                byte chunk[] = new byte[4096];
                int read = 0;
                while ((read = byteArrayInputStream.read(chunk)) != -1) {
                    outputStream.write(chunk, 0, read);
                }

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    JSONObject object = new JSONObject(response);
                    int status = 0;
                    if (!object.isNull("status")) {
                        status = object.getInt("status");
                    }
                    if (status == 200) {
                        success = true;
                        JSONObject match = object.getJSONObject("matches");
                        mMatchMap.clear();
                        for (String m : mMatchId) {
                            if (!match.isNull(m)) {
                                mMatchMap.put(m, match.getJSONArray(m));
                            }
                        }

                    }
                    br.close();
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

    private JSONArray getMatchIdAsJson() {
        JSONArray matchJson = new JSONArray();
        if (mMatchId.size() > 0) {
            for (String id : mMatchId) {
                matchJson.put(id);
            }
        }
        return matchJson;
    }

    public int getNoOfFriends(String matchId) {
        int noOfFriends = 0;
        if (mMatchMap.containsKey(matchId)) {
            JSONArray friendsJArray = mMatchMap.get(matchId);
            noOfFriends = friendsJArray.length();
        }
        return noOfFriends;
    }

    public boolean isMatchExist(String matchId) {
        boolean isExist = false;
        if (mMatchMap.containsKey(matchId)) {
            isExist = true;
        }

        return isExist;
    }

    public interface FriendsContentListener {
        void handleFriendsContent();
    }
}
