package com.amlopezc.bikesmanager.net;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.amlopezc.bikesmanager.R;
import com.amlopezc.bikesmanager.SettingsActivityFragment;
import com.amlopezc.bikesmanager.entity.JSONBean;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Manages HTTP connections.
 */

public class HttpDispatcher {

    //Constants to identify operations in the caller activities (to process the result)
    public static final int OPERATION_GET = 1;
    public static final int OPERATION_PUT = 2;

    private final Context context; //Context from the caller Activity, needed for Toasts, progress dialog...

    // Server developed in NetBeans, basic constants to establish connection
    private final String BASE_URL_ADDRESS = "http://%s:%s/BikesManager/rest/entities.bikestation";
    private final String SERVER_ADDRESS;
    private final String SERVER_PORT;

    private ObjectMapper mapper; //To process JSON strings

    public HttpDispatcher(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SERVER_ADDRESS = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, "");
        SERVER_PORT = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, "");

        this.context = context;

        //Deactivate auto-detection, do not use getters or setters, but attributes of the instance when processing JSON strings
        mapper =  new ObjectMapper()
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public void doGet(AsyncTaskListener listener) {
        String url = String.format(BASE_URL_ADDRESS, SERVER_ADDRESS, SERVER_PORT);
        if (isOnline()) {
            Log.i(this.getClass().getCanonicalName(), "Connected");
            HttpGetWorker worker = new HttpGetWorker(context);
            worker.addAsyncTaskListener(listener); //Register the listener to be aware of the task termination
            worker.execute(url);
        } else {
            Log.i(this.getClass().getCanonicalName(), "Not connected");
            Toast.makeText(context,
                    i18n(R.string.toast_unable_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void doPut(AsyncTaskListener listener, JSONBean bean, String method) {
        StringBuilder builder = new StringBuilder(String.format(BASE_URL_ADDRESS, SERVER_ADDRESS, SERVER_PORT));
        String url = builder.append("/")
                .append(method).append("/")
                .append(bean.getServerId()).toString(); //ID to update
        if (isOnline()) {
            Log.i(this.getClass().getCanonicalName(), "Connected");
            HttpPutWorker worker = new HttpPutWorker(context, bean, mapper);
            worker.addAsyncTaskListener(listener);//Register the listener to be aware of the task termination
            worker.execute(url);
        } else {
            Log.i(this.getClass().getCanonicalName(), "Not connected");
            Toast.makeText(context,
                    i18n(R.string.toast_unable_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Checking whether net is reachable
    private boolean isOnline () {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public ObjectMapper getMapper() { return mapper; }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return context.getString(resourceId, formatArgs);
    }

}
