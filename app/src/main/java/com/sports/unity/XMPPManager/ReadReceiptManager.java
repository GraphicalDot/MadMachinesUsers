package com.sports.unity.XMPPManager;

import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by amandeep on 10/12/15.
 */
public class ReadReceiptManager extends Manager {

//    private static final StanzaFilter MESSAGES_WITH_DEVLIERY_RECEIPT_REQUEST = new AndFilter(StanzaTypeFilter.MESSAGE,
//            new StanzaExtensionFilter(new DeliveryReceiptRequest()));
    private static final StanzaFilter MESSAGES_WITH_READ_RECEIPT = new AndFilter(StanzaTypeFilter.MESSAGE,
            new StanzaExtensionFilter(ReadReceipt.ELEMENT, ReadReceipt.NAMESPACE));

    private static Map<XMPPConnection, ReadReceiptManager> instances = new WeakHashMap<XMPPConnection, ReadReceiptManager>();

    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            public void connectionCreated(XMPPConnection connection) {
                getInstanceFor(connection);
            }
        });
    }

//    /**
//     * Specifies when incoming message delivery receipt requests should be automatically
//     * acknowledged with an receipt.
//     */
//    public enum AutoReceiptMode {
//
//        /**
//         * Never send deliver receipts
//         */
//        disabled,
//
//        /**
//         * Only send delivery receipts if the requester is subscribed to our presence.
//         */
//        ifIsSubscribed,
//
//        /**
//         * Always send delivery receipts. <b>Warning:</b> this may causes presence leaks. See <a
//         * href="http://xmpp.org/extensions/xep-0184.html#security">XEP-0184: Message Delivery
//         * Receipts § 8. Security Considerations</a>
//         */
//        always,
//    }

//    private static AutoReceiptMode defaultAutoReceiptMode = AutoReceiptMode.ifIsSubscribed;
//
//    /**
//     * Set the default automatic receipt mode for new connections.
//     *
//     * @param autoReceiptMode the default automatic receipt mode.
//     */
//    public static void setDefaultAutoReceiptMode(AutoReceiptMode autoReceiptMode) {
//        defaultAutoReceiptMode = autoReceiptMode;
//    }
//
//    private AutoReceiptMode autoReceiptMode = defaultAutoReceiptMode;

    private final Set<ReadReceiptReceivedListener> receiptReceivedListeners = new CopyOnWriteArraySet<ReadReceiptReceivedListener>();

    private ReadReceiptManager(XMPPConnection connection) {
        super(connection);
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);
        sdm.addFeature(ReadReceipt.NAMESPACE);

        // Add the packet listener to handling incoming delivery receipts
        connection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                ReadReceipt dr = ReadReceipt.from((Message) packet);
                // notify listeners of incoming receipt
                for (ReadReceiptReceivedListener l : receiptReceivedListeners) {
                    ReadReceipt readReceipt = (ReadReceipt)packet.getExtension(ReadReceipt.NAMESPACE);
                    l.onReceiptReceived(packet.getFrom(), packet.getTo(), readReceipt.getId());
                }
            }
        }, MESSAGES_WITH_READ_RECEIPT);

//        // Add the packet listener to handle incoming delivery receipt requests
//        connection.addAsyncStanzaListener(new StanzaListener() {
//            @Override
//            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
//                final String from = packet.getFrom();
//                final XMPPConnection connection = connection();
//                switch (autoReceiptMode) {
//                    case disabled:
//                        return;
//                    case ifIsSubscribed:
//                        if (!Roster.getInstanceFor(connection).isSubscribedToMyPresence(from)) {
//                            return;
//                        }
//                        break;
//                    case always:
//                        break;
//                }
//
//                final Message messageWithReceiptRequest = (Message) packet;
//                Message ack = receiptMessageFor(messageWithReceiptRequest);
//                connection.sendStanza(ack);
//            }
//        }, MESSAGES_WITH_DEVLIERY_RECEIPT_REQUEST);
    }

    /**
     * Obtain the DeliveryReceiptManager responsible for a connection.
     *
     * @param connection the connection object.
     *
     * @return the DeliveryReceiptManager instance for the given connection
     */
    public static synchronized ReadReceiptManager getInstanceFor(XMPPConnection connection) {
        ReadReceiptManager receiptManager = instances.get(connection);

        if (receiptManager == null) {
            receiptManager = new ReadReceiptManager(connection);
            instances.put(connection, receiptManager);
        }

        return receiptManager;
    }

    /**
     * Returns true if Delivery Receipts are supported by a given JID
     *
     * @param jid
     * @return true if supported
     * @throws SmackException if there was no response from the server.
     * @throws XMPPException
     */
    public boolean isSupported(String jid) throws SmackException, XMPPException {
        return ServiceDiscoveryManager.getInstanceFor(connection()).supportsFeature(jid,
                ReadReceipt.NAMESPACE);
    }

