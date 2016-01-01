package com.amlopezc.bikesmanager.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.amlopezc.bikesmanager.util.ApplicationContextProvider;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;


public class HttpGetWorker extends AsyncTask<String, Void, String> {

    private HashSet<AsyncTaskListener> listeners;
    final ProgressDialog progressDialog;

    public HttpGetWorker(Context context) {
        progressDialog = new ProgressDialog(context);
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
            Log.e("HttpGetWorker:", ioe.getLocalizedMessage(), ioe);
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    // onPostExecute process the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        for(AsyncTaskListener listener : listeners) {
            listener.processResult(result);
        }

        progressDialog.dismiss();
    }

    public void addAsyncTaskListener(AsyncTaskListener listener) {
        if (listeners == null) listeners = new HashSet<>();
        listeners.add(listener);
    }

    private String process(String myUrl) throws IOException {
        InputStream is = null;
        // Only display the first 20 characters of the retrieved web page content.
        int len = 20;
        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("HttpGetWorker", "The response is: " + response);
            is = conn.getInputStream();
            String data = readIt(is, len);
            return data;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
