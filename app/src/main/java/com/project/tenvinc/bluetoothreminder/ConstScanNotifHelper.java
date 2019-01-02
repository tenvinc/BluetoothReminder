package com.project.tenvinc.bluetoothreminder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.project.tenvinc.bluetoothreminder.activities.MainActivity;

public class ConstScanNotifHelper extends ContextWrapper {

    public static final String CHANNEL_ID = "Enable foreground channel id";
    public static final String CHANNEL_ID_NAME = "Foreground channel";
    public static final String CHANNEL_DESC = "This channel enables an always on notification to notify user of a " +
            "foreground service.";
    private static final String defaultTitle = "Always ON service monitoring nearby beacons";
    public static int notificationImageId = R.drawable.ic_launcher;
    private NotificationManager manager;
    private NotificationCompat.Builder builder;

    public ConstScanNotifHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_DESC);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getChannelNotificationBuilder() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return builder.setContentTitle(defaultTitle)
                .setSmallIcon(notificationImageId)
                .setContentIntent(pendingIntent);
    }
}
