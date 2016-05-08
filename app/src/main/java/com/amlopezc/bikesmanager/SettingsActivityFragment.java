package com.amlopezc.bikesmanager;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Shows the layout to set some settings (connection data, user options...)
 */

public class SettingsActivityFragment extends PreferenceFragment {

    public static final String KEY_PREF_SUPERUSER = "superuser";
    public static final String KEY_PREF_SYNC_SERVER = "server_address";
    public static final String KEY_PREF_SYNC_PORT = "server_port";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
