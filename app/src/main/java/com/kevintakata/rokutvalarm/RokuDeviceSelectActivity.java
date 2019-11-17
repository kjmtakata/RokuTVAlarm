package com.kevintakata.rokutvalarm;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class RokuDeviceSelectActivity extends AppCompatActivity {

    private static String TAG = RokuDeviceSelectActivity.class.getSimpleName();
    private ArrayAdapter<Device> mArrayAdapter;
    private ArrayList<Device> mDevices;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new UPnPDiscovery(this).execute();

        ListView listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = mDevices.get(position);
                Log.d(TAG, device.getLocation());
                Intent returnIntent = new Intent();
                returnIntent.putExtra("device", device);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        mDevices = new ArrayList<>();
        mArrayAdapter = new DeviceArrayAdapter(this, mDevices);
        listView.setAdapter(mArrayAdapter);

        mSwipeRefreshLayout = findViewById(R.id.pullToRefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDevices.clear();
                mArrayAdapter.notifyDataSetChanged();
                new UPnPDiscovery(RokuDeviceSelectActivity.this).execute();
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);
    }

    protected void addDevice(Device device) {
        mDevices.add(device);
        mArrayAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
