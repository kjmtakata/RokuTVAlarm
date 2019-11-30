package com.kevintakata.rokutvalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Map;

public class BootReceiver extends BroadcastReceiver {
    private static String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("alarms", Context.MODE_PRIVATE);
            Map<String, ?> preferences = sharedPreferences.getAll();
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Gson gson = new Gson();

            for (Map.Entry<String, ?> entry : preferences.entrySet()) {
                Alarm alarm = gson.fromJson((String) entry.getValue(), Alarm.class);

                if (alarm.isOneTime() && alarm.isInPast()) {
                    editor.remove(alarm.getId());
                } else {
                    alarm.setNextAlarm();
                    AlarmManager alarmMgr = (AlarmManager)
                            context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
                    intent.putExtra("serial_number", alarm.getDevice().getSerialNumber());
                    intent.putExtra("id", alarm.getId());
                    intent.putExtra("channel", alarm.getChannel());
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                            Integer.parseInt(alarm.getId()), intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.getTime().getTimeInMillis(), alarmIntent);
                    editor.putString(alarm.getId(), gson.toJson(alarm));

                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM hh:mm a");

                    Log.d(TAG, "Alarm time: " + sdf.format(alarm.getTime().getTime()));
                }
            }

            editor.apply();

            Toast.makeText(context, "Roku TV Alarms Reset", Toast.LENGTH_SHORT).show();
        }

    }
}
