package com.sports.unity.common.view;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.scores.model.ScoresContentHandler;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by amandeep on 21/1/16.
 */
public class CustomVolleyCallerActivity extends CustomAppCompatActivity {

    public static int REQUEST_STATUS_NONE = 0;
    public static int REQUEST_STATUS_FAILED = 1;
    public static int REQUEST_STATUS_SUCCESS = 2;

    private String requestListenerKey = "CustomComponentListener";
    private CustomContentListener contentListener = new CustomContentListener();

    private HashMap<String, CustomComponentListener> customComponentListenerHashMap = new HashMap<>();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onComponentCreate(ArrayList<CustomComponentListener> customComponentListeners, String requestListenerKey) {
        this.requestListenerKey = requestListenerKey;
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
                if( customComponentListener.requestStatus == REQUEST_STATUS_NONE ) {
                    //nothing
                } else if( customComponentListener.requestStatus == REQUEST_STATUS_SUCCESS ) {
                   //nothing
                } else if( customComponentListener.requestStatus == REQUEST_STATUS_FAILED ) {
                    customComponentListener.showErrorLayout();
                }
            }
        }
        addCustomContentListener();
    }

//    public void onComponentResume(Fragment fragment, ArrayList<CustomComponentListener> customComponentListeners, String requestListenerKey) {
//        this.fragment = fragment;
//        this.requestListenerKey = requestListenerKey;
//        initListeners(customComponentListeners);
//
//        addCustomContentListener();
//    }

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
//        customComponentListenerHashMap.clear();

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

//    private void disableAutoRefreshContent(){
//        if( timerToRefreshContent != null ){
//            timerToRefreshContent.cancel();
//            timerToRefreshContent = null;
//        }
//    }

//    private void enableAutoRefreshContent(){
//        disableAutoRefreshContent();
//
//        timerToRefreshContent = new Timer();
//        timerToRefreshContent.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                autoRefreshCall();
//            }
//
//        }, 60000, 60000);
//    }

//    private void autoRefreshCall(){
//        requestMatchScoreDetails();
//    }

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
                    customComponentListener.requestStatus = REQUEST_STATUS_SUCCESS;
                    if( ! customComponentListener.isComponentPaused() ){
                        customComponentListener.changeUI();
                    }
                } else {
                    customComponentListener.requestStatus = REQUEST_STATUS_FAILED;
                    if( ! customComponentListener.isComponentPaused() ) {
                        customComponentListener.showErrorLayout();
                    }
                }
            } else {
                customComponentListener.handleErrorContent(requestTag);
                customComponentListener.requestStatus = REQUEST_STATUS_FAILED;

                if( ! customComponentListener.isComponentPaused() ) {
                    customComponentListener.showErrorLayout();
                }
            }
            if( ! customComponentListener.isComponentPaused() ) {
                customComponentListener.hideProgress();
            }
        } else {
            //nothing
        }
    }

    public abstract class CustomComponentListener {

        private ProgressBar progressBar = null;
        private ViewGroup errorLayout = null;

        private String requestTag = null;
        private boolean componentPaused = false;

        private int requestStatus = REQUEST_STATUS_NONE;

        public CustomComponentListener(String requestTag, ProgressBar progressBar, ViewGroup errorLayout){
            this.progressBar = progressBar;
            this.errorLayout = errorLayout;

            this.requestTag = requestTag;

            initErrorLayout();
            initProgress();
        }

        public String getRequestTag() {
            return requestTag;
        }

        public boolean isComponentPaused() {
            return componentPaused;
        }

        private void setComponentPaused(boolean componentPaused) {
            this.componentPaused = componentPaused;
        }

        protected void initErrorLayout(){
            if( errorLayout != null ) {
                TextView oops = (TextView) errorLayout.findViewById(R.id.oops);
                oops.setTypeface(FontTypeface.getInstance(CustomVolleyCallerActivity.this).getRobotoLight());

                TextView something_wrong = (TextView) errorLayout.findViewById(R.id.something_wrong);
                something_wrong.setTypeface(FontTypeface.getInstance(CustomVolleyCallerActivity.this).getRobotoLight());
            }
        }

        protected void initProgress(){
            if(progressBar != null) {
                progressBar.getIndeterminateDrawable().setColorFilter(progressBar.getResources().getColor(R.color.app_theme_blue), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }

        protected void showErrorLayout(){
            requestStatus = REQUEST_STATUS_FAILED;
            if( errorLayout != null  ) {
                errorLayout.setVisibility(View.VISIBLE);
            }
        }

        protected void hideErrorLayout(){
            if( errorLayout != null  ) {
                errorLayout.setVisibility(View.GONE);
            }
        }

        protected void showProgress(){
            if( progressBar != null ) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        protected void hideProgress(){
            if( progressBar != null ) {
                progressBar.setVisibility(View.GONE);
            }
        }

        abstract public boolean handleContent(String tag, String content);
        abstract public void handleErrorContent(String tag);

        abstract public void changeUI();

    }

    private class CustomContentListener implements ScoresContentHandler.ContentListener {

        @Override
        public void handleContent(String tag, String content, int responseCode) {
            handleResponse(tag, content,responseCode);
        }

    }

}
