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

    private String password; //TODO: Cambiar, esto es la password introducida que irá a las preferences y la cojo de ahí

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
                showSigninDataDialog();
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
        this.password= new String(Hex.encodeHex(DigestUtils.sha1(password))); //Encrypt password

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

    //Validating the user versus its password
    private void validateUser(String result) {
        try {
            //Get the user
            HttpDispatcher httpDispatcher = new HttpDispatcher(this, HttpConstants.ENTITY_USER);
            ObjectMapper mapper = httpDispatcher.getMapper();
            BikeUser bikeUser = mapper.readValue(result, BikeUser.class);

            //Check the password and complete the sign in
            if(bikeUser.getmPassword().equals(this.password)) {
                // completeLogin();
                    //1. Registrar en el shared preferenes
                    //2. Completar el SINGLETON
                    //3. Intent al mapa
                Toast.makeText(this,
                        "OK",
                        Toast.LENGTH_SHORT).show();
            } else
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

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }
}
