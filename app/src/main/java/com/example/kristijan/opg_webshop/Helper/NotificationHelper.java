package com.example.kristijan.opg_webshop.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import com.example.kristijan.opg_webshop.R;

public class NotificationHelper extends ContextWrapper {

    private static final String OPG_CHANEL_ID="com.example.kristijan.opg_webshop.OPG";
    private static final String OPG_CHANEL_NAME="OPG WebShop";

    private  NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel OPGChannel=new NotificationChannel(OPG_CHANEL_ID,OPG_CHANEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        OPGChannel.enableLights(true);
        OPGChannel.setLightColor(Color.WHITE);
        OPGChannel.enableVibration(true);
        OPGChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        OPGChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(OPGChannel);
    }

    public NotificationManager getManager() {

        if(manager==null)
        {
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return manager;
    }
    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOPGWebShopChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),OPG_CHANEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .setSmallIcon(R.drawable.grape)
                .setSound(soundUri)
                .setAutoCancel(true);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOPGWebShopChannelNotification(String title, String body,Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),OPG_CHANEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .setSmallIcon(R.drawable.grape)
                .setSound(soundUri)
                .setAutoCancel(true);
    }

}
