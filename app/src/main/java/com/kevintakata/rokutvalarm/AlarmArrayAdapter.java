package com.kevintakata.rokutvalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AlarmArrayAdapter extends ArrayAdapter<Alarm> {
    AlarmArrayAdapter(Context context, ArrayList<Alarm> alarms) {
        super(context, android.R.layout.simple_list_item_1, alarms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Alarm alarm = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1,
                    parent, false);
        }

        TextView nameTextView = convertView.findViewById(android.R.id.text1);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        nameTextView.setText(sdf.format(alarm.getTime().getTime()) + " : " +
                alarm.getDevice().getName() + " : CH " + alarm.getChannel());

        return convertView;
    }
}
