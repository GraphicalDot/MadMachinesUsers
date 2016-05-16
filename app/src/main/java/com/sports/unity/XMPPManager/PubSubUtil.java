package com.sports.unity.XMPPManager;

import android.content.Context;
import android.util.Log;

import com.sports.unity.messages.controller.model.PubSubMessaging;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.EmptyResultIQ;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormNode;
import org.jivesoftware.smackx.pubsub.FormNodeType;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishItem;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.SubscriptionsExtension;
import org.jivesoftware.smackx.pubsub.UnsubscribeExtension;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.util.NodeUtils;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by amandeep on 7/3/16.
 */
public class PubSubUtil {

    public static void initialSetup(){
        ProviderManager.addExtensionProvider( "affiliations", PubSubNamespace.OWNER.getXmlns(), new SPUAffiliationsProvider());
        ProviderManager.addExtensionProvider( "affiliation", PubSubNamespace.OWNER.getXmlns(), new SPUAffiliationProvider());
        ProviderManager.addExtensionProvider( "pubsub", PubSubNamespace.BASIC.getXmlns(), new PubSubExtensionProvider());
//        ProviderManager.addExtensionProvider( "subscription", "", new SubscriptionProvider());
    }

    public static void getSubscribedNodes(Context context) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSubManager pubSubManager = new PubSubManager(XMPPClient.getConnection());
        List<Subscription> subscriptionList = pubSubManager.getSubscriptions();

        for(Subscription subscription : subscriptionList){
            if( subscription.getState() == Subscription.State.subscribed ){
                PubSubMessaging.getInstance().handleCreationOfAlreadySubscribedGroup( context, subscription.getNode());
            }
        }

    }

    public static List<SPUAffiliation> getAffiliations(String nodeId) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSub pubSub = new PubSub( "pubsub.mm.io", IQ.Type.get, PubSubNamespace.OWNER);
        pubSub.addExtension( new SPUAffiliationsExtension(nodeId));

        PubSub reply = null;
        IQ resultIQ = sendPubsubPacket(XMPPClient.getConnection(), pubSub);
        if (resultIQ instanceof EmptyResultIQ) {
            reply = null;
        } else {
            reply = (PubSub)resultIQ;
        }
        SPUAffiliationsExtension affiliationsExtension = (SPUAffiliationsExtension) reply.getExtension("affiliations", PubSubNamespace.OWNER.getXmlns());

        List<SPUAffiliation> list = affiliationsExtension.getAffiliations();
        return list;
    }

    public static List<Subscription> getSubscriptions(String nodeId)  throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
