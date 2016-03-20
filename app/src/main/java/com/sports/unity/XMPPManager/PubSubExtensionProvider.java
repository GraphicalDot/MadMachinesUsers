package com.sports.unity.XMPPManager;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smackx.pubsub.EventElementType;
import org.jivesoftware.smackx.pubsub.Subscription;

import java.util.List;
import java.util.Map;

/**
 * Created by amandeep on 18/3/16.
 */
public class PubSubExtensionProvider extends EmbeddedExtensionProvider<PubSubExtension> {

    @SuppressWarnings("unchecked")
    @Override
    protected PubSubExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content)
    {
        Subscription subscription = null;
        if( content.size() == 1 ){
            subscription = (Subscription)content.get(0);
        }

        return new PubSubExtension(EventElementType.subscription, subscription);
    }

}
