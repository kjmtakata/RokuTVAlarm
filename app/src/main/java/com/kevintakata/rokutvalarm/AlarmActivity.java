package com.kevintakata.rokutvalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class AlarmActivity extends AppCompatActivity {

    private static String TAG = AlarmActivity.class.getSimpleName();
    private static final int DEVICE_REQUEST = 1;
    private ArrayAdapter<Device> mArrayAdapter;
    private ArrayList<Device> mDevices;
    private TimePicker mTimePicker;
    private EditText mChannelEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Spinner deviceSpinner = findViewById(R.id.device_spinner);

        mDevices = new ArrayList<>();
        Device device = new Device();
        device.setName("Select a Device");
        mDevices.add(device);
        mArrayAdapter = new DeviceArrayAdapter(this, mDevices);
        deviceSpinner.setAdapter(mArrayAdapter);

        mTimePicker = findViewById(R.id.time_picker);

        mChannelEditText = findViewById(R.id.channel_picker);

        deviceSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(AlarmActivity.this, RokuDeviceSelectActivity.class);
                    startActivityForResult(intent, DEVICE_REQUEST);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alarm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
                calendar.set(Calendar.MINUTE, mTimePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                if(calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }

                SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

                Log.d(TAG, "Alarm time: " + sdf.format(calendar.getTime()));
                Log.d(TAG, "Channel: " + mChannelEditText.getText().toString());

                Alarm alarm = new Alarm();
                alarm.setId(String.valueOf(Math.toIntExact(calendar.getTimeInMillis()/1000)));
                alarm.setAppId("tvinput.dtv?ch=5.1");
                alarm.setDevice(mDevices.get(0));
                alarm.setTime(calendar);
                alarm.setChannel(mChannelEditText.getText().toString());

//                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Toast.makeText(getApplicationContext(), "Alarm Set", Toast.LENGTH_LONG).show();
                AlarmManager alarmMgr = (AlarmManager)
                        getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                intent.putExtra("serial_number", alarm.getDevice().getSerialNumber());
                intent.putExtra("id", alarm.getId());
                intent.putExtra("channel", alarm.getChannel());
                PendingIntent alarmIntent = PendingIntent.getBroadcast(getBaseContext(),
                        Integer.parseInt(alarm.getId()), intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm.getTime().getTimeInMillis(), alarmIntent);
//                alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alarmIntent);

                Gson gson = new Gson();

                SharedPreferences sharedPref = getSharedPreferences("alarms", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(alarm.getId(), gson.toJson(alarm));
                editor.commit();

                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DEVICE_REQUEST && resultCode == Activity.RESULT_OK) {
            mDevices.clear();
            Device device = (Device) data.getSerializableExtra("device");
            mDevices.add(device);
            mArrayAdapter.notifyDataSetChanged();
        }
    }
}
