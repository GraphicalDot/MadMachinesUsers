package com.sports.unity.XMPPManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.sports.unity.ChatScreenApplication;
import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.ProfileCreationActivity;
import com.sports.unity.R;
import com.sports.unity.common.controller.MainActivity;
import com.sports.unity.common.model.ContactsObserver;
import com.sports.unity.common.model.TinyDB;
import com.sports.unity.messages.controller.activity.ChatScreenActivity;

import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.joda.time.DateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class XMPPService extends Service {
    private XMPPTCPConnection mConnection = null;
    int mNotificationId = 1;
    private SportsUnityDBHelper sportsUnityDBHelper;
    private TinyDB tinyDB;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    onHandleIntent(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Log.i("Onstcmd : ", "called");
        return START_STICKY;
    }

    protected void onHandleIntent(Intent intent) throws IOException, XMPPException, SmackException {
        if (mConnection == null) {
            mConnection = ProfileCreationActivity.returnConnection();
            if (!mConnection.isAuthenticated())
                mConnection.login(tinyDB.getString("username"), tinyDB.getString("password"));
            StanzaFilter filter = new StanzaTypeFilter(Message.class);
            mConnection.addSyncStanzaListener(new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                    Message message = (Message) packet;
                    if (message.getType().equals(Message.Type.chat)) {
                        Object value = JivePropertiesManager.getProperty(packet, "time");
                        addToDatabase(message, value);
                        if (ChatScreenApplication.isActivityVisible()) {
                            if (ChatScreenActivity.getJABBERID().equals(message.getFrom().toString().substring(0, message.getFrom().indexOf("@")))) {
                                Intent intent = new Intent();
                                intent.setAction("com.madmachines.SINGLE_MESSAGE_RECEIVED");
                                sendBroadcast(intent);
                                Log.i("Broadcast : ", "sent");
                            } else {
                                try {
                                    DisplayNotification(message);
                                } catch (XMPPException.XMPPErrorException e) {
                                    e.printStackTrace();
                                } catch (SmackException.NoResponseException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            try {
                                DisplayNotification(message);
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Log.i("personalmessage :", "recieved");
                }
            }, filter);

        }
        mConnection.addConnectionListener(new AbstractConnectionListener() {
            public void connectionClosed() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mConnection = ProfileCreationActivity.returnConnection();
                    }
                });
                Log.i("connection", "closed");
            }

            public void connectionClosedOnError(Exception e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mConnection = ProfileCreationActivity.returnConnection();
                    }
                });
                Log.i("connection", "closed on error");
            }

            public void reconnectionFailed(Exception e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mConnection = ProfileCreationActivity.returnConnection();
                    }
                });
                Log.i("reconnection", "failed");
            }

            public void reconnectionSuccessful() {
                if (mConnection.isAuthenticated()) {
                    Log.i("isAuthenticated", String.valueOf(XMPPClient.getConnection().isAuthenticated()));
                } else {
                    try {
                        mConnection.login(tinyDB.getString("username"), tinyDB.getString("password"));
                        Log.i("isAuthenticated", String.valueOf(XMPPClient.getConnection().isAuthenticated()));
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("reconnection", "succesful");

            }


            public void reconnectingIn(int seconds) {
                Log.i("reconnectingIn", String.valueOf(seconds));
            }

        });

    }

    @Override
    public void onCreate() {
        Log.i("service", "created");
        sportsUnityDBHelper = SportsUnityDBHelper.getInstance(this);
        tinyDB = TinyDB.getInstance(this);
        getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, ContactsObserver.getInstance(new Handler(), this));

    }

    public void addToDatabase(Message message, Object value) {

        DateTime dateTime = new DateTime(value);
        SimpleDateFormat formatter = new SimpleDateFormat("k:mm");
        Log.i("senttime : ", String.valueOf(formatter.format(dateTime.getMillis())));
        sportsUnityDBHelper.addMessage(message.getBody().toString(), message.getFrom().toString().substring(0, message.getFrom().indexOf("@")), false, String.valueOf(formatter.format(dateTime.getMillis())));
    }

    /*private class ChatManagerListenerImpl implements ChatManagerListener {
        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            if (!createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        Log.i("yeah :", "yeah");
                        new SportsUnityDBHelper(getApplicationContext()).
                                addMessage(message.getBody().toString(), message.getFrom().toString().substring(0, message.getFrom().indexOf("@")), false);
                        if (ChatScreenApplication.isActivityVisible()) {
                            if (ChatScreenActivity.getJABBERID().equals(message.getFrom().toString().substring(0, message.getFrom().indexOf("@")))) {
                                Intent intent = new Intent();
                                intent.setAction("com.madmachines.SINGLE_MESSAGE_RECEIVED");
                                sendBroadcast(intent);
                                Log.i("Broadcast : ", "sent");
                            } else {
                                DisplayNotification(message);
                            }
                        } else {
                            DisplayNotification(message);
                        }

                    }
                });
            }

        }
    }*/

    public void DisplayNotification(Message message) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {

        String jabberID = message.getFrom().substring(0, message.getFrom().indexOf("@"));
        String name = sportsUnityDBHelper.getJabberName(jabberID);
        Intent notificationIntent = new Intent(this, ChatScreenActivity.class);
        notificationIntent.putExtra("jid", jabberID);
        notificationIntent.putExtra("jbname", name);
        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, mNotificationId, new Intent[]{backIntent, notificationIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.ic_stat_notification);
        builder.setContentText(message.getBody().toString());
        if (name != null && !name.isEmpty())
            builder.setContentTitle(name);
        else {
            builder.setContentTitle(jabberID);
            VCard card = new VCard();
            card.load(XMPPClient.getConnection(), jabberID + "@mm.io");
            sportsUnityDBHelper.addToContacts(jabberID, jabberID, true, true, card.getMiddleName());

        }
        builder.setContentIntent(pendingIntent);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(mNotificationId, builder.build());
        Log.i("Notification : ", "notified");
        sportsUnityDBHelper.updateUnreadCount(jabberID);
        Intent intent = new Intent();
        intent.setAction("com.madmachine.SINGLE_MESSAGE_RECEIVED");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        mConnection.disconnect();
        Log.i("service :", "destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
