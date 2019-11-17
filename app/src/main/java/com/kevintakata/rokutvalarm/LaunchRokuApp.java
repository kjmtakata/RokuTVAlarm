package com.kevintakata.rokutvalarm;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LaunchRokuApp extends RokuRequest {
    private static String TAG = LaunchRokuApp.class.getSimpleName();

    LaunchRokuApp(Activity activity, String baseUrl, String AppId) {
        super(activity, baseUrl, "launch/" + AppId, "POST");
//        super(activity, baseUrl, "launch/2285", "POST");
//        super(activity, baseUrl, "query/apps", "GET");
    }

    @Override
    Object parseResponse(InputStream inputStream) {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        try {
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
        } catch (IOException e) {

        }
        Log.d(TAG, total.toString());

        return null;
    }

    @Override
    protected void onPostExecute(Object device) {

    }
}