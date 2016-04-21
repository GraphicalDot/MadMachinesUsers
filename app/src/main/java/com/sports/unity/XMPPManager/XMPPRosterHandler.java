package com.sports.unity.XMPPManager;

import android.content.Context;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.util.GlobalEventListener;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by madmachines on 21/4/16.
 */
public class XMPPRosterHandler implements GlobalEventListener {

    private static XMPPRosterHandler rosterHandler;

    public static XMPPRosterHandler getInstance(Context context) {
        if (rosterHandler == null) {
            rosterHandler = new XMPPRosterHandler(context);
        }
        return rosterHandler;
    }

    public XMPPRosterHandler(Context context) {
        this.context = context;
    }

    private Roster roster;
    private Context context;

    private void checkforPendingEntriesToBeAddedInRoster() {
        ArrayList<String> pendingJIDsToBeAddedInRoster = SportsUnityDBHelper.getInstance(context).getPendingRosterEntries();
        if (pendingJIDsToBeAddedInRoster.size() > 0) {
            addEntriesToRoster(pendingJIDsToBeAddedInRoster);
        }
    }

    public Roster loadRoster(XMPPConnection connection) {
        roster = Roster.getInstanceFor(connection);             // will never return null
        roster.addRosterLoadedListener(rosterLoadedListener);
        roster.addRosterListener(rosterListener);
        return roster;
    }

    public void addEntriesToRoster(ArrayList<String> userJIDs) {
        if (!roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (userJIDs.size() > 0) {
                for (String jid : userJIDs) {
                    try {
                        roster.createEntry(jid.concat("@mm.io"), null, null);
                        SportsUnityDBHelper.getInstance(context).updateRosterEntryinDatabase(jid, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    RosterLoadedListener rosterLoadedListener = new RosterLoadedListener() {
        @Override
        public void onRosterLoaded(Roster roster) {
            // TODO
        }
    };

    RosterListener rosterListener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> addresses) {
            for (String entry : addresses) {
                System.out.println(entry);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {

        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {

        }

        @Override
        public void presenceChanged(Presence presence) {
            System.out.println("Presence changed: " + presence.getFrom() + " " + presence.getType());
            if (presence.getType().equals(Presence.Type.available)) {
                // update status for user on chat screen
            } else {
                //unregistered user will get presence type unavailable
                //do nothing
            }
        }
    };

    @Override
    public void onInternetStateChanged(boolean connected) {

    }

    @Override
    public void onXMPPServiceAuthenticated(boolean connected, XMPPConnection connection) {

    }

    @Override
    public void onReconnecting(int seconds) {
        // reload roster after reconnection
    }


}
