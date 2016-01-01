package com.amlopezc.bikesmanager.net;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.amlopezc.bikesmanager.SettingsActivityFragment;
import com.amlopezc.bikesmanager.entity.JSONBean;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class HttpDispatcher {

    private final Context context;
    private ObjectMapper mapper;
    // Server developed in NetBeans
    private final String BASE_URL_ADDRESS = "http//%s:s/BikesManager/rest/entities.bikestation";
    private final String SERVER_ADDRESS;
    private final String SERVER_PORT;
    private final String REGISTRY_OWNER; // User

    public HttpDispatcher(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SERVER_ADDRESS = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, "");
        SERVER_PORT = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, "");
        REGISTRY_OWNER = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_USER, ""); // Esto posiblemente vaya fuera

        mapper = new ObjectMapper();
        // Desactivar autodetección y obligar al uso de atributos, no get o set
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        this.context = context;
    }

    public <T> void doGet(Class<T> resultType, AsyncTaskListener<T> listener) {
        StringBuilder builder = new StringBuilder(String.format(BASE_URL_ADDRESS, SERVER_ADDRESS, SERVER_PORT));
        //String url = builder.append("/owner/").append(REGISTRY_OWNER.toString(); -> esto creo que es para sólo coger lo que haya hecho este owner, creo que no me interesa
        String url = builder.toString();
        //TODO: implementar HttpGetWorker
    }

    public void doPost(JSONBean bean, AsyncTaskListener<List<String>> listener) {
        StringBuilder builder = new StringBuilder(String.format(BASE_URL_ADDRESS, SERVER_ADDRESS, SERVER_PORT));
        String url = builder.toString();
        //TODO: implementar HttpPostWorker
    }

    public void doPut(JSONBean bean, AsyncTaskListener<List<String>> listener) {
        StringBuilder builder = new StringBuilder(String.format(BASE_URL_ADDRESS, SERVER_ADDRESS, SERVER_PORT));
        String url = builder.append("/").append(bean.getServerId()).toString(); // El bean que queremos enviar, lo insertamos con el ID que queremos actualizar

        //TODO: implementar HttpPutWorker
    }

    public void doDelete(JSONBean bean, AsyncTaskListener<List<String>> listener) {
        StringBuilder builder = new StringBuilder(String.format(BASE_URL_ADDRESS, SERVER_ADDRESS, SERVER_PORT));
        String url = builder.append("/").append(bean.getServerId()).toString(); // El bean que queremos borrar, lo insertamos con el ID que queremos actualizar

        //TODO: implementar HttpDeleteWorker
    }
}
