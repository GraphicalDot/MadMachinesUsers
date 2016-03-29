package com.sports.unity.messages.controller.model;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.PubSubExtension;
import com.sports.unity.XMPPManager.PubSubUtil;
import com.sports.unity.XMPPManager.SPUAffiliation;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.model.ContactsHandler;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.common.model.UserProfileHandler;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;
import com.sports.unity.util.ActivityActionHandler;
import com.sports.unity.util.CommonUtil;
import com.sports.unity.util.Constants;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.UnsubscribeExtension;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
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

    public static int RECEIPT_KIND_READ = 1;
    public static int RECEIPT_KIND_SERVER = 2;
    public static int RECEIPT_KIND_CLIENT = 3;

    private static final String USER_MESSAGE_TYPE = "user_message";
    private static final String GROUP_INVITATION_MESSAGE_TYPE = "group_invitation";

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

    public void initGroupChat(String groupJid){
        //nothing
    }

    public boolean createNode(String groupJid, String groupTitle, ArrayList<String> membersJid, Context context) {
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

            PubSubUtil.updateAffiliations(leaf.getId(), ownerJID, membersJid);
            PubSubUtil.updateSubscriptions(leaf.getId(), ownerJID, membersJid);

            success = true;
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
    }

    public void updateReceipts(int receiptKind) {

        /**
         * get read receipts in database and then update the double ticks in the corresponding chats
         */

        ActivityActionHandler.getInstance().dispatchReceiptEvent(ActivityActionHandler.CHAT_SCREEN_KEY, receiptKind);
    }

    public void sendTextMessage(Context context, String message, long chatID, String groupJid) {
        try {
            TinyDB tinyDB = TinyDB.getInstance(context);
            SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);

            String from = tinyDB.getString(TinyDB.KEY_USER_JID);
            String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());

            String payLoad = encodeSimpleMessagePayload(message, from, groupJid, time, SportsUnityDBHelper.MIME_TYPE_TEXT);
            SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message", "<message xmlns='pubsub:text:message'>" + payLoad + "</message>");
            PayloadItem item = new PayloadItem(from, simplePayload);

            String stanzaId = publishItem(item, groupJid);

            long messageId = sportsUnityDBHelper.addMessage(message, SportsUnityDBHelper.MIME_TYPE_TEXT, from, true, time, stanzaId, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatID, groupJid);

        }catch (Exception ex){
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public void sendMediaMessage(Context context, String contentChecksum, String thumbnailImageAsBase64, long messageId, String mimeType, String groupJid) {
        try{
            SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
            TinyDB tinyDB = TinyDB.getInstance(context);

//            LeafNode node = getLeafNode(groupJid);

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
            PayloadItem item = new PayloadItem(from, simplePayload);

            String stanzaId = publishItem(item, groupJid);

            sportsUnityDBHelper.updateMediaMessage_ContentUploaded(messageId, stanzaId, contentChecksum);
        }catch (Exception ex){
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public void sendStickerMessage(Context context, String stickerAssetPath, long chatId, String groupJid) {
        TinyDB tinyDB = TinyDB.getInstance(context);
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);

        try{
//            LeafNode node = getLeafNode(groupJid);

            String from = tinyDB.getString(TinyDB.KEY_USER_JID);
            String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());

            String payLoad = encodeSimpleMessagePayload(stickerAssetPath, from, groupJid, time, SportsUnityDBHelper.MIME_TYPE_STICKER);
            SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message", "<message xmlns='pubsub:text:message'>" + payLoad + "</message>");
            PayloadItem item = new PayloadItem(from, simplePayload);

            String stanzaId = publishItem(item, groupJid);

            long messageId = sportsUnityDBHelper.addMessage(stickerAssetPath, SportsUnityDBHelper.MIME_TYPE_STICKER, from, true, time,
                    stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, groupJid);
        }catch (Exception ex){
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public void sendIntimationAboutMemberRemoved(){
        //TODO
    }

    public void sendIntimationAboutAffiliationListChanged(){
        //TODO
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
            if( message.hasExtension("pubsub", "http://jabber.org/protocol/pubsub") ){
                PubSubExtension pubSubExtension = message.getExtension("pubsub", "http://jabber.org/protocol/pubsub");
                if( pubSubExtension.getExtensions().size() > 0 ){
                    Subscription subscription = (Subscription) pubSubExtension.getExtensions().get(0);
                    if( subscription.getState().equals(Subscription.State.subscribed) ){
                        handleGroupInvitation(context, subscription.getNode());
                    } else if( subscription.getState().equals(Subscription.State.none) ){
                        handleGroupElimination(context, subscription.getNode());
                    }
                } else {
                    //nothing
                }
            } else if( message.hasExtension("event", "http://jabber.org/protocol/pubsub#event") ) {
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

                        from = messageJsonObject.getString(MESSAGE_FROM);
//                        messageType = messageJsonObject.getString(MESSAGE_TYPE);
                        time = messageJsonObject.getString(MESSAGE_TIME);
                        text = messageJsonObject.getString(MESSAGE_TEXT_DATA);
                        mimeType = messageJsonObject.getString(Constants.PARAM_MIME_TYPE);
                        groupJID = messageJsonObject.getString(GROUP_SERVER_ID);

                        handleUserPubSubMessage(context, from, mimeType, text, time, groupJID, stanzaId);
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

    public void loadAffiliations(Context context, long chatId, String groupJid) {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        try {
            SportsUnityDBHelper.GroupParticipants groupParticipants = sportsUnityDBHelper.getGroupParticipants(chatId);
            if( groupParticipants.usersInGroup.size() == 0 ) {
                List<SPUAffiliation> spuAffiliationsList = PubSubUtil.getAffiliations(groupJid);

                ArrayList<Long> members = new ArrayList<>();
                ArrayList<Long> admins = new ArrayList<>();

                String jid = null;
                long contactId = 0;
                for (SPUAffiliation affiliation : spuAffiliationsList) {
                    jid = affiliation.getJid();
                    jid = jid.substring( 0, jid.indexOf("@mm.io"));

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

                ContactsHandler.getInstance().addCallToUpdateUserVCard(context);

                SportsUnityDBHelper.getInstance(context).createGroupUserEntry(chatId, members);
                SportsUnityDBHelper.getInstance(context).updateAdmin(admins, chatId);
            } else {
                //nothing
            }
        }catch (Exception ex){
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public boolean removeFromGroup(String jid, String groupJID){
        boolean success = false;
        try {
            success = PubSubUtil.removeFromGroup(groupJID, jid);
        }catch (Exception ex){
            ex.printStackTrace();
            handleConnectionException(ex);
        }
        return success;
    }

    public void exitGroup(String jid, String groupJID){
        try {
            PubSubUtil.unsubscribe(jid, groupJID);
        }catch (Exception ex){
            ex.printStackTrace();
            handleConnectionException(ex);
        }
    }

    public void getNodeConfig(String nodeId) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSubManager pubSubManager = new PubSubManager(XMPPClient.getConnection());
//        ConfigureForm form = pubSubManager.getDefaultConfiguration();

        pubSubManager.getNode(nodeId);
    }

    private void handleUserPubSubMessage(Context context, String from, String mimeType, String text, String time, String groupJID, String stanzaId){
        TinyDB tinyDB = TinyDB.getInstance(context);
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);

        if ( from.equals(tinyDB.getString(TinyDB.KEY_USER_JID)) ) {
            //Do nothing
        } else {
            long chatId = XMPPService.getChatIdOrCreateIfNotExist(context, true, from, groupJID, false);
            handlePubSubMessageType(context, chatId, from, mimeType, text, time, stanzaId, groupJID);

            boolean eventDispatched = ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_SCREEN_KEY, groupJID);
            if (eventDispatched) {
                //nothing
            } else {
                long contactId = sportsUnityDBHelper.getContactAssociatedToChat(chatId);
                Contacts contact = sportsUnityDBHelper.getContact(contactId);
                try {
                    sportsUnityDBHelper.updateUnreadCount(chatId, groupJID);
                    if (contact.availableStatus != Contacts.AVAILABLE_BY_MY_CONTACTS) {
                        ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_OTHERS_LIST_KEY);
                    } else {
                        ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY);
                    }

                    //TODO to get image belongs to particular group.
                    byte[] image = null;
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

    private void handleGroupInvitation(Context context, String groupJID){
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        String subject = groupJID.substring(groupJID.indexOf("%") + 1, groupJID.indexOf("%%"));
        long chatId = sportsUnityDBHelper.getChatEntryID(groupJID);
        if( chatId == SportsUnityDBHelper.DEFAULT_ENTRY_ID ) {
            chatId = sportsUnityDBHelper.createGroupChatEntry(subject, SportsUnityDBHelper.getDummyContactRowId(), null, groupJID);
            sportsUnityDBHelper.updateChatEntry(SportsUnityDBHelper.getDummyMessageRowId(), chatId, groupJID);

            ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY);

            loadAffiliations(context, chatId, groupJID);
        }
    }

    private void handleGroupElimination(Context context, String groupJID){
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        long chatId = sportsUnityDBHelper.getChatEntryID(groupJID);
        if( chatId == SportsUnityDBHelper.DEFAULT_ENTRY_ID ) {
            String currentUserJID = TinyDB.getInstance(context).getString(TinyDB.KEY_USER_JID);
            long contactId = sportsUnityDBHelper.getContactIdFromJID(currentUserJID);

            sportsUnityDBHelper.updateChatBlockStatus(chatId, false);
            sportsUnityDBHelper.deleteGroupMember(contactId);

            ActivityActionHandler.getInstance().dispatchCommonEvent(ActivityActionHandler.CHAT_LIST_KEY);
        }
    }

    private void handlePubSubMessageType(Context context, long chatId, String from, String mimeType, String text, String time, String stanzaId, String groupJID) {
        SportsUnityDBHelper sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);

        if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_IMAGE)) {
            String checksum = PersonalMessaging.getChecksumOutOfMessageBody(text);
            String thumbnail = PersonalMessaging.getEncodedImageOutOfImage(text);

            byte[] bytesOfThumbnail = null;
            if (thumbnail != null) {
                bytesOfThumbnail = Base64.decode(thumbnail, Base64.DEFAULT);
            }

            long messageId = sportsUnityDBHelper.addMediaMessage(checksum, mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS, null, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, groupJID);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, from, mimeType, checksum, Long.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_TEXT)) {
            long messageId = sportsUnityDBHelper.addMessage(text.toString(), mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, groupJID);
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_AUDIO)) {
            String checksum = text;

            long messageId = sportsUnityDBHelper.addMessage(text.toString(), mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, groupJID);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, from, mimeType, checksum, Long.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_VIDEO)) {
            String checksum = PersonalMessaging.getChecksumOutOfMessageBody(text);
            String thumbnail = PersonalMessaging.getEncodedImageOutOfImage(text);

            byte[] bytesOfThumbnail = null;
            if (thumbnail != null) {
                bytesOfThumbnail = Base64.decode(thumbnail, Base64.DEFAULT);
            }

            long messageId = sportsUnityDBHelper.addMediaMessage(checksum, mimeType, from, false,
                    time, stanzaId, null, null, chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS, null, bytesOfThumbnail);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, groupJID);

            ActivityActionHandler.getInstance().dispatchIncomingMediaEvent(ActivityActionHandler.CHAT_SCREEN_KEY, from, mimeType, checksum, Long.valueOf(messageId));
        } else if (mimeType.equals(SportsUnityDBHelper.MIME_TYPE_STICKER)) {
            long messageId = sportsUnityDBHelper.addMessage(text.toString(), mimeType, from, false,
                    time, stanzaId, null, null,
                    chatId, SportsUnityDBHelper.DEFAULT_READ_STATUS);
            sportsUnityDBHelper.updateChatEntry(messageId, chatId, groupJID);
        }
    }

    private void handleConnectionException(Exception ex){
        //TODO
    }

    private String encodeSimpleMessagePayload(String message, String from, String groupJid, String time, String mimeType) {
        return encodeHiddenMessagePayload( USER_MESSAGE_TYPE, message, from, groupJid, time, mimeType);
    }

    private String encodeGroupInvitationMessagePayload(String from, String groupJid, String time) {
        return encodeHiddenMessagePayload( GROUP_INVITATION_MESSAGE_TYPE, "", from, groupJid, time, "");
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

            encodedPayload = URLEncoder.encode(payloadJsonObject.toString(), "utf-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedPayload;
    }

    private String publishItem(PayloadItem item, String groupJid) throws SmackException.NotConnectedException {
        return PubSubUtil.publish(item, groupJid);
    }

}
