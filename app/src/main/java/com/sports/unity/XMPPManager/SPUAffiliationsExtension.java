package com.sports.unity.XMPPManager;

import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;

import java.util.Collections;
import java.util.List;

public class SPUAffiliationsExtension extends NodeExtension {
    protected List<SPUAffiliation> items = Collections.emptyList();

    public SPUAffiliationsExtension(String nodeId) {
        super(PubSubElementType.AFFILIATIONS, nodeId);
    }

    public SPUAffiliationsExtension(String nodeId, List<SPUAffiliation> subList) {
        super(PubSubElementType.AFFILIATIONS, nodeId);
        items = subList;
    }

    public List<SPUAffiliation> getAffiliations() {
        return items;
    }

    @Override
    public String getNamespace() {
        return PubSubNamespace.OWNER.getXmlns();
    }

    @Override
    public CharSequence toXML() {
        if ((items == null) || (items.size() == 0)) {
            return super.toXML();
        } else {
            StringBuilder builder = new StringBuilder("<");
            builder.append(getElementName()+ (getNode() == null ? "" : " node='" + getNode() + '\''));
            builder.append(">");

            for (SPUAffiliation item : items) {
                builder.append(item.toXML());
            }

            builder.append("</");
            builder.append(getElementName());
            builder.append(">");
            return builder.toString();
        }
    }

}