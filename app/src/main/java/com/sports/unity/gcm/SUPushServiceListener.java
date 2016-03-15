package com.sports.unity.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.sports.unity.scores.ScoreDetailActivity;

import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;

/**
 * Created by Manish on 04-12-2015.
 */
public class SUPushServiceListener extends GcmListenerService
{
    @Override
    public void onMessageReceived(String from, Bundle data)
    {
        super.onMessageReceived(from, data);
        //Apptenti.setPendingPushNotification(getApplicationContext(), data);

        Intent intent = new Intent(this, ScoreDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent,
                                                                PendingIntent.FLAG_ONE_SHOT);

        String title           = data.getString("gcm.notification.title");
        String body            = data.getString("gcm.notification.body");
        Uri    defaultSoundUri = getDefaultUri(TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
