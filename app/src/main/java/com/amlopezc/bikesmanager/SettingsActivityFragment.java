package com.amlopezc.bikesmanager;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

public class SettingsActivityFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_SYNC_USER = "username";
    public static final String KEY_PREF_SYNC_SERVER = "server_address";
    public static final String KEY_PREF_SYNC_PORT = "server_port";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        updateSummaryText(sharedPreferences, KEY_PREF_SYNC_USER);
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

    private void updateSummaryText(SharedPreferences sharedPreferences, String key) {

        EditTextPreference editTextPref = (EditTextPreference) findPreference(key);
        String dataString = sharedPreferences.getString(key, "");
        final String template = "%s for data connection: %s%s%s";

        if (dataString.trim().isEmpty()) {
            switch (key) {
                case KEY_PREF_SYNC_USER:
                    editTextPref.setSummary(String.format(template, "User name", "", "not defined", ""));
                    break;
                case KEY_PREF_SYNC_SERVER:
                    editTextPref.setSummary(String.format(template, "Server address", "", "not defined", ""));
                    break;
                case KEY_PREF_SYNC_PORT:
                    editTextPref.setSummary(String.format(template, "Server port", "", "not defined", ""));
                    break;
            }
        } else {
            switch (key) {
                case KEY_PREF_SYNC_USER:
                    editTextPref.setSummary(String.format(template, "User name", "'", dataString, "'"));
                    break;
                case KEY_PREF_SYNC_SERVER:
                    editTextPref.setSummary(String.format(template, "Server address", "'", dataString, "'"));
                    break;
                case KEY_PREF_SYNC_PORT:
                    editTextPref.setSummary(String.format(template, "Server port", "'", dataString, "'"));
                    break;
            }
        }
    }
}
