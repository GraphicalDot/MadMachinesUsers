package com.sports.unity.common.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sports.unity.R;
import com.sports.unity.common.controller.CustomAppCompatActivity;
import com.sports.unity.common.model.FontTypeface;
import com.sports.unity.common.viewhelper.CustomComponentListener;
import com.sports.unity.common.viewhelper.VolleyCallComponentHelper;
import com.sports.unity.scores.model.ScoresContentHandler;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by amandeep on 21/1/16.
 */
public abstract class CustomVolleyCallerActivity extends CustomAppCompatActivity {

    private VolleyCallComponentHelper volleyCallComponentHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onComponentResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        onComponentPause();
    }

    public void onComponentCreate() {
        if( volleyCallComponentHelper == null ) {
            volleyCallComponentHelper = getVolleyCallComponentHelper();
            volleyCallComponentHelper.onComponentCreate();
        }
    }

    public void onComponentResume() {
        if( volleyCallComponentHelper != null ) {
            volleyCallComponentHelper.onComponentResume();
        }
    }

    public void onComponentPause() {
        if( volleyCallComponentHelper != null ) {
            volleyCallComponentHelper.onComponentPause();
        }
    }

    public void requestContent(String callName, HashMap<String,String> parameters, String requestTag) {
        if( volleyCallComponentHelper != null ) {
            volleyCallComponentHelper.requestContent(callName, parameters, requestTag);
        }
    }

    public abstract VolleyCallComponentHelper getVolleyCallComponentHelper();

}
