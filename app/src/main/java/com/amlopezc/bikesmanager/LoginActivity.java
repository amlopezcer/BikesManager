package com.amlopezc.bikesmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    //When the user clicks positive button, the app signs him in (if the user exists and the password is correct)
    public void doPositiveClickSignInDialog(String username, String password) {
        LoginActivityFragment fragment = (LoginActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentLogin);
        fragment.doPositiveClickSignInDialog(username, password);
    }

}
