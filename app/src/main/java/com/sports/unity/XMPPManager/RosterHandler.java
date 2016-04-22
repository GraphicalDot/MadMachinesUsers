package com.sports.unity.XMPPManager;

import android.content.Context;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.util.GlobalEventListener;
import com.sports.unity.util.ThreadTask;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by madmachines on 21/4/16.
 */
public class RosterHandler {

    private static RosterHandler rosterHandler;

    synchronized public static RosterHandler getInstance(Context context) {
        if (rosterHandler == null) {
            rosterHandler = new RosterHandler(context);
        }
        return rosterHandler;
    }

    private Roster roster;
    private Context context;

    private RosterLoadedListener rosterLoadedListener = new RosterLoadedListener() {

        @Override
        public void onRosterLoaded(Roster roster) {
            ThreadTask threadTask = new ThreadTask(null) {

                @Override
                public Object process() {
                    checkForPendingEntriesToBeAddedInRoster();
                    return null;
                }

                @Override
                public void postAction(Object object) {

                }

            };
            threadTask.start();
        }

    };

    private RosterListener rosterListener = new RosterListener() {

        @Override
        public void entriesAdded(Collection<String> addresses) {

        }

        @Override
        public void entriesUpdated(Collection<String> addresses) {

        }

        @Override
        public void entriesDeleted(Collection<String> addresses) {

        }

        @Override
        public void presenceChanged(Presence presence) {
            if (presence.getType().equals(Presence.Type.available)) {
                // update status for user on chat screen
            } else {
                //unregistered user will get presence type unavailable
                //do nothing
            }
        }

    };

    private RosterHandler(Context context) {
        this.context = context;
    }

    private void checkForPendingEntriesToBeAddedInRoster() {
        ArrayList<String> pendingJIDsToBeAddedInRoster = SportsUnityDBHelper.getInstance(context).getPendingRosterEntries();
        if (pendingJIDsToBeAddedInRoster.size() > 0) {
            addEntriesToRoster(pendingJIDsToBeAddedInRoster);
        } else {
            //nothing
        }
    }

    public Roster loadRoster(XMPPConnection connection) {
        roster = Roster.getInstanceFor(connection);
        roster.addRosterLoadedListener(rosterLoadedListener);
        roster.addRosterListener(rosterListener);
        return roster;
    }

    private void addEntriesToRoster(ArrayList<String> userJIDs) {
        if ( ! roster.isLoaded() ) {
//            try {
//                roster.reloadAndWait();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
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

}
