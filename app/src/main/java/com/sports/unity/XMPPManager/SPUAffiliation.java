package com.sports.unity.XMPPManager;

import org.jivesoftware.smack.packet.ExtensionElement;

public class SPUAffiliation implements ExtensionElement
{
    protected String jid;
    protected Type type;

    public enum Type {
        member, none, outcast, owner, publisher
    }

    public SPUAffiliation(String jid, Type affiliation) {
        this.jid = jid;
        type = affiliation;
    }

    public String getJid() {
        return jid;
    }

    public Type getType()
    {
        return type;
    }

    public String getElementName()
    {
        return "affiliation";
    }

    public String getNamespace()
    {
        return null;
    }

    public String toXML()
    {
        StringBuilder builder = new StringBuilder("<");
        builder.append(getElementName());
        appendAttribute(builder, "jid", jid);
        appendAttribute(builder, "affiliation", type.toString());

        builder.append("/>");
        return builder.toString();
    }

    private void appendAttribute(StringBuilder builder, String att, String value)
    {
        builder.append(" ");
        builder.append(att);
        builder.append("='");
        builder.append(value);
        builder.append("'");
    }
}
