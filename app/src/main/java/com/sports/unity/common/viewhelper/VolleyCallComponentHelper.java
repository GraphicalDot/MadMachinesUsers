package com.sports.unity.common.viewhelper;

import android.util.Log;

import com.sports.unity.scores.model.ScoresContentHandler;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by amandeep on 26/4/16.
 */
public class VolleyCallComponentHelper {

    public static int REQUEST_STATUS_NONE = 0;
    public static int REQUEST_STATUS_FAILED = 1;
    public static int REQUEST_STATUS_SUCCESS = 2;

    private String requestListenerKey = "CustomComponentListener";

    private CustomComponentListener customComponentListener = null;

    private CustomContentListener contentListener = new CustomContentListener();

    private HashMap<String, CustomComponentListener> customComponentListenerHashMap = new HashMap<>();

    public VolleyCallComponentHelper(String requestListenerKey, CustomComponentListener customComponentListener){
        this.requestListenerKey = requestListenerKey;
        this.customComponentListener = customComponentListener;
    }

    public void onComponentCreate() {
        ArrayList<CustomComponentListener> customComponentListeners = new ArrayList<>();
        customComponentListeners.add(customComponentListener);
        initListeners(customComponentListeners);

        addCustomContentListener();
    }

    public void onComponentResume() {
        Iterator<String> keys = customComponentListenerHashMap.keySet().iterator();
        String key = null;
        CustomComponentListener customComponentListener = null;
        while( keys.hasNext() ){
            key = keys.next();
            customComponentListener = customComponentListenerHashMap.get(key);
            customComponentListener.setComponentPaused(false);

            if(ScoresContentHandler.getInstance().isRequestInProcess(customComponentListener.getRequestTag())){
                customComponentListener.hideErrorLayout();
                customComponentListener.showProgress();
            } else {
                if( customComponentListener.getRequestStatus() == REQUEST_STATUS_NONE ) {
                    //nothing
                } else if( customComponentListener.getRequestStatus() == REQUEST_STATUS_SUCCESS ) {
                    //nothing
                } else if( customComponentListener.getRequestStatus() == REQUEST_STATUS_FAILED ) {
                    customComponentListener.showErrorLayout();
                }
            }
        }
        addCustomContentListener();
    }

    public void onComponentPause() {
        Iterator<String> keys = customComponentListenerHashMap.keySet().iterator();
        String key = null;
        CustomComponentListener customComponentListener = null;
        while( keys.hasNext() ){
            key = keys.next();
            customComponentListener = customComponentListenerHashMap.get(key);
            customComponentListener.setComponentPaused(true);

            customComponentListener.hideProgress();
            customComponentListener.hideErrorLayout();
        }
        removeCustomContentListener();
    }

    public void requestContent(String callName, HashMap<String,String> parameters, String requestTag) {
        Log.i("Custom Component Volley", "Request Call : request tag " + requestTag);

        CustomComponentListener customComponentListener = customComponentListenerHashMap.get(requestTag);
        if( customComponentListener != null ) {
            customComponentListener.hideErrorLayout();
            customComponentListener.showProgress();

            ScoresContentHandler.getInstance().requestCall(callName, parameters, requestListenerKey, requestTag);
        } else {
            //nothing
        }
    }

    private void initListeners(ArrayList<CustomComponentListener> customComponentListeners){
        for(CustomComponentListener customComponentListener : customComponentListeners) {
            customComponentListenerHashMap.put(customComponentListener.getRequestTag(), customComponentListener);
        }
    }

    private void addCustomContentListener(){
        ScoresContentHandler.getInstance().addResponseListener(contentListener, requestListenerKey);
    }

    private void removeCustomContentListener(){
        ScoresContentHandler.getInstance().removeResponseListener(requestListenerKey);
    }

    private void handleResponse(String requestTag, String content, int responseCode){
        CustomComponentListener customComponentListener = customComponentListenerHashMap.get(requestTag);
        if( customComponentListener != null ) {
            if( responseCode == HttpURLConnection.HTTP_OK ) {
                boolean success = customComponentListener.handleContent(requestTag, content);
                if( success ){
                    customComponentListener.setRequestStatus(REQUEST_STATUS_SUCCESS);
                    if( ! customComponentListener.isComponentPaused() ){
                        customComponentListener.changeUI(requestTag);
                    }
                } else {
                    customComponentListener.setRequestStatus(REQUEST_STATUS_FAILED);
                    if( ! customComponentListener.isComponentPaused() ) {
                        customComponentListener.showErrorLayout();
                    }
                }
            } else {
                customComponentListener.handleErrorContent(requestTag);
                customComponentListener.setRequestStatus(REQUEST_STATUS_FAILED);

                if( ! customComponentListener.isComponentPaused() ) {
                    customComponentListener.showErrorLayout();
                }
            }
            if( ! customComponentListener.isComponentPaused() ) {
                customComponentListener.hideProgress();
            }
        }
    }

    private class CustomContentListener implements ScoresContentHandler.ContentListener {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            handleResponse(tag, content,responseCode);
        }

    }

}
