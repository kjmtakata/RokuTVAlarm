package com.kevintakata.rokutvalarm;

import java.io.Serializable;

public class Device implements Serializable {
    private String mName;
    private String mLocation;
    private String mSerialNumber;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        mSerialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return "Name: " + mName + ", Location: "  + mLocation + ", Serial Number: " + mSerialNumber;
    }
}
