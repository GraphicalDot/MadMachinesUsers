package com.sports.unity.common.viewhelper;

import android.view.View;

import java.util.HashMap;

/**
 * Created by amandeep on 26/4/16.
 */
public abstract class BasicVolleyRequestResponseViewHelper {

    private VolleyCallComponentHelper volleyCallComponentHelper = null;

    public BasicVolleyRequestResponseViewHelper(){

    }

    public VolleyCallComponentHelper getVolleyCallComponentHelper(View view) {
        if( volleyCallComponentHelper == null ) {
            volleyCallComponentHelper = new VolleyCallComponentHelper(getRequestListenerKey(), getCustomComponentListener(view));
        }
        return volleyCallComponentHelper;
    }

    public GenericVolleyRequestResponseFragment getFragment(){
        GenericVolleyRequestResponseFragment fragment = new GenericVolleyRequestResponseFragment();
        return fragment;
    }

    public void requestContent() {
        if( volleyCallComponentHelper != null ) {
            volleyCallComponentHelper.requestContent( getRequestCallName(), getRequestParameters(), getRequestTag());
        }
    }

    abstract public int getFragmentLayout();

    abstract public String getFragmentTitle();

    abstract public String getRequestListenerKey();

    abstract public CustomComponentListener getCustomComponentListener(View view);

    abstract public String getRequestTag();

    abstract public String getRequestCallName();

    abstract public HashMap<String,String> getRequestParameters();

    abstract public void initialiseViews(View view);

}