//        return node.getSubscriptions();
        PubSub pubSub = new PubSub( "pubsub.mm.io", IQ.Type.get, PubSubNamespace.OWNER);
        pubSub.addExtension( new NodeExtension(PubSubElementType.SUBSCRIPTIONS, nodeId));

        PubSub reply = null;
        IQ resultIQ = sendPubsubPacket(XMPPClient.getConnection(), pubSub);
        if (resultIQ instanceof EmptyResultIQ) {
            reply = null;
        } else {
            reply = (PubSub)resultIQ;
        }
        SubscriptionsExtension subscriptionsExtension = (SubscriptionsExtension) reply.getExtension("subscriptions", PubSubNamespace.BASIC.getXmlns());

        List<Subscription> list = subscriptionsExtension.getSubscriptions();
        return list;
    }

    public static boolean updateAffiliations(String nodeId, String ownerJID, ArrayList<String> listOfMembers) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSub pubSub = new PubSub( "pubsub.mm.io", IQ.Type.set, PubSubNamespace.OWNER);

        List<SPUAffiliation> affiliationList = new ArrayList<>();
        if(ownerJID != null) {
            affiliationList.add(new SPUAffiliation(ownerJID, SPUAffiliation.Type.owner));
        }
        for(String jid : listOfMembers) {
            affiliationList.add(new SPUAffiliation(jid, SPUAffiliation.Type.publisher));
        }

        SPUAffiliationsExtension affiliationsExtension = new SPUAffiliationsExtension(nodeId, affiliationList);
        pubSub.addExtension( affiliationsExtension);

        boolean success = false;
        IQ reply = sendPubsubPacket(XMPPClient.getConnection(), pubSub);
        if( reply.getType() == IQ.Type.error ){
            success = false;
        } else {
            success = true;
        }

        return success;
    }

    public static boolean updateSubscriptions(String nodeId, String ownerJid, ArrayList<String> listOfSubscribers)  throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSub pubSub = new PubSub( "pubsub.mm.io", IQ.Type.set, PubSubNamespace.OWNER);

        List<Subscription> subscriptionList = new ArrayList<>();
        if(ownerJid != null) {
            subscriptionList.add(new Subscription(ownerJid, nodeId, null, Subscription.State.subscribed));
        }
        for(String jid : listOfSubscribers) {
            subscriptionList.add(new Subscription(jid, nodeId, null, Subscription.State.subscribed));
        }
        SubscriptionsExtension subscriptionsExtension = new SubscriptionsExtension(nodeId, subscriptionList);
        pubSub.addExtension( subscriptionsExtension);

        boolean success = false;
        IQ reply = sendPubsubPacket(XMPPClient.getConnection(), pubSub);
        if( reply.getType() == IQ.Type.error ){
            success = false;
        } else {
            success = true;
        }

        return success;
    }

    public static boolean removeFromGroup(String nodeId, String unsubscriberJid)  throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSub pubSub = new PubSub( "pubsub.mm.io", IQ.Type.set, PubSubNamespace.OWNER);

        List<Subscription> subscriptionList = new ArrayList<>();
        subscriptionList.add(new Subscription(unsubscriberJid, nodeId, null, Subscription.State.none));

        SubscriptionsExtension subscriptionsExtension = new SubscriptionsExtension(nodeId, subscriptionList);
        pubSub.addExtension( subscriptionsExtension);

        boolean success = false;
        IQ reply = sendPubsubPacket(XMPPClient.getConnection(), pubSub);
        if( reply.getType() == IQ.Type.error ){
            success = false;
        } else {
            pubSub = new PubSub( "pubsub.mm.io", IQ.Type.set, PubSubNamespace.OWNER);

            List<SPUAffiliation> affiliationList = new ArrayList<>();
            affiliationList.add(new SPUAffiliation(unsubscriberJid, SPUAffiliation.Type.outcast));

            SPUAffiliationsExtension affiliationsExtension = new SPUAffiliationsExtension(nodeId, affiliationList);
            pubSub.addExtension( affiliationsExtension);

            reply = sendPubsubPacket(XMPPClient.getConnection(), pubSub);
            if( reply.getType() == IQ.Type.error ){
                success = false;
            } else {
                success = true;
            }
        }

        return success;
    }

    public static ConfigureForm getNodeConfig(String nodeId) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSub packet = PubSub.createPubsubPacket("pubsub.mm.io", IQ.Type.get, new NodeExtension(PubSubElementType.CONFIGURE, nodeId), PubSubNamespace.OWNER);
        String stanzaId = packet.getStanzaId();
        IQ reply = sendPubsubPacket(XMPPClient.getConnection(), packet);

        ConfigureForm configureForm = NodeUtils.getFormFromPacket(reply, PubSubElementType.CONFIGURE_OWNER);
        return configureForm;
    }

    public static void sendNodeConfig(String nodeId, ConfigureForm form) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSub packet = PubSub.createPubsubPacket( "pubsub.mm.io", IQ.Type.set, new FormNode(FormNodeType.CONFIGURE_OWNER, nodeId, form), PubSubNamespace.OWNER);
        XMPPClient.getConnection().sendStanza(packet);
    }

    public static String publish(Item item, String nodeId) throws SmackException.NotConnectedException {
        Collection<Item> items = new ArrayList<Item>(1);
        items.add((item == null ? new Item() : item));
        return publish(items, nodeId);
    }

    public static String publish(Collection<Item> items, String nodeId) throws SmackException.NotConnectedException {
        PubSub packet = PubSub.createPubsubPacket( "pubsub.mm.io", IQ.Type.set, new PublishItem<Item>(nodeId, items), null);
        String stanzaId = packet.getStanzaId();
        XMPPClient.getConnection().sendStanza(packet);
        return stanzaId;
    }

    public static String unsubscribe(String jid, String nodeId) throws SmackException.NotConnectedException {
        PubSub packet = PubSub.createPubsubPacket( "pubsub.mm.io", IQ.Type.set, new UnsubscribeExtension( jid, nodeId, null), null);
        String stanzaId = packet.getStanzaId();
        XMPPClient.getConnection().sendStanza(packet);
        return stanzaId;
    }

    static IQ sendPubsubPacket(XMPPConnection con, PubSub packet) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        IQ resultIQ = con.createPacketCollectorAndSend(packet).nextResultOrThrow();
        return resultIQ;
    }

}
