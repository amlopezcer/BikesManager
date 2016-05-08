package com.amlopezc.bikesmanager;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amlopezc.bikesmanager.entity.BikeUser;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


/**
 * Launcher activity to sign up or sign in users
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskListener<String> {

    private String mTrialPassword; //Password introduced by the user who wants to log in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Ensuring connection data is set, showing ConnectionDataDialog otherwise
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serverAddress = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, "");
        String serverPort = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, "");
        if(serverAddress.trim().isEmpty() || serverPort.trim().isEmpty())
            showConnectionDataDialog();

        //Check if a user is already logged and skip this activity in that case
        if(isUserLogged()) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else
            initActivity();
    }

    private void showConnectionDataDialog() {
        DialogFragment dialog = new ConnectionDataDialogFragment();
        dialog.show(getFragmentManager(), ConnectionDataDialogFragment.CLASS_ID);
    }

    private boolean isUserLogged() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.text_user_name), "");
        String password = sharedPreferences.getString(getString(R.string.text_password), "");

        return !(username.equals("") || password.equals(""));
    }

    private void initActivity() {
        setAppVersion(); //Sets version textView with the current app version

        Button buttonSignUp = (Button) findViewById(R.id.button_signUp);
        buttonSignUp.setOnClickListener(this);
        Button buttonSignIn = (Button) findViewById(R.id.button_signIn);
        buttonSignIn.setOnClickListener(this);
        ImageButton buttonConnectionSettings = (ImageButton) findViewById(R.id.imgButton_connection_settings);
        buttonConnectionSettings.setOnClickListener(this);
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
                showSigninDataDialog();
                break;
            case R.id.imgButton_connection_settings:
                showConnectionDataDialog();
                break;
        }
    }

    //Showing the dialog for the user to sign in
    private void showSigninDataDialog() {
        DialogFragment dialog = new SigninDialogFragment();
        dialog.show(getFragmentManager(), SigninDialogFragment.CLASS_ID);
    }

    //When the user clicks "ok", the app signs him in (if the user exists and the password is correct)
    public void doPositiveClick(String username, String password) {
        this.mTrialPassword = new String(Hex.encodeHex(DigestUtils.sha1(password))); //Encrypt password

        //Get the user selected
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
        httpDispatcher.doGet(this, String.format(HttpConstants.GET_FIND_USERID, username)); //path: .../user/{username}
    }

    @Override
    public void processServerResult(String result, int operation) {
        switch (operation) {
            case HttpConstants.OPERATION_GET:
                if(result == null || result.isEmpty())
                    Toast.makeText(this,
                            i18n(R.string.toast_user_not_found),
                            Toast.LENGTH_SHORT).show();
                else
                    validateUser(result);
            }
    }

    //Validating the user versus its mTrialPassword
    private void validateUser(String result) {
        try {
            //Get the user
            HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
            ObjectMapper mapper = httpDispatcher.getMapper();
            BikeUser bikeUser = mapper.readValue(result, BikeUser.class);

            //Check the mTrialPassword and complete the sign in
            if(bikeUser.getmPassword().equals(this.mTrialPassword))
                completeLogin(bikeUser);
            else
                Toast.makeText(this,
                        i18n(R.string.toast_incorrect_password, bikeUser.getmUserName()),
                        Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("[GET Result]" + getClass().getCanonicalName(), e.getLocalizedMessage(), e);
            Toast.makeText(this,
                    i18n(R.string.toast_sync_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void completeLogin(BikeUser bikeUser) {
        //Save data consistenly
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(getString(R.string.text_user_name), bikeUser.getmUserName())
                .putString(getString(R.string.text_password),  bikeUser.getmPassword())
                .apply();

        //Go to the main class
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }
}
