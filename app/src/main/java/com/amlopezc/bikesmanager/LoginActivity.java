package com.amlopezc.bikesmanager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView textViewVersion = (TextView) findViewById(R.id.textView_version_text);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            textViewVersion.setText(version);
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(getClass().getCanonicalName(), nnfe.getLocalizedMessage(), nnfe);
        }

    }
}
