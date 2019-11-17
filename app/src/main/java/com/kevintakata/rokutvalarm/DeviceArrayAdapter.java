package com.kevintakata.rokutvalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceArrayAdapter extends ArrayAdapter<Device> {
    public DeviceArrayAdapter(Context context, ArrayList<Device> devices) {
        super(context, android.R.layout.simple_list_item_1, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Device device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1,
                    parent, false);
        }

        TextView nameTextView = convertView.findViewById(android.R.id.text1);
        nameTextView.setText(device.getName());

        return convertView;
    }
}