//    /**
//     * Configure whether the {@link ReadReceiptManager} should automatically
//     * reply to incoming {@link ReadReceipt}s.
//     *
//     * @param autoReceiptMode the new auto receipt mode.
//     * @see AutoReceiptMode
//     */
//    public void setAutoReceiptMode(AutoReceiptMode autoReceiptMode) {
//        this.autoReceiptMode = autoReceiptMode;
//    }
//
//    /**
//     * Get the currently active auto receipt mode.
//     *
//     * @return the currently active auto receipt mode.
//     */
//    public AutoReceiptMode getAutoReceiptMode() {
//        return autoReceiptMode;
//    }

    /**
     * Get informed about incoming delivery receipts with a {@link ReadReceiptReceivedListener}.
     *
     * @param listener the listener to be informed about new receipts
     */
    public void addReadReceiptReceivedListener(ReadReceiptReceivedListener listener) {
        receiptReceivedListeners.add(listener);
    }

    /**
     * Stop getting informed about incoming delivery receipts.
     *
     * @param listener the listener to be removed
     */
    public void removeReadReceiptReceivedListener(ReadReceiptReceivedListener listener) {
        receiptReceivedListeners.remove(listener);
    }

//    /**
//     * A filter for stanzas to request delivery receipts for. Notably those are message stanzas of type normal, chat or
//     * headline, which <b>do not</b>contain a delivery receipt, i.e. are ack messages.
//     *
//     * @see <a href="http://xmpp.org/extensions/xep-0184.html#when-ack">XEP-184 § 5.4 Ack Messages</a>
//     */
//    private static final StanzaFilter MESSAGES_TO_REQUEST_RECEIPTS_FOR = new AndFilter(
//            MessageTypeFilter.NORMAL_OR_CHAT_OR_HEADLINE, new NotFilter(new StanzaExtensionFilter(
//            ReadReceipt.ELEMENT, ReadReceipt.NAMESPACE)));
//
//    private static final StanzaListener AUTO_ADD_DELIVERY_RECEIPT_REQUESTS_LISTENER = new StanzaListener() {
//        @Override
//        public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
//            Message message = (Message) packet;
//            DeliveryReceiptRequest.addTo(message);
//        }
//    };
//
//    /**
//     * Enables automatic requests of delivery receipts for outgoing messages of type 'normal', 'chat' or 'headline.
//     *
//     * @since 4.1
//     * @see #dontAutoAddDeliveryReceiptRequests()
//     */
//    public void autoAddDeliveryReceiptRequests() {
//        connection().addPacketInterceptor(AUTO_ADD_DELIVERY_RECEIPT_REQUESTS_LISTENER,
//                MESSAGES_TO_REQUEST_RECEIPTS_FOR);
//    }
//
//    /**
//     * Disables automatically requests of delivery receipts for outgoing messages.
//     *
//     * @since 4.1
//     * @see #autoAddDeliveryReceiptRequests()
//     */
//    public void dontAutoAddDeliveryReceiptRequests() {
//        connection().removePacketInterceptor(AUTO_ADD_DELIVERY_RECEIPT_REQUESTS_LISTENER);
//    }

//    /**
//     * Test if a message requires a delivery receipt.
//     *
//     * @param message Stanza(/Packet) object to check for a DeliveryReceiptRequest
//     *
//     * @return true if a delivery receipt was requested
//     */
//    public static boolean hasDeliveryReceiptRequest(Message message) {
//        return (DeliveryReceiptRequest.from(message) != null);
//    }
//
//    /**
//     * Add a delivery receipt request to an outgoing packet.
//     *
//     * Only message packets may contain receipt requests as of XEP-0184,
//     * therefore only allow Message as the parameter type.
//     *
//     * @param m Message object to add a request to
//     * @return the Message ID which will be used as receipt ID
//     * @deprecated use {@link DeliveryReceiptRequest#addTo(Message)}
//     */
//    @Deprecated
//    public static String addDeliveryReceiptRequest(Message m) {
//        return DeliveryReceiptRequest.addTo(m);
//    }

    /**
     * Create and return a new message including a delivery receipt extension for the given message.
     *
     * @param messageWithReceiptRequest the given message with a receipt request extension.
     * @return a new message with a receipt.
     * @since 4.1
     */
    public static Message receiptMessageFor(Message messageWithReceiptRequest) {
        Message message = new Message(messageWithReceiptRequest.getFrom(), messageWithReceiptRequest.getType());
        message.addExtension(new ReadReceipt(messageWithReceiptRequest.getStanzaId()));
        return message;
    }

}
