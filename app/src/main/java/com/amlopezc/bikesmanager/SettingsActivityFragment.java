package com.amlopezc.bikesmanager;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

/**
 * Shows the layout to set connection data with the server: IP + Port (+ user, useless nowadays).
 * Data is stored in a SharedPreferences instance.
 */

public class SettingsActivityFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    // Constants for the data managed here: ip + port
    public static final String KEY_PREF_SYNC_SERVER = "server_address";
    public static final String KEY_PREF_SYNC_PORT = "server_port";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Setting default data
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        updateSummaryText(sharedPreferences, KEY_PREF_SYNC_SERVER);
        updateSummaryText(sharedPreferences, KEY_PREF_SYNC_PORT);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummaryText(sharedPreferences, key);
    }

    //Just formatting an updated tag with data provided
    private void updateSummaryText(SharedPreferences sharedPreferences, String key) {
        EditTextPreference editTextPref = (EditTextPreference) findPreference(key);
        String dataString = sharedPreferences.getString(key, "");

        final String serverAddress = i18n(R.string.text_server_address);
        final String serverPort = i18n(R.string.text_server_port);
        final String notDefined = i18n(R.string.text_not_defined);

        if (dataString.trim().isEmpty()) {
            switch (key) {
                case KEY_PREF_SYNC_SERVER:
                    editTextPref.setSummary(i18n(R.string.template_text, serverAddress, "", notDefined, ""));
                    break;
                case KEY_PREF_SYNC_PORT:
                    editTextPref.setSummary(i18n(R.string.template_text, serverPort, "", notDefined, ""));
                    break;
            }
        } else {
            switch (key) {
                case KEY_PREF_SYNC_SERVER:
                    editTextPref.setSummary(i18n(R.string.template_text, serverAddress, "'", dataString, "'"));
                    break;
                case KEY_PREF_SYNC_PORT:
                    editTextPref.setSummary(i18n(R.string.template_text, serverPort, "'", dataString, "'"));
                    break;
            }
        }
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }
}
