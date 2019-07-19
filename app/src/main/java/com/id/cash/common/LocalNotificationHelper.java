package com.id.cash.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.id.cash.BuildConfig;
import com.id.cash.R;

/**
 * Created by linchen on 2018/5/24.
 */

public class LocalNotificationHelper {
    private int notificationId;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;

    public LocalNotificationHelper(Context context, int notificationId) {
        this.notificationId = notificationId;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildNotificationChannel(context);
        }
        builder = new NotificationCompat.Builder(context, context.getString(R.string.default_notification_channel_id));
    }

    public static void cancelAll(Context context) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }
        } catch (Exception ex) {
            LogUtil.e(ex);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void buildNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                context.getString(R.string.default_notification_channel_id),
                context.getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(context.getString(R.string.default_notification_channel_description));
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(channel);
    }

    private NotificationCompat.Builder configBuilder(PendingIntent pendingIntent, int icon, String ticker, String title, String body,
                                                     boolean shouldSound, boolean shouldVibrate, boolean shouldLight) {
        int notificationProperty = Notification.DEFAULT_SOUND;
        if (!shouldSound) {
            notificationProperty = 0;
        }
        if (shouldVibrate) {
            notificationProperty |= Notification.DEFAULT_VIBRATE;
        }
        if (shouldLight) {
            notificationProperty |= Notification.DEFAULT_LIGHTS;
        }

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(icon)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(body)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(notificationProperty);
        return builder;
    }

    public void send(PendingIntent pendingIntent, int icon, String ticker, String title, String body, boolean shouldSound, boolean shouldVibrate, boolean shouldLight) {
        Notification notification = configBuilder(pendingIntent, icon, ticker, title, body, shouldSound, shouldVibrate, shouldLight)
                .build();
        try {
            // on some old Android system, it throws SecurityException: Requires VIBRATE permission
            notificationManager.notify(notificationId, notification);
        } catch (Exception ex) {
            LogUtil.e(ex);
        }
    }
}
