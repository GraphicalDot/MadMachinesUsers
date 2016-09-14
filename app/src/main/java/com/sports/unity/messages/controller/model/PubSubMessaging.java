package com.sports.unity.messages.controller.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.PubSubExtension;
import com.sports.unity.XMPPManager.PubSubUtil;
import com.sports.unity.XMPPManager.SPUAffiliation;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserProfileHandler;
import com.sports.unity.common.model.UserUtil;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;
import com.sports.unity.util.NotificationHandler;
import com.sports.unity.util.SPORTSENUM;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by madmachines on 18/11/15.
 */
public class PubSubMessaging {

//    private Thread getLeafNode = null;

    public static final String MESSAGE_TIME = "message_time";
    public static final String MESSAGE_TEXT_DATA = "message_text_data";
    public static final String GROUP_SERVER_ID = "group_server_id";
    public static final String MESSAGE_FROM = "message_from";
    private static final String MESSAGE_TYPE = "message_type";
    private static final String MESSAGE_USER = "user";

    public static int RECEIPT_KIND_READ = 1;
    public static int RECEIPT_KIND_SERVER = 2;
    public static int RECEIPT_KIND_CLIENT = 3;

    private static final String USER_MESSAGE_TYPE = "u";
    private static final String GROUP_INVITATION_MESSAGE_TYPE = "i";
    private static final String GROUP_MEMBER_REMOVED_MESSAGE_TYPE = "r";
    private static final String GROUP_MEMBER_ADDED_MESSAGE_TYPE = "a";
    private static final String GROUP_INFO_CHANGED = "c";
    public static final String CURATED_ADMIN_JID = "admin";

    synchronized public static PubSubMessaging getInstance() {
        if (PUB_SUB_MESSAGING == null) {
            PUB_SUB_MESSAGING = new PubSubMessaging();
        }
        return PUB_SUB_MESSAGING;
    }

    private static PubSubMessaging PUB_SUB_MESSAGING = null;

    public PubSubMessaging() {
        //nothing
    }

    public void initGroupChat(String groupJid) {
        //nothing
    }

    public boolean createNode(String groupJid, String groupTitle, String groupImage, ArrayList<String> membersJid, Context context) {
        boolean success = false;
        PubSubManager pubSubManager = new PubSubManager(XMPPClient.getConnection());

        CustomConfigurationForm form = new CustomConfigurationForm(DataForm.Type.submit);
        form.setAccessModel(AccessModel.whitelist);
        form.setDeliverPayloads(true);
        form.setNotifyRetract(true);
        form.setPersistentItems(false);
        form.setPresenceBasedDelivery(false);
        form.getAccessModel();
        form.setSubscribe(true);
        form.setPublishModel(PublishModel.publishers);
        form.setTitle(groupTitle);

        form.addField("pubsub#notification_type", FormField.Type.text_single);
        form.setAnswer("pubsub#notification_type", "normal");

        form.addField("pubsub#send_last_published_item", FormField.Type.text_single);
        form.setAnswer("pubsub#send_last_published_item", "never");

        try {
            LeafNode leaf = (LeafNode) pubSubManager.createNode(groupJid, form);
            String ownerJID = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID) + "@mm.io";

            success = UserProfileHandler.uploadDisplayPic(context, groupJid, groupImage);

            PubSubUtil.updateAffiliations(leaf.getId(), ownerJID, membersJid);
            PubSubUtil.updateSubscriptions(leaf.getId(), ownerJID, membersJid);

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }

