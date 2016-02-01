package com.sports.unity.XMPPManager;

/**
 * Created by amandeep on 10/12/15.
 */
public interface ReadReceiptReceivedListener {

    public void onReceiptReceived(String fromJid, String toJid, String packetId);

}
