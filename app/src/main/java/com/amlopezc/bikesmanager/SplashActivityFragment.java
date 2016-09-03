package com.amlopezc.bikesmanager;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Launcher activity which shows a logo and decides which activity goes then
 */
public class SplashActivityFragment extends Fragment {

    public SplashActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        final Intent intent;
        // Wait duration
        final int SPLASH_DISPLAY_LENGTH = 1000;

        //Check if a user is already logged and init the intent to go to the appropriate class
        if(isUserLogged())
            intent = new Intent(getActivity(), MapsActivity.class);
        else
            intent = new Intent(getActivity(), LoginActivity.class);

        //New Handler to start the new activity and close this Splash-Screen after some seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                getActivity().finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

        return view;
    }

    public boolean isUserLogged() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.text_user_name), "");
        String password = sharedPreferences.getString(getString(R.string.text_password), "");
        return !(username.equals("") || password.equals(""));
    }

}
