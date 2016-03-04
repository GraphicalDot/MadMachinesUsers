package com.sports.unity.messages.controller.model;

import android.content.Context;
import android.util.Log;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by madmachines on 18/11/15.
 */
public class PubSubMessaging {

    private static PubSubMessaging pubsubMessaging = null;
    private static PubSubManager pubSubManager = null;
    private static TinyDB tinyDB = null;

    private Thread getLeafNode = null;

    public static final String MESSAGE_TIME = "message_time";
    public static final String MESSAGE_TEXT_DATA = "message_text_data";
    public static final String GROUP_SERVER_ID = "group_server_id";
    public static final String MESSAGE_FROM = "message_from";


    public static int RECEIPT_KIND_READ = 1;
    public static int RECEIPT_KIND_SERVER = 2;
    public static int RECEIPT_KIND_CLIENT = 3;

    private Thread publishMessageThread = null;

    private SportsUnityDBHelper sportsUnityDBHelper = null;

    private LeafNode serverId = null;

    public PubSubMessaging(Context context) {
        sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        tinyDB = TinyDB.getInstance(context);
        pubSubManager = new PubSubManager(XMPPClient.getConnection());


    }

    synchronized public static PubSubMessaging getInstance(Context context) {
        if (pubsubMessaging == null) {
            pubsubMessaging = new PubSubMessaging(context);
        }
        return pubsubMessaging;
    }

    public void getJoinedGroups(Context context) {

//        String subject = "";
//        boolean isProcessedBefore = tinyDB.getBoolean(TinyDB.KEY_GET_JOINED_GROUPS_ON_REGISTRATION, true);
//        if (!isProcessedBefore) {
//            try {
//                List<Subscription> subscriptions = pubSubManager.getSubscriptions();
//                for (Subscription s :
//                        subscriptions) {
//                    Log.i("node", s.getNode());
//                    String groupServerId = s.getNode();
//                    LeafNode node = pubSubManager.getNode(groupServerId);
//                    List<Affiliation> affiliations = node.getAffiliations();
//                    Log.i("affiliations", affiliations.toString());
//                    List<Subscription> owners = node.getSubscriptionsAsOwner();
//                    Log.i("owners", owners.toString());
//                    for (Subscription owner :
//                            owners) {
//                        subject = groupServerId.substring(groupServerId.indexOf("%") + 1, groupServerId.indexOf("%%"));
//                        String ownerPhoneNumber = owner.getJid().substring(0, owner.getJid().indexOf("@"));
//                        Contacts ownerContact = sportsUnityDBHelper.getContact(ownerPhoneNumber);
//                        if (owner == null) {
//                            XMPPService.createContact(ownerPhoneNumber, context, true);
//                            ownerContact = sportsUnityDBHelper.getContact(ownerPhoneNumber);
//                        }
//                        long chatId = sportsUnityDBHelper.createGroupChatEntry(subject, ownerContact.id, null, groupServerId);
//                        sportsUnityDBHelper.updateChatEntry(SportsUnityDBHelper.getDummyMessageRowId(), chatId, groupServerId);
//                    }
//                }
//                tinyDB.putBoolean(TinyDB.KEY_GET_JOINED_GROUPS_ON_REGISTRATION, true);
//            } catch (SmackException.NoResponseException e) {
//                e.printStackTrace();
//            } catch (XMPPException.XMPPErrorException e) {
//                e.printStackTrace();
//            } catch (SmackException.NotConnectedException e) {
//                e.printStackTrace();
//            }
//        } else {
//            //do nothing
//        }

    }

    public boolean createNode(String roomName, Context context) {
        boolean success = false;
        PubSubManager pubSubManager = new PubSubManager(XMPPClient.getConnection());

        CustomConfigurationForm form = new CustomConfigurationForm(DataForm.Type.submit);
        form.setAccessModel(AccessModel.open);
        form.setDeliverPayloads(true);
        form.setNotifyRetract(true);
        form.setPersistentItems(false);
        form.setPresenceBasedDelivery(false);
        form.getAccessModel();
        form.setPublishModel(PublishModel.open);

        form.addField("pubsub#notification_type", FormField.Type.text_single);
        form.setAnswer("pubsub#notification_type", "normal");

        form.addField("pubsub#send_last_published_item", FormField.Type.text_single);
        form.setAnswer("pubsub#send_last_published_item", "never");


        try {
            Log.i("creating node", "true");
            LeafNode leaf = (LeafNode) pubSubManager.createNode(roomName, form);
            Log.i("subscribing node", "true");
            leaf.subscribe(TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID) + "@mm.io");
            success = true;

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return success;
    }

