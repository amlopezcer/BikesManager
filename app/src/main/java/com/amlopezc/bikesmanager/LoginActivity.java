package com.amlopezc.bikesmanager;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setAppVersion(); //Sets version textView with the current app version

        Button buttonSignUp = (Button) findViewById(R.id.button_signUp);
        buttonSignUp.setOnClickListener(this);
        Button buttonSignIn = (Button) findViewById(R.id.button_signIn);
        buttonSignIn.setOnClickListener(this);

    }

    //Sets version textView with the current app version
    private void setAppVersion() {
        TextView textViewVersion = (TextView) findViewById(R.id.textView_version_text);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            textViewVersion.setText(version);
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(getClass().getCanonicalName(), nnfe.getLocalizedMessage(), nnfe);
            textViewVersion.setText("-");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.button_signUp:
                intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.button_signIn:
                showConnectionDataDialog();
                break;
        }
    }

    private void showConnectionDataDialog() {
        DialogFragment dialog = new SigninDialogFragment();
        dialog.show(getFragmentManager(), SigninDialogFragment.CLASS_ID);
    }

    public void doPositiveClick() {
        Log.i(this.getClass().getCanonicalName(), "Positive button clicked");

    }


}