        return success;
    }

    private boolean loadAndUpdateGroupTitle(Context context, int chatId, String groupJid) {
        boolean success = false;
        try {
            ConfigureForm form = PubSubUtil.getNodeConfig(groupJid);
            String title = form.getTitle();

            SportsUnityDBHelper.getInstance(context).updateContactName(chatId, title);

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
        return success;
    }

    public boolean updateGroupInfo(String groupJid, String groupTitle, String groupImage, Context context) {
        boolean success = false;
        try {
            CustomConfigurationForm form = new CustomConfigurationForm(DataForm.Type.submit);
            form.setTitle(groupTitle);

            PubSubUtil.sendNodeConfig(groupJid, form);
            success = UserProfileHandler.uploadDisplayPic(context, groupJid, groupImage);

            if (success) {
                success = false;
                sendIntimationAboutGroupInfoChanged(context, groupJid, groupTitle);

                SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
                byte[] groupImageBytes = null;
                if (groupImage != null) {
                    groupImageBytes = Base64.decode(groupImage, Base64.DEFAULT);
                }
                sportsUnityDBHelper.updateGroupInfo(groupJid, groupTitle, groupImageBytes);

                success = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }

        return success;
    }

    public boolean addMembers(String groupJid, ArrayList<String> membersJid, Context context) {
        boolean success = false;
        try {
//            LeafNode leaf = getLeafNode(groupJid);

            PubSubUtil.updateAffiliations(groupJid, null, membersJid);
            PubSubUtil.updateSubscriptions(groupJid, null, membersJid);

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
        return success;
    }

//    private LeafNode getLeafNode(String nodeId) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
//        PubSubManager pubSubManager = new PubSubManager(XMPPClient.getConnection());
//        LeafNode leafNode = pubSubManager.getNode(nodeId);
//        return leafNode;
//    }

    public void updatePublishedReceipt(Context context, String packetId) {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        sportsUnityDBHelper.updateServerReceived(packetId);

        updateReceipts(RECEIPT_KIND_SERVER);
        XMPPMessageQueueHelper.getInstance().dequeue(packetId);
    }

    public void updateReceipts(int receiptKind) {

        /**
         * get read receipts in database and then update the double ticks in the corresponding chats
         */

        ActivityActionHandler.getInstance().dispatchReceiptEvent(ActivityActionHandler.CHAT_SCREEN_KEY, receiptKind);
    }

    public boolean resendMessage(Context context, com.sports.unity.messages.controller.model.Message messageObject) {
        boolean success = false;
        try {
            TinyDB tinyDB = TinyDB.getInstance(context);

            String from = tinyDB.getString(TinyDB.KEY_USER_JID);
            String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());

            Contacts contacts = SportsUnityDBHelper.getInstance(context).getContact(messageObject.contactID);
            String payLoad = encodeSimpleMessagePayload(messageObject.textData, from, contacts.jid, time, messageObject.mimeType);
            SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message", "<message xmlns='pubsub:text:message'>" + payLoad + "</message>");
            PayloadItem item = new PayloadItem(simplePayload);

            if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                String stanzaId = null;
                if (messageObject.messageStanzaId != null) {
                    stanzaId = messageObject.messageStanzaId;
                    PubSubUtil.publish(item, contacts.jid, stanzaId);
                } else {
                    stanzaId = PubSubUtil.publish(item, contacts.jid);
                    SportsUnityDBHelper.getInstance(context).updateMessageStanzaId(messageObject.id, stanzaId);
                }

                XMPPMessageQueueHelper.getInstance().enqueue(stanzaId);
                success = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return success;
    }

    public void sendTextMessage(Context context, String message, int chatID, String groupJid) {
        try {
            TinyDB tinyDB = TinyDB.getInstance(context);
            SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);

            String from = tinyDB.getString(TinyDB.KEY_USER_JID);
            String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());

            String payLoad = encodeSimpleMessagePayload(message, from, groupJid, time, SportsUnityDBHelper.MIME_TYPE_TEXT);
            SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message", "<message xmlns='pubsub:text:message'>" + payLoad + "</message>");
            PayloadItem item = new PayloadItem(simplePayload);

            String stanzaId = null;
            if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                stanzaId = publishItem(item, groupJid);
                XMPPMessageQueueHelper.getInstance().enqueue(stanzaId);
            }

            int messageId = sportsUnityDBHelper.addMessage(message, SportsUnityDBHelper.MIME_TYPE_TEXT, from, true, time, stanzaId, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, groupJid);
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public void sendMediaMessage(Context context, String contentChecksum, String thumbnailImageAsBase64, int messageId, String mimeType, String groupJid) {
        try {
            SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
            TinyDB tinyDB = TinyDB.getInstance(context);

            String from = tinyDB.getString(TinyDB.KEY_USER_JID);
            String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());

            String messageBody = null;
            if (thumbnailImageAsBase64 != null) {
                messageBody = contentChecksum + ":" + thumbnailImageAsBase64;
            } else {
                messageBody = contentChecksum;
            }

            String payLoad = encodeSimpleMessagePayload(messageBody, from, groupJid, time, mimeType);
            SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message", "<message xmlns='pubsub:text:message'>" + payLoad + "</message>");
            PayloadItem item = new PayloadItem(simplePayload);

            String stanzaId = null;
            if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                stanzaId = publishItem(item, groupJid);
                XMPPMessageQueueHelper.getInstance().enqueue(stanzaId);
            }

            sportsUnityDBHelper.updateMediaMessage_ContentUploaded(messageId, stanzaId, contentChecksum);
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public void sendStickerMessage(Context context, String stickerAssetPath, int chatId, String groupJid) {
        TinyDB tinyDB = TinyDB.getInstance(context);
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);

        try {
            String from = tinyDB.getString(TinyDB.KEY_USER_JID);
            String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());

            String payLoad = encodeSimpleMessagePayload(stickerAssetPath, from, groupJid, time, SportsUnityDBHelper.MIME_TYPE_STICKER);
            SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message", "<message xmlns='pubsub:text:message'>" + payLoad + "</message>");
            PayloadItem item = new PayloadItem(simplePayload);

            String stanzaId = null;
            if (XMPPClient.getInstance().isConnectionAuthenticated()) {
                stanzaId = publishItem(item, groupJid);
                XMPPMessageQueueHelper.getInstance().enqueue(stanzaId);
            }

            int messageId = sportsUnityDBHelper.addMessage(stickerAssetPath, SportsUnityDBHelper.MIME_TYPE_STICKER, from, true, time,
                    stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, groupJid);
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public void sendIntimationAboutMemberRemoved(Context context, String removedUserJid, String groupJid) {
        TinyDB tinyDB = TinyDB.getInstance(context);
        try {
            String from = tinyDB.getString(TinyDB.KEY_USER_JID);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MESSAGE_TYPE, GROUP_MEMBER_REMOVED_MESSAGE_TYPE);
            jsonObject.put(MESSAGE_FROM, from);
            jsonObject.put(GROUP_SERVER_ID, groupJid);
            jsonObject.put(MESSAGE_USER, removedUserJid);

            String payLoad = encodeSimpleMessagePayload(jsonObject);
            SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message", "<message xmlns='pubsub:text:message'>" + payLoad + "</message>");
            PayloadItem item = new PayloadItem(simplePayload);

            publishItem(item, groupJid);
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public void sendIntimationAboutAffiliationListChanged(Context context, String groupJid) {
        TinyDB tinyDB = TinyDB.getInstance(context);
        try {
            String from = tinyDB.getString(TinyDB.KEY_USER_JID);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MESSAGE_TYPE, GROUP_MEMBER_ADDED_MESSAGE_TYPE);
            jsonObject.put(MESSAGE_FROM, from);
            jsonObject.put(GROUP_SERVER_ID, groupJid);

            String payLoad = encodeSimpleMessagePayload(jsonObject);
            SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message", "<message xmlns='pubsub:text:message'>" + payLoad + "</message>");
            PayloadItem item = new PayloadItem(simplePayload);

            publishItem(item, groupJid);
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public void sendIntimationAboutGroupInfoChanged(Context context, String groupJid, String title) {
        TinyDB tinyDB = TinyDB.getInstance(context);
        try {
            String from = tinyDB.getString(TinyDB.KEY_USER_JID);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MESSAGE_TYPE, GROUP_INFO_CHANGED);
            jsonObject.put(MESSAGE_FROM, from);
            jsonObject.put(GROUP_SERVER_ID, groupJid);
            jsonObject.put(MESSAGE_TEXT_DATA, title);

            String payLoad = encodeSimpleMessagePayload(jsonObject);
            SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message", "<message xmlns='pubsub:text:message'>" + payLoad + "</message>");
            PayloadItem item = new PayloadItem(simplePayload);

            publishItem(item, groupJid);
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public void handlePubSubMessage(Context context, org.jivesoftware.smack.packet.Message message) {
        String stanzaId = message.getStanzaId();

        String messageType = null;
        String groupJID = null;
        String time = null;
        String text = null;
        String mimeType = null;
        String from = null;

        JSONObject messageJsonObject = null;
        try {
            if (message.hasExtension("pubsub", "http://jabber.org/protocol/pubsub")) {
                PubSubExtension pubSubExtension = message.getExtension("pubsub", "http://jabber.org/protocol/pubsub");
                if (pubSubExtension.getExtensions().size() > 0) {
                    Subscription subscription = (Subscription) pubSubExtension.getExtensions().get(0);
                    if (subscription.getState().equals(Subscription.State.subscribed)) {
                        handleGroupInvitation(context, subscription.getNode());
                        ContactsHandler.getInstance().addCallToUpdateRequiredContactChat(context);
                    } else if (subscription.getState().equals(Subscription.State.none)) {
                        handleGroupElimination(context, subscription.getNode());
                    }
                } else {
                    //nothing
                }
            } else if (message.hasExtension("event", "http://jabber.org/protocol/pubsub#event")) {
                EventElement eventElement = message.getExtension("event", "http://jabber.org/protocol/pubsub#event");
                List<ExtensionElement> extensionElementList = eventElement.getExtensions();
                if (extensionElementList.size() > 0) {
                    ItemsExtension itemsExtension = (ItemsExtension) extensionElementList.get(0);
                    List<ExtensionElement> items = (List<ExtensionElement>) itemsExtension.getItems();
                    if (items.size() > 0) {
                        PayloadItem payloadItem = (PayloadItem) items.get(0);
                        SimplePayload simplePayload = (SimplePayload) payloadItem.getPayload();

                        String messageXML = String.valueOf(simplePayload.toXML());
                        String data = messageXML.substring(messageXML.indexOf(">") + 1, messageXML.indexOf("</message>"));

                        String decodedData = URLDecoder.decode(data, "utf-8");
                        messageJsonObject = new JSONObject(decodedData);

                        messageType = messageJsonObject.getString(MESSAGE_TYPE);
                        groupJID = messageJsonObject.getString(GROUP_SERVER_ID);
                        from = messageJsonObject.getString(MESSAGE_FROM);

                        TinyDB tinyDB = TinyDB.getInstance(context);
                        if (from.equals(tinyDB.getString(TinyDB.KEY_USER_JID))) {
                            //nothing
                        } else {
                            if (messageType == null || messageType.equals(USER_MESSAGE_TYPE)) {
                                time = messageJsonObject.getString(MESSAGE_TIME);
                                text = messageJsonObject.getString(MESSAGE_TEXT_DATA);
                                mimeType = messageJsonObject.getString(Constants.PARAM_MIME_TYPE);

                                handleUserPubSubMessage(context, from, mimeType, text, time, groupJID, stanzaId);
                            } else if (messageType.equals(GROUP_MEMBER_REMOVED_MESSAGE_TYPE)) {
                                String userJid = messageJsonObject.getString(MESSAGE_USER);
                                handleOtherMemberRemoved(context, userJid, groupJID);
                            } else if (messageType.equals(GROUP_MEMBER_ADDED_MESSAGE_TYPE)) {
                                handleMembersAdded(context, groupJID);
                                ContactsHandler.getInstance().addCallToUpdateRequiredContactChat(context);
                            } else if (messageType.equals(GROUP_INFO_CHANGED)) {
                                handleGroupInfoChanged(context, groupJID);
                                ContactsHandler.getInstance().addCallToUpdateRequiredContactChat(context);
                            }
                        }
                    } else {

                    }
                } else {

                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean loadGroupInfo(Context context, int chatId, String groupJid) {
        boolean success = false;
        success = loadAndUpdateGroupTitle(context, chatId, groupJid);
        if (success) {
            success = loadAffiliations(context, chatId, groupJid);
            if (success) {
                String imageContent = UserProfileHandler.getInstance().downloadDisplayPic(context, groupJid, UserProfileHandler.IMAGE_THUMNB);
                if (imageContent == null) {
                    success = false;
                } else {
                    byte[] groupImageBytes = null;
                    if (imageContent != null) {
                        groupImageBytes = Base64.decode(imageContent, Base64.DEFAULT);
                    }

                    if (imageContent.length() > 0) {
                        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
                        sportsUnityDBHelper.updateGroupInfo(groupJid, sportsUnityDBHelper.getUserNameByJid(groupJid), groupImageBytes);
                    }
                    success = true;
                }
            }
        }
        return success;
    }

    public boolean loadAffiliations(Context context, int chatId, String groupJid) {
        boolean success = false;
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        try {
            SportsUnityDBHelper.GroupParticipants groupParticipants = sportsUnityDBHelper.getGroupParticipants(chatId);
//            if (groupParticipants.usersInGroup.size() == 0)
            {
                List<SPUAffiliation> spuAffiliationsList = PubSubUtil.getAffiliations(groupJid);

                ArrayList<Integer> members = new ArrayList<>();
                ArrayList<Integer> admins = new ArrayList<>();

                String jid = null;
                int contactId = 0;
                for (SPUAffiliation affiliation : spuAffiliationsList) {
                    jid = affiliation.getJid();
                    if (jid.contains("@admin.mm.io")) {
                        jid = jid.substring(0, jid.indexOf("@admin.mm.io"));
                    } else {
                        jid = jid.substring(0, jid.indexOf("@mm.io"));
                    }
                    Contacts contacts = sportsUnityDBHelper.getContactByJid(jid);
                    if (contacts == null) {
                        contactId = sportsUnityDBHelper.addToContacts("Unknown", null, jid, "", null, Contacts.AVAILABLE_BY_OTHER_CONTACTS, true);
                    } else {
                        contactId = contacts.id;
                    }

                    if (affiliation.getType() == SPUAffiliation.Type.owner) {
                        admins.add(contactId);
                    }

                    if (affiliation.getType() == SPUAffiliation.Type.owner || affiliation.getType() == SPUAffiliation.Type.publisher) {
                        members.add(contactId);
                    } else {
                        //nothing
                    }
                }

//                ContactsHandler.getInstance().addCallToUpdateRequiredContactChat(context);

                SportsUnityDBHelper.getInstance(context).createGroupUserEntry(chatId, members);
                SportsUnityDBHelper.getInstance(context).updateParticipantAsAdmin(admins, chatId);

                SportsUnityDBHelper.getInstance(context).updateChatUpdateRequired(chatId, false);
            }

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
        return success;
    }

    public boolean removeFromGroup(String jid, String groupJID) {
        boolean success = false;
        try {
            success = PubSubUtil.removeFromGroup(groupJID, jid);
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
        return success;
    }

    public boolean exitGroup(String jid, String groupJID) {
        boolean success = false;
        try {
            PubSubUtil.unsubscribe(jid, groupJID);

            success = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            handleConnectionException(ex);
        }
        return success;
    }

    public void handleCreationOfAlreadySubscribedGroup(Context context, String nodeId) {
        handleGroupCreation(context, nodeId);
    }

//    public void getNodeConfig(String nodeId) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
//        PubSubManager pubSubManager = new PubSubManager(XMPPClient.getConnection());
////        ConfigureForm form = pubSubManager.getDefaultConfiguration();
//
//        pubSubManager.getNode(nodeId);
//    }

    private void handleUserPubSubMessage(Context context, String from, String mimeType, String text, String time, String groupJID, String stanzaId) {
        TinyDB tinyDB = TinyDB.getInstance(context);
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);

        if (from.equals(tinyDB.getString(TinyDB.KEY_USER_JID))) {
            //Do nothing
        } else {
            int chatId = XMPPService.getChatIdOrCreateIfNotExist(context, true, groupJID, false);
            handlePubSubMessageType(context, chatId, from, mimeType, text, time, stanzaId, groupJID);

            boolean eventDispatched = ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_SCREEN_KEY, groupJID);
            if (eventDispatched) {
                //nothing
            } else {
                Contacts contact = sportsUnityDBHelper.getContact(chatId);
                try {
                    sportsUnityDBHelper.updateUnreadCount(chatId, groupJID);
                    if (contact.availableStatus != Contacts.AVAILABLE_BY_MY_CONTACTS) {
                        ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_OTHERS_LIST_KEY);
                    } else {
                        ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY);
                    }

                    byte[] image = contact.image;
                    XMPPService.displayNotification(context, text, from, mimeType, chatId, true, groupJID, image, contact.availableStatus, contact.status);
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void handleGroupInvitation(Context context, String groupJID) {
        handleGroupCreation(context, groupJID);
    }

    private void handleGroupCreation(Context context, String nodeId) {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        String subject = nodeId.substring(nodeId.indexOf("%") + 1, nodeId.indexOf("%%"));
        if (nodeId.startsWith(Constants.DISCUSS_JID)) {
            sportsUnityDBHelper.updateGroupJIDInNewsDiscuss(subject, nodeId);
            subject = sportsUnityDBHelper.getArticleNameThroughID(subject);
        }
        int chatId = sportsUnityDBHelper.getChatEntryID(nodeId);
        if (chatId == SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            chatId = sportsUnityDBHelper.createGroupChatEntry(subject, null, nodeId);
            sportsUnityDBHelper.updateChatEntry(SportsUnityDBHelper.getDummyMessageRowId(), nodeId);

            ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY);
        } else {
            sportsUnityDBHelper.updateUserBlockStatus(chatId, false);

            ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY);
        }
        if (nodeId.startsWith(Constants.DISCUSS_JID)) {
            displayNotificationForDiscuss(context, nodeId, subject);
        }
    }

    private void handleGroupElimination(Context context, String groupJID) {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        int chatId = sportsUnityDBHelper.getChatEntryID(groupJID);
        if (chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            String currentUserJID = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);
            int contactId = sportsUnityDBHelper.getContactIdFromJID(currentUserJID);

            sportsUnityDBHelper.updateUserBlockStatus(chatId, true);
            sportsUnityDBHelper.deleteGroupMember(chatId, contactId);

            ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY);
        }
    }

    private void handleOtherMemberRemoved(Context context, String removedUserJid, String groupJid) {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        int chatId = sportsUnityDBHelper.getChatEntryID(groupJid);
        if (chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            int contactId = sportsUnityDBHelper.getContactIdFromJID(removedUserJid);
            sportsUnityDBHelper.deleteGroupMember(chatId, contactId);
        }
    }

    private void handleMembersAdded(Context context, String groupJid) {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        int chatId = sportsUnityDBHelper.getChatEntryID(groupJid);
        if (chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            sportsUnityDBHelper.updateChatUpdateRequired(chatId, true);
        }
    }

    private void handleGroupInfoChanged(Context context, String groupJid) {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        int chatId = sportsUnityDBHelper.getChatEntryID(groupJid);
        if (chatId != SportsUnityDBHelper.DEFAULT_ENTRY_ID) {
            sportsUnityDBHelper.updateChatUpdateRequired(chatId, true);
        }
    }

    private void handlePubSubMessageType(Context context, int chatId, String from, String mimeType, String text, String time, String stanzaId, String groupJID) {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);

        if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            String checksum = PersonalMessaging.getChecksumOutOfMessageBody(text);
            String thumbnail = PersonalMessaging.getEncodedImageOutOfImage(text);

            byte[] bytesOfThumbnail = null;
            if (thumbnail != null) {
                bytesOfThumbnail = Base64.decode(thumbnail, Base64.DEFAULT);
            }

            int messageId = sportsUnityDBHelper.addMediaMessage(checksum, mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS, null, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, groupJID);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, from, mimeType, checksum, Integer.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
            int messageId = sportsUnityDBHelper.addMessage(text.toString(), mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, groupJID);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
            String checksum = text;

            int messageId = sportsUnityDBHelper.addMessage(text.toString(), mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, groupJID);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, from, mimeType, checksum, Integer.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            String checksum = PersonalMessaging.getChecksumOutOfMessageBody(text);
            String thumbnail = PersonalMessaging.getEncodedImageOutOfImage(text);

            byte[] bytesOfThumbnail = null;
            if (thumbnail != null) {
                bytesOfThumbnail = Base64.decode(thumbnail, Base64.DEFAULT);
            }

            int messageId = sportsUnityDBHelper.addMediaMessage(checksum, mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS, null, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, groupJID);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, from, mimeType, checksum, Integer.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            int messageId = sportsUnityDBHelper.addMessage(text.toString(), mimeType, from, false,
                    time, stanzaId, null, null,
                    chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, groupJID);
        }
    }

    private void handleConnectionException(Exception ex) {
        //TODO
    }

    private String encodeSimpleMessagePayload(String message, String from, String groupJid, String time, String mimeType) {
        return encodeHiddenMessagePayload(USER_MESSAGE_TYPE, message, from, groupJid, time, mimeType);
    }

    private String encodeGroupInvitationMessagePayload(String from, String groupJid, String time) {
        return encodeHiddenMessagePayload(GROUP_INVITATION_MESSAGE_TYPE, "", from, groupJid, time, "");
    }

    private String encodeHiddenMessagePayload(String messageType, String message, String from, String groupJid, String time, String mimeType) {
        JSONObject payloadJsonObject = new JSONObject();
        String encodedPayload = null;
        try {
            payloadJsonObject.put(MESSAGE_TYPE, messageType);
            payloadJsonObject.put(Constants.PARAM_MIME_TYPE, mimeType);
            payloadJsonObject.put(MESSAGE_TEXT_DATA, message);
            payloadJsonObject.put(MESSAGE_TIME, time);
            payloadJsonObject.put(MESSAGE_FROM, from);
            payloadJsonObject.put(GROUP_SERVER_ID, groupJid);

            encodedPayload = encodeSimpleMessagePayload(payloadJsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedPayload;
    }

    private String encodeSimpleMessagePayload(JSONObject payloadJsonObject) throws Exception {
        String encodedPayload = URLEncoder.encode(payloadJsonObject.toString(), "utf-8");
        return encodedPayload;
    }

    private String publishItem(PayloadItem item, String groupJid) throws SmackException.NotConnectedException {
        return PubSubUtil.publish(item, groupJid);
    }

    private void displayNotificationForDiscuss(Context context, String groupJID, String name) {
        android.support.v7.app.NotificationCompat.Builder builder = new android.support.v7.app.NotificationCompat.Builder(context);
        builder.setColor(context.getResources().getColor(R.color.orange));
        builder.setSmallIcon(R.drawable.ic_ngroup_crtd_maximus);
        builder.setContentTitle("Join the discussion");
        builder.setContentText(name);
        builder.setPriority(Notification.PRIORITY_HIGH);
        int defaults = 0;
        defaults = CommonUtil.getDefaults(context, defaults, builder);
        builder.setDefaults(defaults);
        builder.setAutoCancel(true);
        int chatId = XMPPService.getChatIdOrCreateIfNotExist(context, true, groupJID, false);
        PendingIntent pendingIntent = XMPPService.getPendingIntentForNotificationChatActivity(context, true, name, groupJID, chatId, null, false, Contacts.AVAILABLE_BY_MY_CONTACTS, null);
        builder.setContentIntent(pendingIntent);
        if (UserUtil.isNotificationAndSound()) {
            Uri uri = Uri.parse(UserUtil.getNotificationSoundURI());
            builder.setSound(uri);
        } else {
            //nothing
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!UserUtil.isFilterCompleted()) {
            //do nothing
        } else {
            notificationManager.notify(NotificationHandler.NOTIFICATION_ID, builder.build());
        }

    }
}
