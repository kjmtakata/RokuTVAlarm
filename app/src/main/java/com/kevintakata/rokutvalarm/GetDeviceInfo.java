package com.kevintakata.rokutvalarm;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class GetDeviceInfo extends RokuRequest {
    private static String TAG = GetDeviceInfo.class.getSimpleName();

    GetDeviceInfo(RokuDeviceSelectActivity activity, String baseUrl) {
        super(activity, baseUrl, "query/device-info", "GET");
    }

    @Override
    Object parseResponse(InputStream inputStream) {
        Device device = new Device();
        device.setLocation(getBaseUrl());
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            int eventType = parser.getEventType();
            String eltName = null;
            boolean inDevice = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        eltName = parser.getName();

                        if (eltName.equals("device-info")) {
                            inDevice = true;
                        } else if (inDevice) {
                            if (eltName.equals("friendly-device-name")) {
                                device.setName(parser.nextText());
                            } else if (eltName.equals("serial-number")) {
                                device.setSerialNumber(parser.nextText());
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, "", e);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }

        Log.d(TAG, device.toString());

        return device;
    }

    @Override
    protected void onPostExecute(Object device) {
        ((RokuDeviceSelectActivity) getActivity()).addDevice((Device) device);
    }
}