    public void publishMessage(String message, long chatID, String groupServerId, Context context) {

        String from = tinyDB.getString(TinyDB.KEY_USER_JID);
        String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());

        String payLoad = createPayLoad(message, from, groupServerId, time, SportsUnityDBHelper.MIME_TYPE_TEXT);
        SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message",
                "<message xmlns='pubsub:text:message'>" + "!@#$" + payLoad + "$#@!" + "</message>");
        PayloadItem item = new PayloadItem(from, simplePayload);

        long messageId = sportsUnityDBHelper.addMessage(message, SportsUnityDBHelper.MIME_TYPE_TEXT, item.getId(), true, time, item.getId(), null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS);
        sportsUnityDBHelper.updateChatEntry(messageId, chatID, groupServerId);

        publishItem(item);
    }

    private void publishItem(PayloadItem item) {
        if (publishMessageThread != null && publishMessageThread.isAlive()) {
            //do nothing
        } else {
            try {
                if (serverId != null) {
                    serverId.publish(item);
                } else {
                    Log.i("Leaf node null", "true");
                }
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    private String createPayLoad(String message, String from, String groupServerId, String time, String mimeType) {
        JSONObject payload = new JSONObject();
        String encodedPayload = null;
        try {
            payload.put(Constants.PARAM_MIME_TYPE, mimeType);
            payload.put(MESSAGE_TEXT_DATA, message);
            payload.put(MESSAGE_TIME, time);
            payload.put(MESSAGE_FROM, from);
            payload.put(GROUP_SERVER_ID, groupServerId);
            encodedPayload = URLEncoder.encode(payload.toString(), "utf-8");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedPayload;
    }

    public void updatePublishedReceipt(String fromJid, String packetId) {
        sportsUnityDBHelper.updateReadStatus(packetId);

        updateReceipts(RECEIPT_KIND_SERVER);
    }

    public void updateReceipts(int receiptKind) {

        /**
         * get read receipts in database and then update the double ticks in the corresponding chats
         */

        ActivityActionHandler.getInstance().dispatchReceiptEvent(ActivityActionHandler.CHAT_SCREEN_KEY, receiptKind);
    }

    public void sendMediaMessage(String contentChecksum, String thumbnailImageAsBase64, String mimeType, String groupServerId) {
        String from = tinyDB.getString(TinyDB.KEY_USER_JID);
        String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());

        String messageBody = null;
        if (thumbnailImageAsBase64 != null) {
            messageBody = contentChecksum + ":" + thumbnailImageAsBase64;
        } else {
            messageBody = contentChecksum;
        }

        String payLoad = createPayLoad(messageBody, from, groupServerId, time, mimeType);
        SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message",
                "<message xmlns='pubsub:text:message'>" + "!@#$" + payLoad + "$#@!" + "</message>");
        PayloadItem item = new PayloadItem(from, simplePayload);

        publishItem(item);
    }

    public void sendStickerMessage(String stickerAssetPath, long chatId, String groupServerId) {
        String from = tinyDB.getString(TinyDB.KEY_USER_JID);
        String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());

        String payLoad = createPayLoad(stickerAssetPath, from, groupServerId, time, SportsUnityDBHelper.MIME_TYPE_STICKER);
        SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message",
                "<message xmlns='pubsub:text:message'>" + "!@#$" + payLoad + "$#@!" + "</message>");
        PayloadItem item = new PayloadItem(from, simplePayload);

        long messageId = sportsUnityDBHelper.addMessage(stickerAssetPath, SportsUnityDBHelper.MIME_TYPE_STICKER, from, true, time,
                item.getId(), null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
        sportsUnityDBHelper.updateChatEntry(messageId, chatId, groupServerId);

        publishItem(item);
    }

    public void updateLeadNode(final String groupServerId) {
        Log.i("Leaf node ", "updated");
        serverId = null;
        if (pubSubManager != null) {
            if (getLeafNode != null && getLeafNode.isAlive()) {
                // do nothing
            } else {
                getLeafNode = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            serverId = pubSubManager.getNode(groupServerId);
                            Log.i("server id ", serverId.toString());
                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                getLeafNode.start();
            }
        } else {
            Log.i("pubsub manager ", "is null");
        }

    }
}
