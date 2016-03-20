package com.sports.unity.XMPPManager;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smackx.pubsub.EmbeddedPacketExtension;
import org.jivesoftware.smackx.pubsub.EventElementType;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;

import java.util.Arrays;
import java.util.List;

/**
 * Created by amandeep on 18/3/16.
 */
public class PubSubExtension implements EmbeddedPacketExtension {
    private EventElementType type;
    private NodeExtension ext;

    public PubSubExtension(EventElementType eventType, NodeExtension eventExt) {
        type = eventType;
        ext = eventExt;
    }

    public EventElementType getEventType() {
        return type;
    }

    public List<ExtensionElement> getExtensions() {
        return Arrays.asList(new ExtensionElement[]{getEvent()});
    }

    public NodeExtension getEvent() {
        return ext;
    }

    public String getElementName() {
        return "pubsub";
    }

    public String getNamespace() {
        return PubSubNamespace.BASIC.getXmlns();
    }

    public String toXML() {
        StringBuilder builder = new StringBuilder("<pubsub xmlns='" + PubSubNamespace.BASIC.getXmlns() + "'>");

        builder.append(ext.toXML());
        builder.append("</pubsub>");
        return builder.toString();
    }

}
