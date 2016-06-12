package com.amlopezc.bikesmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * Something nice to salute a new user
 */
public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final float NEW_USER_PRESENT = 5.00f; //Some money for the new user (programatically assigned while the user creation, see constructor in BikeUser.java)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button buttonGreat = (Button) findViewById(R.id.button_great);
        buttonGreat.setOnClickListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.file_user_preferences), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(getString(R.string.text_user_name), "");
        TextView textViewWelcome = (TextView) findViewById(R.id.textView_welcome);
        textViewWelcome.setText(i18n(R.string.textView_welcome, username));

        TextView textViewMoney = (TextView) findViewById(R.id.textView_welcomeMoney);
        textViewMoney.setText(i18n(R.string.text_format_money, NEW_USER_PRESENT));

        //getUpdatedUserData();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.button_great:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
        }
    }

    //Internationalization method
    private String i18n(int resourceId, Object ... formatArgs) {
        return getResources().getString(resourceId, formatArgs);
    }
}
