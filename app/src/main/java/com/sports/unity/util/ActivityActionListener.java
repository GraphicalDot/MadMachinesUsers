package com.sports.unity.util;

/**
 * Created by madmachines on 29/10/15.
 */
public interface ActivityActionListener {

    public void handleAction(int id, Object object);

    public void handleAction(int id);

    public void handleMediaContent(int id, String mimeType, Object messageContent, Object mediaContent);

    public void handleMediaContent(int id, String mimeType, Object messageContent, String thumbnailImage, Object mediaContent);

}
