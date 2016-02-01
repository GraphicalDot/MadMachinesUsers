package com.sports.unity.XMPPManager;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smack.util.XmlStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by amandeep on 10/12/15.
 */
public class ReadReceipt implements ExtensionElement {

    public static final String NAMESPACE = "urn:xmpp:read";
    public static final String ELEMENT = "received";

    /**
     * original ID of the delivered message
     */
    private final String id;

    public ReadReceipt(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String getElementName()
    {
        return ELEMENT;
    }

    @Override
    public String getNamespace()
    {
        return NAMESPACE;
    }

    @Override
    public XmlStringBuilder toXML()
    {
        XmlStringBuilder xml = new XmlStringBuilder(this);
        xml.attribute("id", id);
        xml.closeEmptyElement();
        return xml;
    }

    /**
     * Get the {@link ReadReceipt} extension of the packet, if any.
     *
     * @param p the packet
     * @return the {@link ReadReceipt} extension or {@code null}
     * @deprecated use {@link #from(Message)} instead
     */
    @Deprecated
    public static ReadReceipt getFrom(Message p) {
        return from(p);
    }

    /**
     * Get the {@link ReadReceipt} extension of the message, if any.
     *
     * @param message the message.
     * @return the {@link ReadReceipt} extension or {@code null}
     */
    public static ReadReceipt from(Message message) {
        return message.getExtension(ELEMENT, NAMESPACE);
    }

    /**
     * This Provider parses and returns DeliveryReceipt packets.
     */
    public static class Provider extends EmbeddedExtensionProvider<ReadReceipt> {

        @Override
        protected ReadReceipt createReturnExtension(String currentElement, String currentNamespace,
                                                        Map<String, String> attributeMap, List<? extends ExtensionElement> content)
        {
            return new ReadReceipt(attributeMap.get("id"));
        }

    }

}
