package com.example.eugenedolgushev.workhub;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.eugenedolgushev.workhub.Activities.MyReservationsActivity;

import java.util.Random;

public class TimeNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Random rand = new Random();
        String txtName = intent.getStringExtra("name");
        String day = intent.getStringExtra("day");
        Intent notificationIntent = new Intent(context, MyReservationsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Праздник")
                .setContentText(day + " у " + txtName + " День Рождение! :)"); // Текст уведомления

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int mineRow = rand.nextInt(100000);
        notificationManager.notify(mineRow, notification);
    }
}
