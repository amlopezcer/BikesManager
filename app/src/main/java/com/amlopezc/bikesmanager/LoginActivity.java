package com.amlopezc.bikesmanager;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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

import com.amlopezc.bikesmanager.entity.BikeUser;
import com.amlopezc.bikesmanager.net.HttpConstants;
import com.amlopezc.bikesmanager.net.HttpDispatcher;
import com.amlopezc.bikesmanager.util.AsyncTaskListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


/**
 * Activity to sign up or sign in users
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskListener<String> {

    private String mTrialPassword; //Password introduced by the user who wants to log in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Ensuring connection data is set, showing ConnectionDataDialog otherwise
        checkConnectionData();

        setAppVersion(); //Set version textView with the current app version

        Button buttonSignUp = (Button) findViewById(R.id.button_signUp);
        assert buttonSignUp != null;
        buttonSignUp.setOnClickListener(this);
        Button buttonSignIn = (Button) findViewById(R.id.button_signIn);
        assert buttonSignIn != null;
        buttonSignIn.setOnClickListener(this);
        ImageButton buttonConnectionSettings = (ImageButton) findViewById(R.id.imgButton_connection_settings);
        assert buttonConnectionSettings != null;
        buttonConnectionSettings.setOnClickListener(this);
    }

    private void checkConnectionData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serverAddress = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_SERVER, "");
        String serverPort = sharedPreferences.getString(SettingsActivityFragment.KEY_PREF_SYNC_PORT, "");
        if(serverAddress.trim().isEmpty() || serverPort.trim().isEmpty())
            showConnectionDataDialog();
    }

    //Show Dialog to select server IP and port
    private void showConnectionDataDialog() {
        DialogFragment dialog = new ConnectionDataDialogFragment();
        dialog.show(getFragmentManager(), ConnectionDataDialogFragment.CLASS_ID);
    }

    //Set version textView with the current app version
    private void setAppVersion() {
        TextView textViewVersion = (TextView) findViewById(R.id.textView_version_text);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            assert textViewVersion != null;
            textViewVersion.setText(version);
        } catch (PackageManager.NameNotFoundException nnfe) {
            Log.e(getClass().getCanonicalName(), nnfe.getLocalizedMessage(), nnfe);
            assert textViewVersion != null;
            textViewVersion.setText("-");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.button_signUp:
                intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.button_signIn:
                showSignInDataDialog();
                break;
            case R.id.imgButton_connection_settings:
                showConnectionDataDialog();
                break;
        }
    }

    //Show the dialog for the user to sign in
    private void showSignInDataDialog() {
        DialogFragment dialog = new SignInDialogFragment();
        dialog.show(getFragmentManager(), SignInDialogFragment.CLASS_ID);
    }

    //When the user clicks positive button, the app signs him in (if the user exists and the password is correct)
    public void doPositiveClickSignInDialog(String username, String password) {
        this.mTrialPassword = new String(Hex.encodeHex(DigestUtils.sha1(password))); //Encrypt password (SHA1)

        //Get the user selected
        HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
        httpDispatcher.doGet(this, String.format(HttpConstants.GET_FIND_USERID, username)); //path: .../user/{username}
    }

    @Override
    public void processServerResult(String result, int operation) {
        switch (operation) {
            case HttpConstants.OPERATION_GET:
                if(result == null || result.isEmpty()) //User not found
                    showBasicErrorDialog(i18n(R.string.toast_user_not_found), i18n(R.string.text_ok));
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
                showBasicErrorDialog(i18n(R.string.toast_incorrect_password, bikeUser.getmUserName()),
                        i18n(R.string.text_ok));

        } catch (Exception e) {
            Log.e("[GET Result]" + getClass().getCanonicalName(), e.getLocalizedMessage(), e);
            showBasicErrorDialog(i18n(R.string.toast_sync_error), i18n(R.string.text_ok));
        }
    }

    //Once the user has been validated, complete login by going to the map and filling the singleton instance
    private void completeLogin(BikeUser bikeUser) {
        //Save data consistently
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        sharedPreferences.edit().
                putString(getString(R.string.text_user_name), bikeUser.getmUserName()).
                putString(getString(R.string.text_password),  bikeUser.getmPassword()).
                apply();

        //Go to the main class
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    // Show a basic error dialog with a custom message
    private void showBasicErrorDialog(String message, String positiveButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(i18n(R.string.text_error)).
                setIcon(R.drawable.ic_error_outline).
                setMessage(message).
                setPositiveButton(
                        positiveButtonText,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }
}
