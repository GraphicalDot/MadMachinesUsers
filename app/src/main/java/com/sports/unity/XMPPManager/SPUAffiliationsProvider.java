package com.sports.unity.XMPPManager;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by amandeep on 8/3/16.
 */
public class SPUAffiliationsProvider extends EmbeddedExtensionProvider<SPUAffiliationsExtension> {

    @SuppressWarnings("unchecked")
    @Override
    protected SPUAffiliationsExtension createReturnExtension(String currentElement, String currentNamespace, Map<String, String> attributeMap, List<? extends ExtensionElement> content)
    {
        return new SPUAffiliationsExtension( attributeMap.get("node"), (List<SPUAffiliation>)content);
    }

}
