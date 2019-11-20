package com.kevintakata.rokutvalarm;

import java.util.Calendar;

public class Alarm {

    private String mId;
    private Calendar mTime;
    private Device mDevice;
    private String mAppId;
    private String mChannel;
    private boolean[] mDaysOfWeek = new boolean[] {false, false, false, false, false, false, false};

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

    public void setNextAlarm() {
        mTime.add(Calendar.DATE, numDaysTillNextAlarm());
    }

    private int numDaysTillNextAlarm() {
        int init = 0;
        Calendar now = Calendar.getInstance();
        // add a day if the alarm is before now
        if(getTime().before(now) || getTime().equals(now)) {
            init++;
        }
        // loop through days of week starting with the next day the time happens (today or tomorrow)
        int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK)-1;
        for(int i = init; i <= 7; i++) {
            if(mDaysOfWeek[(currentDayOfWeek+i)%7]) {
                return i;
            }
        }
        // if no days of week set, 1 time alarm, return init val (0 or 1)
        return init;
    }

    public boolean isOneTime() {
        for(boolean day : mDaysOfWeek) {
            if(day) {
                return false;
            }
        }
        return true;
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

    public void setSunday(boolean isSunday) {
        mDaysOfWeek[Calendar.SUNDAY-1] = isSunday;
    }

    public void setMonday(boolean isMonday) {
        mDaysOfWeek[Calendar.MONDAY-1] = isMonday;
    }

    public void setTuesday(boolean isTuesday) {
        mDaysOfWeek[Calendar.TUESDAY-1] = isTuesday;
    }

    public void setWednesday(boolean isWednesday) {
        mDaysOfWeek[Calendar.WEDNESDAY-1] = isWednesday;
    }

    public void setThursday(boolean isThursday) {
        mDaysOfWeek[Calendar.THURSDAY-1] = isThursday;
    }

    public void setFriday(boolean isFriday) {
        mDaysOfWeek[Calendar.FRIDAY-1] = isFriday;
    }

    public void setSaturday(boolean isSaturday) {
        mDaysOfWeek[Calendar.SATURDAY-1] = isSaturday;
    }
}
