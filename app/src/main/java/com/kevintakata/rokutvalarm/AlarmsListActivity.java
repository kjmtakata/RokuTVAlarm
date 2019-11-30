package com.kevintakata.rokutvalarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class AlarmsListActivity extends AppCompatActivity {

    private static String TAG = AlarmsListActivity.class.getSimpleName();

    private ArrayList<Alarm> mAlarms = new ArrayList<>();
    private AlarmArrayAdapter mAlarmArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms_list);

        ListView listView = findViewById(R.id.list_view);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Device device = mDevices.get(position);
//                Log.d(TAG, device.getLocation());
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("device", device);
//                setResult(Activity.RESULT_OK, returnIntent);
//                finish();
//            }
//        });


        mAlarmArrayAdapter = new AlarmArrayAdapter(this, mAlarms);
        listView.setAdapter(mAlarmArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "delete");
                Alarm alarm = mAlarms.get(position);
                Log.d(TAG, "time: " + alarm.getTime().toString());

                // cancel alarm
                Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                intent.putExtra("serial_number", alarm.getDevice().getSerialNumber());
                intent.putExtra("id", alarm.getId());
                if(PendingIntent.getBroadcast(getBaseContext(),
                        Integer.parseInt(alarm.getId()), intent,
                        PendingIntent.FLAG_NO_CREATE) != null) {
                    Toast.makeText(getBaseContext(), "Alarm Set", Toast.LENGTH_SHORT).show();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d(TAG, "long click");

                AlarmsListActivity.this.startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater inflater = mode.getMenuInflater();
                        inflater.inflate(R.menu.alarm_list_context_menu, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                Log.d(TAG, "delete");
                                Alarm alarm = mAlarms.get(position);
                                Log.d(TAG, "time: " + alarm.getTime().toString());

                                // cancel alarm
                                Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
                                intent.putExtra("serial_number", alarm.getDevice().getSerialNumber());
                                intent.putExtra("id", alarm.getId());
                                PendingIntent alarmIntent = PendingIntent.getBroadcast(getBaseContext(),
                                        Integer.parseInt(alarm.getId()), intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
                                AlarmManager alarmMgr = (AlarmManager)
                                        getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                alarmMgr.cancel(alarmIntent);

                                // remove from list
                                mAlarms.remove(position);
                                mAlarmArrayAdapter.notifyDataSetChanged();

                                // remove from shared preferences
                                SharedPreferences.Editor editor =
                                        getSharedPreferences("alarms", Context.MODE_PRIVATE).edit();
                                editor.remove(alarm.getId());
                                editor.apply();

                                mode.finish();
                                return true;
                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                });
                return false;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmsListActivity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAlarms.clear();

        Map<String,?> preferences =
                getSharedPreferences("alarms", Context.MODE_PRIVATE).getAll();

        Gson gson = new Gson();

        for (Map.Entry<String,?> entry : preferences.entrySet()) {
            mAlarms.add(gson.fromJson((String) entry.getValue(), Alarm.class));
        }

        mAlarmArrayAdapter.notifyDataSetChanged();
    }

}
