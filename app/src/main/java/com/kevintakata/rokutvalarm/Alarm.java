package com.kevintakata.rokutvalarm;

import java.util.Calendar;

public class Alarm {

    private String mId;
    private Calendar mTime;
    private Device mDevice;
    private String mAppId;
    private String mChannel;

    public String getId() {
        return mId;
    }

    public Calendar getTime() {
        return mTime;
    }

    public Device getDevice() {
        return mDevice;
    }

    public String getAppId() {
        return mAppId;
    }

    public String getChannel() {
        return mChannel;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setTime(Calendar time) {
        mTime = time;
    }

    public void setDevice(Device device) {
        mDevice = device;
    }

    public void setAppId(String appId) {
        mAppId = appId;
    }

    public void setChannel(String channel) {
        mChannel = channel;
    }
}
