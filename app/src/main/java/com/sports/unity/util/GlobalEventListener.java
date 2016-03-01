package com.sports.unity.util;

/**
 * Created by amandeep on 12/1/16.
 */
public interface GlobalEventListener {

    public void onInternetStateChanged(boolean connected);

    public void onXMPPServiceAuthenticated(boolean connected);

}
