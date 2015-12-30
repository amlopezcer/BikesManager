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
        if(key.equals(KEY_PREF_SYNC_USER))
            updateSummaryText(sharedPreferences, key);
    }

    private void updateSummaryText(SharedPreferences sharedPreferences, String key) {
        final String templateText = "User name to data connection: %s%s%s";
        EditTextPreference editTextPref = (EditTextPreference) findPreference(key);
        String userNameString = sharedPreferences.getString(key, "");

        if(userNameString.isEmpty())
            editTextPref.setSummary(String.format(templateText, "", "not defined", ""));
        else
            editTextPref.setSummary(String.format(templateText, "'", userNameString, "'"));
    }
}
