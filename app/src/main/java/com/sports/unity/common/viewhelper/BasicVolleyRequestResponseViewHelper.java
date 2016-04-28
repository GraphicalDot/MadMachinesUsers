package com.sports.unity.common.viewhelper;

import android.view.View;

/**
 * Created by amandeep on 26/4/16.
 */
public abstract class BasicVolleyRequestResponseViewHelper {

    public BasicVolleyRequestResponseViewHelper(){

    }

    public VolleyCallComponentHelper getVolleyCallComponentHelper(View view) {
        VolleyCallComponentHelper volleyCallComponentHelper = new VolleyCallComponentHelper( getRequestListenerKey(), getCustomComponentListener(view));
        return volleyCallComponentHelper;
    }

    public GenericVolleyRequestResponseFragment getFragment(){
        GenericVolleyRequestResponseFragment fragment = new GenericVolleyRequestResponseFragment();
        return fragment;
    }

    abstract public String getFragmentTitle();

    abstract public String getRequestListenerKey();

    abstract public CustomComponentListener getCustomComponentListener(View view);

}
