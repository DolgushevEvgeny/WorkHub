package com.example.eugenedolgushev.workhub;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Random;

public class TimeNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Random rand = new Random();
        String date = intent.getStringExtra("date");
        String officeName = intent.getStringExtra("officeName");
        String officeAddress = intent.getStringExtra("officeAddress");
        Integer startTime = intent.getIntExtra("startTime", 0);
        Integer duration = intent.getIntExtra("duration", 0);
        //Intent notificationIntent = new Intent(context, MyReservationsActivity.class);
        Intent notificationIntent = new Intent();
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        String content = date + " у вас забронировано посещение офиса " + officeName
                + " по адресу: " + officeAddress + " c " + startTime + " до " + (startTime + duration);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("Посещение офиса");

        Notification notification = new Notification.BigTextStyle(builder).bigText(content).build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int mineRow = rand.nextInt(100000);
        notificationManager.notify(mineRow, notification);
    }
}
