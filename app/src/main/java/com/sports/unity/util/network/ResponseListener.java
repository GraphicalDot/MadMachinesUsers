package com.sports.unity.util.network;

/**
 * Created by amandeep on 20/1/16.
 */
public interface ResponseListener {

    public void response(int responseCode, byte[] content);

}
