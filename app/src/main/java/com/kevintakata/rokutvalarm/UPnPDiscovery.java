package com.kevintakata.rokutvalarm;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Bernd Verst(@berndverst)
 */
public class UPnPDiscovery extends AsyncTask<Void, String, HashSet<String>>
{
    private static String TAG = UPnPDiscovery.class.getSimpleName();
    private HashSet<String> mPackets = new HashSet<>();
    private WeakReference<RokuDeviceSelectActivity> mWeakActivity = null;
    private WeakReference<Context> mWeakContext;
    private String mSerialNumber = null;
    private String mChannel = null;

    // get device info
    UPnPDiscovery(RokuDeviceSelectActivity activity) {
        mWeakActivity = new WeakReference<>(activity);
        mWeakContext = new WeakReference<>(activity.getApplicationContext());
    }

    // launch roku app
    UPnPDiscovery(Context context, String serialNumber, String channel) {
        mWeakContext = new WeakReference<>(context);
        mSerialNumber = serialNumber;
        mChannel = channel;
    }

    @Override
    protected HashSet<String> doInBackground(Void... voids) {
        WifiManager wifi = (WifiManager) mWeakContext.get().getSystemService( Context.WIFI_SERVICE );

        if(wifi != null) {

            WifiManager.MulticastLock lock = wifi.createMulticastLock("The Lock");
            lock.acquire();

            DatagramSocket socket = null;

            try {

                InetAddress group = InetAddress.getByName("239.255.255.250");
                int port = 1900;
                String query =
                        "M-SEARCH * HTTP/1.1\r\n" +
                                "HOST: 239.255.255.250:1900\r\n"+
                                "MAN: \"ssdp:discover\"\r\n"+
                                "MX: 1\r\n"+
                                "ST: roku:ecp\n\r\n"+  // Use for Roku
                                //"ST: ssdp:all\r\n"+  // Use this for all UPnP Devices
                                "\r\n";

                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);

                DatagramPacket dgram = new DatagramPacket(query.getBytes(), query.length(),
                        group, port);
                socket.send(dgram);

                long time = System.currentTimeMillis();
                long curTime = System.currentTimeMillis();
                int maxTime = 2000;

                //65507
                DatagramPacket p = new DatagramPacket(new byte[65507], 65507);
                // Let's consider all the responses we can get in 2 seconds
                while (curTime - time < maxTime) {
                    Log.d(TAG, "reading packet");

                    socket.setSoTimeout(maxTime - (int) (curTime - time));
                    try {
                        socket.receive(p);
                    } catch (SocketTimeoutException e) {
                        Log.d(TAG, "receive timeout");
                        break;
                    }

                    Log.d(TAG, "extracting data from packet");
                    String s = new String(p.getData(), 0, p.getLength());
                    Log.d(TAG, s);
                    if (s.toUpperCase().startsWith("HTTP/1.1 200")) {
                        mPackets.add(s);
                        publishProgress(s);
                    }

                    curTime = System.currentTimeMillis();
                }
                Log.d(TAG, "done");

            } catch (UnknownHostException e) {
                Log.e(TAG, "", e);
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }
            finally {
                Log.d(TAG, "closing socket");
                socket.close();
            }
            lock.release();
        }
        return mPackets;
    }

    protected void onProgressUpdate(String... packets) {
//        List<String> locations = new ArrayList<>();
        List<Device> devices = new ArrayList<>();
        for (String packet : packets) {
            Device device = new Device();
            String headers[] = packet.split("\r\n");
            for (String header : headers) {
                String parts[] = header.split(":", 2);
                if (parts.length >= 2) {
                    if (parts[0].toLowerCase().equals("location")) {
                        device.setLocation(parts[1]);
                    } else if (parts[0].toLowerCase().equals("usn")) {
                        String usnParts[] = parts[1].split(":");
                        device.setSerialNumber(usnParts[usnParts.length-1]);
                    }
                }
            }
            devices.add(device);
        }

        for (Device device : devices) {
            // launch roku app
            if (mWeakActivity == null) {
                if(device.getSerialNumber().equals(mSerialNumber)) {
                    new LaunchRokuApp(null, device.getLocation(),
                            "tvinput.dtv?ch="+mChannel).execute();
                }
            }
            // get device info
            else {
                Log.d(TAG, "getting: " + device.getLocation());
                new GetDeviceInfo(mWeakActivity.get(), device.getLocation()).execute();
            }
        }
    }

    protected void onPostExecute(HashSet<String> packets) {

    }
}
