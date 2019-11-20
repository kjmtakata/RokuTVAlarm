package com.kevintakata.rokutvalarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;

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

        SharedPreferences sharedPreferences = context.getSharedPreferences("alarms", Context.MODE_PRIVATE);
        String alarmJson = sharedPreferences.getString(id, null);

        Gson gson = new Gson();
        Alarm alarm = gson.fromJson(alarmJson, Alarm.class);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(!alarm.isOneTime()) {
            alarm.setNextAlarm();
            AlarmManager alarmMgr = (AlarmManager)
                    context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
            intent.putExtra("serial_number", alarm.getDevice().getSerialNumber());
            intent.putExtra("id", alarm.getId());
            intent.putExtra("channel", alarm.getChannel());
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
                    Integer.parseInt(alarm.getId()), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getTime().getTimeInMillis(), alarmIntent);
            editor.putString(alarm.getId(), gson.toJson(alarm));

            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM hh:mm a");

            Log.d(TAG, "Alarm time: " + sdf.format(alarm.getTime().getTime()));
        } else {
            editor.remove(id);
        }


        editor.apply();

    }
}
