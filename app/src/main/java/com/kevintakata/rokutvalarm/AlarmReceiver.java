package com.kevintakata.rokutvalarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "extras: " + intent.getExtras().toString());
        Log.d(TAG, "onReceive Alarm");
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        String serialNumber = intent.getStringExtra("serial_number");
        String channel = intent.getStringExtra("channel");
        String id = intent.getStringExtra("id");

        String CHANNEL_ID = "AlarmChannelId";

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Alarm", importance);
        notificationChannel.setDescription("Alarm");
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Alarm Title")
                .setContentText("Turning on Roku SN: " + serialNumber)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int NOTIFICATION_ID = 0;

        //Deliver the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        new UPnPDiscovery(context, serialNumber, channel).execute();

        // remove alarm from shared preferences
        SharedPreferences.Editor editor =
                context.getSharedPreferences("alarms", Context.MODE_PRIVATE).edit();
        editor.remove(id);
        editor.apply();
    }
}
