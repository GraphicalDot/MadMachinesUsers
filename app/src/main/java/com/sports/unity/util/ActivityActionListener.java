package com.sports.unity.util;

/**
 * Created by madmachines on 29/10/15.
 */
public interface ActivityActionListener {

    public void handleAction(int id, Object object);

    public void handleAction(int id);

    public void handleMediaContent(String mimeType, Object content);

}
