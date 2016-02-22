package com.sports.unity.messages.controller.model;

import android.content.Context;
import android.util.Log;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.XMPPManager.XMPPClient;
import com.sports.unity.XMPPManager.XMPPService;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.util.CommonUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishItem;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.SubscribeForm;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by madmachines on 18/11/15.
 */
public class PubSubMessaging {

    private static PubSubMessaging pubsubMessaging = null;
    private static PubSubManager pubSubManager = null;
    private static TinyDB tinyDB = null;


    private SportsUnityDBHelper sportsUnityDBHelper = null;

    public PubSubMessaging(Context context) {
        sportsUnityDBHelper = SportsUnityDBHelper.getInstance(context);
        pubSubManager = new PubSubManager(XMPPClient.getConnection());
        tinyDB = TinyDB.getInstance(context);

    }

    synchronized public static PubSubMessaging getInstance(Context context) {
        if (pubsubMessaging == null) {
            pubsubMessaging = new PubSubMessaging(context);
        }
        return pubsubMessaging;
    }

    public void getJoinedGroups(Context context) {

        String subject = "";
        boolean isProcessedBefore = tinyDB.getBoolean(TinyDB.KEY_GET_JOINED_GROUPS_ON_REGISTRATION, true);
        if (!isProcessedBefore) {
            try {
                List<Subscription> subscriptions = pubSubManager.getSubscriptions();
                for (Subscription s :
                        subscriptions) {
                    Log.i("node", s.getNode());
                    String groupServerId = s.getNode();
                    LeafNode node = pubSubManager.getNode(groupServerId);
                    List<Affiliation> affiliations = node.getAffiliations();
                    Log.i("affiliations", affiliations.toString());
                    List<Subscription> owners = node.getSubscriptionsAsOwner();
                    Log.i("owners", owners.toString());
                    for (Subscription owner :
                            owners) {
                        subject = groupServerId.substring(groupServerId.indexOf("%") + 1, groupServerId.indexOf("%%"));
                        String ownerJID = owner.getJid().substring(0, owner.getJid().indexOf("@"));
                        Contacts ownerContact = sportsUnityDBHelper.getContactByJid(ownerJID);
                        if (owner == null) {
                            XMPPService.createContact(ownerJID, context, true);
                            ownerContact = sportsUnityDBHelper.getContactByJid(ownerJID);
                        }
                        long chatId = sportsUnityDBHelper.createGroupChatEntry(subject, ownerContact.id, null, groupServerId);
                        sportsUnityDBHelper.updateChatEntry(SportsUnityDBHelper.getDummyMessageRowId(), chatId, groupServerId);
                    }
                }
                tinyDB.putBoolean(TinyDB.KEY_GET_JOINED_GROUPS_ON_REGISTRATION, true);
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        } else {
            //do nothing
        }

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
            LeafNode leaf = (LeafNode) pubSubManager.createNode(roomName, form);
            leaf.subscribe(TinyDB.getInstance(context).getString(TinyDB.KEY_USERNAME) + "@mm.io");
            Log.i("discoverinfo", "true");

            ServiceDiscoveryManager serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(XMPPClient.getConnection());
            DiscoverInfo info = leaf.discoverInfo();
            info.addIdentity(new DiscoverInfo.Identity("account", "test", "admin"));

            List<Subscription> subscriptions = leaf.getSubscriptions();
            for (Subscription s :
                    subscriptions) {
                Log.i("subscriber", s.getNamespace());
                Log.i("subscriber", s.getElementName());
                Log.i("subscriber", s.getJid());
            }
            success = true;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        ServiceDiscoveryManager serviceDiscoveryManager = ServiceDiscoveryManager.getInstanceFor(XMPPClient.getConnection());

        return success;
    }

    public boolean publishMessage(String message, long chatID, String groupServerId, Context context) {

        String from = tinyDB.getString(TinyDB.KEY_USERNAME);
        message = "$" + message + "$$";
        String time = String.valueOf(CommonUtil.getCurrentGMTTimeInEpoch());
        boolean success = false;
        try {
            LeafNode node = pubSubManager.getNode(groupServerId);
            SimplePayload simplePayload = new SimplePayload("message", "pubsub:text:message",
                    "<message xmlns='pubsub:text:message'>" + message + "*" + time + "**" + "!" + from + "!!</message>");
            PayloadItem item = new PayloadItem(from, simplePayload);
            node.publish(item);
            success = true;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        /**
         * Add message to database
         */

        message = message.replaceAll(Pattern.quote("$"), "");
        long messageId = sportsUnityDBHelper.addMessage(message, SportsUnityDBHelper.MIME_TYPE_TEXT, from, true, time, null, null, null, chatID, SportsUnityDBHelper.DEFAULT_READ_STATUS);
        sportsUnityDBHelper.updateChatEntry(messageId, chatID, groupServerId);

        return success;
    }
}
