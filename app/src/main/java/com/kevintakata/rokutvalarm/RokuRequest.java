package com.kevintakata.rokutvalarm;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class RokuRequest extends AsyncTask<Void, Void, Object> {

    private static String TAG = RokuRequest.class.getSimpleName();
    private String mUrl;
    private WeakReference<Activity> mWeakActivity;
    private String mBaseUrl;
    private String mMethod;

    RokuRequest(Activity activity, String baseUrl, String relativeEndpoint, String method) {
        mUrl = baseUrl + relativeEndpoint;
        mBaseUrl = baseUrl;
        mWeakActivity = new WeakReference<>(activity);
        mMethod = method;
    }

    abstract Object parseResponse(InputStream inputStream);

    Activity getActivity() {
        return mWeakActivity.get();
    }

    String getBaseUrl() {
        return mBaseUrl;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        StringBuilder sb = new StringBuilder();
        Object result = new Object();
        InputStream inputStream = null;

        try {
            URL url = new URL(mUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(mMethod);
            inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            result = parseResponse(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
        return result;
    }

}

