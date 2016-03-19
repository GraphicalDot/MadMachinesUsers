package com.sports.unity.XMPPManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.EmptyResultIQ;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PublishItem;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.SubscriptionsExtension;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;

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

    public static List<SPUAffiliation> getAffiliations(LeafNode node) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSub pubSub = new PubSub( "pubsub.mm.io", IQ.Type.get, PubSubNamespace.OWNER);
        pubSub.addExtension( new SPUAffiliationsExtension(node.getId()));

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

    public static List<Subscription> getSubscriptions(LeafNode node)  throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
//        return node.getSubscriptions();
        PubSub pubSub = new PubSub( "pubsub.mm.io", IQ.Type.get, PubSubNamespace.OWNER);
        pubSub.addExtension( new NodeExtension(PubSubElementType.SUBSCRIPTIONS, node.getId()));

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

    public static boolean updateAffiliations(LeafNode node, String ownerJID, ArrayList<String> listOfMembers) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSub pubSub = new PubSub( "pubsub.mm.io", IQ.Type.set, PubSubNamespace.OWNER);

        List<SPUAffiliation> affiliationList = new ArrayList<>();
        if(ownerJID != null) {
            affiliationList.add(new SPUAffiliation(ownerJID, SPUAffiliation.Type.owner));
        }
        for(String jid : listOfMembers) {
            affiliationList.add(new SPUAffiliation(jid, SPUAffiliation.Type.publisher));
        }

        SPUAffiliationsExtension affiliationsExtension = new SPUAffiliationsExtension(node.getId(), affiliationList);
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

    public static boolean updateSubscriptions(LeafNode node, String ownerJid, ArrayList<String> listOfSubscribers)  throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        PubSub pubSub = new PubSub( "pubsub.mm.io", IQ.Type.set, PubSubNamespace.OWNER);

        List<Subscription> subscriptionList = new ArrayList<>();
        if(ownerJid != null) {
            subscriptionList.add(new Subscription(ownerJid, node.getId(), null, Subscription.State.subscribed));
        }
        for(String jid : listOfSubscribers) {
            subscriptionList.add(new Subscription(jid, node.getId(), null, Subscription.State.subscribed));
        }
        SubscriptionsExtension subscriptionsExtension = new SubscriptionsExtension(node.getId(), subscriptionList);
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

    public static String publish(Item item, LeafNode node) throws SmackException.NotConnectedException {
        Collection<Item> items = new ArrayList<Item>(1);
        items.add((item == null ? new Item() : item));
        return publish(items, node);
    }

    public static String publish(Collection<Item> items, LeafNode node) throws SmackException.NotConnectedException {
        PubSub packet = PubSub.createPubsubPacket( "pubsub.mm.io", IQ.Type.set, new PublishItem<Item>(node.getId(), items), null);
        String stanzaId = packet.getStanzaId();
        XMPPClient.getConnection().sendStanza(packet);
        return stanzaId;
    }

    static IQ sendPubsubPacket(XMPPConnection con, PubSub packet) throws SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        IQ resultIQ = con.createPacketCollectorAndSend(packet).nextResultOrThrow();
        return resultIQ;
    }

}
