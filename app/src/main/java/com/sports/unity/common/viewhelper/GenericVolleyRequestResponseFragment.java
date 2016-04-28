package com.sports.unity.common.viewhelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amandeep on 26/4/16.
 */
public class GenericVolleyRequestResponseFragment extends Fragment {

    private BasicVolleyRequestResponseViewHelper basicVolleyRequestResponseViewHelper = null;
    private VolleyCallComponentHelper volleyCallComponentHelper = null;

    public GenericVolleyRequestResponseFragment(){

    }

    public void setBasicVolleyRequestResponseViewHelper(BasicVolleyRequestResponseViewHelper basicVolleyRequestResponseViewHelper) {
        this.basicVolleyRequestResponseViewHelper = basicVolleyRequestResponseViewHelper;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        onComponentCreate(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        onComponentResume();
    }


    @Override
    public void onPause() {
        super.onPause();

        onComponentPause();
    }

    public void onComponentCreate(View view) {
        if( volleyCallComponentHelper == null ) {
            volleyCallComponentHelper = basicVolleyRequestResponseViewHelper.getVolleyCallComponentHelper(view);
            volleyCallComponentHelper.onComponentCreate();
            volleyCallComponentHelper.requestContent( basicVolleyRequestResponseViewHelper.getRequestCallName(), basicVolleyRequestResponseViewHelper.getRequestParameters(), basicVolleyRequestResponseViewHelper.getRequestTag());
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

}
