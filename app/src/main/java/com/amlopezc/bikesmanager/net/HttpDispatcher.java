package com.amlopezc.bikesmanager.net;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.amlopezc.bikesmanager.SettingsActivityFragment;
import com.amlopezc.bikesmanager.entity.JSONBean;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;


public class HttpDispatcher {

    public static final int OPERATION_GET = 1;
    public static final int OPERATION_PUT = 2;

    private final Context context;
    // Server developed in NetBeans
    private final String BASE_URL_ADDRESS = "http://%s:%s/BikesManager/rest/entities.bikestation";
    private final String SERVER_ADDRESS;
    private final String SERVER_PORT;
    //private final String REGISTRY_OWNER; // User

    private ObjectMapper mapper;

    public HttpDispatcher(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SERVER_ADDRESS = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, "");
        SERVER_PORT = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, "");

        this.context = context;

        // Deactivate auto-detection, do not use getters or setters, but attributes
        mapper =  new ObjectMapper()
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public void doGet(AsyncTaskListener listener) {
        String url = String.format(BASE_URL_ADDRESS, SERVER_ADDRESS, SERVER_PORT);
        if (isOnline()) {
            Log.i(this.getClass().getCanonicalName(), "online");
            HttpGetWorker worker = new HttpGetWorker(context);
            worker.addAsyncTaskListener(listener);
            worker.execute(url);
        } else {
            Toast.makeText(context, "offline", Toast.LENGTH_SHORT).show();
        }
    }

    public void doPut(AsyncTaskListener listener, JSONBean bean, String method) {
        StringBuilder builder = new StringBuilder(String.format(BASE_URL_ADDRESS, SERVER_ADDRESS, SERVER_PORT));
        String url = builder.append("/").append(method).append("/").append(bean.getServerId()).toString(); //ID to update
        if (isOnline()) {
            Log.i(this.getClass().getCanonicalName(), "online");
            HttpPutWorker worker = new HttpPutWorker(context, bean, mapper);
            worker.addAsyncTaskListener(listener);
            worker.execute(url);
        } else {
            Toast.makeText(context, "offline", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isOnline () {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public ObjectMapper getMapper() { return mapper; }

}
