package com.project.tenvinc.bluetoothreminder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class BeaconOorNotifHelper extends ContextWrapper {

    public static final String CHANNEL_ID = "Channel Id";
    public static final String CHANNEL_ID_NAME = "Main Channel";
    public static final String defaultMessageFormat = "\"%s\" is out of range!";
    public static int notificationImageId = R.drawable.ic_warning;

    private static String defaultTitle = "Registered beacon out of range";
    private static int notificationID = 123;

    private NotificationManager manager;
    private NotificationManagerCompat managerCompat;
    private NotificationCompat.Builder builder;


    public BeaconOorNotifHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        managerCompat = NotificationManagerCompat.from(base);
        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(R.color.colorPrimary);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getChannelNotificationBuilder(String beaconName) {
        builder.setContentTitle(defaultTitle)
                .setContentText(String.format(defaultMessageFormat, beaconName))
                .setSmallIcon(notificationImageId);
        return builder;
    }

    public void sendNotification(Notification notification) {
        managerCompat.notify(notificationID, notification);
    }
}
