package com.amlopezc.bikesmanager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Launcher activity which shows a logo and decides which activity goes then
 */
public class SplashActivity extends AppCompatActivity  {

    // Duration of wait
    private final int SPLASH_DISPLAY_LENGTH = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Intent intent;

        //Check if a user is already logged and init the intent to go to the appropriate class
        if(isUserLogged())
            intent = new Intent(this, MapsActivity.class);
        else
            intent = new Intent(this, LoginActivity.class);

        //New Handler to start the new activity and close this Splash-Screen after some seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public boolean isUserLogged() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.text_user_name), "");
        String password = sharedPreferences.getString(getString(R.string.text_password), "");
        return !(username.equals("") || password.equals(""));
    }

}
