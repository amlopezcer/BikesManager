package com.amlopezc.bikesmanager.net;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amlopezc.bikesmanager.R;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class HttpDeleteWorker extends AsyncTask<String, Void, String> {

    private HashSet<AsyncTaskListener<String>> listeners;
    private final ProgressDialog progressDialog;
    private final Context context;

    public HttpDeleteWorker(Context context) {
        progressDialog = new ProgressDialog(context);
        this.context = context;
    }

    //Starting a progress dialog for user feedback
    @Override
    protected void onPreExecute() {
        progressDialog.setTitle(i18n(R.string.progress_title));
        progressDialog.setMessage(i18n(R.string.progress_body));
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    //Processing data in background
    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url.
        try {
            return process(urls[0]);
        } catch (IOException ioe) {
            Log.e(this.getClass().getCanonicalName(), ioe.getLocalizedMessage(), ioe);
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    //Notifying task termination
    @Override
    protected void onPostExecute(String result) {
        for(AsyncTaskListener<String> listener : listeners) {
            listener.processServerResult(result, HttpConstants.OPERATION_DELETE);
        }
        progressDialog.dismiss();
    }

    public void addAsyncTaskListener(AsyncTaskListener<String> listener) {
        if (listeners == null) listeners = new HashSet<>();
        listeners.add(listener);
    }

    //Process server data in order to send it to the caller activity
    private String process(String myUrl) throws IOException {

        HttpURLConnection conn = null;
        try {
            URL url = new URL(myUrl);
            conn = (HttpURLConnection) url.openConnection();
            //Set connection stuff
            conn.setRequestMethod("DELETE");
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            //Get response
            int response = conn.getResponseCode();
            Log.i(this.getClass().getCanonicalName(), "The response is: " + response);
            return Integer.toString(response);
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return context.getResources().getString(resourceId, formatArgs);
    }

}
