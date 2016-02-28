package com.amlopezc.bikesmanager.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amlopezc.bikesmanager.entity.JSONBean;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class HttpPutWorker  extends AsyncTask<String, Void, String> {

    private HashSet<AsyncTaskListener<String>> listeners;
    private final ProgressDialog progressDialog;
    private JSONBean bean;
    private ObjectMapper mapper;

    public HttpPutWorker(Context context, JSONBean bean, ObjectMapper mapper) {
        progressDialog = new ProgressDialog(context);
        this.bean = bean;
        this.mapper = mapper;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("Esperar...");
        progressDialog.setMessage("Sincronizando...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

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

    // onPostExecute process the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        for(AsyncTaskListener<String> listener : listeners) {
            listener.processResult(result, HttpDispatcher.OPERATION_PUT);
        }
        progressDialog.dismiss();
    }

    public void addAsyncTaskListener(AsyncTaskListener<String> listener) {
        if (listeners == null) listeners = new HashSet<>();
        listeners.add(listener);
    }

    private String process(String myUrl) throws IOException {
        OutputStreamWriter osw = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(myUrl);
            conn = (HttpURLConnection) url.openConnection();
            //Set connection
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            //Send data
            osw = new OutputStreamWriter(conn.getOutputStream());
            osw.write(mapper.writeValueAsString(bean));
            osw.flush();
            osw.close();
            int response = conn.getResponseCode();
            Log.d(this.getClass().getCanonicalName(), "The response is: " + response);
            return Integer.toString(response);
        } finally {
            if(osw != null)
                osw.close();
            if (conn != null)
                conn.disconnect();
        }
    }

}
